package panther.pantherii;

import java.io.IOException;

public class Main {
    static Websocket ws;
    public static void main(String[] args) {
        LaunchInterface li = new LaunchInterface(args);
        ws = new Websocket();

        li.start();
        ws.start();
    }

    public static Websocket getWS() {
        return ws;
    }
    public static void sendLog(String str) {
        System.out.print(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS")) + " > ");
        System.out.println(str);
    }
}
