package org.gleisbelegung.ui.panel.plugin;

import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.panel.PanelInterface;

/**
 * Represents the Panel at the bottom of the {@link org.gleisbelegung.ui.window.PluginWindow}
 */
public class StatusPanel implements PanelInterface {
    Pane pane;

    @Override
    public Pane init() {
        pane = new Pane();

        pane.setStyle("-fx-background-color: #00f");

        return pane;
    }

    @Override
    public void setSizes() {
        pane.setPrefHeight(35);
    }

    @Override
    public void onResize(double width, double height) {

    }

    @Override
    public void onHide() {

    }

    @Override
    public void onVisible() {

    }
}
