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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import edu.cornell.gdiac.rekindled.obstacle.Obstacle;
import edu.cornell.gdiac.rekindled.obstacle.PolygonObstacle;
import edu.cornell.gdiac.util.*;
import javafx.scene.effect.Light;

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
public class GameplayController extends WorldController implements ContactListener {

	/**
	 * File storing the players
	 */
	private static final String PLAYER_FILE_FRONT = "images/front.png";
	private static final String PLAYER_FILE_BACK = "images/back.png";
	private static final String PLAYER_FILE_LEFT = "images/left.png";
	/**
	 * File storing the enemy
	 */
	private static final String ENEMY_FILE = "images/enemy.png";
	/**
	 * File storing the saved enemy
	 */
	private static final String SAVED_ENEMY_FILE = "images/savedEnemy.png";
	/**
	 * win/loss screens
	 */
	private static final String WIN_SCREEN_FILE = "images/winScreen.png";
	private static final String LOSS_SCREEN_FILE = "images/lossScreen.png";

	/** The file location of the wall*/
	private static final String WALL_FILE = "images/wall.png";

	private static final String LIT_SOURCE_FILE = "images/litLightSource.png";
	/** The file location of a dim light source*/
	private static final String DIM_SOURCE_FILE = "images/dimLightSource.png";


	/**texture region for wall*/
	private TextureRegion wallTexture;
	/**
	 * Textures for player
	 */
	private TextureRegion playerTextureLeft;
	private TextureRegion playerTextureBack;
	private TextureRegion playerTextureFront;
	/**
	 * Texture for enemy
	 */
	private TextureRegion enemyTexture;
	/**
	 * Texture for saved enemy
	 */
	private TextureRegion savedEnemyTexture;
	private TextureRegion winScreenTexture;
	private TextureRegion lossScreenTexture;

	/**texture region for dim light source*/
	private TextureRegion dimSourceTexture;
	/**texture region for lit light source*/
	private TextureRegion litSourceTexture;

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

	// Physics constants for initialization
	/** The density for all of (external) objects */
	private static final float BASIC_DENSITY = 0.0f;
	/** The friction for all of (external) objects */
	private static final float BASIC_FRICTION = 0.1f;
	/** The restitution for all of (external) objects */
	private static final float BASIC_RESTITUTION = 0.1f;



	/**
	 * Preloads the assets for this controller.
	 * <p>
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 *
	 * @param manager Reference to global asset manager.
	 */
	public void preLoadContent(AssetManager manager) {
		if (assetState != AssetState.EMPTY) {
			return;
		}
		assetState = AssetState.LOADING;

		manager.load(PLAYER_FILE_LEFT, Texture.class);
		assets.add(PLAYER_FILE_LEFT);
		manager.load(PLAYER_FILE_BACK, Texture.class);
		assets.add(PLAYER_FILE_BACK);
		manager.load(PLAYER_FILE_FRONT, Texture.class);
		assets.add(PLAYER_FILE_FRONT);
		manager.load(ENEMY_FILE, Texture.class);
		assets.add(ENEMY_FILE);
		manager.load(SAVED_ENEMY_FILE, Texture.class);
		assets.add(SAVED_ENEMY_FILE);
		manager.load(WIN_SCREEN_FILE, Texture.class);
		assets.add(WIN_SCREEN_FILE);
		manager.load(LOSS_SCREEN_FILE, Texture.class);
		assets.add(LOSS_SCREEN_FILE);
		manager.load(WALL_FILE, Texture.class);
		assets.add(WALL_FILE);
		manager.load(LIT_SOURCE_FILE, Texture.class);
		assets.add(LIT_SOURCE_FILE);
		manager.load(DIM_SOURCE_FILE, Texture.class);
		assets.add(DIM_SOURCE_FILE);

		super.preLoadContent(manager);
	}

