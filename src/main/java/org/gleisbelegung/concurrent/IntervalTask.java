package org.gleisbelegung.concurrent;

/**
 * implemented to be able to stop the task in a situation which is not handled by the user
 */
public abstract class IntervalTask implements IntervalTaskInterface {
    private boolean execute;

    public IntervalTask(boolean execute){
        this.execute = execute;
    }

    @Override public final boolean continueExecution() {
        return execute;
    }

    /**
     * i.e. called by the {@link org.gleisbelegung.ui.lib.panel.TaskPanel TaskPanel} when the corresponding window is closed
     */
    public void stopExecution(){
        execute = false;
    }

    /**
     * not implemented at the moment because the threads end after the corresponding task is closed
     */
    /*public void startExecution(){
        execute = true;
    }
    */
}
