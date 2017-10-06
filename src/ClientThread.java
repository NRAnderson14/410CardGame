import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    Player playerToUpdate;
    Socket socket;

    public ClientThread(Player player, Socket socket) {
        this.socket = socket;
        this.playerToUpdate = player;
    }

    @Override
    public void run() {
        Player receivedPlayer = null;
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
                oOutputStream.writeObject(playerToUpdate);

                try {
                    receivedPlayer = (Player) oInputStream.readObject();    //Cast the rec'd object to Player
                } catch (EOFException e) {
                    //Ignore
                }

                Player newPlayer = receivedPlayer;
                int newScore = newPlayer.getScore();

                System.out.println(newScore);

//                oOutputStream.close();
//                oInputStream.close();
//                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
