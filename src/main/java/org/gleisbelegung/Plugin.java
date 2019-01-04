package org.gleisbelegung;

import javafx.application.Application;
import javafx.stage.Stage;
import org.gleisbelegung.ui.main.MainWindow;
import org.gleisbelegung.updater.DownloadUpdateWindow;
import org.gleisbelegung.updater.UpdateAvailableWindow;
import org.gleisbelegung.updater.Updater;


public class Plugin extends Application {

    String version = "0.0.1";

    @Override public void start(Stage primaryStage) {
        Updater updater = new Updater();

        if (updater.isNeverVersionAvailable() && updater.isUpdateable()) {
            if (!updater.isUpdater()) {
                UpdateAvailableWindow window =
                        new UpdateAvailableWindow(version, "0.0.2", updater);
            } else {
                DownloadUpdateWindow downloadUpdateWindow = new DownloadUpdateWindow(updater);
            }

        } else {
            startPlugin(primaryStage);
        }



    }

    public static void startPlugin(Stage stage) {
        MainWindow mainWindow = new MainWindow(stage);
    }
}
