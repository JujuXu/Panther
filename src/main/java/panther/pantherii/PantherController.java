package panther.pantherii;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Timer;

/**
 * This class is the controller for the JavaFX GUI of the Panther robot. It is responsible for
 * communicating with the robot and updating the GUI elements with sensor readings and user input.
 */
public class PantherController {

    Websocket ws = Main.getWS();

    private int sliderStep = 1;

    ServoMoteur smClamp;
    ServoMoteur smWrist;
    ServoMoteur smC;
    ServoMoteur smD;
    ServoMoteur smE;
    ServoMoteur smRot;
    ArrayList<ServoMoteur> servos = new ArrayList<>();

    /**
     * This method is called when the JavaFX application initializes.
     * It sets up the GUI elements and initializes the communication with the robot.
     */
    public void initialize() {
        // set the maximum value of the sliders
        sliderClamp.setMax(100);
        sliderClamp.setMin(30);

        sliderArmWrist.setMax(180);

        sliderArmUpDown.setMax(180);

        sliderArmFB.setMax(20);

        sliderArmRot.setMax(180);

        sliderSpeed.setMax(20);
        sliderSpeed.setMin(1);
        sliderSpeed.adjustValue(sliderStep);

        mainAnchor.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1); -fx-background-radius: 10;");

        ArrayList<Text> tPS = new ArrayList<>();
        tPS.add(textFrontProximity);
        tPS.add(textLeftProximity);
        tPS.add(textRightProximity);

        ArrayList<ArrayList<Text>> aTexts = new ArrayList<>();
        aTexts.add(tPS);

        new Timer().schedule(new Data(ws, aTexts,1000),1000);

        smClamp = new ServoMoteur(servoA,ws,90);
        smWrist = new ServoMoteur(servoB,ws,7);
        smC = new ServoMoteur(servoC,ws,90);
        smD = new ServoMoteur(servoD,ws,170);
        smE = new ServoMoteur(servoE,ws,25);
        smRot = new ServoMoteur(servoF,ws,90);

        servos.add(smClamp);
        servos.add(smWrist);
        servos.add(smC);
        servos.add(smD);
        servos.add(smE);
        servos.add(smRot);

        new Timer().schedule(new ServosData(servos,100),100);

        sliderClamp.adjustValue(smClamp.getResetValue());
        sliderArmWrist.adjustValue(smWrist.getResetValue());
        sliderArmRot.adjustValue(smRot.getResetValue());

        Main.sendLog("PantherII HUD initialized !");
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
    private ToggleButton armWristDown;

    @FXML
    private ToggleButton armWristUp;

    @FXML
    private ToggleButton backward;

    @FXML
    private Rectangle battery100;

    @FXML
    private Rectangle battery33;

    @FXML
    private Rectangle battery66;

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

    @FXML Slider sliderSpeed;

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
    private Text servoA;

    @FXML
    private Text servoB;

    @FXML
    private Text servoC;

    @FXML
    private Text servoD;

    @FXML
    private Text servoE;

    @FXML
    private Text servoF;

    /**
     * This method is called when a key is pressed on the keyboard.
     * It sets the state of the buttons and sliders to reflect the current keyboard input.
     *
     * @param event the KeyEvent that triggered the method call
     */
    @FXML
    private void keyboardPressed(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.Z) {
            if (!forward.isSelected()) {
                forward.setSelected(true);
            }
        }

        if (code == KeyCode.Q) {
            if (!left.isSelected()) {
                left.setSelected(true);
            }
        }

        if (code == KeyCode.S) {
            if (!backward.isSelected()) {
                backward.setSelected(true);
            }
        }

        if (code == KeyCode.D) {
            if (!right.isSelected()) {
                right.setSelected(true);
            }
        }

        if (code == KeyCode.NUMPAD1) {
            armDown.setSelected(true);
            double value = sliderArmUpDown.getValue();
            if (value > sliderArmUpDown.getMin()) {
                sliderArmUpDown.adjustValue(value - 1);
            }
        }

        if (code == KeyCode.NUMPAD2) {
            armWristDown.setSelected(true);
            double value = sliderArmWrist.getValue();
            if (value > sliderArmWrist.getMin()) {
                sliderArmWrist.adjustValue(value - 1);
            }
        }

