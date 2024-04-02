package panther.pantherii;

import javafx.scene.control.Slider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ResendData extends TimerTask {
    ArrayList<ServoMoteur> servos;
    Slider sliderSpeed;
    int ms;
    public ResendData(ArrayList<ServoMoteur> servos, Slider sliderSpeed, int ms) {
        this.servos = servos;
        this.sliderSpeed = sliderSpeed;
        this.ms = ms;
    }
    @Override
    public void run() {
        for(ServoMoteur sm : servos) {
            sm.sendData();
        }

        int speedPercent = (int) Math.ceil((100/(sliderSpeed.getMax()-sliderSpeed.getMin())*sliderSpeed.getValue()-100/sliderSpeed.getMax()-sliderSpeed.getMin()*1));

        Websocket ws = Main.getWS();
        try {
            ws.sendData("ST"+speedPercent);
        } catch (IOException ignored) {

        }

        new Timer().schedule(new ResendData(servos,sliderSpeed,ms),ms);
    }
}
