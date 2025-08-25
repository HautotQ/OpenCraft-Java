package net.tabarcraft.opencraft.View;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import net.tabarcraft.opencraft.AppStorage;
import net.tabarcraft.opencraft.Settings;

public class SettingsView extends VBox {
    private static final AppStorage storage = new AppStorage(System.getProperty("user.home") + "/.config.properties");
    private static final Settings settings = new Settings();

    public SettingsView() {
        this.getStyleClass().add("settings-view");

        CheckBox toggleCoolMode = new CheckBox("Mode Cool (recommandé si vous étudiez de la théorie)");
        toggleCoolMode.setOnAction( e -> {
            boolean enabled = toggleCoolMode.isSelected();
            Settings.setCoolMode(enabled);
        });
        /*
        toggleCoolMode.setSelected(storage.getBoolean("isCoolMode", true));
        toggleCoolMode.setOnAction(e -> {
            boolean enabled = toggleCoolMode.isSelected();
            storage.setBoolean("isCoolMode", enabled);
            System.out.println("Mode Cool activé : " + enabled);
        });
         */

        this.getChildren().addAll(toggleCoolMode);
    }
}