        if (code == KeyCode.NUMPAD3) {
            armBackward.setSelected(true);
            double value = sliderArmFB.getValue();
            if (value > sliderArmFB.getMin()) {
                sliderArmFB.adjustValue(value - 1);
            }
        }

        if (code == KeyCode.NUMPAD4) {
            armRotLeft.setSelected(true);
            double value = sliderArmRot.getValue();
            if (value > sliderArmRot.getMin()) {
                sliderArmRot.adjustValue(value - 1);
            }
        }

        if (code == KeyCode.NUMPAD5) {
            if (!armHome.isSelected()) {
                armHome.setSelected(true);

                sliderArmWrist.adjustValue(smWrist.getResetValue());
                sliderArmUpDown.adjustValue(smC.getResetValue());
                sliderArmFB.adjustValue(10);
                sliderArmRot.adjustValue(smRot.getResetValue());

                smWrist.reset();
                smC.reset();
                smD.reset();
                smE.reset();
                smRot.reset();
            }
        }

        if (code == KeyCode.NUMPAD6) {
            armRotRight.setSelected(true);
            double value = sliderArmRot.getValue();
            if (value < sliderArmRot.getMax()) {
                sliderArmRot.adjustValue(value + 1);
            }
        }

        if (code == KeyCode.NUMPAD7) {
            armUp.setSelected(true);
            double value = sliderArmUpDown.getValue();
            if (value < sliderArmUpDown.getMax()) {
                sliderArmUpDown.adjustValue(value + 1);
            }
        }

        if (code == KeyCode.NUMPAD8) {
            armWristUp.setSelected(true);
            double value = sliderArmWrist.getValue();
            if (value < sliderArmWrist.getMax()) {
                sliderArmWrist.adjustValue(value + 1);
            }
        }

        if (code == KeyCode.NUMPAD9) {
            armForward.setSelected(true);
            double value = sliderArmFB.getValue();
            if (value < sliderArmFB.getMax()) {
                sliderArmFB.adjustValue(value + 1);
            }
        }

        if (code == KeyCode.X) {
            speedDown.setSelected(true);
            double value = sliderSpeed.getValue();
            if (value > sliderSpeed.getMin()) {
                sliderSpeed.adjustValue(value - 1);
                sliderStep = (int) value;
            }
        }

        if (code == KeyCode.W) {
            speedUp.setSelected(true);
            double value = sliderSpeed.getValue();
            if (value < sliderSpeed.getMax()) {
                sliderSpeed.adjustValue(value + 1);
                sliderStep = (int) value;
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

        smRot.setAngle(Math.floor(sliderArmRot.getValue()));
        smWrist.setAngle(Math.floor(sliderArmWrist.getValue()));

    }

    private void clampSend() {
        smClamp.setAngle(Math.floor(sliderClamp.getValue()));
        smClamp.sendData();
    }

    /**
     * This method is called when a key is released on the keyboard.
     * It sets the state of the buttons and sliders to reflect the current keyboard input.
     *
     * @param event the KeyEvent that triggered the method call
     */
    @FXML
    private void keyboardReleased(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.Z) {
            forward.setSelected(false);
        }

        if (code == KeyCode.Q) {
            left.setSelected(false);
        }

        if (code == KeyCode.S) {
            backward.setSelected(false);
        }

        if (code == KeyCode.D) {
            right.setSelected(false);
        }

        if (code == KeyCode.NUMPAD1) {
            armDown.setSelected(false);
        }

        if (code == KeyCode.NUMPAD2) {
            armWristDown.setSelected(false);
        }

        if (code == KeyCode.NUMPAD3) {
            armBackward.setSelected(false);
        }

        if (code == KeyCode.NUMPAD4) {
            armRotLeft.setSelected(false);
        }

        if (code == KeyCode.NUMPAD5) {
            armHome.setSelected(false);
        }

        if (code == KeyCode.NUMPAD6) {
            armRotRight.setSelected(false);
        }

        if (code == KeyCode.NUMPAD7) {
            armUp.setSelected(false);
        }

        if (code == KeyCode.NUMPAD8) {
            armWristUp.setSelected(false);
        }

        if (code == KeyCode.NUMPAD9) {
            armForward.setSelected(false);
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
     * @param event the ActionEvent that triggered the method call
     */
    @FXML
    private void servosAnglesPressed(ActionEvent event) {
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
