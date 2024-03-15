package panther.pantherii;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ServosData extends TimerTask {
    ArrayList<ServoMoteur> servos;
    public ServosData(ArrayList<ServoMoteur> servos) {
        this.servos = servos;
    }
    @Override
    public void run() {
        for(ServoMoteur sm : servos) {
            sm.sendData();
        }

        new Timer().schedule(new ServosData(servos),100);
    }
}
