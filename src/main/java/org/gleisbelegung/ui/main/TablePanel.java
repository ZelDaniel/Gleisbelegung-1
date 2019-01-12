package org.gleisbelegung.ui.main;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.lib.node.GridPaneFactory;
import org.gleisbelegung.ui.lib.node.LabelFactory;
import org.gleisbelegung.ui.lib.panel.Panel;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;


/**
 * Represents the Panel at the center of the {@link MainWindow}
 */
public class TablePanel extends Panel {

    NodeWrapper<Pane> pane;

    @Override public Pane init() {
        NodeWrapper<GridPane> platformGridPane = GridPaneFactory.create();

        for(int i = 0; i < 12; i++) {
            platformGridPane.getNode().add(LabelFactory.create(i+"", 13, 100, Pos.CENTER).getNode(), i, 0);
        }

        pane = new NodeWrapper<>(new Pane(platformGridPane.getNode()));



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
