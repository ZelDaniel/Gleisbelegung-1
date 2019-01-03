package org.gleisbelegung;

import org.gleisbelegung.io.StsSocket;
import org.gleisbelegung.xml.XML;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import static org.gleisbelegung.Plugin.*;

class XmlInputHandlerThread extends Thread {

    private final Plugin plugin;
    private StsSocket stSSocket;

    enum ExitReason {
        SOCKET_CLOSED, ERROR
    }

    public XmlInputHandlerThread(Plugin plugin) {
        this.plugin = plugin;
    }

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
                plugin.connectionEstablished();
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

    private ExitReason handleInput(Socket socket) {
        stSSocket = new StsSocket(socket);

        while(!socket.isClosed()) {
            try {
                final XML readXml = stSSocket.read();
                if (readXml == null) {
                    socket.close();
                }
                switch (readXml.getKey()) {
                    case "status":
                        handleStatus(readXml);
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
