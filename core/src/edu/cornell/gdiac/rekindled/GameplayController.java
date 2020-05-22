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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import edu.cornell.gdiac.rekindled.light.LightSourceLight;
import edu.cornell.gdiac.rekindled.light.SightConeLight;
import edu.cornell.gdiac.rekindled.obstacle.BoxObstacle;
import edu.cornell.gdiac.rekindled.obstacle.FeetHitboxObstacle;
import edu.cornell.gdiac.rekindled.obstacle.Obstacle;
import edu.cornell.gdiac.rekindled.obstacle.PolygonObstacle;
import edu.cornell.gdiac.util.*;
import javafx.scene.media.AudioSpectrumListener;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;


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

	// Level Json Path
	private String LEVEL_PATH;

	// Tutorial Boolean
	private boolean TUTORIAL;

	// Level ID
	private int LEVEL_ID;

	/**
	 * File storing the players
	 */
	private static final String PLAYER_ANIMATION_FRONT = "spritesheets/spritesheet_lux_front.png";
	private static final String PLAYER_ANIMATION_LEFT = "spritesheets/spritesheet_lux_left.png";
	private static final String PLAYER_ANIMATION_RIGHT = "spritesheets/spritesheet_lux_right.png";
	private static final String PLAYER_ANIMATION_BACK = "spritesheets/spritesheet_lux_back.png";
	private static final String PLAYER_FILE_FRONT = "images/front.png";
	private static final String PLAYER_FILE_BACK = "images/back.png";
	private static final String PLAYER_FILE_LEFT = "images/left.png";
	private static final String PLACING_LIGHT_BACK = "spritesheets/spritesheet_back_throw_light.png";
	private static final String PLACING_LIGHT_FRONT = "spritesheets/spritesheet_front_throw_light.png";
	private static final String TAKING_LIGHT_FRONT = "spritesheets/spritesheet_front-place-light.png";
	private static final String PLACING_LIGHT_LEFT = "spritesheets/spritesheet_left_throw_light.png";
	private static final String TAKING_LIGHT_LEFT = "spritesheets/spritesheet_left-take-light.png";
	private static final String PLACING_LIGHT_RIGHT = "spritesheets/spritesheet_right_throw_light.png";
	private static final String TAKING_LIGHT_RIGHT = "spritesheets/spritesheet_right-take-light.png";
	private static final String THROW_LIGHT_BACK = "spritesheets/spritesheet_lux_back.png";
	private static final String THROW_LIGHT_FRONT = "spritesheets/spritesheet_front-place-light.png";
	private static final String THROW_LIGHT_LEFT =  "spritesheets/spritesheet_left-place-light.png";
	private static final String THROW_LIGHT_RIGHT = 	"spritesheets/spritesheet_right-place-light.png";
	private static final String PLAYER_FRONT_IDLE = "spritesheets/spritesheet_front_idle.png";
	private static final String PLAYER_BACK_IDLE = "spritesheets/spritesheet_back_idle.png";
	private static final String PLAYER_LEFT_IDLE = "spritesheets/spritesheet_left_idle.png";
	private static final String PLAYER_RIGHT_IDLE = "spritesheets/spritesheet_right_idle.png";
	private static final String DEATH = "spritesheets/spritesheet_lux_death.png";
	/**
	 * File storing the enemy
	 */
	private static final String ENEMY_FILE = "images/enemy.png";
	private static final String SEEN_FILE = "images/exclamation.png";
	private static final String SAVED_ENEMY_FILE = "images/savedEnemy.png";
	private static final String ENEMY_ANIMATION_FRONT = "spritesheets/spritesheet_enemy_front.png";
	private static final String ENEMY_ANIMATION_LEFT = "spritesheets/spritesheet_enemy_left.png";
	private static final String ENEMY_ANIMATION_RIGHT = "spritesheets/spritesheet_enemy_right.png";
	private static final String ENEMY_ANIMATION_BACK = "spritesheets/spritesheet_enemy_back.png";

	private static final String ENEMY_ANGRY_ANIMATION_FRONT = "spritesheets/spritesheet_enemy_angry_front.png";
	private static final String ENEMY_ANGRY_ANIMATION_LEFT = "spritesheets/spritesheet_enemy_angry_left.png";
	private static final String ENEMY_ANGRY_ANIMATION_RIGHT = "spritesheets/spritesheet_enemy_angry_right.png";
	private static final String ENEMY_ANGRY_ANIMATION_BACK = "spritesheets/spritesheet_enemy_angry_back.png";

	private static final String ENEMY_TRANSFORMATION = "spritesheets/spritesheet_transformation.png";
	private static final String ENEMY_ANIMATION_SAVED = "spritesheets/spritesheet_saved_soul.png";
	/**
	 * win/loss screens
	 */
	private static final String WIN_SCREEN_FILE = "images/winScreen.png";
	private static final String LOSS_SCREEN_FILE = "images/lossScreen.png";

	/** The file location of the wall*/
	private static final String D_WALL = "wall/d.png";
	private static final String DL_WALL = "wall/dl.png";
	private static final String DL_SINGLE_WALL = "wall/dl-single.png";
	private static final String DLR_WALL = "wall/dlr.png";
	private static final String DR_WALL = "wall/dr.png";
	private static final String DR_SINGLE_WALL = "wall/dr-single.png";
	private static final String L_WALL = "wall/l.png";
	private static final String LR_WALL = "wall/lr.png";
	private static final String LR__SINGLE_WALL = "wall/lr-single.png";
	private static final String R_WALL = "wall/r.png";
	private static final String SINGULAR_WALL = "wall/singular.png";
	private static final String U_WALL = "wall/u.png";
	private static final String UD_WALL = "wall/ud.png";
	private static final String UDL_WALL = "wall/udl.png";
	private static final String UDLR_WALL = "wall/udlr.png";
	private static final String UDR_WALL = "wall/udr.png";
	private static final String UL_WALL = "wall/ul.png";
	private static final String ULR_WALL = "wall/ulr.png";
	private static final String UR_WALL = "wall/ur.png";
	private static final String UR_SINGLE_WALL = "wall/ur-single.png";
	private static final String DLR_SINGLE_WALL = "wall/dlr-single.png";
	private static final String UDL_SINGLE_WALL = "wall/udl-single.png";
	private static final String UDLR_SINGLE_WALL = "wall/udlr-single.png";
	private static final String UDR_SINGLE_WALL = "wall/udr-single.png";
	private static final String ULR_SINGLE_WALL = "wall/ulr-single.png";
	private static final String DL_COVER_WALL = "wall/dl-cover.png";
	private static final String DR_COVER = "wall/dr-cover.png";
	private static final String UL_COVER = "wall/ul-cover.png";
	private static final String UR_COVER = "wall/ur-cover.png";

	/** file locations of the hole*/
	private static final String D_HOLE = "hole/d.png";
	private static final String DL_HOLE = "hole/dl.png";
	private static final String DL_SINGLE_HOLE = "hole/dl-single.png";
	private static final String DLR_HOLE = "hole/dlr.png";
	private static final String DR_HOLE = "hole/dr.png";
	private static final String DR_SINGLE_HOLE = "hole/dr-single.png";
	private static final String L_HOLE = "hole/l.png";
	private static final String LR_HOLE = "hole/lr.png";
	private static final String LR__SINGLE_HOLE = "hole/ul-single.png"; //the LR wall single is actually incorrectly named
	//for consistency, I am naming this wrong as well
	private static final String R_HOLE = "hole/r.png";
	private static final String SINGULAR_HOLE = "hole/single.png"; //name changed for consistency
	private static final String U_HOLE = "hole/u.png";
	private static final String UD_HOLE = "hole/ud.png";
	private static final String UDL_HOLE = "hole/udl.png";
	private static final String UDLR_HOLE = "hole/udlr.png";
	private static final String UDR_HOLE = "hole/udr.png";
	private static final String UL_HOLE = "hole/ul.png";
	private static final String ULR_HOLE = "hole/ulr.png";
	private static final String UR_HOLE = "hole/ur.png";
	private static final String UR_SINGLE_HOLE = "hole/ur-single.png";
	private static final String DLR_SINGLE_HOLE = "hole/dlr-single.png";
	private static final String UDL_SINGLE_HOLE = "hole/udl-single.png";
	private static final String UDLR_SINGLE_HOLE = "hole/udlr-single.png";
	private static final String UDR_SINGLE_HOLE = "hole/udr-single.png";
	private static final String ULR_SINGLE_HOLE = "hole/ulr-single.png";

	/** file locations of the light sources*/
	private static final String LIT_SOURCE_FILE = "images/litLightSource.png";
	private static final String DIM_SOURCE_FILE = "images/dimLightSource.png";
	private static final String LIGHT_ANIMATION_FILE = "spritesheets/spritesheet_lamp.png";

	/** file location for the art objects */
	private static final String GRASS_SOURCE_FILE = "spritesheets/spritesheet_grass.png";
	private static final String MUSHROOM_SOURCE_FILE = "spritesheets/spritesheet_mushrooms.png";
	private static final String MUSHROOM_WIGGLE = "spritesheets/spritesheet_mushrooms_giggle.png";

	/** file location for UI elements */
	private static final String LIGHTS_TEXT_FILE = "images/light_text.png";
	private static final String LIGHT_COUNTER_FILE = "images/light_counter.png";

	/** file location for water */
	private static final String WATER_DARK_FILE = "images/water_tile_dark.png";
	private static final String WATER_LIGHT_FILE = "images/water_tile_light.png";

	private static final String PICKUP_SOURCE_FILE = "spritesheets/spritesheet_pickup.png";

	private static final String PAUSE_FILE = "ui/pause_ui.png";

	private static final String COOLDOWN_0 = "images/0.png";
	private static final String COOLDOWN_25 = "images/25.png";
	private static final String COOLDOWN_50 = "images/50.png";
	private static final String COOLDOWN_75 = "images/75.png";
	private static final String COOLDOWN_100 = "images/100.png";


	/** file location for tutorials */
	private static final String TUTORIAL_ZOOM = "tutorial/zoom.png";

	private static final String TUTORIAL_FIREFLY_FILE = "tutorial/firefly.png";
	private static final String TUTORIAL_FIREFLY_FLIPPED = "tutorial/firefly_flipped.png";

	private static final String TUTORIAL_111 = "tutorial/lv1/1.1.1.png";
	private static final String TUTORIAL_112 = "tutorial/lv1/1.1.2.png";
	private static final String TUTORIAL_121 = "tutorial/lv1/1.2.1.png";
	private static final String TUTORIAL_122 = "tutorial/lv1/1.2.2.png";
	private static final String TUTORIAL_131 = "tutorial/lv1/1.3.1.png";
	private static final String TUTORIAL_132 = "tutorial/lv1/1.3.2.png";
	private static final String TUTORIAL_141 = "tutorial/lv1/1.4.1.png";
	private static final String TUTORIAL_211 = "tutorial/lv2/2.1.1.png";
	private static final String TUTORIAL_221 = "tutorial/lv2/2.2.1.png";
	private static final String TUTORIAL_311 = "tutorial/lv3/3.1.1.png";
	private static final String TUTORIAL_312 = "tutorial/lv3/3.1.2.png";
	private static final String TUTORIAL_411 = "tutorial/lv4/4.1.1.png";
	private static final String TUTORIAL_412 = "tutorial/lv4/4.1.2.png";
	private static final String TUTORIAL_413 = "tutorial/lv4/4.1.3.png";
	private static final String TUTORIAL_414 = "tutorial/lv4/4.1.4.png";
	private static final String TUTORIAL_511 = "tutorial/lv5/5.1.1.png";
	private static final String TUTORIAL_512 = "tutorial/lv5/5.1.2.png";
	private static final String TUTORIAL_611 = "tutorial/lv6/6.1.1.png";
	private static final String TUTORIAL_612 = "tutorial/lv6/6.1.2.png";
	private static final String TUTORIAL_711 = "tutorial/lv7/7.1.1.png";
	private static final String TUTORIAL_811 = "tutorial/lv8/8.1.1.png";




	/** texture for pickup */
	private TextureRegion pickupTexture;

	/** texture for water */
	private TextureRegion waterDarkTexture;
	private TextureRegion waterLightTexture;

	/** texture for UI elements */
	private TextureRegion lightsTexture;
	private TextureRegion lightCounterTexture;

	/** texture for art objects */
	private TextureRegion grassTexture;
	private TextureRegion mushroomTexture;
	private TextureRegion mushroomWiggle;

	/** Array of Texture Regions for the wall*/
	private TextureRegion[] wallTextures;

	/**Array of Texture Regions for the holes*/
	private TextureRegion[] holeTextures;

	/**
	 * Textures for player
	 */
	private TextureRegion playerTextureLeft;
	private TextureRegion playerTextureBack;
	private TextureRegion playerTextureFront;

	private TextureRegion playerAnimationFront;
	private TextureRegion playerAnimationBack;
	private TextureRegion playerAnimationLeft;
	private TextureRegion playerAnimationRight;
	private TextureRegion placingLightBack;
	private TextureRegion placingLightFront;
	private TextureRegion takingLightFront;
	private TextureRegion placingLightLeft;
	private TextureRegion takingLightLeft;
	private TextureRegion placingLightRight;
	private TextureRegion takingLightRight;
	private TextureRegion throwingLightFront;
	private TextureRegion throwingLightBack;
	private TextureRegion throwingLightRight;
	private TextureRegion throwingLightLeft;
	private TextureRegion playerFrontIdle;
	private TextureRegion playerBackIdle;
	private TextureRegion playerLeftIdle;
	private TextureRegion playerRightIdle;
	private TextureRegion death;


	private TextureRegion enemyAnimationFront;
	private TextureRegion enemyAnimationBack;
	private TextureRegion enemyAnimationLeft;
	private TextureRegion enemyAnimationRight;
	private TextureRegion enemyAngryAnimationFront;
	private TextureRegion enemyAngryAnimationBack;
	private TextureRegion enemyAngryAnimationLeft;
	private TextureRegion enemyAngryAnimationRight;
	private TextureRegion enemyTransformation;
	private TextureRegion enemyAnimationSaved;

	/**
	 * Texture for enemy
	 */
	private TextureRegion enemyTexture;
	private TextureRegion seenTexture;
	private TextureRegion savedEnemyTexture;

	private TextureRegion winScreenTexture;
	private TextureRegion lossScreenTexture;

	private TextureRegion pauseTexture;

	/**texture region for dim light source*/
	private TextureRegion dimSourceTexture;
	/**texture region for lit light source*/
	private TextureRegion litSourceTexture;
	/** spritesheet for light source */
	private TextureRegion lightAnimation;

	private TextureRegion[] cooldownTextures;

	/**
	 * Texture for Tutorial Text
	 */
	private TextureRegion tutorialZoom;
	private TextureRegion tutorialFirefly;
	private TextureRegion tutorialFireflyFlipped;

	private TextureRegion tutorial111;
	private TextureRegion tutorial112;
	private TextureRegion tutorial121;
	private TextureRegion tutorial122;
	private TextureRegion tutorial131;
	private TextureRegion tutorial132;
	private TextureRegion tutorial141;
	private TextureRegion tutorial211;
	private TextureRegion tutorial221;
	private TextureRegion tutorial311;
	private TextureRegion tutorial312;
	private TextureRegion tutorial411;
	private TextureRegion tutorial412;
	private TextureRegion tutorial413;
	private TextureRegion tutorial414;
	private TextureRegion tutorial511;
	private TextureRegion tutorial512;
	private TextureRegion tutorial611;
	private TextureRegion tutorial612;
	private TextureRegion tutorial711;
	private TextureRegion tutorial811;

	//pickup sound
	private Sound pickupSound;

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

	// Physics constants for initialization
	/** The density for all of (external) objects */
	private static final float BASIC_DENSITY = 0.0f;
	/** The friction for all of (external) objects */
	private static final float BASIC_FRICTION = 0.1f;
	/** The restitution for all of (external) objects */
	private static final float BASIC_RESTITUTION = 0.1f;

	private static final float THROWN_LIGHT_RADIUS = 3f;

	/** This value makes different frame rates all seem the same speed*/
	public static float SPEED_SCALE = 50f;

	private float currentScale;
	private static final float ZOOM_OUT_SCALE = 2.5f;
	private static final float ZOOM_IN_SCALE = 1.0f;

	/** scale of the walls*/
	private static final float WALL_SCALE = .33f;
	/** scale of the hole tiles*/
	private static final float HOLE_SCALE = .146f;

	private boolean removePressAnyKeyText;
	private boolean zoom_in;
	private boolean zoom_out;

	private int spawnx;
	private int spawny;

	private boolean startPause;

	private static boolean muted = false;

	// Number of frames after collision where player doesn't lose
	private static final int GRACE_PERIOD = 10;
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

		manager.load(PLAYER_ANIMATION_FRONT, Texture.class);
		assets.add(PLAYER_ANIMATION_FRONT);
		manager.load(PLAYER_ANIMATION_BACK, Texture.class);
		assets.add(PLAYER_ANIMATION_BACK);
		manager.load(PLAYER_ANIMATION_LEFT, Texture.class);
		assets.add(PLAYER_ANIMATION_LEFT);
		manager.load(PLAYER_ANIMATION_RIGHT, Texture.class);
		assets.add(PLAYER_ANIMATION_RIGHT);

		manager.load(PLACING_LIGHT_FRONT, Texture.class);
		assets.add(PLACING_LIGHT_FRONT);
		manager.load(TAKING_LIGHT_FRONT, Texture.class);
		assets.add(TAKING_LIGHT_FRONT);
		manager.load(PLACING_LIGHT_LEFT, Texture.class);
		assets.add(PLACING_LIGHT_LEFT);
		manager.load(TAKING_LIGHT_LEFT, Texture.class);
		assets.add(TAKING_LIGHT_LEFT);
		manager.load(PLACING_LIGHT_RIGHT, Texture.class);
		assets.add(PLACING_LIGHT_RIGHT);
		manager.load(TAKING_LIGHT_RIGHT, Texture.class);
		assets.add(TAKING_LIGHT_RIGHT);

		manager.load(ENEMY_ANIMATION_FRONT, Texture.class);
		assets.add(ENEMY_ANIMATION_FRONT);
		manager.load(ENEMY_ANIMATION_BACK, Texture.class);
		assets.add(ENEMY_ANIMATION_BACK);
		manager.load(ENEMY_ANIMATION_LEFT, Texture.class);
		assets.add(ENEMY_ANIMATION_LEFT);
		manager.load(ENEMY_ANIMATION_RIGHT, Texture.class);
		assets.add(ENEMY_ANIMATION_RIGHT);

		manager.load(ENEMY_ANGRY_ANIMATION_FRONT, Texture.class);
		assets.add(ENEMY_ANGRY_ANIMATION_FRONT);
		manager.load(ENEMY_ANGRY_ANIMATION_BACK, Texture.class);
		assets.add(ENEMY_ANGRY_ANIMATION_BACK);
		manager.load(ENEMY_ANGRY_ANIMATION_LEFT, Texture.class);
		assets.add(ENEMY_ANGRY_ANIMATION_LEFT);
		manager.load(ENEMY_ANGRY_ANIMATION_RIGHT, Texture.class);
		assets.add(ENEMY_ANGRY_ANIMATION_RIGHT);

		manager.load(ENEMY_TRANSFORMATION, Texture.class);
		assets.add(ENEMY_TRANSFORMATION);
		manager.load(ENEMY_ANIMATION_SAVED, Texture.class);
		assets.add(ENEMY_ANIMATION_SAVED);

		manager.load(PLAYER_FILE_LEFT, Texture.class);
		assets.add(PLAYER_FILE_LEFT);
		manager.load(PLAYER_FILE_BACK, Texture.class);
		assets.add(PLAYER_FILE_BACK);
		manager.load(PLAYER_FILE_FRONT, Texture.class);
		assets.add(PLAYER_FILE_FRONT);

		manager.load(THROW_LIGHT_FRONT, Texture.class);
		assets.add(THROW_LIGHT_FRONT);
		manager.load(THROW_LIGHT_BACK, Texture.class);
		assets.add(THROW_LIGHT_BACK);
		manager.load(THROW_LIGHT_LEFT, Texture.class);
		assets.add(THROW_LIGHT_LEFT);
		manager.load(THROW_LIGHT_RIGHT, Texture.class);
		assets.add(THROW_LIGHT_RIGHT);

		manager.load(PLAYER_FRONT_IDLE, Texture.class);
		assets.add(PLAYER_FRONT_IDLE);
		manager.load(PLAYER_BACK_IDLE, Texture.class);
		assets.add(PLAYER_BACK_IDLE);
		manager.load(PLAYER_LEFT_IDLE, Texture.class);
		assets.add(PLAYER_LEFT_IDLE);
		manager.load(PLAYER_RIGHT_IDLE, Texture.class);
		assets.add(PLAYER_RIGHT_IDLE);
		manager.load(DEATH, Texture.class);
		assets.add(DEATH);

		manager.load(ENEMY_FILE, Texture.class);
		assets.add(ENEMY_FILE);
		manager.load(SEEN_FILE, Texture.class);
		assets.add(SEEN_FILE);
		manager.load(SAVED_ENEMY_FILE, Texture.class);
		assets.add(SAVED_ENEMY_FILE);
		manager.load(WIN_SCREEN_FILE, Texture.class);
		assets.add(WIN_SCREEN_FILE);
		manager.load(LOSS_SCREEN_FILE, Texture.class);
		assets.add(LOSS_SCREEN_FILE);

		manager.load(D_WALL, Texture.class);
		assets.add(D_WALL);
		manager.load(DL_WALL, Texture.class);
		assets.add(DL_WALL);
		manager.load(DL_SINGLE_WALL, Texture.class);
		assets.add(DL_SINGLE_WALL);
		manager.load(DLR_WALL, Texture.class);
		assets.add(DLR_WALL);
		manager.load(DR_WALL, Texture.class);
		assets.add(DR_WALL);
		manager.load(DR_SINGLE_WALL, Texture.class);
		assets.add(DR_SINGLE_WALL);
		manager.load(L_WALL, Texture.class);
		assets.add(L_WALL);
		manager.load(LR_WALL, Texture.class);
		assets.add(LR_WALL);
		manager.load(LR__SINGLE_WALL, Texture.class);
		assets.add(LR__SINGLE_WALL);
		manager.load(R_WALL, Texture.class);
		assets.add(R_WALL);
		manager.load(SINGULAR_WALL, Texture.class);
		assets.add(SINGULAR_WALL);
		manager.load(U_WALL, Texture.class);
		assets.add(U_WALL);
		manager.load(UD_WALL, Texture.class);
		assets.add(UD_WALL);
		manager.load(UDL_WALL, Texture.class);
		assets.add(UDL_WALL);
		manager.load(UDLR_WALL, Texture.class);
		assets.add(UDLR_WALL);
		manager.load(UDR_WALL, Texture.class);
		assets.add(UDR_WALL);
		manager.load(UDLR_WALL, Texture.class);
		assets.add(UDLR_WALL);
		manager.load(UL_WALL, Texture.class);
		assets.add(UL_WALL);
		manager.load(ULR_WALL, Texture.class);
		assets.add(ULR_WALL);
		manager.load(UR_WALL, Texture.class);
		assets.add(UR_WALL);
		manager.load(UR_WALL, Texture.class);
		assets.add(UR_WALL);
		manager.load(UR_SINGLE_WALL, Texture.class);
		assets.add(UR_SINGLE_WALL);
		manager.load(DLR_SINGLE_WALL, Texture.class);
		assets.add(DLR_SINGLE_WALL);
		manager.load(UDL_SINGLE_WALL, Texture.class);
		assets.add(UDL_SINGLE_WALL);
		manager.load(UDLR_SINGLE_WALL, Texture.class);
		assets.add(UDLR_SINGLE_WALL);
		manager.load(UDR_SINGLE_WALL, Texture.class);
		assets.add(UDR_SINGLE_WALL);
		manager.load(ULR_SINGLE_WALL, Texture.class);
		assets.add(ULR_WALL);
		manager.load(DL_COVER_WALL, Texture.class);
		assets.add(DL_COVER_WALL);
		manager.load(DR_COVER, Texture.class);
		assets.add(DR_COVER);
		manager.load(UL_COVER, Texture.class);
		assets.add(UL_COVER);
		manager.load(UR_COVER, Texture.class);
		assets.add(UR_COVER);

		manager.load(D_HOLE, Texture.class);
		assets.add(D_HOLE);
		manager.load(DL_HOLE, Texture.class);
		assets.add(DL_HOLE);
		manager.load(DL_SINGLE_HOLE, Texture.class);
		assets.add(DL_SINGLE_HOLE);
		manager.load(DLR_HOLE, Texture.class);
		assets.add(DLR_HOLE);
		manager.load(DR_HOLE, Texture.class);
		assets.add(DR_HOLE);
		manager.load(DR_SINGLE_HOLE, Texture.class);
		assets.add(DR_SINGLE_HOLE);
		manager.load(L_HOLE, Texture.class);
		assets.add(L_HOLE);
		manager.load(LR_HOLE, Texture.class);
		assets.add(LR_HOLE);
		manager.load(LR__SINGLE_HOLE, Texture.class);
		assets.add(LR__SINGLE_HOLE);
		manager.load(R_HOLE, Texture.class);
		assets.add(R_HOLE);
		manager.load(SINGULAR_HOLE, Texture.class);
		assets.add(SINGULAR_HOLE);
		manager.load(U_HOLE, Texture.class);
		assets.add(U_HOLE);
		manager.load(UD_HOLE, Texture.class);
		assets.add(UD_HOLE);
		manager.load(UDL_HOLE, Texture.class);
		assets.add(UDL_HOLE);
		manager.load(UDLR_HOLE, Texture.class);
		assets.add(UDLR_HOLE);
		manager.load(UDR_HOLE, Texture.class);
		assets.add(UDR_HOLE);
		manager.load(UDLR_HOLE, Texture.class);
		assets.add(UDLR_HOLE);
		manager.load(UL_HOLE, Texture.class);
		assets.add(UL_HOLE);
		manager.load(ULR_HOLE, Texture.class);
		assets.add(ULR_HOLE);
		manager.load(UR_HOLE, Texture.class);
		assets.add(UR_HOLE);
		manager.load(UR_HOLE, Texture.class);
		assets.add(UR_HOLE);
		manager.load(UR_SINGLE_HOLE, Texture.class);
		assets.add(UR_SINGLE_HOLE);
		manager.load(DLR_SINGLE_HOLE, Texture.class);
		assets.add(DLR_SINGLE_HOLE);
		manager.load(UDL_SINGLE_HOLE, Texture.class);
		assets.add(UDL_SINGLE_HOLE);
		manager.load(UDLR_SINGLE_HOLE, Texture.class);
		assets.add(UDLR_SINGLE_HOLE);
		manager.load(UDR_SINGLE_HOLE, Texture.class);
		assets.add(UDR_SINGLE_HOLE);
		manager.load(ULR_SINGLE_HOLE, Texture.class);
		assets.add(ULR_HOLE);

		manager.load(LIT_SOURCE_FILE, Texture.class);
		assets.add(LIT_SOURCE_FILE);
		manager.load(DIM_SOURCE_FILE, Texture.class);
		assets.add(DIM_SOURCE_FILE);
		manager.load(LIGHT_ANIMATION_FILE, Texture.class);
		assets.add(LIGHT_ANIMATION_FILE);
		manager.load(GRASS_SOURCE_FILE, Texture.class);
		assets.add(GRASS_SOURCE_FILE);
		manager.load(MUSHROOM_SOURCE_FILE, Texture.class);
		assets.add(MUSHROOM_SOURCE_FILE);
		manager.load(MUSHROOM_WIGGLE, Texture.class);
		assets.add(MUSHROOM_WIGGLE);

		manager.load(LIGHTS_TEXT_FILE, Texture.class);
		assets.add(LIGHTS_TEXT_FILE);
		manager.load(LIGHT_COUNTER_FILE, Texture.class);
		assets.add(LIGHT_COUNTER_FILE);

		manager.load(WATER_DARK_FILE, Texture.class);
		assets.add(WATER_DARK_FILE);
		manager.load(WATER_LIGHT_FILE, Texture.class);
		assets.add(WATER_LIGHT_FILE);

		manager.load(PICKUP_SOURCE_FILE, Texture.class);
		assets.add(PICKUP_SOURCE_FILE);

		manager.load(PAUSE_FILE, Texture.class);
		assets.add(PAUSE_FILE);

		manager.load(COOLDOWN_0, Texture.class);
		assets.add(COOLDOWN_0);
		manager.load(COOLDOWN_25, Texture.class);
		assets.add(COOLDOWN_25);
		manager.load(COOLDOWN_50, Texture.class);
		assets.add(COOLDOWN_50);
		manager.load(COOLDOWN_75, Texture.class);
		assets.add(COOLDOWN_75);
		manager.load(COOLDOWN_100, Texture.class);
		assets.add(COOLDOWN_100);

		manager.load(TUTORIAL_ZOOM, Texture.class);
		assets.add(TUTORIAL_ZOOM);

		if(TUTORIAL) {
			manager.load(TUTORIAL_FIREFLY_FILE, Texture.class);
			assets.add(TUTORIAL_FIREFLY_FILE);
			manager.load(TUTORIAL_FIREFLY_FLIPPED, Texture.class);
			assets.add(TUTORIAL_FIREFLY_FILE);

			manager.load(TUTORIAL_111, Texture.class);
			assets.add(TUTORIAL_111);
			manager.load(TUTORIAL_112, Texture.class);
			assets.add(TUTORIAL_112);
			manager.load(TUTORIAL_121, Texture.class);
			assets.add(TUTORIAL_121);
			manager.load(TUTORIAL_122, Texture.class);
			assets.add(TUTORIAL_122);
			manager.load(TUTORIAL_131, Texture.class);
			assets.add(TUTORIAL_131);
			manager.load(TUTORIAL_132, Texture.class);
			assets.add(TUTORIAL_132);
			manager.load(TUTORIAL_141, Texture.class);
			assets.add(TUTORIAL_141);
			manager.load(TUTORIAL_211, Texture.class);
			assets.add(TUTORIAL_211);
			manager.load(TUTORIAL_221, Texture.class);
			assets.add(TUTORIAL_221);
			manager.load(TUTORIAL_311, Texture.class);
			assets.add(TUTORIAL_311);
			manager.load(TUTORIAL_312, Texture.class);
			assets.add(TUTORIAL_312);
			manager.load(TUTORIAL_411, Texture.class);
			assets.add(TUTORIAL_411);
			manager.load(TUTORIAL_412, Texture.class);
			assets.add(TUTORIAL_412);
			manager.load(TUTORIAL_413, Texture.class);
			assets.add(TUTORIAL_413);
			manager.load(TUTORIAL_414, Texture.class);
			assets.add(TUTORIAL_414);
			manager.load(TUTORIAL_511, Texture.class);
			assets.add(TUTORIAL_511);
			manager.load(TUTORIAL_512, Texture.class);
			assets.add(TUTORIAL_512);
			manager.load(TUTORIAL_611, Texture.class);
			assets.add(TUTORIAL_611);
			manager.load(TUTORIAL_612, Texture.class);
			assets.add(TUTORIAL_612);
			manager.load(TUTORIAL_711, Texture.class);
			assets.add(TUTORIAL_711);
			manager.load(TUTORIAL_811, Texture.class);
			assets.add(TUTORIAL_811);
		}

		super.preLoadContent(manager);
	}

	public void setHoleTextures(AssetManager manager){
		holeTextures = new TextureRegion[25];
		holeTextures[0] = createTexture(manager, D_HOLE, false);
		holeTextures[1] = createTexture(manager, DL_HOLE, false);
		holeTextures[2] = createTexture(manager, DL_SINGLE_HOLE, false);
		holeTextures[3] = createTexture(manager, DLR_HOLE, false);
		holeTextures[4] = createTexture(manager, DR_HOLE, false);
		holeTextures[5] = createTexture(manager, DR_SINGLE_HOLE, false);
		holeTextures[6] = createTexture(manager, L_HOLE, false);
		holeTextures[7] = createTexture(manager, LR_HOLE, false);
		holeTextures[8] = createTexture(manager, LR__SINGLE_HOLE, false);
		holeTextures[9] = createTexture(manager, R_HOLE, false);
		holeTextures[10] = createTexture(manager, SINGULAR_HOLE, false);
		holeTextures[11] = createTexture(manager, U_HOLE, false);
		holeTextures[12] = createTexture(manager, UD_HOLE, false);
		holeTextures[13] = createTexture(manager, UDL_HOLE, false);
		holeTextures[14] = createTexture(manager, UDLR_HOLE, false);
		holeTextures[15] = createTexture(manager, UDR_HOLE, false);
		holeTextures[16] = createTexture(manager, UL_HOLE, false);
		holeTextures[17] = createTexture(manager, ULR_HOLE, false);
		holeTextures[18] = createTexture(manager, UR_HOLE, false);
		holeTextures[19] = createTexture(manager, UR_SINGLE_HOLE, false);
		holeTextures[20] = createTexture(manager, DLR_SINGLE_HOLE, false);
		holeTextures[21] = createTexture(manager, UDL_SINGLE_HOLE, false);
		holeTextures[22] = createTexture(manager, UDLR_SINGLE_HOLE, false);
		holeTextures[23] = createTexture(manager, UDR_SINGLE_HOLE, false);
		holeTextures[24] = createTexture(manager, ULR_SINGLE_HOLE, false);
	}

	public void setWallTextures(AssetManager manager){
		wallTextures = new TextureRegion[29];
		wallTextures[0] = createTexture(manager, D_WALL, false);
		wallTextures[1] = createTexture(manager, DL_WALL, false);
		wallTextures[2] = createTexture(manager, DL_SINGLE_WALL, false);
		wallTextures[3] = createTexture(manager, DLR_WALL, false);
		wallTextures[4] = createTexture(manager, DR_WALL, false);
		wallTextures[5] = createTexture(manager, DR_SINGLE_WALL, false);
		wallTextures[6] = createTexture(manager, L_WALL, false);
		wallTextures[7] = createTexture(manager, LR_WALL, false);
		wallTextures[8] = createTexture(manager, LR__SINGLE_WALL, false);
		wallTextures[9] = createTexture(manager, R_WALL, false);
		wallTextures[10] = createTexture(manager, SINGULAR_WALL, false);
		wallTextures[11] = createTexture(manager, U_WALL, false);
		wallTextures[12] = createTexture(manager, UD_WALL, false);
		wallTextures[13] = createTexture(manager, UDL_WALL, false);
		wallTextures[14] = createTexture(manager, UDLR_WALL, false);
		wallTextures[15] = createTexture(manager, UDR_WALL, false);
		wallTextures[16] = createTexture(manager, UL_WALL, false);
		wallTextures[17] = createTexture(manager, ULR_WALL, false);
		wallTextures[18] = createTexture(manager, UR_WALL, false);
		wallTextures[19] = createTexture(manager, UR_SINGLE_WALL, false);

		wallTextures[20] = createTexture(manager, DLR_SINGLE_WALL, false);
		wallTextures[21] = createTexture(manager, UDL_SINGLE_WALL, false);
		wallTextures[22] = createTexture(manager, UDLR_SINGLE_WALL, false);
		wallTextures[23] = createTexture(manager, UDR_SINGLE_WALL, false);
		wallTextures[24] = createTexture(manager, ULR_SINGLE_WALL, false);

		wallTextures[25] = createTexture(manager, DL_COVER_WALL, false);
		wallTextures[26] = createTexture(manager, DR_COVER, false);
		wallTextures[27] = createTexture(manager, UL_COVER, false);
		wallTextures[28] = createTexture(manager, UR_COVER, false);
	}

	public void setTutorialTextures(AssetManager manager){
		tutorialFirefly = createTexture(manager, TUTORIAL_FIREFLY_FILE, false);
		tutorialFireflyFlipped = createTexture(manager, TUTORIAL_FIREFLY_FLIPPED, false);

		tutorial111 = createTexture(manager, TUTORIAL_111, false);
		tutorial112 = createTexture(manager, TUTORIAL_112, false);
		tutorial121 = createTexture(manager, TUTORIAL_121, false);
		tutorial122 = createTexture(manager, TUTORIAL_122, false);
		tutorial131 = createTexture(manager, TUTORIAL_131, false);
		tutorial132 = createTexture(manager, TUTORIAL_132, false);
		tutorial141 = createTexture(manager, TUTORIAL_141, false);
		tutorial211 = createTexture(manager, TUTORIAL_211, false);
		tutorial221 = createTexture(manager, TUTORIAL_221, false);
		tutorial311 = createTexture(manager, TUTORIAL_311, false);
		tutorial312 = createTexture(manager, TUTORIAL_312, false);
		tutorial411 = createTexture(manager, TUTORIAL_411, false);
		tutorial412 = createTexture(manager, TUTORIAL_412, false);
		tutorial413 = createTexture(manager, TUTORIAL_413, false);
		tutorial414 = createTexture(manager, TUTORIAL_414, false);
		tutorial511 = createTexture(manager, TUTORIAL_511, false);
		tutorial512 = createTexture(manager, TUTORIAL_512, false);
		tutorial611 = createTexture(manager, TUTORIAL_611, false);
		tutorial612 = createTexture(manager, TUTORIAL_612, false);
		tutorial711 = createTexture(manager, TUTORIAL_711, false);
		tutorial811 = createTexture(manager, TUTORIAL_811, false);

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
		playerAnimationFront = createTexture(manager, PLAYER_ANIMATION_FRONT, false);
		playerAnimationBack = createTexture(manager, PLAYER_ANIMATION_BACK, false);
		playerAnimationLeft = createTexture(manager, PLAYER_ANIMATION_LEFT, false);
		playerAnimationRight = createTexture(manager, PLAYER_ANIMATION_RIGHT, false);
		placingLightFront = createTexture(manager, PLACING_LIGHT_FRONT, false);
		takingLightFront = createTexture(manager, TAKING_LIGHT_FRONT, false);
		placingLightLeft = createTexture(manager, PLACING_LIGHT_LEFT, false);
		takingLightLeft = createTexture(manager, TAKING_LIGHT_LEFT, false);
		placingLightRight = createTexture(manager, PLACING_LIGHT_RIGHT, false);
		takingLightRight = createTexture(manager, TAKING_LIGHT_RIGHT, false);
		throwingLightFront = createTexture(manager, THROW_LIGHT_FRONT, false);
		throwingLightBack = createTexture(manager, THROW_LIGHT_BACK, false);
		throwingLightLeft = createTexture(manager, THROW_LIGHT_LEFT, false);
		throwingLightRight = createTexture(manager, THROW_LIGHT_RIGHT, false);
		playerFrontIdle = createTexture(manager, PLAYER_FRONT_IDLE, false);
		playerBackIdle = createTexture(manager, PLAYER_BACK_IDLE, false);
		playerLeftIdle = createTexture(manager, PLAYER_LEFT_IDLE, false);
		playerRightIdle = createTexture(manager, PLAYER_RIGHT_IDLE, false);
		death = createTexture(manager, DEATH, false);

		enemyAngryAnimationFront = createTexture(manager, ENEMY_ANGRY_ANIMATION_FRONT, false);
		enemyAngryAnimationBack = createTexture(manager, ENEMY_ANGRY_ANIMATION_BACK, false);
		enemyAngryAnimationLeft = createTexture(manager, ENEMY_ANGRY_ANIMATION_LEFT, false);
		enemyAngryAnimationRight = createTexture(manager, ENEMY_ANGRY_ANIMATION_RIGHT, false);

		enemyAnimationFront = createTexture(manager, ENEMY_ANIMATION_FRONT, false);
		enemyAnimationBack = createTexture(manager, ENEMY_ANIMATION_BACK, false);
		enemyAnimationLeft = createTexture(manager, ENEMY_ANIMATION_LEFT, false);
		enemyAnimationRight = createTexture(manager, ENEMY_ANIMATION_RIGHT, false);

		enemyTransformation = createTexture(manager, ENEMY_TRANSFORMATION, false);
		enemyAnimationSaved = createTexture(manager, ENEMY_ANIMATION_SAVED, false);

		playerTextureLeft = createTexture(manager, PLAYER_FILE_LEFT, false);
		playerTextureFront = createTexture(manager, PLAYER_FILE_FRONT, false);
		playerTextureBack = createTexture(manager, PLAYER_FILE_BACK, false);
		enemyTexture = createTexture(manager, ENEMY_FILE, false);
		seenTexture = createTexture(manager, SEEN_FILE, false);
		savedEnemyTexture = createTexture(manager, SAVED_ENEMY_FILE, false);
		winScreenTexture = createTexture(manager, WIN_SCREEN_FILE, false);
		lossScreenTexture = createTexture(manager, LOSS_SCREEN_FILE, false);

		dimSourceTexture = createTexture(manager, DIM_SOURCE_FILE, false);
		litSourceTexture = createTexture(manager, LIT_SOURCE_FILE, false);
		lightAnimation = createTexture(manager, LIGHT_ANIMATION_FILE, false);

		grassTexture = createTexture(manager, GRASS_SOURCE_FILE, false);
		mushroomTexture = createTexture(manager, MUSHROOM_SOURCE_FILE, false);
		mushroomWiggle = createTexture(manager, MUSHROOM_WIGGLE, false);

		lightsTexture = createTexture(manager, LIGHTS_TEXT_FILE, false);
		lightCounterTexture = createTexture(manager, LIGHT_COUNTER_FILE, false);

		waterDarkTexture = createTexture(manager, WATER_DARK_FILE, false);
		waterLightTexture = createTexture(manager, WATER_LIGHT_FILE, false);

		pickupTexture = createTexture(manager, PICKUP_SOURCE_FILE, false);

		pauseTexture = createTexture(manager, PAUSE_FILE, false);

		tutorialZoom = createTexture(manager, TUTORIAL_ZOOM, false);


		if(TUTORIAL){
			setTutorialTextures(manager);
		}

		setWallTextures(manager);
		setHoleTextures(manager);

		cooldownTextures = new TextureRegion[]{
				createTexture(manager, COOLDOWN_0, false),
				createTexture(manager, COOLDOWN_25, false),
				createTexture(manager, COOLDOWN_50, false),
				createTexture(manager, COOLDOWN_75, false),
				createTexture(manager, COOLDOWN_100, false)};

		pickupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/off.mp3"));

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
	//protected GameCanvas canvas;
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

	private int timer;
	private boolean cooldown;

	private Player player;
	private boolean inLitTile;

	private Enemy[] enemies;

	private ArtObject[] artObjects;

	private static float muteCooldown = 0.0f;
	private static boolean canMute = true;
	private static Music music;
	private static Sound deathSound;
	private static boolean musicPlaying = false;
	private static float volume = 1.0f;



	/**
	 * Stores all the AI controllers
	 */
	protected AIController[] controls;


	LightSourceObject[] lights;
	private RayHandler sourceRayHandler;
	private OrthographicCamera rayCamera;
	private int[] spawn;
	private int initLights;
	private int[] walls;
	private int[] water; //Now holes
	private HashMap<LightSourceLight, Long> thrownLights;
	CollisionController collisions;

	private ArtObject[] pickups;
	private TutorialObject[] tutorialTexts;


	/** The reader to process JSON files */
	private JsonReader jsonReader;
	/** The JSON defining the level model */
	private JsonValue  levelFormat;

	BitmapFont font = new BitmapFont();


	public void parseJson(){
		float[] dim = levelFormat.get("dimension").asFloatArray();
		BOARD_WIDTH = dim[0];
		BOARD_HEIGHT = dim[1];
		spawn = levelFormat.get("spawn").asIntArray();
		initLights = levelFormat.getInt("init_lights");

		// Parse Walls; dumb format
		int idx = 0;
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

		// Parse Lights
		JsonValue lights_json = levelFormat.get("lights");
		lights = new LightSourceObject[lights_json.size];
		JsonValue light = lights_json.child();
		idx = 0;
		while (light != null){
			int[] pos = light.get("position").asIntArray();
			lights[idx] = new LightSourceObject(pos[0], pos[1], 1.5f, 1.5f,light.getBoolean("lit"));
			idx++;
			light = light.next();
		}

		// Parse Enemies
		JsonValue enemies_json = levelFormat.get("enemies");
		enemies = new Enemy[enemies_json.size];
		controls = new AIController[enemies_json.size];
		JsonValue enemy = enemies_json.child();
		idx = 0;
		while (enemy != null){
			int[] pos = enemy.get("position").asIntArray();
			JsonValue wander = enemy.get("wander");
			enemies[idx] = new Enemy(pos[0], pos[1], 0.5f, 0.5f, enemy.getInt("type"));
			enemies[idx].setWander(wander);
			idx++;
			enemy = enemy.next();
		}

		// Parse Art Objects (Mushrooms / Grass)
		JsonValue grass_json = levelFormat.get("grass");
		JsonValue mushrooms_json = levelFormat.get("mushrooms");
		artObjects = new ArtObject[grass_json.size + mushrooms_json.size];
		JsonValue coord = grass_json.child();
		idx = 0;
		while (coord != null){
			int[] pos = coord.asIntArray();
			artObjects[idx] =
					new ArtObject(pos[0], pos[1], 1, 1, 75, 7, ArtObject.ASSET_TYPE.GRASS);
			coord = coord.next();
			idx++;
		}
		coord = mushrooms_json.child();
		while (coord != null) {
			int[] pos = coord.asIntArray();
			artObjects[idx] =
					new ArtObject(pos[0], pos[1], 1, 1, 75, 5, ArtObject.ASSET_TYPE.MUSHROOM);
			coord = coord.next();
			idx++;
		}

		// Parse Water
		JsonValue water_json = levelFormat.get("water"); //water is really holes
		water = new int[water_json.size * 2];
		coord = water_json.child();
		idx = 0;
		while (coord != null){
			int[] pos = coord.asIntArray();
			water[idx] = pos[0];
			water[idx + 1] = pos[1];
			idx+=2;
			coord = coord.next();
		}
		// Parse Pickups
		JsonValue pickup_json = levelFormat.get("pickup");
		pickups = new ArtObject[pickup_json.size];
		coord = pickup_json.child();
		idx = 0;
		while (coord != null){
			int[] pos = coord.asIntArray();
			pickups[idx] = new ArtObject(pos[0], pos[1], 1, 1, 50, 8, ArtObject.ASSET_TYPE.PICKUP);
			idx++;
			coord = coord.next();
		}
	}


	/**
	 * Creates and initialize a new instance of the rocket lander game
	 * <p>
	 * The game has default gravity and other settings
	 */
	public GameplayController(String json, int id) {
//		System.out.println(json);
		jsonReader = new JsonReader();
		LEVEL_PATH = json;
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);

		LEVEL_ID = id; //starts at 0
		if(LEVEL_ID <8){ //levels 0-7; first 8 levels
			TUTORIAL = true;
		} else {
			TUTORIAL = false;
		}

//		System.out.println("level "+LEVEL_ID+", tutorial: "+TUTORIAL);
	}


	/**
	 * Resets the status of the game so that we can play again.
	 * <p>
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		if(controls != null) {
			for (AIController controller : controls) {
				controller.resetSound();
			}
		}

		Vector2 gravity = new Vector2(world.getGravity());

		for (Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		world = new World(gravity, false);
		world.setContactListener(this);

		if (sourceRayHandler != null)
			sourceRayHandler.dispose();

		setComplete(false);
		setFailure(false);
		wonGame = false;
		lostGame = false;

		// Reload the level json
		levelFormat = jsonReader.parse(Gdx.files.internal(LEVEL_PATH));
		parseJson();
		populateLevel();
		board.reset(walls, lights);
	}


	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		currentScale = ZOOM_OUT_SCALE;
		startPause = true;
		zoom_out = false;
		zoom_in = false;
		removePressAnyKeyText = false;

		canvas.setScale(currentScale);
		initLighting();

		for (int i = 0; i < lights.length; i++){

			LightSourceLight light_s = new LightSourceLight(sourceRayHandler);
			lights[i].setAnimation(lightAnimation);
			lights[i].addLight(light_s);
			lights[i].setSensor(true);
			lights[i].setDrawScale(scale);
			lights[i].setBodyType(BodyDef.BodyType.StaticBody);
			if (lights[i].isLit()){
				lights[i].setTexture(litSourceTexture);
			}
			else {
				lights[i].setTexture(dimSourceTexture);
			}
			lights[i].setTextureCache(litSourceTexture, dimSourceTexture);
			addObject(lights[i]);
		}
		if(!musicPlaying) {
			music = Gdx.audio.newMusic(Gdx.files.internal("sounds/bgm.mp3"));
			music.setLooping(true);
			music.setVolume(volume);
			music.play();
			musicPlaying = true;
		}
		deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/death.mp3"));
		//initialize thrown lights
		thrownLights = new HashMap<>();

		for(int i = 0; i < enemies.length; i ++) {

			SightConeLight sight = new SightConeLight(sourceRayHandler);
			enemies[i].addSight(sight);

			enemies[i].setSensor(true);
			enemies[i].setDrawScale(scale);
			enemies[i].setAnimations(enemyAnimationFront, enemyAnimationBack, enemyAnimationLeft, enemyAnimationRight,
					enemyTransformation, enemyAnimationSaved);
			enemies[i].setAngryAnimations(enemyAngryAnimationFront, enemyAngryAnimationBack, enemyAngryAnimationLeft,
					enemyAngryAnimationRight, enemyTransformation, enemyAnimationSaved);
			enemies[i].setTexture(enemyTexture);
			addObject(enemies[i]);
		}

		// Make Board
		board = new Board((int) BOARD_WIDTH, (int) BOARD_HEIGHT, walls, lights, water);

		// Add Walls
		Wall wall;
		for (int i = 0; i < walls.length; i+=2){
			wall = new Wall(walls[i], walls[i + 1], 1, 1);
			wall.setBodyType(BodyDef.BodyType.KinematicBody);
			wall.setDensity(BASIC_DENSITY);
			wall.setFriction(BASIC_FRICTION);
			wall.setRestitution(BASIC_RESTITUTION);
			wall.setDrawScale(scale);
			wall.setTextures(wallTextures);
			wall.setTexture(board);
			wall.setDraw_scale(WALL_SCALE);
			addObject(wall);
		}

		// Add Water
		// We don't set the texture here since it changes
		// Texture is set by board
		for (int i = 0; i < water.length; i+=2){
			wall = new Wall(water[i], water[i + 1], 1, 1);
			wall.setBodyType(BodyDef.BodyType.KinematicBody);
			wall.setDensity(BASIC_DENSITY);
			wall.setFriction(BASIC_FRICTION);
			wall.setRestitution(BASIC_RESTITUTION);
			wall.setDrawScale(scale);
			wall.setTextures(holeTextures);
			wall.setHoleTexture(board);
			wall.setDraw_scale(HOLE_SCALE);
			wall.getFilterData().categoryBits = Constants.BIT_WATER;
			addObject(wall);
		}


		// Create border pieces
		Wall border;
		for (int ii = 0; ii < BOARD_WIDTH  ; ii++) {
			border = new Wall(ii, 0, 1, 1);
			border.setBodyType(BodyDef.BodyType.KinematicBody);
			border.setDensity(BASIC_DENSITY);
			border.setFriction(BASIC_FRICTION);
			border.setRestitution(BASIC_RESTITUTION);
			border.setDrawScale(scale);
			border.setTextures(wallTextures);
			border.setTexture(board);
			border.setDraw_scale(WALL_SCALE);
			addObject(border);
			border = new Wall(ii, BOARD_HEIGHT - 1, 1, 1);

			border.setBodyType(BodyDef.BodyType.KinematicBody);
			border.setDensity(BASIC_DENSITY);
			border.setFriction(BASIC_FRICTION);
			border.setRestitution(BASIC_RESTITUTION);
			border.setDrawScale(scale);
			border.setTextures(wallTextures);
			border.setDraw_scale(WALL_SCALE);

			border.setTexture(board);
			addObject(border);

		}
		for (int jj = 0; jj < BOARD_HEIGHT ; jj++) {
			border = new Wall(0, jj, 1, 1);
			border.setBodyType(BodyDef.BodyType.KinematicBody);
			border.setDensity(BASIC_DENSITY);
			border.setFriction(BASIC_FRICTION);
			border.setRestitution(BASIC_RESTITUTION);
			border.setDrawScale(scale);
			border.setTextures(wallTextures);
			border.setTexture(board);
			border.setDraw_scale(WALL_SCALE);

			addObject(border);

			border = new Wall(BOARD_WIDTH - 1, jj, 1, 1);
			border.setBodyType(BodyDef.BodyType.KinematicBody);
			border.setDensity(BASIC_DENSITY);
			border.setFriction(BASIC_FRICTION);
			border.setRestitution(BASIC_RESTITUTION);
			border.setDrawScale(scale);
			border.setTextures(wallTextures);
			border.setDraw_scale(WALL_SCALE);

			border.setTexture(board);
			addObject(border);
		}


		// Add Player
		player = new Player(spawn[0], spawn[1], 0.5f, 0.5f, initLights, volume);
		this.spawnx = spawn[0];
		this.spawny = spawn[1];

		player.setDrawScale(scale);
		player.setAnimations(playerAnimationFront, playerAnimationBack, playerAnimationLeft, playerAnimationRight,
				placingLightFront, takingLightFront, placingLightLeft, takingLightLeft, placingLightRight,
				takingLightRight, playerFrontIdle, playerBackIdle, playerLeftIdle, playerRightIdle, throwingLightFront,
				throwingLightBack, throwingLightRight, throwingLightLeft, death); //setting animation
		player.setTexture(playerTextureFront);
		player.setCooldownTextures(cooldownTextures);

		addObject(player);

		// Make AI Controllers
		for (int idx = 0; idx < enemies.length; idx++){
			controls[idx] = new AIController(enemies[idx], board, player, enemies, getWorldStep(), volume);
		}

		//Add Art Objects
		for (ArtObject artObject : artObjects) {
			artObject.setAnimation(artObject.type == ArtObject.ASSET_TYPE.GRASS ? grassTexture : mushroomTexture);
			artObject.setHitAnimation(artObject.type == ArtObject.ASSET_TYPE.MUSHROOM ? mushroomWiggle : null);
			artObject.setDrawScale(scale);
			artObject.setBodyType(BodyDef.BodyType.StaticBody);
			artObject.setSensor(true);
			addObject(artObject);
		}

		// Add pickups
		for (ArtObject pickup : pickups){
			pickup.setAnimation(pickupTexture);
			pickup.setDrawScale(scale);
			pickup.setBodyType(BodyDef.BodyType.StaticBody);
			pickup.setSensor(true);
			addObject(pickup);
		}

		// Add Tutorial Texts
		if(TUTORIAL){
//			System.out.println("level "+LEVEL_ID+", tutorial: "+TUTORIAL+"; adding tutorial text");

			tutorialTexts = new TutorialObject[0]; //default; empty list

			if(LEVEL_ID==0){
				tutorialTexts = new TutorialObject[8];
				//width & height just have to be large enough... I manually sized the files to the correct size...
				tutorialTexts[0] = new TutorialObject(-5, 5, 500, 500);
				tutorialTexts[0].setTexture(tutorial111);

				tutorialTexts[1] = new TutorialObject(-5, 2, 500, 500);
				tutorialTexts[1].setTexture(tutorial112);

				tutorialTexts[2] = new TutorialObject(7, -2, 300, 300);
				tutorialTexts[2].setTexture(tutorial121);

				tutorialTexts[3] = new TutorialObject(2, -2, 300, 300);
				tutorialTexts[3].setTexture(tutorial122);

				tutorialTexts[4] = new TutorialObject(3, 7, 300, 300);
				tutorialTexts[4].setTexture(tutorial131);

				tutorialTexts[5] = new TutorialObject(7, 7, 300, 300);
				tutorialTexts[5].setTexture(tutorial132);

				tutorialTexts[6] = new TutorialObject(6, 3, 300, 300);
				tutorialTexts[6].setTexture(tutorial141);

				tutorialTexts[7] = new TutorialObject(-5, -1, 300, 300);
				tutorialTexts[7].setTexture(tutorialFirefly);

			} else if(LEVEL_ID==1){
				tutorialTexts = new TutorialObject[3];
				tutorialTexts[0] = new TutorialObject(-4, 2, 500, 500);
				tutorialTexts[0].setTexture(tutorial211);

				tutorialTexts[1] = new TutorialObject(8, 2, 500, 500);
				tutorialTexts[1].setTexture(tutorial221);

				tutorialTexts[2] = new TutorialObject(-4, 5, 300, 300);
				tutorialTexts[2].setTexture(tutorialFirefly);

			} else if(LEVEL_ID==2){
				tutorialTexts = new TutorialObject[3];
				tutorialTexts[0] = new TutorialObject(5, 11, 600, 600);
				tutorialTexts[0].setTexture(tutorial311);

				tutorialTexts[1] = new TutorialObject(7, 7, 500, 500);
				tutorialTexts[1].setTexture(tutorial312);

				tutorialTexts[2] = new TutorialObject(0, 11, 300, 300);
				tutorialTexts[2].setTexture(tutorialFirefly);

			} else if(LEVEL_ID==3){
				tutorialTexts = new TutorialObject[3];
				tutorialTexts[0] = new TutorialObject(-5, 4, 500, 500);
				tutorialTexts[0].setTexture(tutorial411);

				tutorialTexts[1] = new TutorialObject(-5, 2, 500, 500);
				tutorialTexts[1].setTexture(tutorial412);

				tutorialTexts[2] = new TutorialObject(-5, -1, 300, 300);
				tutorialTexts[2].setTexture(tutorialFirefly);

			} else if(LEVEL_ID==4){
				tutorialTexts = new TutorialObject[3];
//				tutorialTexts[0] = new TutorialObject(6, 8, 500, 500); // location for transparent text

				tutorialTexts[0] = new TutorialObject(4, 10, 500, 500);
				tutorialTexts[0].setTexture(tutorial511);

				tutorialTexts[1] = new TutorialObject(8, 10, 500, 500);
				tutorialTexts[1].setTexture(tutorial512);

				tutorialTexts[2] = new TutorialObject(0, 11, 500, 500);
				tutorialTexts[2].setTexture(tutorialFireflyFlipped);

			} else if(LEVEL_ID==5){
				tutorialTexts = new TutorialObject[3];
//				tutorialTexts[0] = new TutorialObject(6, 8, 500, 500);

				tutorialTexts[0] = new TutorialObject(18, 5, 500, 500);
				tutorialTexts[0].setTexture(tutorial611);

				tutorialTexts[1] = new TutorialObject(18, 4, 500, 500);
				tutorialTexts[1].setTexture(tutorial612);

				tutorialTexts[2] = new TutorialObject(18, 7, 500, 500);
				tutorialTexts[2].setTexture(tutorialFirefly);

			} else if(LEVEL_ID==6){
				tutorialTexts = new TutorialObject[1];
//				tutorialTexts[0] = new TutorialObject(6, 8, 500, 500);

				tutorialTexts[0] = new TutorialObject(5, 8, 500, 500);
				tutorialTexts[0].setTexture(tutorial711);

			} else if(LEVEL_ID==7){
				tutorialTexts = new TutorialObject[1];
//				tutorialTexts[0] = new TutorialObject(6, 8, 500, 500);

				tutorialTexts[0] = new TutorialObject(5, 2, 500, 500);
				tutorialTexts[0].setTexture(tutorial811);

			}

			for (int i=0; i<tutorialTexts.length;i++){
				tutorialTexts[i].setDrawScale(scale);
				tutorialTexts[i].setBodyType(BodyDef.BodyType.StaticBody);
				tutorialTexts[i].setSensor(true);
				addObject(tutorialTexts[i]);
			}
//			System.out.println("level "+LEVEL_ID+", tutorial: "+TUTORIAL+"; done adding tutorial texts");

		}
	}

	public void initLighting() {
		rayCamera = new OrthographicCamera(Gdx.graphics.getWidth() /  (64f), Gdx.graphics.getHeight() / (64f));
		rayCamera.position.set(spawnx, spawny, 0);
		rayCamera.zoom = currentScale;
		rayCamera.update();

		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		sourceRayHandler = new RayHandler(world);
		sourceRayHandler.setCombinedMatrix(rayCamera);
		sourceRayHandler.useCustomViewport(0, 0, canvas.getWidth(), canvas.getHeight());

		sourceRayHandler.setAmbientLight(Constants.AMBIANCE, Constants.AMBIANCE, Constants.AMBIANCE, Constants.AMBIANCE);
		//sourceRayHandler.setShadows(false);
		sourceRayHandler.setBlur(true);
		sourceRayHandler.setBlurNum(3);
	}


	public void unmute(){
		volume = 1.0f;
		player.unmute();
		for (LightSourceObject l : lights){
			l.unmute();
		}
		for (AIController a : controls){
			a.unmute();
		}
		music.setVolume(1.0f);
		muteCooldown = 0;
		canMute = false;
		muted = !muted;
	}

	public void mute(){
		volume = 0.0f;
		player.mute();
		for (LightSourceObject l : lights){
			l.mute();
		}
		for (AIController a : controls) {
			a.mute();
		}
		music.setVolume(0.0f);
		muteCooldown = 0;
		canMute = false;
		muted = !muted;
	}

	public void muteEnemySound(){
		for (AIController a : controls) {
			a.mute();
		}
	}
	public void unmuteEnemySound(){
		for (AIController a : controls) {
			a.unmute();
		}
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

		player.insideThrownLight = false;
		inLitTile = false;

		if (lostGame){
			// Play Lost Animation Here
			player.move(InputController.Move_Direction.NO_MOVE, dt);
			return;
		}

		if (sourceRayHandler != null) {
			rayCamera.position.set(player.getPosition(), 0);
			rayCamera.update();
			sourceRayHandler.setCombinedMatrix(rayCamera);
			sourceRayHandler.update();
		}

		InputController input = InputController.getInstance();
		input.readInput(bounds, scale);
		InputController.Move_Direction next_move = input.get_Next_Direction();

		if (currentScale != ZOOM_IN_SCALE && input.didZoom()) {
			zoom_in = true;
			removePressAnyKeyText = true;
		}

		// Handle camera zoom
		if (zoom_in) {
			currentScale -= 0.05f * (60f * dt);
			if (currentScale <= ZOOM_IN_SCALE) {
				currentScale = ZOOM_IN_SCALE;
				zoom_in = false;
			}
		} else if (zoom_out) {
			currentScale += 0.05f * (60f * dt);
			if (currentScale  >= ZOOM_OUT_SCALE) {
				currentScale = ZOOM_OUT_SCALE;
				zoom_out = false;
			}
		}
		canvas.setScale(currentScale);
		rayCamera.zoom = currentScale;

		if (input.didMute()){
			if(muted && canMute){ // If currently muted, unmute
				volume = 1.0f;
				player.unmute();
				for (LightSourceObject l : lights){
					l.unmute();
				}
				for (AIController a : controls){
					a.unmute();
				}
				music.setVolume(1.0f);
				muteCooldown = 0;
				canMute = false;
				muted = !muted;
			}

			else if (!muted && canMute){ // If currently not muted, mute
				volume = 0.0f;
				player.mute();
				for (LightSourceObject l : lights){
					l.mute();
				}
				for (AIController a : controls) {
					a.mute();
				}
				music.setVolume(0.0f);
				muteCooldown = 0;
				canMute = false;
				muted = !muted;
			}
		}
		muteCooldown += Gdx.graphics.getDeltaTime();
//		System.out.println(muteCooldown);
		if(muteCooldown >= .5){
			canMute = true;
		}

		//remove old thrown light
		if(!thrownLights.isEmpty()){
			HashSet<LightSourceLight> toRemove = new HashSet<>();
			for (Iterator<LightSourceLight> it = thrownLights.keySet().iterator(); it.hasNext(); ) {
				LightSourceLight l = it.next();
				if (System.currentTimeMillis() - thrownLights.get(l) > 2000L) {
					l.setActive(false);
					toRemove.add(l); }
			}
			for(LightSourceLight l : toRemove){
				thrownLights.remove(l);
			}

		}

		//player movement

		if (currentScale == ZOOM_IN_SCALE) {
			player.move(next_move, dt);
			player.updateCooldown(dt);

			if (input.didSecondary() && player.getTouchingLight() && !player.getToggleCooldown()) {
				LightSourceObject goalLight = null;

				for (LightSourceObject light : lights) {
					if (light.getTouchingPlayer())
						goalLight = light;
				}

				if (goalLight != null && goalLight.isLit() && player.getLightCounter() <= Constants.MAX_LIGHTS) {
					player.takeLight();
					goalLight.toggleLit();
					board.toggleSource(goalLight.getPosition());
				} else if (goalLight != null && !goalLight.isLit() && player.getLightCounter() > 0) {
					player.placeLight();
					goalLight.toggleLit();
					board.toggleSource(goalLight.getPosition());
				}
			}
		}
		// update board
		board.update(player.getPosition(), dt, player.getScaledPosition());

		this.inLitTile = insideLightSource(player.getPosition());

		//throw light
		if((input.didShift() && player.lightCounter > 0 && !player.getThrowCooldown()) &&
				(thrownLights.isEmpty() || over500()) && currentScale == ZOOM_IN_SCALE) {

			LightSourceLight light = new LightSourceLight(sourceRayHandler, THROWN_LIGHT_RADIUS + 2); //don't know why this is necesary, something weird going on with light radius
			light.setColor(Color.PURPLE);
			light.setPosition(player.getX(), player.getY());
			thrownLights.put(light, System.currentTimeMillis());
			player.throwLight();
			player.insideThrownLight = true;

			//find enemies in range
			for (Enemy e : enemies) {
				float distance = player.getPosition().dst(e.getPosition());
				if (distance <= THROWN_LIGHT_RADIUS) {
					float dx = e.getPosition().x - player.getX();
					float dy = e.getPosition().y - player.getY();
					Vector2 direction = (new Vector2(dx, dy)).nor();
					float ratio = THROWN_LIGHT_RADIUS / distance;
					Vector2 new_pos = new Vector2(Math.round((dx * ratio) + player.getX()), Math.round((dy * ratio) + player.getY()));
					Vector2 thrown_pos = getThrownPosition(player.getPosition(), e.getPosition(), direction);
					e.setPosition(new Vector2((int)thrown_pos.x, (int)thrown_pos.y));
					e.stunned = true;
					e.collidedWithPlayer = false;
				}
			}
		}

		//update Art objects
		for(ArtObject obj : artObjects){
			if (board.isLitTileBoard((int)obj.getX(), (int)obj.getY())){
				obj.setLit(true);
			} else {
				obj.setLit(false);
			}
		}

		// Do enemy movement
		// Enemy Movement
		for (AIController controller : controls){
			controller.move(isPlayerLit(), dt);
			Enemy enemy = controller.getEnemy();
			//board.updateSeenTiles(enemy.getPosition(), enemy.getFacingDirection());
			enemy.updateSightCone();

			// Update Enemy Angry
			if (controller.getState() == AIController.FSMState.CHASE
					|| controller.getState() == AIController.FSMState.GOTO){
				enemy.angry = true;
			}
			else {
				enemy.angry = false;
			}
		}

		// Check win Condition
		int numLit = 0;
		for (Enemy e : enemies){
			if (e.getIsLit())
				numLit ++;
		}
		wonGame = (numLit == enemies.length);
		if(wonGame){
			for(AIController controller : controls){
				controller.resetSound();
			}
		}

		// Check loss condition
		// Timer allows game to err on side of player
		timer++;
		int numNotCollided = 0;
		for (Enemy e : enemies){
			if (e.collidedWithPlayer && !e.getIsLit()){
				if (timer % GRACE_PERIOD == 0){
					lostGame = true;
					player.die();
					timer = 0;
					deathSound.play(volume*.25f);
					for(AIController controller : controls){
						controller.resetSound();
					}
				}
			}
			else {
				numNotCollided++;
				e.collidedWithPlayer = false;
			}
		}

		if (numNotCollided == enemies.length){
			timer = 0;
		}


		int numAngry = 0;
		for (Enemy e : enemies){
			if (e.angry == true)
				numAngry ++;
		}
		if(numAngry == 0){
			for(AIController controller : controls){
				controller.resetSound();
			}
		}
	}

	public Vector2 getThrownPosition(Vector2 playerPosition,Vector2 enemyPosition, Vector2 direction){
		float distance = playerPosition.dst(enemyPosition);
		if(distance >= THROWN_LIGHT_RADIUS){
			return enemyPosition;
		}
		Vector2 scaler = new Vector2(0.1f * direction.x, 0.1f * direction.y);

		Vector2 newPos = new Vector2(enemyPosition.x + scaler.x, enemyPosition.y + scaler.y);
		if(board.isWall((int)newPos.x, (int)newPos.y) || board.isWall((int)newPos.x + 1, (int)newPos.y) ||
				board.isWall((int)newPos.x, (int)newPos.y + 1) || board.isWall((int)newPos.x + 1, (int)newPos.y + 1)){
			return enemyPosition;
		}
		return getThrownPosition(playerPosition, newPos, direction);
	}

	@Override
	public void render(float delta) {
		if (isActive()) {
			if (preUpdate(delta)) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);
			}
			draw(delta, board);

		}
	}

	@Override
	public void draw(float delta, Board board) {
		canvas.clear();

		//set the players location
		canvas.setCamera_coordinates(player.getScaledPosition());

		// draw everything that should be affected by lighting (everything excluding walls)
		canvas.begin();
		board.draw(canvas);
		for(Obstacle obj : objects) {
			if (!(obj instanceof BoxObstacle || obj instanceof PolygonObstacle || obj instanceof FeetHitboxObstacle))
				obj.draw(canvas);
		}
		canvas.end();

		// render the light
		sourceRayHandler.render();

		// draw things that should not be affected by shadows
		canvas.begin();

		for(Obstacle obj : objects) {
			if (obj instanceof Wall)
				obj.draw(canvas);
		}

		for(Obstacle obj : objects) {
			if ((obj instanceof BoxObstacle || obj instanceof PolygonObstacle || obj instanceof FeetHitboxObstacle)
					&& !(obj instanceof Wall) && !(obj instanceof Enemy) && !(obj instanceof TutorialObject))
				obj.draw(canvas);
		}

		if(TUTORIAL){
			for (int i=0; i<tutorialTexts.length;i++){
				tutorialTexts[i].draw(canvas);
			}
		}

		// Draw enemies + player + lights; this is redundant but needed for correct ordering of textures
		for (LightSourceObject light : lights){
			light.draw(canvas);
		}
		for (Enemy e: enemies){
			e.draw(canvas);
		}
		player.draw(canvas);
		// Draw Exclamation Points
		for (AIController controller : controls){
			if (controller.getState() == AIController.FSMState.PAUSED){
				controller.playAlarm();
				float x = board.boardToScreenCenter(Math.round(controller.getEnemy().getPosition().x));
				float y = board.boardToScreenCenter(Math.round(controller.getEnemy().getPosition().y) + 1);
				canvas.draw(seenTexture, Color.WHITE, 0, 0, x, y, 0, 1 , 1 );
			}
		}
		drawUI();


		canvas.end();

		//draw the  UI

//		if (debug) {
//			canvas.beginDebug();
//			for(Obstacle obj : objects) {
//				obj.drawDebug(canvas);
//			}
//			canvas.endDebug();
//		}

		if (complete && !failed) {
			canvas.begin(); // DO NOT SCALE
			canvas.end();
		} else if (failed) {
			canvas.begin(); // DO NOT SCALE
			canvas.end();
		}

	}

	private void drawUI() {

		// Magic Numbers - will change later
		canvas.draw(lightsTexture, 10, canvas.getHeight() - 75);
		if (player.getLightCounter() > 0){
			for (int i = 0; i < player.getLightCounter(); i++){
				canvas.draw(lightCounterTexture, 105 + i*62, canvas.getHeight() - 90);
			}
		}
		canvas.draw(pauseTexture, canvas.getWidth() - 150, canvas.getHeight() - 125);

		if(LEVEL_ID==3 && TUTORIAL){
			canvas.draw(tutorial413, 380, canvas.getHeight() - 90);
			canvas.draw(tutorial414, 250, canvas.getHeight() - 75);
		}
		if(!removePressAnyKeyText) {
			canvas.draw(tutorialZoom, 0, 0);
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

	private boolean insideLightSource(Vector2 pos) {
		return board.isLit(pos);
	}

	public boolean isPlayerLit() {
		return this.inLitTile;
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

			//see if we hit a mushroom!
			for(ArtObject o : artObjects) {
				if (((bd1 == player && bd2 == o) || (bd1 == o &&  bd2 == player))){
					if(o.type == ArtObject.ASSET_TYPE.MUSHROOM){
						o.setHit();
					}
				}
			}


			// See if we lost.
			for (Enemy enemy : enemies) {
				if (((bd1 == player && bd2 == enemy) || (bd1 == enemy &&  bd2 == player))
						&& !enemy.getIsLit()){
					enemy.collidedWithPlayer = true;
				}
			}

			//Update touching lights
			for (LightSourceObject light : lights) {
				if((bd1 == player && bd2 == light) || (bd1 == light && bd2 == player)){
					player.setTouchingLight(true);
					light.setTouchingPlayer(true);
				}
			}

			// Check pickup
			// Dumb way to do it
			for (ArtObject pickup : pickups){
				if((bd1 == player && bd2 == pickup) || (bd1 == pickup && bd2 == player)){
					pickup.markRemoved(true);
					if (!pickup.isTaken){
						pickupSound.play((float)(volume*(.75)));
						player.lightCounter++;
						pickup.isTaken = true;
					}
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

	@Override
	public void dispose() {
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		objects = null;
		addQueue = null;
		bounds = null;
		scale  = null;
		world  = null;
		canvas = null;
	}

	private boolean over500 () {
		for (Long l : thrownLights.values()) {
			if (System.currentTimeMillis() - l > 500L)
				return true;
		}
		return false;
	}

	public void resetEnemySound(){
		for(AIController controller : controls){
			controller.resetSound();
		}
	}

//	// gets the vectors position relative to the camera
//	public Vector2 getCameraPos() {
//		return player.getScaledPosition();
//	}
}
