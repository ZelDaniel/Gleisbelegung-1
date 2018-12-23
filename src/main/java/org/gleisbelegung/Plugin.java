package org.gleisbelegung;

import javafx.application.Application;
import javafx.stage.Stage;
import org.gleisbelegung.ui.window.PluginWindow;


public class Plugin extends Application {

    @Override public void start(Stage primaryStage){
        PluginWindow pluginWindow = new PluginWindow(primaryStage);
    }
}
