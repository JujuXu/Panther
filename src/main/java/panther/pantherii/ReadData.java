package panther.pantherii;

import javafx.scene.paint.Paint;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a task that reads data from the WebSocket connection.
 * The data is then processed and displayed in the GUI.
 */
public class ReadData extends TimerTask {
    /**
     * The WebSocket connection.
     */
    private Websocket ws;
    /**
     * The texts to be updated with the data.
     */
    private ArrayList<ArrayList<Text>> aTexts;
    /**
     * The interval for the read data task in milliseconds.
     */
    private int ms;
    /**
     * The curves to be updated with the data.
     */
    private ArrayList<QuadCurve> psCurves;

    /**
     * Constructor for the ReadData class.
     * @param ws The WebSocket connection.
     * @param aTexts The texts to be updated with the data.
     * @param ms The interval for the read data task in milliseconds.
     */
    public ReadData(Websocket ws, ArrayList<ArrayList<Text>> aTexts, int ms, ArrayList<QuadCurve> psCurves) {
        this.ws = ws;
        this.aTexts = aTexts;
        this.ms = ms;
        this.psCurves = psCurves;
    }

    /**
     * Reads data from the WebSocket connection.
     * The data is then processed and displayed in the GUI.
     * A new ReadData task is scheduled.
     * The interval for the new ReadData task is the same as the interval for the current ReadData task.
     * All the data received from ESP32 are for sensors and ping messages.
     */
    @Override
    public void run() {
        ArrayList<String> astr = ws.getData();

        if(!astr.isEmpty()) {
            String strv = "";
            double value = Double.NaN;

            String pattern = "([a-zA-Z]+)(-?\\d+(\\.\\d+)?)";
            Pattern r = Pattern.compile(pattern);

            for(String s : astr) {
                Matcher m = null;
                try {
                    m = r.matcher(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(m == null) {
                    continue;
                }

                if(m.find()) {
                    strv = m.group(1);
                    value = Double.parseDouble(m.group(2));
                }

                if(strv.equals("l")) {

                    aTexts.get(0).get(1).setText(value+"");

                    if(value < 15) {
                        psCurves.get(1).setFill(Paint.valueOf("RED"));
                    } else {
                        psCurves.get(1).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                    }

                } else if(strv.equals("f")) {

                    aTexts.get(0).get(0).setText(value+"");

                    if(value < 15) {
                        psCurves.get(0).setFill(Paint.valueOf("RED"));
                    } else {
                        psCurves.get(0).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                    }

                } else if(strv.equals("r")) {

                    aTexts.get(0).get(2).setText(value+"");

                    if(value < 15) {
                        psCurves.get(2).setFill(Paint.valueOf("RED"));
                    } else {
                        psCurves.get(2).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                    }

                } else if(strv.equals("ca")) {

                    aTexts.get(1).get(3).setText(value+" A");

                } else if(strv.equals("cb")) {

                    aTexts.get(1).get(4).setText(value+" A");

                } else if(strv.equals("ax")) {

                    aTexts.get(1).get(0).setText(String.format("%.3f", (value*90))+"°");

                } else if(strv.equals("ay")) {
                    System.out.println(s);
                    aTexts.get(1).get(1).setText(String.format("%.3f", (value*90))+"°");

                } else if(strv.equals("az")) {

                    aTexts.get(1).get(2).setText(String.format("%.3f", (value*90))+"°");

                } else if(strv.equals("wf")) {
                    if(value == 1) {
                        aTexts.get(1).get(5).setText("WARNING");
                        aTexts.get(1).get(5).setFill(Paint.valueOf("rgba(255, 0, 0, 1)"));
                    } else {
                        aTexts.get(1).get(5).setText("OK");
                        aTexts.get(1).get(5).setFill(Paint.valueOf("rgba(0, 255, 0, 1)"));
                    }
                }
            }
        }

        new Timer().schedule(new ReadData(ws,aTexts,ms,psCurves),ms);
    }
}