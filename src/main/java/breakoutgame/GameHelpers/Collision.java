package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.GameObjects.Block;
import main.java.breakoutgame.GameObjects.Bat;
import main.java.breakoutgame.Utils.Logger;

public class Collision {
    public enum CollisionType {
        NONE, LEFT_BOUND, RIGHT_BOUND, TOP_BOUND, BOTTOM_BOUND, BLOCK, BAT;
    }

    private CollisionType type;
    private GameObject objCollided;

    /**
     * Constructor for Collisions object created with explicitly provided type.
     * Property objCollided is initiated with null.
     * @param t type to initiate the collision with
     */
    public Collision(CollisionType t) {
        type = t;
        objCollided = null;
    }

    // TODO: make it more versatile to manage all type of GameObjects and maybe store information about both of them
    //  to resolve collisions independently eg create list of all collisions managed by CollisionResolver or sth
    /**
     * Constructor for Collisions object created with explicitly provided object.
     * Property type is initiated with proper type deduced from the type of provided object.
     * @param obj GameObject instance to initiate the collision with
     */
    public Collision(GameObject obj) {
        objCollided = obj;
        if (obj instanceof Block) {
            type = CollisionType.BLOCK;
        }
        else if (obj instanceof Bat) {
            type = CollisionType.BAT;
        }
        else {
            Logger.getGlobalInstance().printEvent("UNKNOWN COLLISION TYPE!!! Update Collision class with more GameObject types.", Logger.LogType.ERROR);
            type = null;
        }
    }

    /**
     * Getter for the instance of collided object
     * @return collided object instance
     */
    public GameObject getObjCollided() {
        return objCollided;
    }

    /**
     * Gets the type of collision
     * @return type of collision
     */
    public CollisionType getType() {
        return type;
    }
}