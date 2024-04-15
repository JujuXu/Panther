package panther.pantherii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.TimerTask;

public class PantherInterface extends Application {
    private static Websocket ws;
    private static String log;
    @Override
    public void start(Stage stage) throws IOException {
        ws = new Websocket();
        ws.start();

        display();
    }

    private void display() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(PantherInterface.class.getResource("panther.fxml"));

        stage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        //Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.setFill(Color.TRANSPARENT);

        stage.setX(0);
        stage.setY(0);
        stage.setTitle("PANTHER HUD");
        stage.setScene(scene);
        stage.show();

        // for restarting HUD

        stage.setOnCloseRequest(windowEvent -> {
            stage.close();

            /*try {
                display();
            } catch (IOException ignored) {

            }*/
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
    public static void sendLog(String str) {
        log = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS")) + " > " + str;

        System.out.println(log);
    }

    public static Websocket getWS() {
        return ws;
    }
}