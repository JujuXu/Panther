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
 * This class is the controller for the JavaFX GUI of the Panther robot. It is responsible for
 * communicating with the robot and updating the GUI elements with sensor readings and user input.
 */
public class PantherController {

    Websocket ws = PantherInterface.getWS();

    private int sliderStep = 1;

    ServoMoteur smClamp;
    ServoMoteur smWrist;
    ServoMoteur smC;
    ServoMoteur smD;
    ServoMoteur smE;
    ServoMoteur smRot;
    ArrayList<ServoMoteur> servos = new ArrayList<>();
    Kinematic kinematic;

    double[] dims = new double[3];

    /**
     * This method is called when the JavaFX application initializes.
     * It sets up the GUI elements and initializes the communication with the robot.
     */
    public void initialize() {
        dims[0] = 105;
        dims[1] = 128;
        dims[2] = 180;

        // set the maximum value of the sliders
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

        mainAnchor.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1); -fx-background-radius: 10;");

        ArrayList<Text> tPS = new ArrayList<>();
        tPS.add(textFrontProximity);
        tPS.add(textLeftProximity);
        tPS.add(textRightProximity);

        ArrayList<ArrayList<Text>> aTexts = new ArrayList<>();
        aTexts.add(tPS);

        new Timer().schedule(new ReadData(ws, aTexts,1000),1000);

        smClamp = new ServoMoteur(servoA,0);
        smWrist = new ServoMoteur(servoB,7);
        smC = new ServoMoteur(servoC,90);
        smD = new ServoMoteur(servoE,160);
        smE = new ServoMoteur(servoF,45);
        smRot = new ServoMoteur(servoG,90);

        servos.add(smClamp);
        servos.add(smWrist);
        servos.add(smC);
        servos.add(smD);
        servos.add(smE);
        servos.add(smRot);

        new Timer().schedule(new ResendData(servos,sliderSpeed, 2*1000),1000);

        sliderClamp.adjustValue(smClamp.getResetValue());
        sliderArmWrist.adjustValue(smWrist.getResetValue());
        sliderArmRot.adjustValue(smRot.getResetValue());

        kinematic = new Kinematic(servos,dims,sliderArmFB,sliderArmUpDown);

        new Timer().schedule(new Status(circleStatus, textStatus,1000),1000);

