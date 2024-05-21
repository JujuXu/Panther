/**
 * @author: Julien Navez
 */

package panther.pantherii;

import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class represents a task that reads data from the WebSocket connection.
 * The data is then processed and displayed in the GUI.
 */
public class ReadData extends TimerTask {
    /**
     * The WebSocket connection.
     */
    private Websocket ws;
    /**
     * The texts to be updated with the data.
     */
    private ArrayList<ArrayList<Text>> aTexts;
    /**
     * The interval for the read data task in milliseconds.
     */
    private int ms;

    /**
     * Constructor for the ReadData class.
     * @param ws The WebSocket connection.
     * @param aTexts The texts to be updated with the data.
     * @param ms The interval for the read data task in milliseconds.
     */
    public ReadData(Websocket ws, ArrayList<ArrayList<Text>> aTexts, int ms) {
        this.ws = ws;
        this.aTexts = aTexts;
        this.ms = ms;
    }

    /**
     * Reads data from the WebSocket connection.
     * The data is then processed and displayed in the GUI.
     * A new ReadData task is scheduled.
     * The interval for the new ReadData task is the same as the interval for the current ReadData task.
     * All the data received from ESP32 are for sensors and ping messages.
     */
    @Override
    public void run() {
        ArrayList<String> astr = ws.getData();

        if(!astr.isEmpty()) {
            String value;
            for(int i = 0; i<astr.size(); i++) {
                String str = astr.get(i);

                if(str.contains("PS_")) {
                    value = str.substring(str.indexOf("=")+1);
                    value.replace("" +
                            "","");

                    if(str.contains("FRONT")) {
                        aTexts.get(0).get(0).setText(value+" cm");
                    } else if(str.contains("LEFT")) {
                        aTexts.get(0).get(1).setText(value+" cm");
                    } else if(str.contains("RIGHT")) {
                        aTexts.get(0).get(2).setText(value+" cm");
                    }
                }
            }
        }

        new Timer().schedule(new ReadData(ws,aTexts,ms),ms);
    }
}
