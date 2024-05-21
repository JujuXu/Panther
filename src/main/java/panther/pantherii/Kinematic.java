/**
 * @author: Julien Navez
 */

package panther.pantherii;

import javafx.scene.control.Slider;

import java.util.ArrayList;
/**
 * This class represents the kinematic model of a robot.
 * It uses three ServoMoteur objects to control the robot's movements.
 * The robot's position is controlled by two sliders, one for the x-axis and one for the y-axis.
 *
 */
public class Kinematic {
    /**
     * The ServoMoteur objects for the third servo motor.
     */
    private ServoMoteur smC;
    /**
     * The ServoMoteur objects for the fourth servo motors.
     */
    private ServoMoteur smE;
    /**
     * The ServoMoteur objects for the fifth servo motors.
     */
    private ServoMoteur smF;

    /**
     * The x-axis slider for inverse kinematics.
     */
    private Slider xslider;
    /**
     * The y-axis slider for inverse kinematics.
     */
    private Slider yslider;

    /**
     * ArrayList to store the x coordinates of the robot to know if it is moving forward or backward.
     */
    private ArrayList<Double> x = new ArrayList<>();

    /**
     * ArrayList to store the y coordinates of the robot to know if it is moving up or down.
     */
    private ArrayList<Double> y = new ArrayList<>();

    /**
     * Booleans to check if the robot is moving up.
     */
    private boolean up;
    /**
     * Booleans to check if the robot is moving down.
     */
    private boolean down;
    /**
     * Booleans to check if the robot is moving forward.
     */
    private boolean forward;
    /**
     * Booleans to check if the robot is moving backward.
     */
    private boolean backward;

    /**
     * The at angle for inverse kinematics.
     */
    private double at;
    /**
     * The a angle for inverse kinematics.
     */
    private double a;
    /**
     * The b angle for inverse kinematics.
     */
    private double b;
    /**
     * The c angle for inverse kinematics.
     */
    private double c;
    /**
     * The d angle offset for the wrist pitch in inverse kinematics.
     */
    private double d;

    /**
     * The length of the first segment of the robot.
     */
    private double l1;
    /**
     * The length of the second segment of the robot.
     */
    private double l2;
    /**
     * The length of the third segment of the robot.
     */
    private double l3;

