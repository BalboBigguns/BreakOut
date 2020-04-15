package com.breakoutgame.GameHelpers;

import com.breakoutgame.GameObjects.Block;
import com.breakoutgame.GameObjects.Bat;

public class Collision {
    public enum Type {
        NONE, LEFT_BOUND, RIGHT_BOUND, TOP_BOUND, BOTTOM_BOUND, BLOCK, BAT;
    }

    Type type;
    GameObject objCollided;

    public Collision(Type t) {
        type = t;       
    }

    public Collision(GameObject obj) {
        objCollided = obj;
        if (obj instanceof Block) {
            type = Type.BLOCK;
        }
        else if (obj instanceof Bat) {
            type = Type.BAT;
        }
    }

    public GameObject getObjCollided() {
        return objCollided;
    }

    public Type getType() {
        return type;
    }
}