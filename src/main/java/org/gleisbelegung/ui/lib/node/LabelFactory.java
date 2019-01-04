package org.gleisbelegung.ui.lib.node;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.TextColor;


public class LabelFactory {

    /**
     * Helper for creating Labels
     *
     * @param text
     * @param fontSize
     * @return intitialized and style JavaFX Label
     */
    public static NodeWrapper<Label> create(String text, int fontSize) {
        Label l = new Label(text);

        l.setFont(Font.font(fontSize));

        NodeWrapper<Label> nodeWrapper = new NodeWrapper<>(l);
        nodeWrapper.addStyle(new TextColor("#fff"));

        return nodeWrapper;
    }
}
