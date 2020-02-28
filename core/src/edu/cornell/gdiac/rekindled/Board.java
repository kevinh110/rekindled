/*
 * Board.java
 *
 * This class keeps track of all the tiles in the game. If a photon hits
 * a ship on a Tile, then that Tile falls away.
 *
 * Because of this gameplay, there clearly has to be a lot of interaction
 * between the Board, Ships, and Photons.  However, this way leads to
 * cyclical references.  As we will discover later in the class, cyclic
 * references are bad, because they lead to components that are too
 * tightly coupled.
 *
 * To address this problem, this project uses a philosophy of "passive"
 * models.  Models do not access the methods or fields of any other
 * Model class.  If we need for two Model objects to interact with
 * one another, this is handled in a controller class. This can get
 * cumbersome at times (particularly in the coordinate transformation
 * methods in this class), but it makes it easier to modify our
 * code in the future.
 *
 * Author: Walker M. White, Cristian Zaloj
 * Based on original AI Game Lab by Yi Xu and Don Holden, 2007
 * LibGDX version, 1/24/2015
 */
package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

//import edu.cornell.gdiac.mesh.*;

/**
 * Class represents a 2D grid of tiles.
 *
 * Most of the work is done by the internal Tile class.  The outer class is
 * really just a container.
 */
public class Board {

    /**
     * Each tile on the board has a set of attributes associated with it.
     * However, no class other than board needs to access them directly.
     * Therefore, we make this an inner class.
     */
    private static class TileState {
        public boolean isLightSource;
        public boolean isLitLightSource;
        public boolean isWall;
        public boolean isLitTile;

        /** Is this a goal tiles */
        public boolean goal = false;
        /** Has this tile been visited (used for pathfinding)? */
        public boolean visited = false;
        /** Is this tile falling */
        public boolean falling = false;
        /** How far the tile has fallen */
        public float fallAmount = 0;

        public final int lightRadius = 3;

        private void setWall(){
            isWall = true;
            isLitLightSource = false;
            isLightSource = false;
            isLitTile = false;
        }

        private void setLitLightSource(){
            isWall = false;
            isLitLightSource = true;
            isLightSource = true;
            isLitTile = true;
        }

        private void setDimLightSource(){
            isWall = false;
            isLitLightSource = false;
            isLightSource = true;
            isLitTile = false;
        }
    }

    // Constants
    /** Space to leave open between tiles */
    private static final float TILE_SPACE = 0;
    /** The dimensions of a single tile */
    private static final int TILE_WIDTH = 64; // MUST BE 2X VALUE IN GAMECANVAS

    //images
    /** The file location of the light tile*/
    private static final String LIGHT_TILE = "images/LightTile.png";
    /** The file location of the dark tile*/
    private static final String DARK_TILE = "images/DarkTile.png";
    /** The file location of the wall*/
    private static final String WALL = "images/wall.png";
    /** The file location of a lit light source*/
    private static final String LIT_SOURCE = "images/litLightSource.png";
    /** The file location of a dim light source*/
    private static final String DIM_SOURCE = "images/dimLightSource.png";




    // Instance attributes
    /** The board width (in number of tiles) */
    private int width;
    /** The board height (in number of tiles) */
    private int height;
    /** The tile grid (with above dimensions) */
    private TileState[][] tiles;
    /**The texture for the light tile*/
    private Texture lightTile;
    /**the texture for the dark tile**/
    private Texture darkTile;
    /**the texture for the wall**/
    private Texture wallTexture;
    /**texture region for dim light source*/;
    private Texture dimLightSource;
    /**texture region for lit light source*/;
    private Texture litLightSource;

    /**texture region for lightTile*/;
    private TextureRegion lightRegion
    /**texture region for darkTile*/;
    private TextureRegion darkRegion;
    /**texture region for lit source*/
    private TextureRegion litSourceRegion;
            /**texture region for dim source*/;
    private TextureRegion dimSourceRegion;
    /**texture region for wall*/
    private TextureRegion wallRegion;

