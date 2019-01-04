package org.gleisbelegung.updater;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.ui.lib.node.ButtonFactory;
import org.gleisbelegung.ui.lib.node.LabelFactory;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;
import org.gleisbelegung.ui.lib.window.WindowController;
import org.gleisbelegung.ui.lib.window.WindowInterface;


public class DownloadUpdateWindow implements WindowInterface {

    private WindowController controller;
    private Updater updater;

    private NodeWrapper<Label> informations;
    private NodeWrapper<Button> close;

    public DownloadUpdateWindow(Updater updater) {
        this.updater = updater;

        controller = new WindowController(this, new Stage());
        controller.init("Gleisbelegung - Downloader", 400, 400);
    }

    @Override public Scene init() {
        informations = LabelFactory
                .create("Neue Version wird nun heruntergeladen:", 16);
        informations.getNode().setTranslateX(20);
        informations.getNode().setTranslateY(20);

        Runnable r = () -> {
            System.out.println("starte herunterladen");
            updater.downloadNewVersion();
            System.out.println("herunterladen beendet");
            close.getNode().setDisable(false);
        };
        new Thread(r).start();

        Runnable onCloseClick = () -> {
            controller.close();
            updater.startNewerVersion();
        };
        close = ButtonFactory.create("Update anwenden", 16, onCloseClick);
        close.getNode().setDisable(true);
        close.getNode().setTranslateX(115);
        close.getNode().setTranslateY(300);

        NodeWrapper<Pane> pane = new NodeWrapper<>(new Pane(informations.getNode(), close.getNode()));
        pane.addStyle(new BackgroundColor("#303030"));

        return new Scene(pane.getNode());
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
