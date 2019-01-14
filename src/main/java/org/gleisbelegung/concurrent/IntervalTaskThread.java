package org.gleisbelegung.concurrent;

import org.gleisbelegung.Plugin;

/**
 * Executes a task each start of defined interval.
 */
public class IntervalTaskThread extends Thread {
    protected final IntervalTask task;

    public IntervalTaskThread(String name, IntervalTask task) {
        this.task = task;
        setDaemon(true);
        setName(name);
    }

    @Override
    public void run() {
        while (!interrupted() && task.continueExecution()) {
            long taskStartTime = System.currentTimeMillis();
            try {
                task.run();
                long taskEndTime = System.currentTimeMillis();
                long sleepTime = taskEndTime - taskStartTime + task.getIntervalInMillis();
                if (sleepTime > 0) {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
