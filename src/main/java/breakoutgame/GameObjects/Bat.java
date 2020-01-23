package main.java.breakoutgame.GameObjects;

import main.java.breakoutgame.GameHelpers.DynamicGameObject;
import main.java.breakoutgame.GameHelpers.Collision;

import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
        map.gc.fillRect(position.x, position.y, width, height);
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
        velocity.x = -VELOCITY;
    }

    private void moveRight() {
        velocity.x = VELOCITY;
    }

    private void stop() {
        velocity.x = 0;
    }
}