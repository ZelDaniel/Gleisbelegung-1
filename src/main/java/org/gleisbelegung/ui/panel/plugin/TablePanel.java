package org.gleisbelegung.ui.panel.plugin;

import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.panel.PanelInterface;
import org.gleisbelegung.ui.style.NodeWrapper;
import org.gleisbelegung.ui.style.color.BackgroundColor;


/**
 * Represents the Panel at the center of the {@link org.gleisbelegung.ui.window.PluginWindow}
 */
public class TablePanel implements PanelInterface {

    NodeWrapper<Pane> pane;

    @Override public Pane init() {
        pane = new NodeWrapper<>(new Pane());

        pane.addStyle(new BackgroundColor("#f00"));

        return pane.getNode();
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
