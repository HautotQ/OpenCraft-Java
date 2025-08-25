package net.tabarcraft.opencraft.View;

import javafx.collections.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import net.tabarcraft.opencraft.Question;

public class QuestionsExample extends VBox {
    private ObservableList<Question> questionExample = FXCollections.observableArrayList();

    public QuestionsExample(ObservableList<Question> list) {
        this.questionExample = list;
        this.getStyleClass().add("questions-example");

        for (Question question : list) {
            HBox hbox = new HBox(10);
            Label query = new Label(question.getQuery());
            Label answer = new Label(question.getAnswer());
            hbox.getChildren().addAll(query, answer);
            this.getChildren().add(hbox);
        }
    }
}
