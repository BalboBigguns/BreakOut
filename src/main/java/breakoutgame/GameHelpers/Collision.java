package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.GameObjects.Block;
import main.java.breakoutgame.GameObjects.Bat;

public class Collision {
    public enum Type {
        NONE, LEFT_BOUND, RIGHT_BOUND, TOP_BOUND, BOTTOM_BOUND, BLOCK, BAT;
    }

    private Type type;
    private GameObject objCollided;

    public Collision(Type t) {
        type = t;       
    }

    // TODO: make it more versatile to manage all type of GameObjects and maybe store information about both of them
    //  to resolve collisions independently eg create list of all collisions managed by CollisionResolver or sth
    public Collision(GameObject obj) {
        objCollided = obj;
        if (obj instanceof Block) {
            type = Type.BLOCK;
        }
        else if (obj instanceof Bat) {
            type = Type.BAT;
        }
        else {
            // TODO: refactor to better logging system
            System.out.println("UNKNOWN COLLISION TYPE!!!");
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
     * Getter for the type of collision
     * @return type of collision
     */
    public Type getType() {
        return type;
    }
}