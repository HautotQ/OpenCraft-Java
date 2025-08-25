package net.tabarcraft.opencraft.View;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CommandsList extends VBox {
    String[] commands;

    public CommandsList(String[] cmds) {
        this.commands = cmds;
        this.getStyleClass().add("commands-list");

        for (String cmd : commands) {
            Label commandText = new Label(cmd);
            Button copy = new Button("Copier la commande");
            copy.setOnAction(e -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(cmd);
                clipboard.setContent(content);
            });

            HBox hBox = new HBox(); // Crée un nouveau HBox pour chaque commande
            hBox.getStyleClass().add("h-box");
            hBox.getChildren().addAll(commandText, copy);

            this.getChildren().add(hBox);
        }
    }
}
