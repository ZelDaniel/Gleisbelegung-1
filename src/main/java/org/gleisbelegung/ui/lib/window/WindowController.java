package org.gleisbelegung.ui.lib.window;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.ui.lib.panel.PanelController;
import org.gleisbelegung.ui.lib.panel.PanelInterface;


public class WindowController {

    private WindowInterface window;
    private PanelController panelController;
    private Stage stage;

    public WindowController(WindowInterface window, Stage stage) {
        this.stage = stage;
        this.window = window;

        panelController = new PanelController();
    }

    /**
     * Adds a {@link PanelInterface Panel} to a Window
     *
     * @param panel panel to add to the window
     * @return initialized and styled {@link javafx.scene.layout.Pane Pane}
     */
    public Pane addPanel(PanelInterface panel) {
        return panelController.addPanel(panel);
    }

    /**
     * intitialize and show the {@link WindowInterface window}
     *
     * @param title  title of the window
     * @param width  the initial and min with of the {@link WindowInterface window}
     * @param height the initial and min height of the {@link WindowInterface window}
     */
    public void init(String title, double width, double height) {
        Scene scene = window.init();

        stage.setResizable(true);
        stage.setTitle(title);
        stage.getIcons().add(new Image("icon.png"));

        stage.setScene(scene);
        stage.show();

        stage.setMinWidth(width);
        stage.setMinHeight(height);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();

        panelController.setSizes();

        window.onResize(stage.getWidth(), stage.getHeight());
        panelController.onResize(stage.getWidth(), stage.getHeight());

        setEvents();
    }

    public void close() {
        panelController.onClose();
        stage.close();
    }

    /**
     * set the action listener for the {@link WindowInterface} events
     */
    private void setEvents() {
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            window.onResize(newVal.doubleValue(), stage.getHeight());
            panelController.onResize(newVal.doubleValue(), stage.getHeight());
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            window.onResize(stage.getWidth(), newVal.doubleValue());
            panelController.onResize(stage.getWidth(), newVal.doubleValue());
        });

        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal){
                window.onMaximize();
            } else {
                window.onMinimize();
            }
        });

        stage.setOnCloseRequest((e) -> {
            window.onClose();
        });
    }
}
