package org.gleisbelegung.ui.window;

import javafx.scene.Scene;


public interface WindowInterface {

    Scene init();
    void onResize(double width, double height);
    void onMaximize();
    void onMinimize();
    void onClose();
}
