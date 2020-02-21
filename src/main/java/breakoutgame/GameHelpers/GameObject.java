package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.Utils.Loggable;
import main.java.breakoutgame.GameObjects.Map;
import main.java.breakoutgame.Utils.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class GameObject implements Loggable {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Vector2D position;
    private double width;
    private double height;

    //  TODO: maybe encapsulate it
    protected Map map;

    public GameObject(Map map, double x, double y, double w, double h) {
        this.map = map;
        width = w;
        height = h;
        position = new Vector2D(x, y);
        updateGrid();
    }

    // Getters and setters

    /**
     * Getter for the center position of the object.
     * @return Vector2D of center position
     */
    public Vector2D getPositionCenter() { return position.add(- width / 2, height / 2); }

    /**
     * Setter for the center position of the object.
     * @param position Vector2D of the new position
     */
    public void setPositionCenter(Vector2D position) {
        Vector2D oldValue = this.position;
        this.position = position.add(- width / 2, height / 2);
        pcs.firePropertyChange("position", oldValue, this.position);
    }

    /**
     * Getter for width of the object.
     * @return width
     */
    public double getWidth() { return width; }

    /**
     * Setter for width of the object.
     * @param width new width value
     */
    public void setWidth(double width) {
        pcs.firePropertyChange("width", this.width, width);
        this.width = width;
    }

    /**
     * Getter for height of the object.
     * @return height
     */
    public double getHeight() { return height; }

    /**
     * Setter for height of the object.
     * @param height new height value
     */
    public void setHeight(double height) {
        pcs.firePropertyChange("height", this.height, height);
        this.height = height;
    }

    /**
     * Getter for top boundary coordinate of the object.
     * @return position of top edge of GameObject
     */
    public double getTop() { return position.y; }

    /**
     * Setter for top boundary coordinate of the object.
     * @param y of top edge of GameObject
     */
    public void setTop(double y) {
        Vector2D oldValue = position;
        position.y = y;
        pcs.firePropertyChange("position", oldValue, position);
    }

    /**
     * Getter for bottom boundary coordinate of the object.
     * @return position of bottom edge of GameObject
     */
    public double getBot() { return position.y + height; }

    /**
     * Setter for bot boundary coordinate of the object.
     * @param y of bot edge of GameObject
     */
    public void setBot(double y) {
        Vector2D oldValue = position;
        position.y = y - (height + 1);
        pcs.firePropertyChange("position", oldValue, position);
    }

    /**
     * Getter for right boundary coordinate of the object.
     * @return position of right edge of GameObject
     */
    public double getRight() { return position.x + width; }

    /**
     * Setter for right boundary coordinate of the object.
     * @param x of right edge of GameObject
     */
    public void setRight(double x) {
        Vector2D oldValue = position;
        position.x = x - (width + 1);
        pcs.firePropertyChange("position", oldValue, position);
    }

    /**
     * Getter for left boundary coordinate of the object.
     * @return x coordinate of left edge of GameObject
     */
    public double getLeft() { return position.x; }

    /**
     * Setter for left boundary coordinate of the object.
     * @param x of left edge of GameObject
     */
    public void setLeft(double x) {
        Vector2D oldValue = position;
        position.x = x;
        pcs.firePropertyChange("position", oldValue, position);
    }

    /**
     * Getter for top right corner position of the object.
     * @return position Vector2D
     */
    public Vector2D getTopRightCorner() { return new Vector2D(position).add(width, 0); }

    /**
     * Getter for top left corner position of the object.
     * @return position Vector2D
     */
    public Vector2D getTopLeftCorner() { return new Vector2D(position); }

    /**
     * Getter for bottom right corner position of the object.
     * @return position Vector2D
     */
    public Vector2D getBotRightCorner() { return new Vector2D(position).add(width, height); }

    /**
     * Getter for bottom left corner position of the object.
     * @return position Vector2D
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