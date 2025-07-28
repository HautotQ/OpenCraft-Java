package net.tabarcraft.opencraft;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ContentView extends VBox {
    private BorderPane root;
    private final QuestionStore questionStore = new QuestionStore(); // partagé avec les vues

    public ContentView() {
        root = new BorderPane();
        this.getChildren().add(root);

        Label defaultLabel = new Label("Bienvenue dans OpenCraft ! Sélectionne une option dans le menu pour commencer.");
        defaultLabel.setStyle("-fx-font-size: 16px; -fx-text-alignment: center; -fx-padding: 20;");
        root.setCenter(new StackPane(defaultLabel));

        // Création de la barre de menu
        MenuBar menuBar = new MenuBar();
        menuBar.useSystemMenuBarProperty().set(true);

        // Menu "craftion"
        Menu craftion = new Menu("Menu");

        MenuItem addQuestion = new MenuItem("Ajouter une Question");
        addQuestion.setOnAction(e -> {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL); // Modal : bloque la principale

            AddQuestionView view = new AddQuestionView(questionStore);
            Scene scene = new Scene(view, 600, 400);

            // Charge le CSS dans cette scène
            OCUtils.applyCSS(scene);

            dialog.setScene(scene);
            dialog.setTitle("Ajouter une Question");
            dialog.showAndWait(); // Attends la fermeture avant de revenir à la principale
        });

        MenuItem playQuestions = new MenuItem("Jouer les Questions");
        playQuestions.setOnAction(e -> {
            Stage playStage = new Stage();
            PlayQuestionsView playView = new PlayQuestionsView(playStage, questionStore, endView -> {
                root.setCenter(endView);  // root est ton BorderPane principal contenant la MenuBar en haut
            });
            root.setCenter(playView);
        });
        MenuItem viewQuestions = new MenuItem("Voir les Questions");
        viewQuestions.setOnAction(e -> {
            ViewQuestionsView view = new ViewQuestionsView(questionStore);
            root.setCenter(view);
        });
        MenuItem questionsExample = new MenuItem("Exemples de Questions");
        questionsExample.setOnAction(e -> {
            ObservableList<Question> temporaryList = FXCollections.observableArrayList(
                    new Question("1+1", "2"),
                    new Question("1+2", "3"),
                    new Question("1+3", "4"),
                    new Question("1+4", "5"),
                    new Question("1+5", "6"),
                    new Question("1+6", "7")
            );
            QuestionsExample qExample = new QuestionsExample(temporaryList);
            root.setCenter(qExample);
        });

        MenuItem editQuestions = new MenuItem("Éditeur de fichiers clist/txt");
        editQuestions.setOnAction(e -> {
            Stage editStage = new Stage();
            // Affiche temporairement une vue de chargement
            root.setCenter(new Label("Chargement de l’éditeur..."));

            new Thread(() -> {
                FileEdit editor = new FileEdit(editStage); // traitement potentiellement lent

                Platform.runLater(() -> {
                    root.setCenter(editor);
                });
            }).start();
        });


        craftion.getItems().addAll(
                addQuestion,
                playQuestions,
                viewQuestions,
                new SeparatorMenuItem(),
                editQuestions
        );

        // Menu "MLInfo"
        /*
        Menu others = new Menu("MLInfo");
        MenuItem mlInfo = new MenuItem("MLInfo");
        mlInfo.setOnAction(e -> {
            MLInfoView ai = new MLInfoView();
            root.setCenter(ai);
        });

        others.getItems().addAll(
                mlInfo
        );
        */

        Menu cmds = new Menu("Commandes");
        MenuItem commands = new MenuItem("Commandes");
        commands.setOnAction(e -> {
            CommandView command = new CommandView();
            root.setCenter(command);
        });
        MenuItem cmdsList = new MenuItem("Liste de commandes");
        cmdsList.setOnAction(e -> {
            String[] cmdsTable = new String[]{
                    "/say Hello World",
                    "/clear"
            };
            CommandsList commandsList = new CommandsList(cmdsTable);
            root.setCenter(commandsList);
        });
        cmds.getItems().addAll(
                commands,
                cmdsList
        );

        // Menu "À propos"
        Menu about = new Menu("À propos");
        MenuItem infos = new MenuItem("Informations sur l'app");
        infos.setOnAction(e -> {
            OCUtils.showAlert(
                    Alert.AlertType.INFORMATION,
                    "Informations sur OpenCraft",
                    """
                            Version: 1.7
                            Développeur: Tabarcraft
                            """,
                    """
                            Notes:
                            1) OpenCraft est créé par un développeur indépendant(à l'origine) de 14 ans, lors de la sortie de la v1.
                            2) MLInfo est un secret.
                            """
            );
        });
        MenuItem settings = new MenuItem("Réglages");
        settings.setOnAction(e -> {
            SettingsView settingsView = new SettingsView();
            root.setCenter(settingsView);
        });
        MenuItem quit = new MenuItem("Quitter");
        quit.setOnAction(e -> System.exit(0));
        about.getItems().addAll(
                infos,
                settings,
                quit
        );

        // Ajout des menus à la barre
        menuBar.getMenus().addAll(craftion, about);

        VBox topContainer = new VBox();
        topContainer.getChildren().addAll(menuBar);
        root.setTop(topContainer);

    }
}
