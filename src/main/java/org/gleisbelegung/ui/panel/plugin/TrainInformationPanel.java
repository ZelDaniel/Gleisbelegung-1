package org.gleisbelegung.ui.panel.plugin;

import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.panel.PanelInterface;
import org.gleisbelegung.ui.style.NodeWrapper;
import org.gleisbelegung.ui.style.color.BackgroundColor;


/**
 * Represents the Panel at the right of the {@link org.gleisbelegung.ui.window.PluginWindow}
 */
public class TrainInformationPanel implements PanelInterface {

    NodeWrapper<Pane> pane;

    @Override public Pane init() {
        pane = new NodeWrapper<>(new Pane());

        pane.addStyle(new BackgroundColor("#0f0"));
        return pane.getNode();
    }

    @Override public void setSizes() {
        pane.getNode().setPrefWidth(200);
    }

    @Override public void onResize(double width, double height) {

    }

    @Override public void onHide() {

    }

    @Override public void onVisible() {

    }
}