    /**
     * Boolean to correctly set the angles of the robot.
     * Using arc cosine functions can return NaN values.
     */
    private boolean run;
    private int aD, bD, cD, dD;
    /**
     * Constructor for the Kinematic class.
     * Initializes the servo motors, segment lengths, and sliders.
     *
     * @param servos An ArrayList of ServoMoteur objects.
     * @param dims An array of doubles representing the lengths of the robot's segments.
     * @param xslider A Slider object to control the robot's x-axis movement.
     * @param yslider A Slider object to control the robot's y-axis movement.
     */
    public Kinematic(ArrayList<ServoMoteur> servos, double[] dims, Slider xslider, Slider yslider) {
        this.smC = servos.get(2);
        this.smE = servos.get(3);
        this.smF = servos.get(4);

        this.l1 = dims[0];
        this.l2 = dims[1];
        this.l3 = dims[2];

        this.xslider = xslider;
        this.yslider = yslider;

        home();
    }
    /**
     * Resets the robot to its home position.
     */
    public void home() {
        /*a = smF.getResetValue();
        b = smE.getResetValue();
        c = smC.getResetValue();
        d = 0;

        // a b c e f g

        double addX = l1*Math.sin(Math.toRadians(a)+l2*Math.sin(Math.toRadians(a+b)));
        double addY = l1*Math.cos(Math.toRadians(a)+l2*Math.cos(Math.toRadians(a+b)));
        */

        //System.out.println("home: a: "+a+" b: "+b+" c: "+c);

        x.clear();
        y.clear();

        x.add(125d);
        y.add(105d);

        offset(0);

        setAngles(true);
    }
    /**
     * Sets the x-axis position of the robot.
     *
     * @param x A double representing the x-axis position.
     */
    public void setX(double x) {
        this.x.add(x);
        setAngles(true);
    }
    /**
     * Sets the y-axis position of the robot.
     *
     * @param y A double representing the y-axis position.
     */
    public void setY(double y) {
        this.y.add(y);
        setAngles(false);
    }
    /**
     * Checks if the robot is moving forward or backward.
     */
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
    /**
     * Checks if the robot is moving up or down.
     */
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
    /**
     * This method calculates and sets the angles of the servo motors based on the robot's position.
     * It first checks if the robot is moving up, down, forward, or backward.
     * Then, it calculates the hypotenuse and the angles 'a' and 'b' using trigonometric functions and the law of cosines.
     * If the calculated angles are not a number (NaN), it adjusts the x and y coordinates accordingly.
     * Finally, it sends the calculated angles to the servo motors.
     *
     * @param isX A boolean value that indicates whether the x-axis position is being set. If true, the x-axis position is being set. If false, the y-axis position is being set.
     */
    private void setAngles(boolean isX) {
        /**
         * The x-axis position of the robot.
         * xx is initialized to the second x coordinate if there are two x coordinates.
         */
        double xx;
        /**
         * The y-axis position of the robot.
         * yy is initialized to the second y coordinate if there are two y coordinates.
         */
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

            /**
             * If the angle 'a' is less than 90 degrees, set it to 90 degrees.
             * This is a mechanical limitation of the robot.
             */
            if(a + Math.PI/2 < Math.PI/2) {
                a = 0;
            }
            /**
             * If the angle 'a' is greater than 180 degrees, set it to 180 degrees.
             * This is a mechanical limitation of the robot.
             */
            else if (a + Math.PI/2 > Math.PI) {
                a = Math.PI;
            }

            b = Math.PI-Math.acos((l2*l2+l1*l1-xx*xx-yy*yy)/(2*l1*l2));
            if(b > Math.PI/2) {
                b = Math.PI/2;
            }

            /**
             * If the calculated angles are not a number (NaN), adjust the x and y coordinates accordingly.
             * If the x-axis position is being set, adjust the y-axis position by incrementing or decrementing it.
             * If the y-axis position is being set, adjust the x-axis position by incrementing or decrementing it.
             * Incrementing or decrementing depends on the direction of the movement.
             *
             */

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
            }
            /**
             * If the calculated angles are valid, set the run boolean to false to exit the while loop.
             */
            else {
                run = false;
            }
        }

        /**
         * Add the x and y coordinates to the ArrayLists.
         * Adjust the x and y sliders to the new x and y coordinates.
         */
        x.add(xx);
        y.add(yy);

        xslider.adjustValue(xx);
        yslider.adjustValue(yy);

        //System.out.println("setAngles at: "+at);
        //System.out.println("setAngles a: "+a);
        //System.out.println("setAngles b: "+b);
        //System.out.println("setAngles c: "+((Math.PI / 2) - a - b));
        //System.out.println("setAngles d: "+d);

        /**
         * Calculate the angle 'c' of the robot.
         * Add an offset to control the pitch of the angle 'c' (wrist up and down).
         * Convert the angles 'a' and 'b' to degrees.
         * Send the calculated angles to the servo motors.
         */
        c = (Math.PI / 2) - a - b + d;

        cD = (int) Math.toDegrees(c)+90; // add offset to control pitch of angle c
        aD = (int) Math.toDegrees(a)+90;
        bD = (int) Math.toDegrees(b)+90;

        sendAngles();
        //System.out.println("x: "+xx+" y: "+yy);
    }
    /**
     * Sends the calculated angles to Websocket.
     */
    private void sendAngles() {
        smF.setAngle(aD);
        smF.sendData();

        smE.setAngle(bD);
        smE.sendData();

        smC.setAngle(cD);
        smC.sendData();

        //System.out.println("sendAngles rad: a: "+a+" b: "+b+" c: "+c+" d: "+d);
        //System.out.println("sendAngles deg: a: "+aD+" b: "+bD+" c: "+cD+" d: "+dD);
    }

    /**
     * Adjusts the offset of the wrist up and down position.
     * The offset is added to the angle 'c' of the robot.
     *
     * @param angle A double representing the angle by which to adjust the robot's wrist up and down position.
     */
    public void offset(double angle) {
        //System.out.println("angle : "+angle);
        d = angle * Math.PI/180;
        dD = (int) angle + 90;

        c = (Math.PI / 2) - a - b + d;
        cD = (int) Math.toDegrees(c)+90;

        //System.out.println("offset rad: a: "+a+" b: "+b+" c: "+c+" d: "+d);
        //System.out.println("offset deg: a: "+aD+" b: "+bD+" c: "+cD+" d: "+dD);

        sendAngles();
    }
}
