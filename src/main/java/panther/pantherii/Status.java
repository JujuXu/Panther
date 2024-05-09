/**
 * @author: Julien Navez
 */

package panther.pantherii;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class represents a task that checks the status of the WebSocket connection.
 * The status is then displayed in the GUI.
 * A new Status task is scheduled.
 * The interval for the new Status task is the same as the interval for the current Status task.
 */
public class Status extends TimerTask {
    /**
     * The status to be updated with the data.
     */
    private Circle status;
    /**
     * The text to be updated with the data.
     */
    private Text text;
    /**
     * The interval for the status task in milliseconds.
     */
    private int ms;

    /**
     * Constructor for the Status class.
     * @param status The status to be updated with the data.
     * @param text The text to be updated with the data.
     * @param ms The interval for the status task in milliseconds.
     */
    public Status(Circle status,Text text,int ms) {
        this.status = status;
        this.text = text;
        this.ms = ms;
    }

    /**
     * Checks the status of the WebSocket connection.
     * The status is then displayed in the GUI.
     * A new Status task is scheduled.
     * The interval for the new Status task is the same as the interval for the current Status task.
     */
    @Override
    public void run() {
        boolean run = PantherInterface.getWS().isConnected(); // check neither the WebSocket connection is established

        if(run) {
            status.setFill(Color.LIGHTGREEN);
            text.setText("ONLINE");
        } else {
            status.setFill(Color.RED);
            text.setText("OFFLINE");
        }

        new Timer().schedule(new Status(status,text,ms),ms);
    }
}
