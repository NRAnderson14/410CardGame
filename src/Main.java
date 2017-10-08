public class Main {
    public static void main(String[] args) {
        //Example game
        try {
           //Create a new game server
            Runnable gameThread = new Runnable() {
                @Override
                public void run() {
                    Game testGame = new Game();
                    testGame.startServer();
                    testGame.playGame();
                }
            };
            new Thread(gameThread).start();

//            //Create and connect all three players
            Player p1 = new Player("p1", "127.0.0.1");
            Player p2 = new Player("p2", "127.0.0.1");
            Player p3 = new Player("p3", "127.0.0.1");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
