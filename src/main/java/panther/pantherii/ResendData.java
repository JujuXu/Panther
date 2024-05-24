package panther.pantherii;

import javafx.scene.control.Slider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class represents a task that resends data to the ESP32.
 * The data is then processed and sent to the ESP32.
 * A new ResendData task is scheduled.
 * The interval for the new ResendData task is the same as the interval for the current ResendData task.
 * All the data sent to ESP32 are for servos and speed messages.
 */

public class ResendData extends TimerTask {
    /**
     * The servos to be updated with the data.
     */
    private ArrayList<ServoMoteur> servos;
    /**
     * The interval for the resend data task in milliseconds.
     */
    private int ms;

    /**
     * Constructor for the ResendData class.
     * @param servos The servos to be updated with the data.
     * @param ms The interval for the resend data task in milliseconds.
     */
    public ResendData(ArrayList<ServoMoteur> servos, int ms) {
        this.servos = servos;
        this.ms = ms;
    }

    /**
     * Resends data to the ESP32.
     * The data is then processed and sent to the ESP32.
     * A new ResendData task is scheduled.
     * The interval for the new ResendData task is the same as the interval for the current ResendData task.
     */
    @Override
    public void run() {
        /**
         * Each ServoMoteur object sends its data to the ESP32.
         * The data is the angle of the servo motor.
         */
        for(ServoMoteur sm : servos) {
            sm.sendData();
        }

        new Timer().schedule(new ResendData(servos,ms),ms);
    }
}
