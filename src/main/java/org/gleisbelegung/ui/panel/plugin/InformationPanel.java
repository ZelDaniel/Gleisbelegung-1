package org.gleisbelegung.ui.panel.plugin;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.node.ButtonFactory;
import org.gleisbelegung.ui.node.LabelFactory;
import org.gleisbelegung.ui.panel.PanelInterface;


/**
 * Represents the Panel at the top of the {@link org.gleisbelegung.ui.window.PluginWindow}
 */
public class InformationPanel implements PanelInterface {

    private Button settings;
    private Button restart;
    private Button changeView;
    private Label nextRefresh;
    private Label gameTime;
    private Pane pane;

    @Override public Pane init() {
        settings = ButtonFactory.create("Einstellungen", 16);
        restart = ButtonFactory.create("Neustart", 16);
        changeView = ButtonFactory.create("Sichtwechsel", 16);

        nextRefresh = LabelFactory.create("Aktualiesierung in x Sekunden", 16);
        gameTime = LabelFactory.create("Spielzeit: hh:mm", 16);

        pane = new Pane(restart, settings, changeView, nextRefresh, gameTime);
        return pane;
    }

    @Override public void setSizes() {
        pane.setPrefHeight(pane.getHeight() + 20);
    }

    @Override public void onResize(double width, double height) {
        //        pane.setPrefHeight(pane.getHeight() + 20);

        restart.setTranslateX(
                width / 2.0 - restart.getWidth() / 2.0 - settings.getWidth());
        restart.setTranslateY(10);

        settings.setTranslateX(width / 2.0 - settings.getWidth() / 2.0);
        settings.setTranslateY(10);

        changeView.setTranslateX(
                width / 2.0 - changeView.getWidth() / 2.0 + settings.getWidth()
                        + 15);
        changeView.setTranslateY(10);

        nextRefresh.setTranslateX(width - nextRefresh.getWidth()
                * 1.1); //i dont know why i need to multiply with 1.1, but else the text is out of scope
        nextRefresh.setTranslateY(15);

        gameTime.setTranslateX(10);
        gameTime.setTranslateY(15);
    }

    @Override public void onHide() {

    }

    @Override public void onVisible() {

    }
}
