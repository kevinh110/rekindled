/*
 * WorldController.java
 *
 * This is the most important new class in this lab.  This class serves as a combination
 * of the CollisionController and GameplayController from the previous lab.  There is not
 * much to do for collisions; Box2d takes care of all of that for us.  This controller
 * invokes Box2d and then performs any after the fact modifications to the data
 * (e.g. gameplay).
 *
 * If you study this class, and the contents of the edu.cornell.cs3152.physics.obstacles
 * package, you should be able to understand how the Physics engine works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import edu.cornell.gdiac.util.*;

/**
 * Base class for a world-specific controller.
 *
 *
 * A world has its own objects, assets, and input controller.  Thus this is
 * really a mini-GameEngine in its own right.  The only thing that it does
 * not do is create a GameCanvas; that is shared with the main application.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GameplayController {
	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 *
	 * @param manager Reference to global asset manager.
	 */
	public void preLoadContent(AssetManager manager, Array<String> assets) {
		manager.load(PLAYER_FILE, Texture.class);
		assets.add(PLAYER_FILE);
	}

	/**
	 * Loads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 *
	 * @param manager Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {
		playerTexture = createTexture(manager,PLAYER_FILE);
		player.setTexture(playerTexture);
	}

	private Texture createTexture(AssetManager manager, String file) {
		if (manager.isLoaded(file)) {
			Texture texture = manager.get(file, Texture.class);
			texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			return texture;
		}
		return null;
	}

	/**
	 * Unloads the assets for this game.
	 *
	 * This method erases the static variables.  It also deletes the associated textures
	 * from the asset manager. If no assets are loaded, this method does nothing.
	 *
	 * @param manager Reference to global asset manager.
	 */
	public void unloadContent(AssetManager manager, Array<String> assets) {
		for(String s : assets) {
			if (manager.isLoaded(s)) {
				manager.unload(s);
			}
		}
	}

	/** Width of the game world in Box2d units */
	protected static final float DEFAULT_WIDTH  = 32.0f;
	/** Height of the game world in Box2d units */
	protected static final float DEFAULT_HEIGHT = 18.0f;

	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The Game Board*/
	private Board board;
	/** Board width in tiles*/
	private static final int BOARD_WIDTH = 10;
	/** Board Height in tiles*/
	private static final int BOARD_HEIGHT = 8;

	private Player player;

	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;

	/** File storing the enemy texture for a ship */
	private static final String PLAYER_FILE  = "images/player.png";
	/** Texture for beam */
	private Texture playerTexture;

	private int[] walls = {3, 4, 3, 5, 3, 6, 3, 7};
	private int[] dimSources = {5, 3, 2, 4};
	private int[] litSources = {2, 2, 5, 6};


	/**
	 * Creates a new game world with the default values.
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 */
	protected GameplayController(Array<String> assets) {
		this(new Rectangle(0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT), assets);
	}


	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param bounds	The game bounds in Box2d coordinates
	 */
	protected GameplayController(Rectangle bounds, Array<String> assets) {
		board = new Board(BOARD_WIDTH, BOARD_HEIGHT, walls, litSources, dimSources);
		player = new Player(board.boardToScreen(1), board.boardToScreen(1), 2);
		this.bounds = new Rectangle(bounds);
		this.scale = new Vector2(1,1);
	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void update(float dt){
		InputController input = InputController.getInstance();
		input.readInput(bounds, scale);

		if (board.isCenterOfTile(player.getPosition())){
			player.setMoving(false);
		}

		if(input.didUp()){
			player.move(0, board.getTileSize() + board.getTileSpacing());
		}
		else if (input.didDown()){
			player.move(0, -board.getTileSize() - board.getTileSpacing());
		}
		else if (input.didLeft()){
			player.move(-board.getTileSize() - board.getTileSpacing(), 0);
		}
		else if (input.didRight()){
			player.move(board.getTileSize() + board.getTileSpacing(), 0);
		}
		if (board.isObstructed(player.getGoal())){
			player.setMoving(false);
		}

		player.update();
		board.update();
		//enemy.update();
	}


	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
	}

	public boolean isAlive(){
		return true;
	}

	public boolean won(){
		return false;
	}

	/**
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself.  It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 *
	 * @param delta The drawing context
	 */
	public void draw(GameCanvas canvas, float delta) {
		board.draw(canvas);
		player.draw(canvas);

	}
}