        PantherInterface.sendLog("PantherII HUD initialized !");
    }

    @FXML
    private ToggleButton armBackward;

    @FXML
    private ToggleButton armDown;

    @FXML
    private ToggleButton armForward;

    @FXML
    private ToggleButton armHome;

    @FXML
    private ToggleButton armRotLeft;

    @FXML
    private ToggleButton armRotRight;

    @FXML
    private ToggleButton armUp;

    @FXML
    private ToggleButton backward;

    @FXML
    private Rectangle battery100;

    @FXML
    private Rectangle battery33;

    @FXML
    private Rectangle battery66;

    @FXML
    private Circle circleStatus;

    @FXML
    private ToggleButton clampLoosen;

    @FXML
    private ToggleButton clampTighten;

    @FXML
    private ToggleButton forward;

    @FXML
    private QuadCurve frontProximity;

    @FXML
    private ToggleButton left;

    @FXML
    private QuadCurve leftProximity;

    @FXML
    private AnchorPane mainAnchor;

    @FXML
    private ToggleButton right;

    @FXML
    private QuadCurve rightProximity;

    @FXML
    private Text servoA;

    @FXML
    private Text servoB;

    @FXML
    private Text servoC;

    @FXML
    private Text servoE;

    @FXML
    private Text servoF;

    @FXML
    private Text servoG;

    @FXML
    private ToggleButton servosAngles;

    @FXML
    private HBox showServo1;

    @FXML
    private HBox showServo2;

    @FXML
    private HBox showServo3;

    @FXML
    private HBox showServo4;

    @FXML
    private HBox showServo5;

    @FXML
    private HBox showServo6;

    @FXML
    private Slider sliderArmFB;

    @FXML
    private Slider sliderArmRot;

    @FXML
    private Slider sliderArmUpDown;

    @FXML
    private Slider sliderArmWrist;

    @FXML
    private Slider sliderClamp;

    @FXML
    private Slider sliderSpeed;

    @FXML
    private Slider sliderWristPitch;

    @FXML
    private ToggleButton speedDown;

    @FXML
    private ToggleButton speedUp;

    @FXML
    private Text textBattery;

    @FXML
    private Text textFrontProximity;

    @FXML
    private Text textLeftProximity;

    @FXML
    private Text textRightProximity;

    @FXML
    private Text textSpeed;

    @FXML
    private Text textStatus;

    @FXML
    private ToggleButton wristDown;

    @FXML
    private ToggleButton wristLeft;

    @FXML
    private ToggleButton wristRight;

    @FXML
    private ToggleButton wristUp;

    /**
     * This method is called when a key is pressed on the keyboard.
     * It sets the state of the buttons and sliders to reflect the current keyboard input.
     *
     * @param event the KeyEvent that triggered the method call
     */
    @FXML
    private void keyboardPressed(KeyEvent event) throws IOException {
        KeyCode code = event.getCode();

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

        if (code == KeyCode.NUMPAD1) {
            armDown.setSelected(true);
            double value = sliderArmUpDown.getValue();
            if (value > sliderArmUpDown.getMin()) {
                if(!(value - sliderStep < sliderArmWrist.getMin())) {
                    sliderArmUpDown.adjustValue(value - sliderStep);
                } else {
                    sliderArmUpDown.adjustValue(sliderArmUpDown.getMin());
                }

                kinematic.setY(sliderArmUpDown.getValue());
            }
        }

        if (code == KeyCode.NUMPAD7) {
            armUp.setSelected(true);
            double value = sliderArmUpDown.getValue();
            if (value < sliderArmUpDown.getMax()) {
                if(!(value + sliderStep > sliderArmUpDown.getMax())) {
                    sliderArmUpDown.adjustValue(value + sliderStep);
                } else {
                    sliderArmUpDown.adjustValue(sliderArmUpDown.getMax());
                }

                kinematic.setY(sliderArmUpDown.getValue());
            }
        }

        // ARM FORWARD BACKWARD

        if (code == KeyCode.NUMPAD2) {
            armBackward.setSelected(true);
            double value = sliderArmFB.getValue();
            if (value > sliderArmFB.getMin()) {
                if(!(value - sliderStep < sliderArmFB.getMin())) {
                    sliderArmFB.adjustValue(value - sliderStep);
                } else {
                    sliderArmFB.adjustValue(sliderArmFB.getMin());
                }

                kinematic.setX(sliderArmFB.getValue());
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

                kinematic.setX(sliderArmFB.getValue());
            }
        }

        // WRIST ROLL

        if (code == KeyCode.NUMPAD3) {
            wristLeft.setSelected(true);
            double value = sliderArmWrist.getValue();
            if (value > sliderArmWrist.getMin()) {
                if(!(value - sliderStep < sliderArmWrist.getMin())) {
                    sliderArmWrist.adjustValue(value - sliderStep);
                } else {
                    sliderArmWrist.adjustValue(sliderArmWrist.getMin());
                }

                wristSend();
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

                wristSend();
            }
        }

        // ARM ROT

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
                rotSend();
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
                rotSend();
            }
        }

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
                smD.reset();
                smE.reset();
                smRot.reset();

                rotSend();
                //clampSend();
                wristSend();

                kinematic.home();
            }
        }

        if (code == KeyCode.X) {
            speedDown.setSelected(true);
            double value = sliderSpeed.getValue();
            if (value > sliderSpeed.getMin()) {
                sliderSpeed.adjustValue(value - 1);
                sliderStep = (int) value;

                stepSend();
            }
        }

        if (code == KeyCode.W) {
            speedUp.setSelected(true);
            double value = sliderSpeed.getValue();
            if (value < sliderSpeed.getMax()) {
                sliderSpeed.adjustValue(value + 1);
                sliderStep = (int) value;

                stepSend();
            }
        }

        if (code == KeyCode.E) {
            clampTighten.setSelected(true);
            double value = sliderClamp.getValue();
            if (value < sliderClamp.getMax()) {
                if(value + sliderStep > sliderClamp.getMax()) {
                    sliderClamp.adjustValue(sliderClamp.getMax());
                } else {
                    sliderClamp.adjustValue(value + sliderStep);
                }

                clampSend();
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

                clampSend();
            }
        }

        // WRIST UP DOWN

        if (code == KeyCode.M) {
            wristDown.setSelected(true);
            double value = sliderWristPitch.getValue();
            if (value > sliderWristPitch.getMin()) {
                if(!(value - sliderStep < sliderWristPitch.getMin())) {
                    sliderWristPitch.adjustValue(value - sliderStep);
                } else {
                    sliderWristPitch.adjustValue(sliderWristPitch.getMin());
                }

                wristPitchSend();
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

                wristPitchSend();
            }
        }
    }

    private void wristPitchSend() {
        kinematic.offset(Math.floor(sliderWristPitch.getValue()));
    }

    private void clampSend() {
        smClamp.setAngle(Math.floor(sliderClamp.getValue()));
        smClamp.sendData();
    }

    private void stepSend() throws IOException {
        int speedPercent = (int) Math.ceil((100/(sliderSpeed.getMax()-sliderSpeed.getMin())*sliderSpeed.getValue()-100/sliderSpeed.getMax()-sliderSpeed.getMin()*1));
        ws.sendData("ST"+speedPercent);
    }

    private void wristSend() {
        smWrist.setAngle(Math.floor(sliderArmWrist.getValue()));
        smWrist.sendData();
    }

    private void rotSend() {
        smRot.setAngle(Math.floor(sliderArmRot.getValue()));
        //PantherInterface.sendLog("rotSend");
        smRot.sendData();
    }

    /**
     * This method is called when a key is released on the keyboard.
     * It sets the state of the buttons and sliders to reflect the current keyboard input.
     *
     * @param event the KeyEvent that triggered the method call
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
     * This method is called when the "Servo Angles" button is pressed.
     * It toggles the visibility of the servo angle boxes.
     *
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
