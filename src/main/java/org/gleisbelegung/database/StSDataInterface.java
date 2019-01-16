package org.gleisbelegung.database;

import org.gleisbelegung.sts.Train;
import org.gleisbelegung.sts.Trainlist;

import java.util.List;

public interface StSDataInterface {

    void setTrainList(Trainlist trainList);

    List<Train> getTrainList();
}
