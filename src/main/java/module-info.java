module panther.pantherii {
    requires javafx.controls;
    requires javafx.fxml;


    opens panther.pantherii to javafx.fxml;
    exports panther.pantherii;
}