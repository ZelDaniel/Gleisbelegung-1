package org.gleisbelegung.ui.lib.style.color;

import org.gleisbelegung.ui.lib.style.StyleInterface;

/**
 * can set the text color of a {@link javafx.scene.Node}
 */
public class TextColor implements StyleInterface {
    String color;

    public TextColor(String color){
        this.color = color;
    }

    @Override public String apply() {
        return "-fx-text-fill: " + color;
    }
}
