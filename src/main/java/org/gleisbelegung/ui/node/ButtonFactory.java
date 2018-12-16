package org.gleisbelegung.ui.node;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import org.gleisbelegung.ui.style.Style;

public class ButtonFactory {

    public static Button create(String text, int fontSize, Runnable onClick){
        Button b = new Button(text);
        Style.applyClass(b, "button");
        b.setFont(Font.font(fontSize));

        if(onClick != null) b.setOnAction((e) -> onClick.run());

        return b;
    }

    public static Button create(String text, int fontSize){
        return create(text, fontSize, null);
    }
}
