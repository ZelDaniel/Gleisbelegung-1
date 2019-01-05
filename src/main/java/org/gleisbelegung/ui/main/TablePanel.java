package org.gleisbelegung.ui.main;

import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.lib.panel.Panel;
import org.gleisbelegung.ui.lib.panel.PanelInterface;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;


/**
 * Represents the Panel at the center of the {@link MainWindow}
 */
public class TablePanel extends Panel {

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
