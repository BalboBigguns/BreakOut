module breakout {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens com.breakoutgame to javafx.fxml;
    opens com.breakoutgame.UI to javafx.fxml;

    exports com.breakoutgame;
}