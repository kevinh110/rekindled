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

    public boolean isLit(Vector2 pos) {
        int x = Math.round(pos.x);
        int y = Math.round(pos.y);
        return tiles[x][y].isLitTile;
    }

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
        public boolean isTinted;
        public boolean isWater;

        public boolean topBordered;
        public boolean bottomBordered;
        public boolean leftBordered;
        public boolean rightBordered;

        /** Is this a goal tiles */
        public boolean goal = false;
        /** Has this tile been visited (used for pathfinding)? */
        public boolean visited = false;

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

    /** The file locations for water */
    private static final String WATER_DARK_FILE = "images/water_tile_dark.png";
    private static final String WATER_LIGHT_FILE = "images/water_tile_light.png";

    /** The file locations for grass edges */
    private static final String UP_BORDER = "images/up-border.png";
    private static final String DOWN_BORDER = "images/down-border.png";
    private static final String LEFT_BORDER = "images/left-border.png";
    private static final String RIGHT_BORDER = "images/right-border.png";

    /** The file locations for unlit edges */
    private static final String UNLIT_UP_BORDER = "images/unlit_borders_up.png";
    private static final String UNLIT_DOWN_BORDER = "images/unlit_borders_down.png";
    private static final String UNLIT_LEFT_BORDER = "images/unlit_borders_left.png";
    private static final String UNLIT_RIGHT_BORDER = "images/unlit_borders_right.png";

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

    private Texture waterDarkTile;
    private Texture waterLightTile;

    private Texture upBorder;
    private Texture downBorder;
    private Texture leftBorder;
    private Texture rightBorder;

    private Texture unlitUpBorder;
    private Texture unlitDownBorder;
    private Texture unlitLeftBorder;
    private Texture unlitRightBorder;

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

    private TextureRegion waterLightRegion;
    private TextureRegion waterDarkRegion;

    private TextureRegion upBorderRegion;
    private TextureRegion downBorderRegion;
    private TextureRegion leftBorderRegion;
    private TextureRegion rightBorderRegion;

    private TextureRegion unlitUpBorderRegion;
    private TextureRegion unlitDownBorderRegion;
    private TextureRegion unlitLeftBorderRegion;
    private TextureRegion unlitRightBorderRegion;

    private static final Color sightTint = Color.SALMON;

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
        this.waterDarkTile = new Texture(WATER_DARK_FILE);
        this.waterLightTile = new Texture(WATER_LIGHT_FILE);

        this.upBorder = new Texture(UP_BORDER);
        this.downBorder = new Texture(DOWN_BORDER);
        this.leftBorder = new Texture(LEFT_BORDER);
        this.rightBorder = new Texture(RIGHT_BORDER);

        this.unlitUpBorder = new Texture(UNLIT_UP_BORDER);
        this.unlitDownBorder = new Texture(UNLIT_DOWN_BORDER);
        this.unlitLeftBorder = new Texture(UNLIT_LEFT_BORDER);
        this.unlitRightBorder = new Texture(UNLIT_RIGHT_BORDER);

        this.darkRegion = new TextureRegion(darkTile, TILE_WIDTH, TILE_WIDTH);
        this.lightRegion = new TextureRegion(lightTile, TILE_WIDTH, TILE_WIDTH);
        this.litSourceRegion = new TextureRegion(litLightSource, TILE_WIDTH, TILE_WIDTH);
        this.dimSourceRegion = new TextureRegion(dimLightSource, TILE_WIDTH, TILE_WIDTH);
        this.wallRegion = new TextureRegion(wallTexture, TILE_WIDTH, TILE_WIDTH);

        this.waterLightRegion = new TextureRegion(waterLightTile, TILE_WIDTH, TILE_WIDTH);
        this.waterDarkRegion = new TextureRegion(waterDarkTile, TILE_WIDTH, TILE_WIDTH);

        this.upBorderRegion = new TextureRegion(upBorder, TILE_WIDTH + 16, TILE_WIDTH + 16);
        this.downBorderRegion = new TextureRegion(downBorder, TILE_WIDTH + 16, TILE_WIDTH + 16);
        this.leftBorderRegion = new TextureRegion(leftBorder, TILE_WIDTH + 16, TILE_WIDTH + 16);
        this.rightBorderRegion = new TextureRegion(rightBorder, TILE_WIDTH + 16, TILE_WIDTH + 16);

        this.unlitUpBorderRegion = new TextureRegion(unlitUpBorder, TILE_WIDTH + 16, TILE_WIDTH + 16);
        this.unlitDownBorderRegion = new TextureRegion(unlitDownBorder, TILE_WIDTH + 16, TILE_WIDTH + 16);
        this.unlitLeftBorderRegion = new TextureRegion(unlitLeftBorder, TILE_WIDTH + 16, TILE_WIDTH + 16);
        this.unlitRightBorderRegion = new TextureRegion(unlitRightBorder, TILE_WIDTH + 16, TILE_WIDTH + 16);

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

    public Board(int width, int height, int[] walls, LightSourceObject[] lights, int[] water) {
        this(width, height);
        Vector2 temp;
        this.lightSources = new LinkedList<>();

        // Set walls
        for(int ii = 0; ii < walls.length-1; ii += 2){
            tiles[walls[ii]][walls[ii+1]].setWall();
        }
        this.walls = walls;

        // Set Water
        for (int ii = 0; ii < water.length -1 ; ii+= 2){
            tiles[water[ii]][water[ii+1]].isWater = true;
        }

        // Set light sources
        for (LightSourceObject light : lights) {
            temp = light.getPosition();
            lightSources.add((int) temp.x);
            lightSources.add((int) temp.y);
            if (light.isLit()){
                tiles[(int) temp.x][(int) temp.y].setLitLightSource();
                updateLitTiles(temp, false);
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
    public void update(Vector2 pos, float delta, Vector2 center) {
        int xx = Math.round(pos.x);
        int yy = Math.round(pos.y);

        Vector2 temp = new Vector2();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState tile = tiles[x][y];
                tile.isLitTile = false;
                tile.isTinted = false;

                tile.topBordered = false;
                tile.bottomBordered = false;
                tile.leftBordered = false;
                tile.rightBordered = false;
            }
        }

        if (tiles[xx][yy].isLightSource && !tiles[xx][yy].isLitLightSource) {
            updateLitTiles(pos, true);
        }
        for(int ii = 0; ii < lightSources.size() -1; ii += 2){
            TileState source = tiles[lightSources.get(ii)][lightSources.get(ii+1)];
            //URGENT: Change so new vector is not created
            if (source.isLitLightSource) {
                temp.set(lightSources.get(ii), lightSources.get(ii + 1));

                updateLitTiles(temp, false);
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

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                drawLit(x, y, canvas);
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


        Color tint = (tile.isTinted) ? Color.CYAN : Color.WHITE;
        tint = Color.WHITE;

        if (tile.isLitTile && tile.isWater){
            canvas.draw(waterLightRegion, tint, 0, 0, sx, sy, 0, 1 , 1 );
        } else if (tile.isWater){
            canvas.draw(waterDarkRegion, tint, 0, 0, sx, sy, 0, 1 , 1 );
        } else {
            canvas.draw(darkRegion, tint, 0, 0, sx, sy, 0, 1 , 1 );
        }
    }

    private void drawLit(int x, int y, GameCanvas canvas) {
        TileState tile = tiles[x][y];
        // Compute drawing coordinates
        float sx = boardToScreenCenter(x);
        float sy = boardToScreenCenter(y);

        if (tile.isTinted) {
            if (tile.topBordered)
                canvas.draw(unlitUpBorderRegion, Color.WHITE, 6, 5, sx, sy, 0, 1, 1);
            if (tile.bottomBordered)
                canvas.draw(unlitDownBorderRegion, Color.WHITE, 9, 11, sx, sy, 0, 1, 1);
            if (tile.leftBordered)
                canvas.draw(unlitLeftBorderRegion, Color.WHITE, 11, 12, sx, sy, 0, 1, 1);
            if (tile.rightBordered)
                canvas.draw(unlitRightBorderRegion, Color.WHITE, 4, 1, sx, sy, 0, 1, 1);
        }
        if (tile.isLitTile && !tile.isWater) {
            canvas.draw(lightRegion, Color.WHITE, 0, 0, sx, sy, 0, 1, 1);
            canvas.draw(upBorderRegion, Color.WHITE, 5, 8, sx, sy, 0, 1, 1);
            canvas.draw(downBorderRegion, Color.WHITE, 8, 10, sx, sy, 0, 1, 1);
            canvas.draw(leftBorderRegion, Color.WHITE, 11, 8, sx, sy, 0, 1, 1);
            canvas.draw(rightBorderRegion, Color.WHITE, 5, 8, sx, sy, 0, 1, 1);
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
        return Math.round(f);
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

    public boolean isWall(int x, int y){
        if (inBounds(x, y)){
            return tiles[x][y].isWall;
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
        return ((float) (n) * (getTileSize())) - (getTileSize() / 2f);
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
        return tile.isWall || tile.isLightSource || tile.isWater;
    }

    public boolean isObstructedBoard(int x, int y) {
        TileState tile = tiles[x][y];
        return tile.isWall || tile.isLightSource || tile.isWater;
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
        if (x > width || y > height){
            return false;
        }
        TileState tile = tiles[x][y];
        return tile.isLitTile;
    }

    public boolean isEnemyMovable(int x, int y){
        if (x >= width || y >= height){
            return false;
        }
        TileState tile = tiles[x][y];
        return tile.isWall || tile.isLitTile || tile.isWater;
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

    public void clearLightandSeen() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState state = tiles[x][y];
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

    public void updateLitTiles(Vector2 source, boolean tint) {
        int x = Math.round(source.x);
        int y = Math.round(source.y);
        if (tint)
            setTinted(x, y);
        else
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
        spreadLight(LIGHT_RADIUS, x, y, top, bottom, left, right, tint);
    }


    public void spreadLight(int depth, int x, int y, boolean top, boolean bottom, boolean left, boolean right, boolean tint) {
        if (depth == 0)
            return;

        if (top) {
            int xx = x;
            int yy = y+1;
            TileState tile = tiles[xx][yy];
            if (tint)
                setTinted(xx, yy);
            else
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
            spreadLight(depth-1, xx, yy, top1, bottom1, left1, right1, tint);
        }

        if (bottom) {
            int xx = x;
            int yy = y-1;
            TileState tile = tiles[xx][yy];
            if (tint)
                setTinted(xx, yy);
            else
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
            spreadLight(depth-1, xx, yy, top1, bottom1, left1, right1,  tint);
        }
        if (left) {
            int xx = x -1;
            int yy = y;
            TileState tile = tiles[xx][yy];
            if (tint)
                setTinted(xx, yy);
            else
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
            spreadLight(depth-1, xx, yy, top1, bottom1, left1, right1, tint);
        }
        if (right) {
            int xx = x+1;
            int yy = y;
            TileState tile = tiles[xx][yy];
            if (tint)
                setTinted(xx, yy);
            else
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
            spreadLight(depth-1, xx, yy, top1, bottom1, left1, right1, tint);
        }
    }

    private void setTinted(int x, int y) {
        TileState tile = tiles[x][y];

        TileState top = null;
        TileState bottom = null;
        TileState left = null;
        TileState right = null;

        if (inBounds(x, y+1))
            top = tiles[x][y+1];
        if (inBounds(x, y-1))
            bottom = tiles[x][y-1];
        if (inBounds(x-1, y))
            left = tiles[x-1][y];
        if (inBounds(x+1, y))
            right = tiles[x+1][y];


        if (tile.isTinted)
            return;
        else {
            tile.isTinted = true;

            // top border if above tile is not bottom bordered
            if (top != null && !top.bottomBordered)
                tile.topBordered = true;
            // bottom border if bottom tile is not top bordered
            if (bottom != null && !bottom.topBordered)
                tile.bottomBordered = true;
            // left border if left tile is not right bordered
            if (left != null && !left.rightBordered)
                tile.leftBordered = true;
            // right border if right tile is not left bordered
            if (right != null && !right.leftBordered)
                tile.rightBordered = true;
        }
    }

    public void dispose() {
    }
}