package org.gleisbelegung.ui.window;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.gleisbelegung.Plugin;

import java.io.File;
import java.io.FileInputStream;


public class WindowController {

    private WindowInterface window;
    private Stage stage;

    public WindowController(WindowInterface window, Stage stage, String title){
        this.stage = stage;
        this.window = window;

        init(title);
        setEvents();
    }

    private void init(String title){
        stage.setResizable(true);
        stage.setTitle(title);
        stage.getIcons().add(new Image("icon.png"));

        stage.setMinWidth(1250);
        stage.setMinHeight(700);

        Scene scene = window.init();
        scene.getStylesheets().add("ui/style.css");
        stage.setScene(scene);
        stage.show();
    }

    private void setEvents(){
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            window.onResize(newVal.doubleValue(), stage.getHeight());
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            window.onResize(stage.getWidth(), newVal.doubleValue());
        });

        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal) window.onMaximize();
            else window.onMinimize();
        });

        stage.setOnCloseRequest((e) -> {
            window.onClose();
        });
    }
}
