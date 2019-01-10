package org.gleisbelegung;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.gleisbelegung.io.StsSocket;
import org.gleisbelegung.ui.launch.LaunchWindow;
import org.gleisbelegung.ui.main.MainWindow;

import java.util.concurrent.TimeUnit;


public class Plugin extends Application {

    public static final String PLUGIN_NAME = "Gleisbelegung";
    public static final String PLUGIN_TEXT = "";
    public static final String VERSION = "2.0.0-dev-0";
    public static final String AUTHOR = "manuel";
    public static final int PLUGIN_PROTOCOL = 1;

    /**
     * Timeout in milliseconds to connect to the plugin
     */
    public static final int CONNECT_TIMEOUT = 500;

    private MainWindow mainWindow = null;
    private LaunchWindow launchWindow;

    /**
     * Update interval of train list in milliseconds
     */
    private long trainListUpdateInterval = TimeUnit.MINUTES.toMillis(2);

    /**
     * Update interval of schedule in milliseconds
     */
    private long scheduleUpdateInterval = TimeUnit.SECONDS.toMillis(30);

    @Override
    public void start(Stage primaryStage) {
        launchWindow = new LaunchWindow(this);
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
                //Is possible, if the initialization of the Date is not finished and a fatal error occurs
                if(mainWindow != null){
                    mainWindow.onFatalError("Verbindung zum StS verloren");
                }

                Platform.exit();
            }
        });
    }

    /**
     * Informs the plugin that a connection has been established successfully.
     *
     * The regular update of the train list and schedules will be initiated.
     */
    void connectionEstablished(StsSocket socket) {
        UpdateThread.createSimtimeUpdateTask(socket, this).start();
        UpdateThread.createTrainListUpdateTask(socket, this).start();
        UpdateThread.createScheduleUpdateTask(socket, this).start();

        launchWindow.connectionEstablished();
    }

    /**
     * Informs the ui that the simtime, list of plattforms and the info of the facility are present
     */
    void initializationCompleted() {
        Platform.runLater(() -> {
            launchWindow.initializationCompleted();
            mainWindow = new MainWindow(new Stage());
        });
    }

    /**
     * @return Update interval in milliseconds
     */
    long getTrainListUpdateInterval() {
        return trainListUpdateInterval;
    }

    /**
     * @return Update interval in milliseconds
     */
    long getScheduleUpdateInterval() {
        return scheduleUpdateInterval;
    }

    public void tryConnection(String host){
        Thread xmlInputHandlerThread = new XmlInputHandlerThread(this);
        xmlInputHandlerThread.setName("PluginApplicationThread");
        xmlInputHandlerThread.start();
    }
}
