/**
 * @Author: Julien Navez
 * @Version: 1.0
 */

package panther.pantherii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * JavaFX App for the Panther HUD
 */

public class PantherInterface extends Application {
    /**
     * WebSocket connection instance
     */
    private static Websocket ws;
    /**
     * Log message
     */
    private static String log;

    /**
     * Start the WebSocket connection and display the HUD
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        ws = new Websocket();
        ws.start();

        display();
    }

    /**
     * Display the HUD
     * @throws IOException
     */
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

        /**
         * Close the HUD when the window is closed.
         * and exit the program because lots of TimerTasks are running and they need to be stopped.
         */
        stage.setOnCloseRequest(windowEvent -> {
            stage.close();

            System.exit(0);
        });
    }
    /**
     * Main method to launch the HUD
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
    /**
     * Send a log message to the console with a timestamp.
     * @param str
     */
    public static void sendLog(String str) {
        log = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS")) + " > " + str;

        System.out.println(log);
    }
    /**
     * Get the WebSocket connection instance.
     * @return WebSocket connection.
     */
    public static Websocket getWS() {
        return ws;
    }
}