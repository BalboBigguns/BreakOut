package main.java.breakoutgame.GameObjects;

import main.java.breakoutgame.GameHelpers.DynamicGameObject;
import main.java.breakoutgame.GameHelpers.Collision;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.paint.Color;

// extra for debugging
    import javafx.scene.Scene;
    import javafx.scene.input.MouseEvent;
//

public class Ball extends DynamicGameObject {
    public static final int INIT_BALL_SIZE = 16;
    public int starting = 0;

    Color currentColor;

    public Ball(Map root, double InitX, double InitY, boolean mouseControllEnabled) {
        super(root, InitX, InitY, INIT_BALL_SIZE, INIT_BALL_SIZE);
        currentColor = Color.YELLOW;
        if (mouseControllEnabled) {
            mouseControll(root.gc.getCanvas().getScene());
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
                break;
            default:
                System.out.println("Ball: Unserved type of collision occured: " + collision.getType());
                break;
        }
    }

    private void resetAnimation() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (starting++ < 6) {
                    if (currentColor == Color.YELLOW ) {
                        currentColor = Color.GREY;
                    }
                    else {
                        currentColor = Color.YELLOW;
                    }
                }
                else {
                    currentColor = Color.YELLOW;
                    starting = 0;
                    velocity.y = -4;
                    velocity.x = 6;
                }
            }
        }));
        timeline.setCycleCount(7);
        timeline.play();
    }

    // EXTRA ################
    private void mouseControll(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED , mouseEvent -> {
            velocity.x = velocity.y = 0;
            move(mouseEvent.getSceneX() - position.x - width / 2, mouseEvent.getSceneY() - position.y - height / 2);
        });

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED , mouseEvent -> {
            velocity.x = velocity.y = 0;
            move(mouseEvent.getSceneX() - position.x - width / 2, mouseEvent.getSceneY() - position.y - height / 2);
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED , mouseEvent -> {
            velocity.y = -4;
            velocity.x = 0;
        });
    }
    //####################
}