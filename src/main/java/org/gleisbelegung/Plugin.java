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
    Thread xmlInputHandlerThread = null;

    /**
     * Update interval of train list in milliseconds
     */
    private long trainListUpdateInterval = TimeUnit.MINUTES.toMillis(2);

    /**
     * Update interval of schedule in milliseconds
     */
    private long scheduleUpdateInterval = TimeUnit.SECONDS.toMillis(30);

    /**
     * Update interval of schedule in milliseconds
     */
    private long trainDetailsUpdateInterval = TimeUnit.MINUTES.toMillis(5);

    @Override public void start(Stage primaryStage) {
        launchWindow = new LaunchWindow(this);
    }

    /**
     * Closes the displays of the plugin.
     * <p>
     * This method will cause the program to exit normally.
     */
    void tearDown() {
        Platform.runLater(new Runnable() {

            @Override public void run() {
                //Is possible, if the initialization of the Date is not finished and a fatal error occurs
                if (mainWindow != null) {
                    mainWindow.onFatalError("Verbindung zum StS verloren");
                }

                Platform.exit();
            }
        });
    }

    /**
     * Informs the plugin that a connection has been established successfully.
     * <p>
     * The regular update of the train list and schedules will be initiated.
     */
    void connectionEstablished(StsSocket socket) {
        UpdateTask.createSimtimeUpdateTask(socket, this).start();
        UpdateTask.createTrainListUpdateTask(socket, this).start();
        UpdateTask.createScheduleUpdateTask(socket, this).start();

        launchWindow.connectionEstablished();
    }

    /**
     * Informs the ui that the simtime, list of plattforms and the info of the facility are present
     */
    void initializationCompleted(StsSocket socket) {
        Platform.runLater(() -> {
            launchWindow.initializationCompleted();
            mainWindow = new MainWindow(new Stage(), this);
        });
        UpdateTask.createTrainDetailsUpdateTask(socket, this).start();
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

    /**
     * @return Update interval in milliseconds
     */
    long getTrainDetailsUpdateInterval() {
        return trainDetailsUpdateInterval;
    }

    /**
     * called by {@link LaunchWindow} when the user clicks on connect
     *
     * @param host address of the input field
     */
    public void tryConnection(String host) {
        xmlInputHandlerThread = new XmlInputHandlerThread(this, host);
        xmlInputHandlerThread.setName("PluginApplicationThread");
        xmlInputHandlerThread.start();
    }

    /**
     * called if the plugin was unable to connect to the simulation
     */
    public void connectionFailed() {
        launchWindow.connectionFailed();
    }

    /**
     * called if {@link MainWindow} is closed and the socket should be closed now
     * @return success
     */
    public boolean closeConnection() {
        if (xmlInputHandlerThread != null) {
            return ((XmlInputHandlerThread) xmlInputHandlerThread)
                    .closeConnection();
        } else {
            return false;
        }
    }
}
