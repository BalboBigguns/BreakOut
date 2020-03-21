package main.java.breakoutgame.GameObjects;

import main.java.breakoutgame.GameHelpers.DynamicGameObject;
import main.java.breakoutgame.GameHelpers.Collision;

import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.breakoutgame.GameHelpers.Vector2D;

public class Bat extends DynamicGameObject {
    static final private double VELOCITY = 10;
    static final protected double INIT_WIDTH = 70;
    static final protected double INIT_HEIGHT = 10;

    public Bat(Map map, double xInit, double yInit) {
        super(map, xInit, yInit, INIT_WIDTH, INIT_HEIGHT);
        setKeyResponse(map.gc.getCanvas().getScene());
    }

    public void setKeyResponse(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            KeyCode key = keyEvent.getCode();

            if (key == KeyCode.LEFT) {
                moveLeft();
            }
            else if (key == KeyCode.RIGHT) {
                moveRight();
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            KeyCode key = keyEvent.getCode();
            
            if (key == KeyCode.LEFT || key == KeyCode.RIGHT) {
                stop();
            }
        });
    }

    @Override
    public void draw() {
        map.gc.setFill(Color.WHITE);
        map.gc.fillRect(getLeft(), getTop(), getWidth(), getHeight());
    }

    @Override
    public void onCollision(Collision collision) {
        // switch (collision) {
        //     case NONE:
        //         break;
        //     case LEFT_BOUND:
        //         position.x = 0;
        //         break;
        //     case RIGHT_BOUND:
        //         position.x = Map.MAP_WIDTH - width;
        //         break;
        //     default:
        //         break;
        // }
    }

    private void moveLeft() {
        setVelocity(new Vector2D(-VELOCITY, 0));
    }

    private void moveRight() {
        setVelocity(new Vector2D(VELOCITY, 0));
    }

    private void stop() {
        setVelocity(new Vector2D(0, 0));
    }
}