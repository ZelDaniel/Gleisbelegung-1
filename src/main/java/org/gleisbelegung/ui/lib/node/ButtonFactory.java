package org.gleisbelegung.ui.lib.node;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import org.gleisbelegung.ui.lib.style.NodeWrapper;


public class ButtonFactory {

    /**
     * Helper for creating a JavaFX Button
     *
     * @param text
     * @param fontSize
     * @param onClick
     * @return itialized and styled JavaFX Button
     */
    public static NodeWrapper<Button> create(String text, int fontSize,
            Runnable onClick, boolean executeRunnableOnEnter) {
        Button b = new Button(text);
        b.setFont(Font.font(fontSize));
        b.getStylesheets().add("ui/nodes/button.css");

        if (onClick != null) {
            b.setOnAction((e) -> onClick.run());
        }

        if(executeRunnableOnEnter){
            b.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
                if (ev.getCode() == KeyCode.ENTER) {
                    b.fire();
                    ev.consume();
                }
            });
        }

        return new NodeWrapper<>(b);
    }

    /**
     * {@link ButtonFactory#create(String, int, Runnable, boolean)}
     *
     * @param text
     * @param fontSize
     * @return
     */
    public static NodeWrapper<Button> create(String text, int fontSize) {
        return create(text, fontSize, null, false);
    }
}
