package org.gleisbelegung.tasks.main;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.gleisbelegung.concurrent.IntervalTask;
import org.gleisbelegung.database.Database;
import org.gleisbelegung.ui.lib.style.NodeWrapper;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class UpdateGameTimeTask implements IntervalTask {

    private final NodeWrapper<Label> gameTime;

    public UpdateGameTimeTask(NodeWrapper<Label> gameTime) {
        this.gameTime = gameTime;
    }

    @Override public long getIntervalInMillis() {
        return TimeUnit.SECONDS.toMillis(1);
    }

    @Override public boolean continueExecution() {
        return true;
    }

    @Override public void run() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        Database db = Database.getInstance();

        String time = format.format(TimeUnit.SECONDS.toMillis(db.getSimTime()));
        Platform.runLater(() -> {
            gameTime.getNode().setText("Simzeit: " + time);
        });
    }
}
