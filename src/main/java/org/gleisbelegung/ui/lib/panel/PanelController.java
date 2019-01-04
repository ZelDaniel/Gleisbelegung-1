package org.gleisbelegung.ui.lib.panel;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.lib.window.WindowController;
import org.gleisbelegung.ui.lib.window.WindowInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * is part of the {@link WindowController WindowController} and mainly stores and adapts the {@link PanelInterface Panel}
 */
public class PanelController {

    private List<PanelInterface> panels;

    public PanelController() {
        panels = new ArrayList<>();
    }

    /**
     * Initializes and stores the added panel in order to be able to adapt them as needed
     *
     * @param panel a class implementing the {@link PanelInterface}
     * @return JavaFX Pane
     */
    public Pane addPanel(PanelInterface panel) {
        panels.add(panel);

        Pane pane = panel.init();
        pane.setOnMouseClicked(e -> onMouseClicked(pane, panel, e));
        return pane;
    }

    /**
     * called after the {@link javafx.stage.Stage Stage} is shown in order to work with all proper with and height values
     */
    public void setSizes() {
        Consumer<PanelInterface> consumer = PanelInterface::setSizes;
        loopAll(consumer);
    }

    /**
     * called by {@link WindowController WindowController} after a resize of {@link WindowInterface Window}
     *
     * @param width  width of the {@link WindowInterface Window}
     * @param height height of the {@link WindowInterface Window}
     */
    public void onResize(double width, double height) {
        Consumer<PanelInterface> consumer =
                panel -> panel.onResize(width, height);
        loopAll(consumer);
    }

    /**
     * loops every {@link PanelInterface Panel} and ececute the consumer
     *
     * @param consumer a method call to be executed on every {@link PanelInterface Panel}
     */
    private void loopAll(Consumer<PanelInterface> consumer) {
        for (PanelInterface panel : panels) {
            consumer.accept(panel);
        }
    }

    private void onMouseClicked(Pane pane, PanelInterface panel, MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            pane.setPrefWidth(pane.getWidth() + 10);
            pane.setPrefHeight(pane.getHeight() + 10);
        } else if (e.getButton() == MouseButton.SECONDARY) {
            pane.setPrefWidth(pane.getWidth() - 10);
            pane.setPrefHeight(pane.getHeight() - 10);
        } else if (e.getButton() == MouseButton.MIDDLE) {
            panel.setSizes();
        }
    }
}
