package org.gleisbelegung;

import org.gleisbelegung.database.Database;
import org.gleisbelegung.io.StsSocket;
import org.gleisbelegung.sts.Details;
import org.gleisbelegung.sts.Event;
import org.gleisbelegung.sts.Facility;
import org.gleisbelegung.sts.Plattform;
import org.gleisbelegung.sts.Schedule;
import org.gleisbelegung.sts.Train;
import org.gleisbelegung.sts.Trainlist;
import org.gleisbelegung.xml.XML;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.gleisbelegung.Plugin.*;

class XmlInputHandlerThread extends Thread {

    private final Plugin plugin;
    private final String host;
    private StsSocket stSSocket;

    private boolean facilityPresent = false;
    private boolean simtimePresent = false;
    private boolean plattformsPresent = false;
    private Trainlist trainlist;

    enum ExitReason {
        SOCKET_CLOSED, ERROR
    }

    public XmlInputHandlerThread(Plugin plugin, String host) {
        this.plugin = plugin;
        this.host = host;
    }

    private final Pattern zidUnbekannt = Pattern.compile("ZID ([0-9]*) unbekannt");

    private void handleStatus(XML xml) throws IOException {
        int status = Integer.parseInt(xml.get("code"));
        switch (status) {
            case 300:
                stSSocket.write(XML.generateEmptyXML("register")
                        .set("name", PLUGIN_NAME)
                        .set("autor", AUTHOR)
                        .set("version", VERSION)
                        .set("protokoll", Integer.toString(PLUGIN_PROTOCOL))
                        .set("text", PLUGIN_TEXT));
                break;
            case 402:
                Matcher matcher = zidUnbekannt.matcher(xml.getData());
                matcher.matches();
                String zid = matcher.group(1);
                trainlist.remove(Integer.parseInt(zid));
                break;
            case 450:
                // TODO XML is not correct
                break;
            case 220:
                // handshake completed
                plugin.connectionEstablished(stSSocket);
                stSSocket.requestFacilityInfo();
                stSSocket.requestPlattformList();
                break;
        }
    }

    private void handleSimtime(long now, XML xml) {
        long rtt = (now - Long.parseLong(xml.get("sender"))) / 2;
        long realtime = Long.parseLong(xml.get("sender")) + rtt;
        System.err.println("rtt: " + rtt);
        // TODO log rtt as indicator for latency?
        Database.getInstance().setSimTime(realtime, Long.parseLong(xml.get("zeit")));
        this.simtimePresent = true;
    }

    private void handleFacility(XML xml) {
        Database.getInstance().setFacility(Facility.parse(xml));
        this.facilityPresent = true;
    }

    private void handlePlattformList(XML xml) {
        for(XML xmlEntry : xml.getInternXML()) {
            Database.getInstance().registerPlattform(Plattform.parse(xmlEntry));
        }
        this.plattformsPresent = true;
    }

    private void handleDetails(XML xml) throws  IOException {
        Train t = getTrain(xml);
        if (t != null) {
            Details details = Details.parse(xml);
            if (t.getDetails() == null) {
                t.setPosition(details);
            } else {
                t.updateByDetails(details);
            }
        }
    }

    private void handleSchedule(XML xml) throws IOException {
        Train t = getTrain(xml);
        if (t != null && t.getSchedule() == null) {
            t.setSchedule(Schedule.parse(xml, t, trainlist, null));
            for (Event.EventType eventType : Event.EventType.values()) {
                stSSocket.registerEvent(eventType, t);
            }
        } else {
            // TODO
            // t.updateSchedule
        }
    }

    private void handleTrainList(XML xml) throws IOException {
        boolean initTrainlist = false;
        if (trainlist == null) {
            initTrainlist = true;
            trainlist = new Trainlist();
        }
        trainlist.update(xml);
        for (Train train : trainlist.toList()) {
            if (train.getDetails() == null) {
                stSSocket.requestDetails(train);
            }
            if (train.getSchedule() == null) {
                stSSocket.requestSchedule(train);
            }
        }
        if (initTrainlist) {
            Database.getInstance().setTrainList(trainlist);
        }
    }

    private void handleEvent(XML xml) throws IOException {
        Train t = getTrain(xml);
        t.updateByEvent(Event.parse(xml, t));
    }

    private Train getTrain(XML xml) {
        return trainlist.get(xml.get("zid"));
    }

    private boolean tryConnect(Socket socket) throws IOException {
        socket.bind(null);
        try {
            socket.connect(new InetSocketAddress(host, StsSocket.PORT), CONNECT_TIMEOUT);
        } catch (SocketException e) {
            return false;
        }

        return true;
    }

    private ExitReason handleInput(Socket socket) {
        stSSocket = new StsSocket(socket);
        boolean complete = false;

        while(!socket.isClosed()) {
            try {
                final XML readXml = stSSocket.read();
                if (readXml == null) {
                    System.err.println("Closing socket");
                    socket.close();
                    continue;
                }
                switch (readXml.getKey()) {
                    case "status":
                        handleStatus(readXml);
                        break;
                    case "simzeit":
                        long now = System.currentTimeMillis();
                        handleSimtime(now, readXml);
                        break;
                    case "anlageninfo":
                        handleFacility(readXml);
                        break;
                    case "bahnsteigliste":
                        handlePlattformList(readXml);
                        break;
                    case "zugdetails":
                        handleDetails(readXml);
                        break;
                    case "zugfahrplan":
                        handleSchedule(readXml);
                        break;
                    case "zugliste":
                        handleTrainList(readXml);
                        break;
                    case "ereignis":
                        handleEvent(readXml);
                        System.out.println(readXml.toString());
                        break;
                }
                if (!complete && plattformsPresent && simtimePresent && facilityPresent) {
                    complete = true;
                    plugin.initializationCompleted(stSSocket);
                }
            } catch (Exception e) {
                if (socket.isClosed()) {
                } else {
                    e.printStackTrace();// TODO error handling
                    return ExitReason.ERROR;
                }
            }
        }
        // TODO logging

        return ExitReason.SOCKET_CLOSED;
    }

    @Override
    public void run() {
        while (true) {
            try (Socket socket = new Socket()) {
                if (tryConnect(socket)) {
                    handleInput(socket);
                    plugin.tearDown();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
