/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter.
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;

import edu.cornell.gdiac.rekindled.obstacle.WheelObstacle;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.rekindled.*;

import java.util.logging.Level;

/**
 * Root class for a LibGDX.
 *
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However,
 * those classes are unique to each platform, while this class is the same across all
 * plaforms. In addition, this functions as the root class all intents and purposes,
 * and you would draw it as a root class in an architecture specification.
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager manager;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Mode for playing the trailer*/
	private TrailerMode trailer;
	/** Player mode for level complete screen */
	private LevelCompleteMode levelComplete;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;
	/** List of all WorldControllers */
	private GameplayController[] controllers;

	private Music music;

	Cursor cursor;
	Cursor transparentCursor;


	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() {
		// Start loading with the asset manager
		manager = new AssetManager();

		// Add font support to the asset manager
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

	}

	/**
	 * Called when the Application is first created.
	 *
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas = new GameCanvas();
		loading = new LoadingMode(canvas, manager,1);
		trailer = new TrailerMode(canvas, manager);
		trailer.setScreenListener(this);
		levelComplete = new LevelCompleteMode(canvas, manager, 1);
		levelComplete.setScreenListener(this);


		// Initialize all the game worlds
		controllers = new GameplayController[24];
		controllers[0] = new GameplayController("jsons/tutorial/level0.json",0);
		controllers[1] = new GameplayController("jsons/tutorial/level1.json",1);
		controllers[2] = new GameplayController("jsons/tutorial/level2.json",2);
		controllers[3] = new GameplayController("jsons/tutorial/level3.json",3);
		controllers[4] = new GameplayController("jsons/throwlight.json",4);
		controllers[5] = new GameplayController("jsons/level10.json",5);
		controllers[6] = new GameplayController("jsons/Megan_0.json",6);
		controllers[7] = new GameplayController("jsons/emeka3.json",7);
		controllers[8] = new GameplayController("jsons/intermediate2.json",8);
		controllers[9] = new GameplayController("jsons/intermediate.json",9);
		controllers[10] = new GameplayController("jsons/spineasy.json",10);
		controllers[11] = new GameplayController("jsons/hallwayandroom.json",11);
		controllers[12] = new GameplayController("jsons/spinstun.json",12);
		controllers[13] = new GameplayController("jsons/roundabout.json",13);
		controllers[14] = new GameplayController("jsons/level11_Emeka.json",14);
		controllers[15] = new GameplayController("jsons/simultrap.json",15);
		controllers[16] = new GameplayController("jsons/blockedpaths.json",16);
		controllers[17] = new GameplayController("jsons/experiment.json",17); // holes
		controllers[18] = new GameplayController("jsons/longlure.json",18);
		controllers[19] = new GameplayController("jsons/timedstun.json",19);
		controllers[20] = new GameplayController("jsons/keys.json" , 20);
		controllers[21] = new GameplayController("jsons/testlevel.json",21); // circle thing
		controllers[22] = new GameplayController("jsons/levelhard2.json",22); // calvin
		controllers[23] = new GameplayController("jsons/last.json",23);



		for(int ii = 0; ii < controllers.length; ii++) {
			controllers[ii].preLoadContent(manager);
		}
		current = 0;
		loading.setScreenListener(this);
		setScreen(loading);

		Pixmap pixmap = new Pixmap(Gdx.files.internal("ui/cursor.png"));
		int xHotspot = pixmap.getWidth() / 2;
		int yHotspot = pixmap.getHeight() / 2;
		Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
		this.cursor = cursor;
		pixmap = new Pixmap(Gdx.files.internal("ui/transparent.png"));
		this.transparentCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
		pixmap.dispose();
		Gdx.graphics.setCursor(this.cursor);
	}

	/**
	 * Called when the Application is destroyed.
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		for(int ii = 0; ii < controllers.length; ii++) {
			controllers[ii].unloadContent(manager);
			controllers[ii].dispose();
		}

		canvas.dispose();
		canvas = null;

		// Unload all of the resources
		manager.clear();
		manager.dispose();
		super.dispose();
	}

	/**
	 * Called when the Application is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (screen == loading) {
			if (exitCode == LoadingMode.CODE_LEVEL_SELECT){
				current = loading.getCurrentLevel();
			}
			for(int ii = 0; ii < controllers.length; ii++) {
				controllers[ii].loadContent(manager);
				controllers[ii].setScreenListener(this);
				controllers[ii].setCanvas(canvas);
			}
			InputController input = InputController.getInstance();
			if (loading.isArrow()){
				input.setArrow();
			} else {
				input.setWASD();
			}
			controllers[current].reset();
			if (loading.isMuted()){
				controllers[current].mute();
			} else {
				controllers[current].unmute();
			}
			if(current != 0){
				setScreen(controllers[current]);
				Gdx.graphics.setCursor(transparentCursor);
				Gdx.input.setInputProcessor(null);
			} else {
				setScreen(trailer);
				Gdx.graphics.setCursor(transparentCursor);
				Gdx.input.setInputProcessor(null);
			}

		}
		else if(screen == trailer){
			setScreen(controllers[current]);
			trailer.dispose();
		}
		else if (screen == levelComplete){
			if (exitCode == LevelCompleteMode.EXIT_NEXT){
				Gdx.graphics.setCursor(transparentCursor);
				Gdx.input.setInputProcessor(null);
				current = (current+1) % controllers.length;
				controllers[current].reset();
				setScreen(controllers[current]);
			}
			else if (exitCode == LevelCompleteMode.EXIT_REPLAY){
				Gdx.graphics.setCursor(transparentCursor);
				Gdx.input.setInputProcessor(null);
				controllers[current].reset();
				setScreen(controllers[current]);
			}
			else if (exitCode == LevelCompleteMode.EXIT_QUIT){
				controllers[current].reset();
				if (levelComplete.mode == LevelCompleteMode.MODE_COMPLETE){
					current = (current+1) % controllers.length;
				}
				setScreen(loading);
				Gdx.input.setInputProcessor(loading);
			}
			else if (exitCode == LevelCompleteMode.EXIT_CONTINUE){
				controllers[current].unmuteEnemySound();
				Gdx.graphics.setCursor(transparentCursor);
				Gdx.input.setInputProcessor(null);
				setScreen(controllers[current]);
			}
		}

		else if (exitCode == WorldController.EXIT_COMPLETE){
			levelComplete.setModeComplete();
			setScreen(levelComplete);
			Gdx.graphics.setCursor(cursor);
			Gdx.input.setInputProcessor(levelComplete);
		} else if (exitCode == WorldController.EXIT_PAUSED){
			controllers[current].muteEnemySound();
			levelComplete.setModePaused();
			setScreen(levelComplete);
			Gdx.graphics.setCursor(cursor);
			Gdx.input.setInputProcessor(levelComplete);
		} else if (exitCode == WorldController.EXIT_LOST){
			levelComplete.setModeLost();
			setScreen(levelComplete);
			Gdx.graphics.setCursor(cursor);
			Gdx.input.setInputProcessor(levelComplete);
		}
		else if (exitCode == WorldController.EXIT_NEXT) {
			controllers[current].resetEnemySound();
			current = (current+1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_PREV) {
			controllers[current].resetEnemySound();
			current = (current+controllers.length-1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}