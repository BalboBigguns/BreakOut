package main.java.breakoutgame.GameObjects;

import main.java.breakoutgame.GameFXApp;
import main.java.breakoutgame.GameHelpers.Tile;

import java.io.*;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;

// main container for Blocks
// owns grid with tiles for object collision algorithms
// map structure: 16 x 19
public class Map {
    static final public int MAP_TOP_BOUNDARY = Block.BLOCK_HEIGHT * 2;
    static final public int MAP_WIDTH = GameFXApp.WINDOW_WIDTH; // 640
    static final public int MAP_HEIGHT = GameFXApp.WINDOW_HEIGHT - MAP_TOP_BOUNDARY; // 630
    static final public int MAP_GRID_WIDTH = MAP_WIDTH / (Block.BLOCK_WIDTH + 2 * Block.BLOCK_MARGIN);  // 640 / 16 = 40
    static final public int MAP_GRID_HEIGHT = MAP_HEIGHT / (Block.BLOCK_HEIGHT + 2 * Block.BLOCK_MARGIN); // 665 / 35 = 19

    protected ArrayList<Block> blocks;
    protected Tile[][] grid;
    protected GraphicsContext gc;

    public int lives = 3;

    public Map(GraphicsContext gc) {
        this.gc = gc;
        initGrid();
    }

    private void initGrid() {
        grid = new Tile[MAP_GRID_HEIGHT][MAP_GRID_WIDTH];

        for (int i = 0; i < MAP_GRID_HEIGHT; i++) {
            for (int j = 0; j < MAP_GRID_WIDTH; j++) {
                grid[i][j] = new Tile(j, i);
            }
        }
    }

    public Tile[][] getGrid() {
        return grid;
    }

    public boolean loadBlocks(String txtFilePath) {
        FileInputStream in = null;
        byte[] bytes;

        try {
            in = new FileInputStream(txtFilePath);
            bytes = in.readAllBytes();
        } catch(IOException e) {
            System.out.println("Wrong file to load!");
            return false;
        }

        int count = 0;

        for (byte b : bytes) {
            if (b - 48 > 0) {
                count++;
            }
        }

        if (count > 0) {
            blocks = new ArrayList<Block>(count);

            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] - 48 > 0) {
                    // this code takes into account 1 additional byte for line brake in txt file
                    Block b = new Block(this, 
                        ((i - i / (MAP_GRID_WIDTH + 1)) % MAP_GRID_WIDTH) * (Block.BLOCK_WIDTH + 2 * Block.BLOCK_MARGIN) + 1, 
                        ((i - i / (MAP_GRID_WIDTH + 1)) / MAP_GRID_WIDTH) * (Block.BLOCK_HEIGHT + 2 * Block.BLOCK_MARGIN) + 1 + MAP_TOP_BOUNDARY);
                    
                    blocks.add(b);            
                }
            }
        }
        else {
            System.out.println("Error: Nothing loaded from file");
            return false;
        }

        return true;
    }

    public void drawBlocks() {
        for (Block b : blocks) {
            b.draw();
        }
    }

    public void hitBlock(Block blockToDestroy) {
        blockToDestroy.removeFromGrid();
        blocks.remove(blockToDestroy);
    }

    public void log() {
        System.out.println("Tiles: ");
        for (int i = 0; i < MAP_GRID_HEIGHT; i++) {
            for (int j = 0; j < MAP_GRID_WIDTH; j++) {
                System.out.print(grid[i][j].getStoredObjects().size() + " ");
            }
            System.out.println("");
        }
    }
}