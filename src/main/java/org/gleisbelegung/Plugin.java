package org.gleisbelegung;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Application;
import javafx.stage.Stage;

import org.gleisbelegung.io.StsSocket;
import org.gleisbelegung.xml.XML;
import org.gleisbelegung.ui.window.PluginWindow;


public class Plugin extends Application {

    public static final String PLUGIN_NAME = "Gleisbelegung";
    public static final String PLUGIN_TEXT = "";
    public static final String VERSION = "0.0.1-alpha";
    public static final String AUTHOR = "manuel";
    public static final int PLUGIN_PROTOCOL = 1;

    /**
     * Timeout in milliseconds to connect to the plugin
     */
    public static final int CONNECT_TIMEOUT = 500;

    @Override
    public void start(Stage primaryStage) {
        PluginWindow pluginWindow = new PluginWindow(primaryStage);

        // TODO pass socket address from pluginWindow
        Thread main = new Thread() {

            private StsSocket stSSocket;

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
                        // TODO train with given ID unknown
                        break;
                    case 450:
                        // TODO XML is not correct
                        break;
                    case 220:
                        // TODO handshake done
                        stSSocket.write(XML.generateEmptyXML("anlageninfo"));
                        stSSocket.write(XML.generateEmptyXML("simzeit"));
                        stSSocket.write(XML.generateEmptyXML("bahnsteigliste"));
                        stSSocket.write(XML.generateEmptyXML("zugliste"));
                        break;
                }
            }

            private boolean tryConnect(Socket socket) throws IOException {
                socket.bind(null);
                try {
                    socket.connect(new InetSocketAddress("localhost", StsSocket.PORT), CONNECT_TIMEOUT);
                } catch (SocketException e) {
                    return false;
                }

                return true;
            }

            private void handleInput(Socket socket) {
                stSSocket = new StsSocket(socket);

                while(true) {
                    try {
                        final XML readXml = stSSocket.read();
                        if (readXml == null) {
                            socket.close();
                            return;
                        }
                        switch (readXml.getKey()) {
                            case "status":
                                handleStatus(readXml);
                        }
                    } catch (Exception e) {
                        if (socket.isClosed()) {
                            System.out.println("Socket closed");
                            return;
                        }
                        e.printStackTrace();// TODO error handling
                    }
                }
            }

            @Override
            public void run() {
                while (true) {
                    try (Socket socket = new Socket()) {
                        if (tryConnect(socket)) {
                            handleInput(socket);
                            tearDown();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void tearDown() {
                // TODO
                Thread[] threads = new Thread[Thread.activeCount()];
                Thread.enumerate(threads);
                for (Thread thread : threads) {
                    if (thread != this && !thread.isDaemon()) {
                        thread.interrupt();
                    }
                }
                for (Thread thread : threads) {
                    if (thread != this && !thread.isDaemon()) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        main.setName("PluginApplicationThread");
        main.start();
    }

    public static void main(final String[] args) {
        Plugin.launch(Plugin.class, args);
    }
}
