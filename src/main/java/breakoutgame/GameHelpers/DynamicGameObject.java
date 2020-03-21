package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.GameObjects.Ball;
import main.java.breakoutgame.GameObjects.Map;
import main.java.breakoutgame.GameObjects.Bat;
import main.java.breakoutgame.GameObjects.Block;
import main.java.breakoutgame.GameHelpers.Collision.CollisionType;
import main.java.breakoutgame.Utils.Logger;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * Basic abstract class for moving  <code>DynamicGameObject</code>.
 * <p>
 * Extends <code>GameObject</code> supporting observable pattern.
 * All the moving objects in the game inherit from this class.
 * @see PropertyChangeSupport
 */
public abstract class DynamicGameObject extends GameObject {
    private Vector2D initPosition;
    private Vector2D velocity;

    /**
     * Constructor for <code>DynamicGameObject</code>.
     * <p>
     * Initialize this object with <code>initPosition</code> responsible for default starting position in case of any
     * reset to the game. Default initial velocity of this object is equal to 0.
     * @param map reference to the map, this object is managed by (possibly deprecated)
     * @param x initial x coordinate of this object
     * @param y initial y coordinate of this object
     * @param w width of this object
     * @param h height of this object
     */
    public DynamicGameObject(Map map, double x, double y, double w, double h) {
        super(map, x, y, w, h);
        initPosition = new Vector2D(x, y);
        velocity = new Vector2D(0, 0);      // initial velosity = 0
    }

    /**
     * Gets the velocity of this object.
     * @return velocity as <code>Vector2D</code>
     */
    public Vector2D getVelocity() {
        return velocity;
    }

    /**
     * Sets the velocity of this object.
     * @param velocity <code>Vector2D</code> with x and y velocities
     */
    public void setVelocity(Vector2D velocity) {
        pcs.firePropertyChange("velocity", this.velocity, velocity);
        this.velocity = velocity;
    }

    /**
     * Reset this object to its default startup values.
     */
    public void reset() {
        setVelocity(new Vector2D(0, 0));
        removeFromGrid();
        setPosition(new Vector2D(initPosition));
        updateGrid();
    }

    // TODO: get out with this
    protected boolean isCollided(GameObject o) {
        return (getTop() <= o.getBot()) && (getBot() >= o.getTop()) &&
                (getLeft() <= o.getRight()) && (getRight() >= o.getLeft());
    }

    public void update() {
        onCollision(move());
    }

    abstract public void onCollision(Collision collision);

    // HELPERS
    protected Collision move() {
        return move(velocity);
    }

    protected Collision move(Vector2D shift) {
        return move(shift.getX(), shift.getY());
    }

    /* UNSERVED CASES OF COLLISIONS:
        # "jumping" over another object in case of too big speed or passing the vertices through each other
        # ???collision with more than one object at the time
     */
    // IMPORTANT: always use this to move any object
    protected Collision move(double deltaX, double deltaY) {
        // IMPORTANT: first check for collisions then boundary check to be able to rewind move() step

        // Removing the object from grid (becomes invisible for the map
        removeFromGrid();

        // Performing the time step position change
        setPosition(getPosition().add(deltaX, deltaY));

        // Loading the obj back to the grid
        updateGrid();

        // Creating variable to store the output of collision detection
        Collision output = new Collision(CollisionType.NONE);

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
                            if (output.getType() == CollisionType.NONE) {
                                output = new Collision(obj);
                            }
                        }
                        else if (obj instanceof Bat) {
                            collidingObjects.add(obj);
                            if (output.getType() == CollisionType.NONE) {
                                output = new Collision(obj);
                            }
                        }
                        else {
                            Logger.getGlobalInstance().printEvent("From: " + getClass().getSimpleName() + " -> Collision with unknown object:" + obj.getClass().getSimpleName(), Logger.LogType.ERROR);
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

        if (getRight() >= Map.MAP_WIDTH) {
            setRight(Map.MAP_WIDTH);
            output = new Collision(CollisionType.RIGHT_BOUND);
        }
        else if (getLeft() < 0) {
            setLeft(0);
            output = new Collision(CollisionType.LEFT_BOUND);
        }

        if (getBot() >= Map.MAP_HEIGHT) {
            setBot(Map.MAP_HEIGHT);
            output = new Collision(CollisionType.BOTTOM_BOUND);
        }
        else if (getTop() < Map.MAP_TOP_BOUNDARY) {
            setTop(Map.MAP_TOP_BOUNDARY);
            output = new Collision(CollisionType.TOP_BOUND);
        }
        updateGrid();

        return output;
    }

