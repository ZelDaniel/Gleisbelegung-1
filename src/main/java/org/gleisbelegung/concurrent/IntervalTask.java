package org.gleisbelegung.concurrent;

public interface IntervalTask extends Runnable {

    /**
     *
     * @return The desired time between to starts of this task in milliseconds
     */
    long getInterval();

    boolean continueExecution();
}
