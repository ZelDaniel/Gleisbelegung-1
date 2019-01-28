package org.gleisbelegung.io;

import org.gleisbelegung.database.Database;
import org.gleisbelegung.xml.MalformedXMLException;
import org.gleisbelegung.xml.XML;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class XmlSocket {

    private Socket socket;

    public XmlSocket(Socket socket) {
        this.socket = socket;
    }

    public XML read() throws IOException, MalformedXMLException {
        XML xml = XML.read(socket.getInputStream(), Charset.defaultCharset());
        int simtime = Database.getInstance().getSimTime();
        System.out.printf("%02d:%02d:%02d: %s\n",
                TimeUnit.SECONDS.toHours(simtime),
                TimeUnit.SECONDS.toMinutes(simtime) % 60,
                TimeUnit.SECONDS.toSeconds(simtime) % 60,
                xml.toString()
        );

        return xml;
    }

    public void write(XML xml) throws IOException {
         socket.getOutputStream().write(xml.getBytes(Charset.defaultCharset()));
         socket.getOutputStream().write(10);
         socket.getOutputStream().flush();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    /**
     * close the open socket
     * @return success
     */
    public boolean close(){
        try {
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
