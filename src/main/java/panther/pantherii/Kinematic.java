package panther.pantherii;

import javafx.scene.control.Slider;

import java.util.ArrayList;

public class Kinematic {
    private ServoMoteur smC; // top
    private ServoMoteur smD;
    private ServoMoteur smE; // bottom
    private Slider xslider;
    private Slider yslider;
    private ArrayList<Double> x = new ArrayList<>();
    private ArrayList<Double> y = new ArrayList<>();
    private boolean up;
    private boolean down;
    private boolean forward;
    private boolean backward;
    private double at;
    private double a;
    private double b;
    private double c;
    private double l1;
    private double l2;
    private double l3;
    private boolean run;
    public Kinematic(ArrayList<ServoMoteur> servos, double[] dims, Slider xslider, Slider yslider) {
        this.smC = servos.get(2);
        this.smD = servos.get(3);
        this.smE = servos.get(4);

        this.l1 = dims[0];
        this.l2 = dims[1];
        this.l3 = dims[2];

        this.xslider = xslider;
        this.yslider = yslider;

        home();
    }

    public void home() {
        a = smE.getResetValue();
        b = smD.getResetValue();
        c = smC.getResetValue();

        //System.out.println("home: a: "+a+" b: "+b+" c: "+c);

        //double addX = l1*Math.sin(Math.toRadians(a)+l2*Math.sin(Math.toRadians(a+b)));
        //double addY = l1*Math.cos(Math.toRadians(a)+l2*Math.cos(Math.toRadians(a+b)));

        x.clear();
        y.clear();

        x.add(125d);
        y.add(105d);

        setAngles(true);
    }

    public void setX(double x) {
        this.x.add(x);
        setAngles(true);
    }

    public void setY(double y) {
        this.y.add(y);
        setAngles(false);
    }

    private void checkFor() {
        if(x.size() >= 2) {
            while (x.size()>2) {
                x.remove(0);
            }

            if(x.get(0) > x.get(1)) {
                backward = true;
                forward = false;
            } else {
                backward = false;
                forward = true;
            }
        }
    }

    private void checkUp() {
        if(y.size() >= 2) {
            while (y.size()>2) {
                y.remove(0);
            }

            if(y.get(0) > y.get(1)) {
                down = true;
                up = false;
            } else {
                down = false;
                up = true;
            }
        }
    }

    private void setAngles(boolean isX) {
        double xx;
        double yy;

        checkUp();
        checkFor();

        if(x.size() >= 2) {
            xx = x.get(1);
        } else {
            xx = x.get(0);
        }

        if(y.size() >= 2) {
            yy = y.get(1);
        } else {
            yy = y.get(0);
        }

        double hyp;

        run = true;

        while (run) {
            hyp = Math.sqrt(xx*xx+yy*yy);
            at = Math.acos(yy/hyp);
            a = at - Math.acos((l1*l1 + xx*xx + yy*yy - l2*l2) / (2*l1 *hyp));
            b = Math.PI-Math.acos((l2*l2+l1*l1-xx*xx-yy*yy)/(2*l1*l2));

            if(Double.isNaN(a) || Double.isNaN(b)) {
                if(isX) {
                    if(forward && !backward) {
                        yy = yy-1;
                    } else if(!forward && backward) {
                        yy = yy +1;
                    }
                } else {
                    if(up && !down) {
                        xx = xx -1;
                    } else if(!up && down) {
                        xx = xx +1;
                    }
                }
            } else {
                run = false;
            }
        }
        x.add(xx);
        y.add(yy);

        xslider.adjustValue(xx);
        yslider.adjustValue(yy);

        //System.out.println("setAngles at: "+at);
        //System.out.println("setAngles a: "+a);
        //System.out.println("setAngles b: "+b);
        //System.out.println("setAngles c: "+((Math.PI / 2) - a - b));

        c = (int) Math.toDegrees((Math.PI / 2) - a - b)+90; // add offset to control pitch of angle c
        a = (int) Math.toDegrees(a) + 90;
        b = (int) Math.toDegrees(b) + 90;

        sendAngles();
        //System.out.println("x: "+xx+" y: "+yy);
    }

    private void sendAngles() {
        smE.setAngle(a);
        smE.sendData();

        smD.setAngle(b);
        smD.sendData();

        smC.setAngle(c);
        smC.sendData();

        //System.out.println("sendAngles: a: "+a+" b: "+b+" c: "+c);
    }
}