package main.java.breakoutgame.GameObjects;

import main.java.breakoutgame.GameHelpers.Vector2D;

import main.java.breakoutgame.GameFXApp;
import main.java.breakoutgame.GameHelpers.DynamicGameObject;
import main.java.breakoutgame.GameHelpers.Collision;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javafx.scene.paint.Color;

// extra for debugging
    import javafx.scene.Scene;
    import javafx.scene.input.MouseEvent;

import java.util.Random;
//

public class Ball extends DynamicGameObject {
    public static final int INIT_BALL_SIZE = 16;
    public static final int INIT_BALL_SPEED = 7;
    public int starting = 0;
    private Vector2D lastMousePosition;

    private Color currentColor;
    private Random randGen;

    public Ball(Map map, double InitX, double InitY) {
        super(map, InitX, InitY, INIT_BALL_SIZE, INIT_BALL_SIZE);
        currentColor = Color.YELLOW;
        randGen = new Random();
        if (GameFXApp.DEBUG_MODE) {
            mouseControl(map.gc.getCanvas().getScene());
        }
        reset();
    }

    public void reset() {
        super.reset();
        resetAnimation();
    }
    
    @Override
    public void draw() {
        map.gc.setFill(currentColor);
        map.gc.fillOval(getLeft(), getTop(), getWidth(), getHeight());
    }

    @Override
    public void onCollision(Collision collision) {
        switch(collision.getType()) {
            case NONE:
                break;
            case RIGHT_BOUND:
            case LEFT_BOUND:
                setVelocity(getVelocity().scale(-1, 1));
                break;
            case TOP_BOUND:
                setVelocity(getVelocity().scale(1, -1));
                break;
            case BOTTOM_BOUND:
                map.lives--;
                reset();
                break;
            case BLOCK:
                map.hitBlock((Block)collision.getObjCollided());
                break;
            case BAT:
                // TODO: play sound
                break;
            default:
                if (GameFXApp.DEBUG_MODE) {System.out.println("Ball: Unserved type of collision occured: " + collision.getType());}
                break;
        }
    }

    private void resetAnimation() {
        Color active = Color.web("#F1C40F");
        Color inActive = Color.web("#666666");

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), t -> {
            if (starting++ < 6) {
                if (currentColor == active ) {
                    currentColor = inActive;
                }
                else {
                    currentColor = active;
                }
            }
            else {
                currentColor = active;
                starting = 0;
                initVelocity();
            }
        }));
        timeline.setCycleCount(7);
        timeline.play();
    }

    private void initVelocity() {
        int x = randGen.nextInt(INIT_BALL_SPEED + 1) + 1;
        double y = Math.sqrt(INIT_BALL_SPEED*INIT_BALL_SPEED - x*x);
        setVelocity(new Vector2D(x, -y));
    }

    // EXTRA ################
    private void mouseControl(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED , mouseEvent -> {
            setVelocity(new Vector2D(0, 0));
            move(mouseEvent.getSceneX() - getLeft() - getWidth() / 2,
                    mouseEvent.getSceneY() - getTop() - getHeight() / 2);
        });

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED , mouseEvent -> {
            setVelocity(new Vector2D(0,0));
            lastMousePosition = new Vector2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            move(mouseEvent.getSceneX() - getLeft() - getWidth() / 2,
                    mouseEvent.getSceneY() - getTop() - getHeight() / 2);
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED , mouseEvent -> {
            setVelocity(new Vector2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()).sub(lastMousePosition).normalize().mult(3));
        });
    }

    @Override
    public String log() {
        return null;
    }
    //####################
}