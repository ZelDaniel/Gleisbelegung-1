package org.gleisbelegung.ui.launch;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.Plugin;
import org.gleisbelegung.ui.lib.panel.PanelInterface;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;
import org.gleisbelegung.ui.lib.window.WindowController;
import org.gleisbelegung.ui.lib.window.WindowInterface;


public class LaunchWindow implements WindowInterface {

    WindowController controller;

    Plugin plugin;
    PanelInterface main;

    public LaunchWindow(Plugin plugin) {
        this.plugin = plugin;

        controller = new WindowController(this, new Stage());
        controller.init("Gleisbelegung - Wilkommen", 500, 200);
    }

    @Override public Scene init() {
        main = new MainPanel(plugin);

        NodeWrapper<Pane> pane = new NodeWrapper<>(controller.addPanel(main));
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

    public void connectionEstablished(){
        ((MainPanel)main).connectionEstablished();
    }

    public void initializationCompleted(){
        controller.close();
    }
}
