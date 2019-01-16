package org.gleisbelegung.database;

import org.gleisbelegung.annotations.Threadsafe;
import org.gleisbelegung.sts.Facility;
import org.gleisbelegung.sts.Plattform;
import org.gleisbelegung.sts.Schedule;
import org.gleisbelegung.sts.Train;
import org.gleisbelegung.sts.Trainlist;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Database implements StSDataInterface {

    private static Database instance;

    @Threadsafe
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }

        return instance;
    }

    private final Object monitor = new Object();

    private int simTime;
    private long realTime;
    private Facility facility = null;
    private Trainlist trainList;
    private List<Plattform> plattformList = new LinkedList<>();
    private Set<WeakReference<Schedule>> schedules = new HashSet<>();

    private Database() {

    }

    /**
     * @return seconds since midnight
     */
    @Threadsafe
    public int getSimTime() {
        int simTime;
        synchronized (monitor) {
            simTime = this.simTime;
        }
        return (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - realTime) + simTime;
    }

    /**
     * @param realTime unix timestamp (milliseconds since 1970-01-01 00:00:00 UTC)
     * @param simTime milliseconds since midnight
     * @return
     */
    @Threadsafe
    public void setSimTime(long realTime, long simTime) {
        synchronized (monitor) {
            this.realTime = realTime;
            this.simTime = (int) TimeUnit.MILLISECONDS.toSeconds(simTime);
        }
    }

    public void registerPlattform(Plattform plattform) {
        plattformList.add(plattform);
    }

    @Threadsafe
    public void registerSchedule(Schedule schedule) {
        synchronized (schedules) {
            schedules.add(new WeakReference<>(schedule));
        }
    }

    @Threadsafe
    public Iterator<Schedule> getScheduleIterator() {
        final Set<WeakReference<Schedule>> schedules;
        synchronized (this.schedules) {
            schedules = new HashSet<>(this.schedules);
        }

        return new Iterator<Schedule>() {

            private Schedule next;
            private Iterator<WeakReference<Schedule>> iter = schedules.iterator();

            @Override
            public boolean hasNext() {
                while (iter.hasNext()) {
                    WeakReference<Schedule> nextWeakCandidate = iter.next();
                    Schedule nextCandidate = nextWeakCandidate.get();
                    if (!nextWeakCandidate.isEnqueued() && nextCandidate != null) {
                        next = nextCandidate;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Schedule next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return next;
            }
        };
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * This method may be called from Trainlist only.
     *
     * @param trainList
     */
    @Override
    @Threadsafe
    public void setTrainList(Trainlist trainList) {
        synchronized (Trainlist.class) {
            this.trainList = trainList;
            Trainlist.class.notifyAll();
        }
    }

    @Threadsafe
    @Override
    public List<Train> getTrainList() {
        synchronized (Trainlist.class) {
            while(trainList == null) {
                try {
                    Trainlist.class.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return trainList.toList();
        }
    }
}
