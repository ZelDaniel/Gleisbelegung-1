package org.gleisbelegung.updater;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.Plugin;
import org.gleisbelegung.ui.node.ButtonFactory;
import org.gleisbelegung.ui.node.LabelFactory;
import org.gleisbelegung.ui.style.Style;
import org.gleisbelegung.ui.window.WindowController;
import org.gleisbelegung.ui.window.WindowInterface;


public class UpdateAvailableWindow implements WindowInterface {

    WindowController controller;

    Label text;
    Label version;
    Button cancel;
    Button doUpdate;

    String currentVersion;
    String newVersion;
    Updater updater;

    public UpdateAvailableWindow(String currentVersion, String newVersion,
            Updater updater) {
        this.currentVersion = currentVersion;
        this.newVersion = newVersion;
        this.updater = updater;

        controller = new WindowController(this, new Stage());
        controller.init("Gleisbelegung - Updater", 800, 300);
    }

    @Override public Scene init() {
        text = LabelFactory
                .create("Hi, ich bin Dein Updater. Die Ersteller des Plugins haben mir zukommen lassen, dass es eine neue Version gibt:",
                        16);
        text.setWrapText(true);
        text.setTranslateX(20);
        text.setTranslateY(20);

        version = LabelFactory.create("Deine Version: " + currentVersion
                + "\nNeuste Version: " + newVersion, 16);
        version.setTranslateX(320);
        version.setTranslateY(100);

        Runnable onCancelClick = () -> {
            controller.close();
            Plugin.startPlugin(new Stage());
        };
        cancel = ButtonFactory.create("Abbrechen", 16, onCancelClick);
        cancel.setTranslateX(200);
        cancel.setTranslateY(200);

        Runnable onUpdateClick = () -> {
            controller.close();
            updater.copyFile();
            updater.startUpdater();
        };
        doUpdate = ButtonFactory.create("Mach mal", 16, onUpdateClick);
        doUpdate.setTranslateX(500);
        doUpdate.setTranslateY(200);

        Pane pane = new Pane(text, version, cancel, doUpdate);
        Style.applyClass(pane, "dark_gray");

        return new Scene(pane);
    }

    @Override public void onResize(double width, double height) {
        text.setMaxWidth(width - 40);
    }

    @Override public void onMaximize() {

    }

    @Override public void onMinimize() {

    }

    @Override public void onClose() {

    }
}
