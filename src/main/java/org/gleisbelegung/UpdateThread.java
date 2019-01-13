package org.gleisbelegung;

import org.gleisbelegung.database.Database;
import org.gleisbelegung.io.StsSocket;
import org.gleisbelegung.sts.Train;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

abstract class UpdateThread extends Thread {

    private static class TrainListUpdateTask extends UpdateThread {

        private TrainListUpdateTask(StsSocket socket, Plugin plugin, String name) {
            super(socket, plugin, name);
        }

        @Override
        public void doTask() throws  IOException {
                socket.requestTrainList();
        }

        @Override
        public long getInterval() {
            return plugin.getTrainListUpdateInterval();
        }
    }

    private static class ScheduleUpdateThread extends UpdateThread {

        private ScheduleUpdateThread(StsSocket socket, Plugin plugin, String name) {
            super(socket, plugin, name);
        }

        @Override
        public void doTask() throws  IOException {
          if (null == Database.getInstance().getTrainList()) {
              return;
          }
          for (Train train : Database.getInstance().getTrainList()) {
              socket.requestSchedule(train);
          }
        }

        @Override
        public long getInterval() {
            return plugin.getScheduleUpdateInterval();
        }
    }

    private static class SimTimeUpdateThread extends UpdateThread {

        private SimTimeUpdateThread(StsSocket socket, Plugin plugin, String name) {
            super(socket, plugin, name);
        }

        @Override
        public void doTask() throws  IOException {
            socket.requestSimtime();
        }

        @Override
        public long getInterval() {
            return TimeUnit.MINUTES.toMillis(15);
        }
    }

    private static class TrainDetailsUpdateThread extends UpdateThread {

        private TrainDetailsUpdateThread(StsSocket socket, Plugin plugin, String name) {
            super(socket, plugin, name);
        }

        @Override
        public void doTask() throws IOException {
            for (Train t : Database.getInstance().getTrainList()) {
                socket.requestDetails(t);
            }
        }

        @Override
        public long getInterval() {
            return plugin.getTrainDetailsUpdateInterval();
        }
    }

    public static UpdateThread createTrainListUpdateTask(StsSocket socket, Plugin plugin) {
        return new TrainListUpdateTask(socket, plugin, "TrainListUpdateThread");
    }

    public static UpdateThread createScheduleUpdateTask(StsSocket socket, Plugin plugin) {
        return new ScheduleUpdateThread(socket, plugin, "ScheduleUpdateThread");
    }

    public static UpdateThread createSimtimeUpdateTask(StsSocket socket, Plugin plugin) {
        return new SimTimeUpdateThread(socket, plugin, "SimTimeUpdateThread");
    }

    public static UpdateThread createTrainDetailsUpdateTask(StsSocket socket, Plugin plugin) {
        return new TrainDetailsUpdateThread(socket, plugin, "TrainDetailsUpdateThread");
    }

    protected final StsSocket socket;
    protected final Plugin plugin;

    public UpdateThread(StsSocket socket, Plugin plugin, String name) {
        this.socket = socket;
        this.plugin = plugin;
        setDaemon(true);
        setName(name);
    }

    @Override
    public void run() {
        while (!socket.isClosed() && !interrupted()) {
            long taskStartTime = System.currentTimeMillis();
            try {
                doTask();
                long taskEndTime = System.currentTimeMillis();
                long sleepTime = taskEndTime - taskStartTime + getInterval();
                if (sleepTime > 0) {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void doTask() throws IOException;

    protected abstract long getInterval();

}
