package org.gleisbelegung.ui.window;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.ui.panel.plugin.InformationPanel;
import org.gleisbelegung.ui.panel.plugin.StatusPanel;
import org.gleisbelegung.ui.panel.plugin.TablePanel;
import org.gleisbelegung.ui.panel.plugin.TrainInformationPanel;
import org.gleisbelegung.ui.style.Style;


public class PluginWindow implements WindowInterface {

    private WindowController controller;

    private TablePanel table;
    private InformationPanel informations;
    private TrainInformationPanel trainInformation;
    private StatusPanel status;

    public PluginWindow(Stage stage) {
        controller = new WindowController(this, stage);
        controller.init("Gleisbelegung", 1250, 700);
    }

    @Override public Scene init() {
        BorderPane bp = new BorderPane();
        Style.applyClass(bp, "dark_gray");

        informations = new InformationPanel();
        Pane informationPane = controller.addPanel(informations);
        bp.setTop(informationPane);

        table = new TablePanel();
        Pane tablePanel = controller.addPanel(table);
        bp.setCenter(tablePanel);


        trainInformation = new TrainInformationPanel();
        Pane trainPanel = controller.addPanel(trainInformation);
        bp.setRight(trainPanel);

        status = new StatusPanel();
        Pane statusPanel = controller.addPanel(status);
        bp.setBottom(statusPanel);

        Scene scene = new Scene(bp);
        return scene;
    }

    @Override public void onResize(double width, double height) {

    }

    @Override public void onMaximize() {

    }

    @Override public void onMinimize() {

    }

    @Override public void onClose() {

    }
}
