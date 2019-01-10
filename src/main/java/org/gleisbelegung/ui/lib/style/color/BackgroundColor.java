package org.gleisbelegung.ui.lib.style.color;

import org.gleisbelegung.ui.lib.style.StyleInterface;

public class BackgroundColor implements StyleInterface {

    private Color color;

    public BackgroundColor(String color){
        this.color = new Color(color);
    }

    @Override
    public String apply() {
        return "-fx-background-color: " + color.get();
    }
}
