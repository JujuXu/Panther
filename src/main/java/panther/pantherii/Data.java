package panther.pantherii;

import javafx.scene.text.Text;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Data extends TimerTask {
    Websocket ws;
    ArrayList<ArrayList<Text>> aTexts;
    public Data(Websocket ws, ArrayList<ArrayList<Text>> aTexts) {
        this.ws = ws;
        this.aTexts = aTexts;
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

        new Timer().schedule(new Data(ws,aTexts),1000);
    }
}
