package org.gleisbelegung.database;

public interface StsTrainInterface {

    Integer getId();

    StsTrainDetailsInterface getDetails();

    StsScheduleInterface getSchedule();

    <T extends StsTrainInterface> void setPredecessor(T predecessor);

    <T extends StsTrainInterface> void setSuccessor(T successor);
}
