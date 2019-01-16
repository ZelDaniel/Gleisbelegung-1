package org.gleisbelegung.concurrent;

/**
 * Executes a task each start of defined interval.
 */
public class IntervalTaskThread extends Thread {
    protected final IntervalTaskInterface task;

    public IntervalTaskThread(String name, IntervalTaskInterface task) {
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
