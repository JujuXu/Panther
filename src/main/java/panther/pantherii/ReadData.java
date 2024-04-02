package panther.pantherii;

import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ReadData extends TimerTask {
    private Websocket ws;
    private ArrayList<ArrayList<Text>> aTexts;
    private int ms;
    public ReadData(Websocket ws, ArrayList<ArrayList<Text>> aTexts, int ms) {
        this.ws = ws;
        this.aTexts = aTexts;
        this.ms = ms;
    }
    @Override
    public void run() {
        ArrayList<String> astr = ws.getData();

        if(!astr.isEmpty()) {
            String PS;
            for(int i = 0; i<astr.size(); i++) {
                String str = astr.get(i);

                if(str.contains("PS_")) {
                    PS = str.substring(str.indexOf("=")+1);

                    if(str.contains("FRONT")) {
                        aTexts.get(0).get(0).setText(PS+" cm");
                    } else if(str.contains("LEFT")) {
                        aTexts.get(0).get(1).setText(PS+" cm");
                    } else if(str.contains("RIGHT")) {
                        aTexts.get(0).get(2).setText(PS+" cm");
                    }
                }
            }
        }

        new Timer().schedule(new ReadData(ws,aTexts,ms),ms);
    }
}
