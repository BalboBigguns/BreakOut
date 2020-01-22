package main.java.breakoutgame.GameObjects;

import javafx.scene.layout.StackPane;
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
    Map map;
    Bat bat;
    Ball ball;
    StackPane rootPane;
    Canvas canvas;
    GraphicsContext gc;
    Timeline timeline;

    public GameManager(StackPane rootPane) {
        this.rootPane = rootPane;
        this.canvas = (Canvas)rootPane.getChildren().get(0);
        this.gc = canvas.getGraphicsContext2D();
        map = new Map(gc);

        if (!map.loadBlocks("src/main/resources/LevelMaps/level_1.txt")) {
            System.out.println("Level loading failed \nAborting game execution...");
            throw new RuntimeException("Level loading failed \nAborting game execution...");
        }

        bat = new Bat(map, canvas.getWidth() / 2 - Bat.INIT_WIDTH / 2 , canvas.getHeight() - Bat.INIT_HEIGHT * 2);
        ball = new Ball(map, canvas.getWidth() / 2 - Ball.INIT_BALL_SIZE / 2, 550, GameFXApp.DEBUG_MODE);

        //debugging stuff

    }

    public void startGameLoop() {
        int gameRate = GameFXApp.DEBUG_MODE ? 500 : 20;

        timeline = new Timeline(new KeyFrame(Duration.millis(gameRate), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                bat.update();
                ball.update();
                backgroundRefresh(gc);
                endGameCheck();

                // here goes all of the drawing methods
                gc.setFill(Color.GREEN);
                gc.setFont(new Font(20));
                gc.fillText(String.valueOf(map.lives), 10, 20);
                bat.draw();
                map.drawBlocks();
                ball.draw();

            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void checkDebugModeOn() {
        Logger logger = new Logger(timeline, 500);

        // List of traced objects
        logger.addObjectToTrack(ball);

        canvas.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
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

    // probably can be optimized
    private void backgroundRefresh(GraphicsContext gc) {
        //set background to black
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private void endGameCheck() {
        if (map.lives == 0) {
            timeline.stop();
            Text endGameText = new Text(100, 300, "GAME OVER");
            endGameText.setFont(new Font(50));
            endGameText.setFill(Color.GREEN);
            rootPane.getChildren().add(endGameText);
        }
        else if (map.blocks.isEmpty()) {
            timeline.stop();
            Text endGameText = new Text(100, 300, "YOU WON");
            endGameText.setFont(new Font(50));
            endGameText.setFill(Color.GREEN);
            rootPane.getChildren().add(endGameText);
        }
    }
}
