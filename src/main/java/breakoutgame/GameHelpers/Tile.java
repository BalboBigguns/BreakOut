package main.java.breakoutgame.GameHelpers;

import java.util.ArrayList;

public class Tile {
    static final public int TILE_WIDTH = 40;
    static final public int TILE_HEIGHT = 20;
    static final private int INITIAL_CAPACITY = 5;
    protected ArrayList<GameObject> storedObjects;
    protected int xIndex;
    protected int yIndex;

    public Tile(int xIndex, int yIndex) {
        storedObjects = new ArrayList<GameObject>(INITIAL_CAPACITY);
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    protected void setIndices(int x, int y) {
        xIndex = x;
        yIndex = y;
    }

    public ArrayList<GameObject> getStoredObjects() {
        return storedObjects;
    }

    public boolean isOccupied() {
        return storedObjects.size() > 1;
    }

    public ArrayList<GameObject> getObjects() {
        return new ArrayList<GameObject>(storedObjects);
    }

    public void putObject(GameObject obj) {
        storedObjects.add(obj);
    }

    public boolean removeObject(GameObject obj) {
        return storedObjects.remove(obj);
    }

    @Override
    public String toString() {
        return "Indices: " + xIndex + '\t' + yIndex + '\n' +
                "Num of objects: " + storedObjects.size() + '\n';
    }
}