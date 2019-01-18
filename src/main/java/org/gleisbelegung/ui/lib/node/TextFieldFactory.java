package org.gleisbelegung.ui.lib.node;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;
import org.gleisbelegung.ui.lib.style.color.TextColor;


public class TextFieldFactory {

    /**
     * Helper for creating Labels
     *
     * @param text the text of the label
     * @param hint the text to be displayed on mouse hover
     * @param fontSize font size in px
     * @param onTextChange runnable executed when the text changes
     * @return initialized and style JavaFX Label
     */
    public static NodeWrapper<TextField> create(String text, String hint,
            int fontSize, Runnable onTextChange) {
        TextField tf = new TextField(text);

        tf.setFont(Font.font(fontSize));
        tf.setPromptText(hint);
        tf.getStylesheets().add("ui/nodes/text-field.css");

        if (onTextChange != null) {
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                onTextChange.run();
            });
        }

        NodeWrapper<TextField> nodeWrapper = new NodeWrapper<>(tf);

        return nodeWrapper;
    }

    /**
     *
     * @param text the text of the label
     * @param hint hint the text to be displayed on mouse hover
     * @param fontSize fontSize font size in px
     * @return initialized and style JavaFX Label
     */
    public static NodeWrapper<TextField> create(String text, String hint,
            int fontSize) {
        return create(text, hint, fontSize, null);
    }
}
