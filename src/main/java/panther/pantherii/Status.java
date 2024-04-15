package panther.pantherii;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import old.Main;

import java.util.Timer;
import java.util.TimerTask;

public class Status extends TimerTask {
    private Circle status;
    private Text text;
    private int ms;
    public Status(Circle status,Text text,int ms) {
        this.status = status;
        this.text = text;
        this.ms = ms;
    }
    @Override
    public void run() {
        boolean run = PantherInterface.getWS().isConnected();

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
