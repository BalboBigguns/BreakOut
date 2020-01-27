package main.java.breakoutgame.UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreditsView {

    AnchorPane rootPane;
    FXMLLoader loader;

    public CreditsView() {

        loader = new FXMLLoader(getClass().getResource("Credits.fxml"));

        try {
            rootPane = loader.load();
        } catch (IOException e) {
            System.out.println("Fxml file loading error: " + e.getMessage());
        }

        loader.<CreditsController>getController().init(rootPane);
    }

    public CreditsController getController() {return loader.getController();}
}
