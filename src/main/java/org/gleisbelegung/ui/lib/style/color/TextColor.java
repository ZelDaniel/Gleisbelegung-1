package org.gleisbelegung.ui.style.color;

import org.gleisbelegung.ui.style.StyleInterface;


public class TextColor implements StyleInterface {
    String color;

    public TextColor(String color){
        this.color = color;
    }

    @Override public String apply() {
        return "-fx-text-fill: " + color;
    }
}
