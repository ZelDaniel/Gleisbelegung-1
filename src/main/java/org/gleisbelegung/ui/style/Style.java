package org.gleisbelegung.ui.style;

import javafx.scene.Node;


/**
 * Helper class for easier Styling of nodes
 */
public class Style {

    /**
     * @param n    {@link javafx.scene.Node} to style
     * @param name class name specified in stylesheet of {@link org.gleisbelegung.ui.window.WindowController WindowController}
     */
    public static void applyClass(Node n, String name) {
        n.getStyleClass().add(name);
    }
}
