package org.gleisbelegung.ui.window;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.ui.node.ButtonFactory;
import org.gleisbelegung.ui.node.LabelFactory;
import org.gleisbelegung.ui.style.Style;

public class MainWindow implements WindowInterface {
    private WindowController controller;
    private Button settings;
    private Button restart;
    private Button changeView;
    private Label nextRefresh;
    private Label gameTime;

    public MainWindow(Stage stage){
        controller = new WindowController(this, stage, "Gleisbelegung");
    }

    @Override public Scene init() {
        BorderPane bp = new BorderPane();
        Style.applyClass(bp, "dark_gray");


        settings = ButtonFactory.create("Einstellungen", 16);
        restart = ButtonFactory.create("Neustart", 16);
        changeView = ButtonFactory.create("Sichtwechsel", 16);

        nextRefresh = LabelFactory.create("Aktualiesierung in x Sekunden", 16);
        gameTime = LabelFactory.create("Spielzeit: hh:mm", 16);

        Pane pane = new Pane(restart, settings, changeView, nextRefresh, gameTime);

        bp.setCenter(pane);

        Scene scene = new Scene(bp);
        return scene;
    }

    @Override public void onResize(double width, double height) {
        restart.setTranslateX(width/2.0 - restart.getWidth()/2.0 - settings.getWidth());
        restart.setTranslateY(10);

        settings.setTranslateX(width/2.0 - settings.getWidth()/2.0);
        settings.setTranslateY(10);

        changeView.setTranslateX(width/2.0 - changeView.getWidth()/2.0 + settings.getWidth() + 15);
        changeView.setTranslateY(10);

        nextRefresh.setTranslateX(width - nextRefresh.getWidth()*1.1); //i dont know why i need to multiply with 1.1, but else the text is out of scope
        nextRefresh.setTranslateY(15);

        gameTime.setTranslateX(10);
        gameTime.setTranslateY(15);
    }

    @Override public void onMaximize() {

    }

    @Override public void onMinimize() {

    }

    @Override public void onClose() {

    }
}
