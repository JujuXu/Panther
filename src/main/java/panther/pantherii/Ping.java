/**
 * @author: Julien Navez
 */

package panther.pantherii;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
/**
 * This class represents a ping task that checks for "ping" messages in the WebSocket data.
 * "ping" messages are sent by the Panther32 to check the connection status.
 * Panther32 is the ESP32 controller of the Panther robot.
 * If no "ping" messages are received, the WebSocket connection is reset.
 * In reality, when the ESP32 is reseted, both are trying to reconnect.
 */
public class Ping extends TimerTask {
    /**
     * The WebSocket connection.
     */
    Websocket ws;
    /**
     * The interval for the ping task in milliseconds.
     */
    private int ms;
    /**
     * The list of ping messages.
     */
    private ArrayList<Long> pings = new ArrayList<>();

    /**
     * Constructor for the Ping class.
     * @param ws The WebSocket connection.
     * @param ms The interval for the ping task in milliseconds.
     */
    public Ping(Websocket ws, int ms) {
        this.ws = ws;
        this.ms = ms;
    }

    /**
     * Checks for "ping" messages in the WebSocket data.
     * If no "ping" messages are received, the WebSocket connection is reset.
     * If "ping" messages are received, a new Ping task is scheduled.
     * The interval for the new Ping task is the same as the interval for the current Ping task.
     */
    @Override
    public void run() {
        ArrayList<String> astr = ws.getData();

        long millis = ZonedDateTime.now().toInstant().toEpochMilli();

        if(!astr.isEmpty()) {
            for(int i = 0; i<astr.size(); i++) {
                String str = astr.get(i);

                if(str.contains("ping")) {
                    pings.add(millis);
                }

                ws.clearPings();
            }
        }

        if(pings.isEmpty()) {
            ws.reset();
            PantherInterface.sendLog("No ping received from Panther32. Reseting WebSocket Client...");
        } else {
            new Timer().schedule(new Ping(ws, ms),ms);
        }
    }
}
