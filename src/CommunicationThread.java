import java.io.*;
import java.net.Socket;

public class CommunicationThread extends Thread {
    Socket socket;

    public CommunicationThread(Socket connectedSocket) {
        socket = connectedSocket;
    }

    @Override
    public void run() {
        InputStream inStream = null;
        OutputStream outStream = null;
        BufferedInputStream binStream = null;
        ObjectOutputStream oOutputStream = null;
        ObjectInputStream oInputStream = null;

        try {
            inStream = socket.getInputStream();
            binStream = new BufferedInputStream(inStream);
            outStream = socket.getOutputStream();

            oOutputStream = new ObjectOutputStream(outStream);
            oInputStream = new ObjectInputStream(binStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                try {
                    Player receivedPlayer = null;
                    try {
                        receivedPlayer = (Player) oInputStream.readObject();
                    } catch (EOFException ee) {
                        //Ignore
                    }
                    System.out.println(receivedPlayer.getName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException es) {
                es.printStackTrace();
            }
        }
    }
}
