/**
 * @author: Julien Navez
 */

package panther.pantherii;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
/**
 * PantherController is a class that controls the behavior of the Panther robot.
 * It includes methods for handling keyboard events, sending data to the robot, and updating the user interface.
 */
public class PantherController {
    /**
     * The Websocket instance used for communication with the Panther robot.
     */
    Websocket ws = PantherInterface.getWS();
    /**
     * The step size for the sliders controlling the robot's movements.
     */
    private int sliderStep = 1;
    /**
     * The ServoMoteur instance representing the robot's clamp servo.
     */
    ServoMoteur smClamp;
    /**
     * The ServoMoteur instance representing the robot's wrist servo.
     */
    ServoMoteur smWrist;
    /**
     * The ServoMoteur instance representing the robot's C servo.
     */
    ServoMoteur smC;
    /**
     * The ServoMoteur instance representing the robot's D servo.
     */
    ServoMoteur smE;
    /**
     * The ServoMoteur instance representing the robot's E servo.
     */
    ServoMoteur smF;
    /**
     * The ServoMoteur instance representing the robot's rotation servo.
     */
    ServoMoteur smRot;

    /**
     * An ArrayList of ServoMoteur instances representing all the robot's servos.
     */
    ArrayList<ServoMoteur> servos = new ArrayList<>();
    /**
     * The Kinematic instance used for controlling the robot's movements.
     */
    Kinematic kinematic;
    /**
     * An array of dimensions for the robot's movements.
     */
    double[] dims = new double[3];

    /**
     * Initializes the state of the Panther robot and the user interface.
     *
     * This method sets up the initial dimensions for the robot's movements,
     * configures the maximum and minimum values for the sliders controlling the robot's movements,
     * sets up the initial style for the main anchor,
     * schedules tasks for reading data from the WebSocket, resending data to the servos, and checking the status of the robot,
     * initializes the ServoMoteur instances representing the robot's servos,
     * adjusts the initial values for the sliders,
     * and initializes the Kinematic instance used for controlling the robot's movements.
     *
     * This method is called once after all FXML components have been loaded and the controller instance has been created.
     */
    public void initialize() {
        /**
         * The dimensions for the robot's movements.
         * The dimensions are stored in an array of doubles.
         * The dimensions are used to set the maximum and minimum values for the sliders controlling the robot's movements.
         * The dimensions are also used to initialize the Kinematic instance used for controlling the robot's movements.
         * The dimensions are set to 105, 128, and 180.
         */
        dims[0] = 105;
        dims[1] = 128;
        dims[2] = 180;

        /**
         * The maximum and minimum values for the slider controlling the clamp servo.
         * The slider is used to control the clamp servo's angle.
         * The slider's maximum value is set to 100.
         * The slider's minimum value is set to 30.
         */
        sliderClamp.setMax(100);
        sliderClamp.setMin(30);

        sliderArmWrist.setMax(180);

        sliderArmUpDown.setMax(dims[0]+dims[1]);
        sliderArmUpDown.setMin(0);

        sliderArmFB.setMax(dims[0]+dims[1]);
        sliderArmFB.setMin(0);

        sliderArmRot.setMax(180);

        sliderSpeed.setMax(15);
        sliderSpeed.setMin(1);
        //sliderSpeed.adjustValue(sliderSpeed.getMin());

        sliderSpeed.adjustValue(8);

        sliderStep = (int) sliderSpeed.getValue();

        sliderWristPitch.setMax(90);
        sliderWristPitch.setMin(-90);
        sliderWristPitch.adjustValue(0);

        /**
         * The initial style for the main anchor.
         * The main anchor is the main container for the user interface components.
         * The main anchor's style is set to a light gray background with rounded corners.
         */

        mainAnchor.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1); -fx-background-radius: 10;");

