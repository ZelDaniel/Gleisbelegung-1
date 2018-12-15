package org.gleisbelegung.ui.window;

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
        setIcon();
        setEvents();
    }

    private void init(String title){
        stage.setResizable(true);
        stage.setTitle(title);

        stage.setScene(window.init());
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

    private void setIcon(){
        try {
            stage.getIcons().add(new Image(Plugin.class.getResourceAsStream(
                    "icon.png")));
        } catch (Exception e) {
            try {
                File f = new File("src/main/resources/icon.png");
                stage.getIcons().add(new Image(new FileInputStream(f)));

            } catch (Exception e1) {
                e.printStackTrace();
                e1.printStackTrace();
            }
        }
    }
}
