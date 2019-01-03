package frontend;

import data.Clock;
import data.Facility;
import data.Plattform;
import org.gleisbelegung.io.StsSocket;
import org.gleisbelegung.io.XmlSocket;
import org.gleisbelegung.xml.XML;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

public class PluginTester {

    private final Flag flag;
    private final Console console = new Console();
    private final List<Plattform> plattformList = new LinkedList<>();

    private Facility facility;

    private PluginTester(final Flag flag) {
        this.flag = flag;
        this.console.setDaemon(true);
        this.console.setName("InputConsole");
    }

    private void setup(ServerSocket socket) {
        this.facility = new Facility(flag.getFacilityName(), flag.getSimbuild(), flag.getAid());
        this.console.setServerSocket(socket);
        Set<Plattform> hbfList = new HashSet<>();
        for (int i = 0; i < 10; ++i) {
            Plattform p = new Plattform(Integer.toString(i + 1));
            hbfList.add(p);
            plattformList.add(p);
        }
        for (Plattform p : hbfList) {
            p.neighbours.addAll(hbfList);
            p.neighbours.remove(p);
        }

        this.console.start();
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
                try (final Socket socket = serverSocket.accept()) {
                    tester.console.setSocket(socket);
                    System.out.println("Connected with " + socket.getRemoteSocketAddress());

                    final Clock clock = new Clock();
                    final XmlSocket xmlSocket = new XmlSocket(socket);
                    final Thread inputHandler = new Thread() {

                        @Override
                        public void run() {
                            while (!socket.isClosed()) {
                                try {
                                    final XML xml = xmlSocket.read();
                                    if (xml == null) {
                                        System.err.println("> NULL\nClosing socket " + socket + "\n");
                                        socket.close();
                                        return;
                                    }
                                    if (tester.flag.isEchoInput()) {
                                        System.out.println("> " + xml);
                                    }
                                    switch (xml.getKey()) {
                                        case "register":
                                            xmlSocket.write(
                                                    XML.generateEmptyXML("status")
                                                            .set("code", "220")
                                                            .setData("Ok.")
                                            );
                                            break;
                                        case "simzeit":
                                            xmlSocket.write(clock.getTime(xml));
                                            break;
                                        case "anlageninfo":
                                            xmlSocket.write(tester.facility.toXML());
                                            break;
                                        case "bahnsteigliste":
                                            xmlSocket.write(Plattform.toXML(tester.plattformList));
                                            break;

                                    }
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
                                        // TODO pass input injected by running console
                                        sleep(2000);
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
                } catch (Exception e) {
                    System.out.println("Socket closed");
                }
            }
        }
    }
}
