package net.tabarcraft.opencraft.View;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tabarcraft.opencraft.OCUtils;
import net.tabarcraft.opencraft.Question;
import net.tabarcraft.opencraft.QuestionStore;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ViewQuestionsView extends VBox {

    private final ListView<Question> listView;
    HBox hBox = new HBox(10);
    private BorderPane root;
    private QuestionStore questionStore;

    public ViewQuestionsView(QuestionStore store) {
        this.questionStore = store;
        store.loadQuestions();
        getStyleClass().add("view-questions-view");
        setSpacing(10);

        Label title = new Label("Liste des Questions");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button btnExporter = new Button("Exporter");
        btnExporter.setOnAction(e -> exporterQuestions(store));

        Button btnImporter = new Button("Importer");
        btnImporter.setOnAction(e -> importText(new Stage()));

        Button deleteAll = new Button("Tout supprimer");
        deleteAll.setOnAction(e -> {
            boolean confirmed = OCUtils.showAlertBool(
                    Alert.AlertType.CONFIRMATION,
                    "Supprimer les questions",
                    "Tout supprimer ?",
                    "Si vous n'avez pas enregistré dans vos fichiers les questions, tout sera perdu. Tout supprimer ?"
            );

            if (confirmed) {
                System.out.println("Suppression confirmée");
                deleteAllQuestions();
            }
        });

        hBox.getChildren().addAll(
                title,
                btnImporter,
                btnExporter,
                deleteAll
        );

        listView = new ListView<>(store.getObservableQuestions());

        VBox.setVgrow(listView, Priority.ALWAYS);

        listView.setCellFactory(param -> {
            ListCell<Question> cell = new ListCell<>() {
                @Override
                protected void updateItem(Question item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setContextMenu(null);
                    } else {
                        setText("Q: " + item.getQuery() + "\nR: " + item.getAnswer());

                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem editQuestion = new MenuItem("Modifier");
                        editQuestion.setOnAction(e -> {
                            Stage dialog = new Stage();

                            dialog.initModality(Modality.APPLICATION_MODAL); // Modal : bloque la principale

                            EditQuestionView view = new EditQuestionView(item, store);
                            Scene scene = new Scene(view, 600, 400);

                            // Charge le CSS dans cette scène
                            OCUtils.applyCSS(scene);

                            dialog.setScene(scene);
                            dialog.setTitle("Ajouter une Question");
                            dialog.showAndWait(); // Attends la fermeture avant de revenir à la principale
                        });
                        MenuItem delete = new MenuItem("Supprimer");
                        delete.setOnAction(e -> store.deleteQuestion(item));
                        contextMenu.getItems().addAll(editQuestion, delete);
                        setContextMenu(contextMenu);
                    }
                }
            };
            return cell;
        });

        getChildren().addAll(hBox, listView);
    }

    private void deleteAllQuestions() {
        questionStore.getObservableQuestions().clear();
        questionStore.saveQuestions();
    }

    private void exporterQuestions(QuestionStore store) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter les questions");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier clist", "*.clist"),
                new FileChooser.ExtensionFilter("Fichier Texte", "*.txt")
        );
        File file = chooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Question q : store.getObservableQuestions()) {
                    writer.write(q.getQuery() + "|" + q.getAnswer());
                    writer.newLine();
                }
            } catch (IOException ex) {
                showError("Erreur lors de l’export : " + ex.getMessage());
            }
        }
    }

    private void importText(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer un fichier clist/txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier clist", "*.clist", "*.txt")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                deleteAllQuestions();

                // Supposons que parseQuestions(String) retourne une List<Question>
                List<Question> parsedQuestions = parseQuestions(content);

                // Met à jour la liste sur le thread JavaFX
                Platform.runLater(() -> {
                    questionStore.getObservableQuestions().addAll(parsedQuestions);
                    // Si tu veux stocker le nom du fichier :
                    String fileName = file.getName().replaceFirst("[.][^.]+$", "");
                    System.out.println("Nom du fichier : " + fileName);
                    questionStore.saveQuestions();
                });

            } catch (IOException e) {
                System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            }
        }
    }

    public List<Question> parseQuestions(String fileContents) {
        List<Question> parsedQuestions = new ArrayList<>();

        // Sépare le contenu en blocs (chaque bloc représente une question + réponse)
        String[] questionComponents = fileContents.split("\\R\\R"); // correspond à "\n\n" ou "\r\n\r\n"

        for (String component : questionComponents) {
            String[] questionAndAnswer = component.split("\\R", 2); // séparé par une seule ligne

            if (questionAndAnswer.length >= 2) {
                String question = questionAndAnswer[0].trim();
                String answer = questionAndAnswer[1].trim();

                Question parsedQuestion = new Question(question, answer);
                parsedQuestions.add(parsedQuestion);
            }
        }

        return parsedQuestions;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