    /**
     * Creates a new board of the given size
     *
     * @param width Board width in tiles
     * @param height Board height in tiles
     */
    public Board(int width, int height, int[] walls, int[] litSources, int[] dimSources) {
        this.width = width;
        this.height = height;
        this.darkTile = new Texture(DARK_TILE);
        this.lightTile = new Texture(LIGHT_TILE);
        this.wallTexture = new Texture(WALL);
        this.dimLightSource = new Texture(DIM_SOURCE);
        this.litLightSource = new Texture(LIT_SOURCE);

        this.darkRegion = new TextureRegion(darkTile, TILE_WIDTH, TILE_WIDTH);
        this.lightRegion = new TextureRegion(lightTile, TILE_WIDTH, TILE_WIDTH);
        this.litSourceRegion = new TextureRegion(litLightSource, TILE_WIDTH, TILE_WIDTH);
        this.dimSourceRegion = new TextureRegion(dimLightSource, TILE_WIDTH, TILE_WIDTH);
        this.wallRegion = new TextureRegion(wallTexture, TILE_WIDTH, TILE_WIDTH);


        tiles = new TileState[width][height];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                tiles[x][y] = new TileState();
            }
        }
        for(int ii = 0; ii < walls.length-1; ii += 2){
            tiles[walls[ii]][walls[ii+1]].setWall();
        }
        for(int ii = 0; ii < litSources.length-1; ii += 2){
            tiles[litSources[ii]][litSources[ii+1]].setLitLightSource();
        }
        for(int ii = 0; ii < dimSources.length-1; ii += 2){
            tiles[dimSources[ii]][dimSources[ii+1]].setDimLightSource();
        }


        //temp code for perimeter
        for (int x = 0; x < tiles.length; x++){
            tiles[x][0].setWall();
            tiles[x][8].setWall();
        }
        for (int y = 0; y < tiles[0].length; y++){
            tiles[0][y].setWall();
            tiles[15][y].setWall();
        }

        resetTiles();
    }

    /**
     * Resets the values of all the tiles on screen.
     */
    public void resetTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState tile = tiles[x][y];
                tile.goal = false;
                tile.visited = false;
                tile.fallAmount = 0.0f;
                tile.falling = false;
            }
        }
    }

    public void reset(int[] walls, int[] litSources, int[] dimSources){
        for(int ii = 0; ii < walls.length-1; ii += 2){
            tiles[walls[ii]][walls[ii+1]].setWall();
        }
        for(int ii = 0; ii < litSources.length-1; ii += 2){
            tiles[litSources[ii]][litSources[ii+1]].setLitLightSource();
        }
        for(int ii = 0; ii < dimSources.length-1; ii += 2){
            tiles[dimSources[ii]][dimSources[ii+1]].setDimLightSource();
        }
    }

    /**
     * Returns the number of tiles horizontally across the board.
     *
     * @return the number of tiles horizontally across the board.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of tiles vertically across the board.
     *
     * @return the number of tiles vertically across the board.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the size of the tile texture.
     *
     * @return the size of the tile texture.
     */
    public int getTileSize() {
        return TILE_WIDTH;
    }

    /**
     * Returns the amount of spacing between tiles.
     *
     * @return the amount of spacing between tiles.
     */
    public float getTileSpacing() {
        return TILE_SPACE;
    }


    // GAME LOOP
    // This performs any updates local to the board (e.g. animation)

    /**
     * Updates the state of all of the tiles.
     *
     * All we do is animate falling tiles.
     */
    public void update() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState tile = tiles[x][y];
            }
        }
    }

    /**
     * Draws the board to the given canvas.
     *
     * This method draws all of the tiles in this board. It should be the first drawing
     * pass in the GameEngine.
     *
     * @param canvas the drawing context
     */
    public void draw(GameCanvas canvas) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                drawTile(x, y, canvas);
            }
        }
    }

    /**
     * Draws the individual tile at position (x,y).
     *
     * Fallen tiles are not drawn.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    private void drawTile(int x, int y, GameCanvas canvas) {
        TileState tile = tiles[x][y];

        // Compute drawing coordinates
        float sx = boardToScreen(x);
        float sy = boardToScreen(y);

        if(tile.isWall)
            canvas.draw(wallRegion,  sx-(getTileSize()-getTileSpacing())/2, sy-(getTileSize()-getTileSpacing())/2);
        else if (tile.isLitTile)
            canvas.draw(lightRegion,  sx-(getTileSize()-getTileSpacing())/2, sy-(getTileSize()-getTileSpacing())/2);
        else
            canvas.draw(darkRegion,  sx-(getTileSize()-getTileSpacing())/2, sy-(getTileSize()-getTileSpacing())/2);
        if (tile.isLightSource){
            if(tile.isLitLightSource)
                canvas.draw(litSourceRegion,  sx-(getTileSize()-getTileSpacing())/2, sy-(getTileSize()-getTileSpacing())/2);
            else
                canvas.draw(dimSourceRegion,  sx-(getTileSize()-getTileSpacing())/2, sy-(getTileSize()-getTileSpacing())/2);
        }
    }

    // METHODS FOR LAB 2

    // CONVERSION METHODS (OPTIONAL)
    // Use these methods to convert between tile coordinates (int) and
    // world coordinates (float).

    /**
     * Returns the board cell index for a screen position.
     *
     * While all positions are 2-dimensional, the dimensions to
     * the board are symmetric. This allows us to use the same
     * method to convert an x coordinate or a y coordinate to
     * a cell index.
     *
     * @param f Screen position coordinate
     *
     * @return the board cell index for a screen position.
     */
    public int screenToBoard(float f) {
        return (int)(f / (getTileSize() + getTileSpacing()));
    }

    /**
     * Returns if the given position is at center of a tile.
     */
    public boolean isCenterOfTile(Vector2 position){
        float nearestCenterX = boardToScreen(screenToBoard(position.x));
        float nearestCenterY = boardToScreen(screenToBoard(position.y));
        if(Math.abs(nearestCenterX - position.x) < .001 && Math.abs(nearestCenterY - position.y) < .001){
            return true;
        }
        return false;
    }

    /**
     * Returns the screen position coordinate for a board cell index.
     *
     * While all positions are 2-dimensional, the dimensions to
     * the board are symmetric. This allows us to use the same
     * method to convert an x coordinate or a y coordinate to
     * a cell index.
     *
     * @param n Tile cell index
     *
     * @return the screen position coordinate for a board cell index.
     */
    public float boardToScreen(int n) {
        return (float) (n + 0.5f) * (getTileSize() + getTileSpacing());
    }

    /**
     * Returns the distance to the tile center in screen coordinates.
     *
     * This method is an implicit coordinate transform. It takes a position (either
     * x or y, as the dimensions are symmetric) in screen coordinates, and determines
     * the distance to the nearest tile center.
     *
     * @param f Screen position coordinate
     *
     * @return the distance to the tile center
     */
    public float centerOffset(float f) {
        float paddedTileSize = getTileSize() + getTileSpacing();
        int cell = screenToBoard(f);
        float nearestCenter = (cell + 0.5f) * paddedTileSize;
        return f - nearestCenter;
    }

    // PATHFINDING METHODS (REQUIRED)
    // Use these methods to implement pathfinding on the board.

    /**
     * Returns true if the given position is a valid tile
     *
     * It does not check whether the tile is live or not.  Dead tiles are still valid.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the given position is a valid tile
     */
    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Returns true if the tile has been visited.
     *
     * A tile position that is not on the board will always evaluate to false.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the tile has been visited.
     */
    public boolean isVisited(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return tiles[x][y].visited;
    }

    /**
     * Marks a tile as visited.
     *
     * A marked tile will return true for isVisited(), until a call to clearMarks().
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    public void setVisited(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Board", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        tiles[x][y].visited = true;
    }

    /**
     * Returns true if the tile is a goal.
     *
     * A tile position that is not on the board will always evaluate to false.
     *
     *
     * @return true if the tile is a goal.
     */
    public boolean isObstructed(Vector2 position) {
        TileState tile = tiles[screenToBoard(position.x)][screenToBoard(position.y)];
        return tile.isWall || tile.isLightSource;
    }

    /**
     * Marks a tile as a goal.
     *
     * A marked tile will return true for isGoal(), until a call to clearMarks().
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    public void setGoal(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Board", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        tiles[x][y].goal = true;
    }


    /**
     * Clears all marks on the board.
     *
     * This method should be done at the beginning of any pathfinding round.
     */
    public void clearMarks() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState state = tiles[x][y];
                state.visited = false;
                state.goal = false;
            }
        }
    }
}