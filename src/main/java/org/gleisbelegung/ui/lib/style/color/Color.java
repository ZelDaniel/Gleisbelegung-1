package org.gleisbelegung.ui.lib.style.color;

class Color {
    private String color;

    Color(String color){
        if (color.matches("[0-9]+")) {
            this.color = "#"+color;
        } else {
            this.color = color;
        }
    }

    String get(){
        return color;
    }

}
