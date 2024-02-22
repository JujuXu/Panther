package panther.pantherii;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        LaunchInterface li = new LaunchInterface(args);
        Websocket ws = new Websocket();

        //li.start();
        ws.start();
    }
}
