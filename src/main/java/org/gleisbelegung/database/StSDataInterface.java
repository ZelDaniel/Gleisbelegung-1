package org.gleisbelegung.database;


import java.util.Iterator;
import java.util.List;

public interface StSDataInterface {

    <T extends StsScheduleEntryInterface> Iterator<T> getScheduleIterator();

    int getSimTime();

    void registerPlatform(StsPlatformInterface platform);

    void registerSchedule(StsScheduleInterface schedule);

    <T extends StsTrainInterface> List<T> getTrainList();
}
