package net.tabarcraft.opencraft.View;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CommandView extends VBox {
    private String userInput = "";
    private VBox historyBox = new VBox(5); // conteneur visuel pour l'historique
    private ScrollPane scrollPane = new ScrollPane();

    public CommandView() {
        this.getStyleClass().add("command-view");
        historyBox.setMaxWidth(Double.MAX_VALUE);
        historyBox.setPrefHeight(400);
        VBox.setVgrow(historyBox, Priority.ALWAYS);

        scrollPane.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setPrefHeight(400);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        TextField input = new TextField();
        input.setPromptText("Entrez votre commande...");
        input.setMaxWidth(Double.MAX_VALUE);
        input.setPrefWidth(1000);
        VBox.setVgrow(input, Priority.ALWAYS);


        Button exec = new Button("Exécuter");
        exec.setDisable(true); // désactivé au départ car le champ est vide

        // Listener qui active/désactive le bouton selon que le champ est vide ou non
        input.textProperty().addListener((obs, oldText, newText) -> {
            exec.setDisable(newText.trim().isEmpty());
        });
        exec.setDefaultButton(true);
        exec.setOnAction(e -> {
            userInput = input.getText().trim();
            execCommand(userInput);
            input.clear();
            // Scroll automatique vers le bas à chaque nouvelle commande
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });

        HBox hBox = new HBox(15, input, exec);

        // Configurer le ScrollPane avec historyBox dedans
        scrollPane.setContent(historyBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300); // hauteur du scroll, ajustable

        this.getChildren().addAll(scrollPane, hBox);
    }

    private void execCommand(String input) {
        String trimmedInput = input.trim();

        if(trimmedInput.toLowerCase().startsWith("/say ")) {
            // On récupère tout ce qui vient après "/say " (longueur 5)
            String message = trimmedInput.substring(5);
            historyBox.getChildren().add(new Label(message));
        } else if(trimmedInput.equalsIgnoreCase("/clear")) {
            historyBox.getChildren().clear();
        } else {
            historyBox.getChildren().add(new Label("Commande non reconnue"));
        }
    }
}
