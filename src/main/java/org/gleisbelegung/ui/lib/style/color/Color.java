package org.gleisbelegung.ui.lib.style.color;

/**
 * stores the color for a {@link org.gleisbelegung.ui.lib.style.StyleInterface}
 */
class Color {
    private String color;

    /**
     * convert a string to a real JavaFX color <br>
     *     possibilities for color: #3033030, 303030, red
     *     if the string only contains numbers than set a # before
     * @param color string representing the color
     */
    Color(String color){
        if (color.matches("[0-9]+")) {
            this.color = "#"+color;
        } else {
            this.color = color;
        }
    }

    /**
     *
     * @return the adjusted color
     */
    String get(){
        return color;
    }

}
