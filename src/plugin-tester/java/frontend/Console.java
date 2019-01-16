package frontend;

import data.Train;
import org.gleisbelegung.xml.XML;

import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Console extends Thread {

    private final Thread creatorThread = Thread.currentThread();
    private ServerSocket serverSocket;
    private Socket socket;
    private final PluginTester tester;

    final BlockingQueue<XML> queue = new LinkedBlockingDeque<>();

    public Console(PluginTester tester) {
        this.tester = tester;
    }

    @Override
    public void run() {
        int offset = 0;
        char[] buffer = new char[100];
        try (InputStreamReader reader = new InputStreamReader(System.in)) {
            while (true) {
                if (offset == 0 || System.in.available() > 0) {
                    int read = reader.read(buffer, offset, buffer.length - offset);
                    if (read < 0) {
                        return;
                    }
                    offset += read;
                }
                String currentInput = new String(buffer, 0, offset - 1);
                int endOfLine = currentInput.indexOf('\n');
                if (endOfLine < 0) {
                    endOfLine = currentInput.length();
                }
                final String input = currentInput.substring(0, endOfLine).trim();
                offset -= endOfLine + 1;

                handleInput(input);
            }
        } catch (IOException e) {
        }
    }

    void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void printHelp() {
        System.out.println("Supported commands\n");
        System.out.printf("%-30s: %s\n", "help", "Prints this message");
        System.out.printf("%-30s: %s\n", "quit", "Closes the socket and terminates the tester.");
        System.out.printf("%-30s: %s\n", "delay <train id>", "Adjust the delay of a train.");
        System.out.printf("%-30s: %s\n", "trainlist", "Sends the trainlist (<zugliste>).");
        System.out.printf("%-30s: %s\n", "remove train <train id>", "Removes a train.");
        System.out.printf("%-30s: %s\n", "close", "Closes the socket to the current connected plugin.");
    }

    private void handleInput(final String input) throws IOException {
        String[] tokens = input.toLowerCase().split("\t+|\r+| +");
        for (int i = 0; i < tokens.length; ++i) {
            String token = tokens[i];
            try {
                switch (token) {
                    case "help":
                        printHelp();
                        return;
                    case "quit":
                        System.in.close();
                        this.serverSocket.close();
                        creatorThread.interrupt();
                        return;
                    case "delay":
                        Integer zid, delay;
                        try {
                            zid = Integer.parseInt(tokens[++i]);
                            delay = Integer.parseInt(tokens[++i]);
                            Train t = tester.trains.get(zid);
                            if (t == null) {
                                System.err.println("E: No such train");
                            } else {
                                t.verspaetung = delay.toString();
                                System.err.printf("Train %d has no a delay of %+d\n", zid.intValue(), delay.intValue());
                            }
                        } catch (NumberFormatException e) {
                        }
                        break;
                    case "trainlist":
                        if (!queue.add(Train.toXmlList(tester.trains.entrySet()))) {
                            System.err.println("Failed to add train list into sending queue");
                        } else {
                            System.err.println("Successfully added train list into sending queue");
                        }
                        break;
                    case "remove":
                        token = tokens[++i];
                        switch (token) {
                            case "train":
                                try {
                                    zid = Integer.parseInt(tokens[++i]);
                                    System.err.printf(null == tester.trains.remove(zid) ? "Train %d does not exists\n" : "Train %d removed successfully\n", zid.intValue());
                                } catch (NumberFormatException e) {

                                }
                                break;
                            // TODO add more remove commands here
                            default:
                                System.out.println("unknown [remove] command");
                        }
                        break;
                    case "close":
                        this.socket.close();
                        break;
                    // TODO add more commands here
                    default:
                        System.out.println("unknown command");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    void setSocket(Socket s) {
        this.socket = s;
        queue.clear();
    }

    public void queueXml(XML xml) {
        this.queue.add(xml);
    }
}
