package main.java.breakoutgame.GameObjects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import main.java.breakoutgame.Utils.Logger;

import javafx.scene.canvas.Canvas;
import main.java.breakoutgame.GameFXApp;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameManager {
    public static EventHandler<ActionEvent> endTheGame;

    Map map;
    Bat bat;
    Ball ball;
    StackPane rootPane;
    Scene gameScene;
    Canvas canvas;
    GraphicsContext gc;
    Timeline timeline;
    Text pauseText;
    ImageView pauseButton;

    public GameManager() {
        this.canvas = new Canvas(GameFXApp.WINDOW_WIDTH, GameFXApp.WINDOW_HEIGHT);
        this.rootPane = new StackPane(canvas);
        this.gameScene = new Scene(rootPane);
        this.gc = canvas.getGraphicsContext2D();
        map = new Map(gc);

        if (!map.loadBlocks("src/main/resources/LevelMaps/level_1.txt")) {
            System.out.println("Level loading failed \nAborting game execution...");
            throw new RuntimeException("Level loading failed \nAborting game execution...");
        }

        gameScene.getStylesheets().addAll(getClass().getResource("../UI/styleButtons.css").toExternalForm());

        pauseButton = new ImageView();
        pauseButton.setImage(new Image(getClass().getResourceAsStream("../../../resources/Icon/pauseImg.png"), 36, 36, false, false));
        StackPane.setAlignment(pauseButton, Pos.TOP_RIGHT);
        rootPane.getChildren().add(pauseButton);
        pauseButton.setOnMouseClicked(mouseEvent -> {
            System.out.println(timeline.getStatus().toString());
            if (timeline.getStatus() == Animation.Status.PAUSED) {
                resumeGameLoop();
            }
            else if (timeline.getStatus() == Animation.Status.RUNNING){
                pauseGameLoop();
            }
        });

        bat = new Bat(map, canvas.getWidth() / 2 - Bat.INIT_WIDTH / 2 , canvas.getHeight() - Bat.INIT_HEIGHT * 2);
        ball = new Ball(map, canvas.getWidth() / 2 - Ball.INIT_BALL_SIZE / 2, 600);

        timeline = new Timeline(new KeyFrame(Duration.millis(20), new EventHandler<>() {
            @Override
            public void handle(ActionEvent t) {
                bat.update();
                ball.update();
                backgroundRefresh(gc);
                endGameCheck();

                // here goes all of the drawing methods
                drawHud();
                bat.draw();
                map.drawBlocks();
                ball.draw();

            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        //debugging stuff
        if (GameFXApp.DEBUG_MODE) {
            checkDebugModeOn();
        }
    }

    public Scene getScene() {return gameScene;}

    public void startGameLoop() {
        timeline.play();
    }

    public void resumeGameLoop() {
        rootPane.getChildren().remove(pauseText);
        pauseButton.setImage(new Image(getClass().getResourceAsStream("../../../resources/Icon/pauseImg.png"), 36, 36, false, false));

        timeline.play();
    }

    public void pauseGameLoop() {
        timeline.pause();
        pauseText = new Text(100, 300, "GAME PAUSED");
        pauseText.setFill(Color.web("#E74C3C"));
        pauseText.setFont(Font.font("Agency FB", FontWeight.BOLD, 84));
        rootPane.getChildren().add(pauseText);

        pauseButton.setImage(new Image(getClass().getResourceAsStream("../../../resources/Icon/resumeImg.png"), 36, 36, false, false));
    }

    private void checkDebugModeOn() {
        Logger logger = new Logger(timeline, 500);

        // List of traced objects
        logger.addObjectToTrack(ball);

        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            KeyCode key = keyEvent.getCode();

            if (key == KeyCode.SPACE) {
                if (timeline.getStatus() == Animation.Status.PAUSED){
                    timeline.play();
                }
                else {
                    timeline.pause();
                }
            }
        });

    }

    // game hud
    private void drawHud() {
        //gc.setFill(Color.rgb(0, 204, 204));
        gc.setFill(Color.web("#E67E22"));
        gc.fillRect(0 ,0, GameFXApp.WINDOW_WIDTH, Map.MAP_TOP_BOUNDARY);

        gc.setFill(Color.web("#9b59b6"));
        gc.setFont(Font.font("Agency FB", FontWeight.BOLD, 24));
        gc.fillText(String.valueOf(map.lives) + " UP", 18, 26);
        //gc.drawImage();
    }

    // probably can be optimized
    private void backgroundRefresh(GraphicsContext gc) {
        //set background to black
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private void endGameCheck() {
        if (map.lives == 0 || map.blocks.isEmpty()) {
            timeline.stop();
            Text endGameText = new Text(100, 300, map.lives == 0 ? "GAME OVER": "YOU WON");
            endGameText.setFont(Font.font("Agency FB", FontWeight.BOLD, 84));
            endGameText.setFill(map.lives == 0 ? Color.web("#E74C3C") : Color.web("#B9FAF8"));
            VBox endGame = new VBox();
            endGame.setStyle("-fx-background-color: rgb(66,66,66, 0.5)");
            Button goBack = new Button("MENU");
            goBack.setPadding(new Insets(5, 20, 5, 20));
            goBack.setOnMouseClicked(mouseEvent -> {
                endTheGame.handle(new ActionEvent());
            });
            endGame.getChildren().addAll(endGameText, goBack);
            endGame.setAlignment(Pos.CENTER);
            rootPane.getChildren().add(endGame);
        }
    }
}
