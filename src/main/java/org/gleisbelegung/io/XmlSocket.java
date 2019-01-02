package org.gleisbelegung.io;

import org.gleisbelegung.xml.MalformedXMLException;
import org.gleisbelegung.xml.XML;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

public class XmlSocket {

    private Socket socket;

    public XmlSocket(Socket socket) {
        this.socket = socket;
    }

    public XML read() throws IOException, MalformedXMLException {
        return XML.read(socket.getInputStream(), Charset.defaultCharset());
    }

    public void write(XML xml) throws IOException {
         socket.getOutputStream().write(xml.getBytes(Charset.defaultCharset()));
         socket.getOutputStream().write(10);
         socket.getOutputStream().flush();
    }
}
