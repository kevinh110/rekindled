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

import java.util.Arrays;

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
		manager.load(PLAYER_FILE_RIGHT, Texture.class);
		assets.add(PLAYER_FILE_RIGHT);
		manager.load(PLAYER_FILE_LEFT, Texture.class);
		assets.add(PLAYER_FILE_LEFT);
		manager.load(PLAYER_FILE_UP, Texture.class);
		assets.add(PLAYER_FILE_UP);
		manager.load(PLAYER_FILE_DOWN, Texture.class);
		assets.add(PLAYER_FILE_DOWN);
		manager.load(ENEMY_FILE, Texture.class);
		assets.add(ENEMY_FILE);
		manager.load(SAVED_ENEMY_FILE, Texture.class);
		assets.add(SAVED_ENEMY_FILE);
		manager.load(WIN_SCREEN_FILE, Texture.class);
		assets.add(WIN_SCREEN_FILE);
		manager.load(LOSS_SCREEN_FILE, Texture.class);
		assets.add(LOSS_SCREEN_FILE);
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
		playerTextureRight = createTexture(manager,PLAYER_FILE_RIGHT);
		playerTextureLeft = createTexture(manager,PLAYER_FILE_LEFT);
		playerTextureDown = createTexture(manager,PLAYER_FILE_DOWN);
		playerTextureUp = createTexture(manager,PLAYER_FILE_UP);
		player.setTexture(playerTextureRight);
		enemyTexture = createTexture(manager,ENEMY_FILE);
		savedEnemyTexture = createTexture(manager,SAVED_ENEMY_FILE);
		for(Enemy enemy: enemies) {
			enemy.setTexture(enemyTexture);
			enemy.setSecondaryTexture(savedEnemyTexture);
		}
		winScreenTexture = createTexture(manager, WIN_SCREEN_FILE);
		lossScreenTexture = createTexture(manager, LOSS_SCREEN_FILE);
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
	private static final int BOARD_WIDTH = 16;
	/** Board Height in tiles*/
	private static final int BOARD_HEIGHT = 9;
	private static final int TURN_ON_DELAY = 2;

	private float delayTimer;
	private boolean cooldown;

	private Player player;

	private Enemy[] enemies;

	/** Stores all the AI controllers */
	protected AIController[] controls;

	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;

	/** File storing the players */
	private static final String PLAYER_FILE_RIGHT  = "images/player.png";
	private static final String PLAYER_FILE_LEFT  = "images/playerLEFT.png";
	private static final String PLAYER_FILE_DOWN = "images/playerDOWN.png";
	private static final String PLAYER_FILE_UP = "images/playerUP.png";

	/** Textures for player */
	private Texture playerTextureRight;
	private Texture playerTextureLeft;
	private Texture playerTextureDown;
	private Texture playerTextureUp;

	/** File storing the enemy */
	private static final String ENEMY_FILE  = "images/enemy.png";
	/** File storing the saved enemy */
	private static final String SAVED_ENEMY_FILE  = "images/savedEnemy.png";
	/** Texture for enemy */
	private Texture enemyTexture;
	/** Texture for saved enemy */
	private Texture savedEnemyTexture;

	private Texture winScreenTexture;
	private Texture lossScreenTexture;
	private static final String WIN_SCREEN_FILE = "images/winScreen.png";
	private static final String LOSS_SCREEN_FILE = "images/lossScreen.png";

	private int[] walls = {3, 4, 3, 5, 3, 6, 3, 7};
	private int[] dimSources = {5, 3, 2, 4};
	private int[] litSources = {2, 2, 5, 6};
	private int[] enemyLocations = {10, 4, 7, 7};

	CollisionController collisions;

	boolean lostGame;
	boolean wonGame;


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
		player = new Player(board.boardToScreen(1), board.boardToScreen(1), 1f);
		enemies = new Enemy[enemyLocations.length/2];
		for (int ii = 0; ii < enemyLocations.length-1; ii += 2){
			enemies[ii/2] = new Enemy(board.boardToScreen(enemyLocations[ii]), board.boardToScreen(enemyLocations[ii+1]), 2f);
		}
		this.bounds = new Rectangle(bounds);
		this.scale = new Vector2(1,1);
		collisions = new CollisionController(board, enemies, player);
		lostGame = false;
		wonGame = false;
		delayTimer = 0;
		cooldown = false;

		controls = new AIController[enemies.length];
		for(int ii = 0; ii < enemies.length; ii++) {
			controls[ii] = new AIController(enemies[ii],board,player, enemies);
		}
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

		//player movement
		if (board.isCenterOfTile(player.getPosition())){
			player.setMoving(false);
			//reset to center
			player.setPosition(board.boardToScreen(board.screenToBoard(player.getPosition().x)),
					board.boardToScreen(board.screenToBoard(player.getPosition().y)));
		}
		if(input.didUp()){
			if(player.getDirection() == Entity.Direction.UP){
				player.move(0, board.getTileSize() + board.getTileSpacing());
			} else {
				player.setDirection(Entity.Direction.UP);
				player.setTexture(playerTextureUp);
			}

		}
		else if (input.didDown()){
			if(player.getDirection() == Entity.Direction.DOWN){
				player.move(0, -board.getTileSize() - board.getTileSpacing());
			} else {
				player.setDirection(Entity.Direction.DOWN);
				player.setTexture(playerTextureDown);
			}

		}
		else if (input.didLeft()){
			if(player.getDirection() == Entity.Direction.LEFT){
				player.move(-board.getTileSize() - board.getTileSpacing(), 0);
			} else {
				player.setDirection(Entity.Direction.LEFT);
				player.setTexture(playerTextureLeft);
			}

		}
		else if (input.didRight()){
			if(player.getDirection() == Entity.Direction.RIGHT) {
				player.move(board.getTileSize() + board.getTileSpacing(), 0);
			} else {
				player.setDirection(Entity.Direction.RIGHT);
				player.setTexture(playerTextureRight);
			}
		}
		if (board.isObstructed(player.getGoal())){
			player.setMoving(false);
		}

		//update light cooldown
		if (cooldown) {
			delayTimer+= dt;
			if (delayTimer >= TURN_ON_DELAY)
				cooldown = false;
		}

		//placing and taking light
		if (input.didSecondary() && board.inLightInteractRange(player.getPosition().x, player.getPosition().y) && !cooldown) {
			doLightInteraction();
			delayTimer = 0;
			cooldown = true;
		}

		//player-enemy collision
		if(collisions.checkPlayerEnemyCollision()){
			lostGame = true;
		}
		player.update();
		board.clearLight();
		if (player.hasLightRadius())
			board.dimTiles(player.getPosition());

		// Enemy Movement
		for (AIController controller : controls){
			controller.move();
		}

		// Check win Condition
		int numLit = 0;
		for (Enemy e : enemies){
			if (board.isLitTile(e.getPosition())){
				numLit++;
				e.setIsLit(true);
			}
			else
				e.setIsLit(false);
		}
		wonGame = (numLit == enemies.length);
		player.update();
		board.update();
	}


	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		board.reset(walls, litSources, dimSources);
		player.setPosition(board.boardToScreen(1), board.boardToScreen(1));
		player.setLights(0);
		for (int ii = 0; ii < enemyLocations.length-1; ii += 2){
			enemies[ii/2].setPosition(board.boardToScreen(enemyLocations[ii]), board.boardToScreen(enemyLocations[ii+1]));
		}
		lostGame = false;
		wonGame = false;
		cooldown = false;
	}

	public boolean isAlive(){
		return true;
	}

	public boolean won(){
		return wonGame;
	}

	public boolean lost(){
		return lostGame;
	}

	/** Handles Lux's light interactions (placing and taking)
	 *
	 */
	public void doLightInteraction() {
		//find whether or not lux is in range of a light source
		Vector2 pos = player.getPosition();

		System.out.println("in Range");
		Vector2 source = board.getInteractedSource(pos.x, pos.y);
		System.out.println(source.x);
		System.out.println(source.y);
			//take light, place light
		if (board.getSourceOn(source) && player.hasSpace()) {
			System.out.println("Taking Light");
			board.turnSourceOff(source);
			player.increaseLights();
		} else if (!board.getSourceOn(source) && player.hasLights()) {
			System.out.println("Placing light");
			board.turnSourceOn(source);
			player.decreaseLights();
		}
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
		for(Enemy enemy : enemies){
			enemy.draw(canvas);
		}
		if(wonGame){
			canvas.draw(winScreenTexture, 450, 350);
		}
		if(lostGame){
			canvas.draw(lossScreenTexture, 350, 300);
		}

	}
}