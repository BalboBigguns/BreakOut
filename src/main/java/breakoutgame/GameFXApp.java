package main.java.breakoutgame;

import javafx.scene.image.Image;
import main.java.breakoutgame.GameObjects.GameManager;
import main.java.breakoutgame.UI.MainMenuController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.stage.Stage;


public class GameFXApp extends Application {
    public static final int WINDOW_WIDTH = 640;
    public static final int WINDOW_HEIGHT = 700;

    public static final boolean DEBUG_MODE = true;  // turns on debug logs and allows for game pause and manual ball manipulations

    Scene menu;
    GameManager gameManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Breakout");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/Icon/IconWhite.png" )));

        loadMenu(primaryStage);

        MainMenuController.startTheGame = actionEvent -> {
            gameManager = new GameManager();
            gameManager.startGameLoop();
            primaryStage.setScene(gameManager.getScene());
        };

        GameManager.endTheGame = actionEvent -> {
            loadMenu(primaryStage);
        };

        primaryStage.show();
    }

    private void loadMenu(Stage primaryStage) {
        try {
            menu = new Scene(FXMLLoader.load(getClass().getResource("UI/MainMenu.fxml")));
        } catch (Exception e) {
            System.out.println("Fxml file loading error: " + e.getMessage());
        }
        primaryStage.setScene(menu);
    }

    public static void main(String[] args) {
        launch(args);
    }

}