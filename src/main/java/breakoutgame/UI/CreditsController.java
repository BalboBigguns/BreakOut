package main.java.breakoutgame.UI;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;


public class CreditsController {

    Pane root;

    public void init(Pane root) {
        this.root = root;
    }

    public void onBackButtonPress(Event evt) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        try {
            Pane root = loader.load();
            ((Node)evt.getSource()).getScene().setRoot(root);
            ((MainMenuController)loader.getController()).init(root);
        } catch (Exception e) {
            System.out.println("Fxml file loading error: " + e.getMessage());
        }
    }
}
