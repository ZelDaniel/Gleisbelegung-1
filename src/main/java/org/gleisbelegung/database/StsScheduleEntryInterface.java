package org.gleisbelegung.database;

public interface StsScheduleEntryInterface {

    int getArrival();

    int getDepature();

    StsScheduleFlagsInterface getFlags();

    StsPlatformInterface getPlatformPlanned();
}
