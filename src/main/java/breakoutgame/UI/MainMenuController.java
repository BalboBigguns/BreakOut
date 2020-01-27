package main.java.breakoutgame.UI;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;


public class MainMenuController {
    public static EventHandler<ActionEvent> startTheGame;

    Pane root;

    public void init(Pane root) {
        this.root = root;
    }

    public void onStartButtonPress(Event evt) {
        startTheGame.handle(new ActionEvent());
    }

    public void onCreditsButtonPress(Event evt) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Credits.fxml"));
        try {
            Pane root = loader.load();
            ((Node)evt.getSource()).getScene().setRoot(root);
            ((CreditsController)loader.getController()).init(root);
        } catch (Exception e) {
            System.out.println("Fxml file loading error: " + e.getMessage());
        }
    }

    public void onQuitButtonPress(Event evt) {
        System.exit(0);
    }
}
