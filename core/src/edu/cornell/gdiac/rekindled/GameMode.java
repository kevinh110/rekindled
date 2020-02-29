/*
 * GameMode.java
 *
 * This is the primary class file for running the game.  You should study this file for
 * ideas on how to structure your own root class. This class follows a
 * model-view-controller pattern fairly strictly.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;

import edu.cornell.gdiac.util.*;

/**
 * The primary controller class for the game.
 *
 * While GDXRoot is the root class, it delegates all of the work to the player mode
 * classes. This is the player mode class for running the game. In initializes all 
 * of the other classes in the game and hooks them together.  It also provides the
 * basic game loop (update-draw).
 */
public class GameMode implements Screen {
    /**
     * Track the current state of the game for the update loop.
     */
    public enum GameState {
        /** Before the game has started */
        INTRO,
        /** While we are playing the game */
        PLAY,
        /** When the ships is dead (but shells still work) */
        OVER
    }

    // GRAPHICS AND SOUND RESOURCES
    /** The file for the background image to scroll */
    private static String BKGD_FILE = "images/background.png";

    // Loaded assets
    /** The background image for the game */
    private Texture background;

    /** Track all loaded assets (for unloading purposes) */
    private Array<String> assets;

    /**
     * Preloads the assets for this game.
     *
     * The asset manager for LibGDX is asynchronous.  That means that you
     * tell it what to load and then wait while it loads them.  This is
     * the first step: telling it what to load.
     *
     * @param manager Reference to global asset manager.
     */
    public void preLoadContent(AssetManager manager) {
        // Load the background.
        manager.load(BKGD_FILE,Texture.class);
        assets.add(BKGD_FILE);

        // Preload gameplay content
        gameplayController.preLoadContent(manager,assets);
    }

    /**
     * Loads the assets for this game.
     *
     * The asset manager for LibGDX is asynchronous.  That means that you
     * tell it what to load and then wait while it loads them.  This is
     * the second step: extracting assets from the manager after it has
     * finished loading them.
     *
     * @param manager Reference to global asset manager.
     */
    public void loadContent(AssetManager manager) {
        // Allocate the background
        if (manager.isLoaded(BKGD_FILE)) {
            background = manager.get(BKGD_FILE, Texture.class);
            background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        // Load gameplay content
        gameplayController.loadContent(manager);
    }

    /**
     * Unloads the assets for this game.
     *
     * This method erases the static variables.  It also deletes the associated textures
     * from the asset manager.
     *
     * @param manager Reference to global asset manager.
     */
    public void unloadContent(AssetManager manager) {
        for(String s : assets) {
            if (manager.isLoaded(s)) {
                manager.unload(s);
            }
        }
    }

    /// CONSTANTS

    /** Factor used to compute where we are in scrolling process */
    private static final float TIME_MODIFIER    = 0.06f;

    /** Reference to drawing context to display graphics (VIEW CLASS) */
    private GameCanvas canvas;

    /** Reads input from keyboard or game pad (CONTROLLER CLASS) */
    private InputController inputController;
    /** Constructs the game models and handle basic gameplay (CONTROLLER CLASS) */
    private GameplayController gameplayController;

    /** Variable to track the game state (SIMPLE FIELDS) */
    private GameState gameState;
    /** Variable to track total time played in milliseconds (SIMPLE FIELDS) */
    private float totalTime = 0;
    /** Whether or not this player mode is still active */
    private boolean active;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /**
     * Creates a new game with the given drawing context.
     *
     * This constructor initializes the models and controllers for the game.  The
     * view has already been initialized by the root class.
     */
    public GameMode(GameCanvas canvas) {
        this.canvas = canvas;
        active = false;
        // Null out all pointers, 0 out all ints, etc.
        gameState = GameState.INTRO;
        assets = new Array<String>();

        // Create the controllers.
        inputController = new InputController();
        gameplayController = new GameplayController(assets);
    }

    /**
     * Dispose of all (non-static) resources allocated to this mode.
     */
    public void dispose() {
        inputController = null;
        gameplayController = null;
        canvas = null;
    }


    /**
     * Update the game state.
     *
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     *
     * @param delta Number of seconds since last animation frame
     */
    private void update(float delta) {
        // Process the game input
        inputController.readInput(null, null);

        // Test whether to reset the game.
        switch (gameState) {
            case INTRO:
                gameState = GameState.PLAY;
                break;
            case OVER:
                if (inputController.didReset()) {
                    gameState = GameState.PLAY;
                    gameplayController.reset();
                }
                break;
            case PLAY:
                play(delta);
                if(gameplayController.lost() || gameplayController.won()){
                    gameState = GameState.OVER;
                }
                break;
            default:
                break;
        }
    }
    int done = 0;

    /**
     * This method processes a single step in the game loop.
     *
     * @param delta Number of seconds since last animation frame
     */
    protected void play(float delta) {
        // if no player is alive, declare game over
        if (!gameplayController.isAlive()) {
            gameState = GameState.OVER;
        }

        if (gameplayController.won()){
            gameState = GameState.OVER;
        }

        // Update objects.
        gameplayController.update(delta);
    }

    /**
     * Draw the status of this player mode.
     *
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     */
    private void draw(float delta) {
        float offset = -((totalTime * TIME_MODIFIER) % canvas.getWidth());
        canvas.begin();
        gameplayController.draw(canvas, delta);

        // Flush information to the graphic buffer.
        canvas.end();
    }

    /**
     * Called when the Screen is resized.
     *
     * This can happen at any point during a non-paused state but will never happen
     * before a call to show().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    public void resize(int width, int height) {
        // IGNORE FOR NOW
    }

    /**
     * Called when the Screen should render itself.
     *
     * We defer to the other methods update() and draw().  However, it is VERY important
     * that we only quit AFTER a draw.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void render(float delta) {
        if (active) {
            update(delta);
            draw(delta);
            if (inputController.didExit() && listener != null) {
                listener.exitScreen(this, 0);
            }
        }
    }

    /**
     * Called when the Screen is paused.
     *
     * This is usually when it's not active or visible on screen. An Application is
     * also paused before it is destroyed.
     */
    public void pause() {
        // TODO Auto-generated method stub
    }

    /**
     * Called when the Screen is resumed from a paused state.
     *
     * This is usually when it regains focus.
     */
    public void resume() {
        // TODO Auto-generated method stub
    }

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    public void show() {
        // Useless if called in outside animation loop
        active = true;
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
        // Useless if called in outside animation loop
        active = false;
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

}