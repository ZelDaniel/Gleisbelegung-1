package org.gleisbelegung.ui.style;

import javafx.scene.Node;

public class Style {

    public static void applyClass(Node n, String name){
        n.getStyleClass().add(name);
    }
}
