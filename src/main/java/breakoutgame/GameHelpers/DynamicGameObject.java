package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.GameFXApp;
import main.java.breakoutgame.GameObjects.Ball;
import main.java.breakoutgame.GameObjects.Map;
import main.java.breakoutgame.GameObjects.Bat;
import main.java.breakoutgame.GameObjects.Block;
import main.java.breakoutgame.GameHelpers.Collision.Type;

import java.util.ArrayList;

public abstract class DynamicGameObject extends GameObject {
    protected Vector2D initPosition;
    protected Vector2D velocity;

    public DynamicGameObject(Map map, double x, double y, double w, double h) {
        super(map, x, y, w, h);
        initPosition = new Vector2D(x, y);
        velocity = new Vector2D(0, 0);      // initial velosity = 0
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

    /* UNSERVED CASES OF COLLISIONS:
        # "jumping" over another object in case of too big speed or passing the vertices through each other
        # ???collision with more than one object at the time
     */
    // IMPORTANT: always use this to move any object
    protected Collision move(double newX, double newY) {
        // IMPORTANT: first check for collisions then boundary check to be able to rewind move() step

        // Removing the object from grid (becomes invisible for the map
        removeFromGrid();

        // Performing the time step position change
        position.x += newX;
        position.y += newY;

        // Loading the obj back to the grid
        updateGrid();

        // Creating variable to store the output of collision detection
        Collision output = new Collision(Type.NONE);

        // Getting array of all the tiles DynamicGameObject currently occupies
        Tile[] currentTiles = getTiles();

        // Creating variable to store the ArrayList of all the colliding GameObjects
        ArrayList<GameObject> collidingObjects = new ArrayList<GameObject>(); 

        // Iterating over all the tiles occupied by this object at the moment
        for (Tile t : currentTiles) {
            // Checking if the tile is not empty
            if (t.isOccupied()) {
                // Checking if Objects in non empty tile actually collide with this Object
                for (GameObject obj : t.storedObjects) {
                    if (isCollided(obj) && !(obj instanceof Ball)) {
                        // Checking the type of collided Object
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
                        else {
                            if (!GameFXApp.DEBUG_MODE) {
                                System.out.println("From: " + getClass().getSimpleName() + " -> Collision with unknown object:" + obj.getClass().getSimpleName());
                            }
                        }
                    }
                }
            }
        }

        // If collision check return non empty ArrayList, getOutOfCollision algorithm must be performed for each colliding Object
        if (!collidingObjects.isEmpty()) {
            removeFromGrid();
            getOutOfCollision(collidingObjects.toArray(GameObject[]::new));
            updateGrid();
        }

        
        // split this part into calculating the type of collision and 
        // returning it in order to let the objects handle different types of collisions themselves

        removeFromGrid();

        if (right() >= Map.MAP_WIDTH) {
            setRight(Map.MAP_WIDTH);
            output = new Collision(Type.RIGHT_BOUND);
        }
        else if (left() < 0) {
            setLeft(0);
            output = new Collision(Type.LEFT_BOUND);
        }

        if (bot() >= Map.MAP_HEIGHT) {
            setBot(Map.MAP_HEIGHT);
            output = new Collision(Type.BOTTOM_BOUND);
        }
        else if (top() < Map.MAP_TOP_BOUNDARY) {
            setTop(Map.MAP_TOP_BOUNDARY);
            output = new Collision(Type.TOP_BOUND);
        }
        updateGrid();

        return output;
    }

    protected void getOutOfCollision(GameObject[] objColliding) {
        if (velocity.x >= 0 && velocity.y > 0) {    // =>v  obj is moving right and down
            Vector2D prevPosRightBotCorner = new Vector2D(position).sub(velocity).add(width, height); // go back to the state  before collision

            double closestDist = prevPosRightBotCorner.distance(objColliding[0].topLeftCorner());   // calc dist between right bot corner of moving and top left of collided[0]
            GameObject closestObj = objColliding[0];    // set initially collided[0] as closes collided

            // TODO: this part probably needs rework ( its not a good idea to check against one corner only, consider dist from edges too)
            for (GameObject obj : objColliding) {   // compare each obj in a given tile and find the closest one aka first collided
                double tempDist = prevPosRightBotCorner.distance(obj.topLeftCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge top = new Edge(closestObj.topLeftCorner(), closestObj.topRightCorner());   // find the line/edge at the y of collision
            Edge left = new Edge(closestObj.botLeftCorner(), closestObj.topLeftCorner());   // find the line/edge at the x of collision
            Edge movePath = new Edge(prevPosRightBotCorner, botRightCorner()); // find the path of movement for the collision facing vertex

            Vector2D collisionPoint = movePath.isCrossing(top); // calc collision point between movement path and horizontal boundary of collided

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(left); // if no collision occurred try to calc the point for vertical boundary
                if (collisionPoint == null) {   // if there was no collision detected sth went wrong
                    if(GameFXApp.DEBUG_MODE) {
                        System.out.println("Getting out of collision that doesnt exist!");
                    }
                }
                else {
                    setRight((int)collisionPoint.x); // getting the moving obj out of collision
                    setBot((int)collisionPoint.y);  // getting the moving obj out of collision
                    velocity.x *= -1;
                }
            }
            else {
                setRight((int)collisionPoint.x); // getting the moving obj out of collision
                setBot((int)collisionPoint.y);  // getting the moving obj out of collision
                velocity.y *= -1;
            }

        }
        else if (velocity.x < 0 && velocity.y >= 0) {   // <=v  obj is moving left and down
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
                if (collisionPoint == null) {
                    if(GameFXApp.DEBUG_MODE) {
                        System.out.println("Getting out of collision that doesnt exist!");
                    }
                }
                else {
                    setLeft((int)collisionPoint.x);
                    setBot((int)collisionPoint.y);
                    velocity.x *= -1;
                }
            }
            else {
                setLeft((int)collisionPoint.x);
                setBot((int)collisionPoint.y);
                velocity.y *= -1;
            }
        }
        else if (velocity.x <= 0 && velocity.y < 0) {   // <=^ obj is moving left and up
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
                if (collisionPoint == null) {
                    if(GameFXApp.DEBUG_MODE) {
                        System.out.println("Getting out of collision that doesnt exist!");
                    }
                }
                else {
                    setLeft((int)collisionPoint.x);
                    setTop((int)collisionPoint.y);
                    velocity.x *= -1;
                }
            }
            else {
                setLeft((int)collisionPoint.x);
                setTop((int)collisionPoint.y);
                velocity.y *= -1;
            }
        }
        else if (velocity.x > 0 && velocity.y <= 0) { // =>^ obj is moving right and up
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
                if (collisionPoint == null) {
                    if(GameFXApp.DEBUG_MODE) {
                        System.out.println("Getting out of collision that doesnt exist!");
                    }
                }
                else {
                    setRight((int)collisionPoint.x);
                    setTop((int)collisionPoint.y);
                    velocity.x *= -1;
                }
            }
            else {
                setRight((int)collisionPoint.x);
                setTop((int)collisionPoint.y);
                velocity.y *= -1;
            }
        }
    }

    @Override
    public String log() {
        return super.log() + "Velocity: " + velocity.toString() + '\n';
    }
}