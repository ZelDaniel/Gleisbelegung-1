package org.gleisbelegung.io;

import java.net.Socket;

public class StSSocket extends XmlSocket {

    public final static int PORT = 3691;

    public StSSocket(Socket socket) {
        super(socket);
    }
}
