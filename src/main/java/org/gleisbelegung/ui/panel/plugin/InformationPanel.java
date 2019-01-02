package org.gleisbelegung.ui.panel.plugin;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.node.ButtonFactory;
import org.gleisbelegung.ui.node.LabelFactory;
import org.gleisbelegung.ui.panel.PanelInterface;
import org.gleisbelegung.ui.style.NodeWrapper;


/**
 * Represents the Panel at the top of the {@link org.gleisbelegung.ui.window.PluginWindow}
 */
public class InformationPanel implements PanelInterface {

    private NodeWrapper<Button> settings;
    private NodeWrapper<Button> restart;
    private NodeWrapper<Button> changeView;
    private NodeWrapper<Label> nextRefresh;
    private NodeWrapper<Label> gameTime;
    private NodeWrapper<Pane> pane;

    @Override public Pane init() {
        settings = ButtonFactory.create("Einstellungen", 16);
        restart = ButtonFactory.create("Neustart", 16);
        changeView = ButtonFactory.create("Sichtwechsel", 16);

        nextRefresh = LabelFactory.create("Aktualiesierung in x Sekunden", 16);
        gameTime = LabelFactory.create("Spielzeit: hh:mm", 16);

        pane = new NodeWrapper<>(new Pane(restart.getNode(), settings.getNode(),
                changeView.getNode(), nextRefresh.getNode(),
                gameTime.getNode()));

        return pane.getNode();
    }

    @Override public void setSizes() {
        pane.getNode().setPrefHeight(60);
    }

    @Override public void onResize(double width, double height) {
        restart.getNode().setTranslateX(
                width / 2.0 - settings.getNode().getWidth() / 2.0 - restart
                        .getNode().getWidth() - 15);
        restart.getNode().setTranslateY(10);

        settings.getNode().setTranslateX(
                width / 2.0 - settings.getNode().getWidth() / 2.0);
        settings.getNode().setTranslateY(10);

        changeView.getNode().setTranslateX(
                width / 2.0 - changeView.getNode().getWidth() / 2.0 + settings
                        .getNode().getWidth() + 15);
        changeView.getNode().setTranslateY(10);

        nextRefresh.getNode().setTranslateX(width - nextRefresh.getNode().getWidth()
                * 1.1); //i dont know why i need to multiply with 1.1, but else the text is out of scope
        nextRefresh.getNode().setTranslateY(15);

        gameTime.getNode().setTranslateX(10);
        gameTime.getNode().setTranslateY(15);
    }

    @Override public void onHide() {

    }

    @Override public void onVisible() {

    }
}
