package org.gleisbelegung.ui.node;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import org.gleisbelegung.ui.style.StyleInterface;


public class LabelFactory {

    /**
     * Helper for creating Labels
     *
     * @param text
     * @param fontSize
     * @return intitialized and style JavaFX Label
     */
    public static Label create(String text, int fontSize) {
        Label l = new Label(text);

        l.setFont(Font.font(fontSize));
        //StyleInterface.applyClass(l, "text_color");

        return l;
    }
}