        /**
         * The ArrayList of Text instances representing the proximity sensors.
         * The proximity sensors are used to detect obstacles in front of, to the left of, and to the right of the robot.
         * The proximity sensors are displayed as QuadCurve shapes in the user interface.
         * The proximity sensors are also displayed as Text instances showing the distance to the nearest obstacle.
         */
        ArrayList<Text> tPS = new ArrayList<>();
        tPS.add(textFrontProximity);
        tPS.add(textLeftProximity);
        tPS.add(textRightProximity);

        /**
         * The ArrayList of ArrayList of Text instances representing the proximity sensors.
         * The proximity sensors are used to detect obstacles in front of, to the left of, and to the right of the robot.
         * The proximity sensors are displayed as QuadCurve shapes in the user interface.
         * The proximity sensors are also displayed as Text instances showing the distance to the nearest obstacle.
         */
        ArrayList<ArrayList<Text>> aTexts = new ArrayList<>();
        aTexts.add(tPS);

        /**
         * The TimerTask for reading data from the WebSocket.
         * The ReadData task reads data from the WebSocket and updates the user interface components.
         * The ReadData task is scheduled to run every 1000 milliseconds.
         */
        new Timer().schedule(new ReadData(ws, aTexts,1000),1000);

        /**
         * The ServoMoteur instances representing the robot's servos.
         * The ServoMoteur instances are used to control the robot's movements.
         * The ServoMoteur instances are initialized with the servo names and initial angles.
         */
        smClamp = new ServoMoteur(servoA,0);
        smWrist = new ServoMoteur(servoB,7);
        smC = new ServoMoteur(servoC,90);
        smE = new ServoMoteur(servoE,160);
        smF = new ServoMoteur(servoF,45);
        smRot = new ServoMoteur(servoG,90);

        servos.add(smClamp);
        servos.add(smWrist);
        servos.add(smC);
        servos.add(smE);
        servos.add(smF);
        servos.add(smRot);

        /**
         * The TimerTask for resending data to the servos.
         * The ResendData task resends data to the servos and updates the user interface components.
         * The ResendData task is scheduled to run every 2000 milliseconds.
         */

        new Timer().schedule(new ResendData(servos,sliderSpeed, 2*1000),1000);

        /**
         * The initial values for the sliders controlling the robot's movements.
         * The sliders are used to control the robot's movements.
         * The sliders are initialized with the initial values for the robot's movements.
         */
        sliderClamp.adjustValue(smClamp.getResetValue());
        sliderArmWrist.adjustValue(smWrist.getResetValue());
        sliderArmRot.adjustValue(smRot.getResetValue());

        /**
         * The Kinematic instance used for controlling the robot's movements.
         * The Kinematic instance is initialized with the ServoMoteur instances representing the robot's servos, the dimensions for the robot's movements, and the sliders controlling the robot's movements.
         */

        kinematic = new Kinematic(servos,dims,sliderArmFB,sliderArmUpDown);

        /**
         * The TimerTask for checking the status of the robot.
         * The Status task checks the status of the robot and updates the user interface components.
         * The Status task is scheduled to run every 1000 milliseconds.
         */
        new Timer().schedule(new Status(circleStatus, textStatus,1000),1000);

