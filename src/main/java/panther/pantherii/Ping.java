package panther.pantherii;

import javafx.scene.text.Text;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Ping extends TimerTask {
    Websocket ws;
    private int ms;
    private ArrayList<Long> pings = new ArrayList<>();
    public Ping(Websocket ws, int ms) {
        this.ws = ws;
        this.ms = ms;
    }
    @Override
    public void run() {
        ArrayList<String> astr = ws.getData();

        long millis = ZonedDateTime.now().toInstant().toEpochMilli();

        if(!astr.isEmpty()) {
            String PS;
            for(int i = 0; i<astr.size(); i++) {
                String str = astr.get(i);

                if(str.contains("ping")) {
                    pings.clear();
                    pings.add(millis);
                }
            }
        }

        if(pings.isEmpty()) {
            ws.reset();
            Main.sendLog("No ping received from Panther32. Reseting WebSocket Client...");
        } else if(millis - pings.get(pings.size()-1) > 10*1000) {
            ws.reset();
            Main.sendLog("No ping received from Panther32. Reseting WebSocket Client...");
        } else {
            new Timer().schedule(new Ping(Main.getWS(),ms),ms);
        }
    }
}
