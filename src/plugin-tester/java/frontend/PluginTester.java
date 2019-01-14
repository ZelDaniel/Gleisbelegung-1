package frontend;

import data.Clock;
import data.Facility;
import data.Platform;
import data.Schedule.ScheduleEntry;
import data.ScheduleTemplate;
import data.Train;
import org.gleisbelegung.io.StsSocket;
import org.gleisbelegung.io.XmlSocket;
import org.gleisbelegung.xml.XML;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginTester {

    private final Flag flag;
    private final Console console;
    final List<Platform> platformList = new LinkedList<>();
    final Map<Integer, Train> trains = new HashMap<>();
    Facility facility;

    private PluginTester(final Flag flag) {
        this.flag = flag;
        this.console = new Console(this);
        this.console.setDaemon(true);
        this.console.setName("InputConsole");
    }

    private void setup(ServerSocket socket) {
        this.facility = new Facility(flag.getFacilityName(), flag.getSimbuild(), flag.getAid());
        this.console.setServerSocket(socket);
        this.console.start();
    }

    private void reset() {
        // setup plattforms
        Set<Platform>[] hbfList = new Set[]{ new HashSet<>(), new HashSet<>(), new HashSet() };
        platformList.clear();
         for (int i = 0; i < 4; ++i) {
            for (int part = 0; part < 2; ++part) {
                Platform p = new Platform(Integer.toString(i + 1) + (char) ('A' + part));
                hbfList[0].add(p);
                platformList.add(p);
            }
        }
        for (int i = 4; i < 10; ++i) {
            for (int part = 0; part < 4; ++part) {
                Platform p = new Platform(Integer.toString(i + 1) + (char) ('A' + part));
                hbfList[1].add(p);
                platformList.add(p);
            }
        }
        for (int i = 10; i < 14; ++i) {
            Platform p = new Platform(Integer.toString(i + 1));
            hbfList[2].add(p);
            platformList.add(p);
        }
        platformList.add(new Platform("stop"));
        for (Set<Platform> platforms : hbfList){
            platforms.forEach(new Consumer<Platform>() {

                @Override
                public void accept(Platform platform) {
                    platform.neighbours.addAll(platforms);
                    platform.neighbours.remove(platform);
                }
            });
        }

        ScheduleTemplate scheduleTemplate_S1 = new ScheduleTemplate() {
            @Override
            public void fillOut(Integer id, Train t) {
                t.schedule.createNewEntry(
                        "4A",
                        "4A",
                        String.format("%02d:%02d", 5 + (id.intValue() / 3), (id % 2 == 0 ? 17 : 3) + ((id.intValue() % 3) * 20)),
                        String.format("%02d:%02d", 5 + (id.intValue() / 3), (id % 2 == 0 ? 17 : 3) + ((id.intValue() % 3) * 20))
                );
            }
        };

        for (int i = 0; i < 50; ++i) {
            trains.put(i, new Train(i < 10 ? "S1" : (i < 15 ? "IRE" : ("RB")) + i));
        }
        for (int i = 1; i < 10; i += 2) {
            trains.get(i).verspaetung = "+2";
        }
        for (int i = 0; i < 50; ++i) {
            Train t = trains.get(i);
            t.verspaetung = i % 5 == 0 ? "-1" : "+3";
            if (i < 10) {
                scheduleTemplate_S1.fillOut(i, t);
                String h = t.nach;
                t.nach = t.von;
                t.von = h;
            } else if (i < 15) {
                if (i % 2 == 0) {
                    t.nach = "C-Teststätt";
                    t.plangleis = t.gleis = "7A";
                } else {
                    t.plangleis = t.gleis = "6B";
                    t.von = "C-Teststätt";
                }
            } else {
                int delay = 3 * (i - 16);
                t.verspaetung = String.format("%+d", delay);
                t.plangleis = "11";
                if (i % 6 == 0) {
                    t.gleis = "12";
                } else {
                    t.gleis = t.plangleis;
                }
            }
        }
    }

    public static void main(final String[] args) throws IOException {
        final Flag flag = Flag.parse(args);

        final PluginTester tester = new PluginTester(flag);

        try (final ServerSocket serverSocket = new ServerSocket()) {
            tester.setup(serverSocket);
            InetSocketAddress localAddress = new InetSocketAddress("localhost", StsSocket.PORT);
            serverSocket.bind(localAddress);
            System.out.println("Accepting connections at " + serverSocket.getLocalSocketAddress().toString());
            while (!serverSocket.isClosed()) {
                tester.reset();
                try (final Socket socket = serverSocket.accept()) {
                    tester.console.setSocket(socket);
                    System.out.println("Connected with " + socket.getRemoteSocketAddress());
                    final Clock clock = new Clock();

                    final Thread runner = new Thread() {

                        @Override
                        public void run() {
                            while(!socket.isClosed()) {
                                final long currentSimTime = TimeUnit.MILLISECONDS.toMinutes(clock.getSimTime());

                                // find schedule entry which will fire an event
                                for (final Map.Entry<Integer,Train> entry : tester.trains.entrySet()) {
                                    final Train t = entry.getValue();
                                    ScheduleEntry next = t.schedule.getNext();
                                    int delay = Integer.parseInt(t.verspaetung);
                                    if (next != null) {
                                        String scheduleTimeS;
                                        if (t.amgleis) {
                                            scheduleTimeS = next.ab;
                                        } else {
                                            scheduleTimeS = next.an;
                                        }
                                        Matcher m = Pattern.compile("([0-9]*):([0-9]*)").matcher(scheduleTimeS);
                                        m.matches();
                                        long scheduleTime = delay
                                                + TimeUnit.HOURS.toMinutes(Integer.parseInt(m.group(1)))
                                                + Integer.parseInt(m.group(2));
                                        if (scheduleTime < currentSimTime) {
                                            delay += currentSimTime - scheduleTime;
                                            t.verspaetung = String.format("%+d", delay);

                                            if (t.amgleis) {
                                                t.triggerDepature(entry.getKey(), tester.console);
                                                next.setVisited();
                                            } else {
                                                t.triggerArrival(entry.getKey(), tester.console, next.flags.indexOf('D') > 0);
                                                if (next.flags.indexOf('D') > 0) {
                                                    next.setVisited();
                                                }
                                            }
                                        }
                                    }
                                }
                                try {
                                    sleep((long) (Math.random() * TimeUnit.MINUTES.toMillis(2)));
                                } catch (InterruptedException e) {
                                    return;
                                }
                            }
                        }
                    };
                    runner.setDaemon(true);
                    runner.setName("runnerThread");
                    runner.start();

                    final XmlSocket xmlSocket = new XmlSocket(socket);
                    final Thread inputHandler = new Thread() {

                        @Override
                        public void run() {
                            while (!socket.isClosed()) {
                                try {
                                    final XML xml = xmlSocket.read();
                                    if (xml == null) {
                                        System.err.println("IN  (" + clock.getTimeString() + "):  NULL\nClosing socket " + socket + "\n");
                                        socket.close();
                                        return;
                                    }
                                    if (tester.flag.isEchoInput()) {
                                        System.out.println("IN  (" + clock.getTimeString() + "):  " + xml);
                                    }
                                    XML response;
                                    final Train t;
                                    switch (xml.getKey()) {
                                        case "register":
                                            response = XML.generateEmptyXML("status")
                                                            .set("code", "220")
                                                            .setData("Ok.");
                                            break;
                                        case "simzeit":
                                            response = clock.getTime(xml);
                                            break;
                                        case "anlageninfo":
                                            response = tester.facility.toXML();
                                            break;
                                        case "bahnsteigliste":
                                            response = Platform.toXML(tester.platformList);
                                            break;
                                        case "zugliste":
                                            response = Train.toXmlList(tester.trains.entrySet());
                                            break;
                                        case "zugdetails":
                                            t = tester.trains.get(Integer.valueOf(xml.get("zid")));
                                            if (t == null) {
                                                response = XML.generateEmptyXML("status")
                                                        .set("code", "402")
                                                        .setData(String.format("ZID %s unbekannt", xml.get("zid")));
                                            } else {
                                                response = t.toXml(Integer.valueOf(xml.get("zid")));
                                            }
                                            break;
                                        case "zugfahrplan":
                                            t = tester.trains.get(Integer.valueOf(xml.get("zid")));
                                            if (t == null) {
                                                response = XML.generateEmptyXML("status")
                                                        .set("code", "402")
                                                        .setData(String.format("ZID %s unbekannt", xml.get("zid")));
                                            } else {
                                                response = t.scheduleToXml(Integer.valueOf(xml.get("zid")));
                                            }
                                            break;
                                        case "ereignis":
                                            t = tester.trains.get(Integer.valueOf(xml.get("zid")));
                                            if (t == null) {
                                                response = XML.generateEmptyXML("status")
                                                        .set("code", "402")
                                                        .set("zid", xml.get("zid"))
                                                        .setData(String.format("ZID %s unbekannt", xml.get("zid")));
                                            } else {
                                                t.setRegisteredEvent(xml.get("art"));
                                                continue;
                                            }
                                            break;
                                        default:
                                            continue;

                                    }
                                    while (!tester.console.queue.offer(response));
                                } catch (Exception e) {
                                    return;
                                }
                            }
                        }
                    };
                    inputHandler.setDaemon(true);
                    inputHandler.start();
                    final Thread outputHandler = new Thread() {

                        @Override
                        public void run() {
                            XML greetings = XML.generateEmptyXML("status");
                            greetings.set("code", "300");
                            greetings = greetings.setData("STS Plugin Interface, bitte anmelden.");
                            try {
                                OutputStream out = socket.getOutputStream();
                                out.write(greetings.getBytes(Charset.defaultCharset()));
                                out.flush();
                                while (!socket.isClosed()) {
                                    try {
                                        XML xmlToSend = tester.console.queue.poll(1, TimeUnit.SECONDS);
                                        if (xmlToSend != null) {
                                            System.out.println("OUT (" + clock.getTimeString() + "):  " + xmlToSend);
                                            xmlSocket.write(xmlToSend);
                                        }
                                    } catch (Exception e) {
                                        return;
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    };
                    outputHandler.start();

                    outputHandler.join();
                    inputHandler.join();
                    runner.interrupt();
                    runner.join();
                } catch (Exception e) {
                    System.out.println("Socket closed");
                }
            }
        }
    }
}
