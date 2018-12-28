package org.gleisbelegung.updater;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.ui.node.ButtonFactory;
import org.gleisbelegung.ui.node.LabelFactory;
import org.gleisbelegung.ui.style.Style;
import org.gleisbelegung.ui.window.WindowController;
import org.gleisbelegung.ui.window.WindowInterface;


public class DownloadUpdateWindow implements WindowInterface {

    private WindowController controller;
    private Updater updater;

    private Label informations;
    private Button close;

    public DownloadUpdateWindow(Updater updater) {
        this.updater = updater;

        controller = new WindowController(this, new Stage());
        controller.init("Gleisbelegung - Downloader", 400, 400);
    }

    @Override public Scene init() {
        informations = LabelFactory
                .create("Neue Version wird nun heruntergeladen:", 16);
        informations.setTranslateX(20);
        informations.setTranslateY(20);

        Runnable r = () -> {
            System.out.println("starte herunterladen");
            updater.downloadNewVersion();
            System.out.println("herunterladen beendet");
            close.setDisable(false);
        };
        new Thread(r).start();

        Runnable onCloseClick = () -> {
            controller.close();
            updater.startNewerVersion();
        };
        close = ButtonFactory.create("Update anwenden", 16, onCloseClick);
        close.setDisable(true);
        close.setTranslateX(115);
        close.setTranslateY(300);

        Pane pane = new Pane(informations, close);
        Style.applyClass(pane, "dark_gray");

        return new Scene(pane);
    }

    @Override public void onResize(double width, double height) {

    }

    @Override public void onMaximize() {

    }

    @Override public void onMinimize() {

    }

    @Override public void onClose() {

    }
}
