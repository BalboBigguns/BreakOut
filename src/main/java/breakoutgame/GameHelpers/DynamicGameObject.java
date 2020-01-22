package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.GameFXApp;
import main.java.breakoutgame.GameObjects.Map;
import main.java.breakoutgame.GameObjects.Bat;
import main.java.breakoutgame.GameObjects.Block;
import main.java.breakoutgame.GameHelpers.Collision.Type;

import java.util.ArrayList;

public abstract class DynamicGameObject extends GameObject {
    protected Vector2D initPosition;
    protected Vector2D velocity;

    public DynamicGameObject(Map root, double x, double y, double w, double h) {
        super(root, x, y, w, h);
        initPosition = new Vector2D(x, y);
        velocity = new Vector2D(0, 0);      // initially velosity = 0
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    protected boolean isCollided(GameObject o) {
        return (top() <= o.bot()) && (bot() >= o.top()) &&      
                (left() <= o.right()) && (right() >= o.left());     
    }

    public void update() {
        onCollision(move());
    }

    abstract public void onCollision(Collision collision);

    public void reset() {
        velocity.x = 0;
        velocity.y = 0;
        removeFromGrid();
        position = new Vector2D(initPosition);
        updateGrid();
    }

    // HELPERS
    protected Collision move() {
        return move(velocity);
    }

    protected Collision move(Vector2D shift) {
        return move(shift.x, shift.y);
    }

    // IMPORTANT: allways use this to move any object
    protected Collision move(double newX, double newY) {
        // IMPORTANT: first check for collisions then boundary check to be able to rewind move() step

        removeFromGrid();
        position.x += newX;
        position.y += newY;
        updateGrid();

        double backgroundWidth = Map.MAP_WIDTH;
        double backgroundHeight = Map.MAP_HEIGHT;
        Collision output = new Collision(Type.NONE);


        Tile[] currentTiles = getTiles();
        ArrayList<GameObject> collidingObjects = new ArrayList<GameObject>(); 

        for (Tile t : currentTiles) {
            if (t.isOccupied()) {
                for (GameObject obj : t.storedObjects) {
                    if (isCollided(obj)) {
                        if (obj instanceof Block) {
                            collidingObjects.add(obj);
                            if (output.type == Type.NONE) {
                                output = new Collision(obj);
                            }
                        }
                        else if (obj instanceof Bat) {
                            collidingObjects.add(obj);
                            if (output.type == Type.NONE) {
                                output = new Collision(obj);
                            }
                        }
                    }
                }
            }
        }

        
        if (!collidingObjects.isEmpty()) {
            removeFromGrid();
            getOutOfCollision(collidingObjects.toArray(GameObject[]::new));
            updateGrid();
        }

        
        // split this part into calculating the type of collision and 
        // returning it in order to let the objects handle different types themselves

        removeFromGrid();
        if (right() >= backgroundWidth) {
            position.x = backgroundWidth - (width + 1);
            output = new Collision(Type.RIGHT_BOUND);
        }
        else if (left() < 0) {
            position.x = 0;
            output = new Collision(Type.LEFT_BOUND);
        }

        if (bot() >= backgroundHeight) {
            position.y = backgroundHeight - (height + 1);
            output = new Collision(Type.BOTTOM_BOUND);
        }
        else if (top() < 0) {
            position.y = 0;
            output = new Collision(Type.TOP_BOUND);
        }
        updateGrid();

        return output;
    }

    protected void getOutOfCollision(GameObject[] objColliding) {
        if (velocity.x >= 0 && velocity.y > 0) {
            Vector2D prevPosRightBotCorner = new Vector2D(position).sub(velocity).add(width, height); // go back to the state  before collision

            double closestDist = prevPosRightBotCorner.distance(objColliding[0].topLeftCorner());
            GameObject closestObj = objColliding[0];

            for (GameObject obj : objColliding) {
                double tempDist = prevPosRightBotCorner.distance(obj.topLeftCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge top = new Edge(closestObj.topLeftCorner(), closestObj.topRightCorner());
            Edge left = new Edge(closestObj.botLeftCorner(), closestObj.topLeftCorner());
            Edge movePath = new Edge(prevPosRightBotCorner, botRightCorner());

            Vector2D collisionPoint = movePath.isCrossing(top);

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(left);
                if (collisionPoint == null && GameFXApp.DEBUG_MODE) {
                    System.out.println("Getting out of collision that doesnt exist!");
                }
                else {
                    collisionPoint.x = (int)collisionPoint.x;
                    collisionPoint.y = (int)collisionPoint.y;

                    velocity.x *= -1;
                }
            }
            else {
                collisionPoint.x = (int)collisionPoint.x;
                collisionPoint.y = (int)collisionPoint.y;

                velocity.y *= -1;
            }
        }
        else if (velocity.x < 0 && velocity.y >= 0) {
            Vector2D prevPosLeftBotCorner = new Vector2D(position).sub(velocity).add(0, height); // go back to the state  before collision

            double closestDist = prevPosLeftBotCorner.distance(objColliding[0].topRightCorner());
            GameObject closestObj = objColliding[0];

            for (GameObject obj : objColliding) {
                double tempDist = prevPosLeftBotCorner.distance(obj.topRightCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge top = new Edge(closestObj.topLeftCorner(), closestObj.topRightCorner());
            Edge right = new Edge(closestObj.botRightCorner(), closestObj.topRightCorner());
            Edge movePath = new Edge(prevPosLeftBotCorner, botLeftCorner());

            Vector2D collisionPoint = movePath.isCrossing(top);

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(right);
                if (collisionPoint == null && GameFXApp.DEBUG_MODE) {
                    System.out.println("Getting out of collision that doesnt exist!");
                }
                else {
                    collisionPoint.x = (int)collisionPoint.x;
                    collisionPoint.y = (int)collisionPoint.y;

                    velocity.x *= -1;
                }
            }
            else {
                collisionPoint.x = (int)collisionPoint.x;
                collisionPoint.y = (int)collisionPoint.y;

                velocity.y *= -1;
            }
        }
        else if (velocity.x <= 0 && velocity.y < 0) {
            Vector2D prevPosLeftTopCorner = new Vector2D(position).sub(velocity).add(0, 0); // go back to the state  before collision

            double closestDist = prevPosLeftTopCorner.distance(objColliding[0].botRightCorner());
            GameObject closestObj = objColliding[0];

            for (GameObject obj : objColliding) {
                double tempDist = prevPosLeftTopCorner.distance(obj.botRightCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge bot = new Edge(closestObj.botLeftCorner(), closestObj.botRightCorner());
            Edge right = new Edge(closestObj.botRightCorner(), closestObj.topRightCorner());
            Edge movePath = new Edge(prevPosLeftTopCorner, topLeftCorner());

            Vector2D collisionPoint = movePath.isCrossing(bot);

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(right);
                if (collisionPoint == null && GameFXApp.DEBUG_MODE) {
                    System.out.println("Getting out of collision that doesnt exist!");
                }
                else {
                    collisionPoint.x = (int)collisionPoint.x;
                    collisionPoint.y = (int)collisionPoint.y;

                    velocity.x *= -1;
                }
            }
            else {
                collisionPoint.x = (int)collisionPoint.x;
                collisionPoint.y = (int)collisionPoint.y;

                velocity.y *= -1;
            }
        }
        else if (velocity.x > 0 && velocity.y <= 0) {
            Vector2D prevPosRightTopCorner = new Vector2D(position).sub(velocity).add(width, 0); // go back to the state  before collision

            double closestDist = prevPosRightTopCorner.distance(objColliding[0].botLeftCorner());
            GameObject closestObj = objColliding[0];

            for (GameObject obj : objColliding) {
                double tempDist = prevPosRightTopCorner.distance(obj.botLeftCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge bot = new Edge(closestObj.botLeftCorner(), closestObj.botRightCorner());
            Edge left = new Edge(closestObj.botLeftCorner(), closestObj.topLeftCorner());
            Edge movePath = new Edge(prevPosRightTopCorner, topRightCorner());

            Vector2D collisionPoint = movePath.isCrossing(bot);

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(left);
                if (collisionPoint == null && GameFXApp.DEBUG_MODE) {
                    System.out.println("Getting out of collision that doesnt exist!");
                }
                else {
                    collisionPoint.x = (int)collisionPoint.x;
                    collisionPoint.y = (int)collisionPoint.y;

                    velocity.x *= -1;
                }
            }
            else {
                collisionPoint.x = (int)collisionPoint.x;
                collisionPoint.y = (int)collisionPoint.y;

                velocity.y *= -1;
            }
        }
    }

    @Override
    public String log() {
        return super.log() + "Velocity: " + velocity.toString() + '\n';
    }
}