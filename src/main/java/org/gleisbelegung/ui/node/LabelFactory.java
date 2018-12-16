package org.gleisbelegung.ui.node;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import org.gleisbelegung.ui.style.Style;

public class LabelFactory {

    public static Label create(String text, int fontSize){
        Label l = new Label(text);

        l.setFont(Font.font(fontSize));
        Style.applyClass(l, "text_color");

        return l;
    }
}