	/**
	 * Loads the assets for this controller.
	 * <p>
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 *
	 * @param manager Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {
		if (assetState != AssetState.LOADING) {
			return;
		}
		playerTextureLeft = createTexture(manager, PLAYER_FILE_LEFT, false);
		playerTextureFront = createTexture(manager, PLAYER_FILE_FRONT, false);
		playerTextureBack = createTexture(manager, PLAYER_FILE_BACK, false);
		enemyTexture = createTexture(manager, ENEMY_FILE, false);
		savedEnemyTexture = createTexture(manager, SAVED_ENEMY_FILE, false);
		winScreenTexture = createTexture(manager, WIN_SCREEN_FILE, false);
		lossScreenTexture = createTexture(manager, LOSS_SCREEN_FILE, false);
		wallTexture = createTexture(manager, WALL_FILE, true);
		dimSourceTexture = createTexture(manager, DIM_SOURCE_FILE, false);
		litSourceTexture = createTexture(manager, LIT_SOURCE_FILE, false);

		super.loadContent(manager);
		assetState = AssetState.COMPLETE;
	}


	/**
	 * Unloads the assets for this game.
	 * <p>
	 * This method erases the static variables.  It also deletes the associated textures
	 * from the asset manager. If no assets are loaded, this method does nothing.
	 *
	 * @param manager Reference to global asset manager.
	 */
	public void unloadContent(AssetManager manager, Array<String> assets) {
		for (String s : assets) {
			if (manager.isLoaded(s)) {
				manager.unload(s);
			}
		}
	}

	/**
	 * Width of the game world in Box2d units
	 */
	protected static final float DEFAULT_WIDTH = 32.0f;
	/**
	 * Height of the game world in Box2d units
	 */
	protected static final float DEFAULT_HEIGHT = 18.0f;

	/**
	 * Reference to the game canvas
	 */
	protected GameCanvas canvas;
	/**
	 * Listener that will update the player mode when we are done
	 */
	private ScreenListener listener;

	/**
	 * The Game Board
	 */
	private Board board;
	/**
	 * Board width in Box2D Units
	 */
	private float BOARD_WIDTH;
	/**
	 * Board Height in box2D Units
	 */
	private float BOARD_HEIGHT;

	private static final int TURN_ON_DELAY = 2;

	private float delayTimer;
	private boolean cooldown;

	private Player player;

	private Enemy[] enemies;

	/**
	 * Stores all the AI controllers
	 */
	protected AIController[] controls;



	private LightSource[] lights;


	private int[] dimSources = {};
	private int[] litSources = {3, 7, 10, 6};
	private int[] enemyLocations = {1, 5, 12, 3};

	private int[] spawn;
	private int initLights;
	private int[] walls;

	CollisionController collisions;

	boolean lostGame;
	boolean wonGame;

	/** The reader to process JSON files */
	private JsonReader jsonReader;
	/** The JSON defining the level model */
	private JsonValue  levelFormat;


	public void parseJson(){
		float[] dim = levelFormat.get("dimension").asFloatArray();
		BOARD_WIDTH = dim[0];
		BOARD_HEIGHT = dim[1];
		spawn = levelFormat.get("spawn").asIntArray();
		initLights = levelFormat.getInt("init_lights");

		// Parse Lights
		JsonValue lights_json = levelFormat.get("lights");
		lights = new LightSource[lights_json.size];
		JsonValue light = lights_json.child();
		int idx = 0;
		while (light != null){
			int[] pos = light.get("position").asIntArray();
			lights[idx] = new LightSource(pos[0], pos[1], 1, 1,light.getBoolean("lit"));
			idx++;
			light = light.next();
		}

		// Parse Enemies
		JsonValue enemies_json = levelFormat.get("enemies");
		enemies = new Enemy[enemies_json.size];
		JsonValue enemy = enemies_json.child();
		idx = 0;
		while (enemy != null){
			int[] pos = enemy.get("position").asIntArray();
			JsonValue wander = enemy.get("wander");
			enemies[idx] = new Enemy(pos[0], pos[1], 1, 1, enemy.getInt("type"));
			enemies[idx].setWander(wander);
			idx++;
			enemy = enemy.next();
		}

		// Parse Walls; dumb format
		JsonValue walls_json = levelFormat.get("walls");
		walls = new int[walls_json.size * 2];
		JsonValue wall = walls_json.child();
		idx = 0;
		while (wall != null){
			int[] pos = wall.get("position").asIntArray();
			walls[idx] = pos[0];
			walls[idx + 1] = pos[1];
			idx += 2;
			wall = wall.next();
		}
	}


