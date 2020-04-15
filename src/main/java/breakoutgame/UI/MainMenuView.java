package main.java.breakoutgame.UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class MainMenuView {
    VBox rootPane;
    FXMLLoader loader;

    public MainMenuView() {

        loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));

        try {
            this.rootPane = loader.load();
        } catch (Exception e) {
            System.out.println("Fxml file loading error: " + e.getMessage());
        }

        loader.<MainMenuController>getController().init(rootPane);
    }

    public MainMenuController getController() {return loader.getController();}
}
