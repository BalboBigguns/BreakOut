package main.java.breakoutgame;

import javafx.animation.Animation;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import main.java.breakoutgame.GameObjects.GameManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// drawing inmports
import javafx.scene.canvas.Canvas;


public class GameFXApp extends Application {
    public static final int WINDOW_WIDTH = 640;
    public static final int WINDOW_HEIGHT = 700;

    public static final boolean DEBUG_MODE = true;  // turns on debug logs and allows for game pause and manual ball manipulations

    StackPane root;
    Canvas canvas;
    Scene gameScene, menuScene;
    GameManager gameManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Breakout");

        root = new StackPane();

        canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
        
        gameScene = new Scene(root);
        primaryStage.setScene(gameScene);


        gameManager = new GameManager(gameScene);
        gameManager.startGameLoop();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}