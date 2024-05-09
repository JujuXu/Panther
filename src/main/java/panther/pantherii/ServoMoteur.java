/**
 * @author: Julien Navez
 */

package panther.pantherii;

import javafx.scene.text.Text;

import java.io.IOException;

/**
 * This class represents a servo motor.
 * The servo motor has a reset value and an angle.
 * The servo motor can be reset to the reset value.
 * The servo motor can send data to the ESP32.
 */

public class ServoMoteur {
    /**
     * The text to be updated with the data.
     */
    private Text text;
    /**
     * The reset value for the servo motor.
     */
    private double resetValue;
    /**
     * The angle of the servo motor.
     */
    private double angle;
    /**
     * The WebSocket connection.
     */
    private Websocket ws = PantherInterface.getWS();

    /**
     * Constructor for the ServoMoteur class.
     * @param text The text to be updated with the data.
     * @param resetValue The reset value for the servo motor.
     */
    public ServoMoteur(Text text, double resetValue) {
        this.text = text;
        this.resetValue = resetValue;

        setAngle(resetValue);
    }

    /**
     * Sets the angle of the servo motor.
     * @param d The angle of the servo motor.
     */
    public void setAngle(double d) {
        angle = d;
        text.setText(Double.toString(d));
    }

    /**
     * Gets the angle of the servo motor.
     * @return The angle of the servo motor.
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Resets the servo motor to the reset value.
     */
    public void reset() {
        setAngle(resetValue);
    }

    /**
     * Sends data to the ESP32.
     */
    public void sendData() {
        String id = text.getId(); // id of the servo motor
        String tosend = id.substring(id.indexOf("o")+1)+(int)(angle); // id.substring(id.indexOf("o")+1) is the id of the servo motor

        //System.out.println(tosend);

        try {
            ws.sendData(tosend); // send data to the websocket
        } catch (IOException ignored) {

        }
    }

    /**
     * Gets the reset value of the servo motor.
     * @return The reset value of the servo motor.
     */
    public double getResetValue() {
        return resetValue;
    }
}
