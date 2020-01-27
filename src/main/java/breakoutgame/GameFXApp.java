package main.java.breakoutgame;

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

        try {
            menu = new Scene(FXMLLoader.load(getClass().getResource("UI/MainMenu.fxml")));
        } catch (Exception e) {
            System.out.println("Fxml file loading error: " + e.getMessage());
        }
        primaryStage.setScene(menu);

        gameManager = new GameManager();

        MainMenuController.startTheGame = actionEvent -> {
            gameManager.startGameLoop();
            primaryStage.setScene(gameManager.getScene());
        };

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}