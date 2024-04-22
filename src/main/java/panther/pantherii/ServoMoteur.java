package panther.pantherii;

import javafx.scene.text.Text;
import old.Main;

import java.io.IOException;

public class ServoMoteur {
    private Text text;
    private double resetValue;
    private double angle;
    private Websocket ws = PantherInterface.getWS();
    public ServoMoteur(Text text, double resetValue) {
        this.text = text;
        this.resetValue = resetValue;

        setAngle(resetValue);
    }

    public void setAngle(double d) {
        angle = d;
        text.setText(Double.toString(d));
    }

    public double getAngle() {
        return angle;
    }

    public void reset() {
        setAngle(resetValue);
    }

    public void sendData() {
        String id = text.getId();
        String tosend = id.substring(id.indexOf("o")+1)+(int)(angle);

        //System.out.println(tosend);

        try {
            ws.sendData(tosend);
            /*if(id.equals("servoG")) {
                PantherInterface.sendLog("ServoMoteurSend");
            }*/
        } catch (IOException ignored) {

        }
    }

    public double getResetValue() {
        return resetValue;
    }
}
