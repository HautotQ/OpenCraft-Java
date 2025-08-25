package net.tabarcraft.opencraft.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        InputStream fontStream = getClass().getResourceAsStream("/fonts/JetBrainsMono-Bold.ttf");

        if (fontStream == null) {
            System.out.println("❌ Fichier introuvable !");
            return;
        }

        Font customFont = Font.loadFont(fontStream, 20);
        if (customFont == null) {
            System.out.println("❌ Police non chargée !");
            return;
        }

        System.out.println("✅ Police chargée : " + customFont.getName());

        Label label = new Label("Texte en JetBrains Mono !");
        label.setFont(customFont);

        Scene scene = new Scene(label, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

