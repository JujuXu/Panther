package panther.pantherii;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ServosData extends TimerTask {
    ArrayList<ServoMoteur> servos;
    int ms;
    public ServosData(ArrayList<ServoMoteur> servos,int ms) {
        this.servos = servos;
        this.ms = ms;
    }
    @Override
    public void run() {
        for(ServoMoteur sm : servos) {
            sm.sendData();
        }

        new Timer().schedule(new ServosData(servos,ms),ms);
    }
}
