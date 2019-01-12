package org.gleisbelegung.ui.lib.node;

import javafx.scene.layout.GridPane;
import org.gleisbelegung.ui.lib.style.NodeWrapper;

public class GridPaneFactory {

    /**
     * Helper for creating GridPanes
     *
     * @return intitialized and style JavaFX GridPane
     */
    public static NodeWrapper<GridPane> create() {
        GridPane p = new GridPane();
        p.setVgap(0);
        p.setHgap(0);

        NodeWrapper<GridPane> nodeWrapper = new NodeWrapper<>(p);

        return nodeWrapper;
    }
}
