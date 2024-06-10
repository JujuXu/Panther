package panther.pantherii;

import javafx.scene.paint.Paint;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
            String strv;
            double value;

            for(String s : astr) {
                value = Double.NaN;
                strv = "";

                for(int i=0;i<s.length()-1;i++) {
                    try {
                        value = Double.parseDouble(s.substring(i));
                    } catch (NumberFormatException e) {

                    }

                    if(!Double.isNaN(value)) {
                        strv = s.substring(0,i);
                    }
                }

                if(strv.equals("l")) {
                    //System.out.println("l"+value);
                    aTexts.get(0).get(1).setText(value+"");
                    if(value < 15) {
                        psCurves.get(1).setFill(Paint.valueOf("RED"));
                    } else {
                        psCurves.get(1).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                    }
                } else if(strv.equals("f")) {
                    //System.out.println("f"+value);
                    aTexts.get(0).get(0).setText(value+"");
                    if(value < 15) {
                        psCurves.get(0).setFill(Paint.valueOf("RED"));
                    } else {
                        psCurves.get(0).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                    }
                } else if(strv.equals("r")) {
                    //System.out.println("r"+value);
                    aTexts.get(0).get(2).setText(value+"");
                    if(value < 15) {
                        psCurves.get(2).setFill(Paint.valueOf("RED"));
                    } else {
                        psCurves.get(2).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                    }
                } else if(strv.equals("ca")) {
                    //System.out.println("ca"+value);
                    aTexts.get(1).get(3).setText(value+" A");
                } else if(strv.equals("cb")) {
                    //System.out.println("cb"+value);
                    aTexts.get(1).get(4).setText(value+" A");
                } else if(strv.equals("ax")) {
                    //System.out.println("ax"+value);
                    aTexts.get(1).get(0).setText(value+"");
                } else if(strv.equals("ay")) {
                    //System.out.println("ay"+value);
                    aTexts.get(1).get(1).setText(value+"");
                } else if(strv.equals("az")) {
                    //System.out.println("az "+value);
                    aTexts.get(1).get(2).setText(value+"");
                }
            }

            /*for(int i = 0; i<astr.size(); i++) {
                String str = astr.get(i);



                /*
                int value = -1;

                if(str.contains("=")) {
                    fstr = str.substring(str.indexOf("=")+1);
                    try {
                        value = Integer.parseInt(fstr);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    if(str.contains("PS_")) {
                        if(str.contains("FRONT")) {
                            aTexts.get(0).get(0).setText(value+" cm");
                            if(value < 15) {
                                psCurves.get(0).setFill(Paint.valueOf("RED"));
                            } else {
                                psCurves.get(0).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                            }
                        } else if(str.contains("LEFT")) {
                            aTexts.get(0).get(1).setText(value+" cm");
                            if(value < 15) {
                                psCurves.get(1).setFill(Paint.valueOf("RED"));
                            } else {
                                psCurves.get(1).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                            }
                        } else if(str.contains("RIGHT")) {
                            aTexts.get(0).get(2).setText(value+" cm");
                            if(value < 15) {
                                psCurves.get(2).setFill(Paint.valueOf("RED"));
                            } else {
                                psCurves.get(2).setFill(Paint.valueOf("rgba(255, 255, 255, 0)"));
                            }
                        }
                    } else if(str.contains("ACC_")) {
                        if(str.contains("X")) {
                            aTexts.get(1).get(0).setText(value+"");
                        } else if(str.contains("Y")) {
                            aTexts.get(1).get(1).setText(value+"");
                        } else if(str.contains("Z")) {
                            aTexts.get(1).get(2).setText(value+"");
                        }
                    } else if (str.contains("CURR_")) {
                        if(str.contains("A")) {
                            aTexts.get(1).get(3).setText(value+"");
                        } else if(str.contains("B")) {
                            aTexts.get(1).get(4).setText(value+"");
                        }
                    }
                }
            }*/
        }

        new Timer().schedule(new ReadData(ws,aTexts,ms,psCurves),ms);
    }
}