	/**
	 * Creates and initialize a new instance of the rocket lander game
	 * <p>
	 * The game has default gravity and other settings
	 */
	public GameplayController() {
		jsonReader = new JsonReader();
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);

//		enemies = new Enemy[10];
//		lights = new LightSource[10];
	}


	/**
	 * Resets the status of the game so that we can play again.
	 * <p>
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity());

		for (Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		world = new World(gravity, false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);

		// Reload the level json
		levelFormat = jsonReader.parse(Gdx.files.internal("jsons/level.json"));
		parseJson();
		populateLevel();
	}


	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {

		for (int i = 0; i < lights.length; i++){
			lights[i].setSensor(true);
			lights[i].setDrawScale(scale);
			lights[i].setTexture(litSourceTexture);
			lights[i].setBodyType(BodyDef.BodyType.StaticBody);
			lights[i].setTextureCache(litSourceTexture, dimSourceTexture);
			addObject(lights[i]);
		}

		for(int i = 0; i < enemies.length; i ++) {
			enemies[i].setSensor(true);
			enemies[i].setDrawScale(scale);
			enemies[i].setTexture(enemyTexture);
			addObject(enemies[i]);
		}

		// Add Walls
		PolygonObstacle obj;
		for (int i = 0; i < walls.length; i+=2){
			float[] vertices = {
					walls[i], walls[i+1],
					walls[i] + 1, walls[i + 1],
					walls[i] + 1, walls[i + 1] + 1,
					walls[i], walls[i + 1] + 1
			};
			obj = new PolygonObstacle(vertices, 0, 0);
			obj.setBodyType(BodyDef.BodyType.KinematicBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(wallTexture);
			addObject(obj);
		}

		// Create border pieces
		float[] border1 = {BOARD_WIDTH / 2f, BOARD_HEIGHT, BOARD_WIDTH / 2f, BOARD_HEIGHT - 1f, 1.0f, BOARD_HEIGHT - 1f,
				1.0f,  1.0f, BOARD_WIDTH / 2f,  1.0f, BOARD_WIDTH / 2,  0.0f, 0.0f,  0.0f,  0.0f, BOARD_HEIGHT};

		obj = new PolygonObstacle(border1, 0, 0);
		obj.setBodyType(BodyDef.BodyType.KinematicBody);
		obj.setDensity(BASIC_DENSITY);
		obj.setFriction(BASIC_FRICTION);
		obj.setRestitution(BASIC_RESTITUTION);
		obj.setDrawScale(scale);
		obj.setTexture(wallTexture);
		obj.setName("wall1");
		addObject(obj);

		float[] border2 = {BOARD_WIDTH, BOARD_HEIGHT, BOARD_WIDTH,  0.0f, BOARD_WIDTH / 2f,  0.0f,
				BOARD_WIDTH / 2f,  1.0f, BOARD_WIDTH - 1f,  1.0f, BOARD_WIDTH - 1f, BOARD_HEIGHT - 1f,
				BOARD_WIDTH / 2f, BOARD_HEIGHT - 1f, BOARD_WIDTH / 2f, BOARD_HEIGHT};

		obj = new PolygonObstacle(border2, 0, 0);
		obj.setBodyType(BodyDef.BodyType.KinematicBody);
		obj.setDensity(BASIC_DENSITY);
		obj.setFriction(BASIC_FRICTION);
		obj.setRestitution(BASIC_RESTITUTION);
		obj.setDrawScale(scale);
		obj.setTexture(wallTexture);
		obj.setName("wall2");
		addObject(obj);

		// Add Player
		player = new Player(spawn[0], spawn[1], 2, 4, initLights);
		player.setDrawScale(scale);
		player.setTexture(playerTextureFront);
		addObject(player);
	}

	/**
	 * The core gameplay loop of this world.
	 * <p>
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void update(float dt) {

		InputController input = InputController.getInstance();
		input.readInput(bounds, scale);
		InputController.Move_Direction next_move = input.get_Next_Direction();

		//player movement
		player.move(next_move);

		player.updateCooldown(dt);
		//placing and taking light
		if (input.didSecondary() && player.getTouchingLight() && !player.getCooldown()) {
			player.takeLight();
			for (LightSource light : lights){
				light.toggleLit();
			}
		}

		// Check win Condition
		int numLit = 0;
		for (Enemy e : enemies){
			if (e.getIsLit())
				numLit ++;
		}
		wonGame = (numLit == enemies.length);
	}

	public boolean isAlive() {
		return true;
	}

	public boolean won() {
		return wonGame;
	}

	public boolean lost() {
		return lostGame;
	}


	/// CONTACT LISTENER METHODS

	/**
	 * Callback method for the start of a collision
	 * <p>
	 * This method is called when we first get a collision between two objects.  We use
	 * this method to test if it is the "right" kind of collision.  In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		try {
			Obstacle bd1 = (Obstacle)body1.getUserData();
			Obstacle bd2 = (Obstacle)body2.getUserData();


			// See if we lost.
			for (Enemy enemy : enemies) {
				if ((bd1 == player && bd2 == enemy) || (bd1 == enemy &&  bd2 == player)){
					lostGame = true;
					System.out.println("enemy contact");
				}
			}

			//Update touching lights
			for (LightSource light : lights) {
				if((bd1 == player && bd2 == light) || (bd1 == light && bd2 == player)){
					player.setTouchingLight(true);
					light.setTouchingPlayer(true);
					System.out.println("touching light");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	/**
	 * Callback method for the start of a collision
	 * <p>
	 * This method is called when two objects cease to touch.  We do not use it.
	 */
	public void endContact(Contact contact) {

		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		try {
			Obstacle bd1 = (Obstacle)body1.getUserData();
			Obstacle bd2 = (Obstacle)body2.getUserData();

			//Update touching lights
			for (LightSource light : lights) {
				if((bd1 == player && bd2 == light) || (bd1 == light && bd2 == player)){
					player.setTouchingLight(false);
					light.setTouchingPlayer(false);
					System.out.println("no longer touching light");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Vector2 cache = new Vector2();

	/**
	 * Unused ContactListener method
	 */
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	/**
	 * Handles any modifications necessary before collision resolution
	 * <p>
	 * This method is called just before Box2D resolves a collision.  We use this method
	 * to implement sound on contact, using the algorithms outlined similar to those in
	 * Ian Parberry's "Introduction to Game Physics with Box2D".
	 * <p>
	 * However, we cannot use the proper algorithms, because LibGDX does not implement
	 * b2GetPointStates from Box2D.  The danger with our approximation is that we may
	 * get a collision over multiple frames (instead of detecting the first frame), and
	 * so play a sound repeatedly.  Fortunately, the cooldown hack in SoundController
	 * prevents this from happening.
	 *
	 * @param contact     The two bodies that collided
	 * @param oldManifold The collision manifold before contact
	 */

	public void preSolve(Contact contact, Manifold oldManifold) {
		float speed = 0;

		// Use Ian Parberry's method to compute a speed threshold
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		WorldManifold worldManifold = contact.getWorldManifold();
		Vector2 wp = worldManifold.getPoints()[0];
		cache.set(body1.getLinearVelocityFromWorldPoint(wp));
		cache.sub(body2.getLinearVelocityFromWorldPoint(wp));
		speed = cache.dot(worldManifold.getNormal());

	}
}