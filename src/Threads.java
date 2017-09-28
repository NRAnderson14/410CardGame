import java.io.IOException;

public class Threads implements Runnable {
    private Thread t;
    private String threadName;
    public enum threadType {SERVER, CLIENT};
    private threadType runningType;

    Threads(String name, threadType type) {
        threadName = name;
        runningType = type;
    }

    public void run() {
        if (runningType == threadType.SERVER) {
            System.out.println("Running server");
            try {
                Board b = new Board();
                b.broadcastTest();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Running client");
            try {
                Player p = new Player("192.168.1.147");
                p.receiveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
