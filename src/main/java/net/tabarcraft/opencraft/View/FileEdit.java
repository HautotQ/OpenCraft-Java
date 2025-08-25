package net.tabarcraft.opencraft.View;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.tabarcraft.opencraft.Question;

import java.nio.file.Files;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileEdit extends VBox {
    private TextArea editor = new TextArea();

    public FileEdit(Window ownerWindow) {
        this.getStyleClass().add("file-edit");
        editor.setPrefHeight(400);

        editor.setMaxWidth(Double.MAX_VALUE);
        editor.setPrefHeight(400); // Tu peux ajuster la hauteur par défaut
        VBox.setVgrow(editor, Priority.ALWAYS);

        Button importText = new Button("Importer un fichier txt/clist");
        importText.setOnAction(e -> importFile(ownerWindow));

        Button shareFile = new Button("Exporter");
        shareFile.setOnAction(e -> saveFile(ownerWindow, editor.getText()));

        getChildren().addAll(
                editor,
                importText,
                shareFile
        );

        // Exemple d'utilisation (à adapter selon où tu récupères tes questions)
        // List<Question> questions = ...;
        // loadQuestions(questions);
    }

    /**
     * Formate la liste de questions dans le format demandé :
     * question\nréponse\n\n
     */
    public void loadQuestions(List<Question> questions) {
        StringBuilder sb = new StringBuilder();
        for (Question q : questions) {
            sb.append(q.getQuery()).append("\n");
            sb.append(q.getAnswer()).append("\n\n");
        }
        editor.setText(sb.toString());
    }

    private void saveFile(Window ownerWindow, String content) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer un fichier texte ou clist");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Fichiers texte (*.txt, *.clist)", "*.txt", "*.clist");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(ownerWindow);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
                // Tu peux afficher une alerte d'erreur ici
            }
        }
    }

    private void importFile(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer un fichier texte ou clist");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Fichiers texte (*.txt, *.clist)", "*.txt", "*.clist");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(ownerWindow);

        if (file != null) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                editor.clear();

                for (String line : lines) {
                    editor.appendText(line + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
                // Afficher une alerte en cas d'erreur
            }
        }
    }
}
