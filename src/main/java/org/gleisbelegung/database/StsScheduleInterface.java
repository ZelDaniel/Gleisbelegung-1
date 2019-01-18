package org.gleisbelegung.database;


import java.util.Iterator;

public interface StsScheduleInterface  {

    <T extends StsScheduleEntryInterface> Iterator<T> iterator();

}
