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

import java.util.LinkedList;

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
        public boolean isDimTile;

        /** Is this a goal tiles */
        public boolean goal = false;
        /** Has this tile been visited (used for pathfinding)? */
        public boolean visited = false;

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

        public void setLit() {
            isLitTile = true;
        }

        public void setDim() {
            isDimTile = true;
        }
    }

    // Constants
    /** Space to leave open between tiles */
    private static final float TILE_SPACE = 0;
    /** The dimensions of a single tile */
    private static final int TILE_WIDTH = 32; // MUST BE 2X VALUE IN GAMECANVAS

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
    private static final int LIGHT_RADIUS = 2;

    // Instance attributes
    /** The board width (in number of tiles) */
    private int width;
    /** The board height (in number of tiles) */
    private int height;

    /** Walls in index coordinates.`Used for Line of sight calculations.
     */
    public int[] walls;

    private LinkedList<Integer> lightSources;
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

    public Board(int width, int height){
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

        Vector2 temp = new Vector2();
        this.lightSources = new LinkedList<>();

        // Init Tiles
        tiles = new TileState[width][height];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                tiles[x][y] = new TileState();
            }
        }
             // Set perimeter as wall
        for (int x = 0; x < tiles.length; x++){
            tiles[x][0].setWall();
            tiles[x][this.height - 1].setWall();
        }
        for (int y = 0; y < tiles[0].length; y++){
            tiles[0][y].setWall();
            tiles[this.width - 1][y].setWall();
        }

        // Resets visited/goal flags of tiles only. Used for pathfinding.
        // I don't know if this needs to be here
        resetTiles();



    }

    public Board(int width, int height, int[] walls, int[] litSources, int[] dimSources) {
        this(width, height);
        Vector2 temp = new Vector2();
        this.lightSources = new LinkedList<>();

        // Set walls
        for(int ii = 0; ii < walls.length-1; ii += 2){
            tiles[walls[ii]][walls[ii+1]].setWall();
        }
        this.walls = walls;

        // Set lit sources
        for(int ii = 0; ii < litSources.length-1; ii += 2){
            lightSources.add(litSources[ii]);
            lightSources.add(litSources[ii + 1]);
            tiles[litSources[ii]][litSources[ii+1]].setLitLightSource();
            //URGENT: Change so new vector is not created
            temp.set(litSources[ii], litSources[ii+1]);
            updateLitTiles(temp);
        }

        // Set dim sources
        for(int ii = 0; ii < dimSources.length-1; ii += 2){
            lightSources.add(dimSources[ii]);
            lightSources.add(dimSources[ii + 1]);
            tiles[dimSources[ii]][dimSources[ii+1]].setDimLightSource();
        }

        // Resets visited/goal flags of tiles only. Used for pathfinding.
        resetTiles();
    }

    public Board(int width, int height, int[] walls, LightSourceObject[] lights) {
        this(width, height);
        Vector2 temp;
        this.lightSources = new LinkedList<>();

        // Set walls
        for(int ii = 0; ii < walls.length-1; ii += 2){
            tiles[walls[ii]][walls[ii+1]].setWall();
        }
        this.walls = walls;

        // Set light sources
        for (LightSourceObject light : lights) {
            temp = light.getPosition();
            lightSources.add((int) temp.x);
            lightSources.add((int) temp.y);
            if (light.isLit()){
                tiles[(int) temp.x][(int) temp.y].setLitLightSource();
                updateLitTiles(temp);
            }
            else {
                tiles[(int) temp.x][(int) temp.y].setDimLightSource();
            }
        }

        // Resets visited/goal flags of tiles only. Used for pathfinding.
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
            }
        }
    }

    public boolean isSafeAt(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
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

    public void reset(int[] walls, LightSourceObject[] lights){
        for(int ii = 0; ii < walls.length-1; ii += 2){
            tiles[walls[ii]][walls[ii+1]].setWall();
        }
        Vector2 temp;
        for (LightSourceObject light : lights){
            temp = light.getPosition();
            if (light.isLit()){
                tiles[(int) temp.x][(int) temp.y].setLitLightSource();
            }
            else {
                tiles[(int) temp.x][(int) temp.y].setDimLightSource();
            }
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
    public int getTileSize()  {
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
        Vector2 temp = new Vector2();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState tile = tiles[x][y];
                tile.isLitTile = false;
            }
        }
        for(int ii = 0; ii < lightSources.size() -1; ii += 2){
            TileState source = tiles[lightSources.get(ii)][lightSources.get(ii+1)];
            //URGENT: Change so new vector is not created
            if (source.isLitLightSource) {
                temp.set(lightSources.get(ii), lightSources.get(ii + 1));

                updateLitTiles(temp);
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
        float sx = boardToScreenCenter(x);
        float sy = boardToScreenCenter(y);

        if (tile.isLitTile){
            canvas.draw(lightRegion,  sx-(getTileSize()-getTileSpacing())/2, sy-(getTileSize()-getTileSpacing())/2);
        }
        else if (tile.isDimTile)
            canvas.draw(darkRegion, Color.YELLOW,  sx-(getTileSize()-getTileSpacing())/2, sy-(getTileSize()-getTileSpacing())/2, darkRegion.getRegionWidth(), darkRegion.getRegionHeight());
        else {
            canvas.draw(darkRegion,  sx-(getTileSize()-getTileSpacing())/2, sy-(getTileSize()-getTileSpacing())/2);
        }
    }

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
     * the same as board to screen but assumes grids are drawn from center.
     * May break other things.
     * @param n
     * @return
     */
    public float boardToScreenCenter(int n) {
        return (float) (n) * (getTileSize() + getTileSpacing());
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

    public boolean isSameTile(Vector2 pos1, Vector2 pos2){
        return screenToBoard(pos1.x) == screenToBoard(pos2.x) && screenToBoard(pos1.y) == screenToBoard(pos2.y);
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

    public boolean isObstructedBoard(int x, int y) {
        TileState tile = tiles[x][y];
        return tile.isWall || tile.isLightSource;
    }

    public boolean isLitTile(Vector2 position){
        TileState tile = tiles[screenToBoard(position.x)][screenToBoard(position.y)];
        return tile.isLitTile;
    }

    public boolean isDimTile(Vector2 position) {
        TileState tile = tiles[screenToBoard(position.x)][screenToBoard(position.y)];
        return tile.isDimTile;
    }

    public boolean isLitTileBoard(int x, int y){
        TileState tile = tiles[x][y];
        return tile.isLitTile;
    }

    public boolean isEnemyMovable(int x, int y){
        TileState tile = tiles[x][y];
        return tile.isWall || tile.isLightSource || tile.isLitTile;
    }

    public boolean isLitLightSource(Vector2 position){
        TileState tile = tiles[screenToBoard(position.x)][screenToBoard(position.y)];
        return tile.isLitLightSource;
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

    public boolean isGoal(int x, int y){
        return tiles[x][y].goal;
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

    public void clearVisited() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState state = tiles[x][y];
                state.visited = false;
            }
        }
    }

    public void clearLight() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState state = tiles[x][y];
               // state.isLitTile = false;
                state.isDimTile = false;
            }
        }
    }

    //Player has to be adjacent to light source
    //May change later
    public boolean inLightInteractRange(float x, float y) {
        int xx = screenToBoard(x);
        int yy = screenToBoard(y);

        boolean interact = false;
        for (int i = xx - 1; i <= xx + 1; i++) {
            for (int j = yy - 1; j <= yy + 1; j++) {
                TileState tile = tiles[i][j];
                if (tile.isLightSource) {
                    interact = true;
                }
            }
        }
        return interact;
    }

    public Vector2 getInteractedSource(float x, float y) {
        int xx = screenToBoard(x);
        int yy = screenToBoard(y);

        for (int i = xx - 1; i <= xx + 1; i++) {
            for (int j = yy - 1; j <= yy + 1; j++) {
                TileState tile = tiles[i][j];
                if (tile.isLightSource) {
                    return new Vector2(i, j);
                }
            }
        }
        return new Vector2(0,0);
    }

    public boolean getSourceOn(Vector2 source) {
        return tiles[(int)source.x][(int)source.y].isLitLightSource;

    }

    public void turnSourceOff(Vector2 source) {
        tiles[(int)source.x][(int)source.y].isLitLightSource = false;
    }

    public void turnSourceOn(Vector2 source) {
        tiles[(int)source.x][(int)source.y].isLitLightSource = true;
//        updateLitTiles(source);
    }

    public void toggleSource(Vector2 source){
        tiles[(int) source.x][(int) source.y].isLitLightSource = !tiles[(int)source.x][(int)source.y].isLitLightSource;
    }

    private void updateLitTiles(Vector2 source) {
        int x = (int) source.x;
        int y = (int) source.y;
        tiles[x][y].setLit();

        boolean top = false;
        boolean bottom = false;
        boolean left = false;
        boolean right = false;

        if (inBounds(x, y + 1)) {
            TileState tile = tiles[x][y+1];
            if (!tile.isWall)
                top = true;
        }
        if (inBounds(x, y - 1)) {
            TileState tile = tiles[x][y-1];
            if (!tile.isWall)
                bottom = true;
        }
        if (inBounds(x-1, y)) {
            TileState tile = tiles[x-1][y];
            if (!tile.isWall)
                left = true;
        }
        if (inBounds(x+1, y)) {
            TileState tile = tiles[x+1][y];
            if (!tile.isWall)
                right = true;
        }
        spreadLight(LIGHT_RADIUS, x, y, top, bottom, left, right);
    }

    public void spreadLight(int depth, int x, int y, boolean top, boolean bottom, boolean left, boolean right) {
        if (depth == 0)
            return;

        if (top) {
            int xx = x;
            int yy = y+1;
            TileState tile = tiles[xx][yy];
            tile.setLit();

            boolean top1 = false;
            boolean bottom1 = false;
            boolean left1 = false;
            boolean right1 = false;

            if (inBounds(xx, yy + 1)) {
                tile = tiles[xx][yy+1];
                if (!tile.isWall)
                    top1 = true;
            }
            if (inBounds(xx, yy - 1)) {
                tile = tiles[xx][yy-1];
                if (!tile.isWall)
                    bottom1 = true;
            }
            if (inBounds(xx-1, yy)) {
                tile = tiles[xx-1][yy];
                if (!tile.isWall)
                    left1 = true;
            }
            if (inBounds(xx+1, yy)) {
                tile = tiles[xx+1][yy];
                if (!tile.isWall)
                    right1 = true;
            }
            spreadLight(depth-1, xx, yy, top1, bottom1, left1, right1);
        }

        if (bottom) {
            int xx = x;
            int yy = y-1;
            TileState tile = tiles[xx][yy];
            tile.setLit();

            boolean top1 = false;
            boolean bottom1 = false;
            boolean left1 = false;
            boolean right1 = false;

            if (inBounds(xx, yy + 1)) {
                tile = tiles[xx][yy+1];
                if (!tile.isWall)
                    top1 = true;
            }
            if (inBounds(xx, yy - 1)) {
                tile = tiles[xx][yy-1];
                if (!tile.isWall)
                    bottom1 = true;
            }
            if (inBounds(xx-1, yy)) {
                tile = tiles[xx-1][yy];
                if (!tile.isWall)
                    left1 = true;
            }
            if (inBounds(xx+1, yy)) {
                tile = tiles[xx+1][yy];
                if (!tile.isWall)
                    right1 = true;
            }
            spreadLight(depth-1, xx, yy, top1, bottom1, left1, right1);
        }
        if (left) {
            int xx = x -1;
            int yy = y;
            TileState tile = tiles[xx][yy];
            tile.setLit();

            boolean top1 = false;
            boolean bottom1 = false;
            boolean left1 = false;
            boolean right1 = false;

            if (inBounds(xx, yy + 1)) {
                tile = tiles[xx][yy+1];
                if (!tile.isWall)
                    top1 = true;
            }
            if (inBounds(xx, yy - 1)) {
                tile = tiles[xx][yy-1];
                if (!tile.isWall)
                    bottom1 = true;
            }
            if (inBounds(xx-1, yy)) {
                tile = tiles[xx-1][yy];
                if (!tile.isWall)
                    left1 = true;
            }
            if (inBounds(xx+1, yy)) {
                tile = tiles[xx+1][yy];
                if (!tile.isWall)
                    right1 = true;
            }
            spreadLight(depth-1, xx, yy, top1, bottom1, left1, right1);
        }
        if (right) {
            int xx = x+1;
            int yy = y;
            TileState tile = tiles[xx][yy];
            tile.setLit();

            boolean top1 = false;
            boolean bottom1 = false;
            boolean left1 = false;
            boolean right1 = false;

            if (inBounds(xx, yy + 1)) {
                tile = tiles[xx][yy+1];
                if (!tile.isWall)
                    top1 = true;
            }
            if (inBounds(xx, yy - 1)) {
                tile = tiles[xx][yy-1];
                if (!tile.isWall)
                    bottom1 = true;
            }
            if (inBounds(xx-1, yy)) {
                tile = tiles[xx-1][yy];
                if (!tile.isWall)
                    left1 = true;
            }
            if (inBounds(xx+1, yy)) {
                tile = tiles[xx+1][yy];
                if (!tile.isWall)
                    right1 = true;
            }
            spreadLight(depth-1, xx, yy, top1, bottom1, left1, right1);
        }
    }
    public void dimTiles(Vector2 pos) {
        int x = screenToBoard(pos.x);
        int y = screenToBoard(pos.y);

        tiles[x][y].setDim();

        //top tile
        if (inBounds(x, y + 1))
            tiles[x][y+1].setDim();
        //bottom tile
        if (inBounds(x, y - 1))
            tiles[x][y-1].setDim();
        //left tile
        if (inBounds(x - 1, y))
            tiles[x-1][y].setDim();
        //right tile
        if (inBounds(x + 1, y))
            tiles[x+1][y].setDim();
    }
}