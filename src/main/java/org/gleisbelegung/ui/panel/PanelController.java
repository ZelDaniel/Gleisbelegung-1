package org.gleisbelegung.ui.panel;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PanelController {
    private List<PanelInterface> panels;

    public PanelController(){
        panels = new ArrayList<>();
    }

    public Pane addPanel(PanelInterface panel){
        panels.add(panel);
        return panel.init();
    }

    public void setSizes(){
        Consumer<PanelInterface> consumer = PanelInterface::setSizes;
        loopAll(consumer);
    }

    public void onResize(double width, double height){
        Consumer<PanelInterface> consumer = panel -> panel.onResize(width, height);
        loopAll(consumer);
    }

    private void loopAll(Consumer<PanelInterface> consumer){
        for (PanelInterface panel : panels){
            consumer.accept(panel);
        }
    }
}
