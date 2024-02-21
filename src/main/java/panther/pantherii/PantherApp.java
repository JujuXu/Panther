package panther.pantherii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class of the Panther application.
 */
public class PantherApp extends Application {

    /**
     * Starts the application.
     *
     * @param stage the primary stage of the application
     * @throws IOException if there is an error loading the FXML file
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PantherApp.class.getResource("panther.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("PANTHER HUD");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method of the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Writes a message to the console.
     *
     * @param str the message to write
     */
    public static void sendLog(String str) {
        System.out.print(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS")) + " > ");
        System.out.println(str);
    }
}