        PantherInterface.sendLog("PantherII HUD initialized !");
    }

    /**
     * ToggleButton for moving the robot arm backward.
     */
    @FXML
    private ToggleButton armBackward;

    /**
     * ToggleButton for moving the robot arm down.
     */
    @FXML
    private ToggleButton armDown;

    /**
     * ToggleButton for moving the robot arm forward.
     */
    @FXML
    private ToggleButton armForward;

    /**
     * ToggleButton for moving the robot arm to the home position.
     */
    @FXML
    private ToggleButton armHome;

    /**
     * ToggleButton for rotating the robot arm to the left.
     */
    @FXML
    private ToggleButton armRotLeft;

    /**
     * ToggleButton for rotating the robot arm to the right.
     */
    @FXML
    private ToggleButton armRotRight;

    /**
     * ToggleButton for moving the robot arm up.
     */
    @FXML
    private ToggleButton armUp;

    /**
     * ToggleButton for moving the robot backward.
     */
    @FXML
    private ToggleButton backward;

    /**
     * Rectangle representing 100% battery level.
     */
    @FXML
    private Rectangle battery100;

    /**
     * Rectangle representing 33% battery level.
     */
    @FXML
    private Rectangle battery33;

    /**
     * Rectangle representing 66% battery level.
     */
    @FXML
    private Rectangle battery66;

    /**
     * Circle representing the status of the robot.
     */
    @FXML
    private Circle circleStatus;

    /**
     * ToggleButton for loosening the robot's clamp.
     */
    @FXML
    private ToggleButton clampLoosen;

    /**
     * ToggleButton for tightening the robot's clamp.
     */
    @FXML
    private ToggleButton clampTighten;

    /**
     * ToggleButton for moving the robot forward.
     */
    @FXML
    private ToggleButton forward;

    /**
     * QuadCurve representing the front proximity sensor.
     */
    @FXML
    private QuadCurve frontProximity;

    /**
     * ToggleButton for moving the robot to the left.
     */
    @FXML
    private ToggleButton left;

    /**
     * QuadCurve representing the left proximity sensor.
     */
    @FXML
    private QuadCurve leftProximity;

    /**
     * AnchorPane representing the main container for the user interface components.
     */
    @FXML
    private AnchorPane mainAnchor;

    /**
     * ToggleButton for moving the robot to the right.
     */
    @FXML
    private ToggleButton right;

    /**
     * QuadCurve representing the right proximity sensor.
     */
    @FXML
    private QuadCurve rightProximity;

    /**
     * Text representing the servo A.
     */
    @FXML
    private Text servoA;

    /**
     * Text representing the servo B.
     */
    @FXML
    private Text servoB;

    /**
     * Text representing the servo C.
     */
    @FXML
    private Text servoC;

    /**
     * Text representing the servo E.
     */
    @FXML
    private Text servoE;

    /**
     * Text representing the servo F.
     */
    @FXML
    private Text servoF;

    /**
     * Text representing the servo G.
     */
    @FXML
    private Text servoG;

    /**
     * ToggleButton for showing or hiding the servo angles.
     */
    @FXML
    private ToggleButton servosAngles;

    /**
     * HBox for displaying servo 1.
     */
    @FXML
    private HBox showServo1;

    /**
     * HBox for displaying servo 2.
     */
    @FXML
    private HBox showServo2;

    /**
     * HBox for displaying servo 3.
     */
    @FXML
    private HBox showServo3;

    /**
     * HBox for displaying servo 4.
     */
    @FXML
    private HBox showServo4;

    /**
     * HBox for displaying servo 5.
     */
    @FXML
    private HBox showServo5;

    /**
     * HBox for displaying servo 6.
     */
    @FXML
    private HBox showServo6;

    /**
     * Slider for controlling the robot's arm forward and backward movement.
     */
    @FXML
    private Slider sliderArmFB;

    /**
     * Slider for controlling the robot's arm rotation.
     */
    @FXML
    private Slider sliderArmRot;

    /**
     * Slider for controlling the robot's arm up and down movement.
     */
    @FXML
    private Slider sliderArmUpDown;

    /**
     * Slider for controlling the robot's wrist left and right movement.
     */
    @FXML
    private Slider sliderArmWrist;

    /**
     * Slider for controlling the robot's clamp angle.
     */
    @FXML
    private Slider sliderClamp;

    /**
     * Slider for controlling the speed of the robot.
     */
    @FXML
    private Slider sliderSpeed;

    /**
     * Slider for controlling the robot's wrist up and down movement.
     */
    @FXML
    private Slider sliderWristPitch;

    /**
     * ToggleButton for decreasing the speed of the robot.
     */
    @FXML
    private ToggleButton speedDown;

    /**
     * ToggleButton for increasing the speed of the robot.
     */
    @FXML
    private ToggleButton speedUp;

    /**
     * Text representing the battery level.
     */
    @FXML
    private Text textBattery;

    /**
     * Text representing the front proximity sensor's distance.
     */
    @FXML
    private Text textFrontProximity;

    /**
     * Text representing the left proximity sensor's distance.
     */
    @FXML
    private Text textLeftProximity;

    /**
     * Text representing the right proximity sensor's distance.
     */
    @FXML
    private Text textRightProximity;

    /**
     * Text representing the speed of the robot.
     */
    @FXML
    private Text textSpeed;

    /**
     * Text representing the status of the robot.
     */
    @FXML
    private Text textStatus;

    /**
     * ToggleButton for moving the robot's wrist down.
     */
    @FXML
    private ToggleButton wristDown;

    /**
     * ToggleButton for moving the robot's wrist to the left.
     */
    @FXML
    private ToggleButton wristLeft;

    /**
     * ToggleButton for moving the robot's wrist to the right.
     */
    @FXML
    private ToggleButton wristRight;

    /**
     * ToggleButton for moving the robot's wrist up.
     */
    @FXML
    private ToggleButton wristUp;

    /**
     * The keyboardPressed method handles keyboard press events and sends appropriate commands to the robot.
     *
     * @param event The KeyEvent representing the keyboard press.
     */
    @FXML
    private void keyboardPressed(KeyEvent event) throws IOException {
        /**
         * The KeyCode representing the key pressed.
         */
        KeyCode code = event.getCode();

        /**
         * ZQSD keys are used for moving the robot forward, backward, left, and right.
         */
        if (code == KeyCode.Z) {
            if (!forward.isSelected()) {
                forward.setSelected(true);
                ws.sendData("Z");
            }
        }

        if (code == KeyCode.Q) {
            if (!left.isSelected()) {
                left.setSelected(true);
                ws.sendData("Q");
            }
        }

        if (code == KeyCode.S) {
            if (!backward.isSelected()) {
                backward.setSelected(true);
                ws.sendData("S");
            }
        }

        if (code == KeyCode.D) {
            if (!right.isSelected()) {
                right.setSelected(true);
                ws.sendData("D");
            }
        }

        // ARM UP DOWN
        /**
         * NUMPAD1 and NUMPAD7 keys are used for moving the robot's arm up and down.
         * This slider controls the robot's arm up and down movement, using inverse kinematics as y-axis.
         */
        if (code == KeyCode.NUMPAD1) { // NUMPAD1 key
            armDown.setSelected(true); // Show the button as pressed
            double value = sliderArmUpDown.getValue(); // Get the current value of the slider
            if (value > sliderArmUpDown.getMin()) { // Check if the value is greater than the minimum value
                if(!(value - sliderStep < sliderArmWrist.getMin())) { // Check if the value minus the step is greater than the minimum value
                    sliderArmUpDown.adjustValue(value - sliderStep); // Adjust the value of the slider by subtracting the step
                } else { // If the value minus the step is less than the minimum value
                    sliderArmUpDown.adjustValue(sliderArmUpDown.getMin()); // Set the value of the slider to the minimum value
                }

                kinematic.setY(sliderArmUpDown.getValue()); // Set the y-axis position of the robot arm
            }
        }

        if (code == KeyCode.NUMPAD7) {
            armUp.setSelected(true);
            double value = sliderArmUpDown.getValue();
            if (value < sliderArmUpDown.getMax()) { // Check if the value is less than the maximum value
                if(!(value + sliderStep > sliderArmUpDown.getMax())) { // Check if the value plus the step is less than the maximum value
                    sliderArmUpDown.adjustValue(value + sliderStep); // Adjust the value of the slider by adding the step
                } else { // If the value plus the step is greater than the maximum value
                    sliderArmUpDown.adjustValue(sliderArmUpDown.getMax()); // Set the value of the slider to the maximum value
                }

                kinematic.setY(sliderArmUpDown.getValue()); // Set the y-axis position of the robot arm
            }
        }

        // ARM FORWARD BACKWARD
        /**
         * NUMPAD2 and NUMPAD8 keys are used for moving the robot's arm forward and backward.
         * This slider controls the robot's arm forward and backward movement, using inverse kinematics as x-axis.
         */
        if (code == KeyCode.NUMPAD2) {
            armBackward.setSelected(true);
            double value = sliderArmFB.getValue();
            if (value > sliderArmFB.getMin()) {
                if(!(value - sliderStep < sliderArmFB.getMin())) {
                    sliderArmFB.adjustValue(value - sliderStep);
                } else {
                    sliderArmFB.adjustValue(sliderArmFB.getMin());
                }

                kinematic.setX(sliderArmFB.getValue()); // Set the x-axis position of the robot arm
            }
        }

        if (code == KeyCode.NUMPAD8) {
            armForward.setSelected(true);
            double value = sliderArmFB.getValue();
            if (value < sliderArmFB.getMax()) {
                if(!(value + sliderStep > sliderArmFB.getMax())) {
                    sliderArmFB.adjustValue(value + sliderStep);
                } else {
                    sliderArmFB.adjustValue(sliderArmFB.getMax());
                }

                kinematic.setX(sliderArmFB.getValue()); // Set the x-axis position of the robot arm
            }
        }

        // WRIST ROLL
        /**
         * NUMPAD3 and NUMPAD9 keys are used for moving the robot's wrist left and right.
         * This slider controls the robot's wrist left and right movement.
         */
        if (code == KeyCode.NUMPAD3) {
            wristLeft.setSelected(true);
            double value = sliderArmWrist.getValue();
            if (value > sliderArmWrist.getMin()) {
                if(!(value - sliderStep < sliderArmWrist.getMin())) {
                    sliderArmWrist.adjustValue(value - sliderStep);
                } else {
                    sliderArmWrist.adjustValue(sliderArmWrist.getMin());
                }

                wristSend(); // Send the wrist angle to the robot
            }
        }

        if (code == KeyCode.NUMPAD9) {
            wristRight.setSelected(true);
            double value = sliderArmWrist.getValue();
            if (value < sliderArmWrist.getMax()) {
                if(!(value + sliderStep > sliderArmWrist.getMax())) {
                    sliderArmWrist.adjustValue(value + sliderStep);
                } else {
                    sliderArmWrist.adjustValue(sliderArmWrist.getMax());
                }

                wristSend(); // Send the wrist angle to the robot
            }
        }

        // ARM ROT
        /**
         * NUMPAD6 and NUMPAD4 keys are used for rotating the robot's arm left and right.
         * This slider controls the robot's arm rotation.
         */
        if (code == KeyCode.NUMPAD6) {
            armRotLeft.setSelected(true);
            double value = sliderArmRot.getValue();
            if (value > sliderArmRot.getMin()) {
                if(!(value - sliderStep < sliderArmWrist.getMin())) {
                    sliderArmRot.adjustValue(value - sliderStep);
                } else {
                    sliderArmRot.adjustValue(sliderArmRot.getMin());
                }

                //PantherInterface.sendLog("rot-");
                rotSend(); // Send the rotation angle to the robot
            }
        }

        if (code == KeyCode.NUMPAD4) {
            armRotRight.setSelected(true);
            double value = sliderArmRot.getValue();
            if (value < sliderArmRot.getMax()) {
                if(!(value + sliderStep > sliderArmRot.getMax())) {
                    sliderArmRot.adjustValue(value + sliderStep);
                } else {
                    sliderArmRot.adjustValue(sliderArmRot.getMax());
                }

                //PantherInterface.sendLog("rot+");
                rotSend(); // Send the rotation angle to the robot
            }
        }

        /**
         * NUMPAD5 key is used for moving the robot's arm to the home position.
         * It resets the angles of the servos controlling the robot's arm to their initial values.
         * It also resets the values of the sliders controlling the robot's arm to their initial values.
         * It sends the reset angles to the robot.
         */
        if (code == KeyCode.NUMPAD5) {
            if (!armHome.isSelected()) {
                armHome.setSelected(true);

                sliderClamp.adjustValue(smClamp.getResetValue());
                sliderArmWrist.adjustValue(smWrist.getResetValue());
                sliderArmUpDown.adjustValue(smC.getResetValue());
                sliderArmFB.adjustValue(10);
                sliderArmRot.adjustValue(smRot.getResetValue());
                sliderWristPitch.adjustValue(0);

                //smClamp.reset();
                smWrist.reset();
                smC.reset();
                smE.reset();
                smF.reset();
                smRot.reset();

                rotSend();
                //clampSend();
                wristSend();

                kinematic.home();
            }
        }

        /**
         * XW keys are used for increasing and decreasing the speed of the robot.
         */
        if (code == KeyCode.X) {
            speedDown.setSelected(true);
            double value = sliderSpeed.getValue();
            if (value > sliderSpeed.getMin()) {
                sliderSpeed.adjustValue(value - 1);
                sliderStep = (int) value;

                stepSend(); // Send the speed value to the robot
            }
        }

        if (code == KeyCode.W) {
            speedUp.setSelected(true);
            double value = sliderSpeed.getValue();
            if (value < sliderSpeed.getMax()) {
                sliderSpeed.adjustValue(value + 1);
                sliderStep = (int) value;

                stepSend(); // Send the speed value to the robot
            }
        }

        /**
         * EA keys are used for tightening and loosening the clamp of the robot.
         */
        if (code == KeyCode.E) {
            clampTighten.setSelected(true);
            double value = sliderClamp.getValue();
            if (value < sliderClamp.getMax()) {
                if(value + sliderStep > sliderClamp.getMax()) {
                    sliderClamp.adjustValue(sliderClamp.getMax());
                } else {
                    sliderClamp.adjustValue(value + sliderStep);
                }

                clampSend(); // Send the clamp angle to the robot
            }
        }

        if (code == KeyCode.A) {
            clampLoosen.setSelected(true);
            double value = sliderClamp.getValue();
            if (value > sliderClamp.getMin()) {
                if(value - sliderStep < sliderClamp.getMin()) {
                    sliderClamp.adjustValue(sliderClamp.getMin());
                } else {
                    sliderClamp.adjustValue(value - sliderStep);
                }

                clampSend(); // Send the clamp angle to the robot
            }
        }

        // WRIST UP DOWN
        /**
         * PM keys are used for moving the robot's wrist up and down.
         * This slider controls the robot's wrist up and down movement,
         * it adds an offset to the wrist pitch angle,
         * as last angle is controlled by inverse kinematics,
         * and it is needed to manually control the wrist pitch angle.
         */
        if (code == KeyCode.M) {
            wristDown.setSelected(true);
            double value = sliderWristPitch.getValue();
            if (value > sliderWristPitch.getMin()) {
                if(!(value - sliderStep < sliderWristPitch.getMin())) {
                    sliderWristPitch.adjustValue(value - sliderStep);
                } else {
                    sliderWristPitch.adjustValue(sliderWristPitch.getMin());
                }

                wristPitchSend(); // Send the wrist pitch angle to the robot
            }
        }

        if (code == KeyCode.P) {
            wristUp.setSelected(true);
            double value = sliderWristPitch.getValue();
            if (value < sliderWristPitch.getMax()) {
                if(!(value + sliderStep > sliderWristPitch.getMax())) {
                    sliderWristPitch.adjustValue(value + sliderStep);
                } else {
                    sliderWristPitch.adjustValue(sliderWristPitch.getMax());
                }

                wristPitchSend(); // Send the wrist pitch angle to the robot
            }
        }
    }

    /**
     * The wristPitchSend method sends the wrist pitch angle to the robot.
     * As the last angle is controlled by inverse kinematics, it is needed to manually control the wrist pitch angle.
     * It sends the offset value to the kinematic class that set the wrist pitch angle.
     */
    private void wristPitchSend() {
        kinematic.offset(Math.floor(sliderWristPitch.getValue()));
    }

    /**
     * The clampSend method sends the clamp angle to the robot.
     * The clamp angle is controlled by the sliderClamp slider and independent of the kinematic class.
     */
    private void clampSend() {
        smClamp.setAngle(Math.floor(sliderClamp.getValue()));
        smClamp.sendData();
    }

    /**
     * The stepSend method sends the speed value to the robot.
     * The speed value is controlled by the sliderSpeed slider and independent of the kinematic class.
     */
    private void stepSend() throws IOException {
        int speedPercent = (int) Math.ceil((100/(sliderSpeed.getMax()-sliderSpeed.getMin())*sliderSpeed.getValue()-100/sliderSpeed.getMax()-sliderSpeed.getMin()*1));
        ws.sendData("ST"+speedPercent);
    }

    /**
     * The wristSend method sends the wrist angle to the robot.
     * The wrist angle is controlled by the sliderArmWrist slider and independent of the kinematic class.
     */
    private void wristSend() {
        smWrist.setAngle(Math.floor(sliderArmWrist.getValue()));
        smWrist.sendData();
    }

    /**
     * The rotSend method sends the rotation angle to the robot.
     * The rotation angle is controlled by the sliderArmRot slider and independent of the kinematic class.
     */
    private void rotSend() {
        smRot.setAngle(Math.floor(sliderArmRot.getValue()));
        //PantherInterface.sendLog("rotSend");
        smRot.sendData();
    }

    /**
     * The keyboardReleased method handles keyboard release events and sends appropriate commands to the robot.
     * @param event The KeyEvent representing the keyboard release.
     */
    @FXML
    private void keyboardReleased(KeyEvent event) throws IOException {
        KeyCode code = event.getCode();

        if (code == KeyCode.Z) {
            forward.setSelected(false);
            ws.sendData("z");
        }

        if (code == KeyCode.Q) {
            left.setSelected(false);
            ws.sendData("q");
        }

        if (code == KeyCode.S) {
            backward.setSelected(false);
            ws.sendData("s");
        }

        if (code == KeyCode.D) {
            right.setSelected(false);
            ws.sendData("d");
        }

        if (code == KeyCode.NUMPAD1) {
            armDown.setSelected(false);
        }

        if (code == KeyCode.NUMPAD7) {
            armUp.setSelected(false);
        }

        if (code == KeyCode.NUMPAD2) {
            armBackward.setSelected(false);
        }

        if (code == KeyCode.NUMPAD8) {
            armForward.setSelected(false);
        }

        if (code == KeyCode.NUMPAD3) {
            wristLeft.setSelected(false);
        }

        if (code == KeyCode.NUMPAD9) {
            wristRight.setSelected(false);
        }

        if (code == KeyCode.P) {
            wristUp.setSelected(false);
        }

        if (code == KeyCode.M) {
            wristDown.setSelected(false);
        }

        if (code == KeyCode.NUMPAD4) {
            armRotRight.setSelected(false);
        }

        if (code == KeyCode.NUMPAD6) {
            armRotLeft.setSelected(false);
        }

        if (code == KeyCode.NUMPAD5) {
            armHome.setSelected(false);
        }

        if (code == KeyCode.X) {
            speedDown.setSelected(false);
        }

        if (code == KeyCode.W) {
            speedUp.setSelected(false);
        }

        if (code == KeyCode.E) {
            clampTighten.setSelected(false);
        }

        if (code == KeyCode.A) {
            clampLoosen.setSelected(false);
        }
    }
    /**
     * The servosAnglesPressed method toggles the visibility of the servo angle displays in the user interface.
     */
    @FXML
    private void servosAnglesPressed() {
        if(servosAngles.isSelected()) {
            showServo1.setVisible(true);
            showServo2.setVisible(true);
            showServo3.setVisible(true);
            showServo4.setVisible(true);
            showServo5.setVisible(true);
            showServo6.setVisible(true);
        } else {
            showServo1.setVisible(false);
            showServo2.setVisible(false);
            showServo3.setVisible(false);
            showServo4.setVisible(false);
            showServo5.setVisible(false);
            showServo6.setVisible(false);
        }
    }
}
