package net.tabarcraft.opencraft.View;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.tabarcraft.opencraft.Question;
import net.tabarcraft.opencraft.QuestionStore;

public class EditQuestionView extends VBox {

    private final TextField questionField;
    private final TextField answerField;
    private final Button saveButton;
    private final Question question;
    private final QuestionStore store;

    public EditQuestionView(Question question, QuestionStore store) {
        this.question = question;
        this.store = store;
        this.getStyleClass().add("edit-question-view");
        System.out.println("Classes CSS : " + this.getStyleClass());

        setSpacing(10);
        setPadding(new Insets(10));

        Label questionLabel = new Label("Modifier la question :");
        questionField = new TextField(question.getQuery());

        Label answerLabel = new Label("Modifier la réponse :");
        answerField = new TextField(question.getAnswer());

        saveButton = new Button("Enregistrer");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(e -> saveChanges());

        getChildren().addAll(questionLabel, questionField, answerLabel, answerField, saveButton);
    }

    private void saveChanges() {
        String newQuery = questionField.getText().trim();
        String newAnswer = answerField.getText().trim();

        if (newQuery.isEmpty() || newAnswer.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "La question et la réponse ne peuvent pas être vides.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        question.setQuery(newQuery);
        question.setAnswer(newAnswer);

        store.updateQuestion(question); // suppose que store gère la mise à jour et rafraîchit la vue

        // Fermer la fenêtre modale
        getScene().getWindow().hide();
    }
}
