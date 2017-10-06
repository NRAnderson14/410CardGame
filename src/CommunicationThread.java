import java.io.*;
import java.net.Socket;

public class CommunicationThread extends Thread {
    Socket socket;

    public CommunicationThread(Socket connectedSocket) {
        this.socket = connectedSocket;
    }

    @Override
    public void run() {
        ObjectOutputStream oOutputStream = null;
        ObjectInputStream oInputStream = null;

        try {
            oOutputStream = new ObjectOutputStream(socket.getOutputStream());
            oInputStream = new ObjectInputStream(socket.getInputStream());
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

                    receivedPlayer.addWin();
                    receivedPlayer.addWin();

                    oOutputStream.writeObject(receivedPlayer);
//                    oInputStream.close();
//                    oOutputStream.close();
//                    socket.close();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException es) {
                es.printStackTrace();
            }
        }
    }
}
