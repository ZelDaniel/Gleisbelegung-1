package org.gleisbelegung.ui.main;

import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.lib.panel.Panel;
import org.gleisbelegung.ui.lib.panel.PanelInterface;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;


/**
 * Represents the Panel at the bottom of the {@link MainWindow}
 */
public class StatusPanel extends Panel {

    NodeWrapper<Pane> pane;

    @Override public Pane init() {
        pane = new NodeWrapper<>(new Pane());

        pane.addStyle(new BackgroundColor("#0000ff"));

        return pane.getNode();
    }

    @Override public void setSizes() {
        pane.getNode().setPrefHeight(35);
    }

    @Override public void onResize(double width, double height) {

    }

    @Override public void onHide() {

    }

    @Override public void onVisible() {

    }
}
