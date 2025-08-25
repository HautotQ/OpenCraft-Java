package net.tabarcraft.opencraft.View;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import net.tabarcraft.opencraft.Question;
import net.tabarcraft.opencraft.QuestionStore;

import static net.tabarcraft.opencraft.OCUtils.showAlert;

public class AddQuestionView extends VBox {

    private final TextField queryField;
    private final TextField answerField;
    private final QuestionStore questionStore;

    public AddQuestionView(QuestionStore store) {
        this.questionStore = store;
        getStyleClass().add("add-question-view");
        setSpacing(15);
        setPadding(new Insets(20));

        // Titre
        Text title = new Text("Ajouter une Question");
        title.getStyleClass().add("title-text");

        // Champ Question
        Label queryLabel = new Label("Question :");
        queryField = new TextField();
        queryField.setPromptText("Tapez la question ici...");

        // Champ Réponse
        Label answerLabel = new Label("Réponse :");
        answerField = new TextField();
        answerField.setPromptText("Tapez la réponse ici...");

        // Bouton Ajouter
        Button addButton = new Button("Ajouter");
        addButton.setDefaultButton(true); // ← permet la validation par Entrée
        addButton.setOnAction(e -> addQuestion());

        // Mise en page
        getChildren().addAll(
                title,
                queryLabel, queryField,
                answerLabel, answerField,
                addButton
        );
    }

    private void addQuestion() {
        String questionText = queryField.getText();
        String answerText = answerField.getText();

        if (!questionText.isEmpty() && !answerText.isEmpty()) {
            Question question = new Question(questionText, answerText);
            questionStore.addQuestion(question);
            queryField.clear();
            answerField.clear();

            System.out.println("Ajout : " + questionText + " => " + answerText);
            System.out.println("Taille actuelle : " + questionStore.getObservableQuestions().size());
            questionStore.getObservableQuestions().forEach(q -> System.out.println("-> " + q.getQuery()));
        } else {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Champs manquants",
                    "Veuillez remplir les deux champs.",
                    null
            );
        }
    }

    //private void showAlert(String message) {
    //    Alert alert = new Alert(Alert.AlertType.WARNING);
    //    alert.setTitle("Champs manquants");
    //    alert.setHeaderText(null);
    //    alert.setContentText(message);
    //    alert.showAndWait();
    //}
}

