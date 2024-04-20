package panther.pantherii;

import old.Main;

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
            PantherInterface.sendLog("No ping received from Panther32. Reseting WebSocket Client... [Empty]");
        } else if(millis - pings.get(pings.size()-1) > 10*1000) {
            ws.reset();
            PantherInterface.sendLog("No ping received from Panther32. Reseting WebSocket Client... [Timed out]");
        } else {
            new Timer().schedule(new Ping(ws, ms),ms);
        }
    }
}
