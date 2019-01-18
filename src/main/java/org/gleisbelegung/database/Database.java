package org.gleisbelegung.database;

import org.gleisbelegung.annotations.Threadsafe;
import org.gleisbelegung.sts.Facility;
import org.gleisbelegung.sts.Trainlist;

import java.lang.ref.WeakReference;
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
        synchronized (Database.class) {
            if (instance == null) {
                instance = new Database();
            }
        }

        return instance;
    }

    private final Object monitor = new Object();

    private int simTime;
    private long realTime;
    private Facility facility = null;
    private Trainlist trainList;
    private List<StsPlatformInterface> platformList = new LinkedList<>();
    private Set<WeakReference<StsScheduleInterface>> schedules = new HashSet<>();

    private Database() {

    }

    /**
     * @return seconds since midnight
     */
    @Threadsafe
    @Override
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

    @Override
    public void registerPlatform(StsPlatformInterface platform) {
        platformList.add(platform);
    }

    @Threadsafe
    @Override
    public void registerSchedule(StsScheduleInterface schedule) {
        synchronized (schedules) {
            schedules.add(new WeakReference<>(schedule));
        }
    }

    @Threadsafe
    @Override
    public Iterator<StsScheduleInterface> getScheduleIterator() {
        final Set<WeakReference<StsScheduleInterface>> schedules;
        synchronized (this.schedules) {
            schedules = new HashSet<WeakReference<StsScheduleInterface>>(this.schedules);
        }

        return new Iterator<StsScheduleInterface>() {

            private StsScheduleInterface next;
            private Iterator<WeakReference<StsScheduleInterface>> iter = schedules.iterator();

            @Override
            public boolean hasNext() {
                while (iter.hasNext()) {
                    WeakReference<StsScheduleInterface> nextWeakCandidate = iter.next();
                    StsScheduleInterface nextCandidate = nextWeakCandidate.get();
                    if (!nextWeakCandidate.isEnqueued() && nextCandidate != null) {
                        try {
                            next = nextCandidate;
                            return true;
                        } catch (ClassCastException e) {
                            // not correct type
                        }
                    } else {
                        iter.remove();
                    }
                }
                return false;
            }

            @Override
            public StsScheduleInterface next() {
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
    @Threadsafe
    public void setTrainList(Trainlist trainList) {
        synchronized (Trainlist.class) {
            this.trainList = trainList;
            Trainlist.class.notifyAll();
        }
    }

    @Threadsafe
    @Override
    public List<? extends StsTrainInterface> getTrainList() {
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
