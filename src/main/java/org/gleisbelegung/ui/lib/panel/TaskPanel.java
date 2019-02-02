package org.gleisbelegung.ui.lib.panel;

import org.gleisbelegung.concurrent.IntervalTask;
import org.gleisbelegung.concurrent.IntervalTaskThread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * should be extended by a {@link Panel} if the Panel has tasks to accomplish. This class contains some useful methods
 */
public abstract class TaskPanel extends Panel implements TaskPanelInterface {
    private List<IntervalTask> tasks;

    public TaskPanel(){
        tasks = new ArrayList<>();
    }

    /**
     * saves the task and creates the according thread with the given parameters
     * @param threadName name of the thread
     * @param task which should be created
     */
    public void addTask(String threadName, IntervalTask task){
        tasks.add(task);
        new IntervalTaskThread(threadName, task).start();
    }

    /**
     * terminates and removes a task by given class
     * @param c the static class attribute of a class
     */
    public void removeTask(Class c){
        Iterator<IntervalTask> it = tasks.iterator();
        while(it.hasNext()){
            IntervalTask task = it.next();
            if(task.getClass() == c){
                task.stopExecution();
                it.remove();
            }
        }
    }

    /**
     * terminates and removes a task by object
     * @param task task to be stopped and removed
     */
    public void removeTask(IntervalTask task){
        task.stopExecution();
        tasks.remove(task);
    }

    /**
     *
     * @param c the static class attribute of a class
     * @return the searched task
     */
    public IntervalTask getTask(Class c){
        for (IntervalTask task : tasks){
            if(task.getClass() == c){
                return task;
            }
        }
        return null;
    }

    /**
     * is called by the {@link PanelController} when the surrounding window is closed
     * stops all tasks.
     * @param remove true if you want to removeTask the tasks from the list, false if you want to let them in
     */
    void stopAllTasks(boolean remove){
        Iterator<IntervalTask> it = tasks.iterator();
        while(it.hasNext()){
            IntervalTask task = it.next();
            task.stopExecution();

            if(remove){
                it.remove();
            }
        }
    }
}
