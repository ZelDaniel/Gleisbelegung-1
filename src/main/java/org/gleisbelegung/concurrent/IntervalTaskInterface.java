package org.gleisbelegung.concurrent;

public interface IntervalTaskInterface extends Runnable {

    /**
     *
     * @return The desired time between to starts of this task in milliseconds
     */
    long getIntervalInMillis();

    /**
     *
     * @return true if you want to continue execution, false if you want to stop it
     */
    boolean continueExecution();
}
