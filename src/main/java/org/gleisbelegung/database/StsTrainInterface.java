package org.gleisbelegung.database;

public interface StsTrainInterface {

    Integer getId();

    <T extends StsTrainDetailsInterface> T getDetails();

    <T extends StsScheduleInterface> T getSchedule();

    <T extends StsTrainInterface> void setPredecessor(T predecessor);

    <T extends StsTrainInterface> void setSuccessor(T successor);
}