    protected void getOutOfCollision(GameObject[] objColliding) {
        if (getVelocity().getX() >= 0 && getVelocity().getY() > 0) {    // =>v  obj is moving right and down
            Vector2D prevPosRightBotCorner = new Vector2D(getPosition()).sub(getVelocity()).add(getWidth(), getHeight()); // go back to the state  before collision

            double closestDist = prevPosRightBotCorner.distance(objColliding[0].getTopLeftCorner());   // calc dist between right bot corner of moving and top left of collided[0]
            GameObject closestObj = objColliding[0];    // set initially collided[0] as closes collided

            // TODO: this part probably needs rework ( its not a good idea to check against one corner only, consider dist from edges too)
            for (GameObject obj : objColliding) {   // compare each obj in a given tile and find the closest one aka first collided
                double tempDist = prevPosRightBotCorner.distance(obj.getTopLeftCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge top = new Edge(closestObj.getTopLeftCorner(), closestObj.getTopRightCorner());   // find the line/edge at the y of collision
            Edge left = new Edge(closestObj.getBotLeftCorner(), closestObj.getTopLeftCorner());   // find the line/edge at the x of collision
            Edge movePath = new Edge(prevPosRightBotCorner, getBotRightCorner()); // find the path of movement for the collision facing vertex

            Vector2D collisionPoint = movePath.isCrossing(top); // calc collision point between movement path and horizontal boundary of collided

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(left); // if no collision occurred try to calc the point for vertical boundary
                if (collisionPoint == null) {   // if there was no collision detected sth went wrong
                    Logger.getGlobalInstance().printEvent("Getting out of collision that doesnt exist!", Logger.LogType.ERROR);
                }
                else {
                    setRight((int)collisionPoint.getX()); // getting the moving obj out of collision
                    setBot((int)collisionPoint.getY());  // getting the moving obj out of collision
                    setVelocity(getVelocity().scale(-1, 1));    // reverse x component
                }
            }
            else {
                setRight((int)collisionPoint.getX()); // getting the moving obj out of collision
                setBot((int)collisionPoint.getY());  // getting the moving obj out of collision
                setVelocity(getVelocity().scale(1, -1));    // reverse y component
            }

        }
        else if (getVelocity().getX() < 0 && getVelocity().getY() >= 0) {   // <=v  obj is moving left and down
            Vector2D prevPosLeftBotCorner = new Vector2D(getPosition()).sub(getVelocity()).add(0, getHeight()); // go back to the state  before collision

            double closestDist = prevPosLeftBotCorner.distance(objColliding[0].getTopRightCorner());
            GameObject closestObj = objColliding[0];

            for (GameObject obj : objColliding) {
                double tempDist = prevPosLeftBotCorner.distance(obj.getTopRightCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge top = new Edge(closestObj.getTopLeftCorner(), closestObj.getTopRightCorner());
            Edge right = new Edge(closestObj.getBotRightCorner(), closestObj.getTopRightCorner());
            Edge movePath = new Edge(prevPosLeftBotCorner, getBotLeftCorner());

            Vector2D collisionPoint = movePath.isCrossing(top);

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(right);
                if (collisionPoint == null) {
                    Logger.getGlobalInstance().printEvent("Getting out of collision that doesnt exist!", Logger.LogType.ERROR);
                }
                else {
                    setLeft((int)collisionPoint.getX());
                    setBot((int)collisionPoint.getY());
                    setVelocity(getVelocity().scale(-1, 1));
                }
            }
            else {
                setLeft((int)collisionPoint.getX());
                setBot((int)collisionPoint.getY());
                setVelocity(getVelocity().scale(1, -1));
            }
        }
        else if (getVelocity().getX() <= 0 && getVelocity().getY() < 0) {   // <=^ obj is moving left and up
            Vector2D prevPosLeftTopCorner = new Vector2D(getPosition()).sub(getVelocity()).add(0, 0); // go back to the state  before collision

            double closestDist = prevPosLeftTopCorner.distance(objColliding[0].getBotRightCorner());
            GameObject closestObj = objColliding[0];

            for (GameObject obj : objColliding) {
                double tempDist = prevPosLeftTopCorner.distance(obj.getBotRightCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge bot = new Edge(closestObj.getBotLeftCorner(), closestObj.getBotRightCorner());
            Edge right = new Edge(closestObj.getBotRightCorner(), closestObj.getTopRightCorner());
            Edge movePath = new Edge(prevPosLeftTopCorner, getTopLeftCorner());

            Vector2D collisionPoint = movePath.isCrossing(bot);

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(right);
                if (collisionPoint == null) {
                     Logger.getGlobalInstance().printEvent("Getting out of collision that doesnt exist!", Logger.LogType.ERROR);

                }
                else {
                    setLeft((int)collisionPoint.getX());
                    setTop((int)collisionPoint.getY());
                    setVelocity(getVelocity().scale(-1, 1));
                }
            }
            else {
                setLeft((int)collisionPoint.getX());
                setTop((int)collisionPoint.getY());
                setVelocity(getVelocity().scale(1, -1));
            }
        }
        else if (getVelocity().getX() > 0 && getVelocity().getY() <= 0) { // =>^ obj is moving right and up
            Vector2D prevPosRightTopCorner = new Vector2D(getPosition()).sub(getVelocity()).add(getWidth(), 0); // go back to the state  before collision

            double closestDist = prevPosRightTopCorner.distance(objColliding[0].getBotLeftCorner());
            GameObject closestObj = objColliding[0];

            for (GameObject obj : objColliding) {
                double tempDist = prevPosRightTopCorner.distance(obj.getBotLeftCorner());
                if (tempDist < closestDist) {
                    closestDist = tempDist;
                    closestObj = obj;
                }
            }

            Edge bot = new Edge(closestObj.getBotLeftCorner(), closestObj.getBotRightCorner());
            Edge left = new Edge(closestObj.getBotLeftCorner(), closestObj.getTopLeftCorner());
            Edge movePath = new Edge(prevPosRightTopCorner, getTopRightCorner());

            Vector2D collisionPoint = movePath.isCrossing(bot);

            if (collisionPoint == null) {
                collisionPoint = movePath.isCrossing(left);
                if (collisionPoint == null) {
                    Logger.getGlobalInstance().printEvent("Getting out of collision that doesnt exist!", Logger.LogType.ERROR);
                }
                else {
                    setRight((int)collisionPoint.getX());
                    setTop((int)collisionPoint.getY());
                    setVelocity(getVelocity().scale(-1,1));
                }
            }
            else {
                setRight((int)collisionPoint.getX());
                setTop((int)collisionPoint.getY());
                setVelocity(getVelocity().scale(1, -1));
            }
        }
    }

    @Override
    public String log() {
        return super.log() + "Velocity: " + velocity.toString() + '\n';
    }
}