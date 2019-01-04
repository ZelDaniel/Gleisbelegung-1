package org.gleisbelegung.updater;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.Plugin;
import org.gleisbelegung.ui.lib.node.ButtonFactory;
import org.gleisbelegung.ui.lib.node.LabelFactory;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;
import org.gleisbelegung.ui.lib.window.WindowController;
import org.gleisbelegung.ui.lib.window.WindowInterface;


public class UpdateAvailableWindow implements WindowInterface {

    WindowController controller;

    NodeWrapper<Label> text;
    NodeWrapper<Label> version;
    NodeWrapper<Button> cancel;
    NodeWrapper<Button> doUpdate;

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
        text.getNode().setWrapText(true);
        text.getNode().setTranslateX(20);
        text.getNode().setTranslateY(20);

        version = LabelFactory.create("Deine Version: " + currentVersion
                + "\nNeuste Version: " + newVersion, 16);
        version.getNode().setTranslateX(320);
        version.getNode().setTranslateY(100);

        Runnable onCancelClick = () -> {
            controller.close();
            Plugin.startPlugin(new Stage());
        };
        cancel = ButtonFactory.create("Abbrechen", 16, onCancelClick);
        cancel.getNode().setTranslateX(200);
        cancel.getNode().setTranslateY(200);

        Runnable onUpdateClick = () -> {
            controller.close();
            updater.copyFile();
            updater.startUpdater();
        };
        doUpdate = ButtonFactory.create("Mach mal", 16, onUpdateClick);
        doUpdate.getNode().setTranslateX(500);
        doUpdate.getNode().setTranslateY(200);

        NodeWrapper<Pane> pane = new NodeWrapper<>(new Pane(text.getNode(), version.getNode(), cancel.getNode(), doUpdate.getNode()));
        pane.addStyle(new BackgroundColor("#303030"));

        return new Scene(pane.getNode());
    }

    @Override public void onResize(double width, double height) {
        text.getNode().setMaxWidth(width - 40);
    }

    @Override public void onMaximize() {

    }

    @Override public void onMinimize() {

    }

    @Override public void onClose() {

    }
}
