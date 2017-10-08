import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {

        //Starts the menu
        try {
            MainMenu menu = new MainMenu();
            menu.startMenu();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
