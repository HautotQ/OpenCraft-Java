package net.tabarcraft.opencraft.View;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.tabarcraft.opencraft.OCUtils;
import net.tabarcraft.opencraft.Question;
import net.tabarcraft.opencraft.QuestionStore;
import net.tabarcraft.opencraft.Settings;

import java.util.*;
import java.util.function.Consumer;

public class PlayQuestionsView extends VBox {
    private QuestionStore questionStore;
    private Question currentQuestion;
    private boolean showingAnswer = false;
    private String userAnswer = "";
    private List<Question> remainingQuestions = new ArrayList<>();
    private boolean allQuestionsAnsweredOnce = false;
    private List<Question> incorrectQuestions = new ArrayList<>();
    private Set<Question> incorrectQuestionSet = new HashSet<>();
    private boolean shouldReaskIncorrectQuestion = false;
    private int score = 0;
    private Consumer<Node> onEndView;

    private Label questionLabel = new Label();
    private Label progressLabel = new Label();
    private ProgressBar progressBar = new ProgressBar(0);
    private TextField answerField = new TextField();
    private Label remainingLabel = new Label();
    private Button checkButton = new Button("Vérifier la réponse");
    private Stage primaryStage;

    public PlayQuestionsView(Stage stage, QuestionStore store, Consumer<Node> onEndView) {
        this.getStyleClass().add("play-questions-view");
        this.primaryStage = stage;
        this.questionStore = store;
        this.onEndView = onEndView;
        remainingQuestions = new ArrayList<>(store.getObservableQuestions());
        Collections.shuffle(remainingQuestions);
        if (!remainingQuestions.isEmpty()) {
            currentQuestion = remainingQuestions.get(0);
        }

        askNextQuestion();
        initUI();
    }

    private void initUI() {
        this.setSpacing(10);
        this.setPadding(new Insets(15));

        if (questionStore.getObservableQuestions().isEmpty()) {
            Label label = new Label("Pas de questions enregistrées...");
            this.getChildren().add(label);
        } else {
            questionLabel.setWrapText(true);
            questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

            answerField.setPromptText("Réponse");

            checkButton.setDefaultButton(true);
            checkButton.setOnAction(e -> checkAnswer());

            VBox.setVgrow(answerField, Priority.ALWAYS);

            this.getChildren().addAll(
                    remainingLabel,
                    progressLabel,
                    progressBar,
                    questionLabel,
                    answerField,
                    checkButton
            );
        }
    }

    private void updateUI() {
        if (allQuestionsAnsweredOnce) {
            showEndView();
            return;
        }

        if (currentQuestion != null) {
            questionLabel.setText(currentQuestion.getQuery());
            remainingLabel.setText("Questions restantes : " + remainingQuestions.size());
            int total = questionStore.getObservableQuestions().size();
            double progress = (double)(total - remainingQuestions.size()) / total;
            progressBar.setProgress(progress);
            progressLabel.setText("Progression : " + (int)(progress * 100) + "%");
        }
    }

    private void askNextQuestion() {
        if (shouldReaskIncorrectQuestion) {
            shouldReaskIncorrectQuestion = false;
            if (!incorrectQuestions.isEmpty()) {
                remainingQuestions.addAll(incorrectQuestions);
                incorrectQuestions.clear();
            }
        }

        if (remainingQuestions.isEmpty()) {
            allQuestionsAnsweredOnce = true;
            updateUI();
            return;
        }

        currentQuestion = remainingQuestions.remove(0);
        showingAnswer = false;
        userAnswer = "";
        answerField.clear();
        updateUI();
    }

    private void checkAnswer() {
        userAnswer = answerField.getText().trim().toLowerCase();

        if (userAnswer.isEmpty()) {
            OCUtils.showAlert(
                    Alert.AlertType.ERROR,
                    "Réponse manquante",
                    null,
                    "La réponse à mettre est:\n" + currentQuestion.getAnswer()
            );
            incorrectQuestions.add(currentQuestion);
            incorrectQuestionSet.add(currentQuestion);
            shouldReaskIncorrectQuestion = true;
            //playSound("gameover");
            answerField.clear();
        } else if (userAnswer.equalsIgnoreCase(currentQuestion.getAnswer())) {
            if (!incorrectQuestionSet.contains(currentQuestion)) {
                score++;
            }
            //playSound("levelup");
            askNextQuestion();
        } else if (isApproximatelyEqual(userAnswer, currentQuestion.getAnswer()) && Settings.isCoolMode()) {
            if (!incorrectQuestionSet.contains(currentQuestion)) {
                score++;
            }
            //playSound("levelup");
            askNextQuestion();
        } else {
            OCUtils.showAlert(
                    Alert.AlertType.ERROR,
                    "Mauvaise réponse",
                    null,
                    "La bonne réponse est:\n" + currentQuestion.getAnswer()
            );
            incorrectQuestions.add(currentQuestion);
            incorrectQuestionSet.add(currentQuestion);
            shouldReaskIncorrectQuestion = true;
            //playSound("gameover");
            answerField.clear();
        }
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showEndView() {
        VBox endRoot = new VBox(10);
        endRoot.setPadding(new Insets(20));
        Label endLabel = new Label("Fin des questions !\nScore : " + score + "/" + questionStore.getObservableQuestions().size());
        endLabel.setStyle("-fx-font-size: 20px;");
        endRoot.getChildren().add(endLabel);

        // Appel du callback pour afficher la vue de fin en centre
        if (onEndView != null) {
            onEndView.accept(endRoot);
        }
    }

    private boolean isApproximatelyEqual(String input, String target) {
        return levenshteinDistance(input, target) <= 4;
    }

    private int levenshteinDistance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;

        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
