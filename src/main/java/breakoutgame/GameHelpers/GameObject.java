package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.Utils.Loggable;
import main.java.breakoutgame.GameObjects.Map;
import main.java.breakoutgame.Utils.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Basic abstract class for static (motionless) <code>GameObject</code>.
 * <p>
 * Implements {@link Loggable} interface and can be subject to {@link PropertyChangeListener} (observable).
 * All the static objects in the game inherit from this class.
 * @see DynamicGameObject
 * @see PropertyChangeSupport
 */
public abstract class GameObject implements Loggable {
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Vector2D position;
    private double width;
    private double height;

    //  TODO: maybe encapsulate it
    protected Map map;

    /**
     * Constructor for <code>GameObject</code>.
     * @param map reference to the map, this object is managed by (possibly deprecated)
     * @param x initial x coordinate of this object
     * @param y initial y coordinate of this object
     * @param w width of this object
     * @param h height of this object
     */
    public GameObject(Map map, double x, double y, double w, double h) {
        this.map = map;
        width = w;
        height = h;
        position = new Vector2D(x, y);
        updateGrid();
    }

    // Getters and setters

    /**
     * Gets the origin position of this object (top left corner).
     * @return <code>Vector2D</code> of center position
     */
    public Vector2D getPosition() {
        return new Vector2D(position);
    }

    /**
     * Sets the origin position of this object (top left corner).
     * @param position <code>Vector2D</code> of the new position
     */
    public void setPosition(Vector2D position) {
        pcs.firePropertyChange("position", this.position, position);
        this.position = position;
    }

    /**
     * Gets the center position of this object.
     * @return position <code>Vector2D</code> of center
     */
    public Vector2D getPositionCenter() {
        return new Vector2D(position).add(- width / 2, height / 2);
    }

    /**
     * Sets the center position of this object.
     * @param position <code>Vector2D</code> of the new position
     */
    public void setPositionCenter(Vector2D position) {
        Vector2D oldValue = this.position;
        this.position = position.add(- width / 2, height / 2);
        pcs.firePropertyChange("position", oldValue, this.position);
    }

    /**
     * Gets the width of this object.
     * @return <code>width</code> value
     */
    public double getWidth() { return width; }

    /**
     * Sets the width of this object.
     * @param width new <code>width</code> value
     */
    public void setWidth(double width) {
        pcs.firePropertyChange("width", this.width, width);
        this.width = width;
    }

    /**
     * Gets the height of this object.
     * @return <code>height</code> value
     */
    public double getHeight() { return height; }

    /**
     * Sets the height of this object.
     * @param height new <code>height</code> value
     */
    public void setHeight(double height) {
        pcs.firePropertyChange("height", this.height, height);
        this.height = height;
    }

    /**
     * Gets the top boundary coordinate of this object.
     * @return coordinate of the top edge
     */
    public double getTop() { return position.getY(); }

    /**
     * Sets the top boundary coordinate of this object.
     * @param y coordinate of the top edge
     */
    public void setTop(double y) {
        Vector2D oldValue = position;
        position.setY(y);
        pcs.firePropertyChange("position", oldValue, position);
    }

    /**
     * Gets the bottom boundary coordinate of this object.
     * @return coordinate of the bottom edge
     */
    public double getBot() { return position.getY() + height; }

    /**
     * Sets the bottom boundary coordinate of this object.
     * @param y coordinate of the bottom edge
     */
    public void setBot(double y) {
        Vector2D oldValue = position;
        position.setY(y - (height + 1));
        pcs.firePropertyChange("position", oldValue, position);
    }

    /**
     * Gets the right boundary coordinate of this object.
     * @return coordinate of the right edge
     */
    public double getRight() { return position.getX() + width; }

    /**
     * Sets the right boundary coordinate of this object.
     * @param x coordinate of the right edge
     */
    public void setRight(double x) {
        Vector2D oldValue = position;
        position.setX(x - (width + 1));
        pcs.firePropertyChange("position", oldValue, position);
    }

    /**
     * Gets the left boundary coordinate of this object.
     * @return x coordinate of the left edge
     */
    public double getLeft() { return position.getX(); }

    /**
     * Sets the left boundary coordinate of this object.
     * @param x coordinate of the left edge
     */
    public void setLeft(double x) {
        Vector2D oldValue = position;
        position.setX(x);
        pcs.firePropertyChange("position", oldValue, position);
    }

    /**
     * Gets the top right corner position of this object.
     * @return position as <code>Vector2D</code>
     */
    public Vector2D getTopRightCorner() { return new Vector2D(position).add(width, 0); }

    /**
     * Gets the top left corner position of this object.
     * @return position as <code>Vector2D</code>
     */
    public Vector2D getTopLeftCorner() { return new Vector2D(position); }

    /**
     * Gets the bottom right corner position of this object.
     * @return position as <code>Vector2D</code>
     */
    public Vector2D getBotRightCorner() { return new Vector2D(position).add(width, height); }

    /**
     * Gets the bottom left corner position of this object.
     * @return position as <code>Vector2D</code>
     */
    public Vector2D getBotLeftCorner() { return new Vector2D(position).add(0, height); }


    // TODO: encapsulate drawing responsibility to the other object, possibly implement observer pattern
    public abstract void draw();


    //HELPERS TODO: shouldn't be here, get out to map class plsss

    public Tile[] getTiles() {
        int topRow = getRow(getTop());
        int botRow = getRow(getBot());
        int leftColumn = getColumn(getLeft());
        int rightColumn = getColumn(getRight());
        Tile[][] grid = map.getGrid();

        int numOfTiles = (botRow - topRow + 1) * (rightColumn - leftColumn + 1);
        Tile[] output = new Tile[numOfTiles];

        try {
            int counter = 0;
            for (int i = topRow; i <= botRow; i++) {
                for (int j = leftColumn; j <= rightColumn; j++) {
                    output[counter++] = grid[i][j];
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            Logger.getGlobalInstance().printEvent("This obj got out of the tile map: " + this, Logger.LogType.ERROR);
            return null;
        }

        return output;
    }

    public void removeFromGrid() {
        for (Tile t : getTiles()) {
            t.removeObject(this);
        }
    }

    protected void updateGrid() {
        for (Tile t : getTiles()) {
            t.putObject(this);
        }
    }

    protected int getRow(double yPos) {
        int row = (int)yPos / Tile.TILE_HEIGHT;
        if (row >= Map.MAP_GRID_HEIGHT) {
            row = Map.MAP_GRID_HEIGHT - 1;
        }
        else if (row < 0) {
            row = 0;
        }
        return row;
    }

    protected int getColumn(double xPos) {
        int column = (int)xPos / Tile.TILE_WIDTH;
        if (column >= Map.MAP_GRID_WIDTH) {
            column = Map.MAP_GRID_WIDTH - 1;
        }
        else if (column < 0) {
            column = 0;
        }
        return column;
    }


    /**
     * Method used by Observer class to subscribe for changes in this object.
     * @param pcl PropertyChangeListener instance subscribing for changes in this object
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    /**
     * Method used by Observer class to unsubscribe for changes in this object.
     * @param pcl PropertyChangeListener instance unsubscribing for changes in this object
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }

    /**
     * Method used by Logger class
     * @return formatted String with position, width and height
     */
    public String log() {
        return  "Position: " + position.toString() + '\n' +
                "Width, height: " + width + '\t' + height + '\n';
    }
}