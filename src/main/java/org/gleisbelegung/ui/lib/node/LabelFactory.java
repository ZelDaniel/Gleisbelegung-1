package org.gleisbelegung.ui.lib.node;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.TextColor;


public class LabelFactory {

    /**
     * Helper for creating Labels
     *
     * @param text the text of the label
     * @param fontSize font size in px
     * @return initialized and style JavaFX Label
     */
    public static NodeWrapper<Label> create(String text, int fontSize) {
        Label l = new Label(text);

        l.setFont(Font.font(fontSize));

        NodeWrapper<Label> nodeWrapper = new NodeWrapper<>(l);
        nodeWrapper.addStyle(new TextColor("#fff"));

        return nodeWrapper;
    }

    /**
     * Helper for creating Labels with spacing
     * @param text the text of the label
     * @param fontSize font size in px
     * @param width desired with of the label
     * @param alignment alignment of the text
     * @return initialized and style JavaFX Label
     */
    public static NodeWrapper<Label> create(String text, int fontSize, int width, Pos alignment) {
        NodeWrapper<Label> l = create(text,fontSize);
        l.getNode().setMinWidth(width);
        l.getNode().setMaxWidth(width);
        l.getNode().setAlignment(alignment);

        return l;
    }
}
