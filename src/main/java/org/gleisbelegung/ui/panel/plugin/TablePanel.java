package org.gleisbelegung.ui.panel.plugin;

import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.panel.PanelInterface;


/**
 * Represents the Panel at the center of the {@link org.gleisbelegung.ui.window.PluginWindow}
 */
public class TablePanel implements PanelInterface {

    Pane pane;

    @Override public Pane init() {
        pane = new Pane();

        pane.setStyle("-fx-background-color: #f00");
        return pane;
    }

    @Override public void setSizes() {

    }

    @Override public void onResize(double width, double height) {

    }

    @Override public void onHide() {

    }

    @Override public void onVisible() {

    }
}
