package net.tabarcraft.opencraft;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.tabarcraft.opencraft.View.ContentView;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) {
        OCUtils.getFont();
        ContentView content = new ContentView();
        Scene scene = new Scene(content, 800, 600);
        OCUtils.applyCSS(scene);

        primaryStage.setTitle("OpenCraft");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
