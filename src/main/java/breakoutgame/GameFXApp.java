package main.java.breakoutgame;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import main.java.breakoutgame.GameObjects.GameManager;
import main.java.breakoutgame.UI.MainMenuController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class GameFXApp extends Application {
    public static final int WINDOW_WIDTH = 640;
    public static final int WINDOW_HEIGHT = 660;

    public static final boolean DEBUG_MODE = false;  // turns on debug logs and allows for game pause and manual ball manipulations

    Scene menu;
    GameManager gameManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Breakout");
        primaryStage.setResizable(false);

        loadFonts();

        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/Icon/IconWhite.png")));
        } catch (Exception e) {
            System.out.println("IconWhite loading failed: " + e.getMessage());
        }

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

    private void loadFonts() {
        List<String> availableFonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());

        if (!availableFonts.contains("Bangers")) {
            try {
                if (Font.loadFont(getClass().getResourceAsStream("/main/resources/Fonts/Bangers-Regular.ttf"), 24) == null) {
                    System.out.println("Bangers-Regular loading failed!");
                }
            } catch (Exception e) {
                System.out.println("Bangers-Regular loading failed: " + e.getMessage());
            }
        }

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