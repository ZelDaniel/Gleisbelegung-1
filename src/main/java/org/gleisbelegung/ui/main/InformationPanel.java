package org.gleisbelegung.ui.main;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.gleisbelegung.database.Database;
import org.gleisbelegung.ui.lib.node.ButtonFactory;
import org.gleisbelegung.ui.lib.node.LabelFactory;
import org.gleisbelegung.ui.lib.panel.PanelInterface;
import org.gleisbelegung.ui.lib.style.NodeWrapper;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Represents the Panel at the top of the {@link MainWindow}
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

        nextRefresh = LabelFactory.create("Aktualisierung in x Sekunden", 16);
        gameTime = LabelFactory.create("Spielzeit: hh:mm", 16);

        pane = new NodeWrapper<>(new Pane(restart.getNode(), settings.getNode(),
                changeView.getNode(), nextRefresh.getNode(),
                gameTime.getNode()));

        Runnable r = () -> {
            while (true){
                updateGameTime();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();

        return pane.getNode();
    }

    @Override public void setSizes() {
        pane.getNode().setPrefHeight(60);
    }

    @Override public void onResize(double width, double height) {
        restart.getNode().setTranslateX(
                width / 2.0 - settings.getNode().getWidth() / 2.0
                        - restart.getNode().getWidth() - 15
        );
        restart.getNode().setTranslateY(10);

        settings.getNode().setTranslateX(
                width / 2.0 - settings.getNode().getWidth() / 2.0
        );
        settings.getNode().setTranslateY(10);

        changeView.getNode().setTranslateX(
                width / 2.0 - changeView.getNode().getWidth() / 2.0
                        + settings.getNode().getWidth() + 15
        );
        changeView.getNode().setTranslateY(10);

        nextRefresh.getNode().setTranslateX(
                width - nextRefresh.getNode().getWidth() * 1.1
        ); //i dont know why i need to multiply with 1.1, but else the text is out of scope
        nextRefresh.getNode().setTranslateY(15);

        gameTime.getNode().setTranslateX(10);
        gameTime.getNode().setTranslateY(15);
    }

    @Override public void onHide() {

    }

    @Override public void onVisible() {

    }

    private void updateGameTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        Database db = Database.getInstance();

        String time = format.format(TimeUnit.SECONDS.toMillis(db.getSimTime()));
        Platform.runLater(() -> {
            gameTime.getNode().setText("Simzeit: " + time);
        });
    }
}
