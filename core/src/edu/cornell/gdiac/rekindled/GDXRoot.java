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
	/** List of TutorialModes*/
	private TutorialMode[] tutorials;

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

		tutorials = new TutorialMode[4];
		tutorials[0] = new TutorialMode(canvas, manager,1,1);
		tutorials[1] = new TutorialMode(canvas, manager,1,2);
		tutorials[2] = new TutorialMode(canvas, manager,1,3);
		tutorials[3] = new TutorialMode(canvas, manager,1,4);


		// Initialize all the game worlds
		controllers = new GameplayController[15];
		controllers[0] = new GameplayController("jsons/tutorial/level0.json");
		controllers[1] = new GameplayController("jsons/tutorial/level1.json");
		controllers[2] = new GameplayController("jsons/tutorial/level2.json");
		controllers[3] = new GameplayController("jsons/tutorial/level3.json");
		controllers[4] = new GameplayController("jsons/throwlight.json");
		controllers[5] = new GameplayController("jsons/level10.json");
		controllers[6] = new GameplayController("jsons/Megan_0.json");
		controllers[7] = new GameplayController("jsons/emeka3.json");

		//controllers[8] = new GameplayController("jsons/level20.json"); //stupid
		controllers[8] = new GameplayController("jsons/intermediate2.json");
		controllers[9] = new GameplayController("jsons/intermediate.json");
		controllers[10] = new GameplayController("jsons/spineasy.json");
		controllers[11] = new GameplayController("jsons/hallwayandroom.json");
		controllers[12] = new GameplayController("jsons/roundabout.json");
		controllers[13] = new GameplayController("jsons/level11_Emeka.json");
		controllers[14] = new GameplayController("jsons/testlevel.json");
//		controllers[15] = new GameplayController("jsons/levelhard2.json");



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
				if(current<4){
					System.out.println("Exited loading mode. Entering tutorial.");
					tutorials[current].setScreenListener(this);
					Gdx.graphics.setCursor(cursor);
					Gdx.input.setInputProcessor(tutorials[current]);
					setScreen(tutorials[current]);
				} else {
					setScreen(controllers[current]);
				}
			} else {
				setScreen(trailer);
			}

//			loading.dispose();
//			loading = null;
			Gdx.graphics.setCursor(transparentCursor);
			Gdx.input.setInputProcessor(null);

		}
		else if(screen == trailer){
			if(current<4){
				System.out.println("Exited trailer. Entering tutorial.");
				tutorials[current].setScreenListener(this);
				Gdx.graphics.setCursor(cursor);
				Gdx.input.setInputProcessor(tutorials[current]);
				setScreen(tutorials[current]);
			} else {
				setScreen(controllers[current]);
			}
			trailer.dispose();
		}
		else if(screen == tutorials[0]){
			setScreen(controllers[0]);
		} else if(screen == tutorials[1]){
			setScreen(controllers[1]);
		} else if(screen == tutorials[2]){
			setScreen(controllers[2]);
		} else if(screen == tutorials[3]){
			setScreen(controllers[3]);
		}
		else if (screen == levelComplete){
			if (exitCode == LevelCompleteMode.EXIT_NEXT){
				Gdx.graphics.setCursor(transparentCursor);
				Gdx.input.setInputProcessor(null);
				current = (current+1) % controllers.length;
				controllers[current].reset();
				if(current<4){
					System.out.println("Level Complete. Next Level. Entering tutorial.");
					tutorials[current].setScreenListener(this);
					Gdx.graphics.setCursor(cursor);
					Gdx.input.setInputProcessor(tutorials[current]);
					setScreen(tutorials[current]);
				} else {
					setScreen(controllers[current]);
				}
			}
			else if (exitCode == LevelCompleteMode.EXIT_REPLAY){
				Gdx.graphics.setCursor(transparentCursor);
				Gdx.input.setInputProcessor(null);
				controllers[current].reset();
				if(current<4){
					System.out.println("Level Complete. Replay Level. Entering tutorial.");
					tutorials[current].setScreenListener(this);
					Gdx.graphics.setCursor(cursor);
					Gdx.input.setInputProcessor(tutorials[current]);
					setScreen(tutorials[current]);
				} else {
					setScreen(controllers[current]);
				}
			}
			else if (exitCode == LevelCompleteMode.EXIT_QUIT){
				if (levelComplete.mode == LevelCompleteMode.MODE_COMPLETE){
					current = (current+1) % controllers.length;
				}
				setScreen(loading);
				Gdx.input.setInputProcessor(loading);
			}
			else if (exitCode == LevelCompleteMode.EXIT_CONTINUE){
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
			current = (current+1) % controllers.length;
			controllers[current].reset();
			if(current<4){
				System.out.println("Next Level. Entering tutorial.");
				tutorials[current].setScreenListener(this);
				Gdx.graphics.setCursor(cursor);
				Gdx.input.setInputProcessor(tutorials[current]);
				setScreen(tutorials[current]);
			} else {
				setScreen(controllers[current]);
			}
		} else if (exitCode == WorldController.EXIT_PREV) {
			current = (current+controllers.length-1) % controllers.length;
			controllers[current].reset();
			if(current<4){
				System.out.println("Previous Level. Entering tutorial.");
				tutorials[current].setScreenListener(this);
				Gdx.graphics.setCursor(cursor);
				Gdx.input.setInputProcessor(tutorials[current]);
				setScreen(tutorials[current]);
			} else {
				setScreen(controllers[current]);
			}
		} else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}
