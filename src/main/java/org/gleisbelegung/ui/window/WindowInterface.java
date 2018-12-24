package org.gleisbelegung.ui.window;

import javafx.scene.Scene;

/**
 * interface which should be implemented by every Window
 */
public interface WindowInterface {

    /**
     * called after the {@link org.gleisbelegung.ui.window.WindowController#init(String, double, double) WindowController.init(args)}. <br>
     * should create and initialize all elements of the Window including all {@link org.gleisbelegung.ui.panel.PanelInterface Panels}
     *
     * @return styled {@link javafx.scene.Scene Scene} in order to create the {@link javafx.stage.Stage Stage}
     */
    Scene init();

    /**
     * called after a resize/maximize/minimize of the {@link javafx.stage.Stage Stage}
     *
     * @param width the absolte width of the {@link javafx.stage.Stage Stage}
     * @param height the absolte height of the {@link javafx.stage.Stage Stage}
     */
    void onResize(double width, double height);

    /**
     * called after the window is maximized
     */
    void onMaximize();

    /**
     * called after the window is minimized
     */
    void onMinimize();

    /**
     * called after the window is closed
     */
    void onClose();
}
