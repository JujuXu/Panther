package panther.pantherii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Main class of the Panther application.
 */
public class PantherInterface extends Application {

    /**
     * Starts the application.
     *
     * @param stage the primary stage of the application
     * @throws IOException if there is an error loading the FXML file
     */

    @Override
    public void start(Stage stage) throws IOException {
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

        stage.setOnCloseRequest(windowEvent -> {
            stage.close();
            try {
                display();
            } catch (IOException ignored) {

            }
        });
    }

    /**
     * Main method of the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}