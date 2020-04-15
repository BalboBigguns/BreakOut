package com.breakoutgame.GameObjects;

import com.breakoutgame.GameHelpers.Vector2D;

import com.breakoutgame.GameFXApp;
import com.breakoutgame.GameHelpers.DynamicGameObject;
import com.breakoutgame.GameHelpers.Collision;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javafx.scene.paint.Color;

// extra for debugging
    import javafx.scene.Scene;
    import javafx.scene.input.MouseEvent;
//

public class Ball extends DynamicGameObject {
    public static final int INIT_BALL_SIZE = 16;
    public int starting = 0;
    private Vector2D lastMousePosition;

    Color currentColor;

    public Ball(Map map, double InitX, double InitY) {
        super(map, InitX, InitY, INIT_BALL_SIZE, INIT_BALL_SIZE);
        currentColor = Color.YELLOW;
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
        map.gc.fillOval(position.x, position.y, width, height);
    }

    @Override
    public void onCollision(Collision collision) {
        switch(collision.getType()) {
            case NONE:
                break;
            case RIGHT_BOUND:
            case LEFT_BOUND:
                velocity.x *= -1;
                break;
            case TOP_BOUND:
                velocity.y *= -1;
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
                velocity.y = -4;
                velocity.x = 6;
            }
        }));
        timeline.setCycleCount(7);
        timeline.play();
    }

    // EXTRA ################
    private void mouseControl(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED , mouseEvent -> {
            velocity.x = velocity.y = 0;
            move(mouseEvent.getSceneX() - position.x - width / 2, mouseEvent.getSceneY() - position.y - height / 2);
        });

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED , mouseEvent -> {
            velocity.x = velocity.y = 0;
            lastMousePosition = new Vector2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            move(mouseEvent.getSceneX() - position.x - width / 2, mouseEvent.getSceneY() - position.y - height / 2);
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED , mouseEvent -> {
            velocity = new Vector2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()).sub(lastMousePosition).normalize().mult(3);
        });
    }

    @Override
    public String log() {
        return null;
    }
    //####################
}