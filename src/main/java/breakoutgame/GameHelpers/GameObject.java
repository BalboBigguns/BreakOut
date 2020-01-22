package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.Utils.Loggable;
import main.java.breakoutgame.GameObjects.Map;

public abstract class GameObject implements Loggable{
    protected Vector2D position;
    protected Map map;
    protected double width;
    protected double height;

    public GameObject(Map map, double x, double y, double w, double h) {
        this.map = map;
        width = w;
        height = h;
        position = new Vector2D(x, y);
        initGrid();
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    private void initGrid() {
        for (Tile t : getTiles()) {
            t.putObject(this);
        }
    }

    public Tile[] getTiles() {
        int topRow = getRow(top());
        int botRow = getRow(bot());
        int leftColumn = getColumn(left());
        int rightColumn = getColumn(right());
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
            System.out.println("this obj is broken: ");
            System.out.println(this);
            return null;
        }

        return output;
    }

    public void removeFromGrid() {
        for (Tile t : getTiles()) {
            t.removeObject(this);
        }
    }

    public abstract void draw();


    //HELPERS

    protected void updateGrid() {
        for (Tile t : getTiles()) {
            t.putObject(this);
        }
    }

    protected int getRow(double Ypos) {
        int row = (int)Ypos / Tile.TILE_HEIGHT;
        if (row >= Map.MAP_GRID_HEIGHT) {
            row = Map.MAP_GRID_HEIGHT - 1;
        }
        else if (row < 0) {
            row = 0;
        }
        return row;
    }

    protected int getColumn(double Xpos) {
        int column = (int)Xpos / Tile.TILE_WIDTH;
        if (column >= Map.MAP_GRID_WIDTH) {
            column = Map.MAP_GRID_WIDTH - 1;
        }
        else if (column < 0) {
            column = 0;
        }
        return column;
    }

    public double top() {
        return position.y;
    }

    public double bot() {
        return position.y + height;
    }

    public double right() {
        return position.x + width;
    }

    public double left() {
        return position.x;
    }

    public Vector2D topRightCorner() {
        return new Vector2D(position).add(width, 0);
    }

    public Vector2D topLeftCorner() {
        return new Vector2D(position);
    }

    public Vector2D botRightCorner() {
        return new Vector2D(position).add(width, height);
    }

    public Vector2D botLeftCorner() {
        return new Vector2D(position).add(0, height);
    }

    public String log() {
        return "Position: " + position.toString() + '\n' +
                "Width, height: " + width + '\t' + height + '\n';
    }
}