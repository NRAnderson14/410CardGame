import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {
    public Socket socket;
    public PrintWriter outBound;
    public BufferedReader inBound;
    private String name;

    public ClientConnection(Socket socket, PrintWriter outBound, BufferedReader inBound) {
        this.socket = socket;
        this.outBound = outBound;
        this.inBound = inBound;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
