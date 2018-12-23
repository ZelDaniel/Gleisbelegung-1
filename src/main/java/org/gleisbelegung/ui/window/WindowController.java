package org.gleisbelegung.ui.window;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.ui.panel.PanelController;
import org.gleisbelegung.ui.panel.PanelInterface;


public class WindowController{
    private WindowInterface window;
    private PanelController panelController;
    private Stage stage;

    public WindowController(WindowInterface window, Stage stage){
        this.stage = stage;
        this.window = window;

        panelController = new PanelController();

        setEvents();
    }

    public Pane addPanel(PanelInterface panel){
        return panelController.addPanel(panel);
    }

    public void init(String title, double width, double height){
        stage.setResizable(true);
        stage.setTitle(title);
        stage.getIcons().add(new Image("icon.png"));

        stage.setMinWidth(width);
        stage.setMinHeight(height);

        Scene scene = window.init();
        scene.getStylesheets().add("ui/style.css");

        stage.setScene(scene);
        stage.show();

        panelController.setSizes();

        window.onResize(stage.getWidth(), stage.getHeight());
        panelController.onResize(stage.getWidth(), stage.getHeight());
    }

    private void setEvents(){
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            window.onResize(newVal.doubleValue(), stage.getHeight());
            panelController.onResize(newVal.doubleValue(), stage.getHeight());
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            window.onResize(stage.getWidth(), newVal.doubleValue());
            panelController.onResize(stage.getWidth(), newVal.doubleValue());
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
