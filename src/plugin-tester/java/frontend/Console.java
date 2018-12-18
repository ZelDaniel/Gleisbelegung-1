package frontend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Console extends Thread{

    private final Thread creatorThread = Thread.currentThread();
    private ServerSocket serverSocket;
    private Socket socket;

    @Override
    public void run() {
        int offset = 0;
        char[] buffer = new char[100];
        try (InputStreamReader reader = new InputStreamReader(System.in))  {
            while(true) {
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
        } catch(IOException e) {
        }
    }

    void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void handleInput(final String input) throws IOException {
        String[] tokens = input.toLowerCase().split("\t+|\r+| +");
        for(final String token : tokens) {
            switch (token) {
                case "quit":
                    System.in.close();
                    this.serverSocket.close();
                    creatorThread.interrupt();
                    return;
                case "close":
                    this.socket.close();
                    break;
            }
        }
        // TODO
    }

    void setSocket(Socket s) {
        this.socket = s;
    }
}
