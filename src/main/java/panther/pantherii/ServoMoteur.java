package panther.pantherii;

import javafx.scene.text.Text;

import java.io.IOException;

public class ServoMoteur {
    private Text text;
    private double resetValue;
    private double angle = resetValue;
    private Websocket ws;
    public ServoMoteur(Text text, Websocket ws, double resetValue) {
        this.text = text;
        this.ws = ws;
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
        String tosend = "SM"+id.substring(id.indexOf("o")+1)+"="+angle;

        try {
            ws.sendData(tosend);
        } catch (IOException ignored) {

        }
    }
}
