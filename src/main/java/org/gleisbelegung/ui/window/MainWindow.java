package org.gleisbelegung.ui.window;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainWindow implements WindowInterface {
    WindowController controller;

    public MainWindow(Stage stage){
        controller = new WindowController(this, stage, "Gleisbelegung");
    }

    @Override public Scene init() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #303030;");

        Button b = new Button("Dies ist ein Test Button");
        b.getStyleClass().add("custom");


        Pane pane = new Pane(b);

        bp.setCenter(pane);

        Scene scene = new Scene(bp);
        return scene;
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
