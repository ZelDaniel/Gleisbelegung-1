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
     * @param text the text of the button
     * @param fontSize font size in px
     * @param onClick runnable executed when the user clicks the button
     * @param executeRunnableOnEnter true to execute the runnable if the button is focused and the users presses enter
     * @return initialized and styled JavaFX Button
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
     * @param text the text of the button
     * @param fontSize font size in px
     * @return initialized and styled JavaFX Button
     */
    public static NodeWrapper<Button> create(String text, int fontSize) {
        return create(text, fontSize, null, false);
    }
}
