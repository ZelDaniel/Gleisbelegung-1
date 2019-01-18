package org.gleisbelegung.io;

import java.io.IOException;
import java.net.Socket;

import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.sts.Event;
import org.gleisbelegung.sts.Train;
import org.gleisbelegung.xml.XML;

public class StsSocket extends XmlSocket {

    public final static int PORT = 3691;

    private final static XML simtimeXml = XML.generateEmptyXML("simzeit");
    private final static XML trainListXML = XML.generateEmptyXML("zugliste");
    private final static XML plattformListXml = XML.generateEmptyXML("bahnsteigliste");
    private final static XML facitlityInfoXml = XML.generateEmptyXML("anlageninfo");
    private final static XML detailsXml = XML.generateEmptyXML("zugdetails");
    private final static XML scheduleXml = XML.generateEmptyXML("zugfahrplan");
    private final static XML eventXml = XML.generateEmptyXML("ereignis");

    public StsSocket(Socket socket) {
        super(socket);
    }

    public void requestFacilityInfo() throws IOException {
        write(facitlityInfoXml);
    }

    public void requestPlattformList() throws IOException {
        write(plattformListXml);
    }

    public void requestSimtime() throws IOException {
        write(simtimeXml.set("sender", Long.toString(System.currentTimeMillis())));
    }
    public void requestTrainList() throws IOException {
        write(trainListXML);
    }

    public void requestDetails(StsTrainInterface train) throws IOException {
        write(detailsXml.set("zid", train.getId().toString()));
    }

    public void requestSchedule(StsTrainInterface train) throws IOException {
        write(scheduleXml.set("zid", train.getId().toString()));
    }

    public void registerEvent(Event.EventType eventType, StsTrainInterface train) throws IOException {
        if (eventType.getKey() != null
            && (train.getId() >= 0 || eventType == Event.EventType.EXIT)
        ) {
            write(eventXml.set("art", eventType.getKey()).set("zid", train.getId().toString()));
        }
    }
}
