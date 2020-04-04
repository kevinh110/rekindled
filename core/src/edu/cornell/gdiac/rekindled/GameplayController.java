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

import box2dLight.RayHandler;
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
	private static final String SAVED_ENEMY_FILE = "images/savedEnemy.png";
	/**
	 * win/loss screens
	 */
	private static final String WIN_SCREEN_FILE = "images/winScreen.png";
	private static final String LOSS_SCREEN_FILE = "images/lossScreen.png";

	/** The file location of the wall*/
	private static final String WALL_FILE = "images/wall.png";
	private static final String LIT_SOURCE_FILE = "images/litLightSource.png";
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
	private TextureRegion savedEnemyTexture;

	private TextureRegion winScreenTexture;
	private TextureRegion lossScreenTexture;

	/**texture region for dim light source*/;
	private TextureRegion dimSourceTexture;
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
	 * Board width in tiles
	 */
	private static final int BOARD_WIDTH = 16;
	/**
	 * Board Height in tiles
	 */
	private static final int BOARD_HEIGHT = 9;
	private static final int TURN_ON_DELAY = 2;

	private float delayTimer;
	private boolean cooldown;

	private Player player;

	private Enemy[] enemies;

	/**
	 * Stores all the AI controllers
	 */
	protected AIController[] controls;


	LightSourceObject[] lights;
	private RayHandler rayHandler;
	private OrthographicCamera rayCamera;

	private int[] walls = {3, 2, 3, 3, 3, 4, 6, 2, 6, 3, 6, 4};
	private int[] dimSources = {};
	private int[] litSources = {3, 7, 10, 6};
	private int[] enemyLocations = {1, 5, 12, 3};

	CollisionController collisions;

	boolean lostGame;
	boolean wonGame;

	// Since these appear only once, we do not care about the magic numbers.
	// In an actual game, this information would go in a data file.
	// Wall vertices
	private static final float[] WALL1 = {16.0f, 18.0f, 16.0f, 17.0f,  1.0f, 17.0f,
			1.0f,  1.0f, 16.0f,  1.0f, 16.0f,  0.0f,
			0.0f,  0.0f,  0.0f, 18.0f};

	private static final float[] WALL2 =  {32.0f, 18.0f, 32.0f,  0.0f, 16.0f,  0.0f,
			16.0f,  1.0f, 31.0f,  1.0f, 31.0f, 17.0f,
			16.0f, 17.0f, 16.0f, 18.0f};

	private static final float AMBIANCE = 0.1f;
	/**
	 * Creates and initialize a new instance of the rocket lander game
	 * <p>
	 * The game has default gravity and other settings
	 */
	public GameplayController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);

		enemies = new Enemy[2];
		lights = new LightSourceObject[1];
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
		populateLevel();
	}


	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {




		for(int i = 0; i < 2; i ++) {
			enemies[i] = new Enemy(enemyLocations[i], enemyLocations[i+1], 1, 1);
			enemies[i].setSensor(true);
			enemies[i].setDrawScale(scale);
			enemies[i].setTexture(enemyTexture);
			addObject(enemies[i]);
		}

		// Create ground pieces
		PolygonObstacle obj;
		obj = new PolygonObstacle(WALL1, 0, 0);
		obj.setBodyType(BodyDef.BodyType.KinematicBody);
		obj.setDensity(BASIC_DENSITY);
		obj.setFriction(BASIC_FRICTION);
		obj.setRestitution(BASIC_RESTITUTION);
		obj.setDrawScale(scale);
		obj.setTexture(wallTexture);
		obj.setName("wall1");
		addObject(obj);

		obj = new PolygonObstacle(WALL2, 0, 0);
		obj.setBodyType(BodyDef.BodyType.KinematicBody);
		obj.setDensity(BASIC_DENSITY);
		obj.setFriction(BASIC_FRICTION);
		obj.setRestitution(BASIC_RESTITUTION);
		obj.setDrawScale(scale);
		obj.setTexture(wallTexture);
		obj.setName("wall2");
		addObject(obj);

		// Add ambient lighting
		initLighting();

		lights[0] = new LightSourceObject(5, 5, 1, 1,true);
		LightSourceLight light = new LightSourceLight(rayHandler, 5, 5);
		lights[0].addLight(light);
		lights[0].setSensor(true);
		lights[0].setDrawScale(scale);
		lights[0].setTexture(litSourceTexture);
		lights[0].setBodyType(BodyDef.BodyType.StaticBody);
		lights[0].setTextureCache(litSourceTexture, dimSourceTexture);
		addObject(lights[0]);

		// Add level goal
		player = new Player(10, 10, 2, 4);
		player.setDrawScale(scale);
		player.setTexture(playerTextureFront);
		addObject(player);
	}

	public void initLighting() {
		rayCamera = new OrthographicCamera(bounds.width,bounds.height);
		rayCamera.position.set(bounds.width/2.0f, bounds.height/2.0f, 0);
		rayCamera.update();

		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(world, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		rayHandler.setCombinedMatrix(rayCamera);

		rayHandler.setAmbientLight(AMBIANCE, AMBIANCE, AMBIANCE, AMBIANCE);
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(3);
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

		if (rayHandler != null)
			rayHandler.update();

		InputController input = InputController.getInstance();
		input.readInput(bounds, scale);
		InputController.Move_Direction next_move = input.get_Next_Direction();

		//player movement
		player.move(next_move);

		player.updateCooldown(dt);
		//placing and taking light
		if (input.didSecondary() && player.getTouchingLight() && !player.getCooldown()) {
			player.takeLight();
			for (LightSourceObject light : lights){
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

	@Override
	public void render(float delta) {
		if (isActive()) {
            if (preUpdate(delta)) {
                update(delta); // This is the one that must be defined.
                postUpdate(delta);
            }
            draw(delta);
            rayHandler.render();
        }
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
			for (LightSourceObject light : lights) {
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
			for (LightSourceObject light : lights) {
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