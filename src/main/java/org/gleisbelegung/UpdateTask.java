package org.gleisbelegung;

import org.gleisbelegung.concurrent.IntervalTaskInterface;
import org.gleisbelegung.concurrent.IntervalTaskThread;
import org.gleisbelegung.database.Database;
import org.gleisbelegung.io.StsSocket;
import org.gleisbelegung.sts.Train;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

abstract class UpdateTask implements IntervalTaskInterface {

    private static class TrainListUpdateTask extends UpdateTask {

        private TrainListUpdateTask(StsSocket socket, Plugin plugin) {
            super(socket, plugin);
        }

        @Override
        public void run() {
            try {
                socket.requestTrainList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public long getIntervalInMillis() {
            return plugin.getTrainListUpdateInterval();
        }
    }

    private static class ScheduleUpdateTask extends UpdateTask {

        private ScheduleUpdateTask(StsSocket socket, Plugin plugin) {
            super(socket, plugin);
        }

        @Override
        public void run() {
          if (null == Database.getInstance().getTrainList()) {
              return;
          }
          try {
              for (Train train : Database.getInstance().getTrainList()) {
                  socket.requestSchedule(train);
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
        }

        @Override
        public long getIntervalInMillis() {
            return plugin.getScheduleUpdateInterval();
        }
    }

    private static class SimTimeUpdateTask extends UpdateTask {

        private SimTimeUpdateTask(StsSocket socket, Plugin plugin) {
            super(socket, plugin);
        }

        @Override
        public void run() {
            try {
                socket.requestSimtime();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public long getIntervalInMillis() {
            return TimeUnit.MINUTES.toMillis(15);
        }
    }

    private static class TrainDetailsUpdateTask extends UpdateTask {

        private TrainDetailsUpdateTask(StsSocket socket, Plugin plugin) {
            super(socket, plugin);
        }

        @Override
        public void run() {
            try {
                for (Train t : Database.getInstance().getTrainList()) {
                    socket.requestDetails(t);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public long getIntervalInMillis() {
            return plugin.getTrainDetailsUpdateInterval();
        }
    }

    public static IntervalTaskThread createTrainListUpdateTask(StsSocket socket, Plugin plugin) {
        return new IntervalTaskThread("TrainListUpdateThread", new TrainListUpdateTask(socket, plugin));
    }

    public static IntervalTaskThread createScheduleUpdateTask(StsSocket socket, Plugin plugin) {
        return new IntervalTaskThread("ScheduleUpdateTask", new ScheduleUpdateTask(socket, plugin));
    }

    public static IntervalTaskThread createSimtimeUpdateTask(StsSocket socket, Plugin plugin) {
        return new IntervalTaskThread("SimTimeUpdateTask", new SimTimeUpdateTask(socket, plugin));
    }

    public static IntervalTaskThread createTrainDetailsUpdateTask(StsSocket socket, Plugin plugin) {
        return new IntervalTaskThread("TrainDetailsUpdateTask", new TrainDetailsUpdateTask(socket, plugin));
    }

    protected final Plugin plugin;
    protected final StsSocket socket;

    protected UpdateTask(StsSocket socket, Plugin plugin) {
        this.plugin = plugin;
        this.socket = socket;
    }

    @Override
    public boolean continueExecution() {
        return !socket.isClosed();
    }
}
