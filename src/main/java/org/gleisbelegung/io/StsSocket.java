package org.gleisbelegung.io;

import java.net.Socket;

public class StsSocket extends XmlSocket {

    public final static int PORT = 3691;

    public StsSocket(Socket socket) {
        super(socket);
    }
}
