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
     * @param text
     * @param fontSize
     * @return intitialized and style JavaFX Label
     */
    public static NodeWrapper<TextField> create(String text, String hint,
            int fontSize, Runnable onTextChange) {
        TextField tf = new TextField(text);

        tf.setFont(Font.font(fontSize));
        tf.setPromptText(hint);

        if (onTextChange != null) {
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                onTextChange.run();
            });
        }

        NodeWrapper<TextField> nodeWrapper = new NodeWrapper<>(tf);
        nodeWrapper.addStyle(new TextColor("#fff"));
        nodeWrapper.addStyle(new BackgroundColor("#505050"));

        return nodeWrapper;
    }

    public static NodeWrapper<TextField> create(String text, String hint,
            int fontSize) {
        return create(text, hint, fontSize, null);
    }
}
