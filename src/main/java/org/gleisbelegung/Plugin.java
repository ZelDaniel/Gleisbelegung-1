package org.gleisbelegung;

import javafx.application.Application;
import javafx.stage.Stage;
import org.gleisbelegung.ui.main.MainWindow;


public class Plugin extends Application {

    @Override public void start(Stage primaryStage){
        MainWindow mainWindow = new MainWindow(primaryStage);
    }
}
