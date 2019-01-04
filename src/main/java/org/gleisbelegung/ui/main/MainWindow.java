package org.gleisbelegung.ui.main;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gleisbelegung.ui.lib.window.WindowController;
import org.gleisbelegung.ui.lib.window.WindowInterface;
import org.gleisbelegung.ui.main.InformationPanel;
import org.gleisbelegung.ui.main.StatusPanel;
import org.gleisbelegung.ui.main.TablePanel;
import org.gleisbelegung.ui.main.TrainInformationPanel;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;


public class MainWindow implements WindowInterface {

    private WindowController controller;

    private TablePanel table;
    private InformationPanel informations;
    private TrainInformationPanel trainInformation;
    private StatusPanel status;

    public MainWindow(Stage stage) {
        controller = new WindowController(this, stage);
        controller.init("Gleisbelegung", 1250, 700);
    }

    @Override public Scene init() {
        BorderPane bp = new BorderPane();
        NodeWrapper nodeWrapper = new NodeWrapper(bp);
        nodeWrapper.addStyle(new BackgroundColor("#303030"));

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
