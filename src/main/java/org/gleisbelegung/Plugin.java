package org.gleisbelegung;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.gleisbelegung.ui.window.PluginWindow;


public class Plugin extends Application {

    public static final String PLUGIN_NAME = "Gleisbelegung";
    public static final String PLUGIN_TEXT = "";
    public static final String VERSION = "0.0.1-alpha";
    public static final String AUTHOR = "manuel";
    public static final int PLUGIN_PROTOCOL = 1;

    /**
     * Timeout in milliseconds to connect to the plugin
     */
    public static final int CONNECT_TIMEOUT = 500;

    private PluginWindow pluginWindow = null;

    @Override
    public void start(Stage primaryStage) {
        pluginWindow = new PluginWindow(primaryStage);

        // TODO pass socket address from pluginWindow
        Thread xmlInputHandlerThread = new XmlInputHandlerThread(this);
        xmlInputHandlerThread.setName("PluginApplicationThread");
        xmlInputHandlerThread.start();
    }

    /**
     * Closes the displays of the plugin.
     *
     * This method will cause the program to exit normally.
     */
    void tearDown() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                pluginWindow.onFatalError("Verbindung zum StS verloren");

                Platform.exit();
            }
        });
    }

    /**
     * Informs the ui that a connection has been established successfully
     */
    void connectionEstablished() {
        // TODO inform pluginWindow that the connection has been establised
    }

    /**
     * Informs the ui that the simtime, list of plattforms and the info of the facility are present
     */
    void initializationCompleted() {
        // TODO
    }
}
