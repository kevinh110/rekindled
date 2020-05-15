/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do 
 * anything until loading is complete. You know those loading screens with the inane tips 
 * that want to be helpful?  That is asynchronous loading.  
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the 
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.controllers.*;
import edu.cornell.gdiac.util.*;

/**
 * Class that provides a loading screen for the state of the game.
 *
 * You still DO NOT need to understand this class for this lab.  We will talk about this
 * class much later in the course.  This class provides a basic template for a loading
 * screen to be used at the start of the game or between levels.  Feel free to adopt
 * this to your needs.
 *
 * You will note that this mode has some textures that are not loaded by the AssetManager.
 * You are never required to load through the AssetManager.  But doing this will block
 * the application.  That is why we try to have as few resources as possible for this
 * loading screen.
 */
public class LoadingMode implements Screen, InputProcessor, ControllerListener {
	// Textures necessary to support the loading screen 
	private static final String BACKGROUND_FILE = "ui/start.png";
	private static final String SETTINGS_FILE = "ui/settings.png";
	private static final String LEVEL_SELECT_FILE = "ui/select.png";

	private static final String LEVEL_HOVER_FILE = "ui/level_hover.png";
	private static final String START_HOVER_FILE = "ui/start_hover.png";
	private static final String SETTINGS_HOVER_FILE = "ui/settings_hover.png";
	private static final String BACK_TO_MAIN_HOVER_FILE = "ui/back_to_main_hover.png";
	private static final String SAVE_CHANGES_HOVER_FILE = "ui/save_changes_hover.png";

	private static final String BACK_TO_MAIN_FILE = "ui/back_to_main.png";

	private static final String ARROW_UNSELECTED_FILE = "ui/arrow_unselected.png";
	private static final String ARROW_SELECTED_FILE = "ui/arrow_selected.png";
	private static final String WASD_SELECTED_FILE = "ui/wasd_selected.png";
	private static final String WASD_UNSELECTED_FILE = "ui/wasd_unselected.png";
	private static final String VOLUME_SELECTED_FILE = "ui/volume_selected.png";
	private static final String VOLUME_UNSELECTED_FILE = "ui/volume_unselected.png";

	private static final String SAVE_CHANGES_FILE = "ui/save_changes.png";

	private static final String PLAY_BTN_FILE = "images/play.png";

	private static final String LEVELS = "ui/levels.png";
	private static final String LEVELS2 = "ui/levels2.png";
	private static final String NEXT_FILE = "ui/next.png";
	private static final String PREV_FILE = "ui/prev.png";

	private static final String HOVER_1_FILE = "hover/1s.png";
	private static final String HOVER_2_FILE = "hover/2s.png";
	private static final String HOVER_3_FILE = "hover/3s.png";
	private static final String HOVER_4_FILE = "hover/4s.png";
	private static final String HOVER_5_FILE = "hover/5s.png";
	private static final String HOVER_6_FILE = "hover/6s.png";
	private static final String HOVER_7_FILE = "hover/7s.png";
	private static final String HOVER_8_FILE = "hover/8s.png";
	private static final String HOVER_9_FILE = "hover/9s.png";
	private static final String HOVER_10_FILE = "hover/10s.png";
	private static final String HOVER_11_FILE = "hover/11s.png";
	private static final String HOVER_12_FILE = "hover/12s.png";
	private static final String HOVER_13_FILE = "hover/13s.png";
	private static final String HOVER_14_FILE = "hover/14s.png";
	private static final String HOVER_15_FILE = "hover/15s.png";
	private static final String HOVER_16_FILE = "hover/16s.png";


	private final ParticleEffect pe;

	/** Background texture for start-up */
	private Texture startBackground;

	private Texture levelHover;
	private Texture startHover;
	private Texture settingsHover;
	private Texture backToMainHover;
	private Texture saveChangesHover;



	/** Play button to display when done */
	private Texture playButton;
	/** Background texture for settings page */
	private Texture settingsBackground;
	/** Background texture for level select page */
	private Texture levelSelectBackground;

	private Texture levelsTexture;
	private Texture levels2Texture;
	private Texture prevArrowTexture;
	private Texture nextArrowTexture;

	private Texture backToMainTexture;

	private Texture wasdSelectedTexture;
	private Texture wasdUnselectedTexture;
	private Texture arrowUnselectedTexture;
	private Texture arrowSelectedTexture;
	private Texture volumeUnselectedTexture;
	private Texture volumeSelectedTexture;
	private Texture saveChangesTexture;

	private Texture hover1;
	private Texture hover2;
	private Texture hover3;
	private Texture hover4;
	private Texture hover5;
	private Texture hover6;
	private Texture hover7;
	private Texture hover8;
	private Texture hover9;
	private Texture hover10;
	private Texture hover11;
	private Texture hover12;
	private Texture hover13;
	private Texture hover14;
	private Texture hover15;
	private Texture hover16;


	/** Texture atlas to support a progress bar */
//	private Texture statusBar;
	
	// statusBar is a "texture atlas." Break it up into parts.
	/** Left cap to the status background (grey region) */
//	private TextureRegion statusBkgLeft;
//	/** Middle portion of the status background (grey region) */
//	private TextureRegion statusBkgMiddle;
//	/** Right cap to the status background (grey region) */
//	private TextureRegion statusBkgRight;
//	/** Left cap to the status forground (colored region) */
//	private TextureRegion statusFrgLeft;
//	/** Middle portion of the status forground (colored region) */
//	private TextureRegion statusFrgMiddle;
//	/** Right cap to the status forground (colored region) */
//	private TextureRegion statusFrgRight;

	/** Default budget for asset loader (do nothing but load 60 fps) */
	private static int DEFAULT_BUDGET = 15;
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;
	/** Ratio of the bar width to the screen */
	private static float BAR_WIDTH_RATIO  = 0.66f;
	/** Ration of the bar height to the screen */
	private static float BAR_HEIGHT_RATIO = 0.25f;	
	/** Height of the progress bar */
	private static int PROGRESS_HEIGHT = 30;
	/** Width of the rounded cap on left or right */
	private static int PROGRESS_CAP    = 15;
	/** Width of the middle portion in texture atlas */
	private static int PROGRESS_MIDDLE = 200;
	/** Amount to scale the play button */
	private static float BUTTON_SCALE  = 0.75f;
	
	/** Start button for XBox controller on Windows */
	private static int WINDOWS_START = 7;
	/** Start button for XBox controller on Mac OS X */
	private static int MAC_OS_X_START = 4;

	/** AssetManager to be loading in the background */
	private AssetManager manager;
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The width of the progress bar */	
	private int width;
	/** The y-coordinate of the center of the progress bar */
	private int centerY;
	/** The x-coordinate of the center of the progress bar */
	private int centerX;
	/** The height of the canvas window (necessary since sprite origin != screen origin) */
	private int heightY;
	/** Scaling factor for when the student changes the resolution. */
	private float scale;
	
	/** Current progress (0 to 1) of the asset manager */
	private float progress;
	/** The current state of the play button */
	private int pressState;
	/** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
	private int   budget;
	/** Support for the X-Box start button in place of play button */
	private int   startButton;
	/** Whether or not this player mode is still active */
	private boolean active;

	private int currentLevel;
	private int currentPage;

	private int hoverLevel; // 1 -16 ; 0 if none

	/** Codes for the different screens */
	public static final int CODE_START = 1;
	public static final int CODE_SETTINGS = 2;
	public static final int CODE_LEVEL_SELECT = 3;

	/** Current mode the screen is on */
	private int mode;
	/** The exit code */
	private int exitCode;

	/** true if user if on arrow; false if on wasd */
	private boolean arrow;

	/** true iff sound is on */
	private boolean soundOff;

	/** Codes for hovering */
	private final int HOVER_START = 4;
	private final int HOVER_LEVELS = 5;
	private final int HOVER_SETTINGS = 6;
	private final int HOVER_BACK_TO_MAIN = 7;
	private final int HOVER_SAVE_CHANGES = 8;
	private int hover;

	public boolean isArrow(){
		return arrow;
	}

	public int getCurrentLevel(){
		return currentLevel;
	}

	public boolean isMuted(){
		return soundOff;
	}

	/**
	 * Returns the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @return the budget in milliseconds
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * Sets the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param millis the budget in milliseconds
	 */
	public void setBudget(int millis) {
		budget = millis;
	}
	
	/**
	 * Returns true if all assets are loaded and the player is ready to go.
	 *
	 * @return true if the player is ready to go
	 */
	public boolean isReady() {
		return pressState == 2;
	}
	
	/**
	 * Creates a LoadingMode with the default budget, size and position.
	 *
	 * @param manager The AssetManager to load in the background
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager) {
		this(canvas, manager,DEFAULT_BUDGET);
	}

	/**
	 * Creates a LoadingMode with the default size and position.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param manager The AssetManager to load in the background
	 * @param millis The loading budget in milliseconds
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager, int millis) {
		this.manager = manager;
		this.canvas  = canvas;
		budget = millis;
		
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());

		// Load the next two images immediately.
		playButton = null;
		startBackground = new Texture(BACKGROUND_FILE);
		settingsBackground = new Texture(SETTINGS_FILE);
		levelSelectBackground = new Texture(LEVEL_SELECT_FILE);
		backToMainTexture = new Texture(BACK_TO_MAIN_FILE);
		wasdSelectedTexture = new Texture(WASD_SELECTED_FILE);
		wasdUnselectedTexture = new Texture(WASD_UNSELECTED_FILE);
		arrowSelectedTexture = new Texture(ARROW_SELECTED_FILE);
		arrowUnselectedTexture = new Texture(ARROW_UNSELECTED_FILE);
		saveChangesTexture = new Texture(SAVE_CHANGES_FILE);
		levelsTexture = new Texture(LEVELS);
		levels2Texture = new Texture(LEVELS2);
		nextArrowTexture = new Texture(NEXT_FILE);
		prevArrowTexture = new Texture(PREV_FILE);
		levelHover = new Texture(LEVEL_HOVER_FILE);
		startHover = new Texture(START_HOVER_FILE);
		settingsHover = new Texture(SETTINGS_HOVER_FILE);
		backToMainHover = new Texture(BACK_TO_MAIN_HOVER_FILE);
		saveChangesHover = new Texture(SAVE_CHANGES_HOVER_FILE);
		volumeSelectedTexture = new Texture(VOLUME_SELECTED_FILE);
		volumeUnselectedTexture = new Texture(VOLUME_UNSELECTED_FILE);

		hover1 = new Texture(HOVER_1_FILE);
		hover2 = new Texture(HOVER_2_FILE);
		hover3 = new Texture(HOVER_3_FILE);
		hover4 = new Texture(HOVER_4_FILE);
		hover5 = new Texture(HOVER_5_FILE);
		hover6 = new Texture(HOVER_6_FILE);
		hover7 = new Texture(HOVER_7_FILE);
		hover8 = new Texture(HOVER_8_FILE);
		hover9 = new Texture(HOVER_9_FILE);
		hover10 = new Texture(HOVER_10_FILE);
		hover11 = new Texture(HOVER_11_FILE);
		hover12 = new Texture(HOVER_12_FILE);
		hover13 = new Texture(HOVER_13_FILE);
		hover14 = new Texture(HOVER_14_FILE);
		hover15 = new Texture(HOVER_15_FILE);
		hover16 = new Texture(HOVER_16_FILE);


//		statusBar  = new Texture(PROGRESS_FILE);
		
		// No progress so far.		
		progress   = 0;
		pressState = 0;
		active = false;
		mode = CODE_START;



		// Break up the status bar texture into regions
//		statusBkgLeft   = new TextureRegion(statusBar,0,0,PROGRESS_CAP,PROGRESS_HEIGHT);
//		statusBkgRight  = new TextureRegion(statusBar,statusBar.getWidth()-PROGRESS_CAP,0,PROGRESS_CAP,PROGRESS_HEIGHT);
//		statusBkgMiddle = new TextureRegion(statusBar,PROGRESS_CAP,0,PROGRESS_MIDDLE,PROGRESS_HEIGHT);
//
//		int offset = statusBar.getHeight()-PROGRESS_HEIGHT;
//		statusFrgLeft   = new TextureRegion(statusBar,0,offset,PROGRESS_CAP,PROGRESS_HEIGHT);
//		statusFrgRight  = new TextureRegion(statusBar,statusBar.getWidth()-PROGRESS_CAP,offset,PROGRESS_CAP,PROGRESS_HEIGHT);
//		statusFrgMiddle = new TextureRegion(statusBar,PROGRESS_CAP,offset,PROGRESS_MIDDLE,PROGRESS_HEIGHT);

		startButton = (System.getProperty("os.name").equals("Mac OS X") ? MAC_OS_X_START : WINDOWS_START);
		Gdx.input.setInputProcessor(this);

		pe = new ParticleEffect();
		pe.load(Gdx.files.internal("particles/mouse.party"), Gdx.files.internal(""));
		for (ParticleEmitter e : pe.getEmitters()) {
			e.setPosition(Gdx.input.getX(), Gdx.input.getY());
		}
		pe.start();
		// Let ANY connected controller start the game.
//		for(Controller controller : Controllers.getControllers()) {
//			controller.addListener(this);
//		}
		active = true;
	}
	
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
//		 statusBkgLeft = null;
//		 statusBkgRight = null;
//		 statusBkgMiddle = null;
//
//		 statusFrgLeft = null;
//		 statusFrgRight = null;
//		 statusFrgMiddle = null;

		 startBackground.dispose();
//		 statusBar.dispose();
		 startBackground = null;
//		 statusBar  = null;
		 if (playButton != null) {
			 playButton.dispose();
			 playButton = null;
		 }
		 pe.dispose();
	}
	
	/**
	 * Update the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	private void update(float delta) {
		if (playButton == null) {
			manager.update(budget);
			this.progress = manager.getProgress();
			if (progress >= 1.0f) {
				this.progress = 1.0f;
				playButton = new Texture(PLAY_BTN_FILE);
				playButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
			Gdx.app.exit();

		pe.update(Gdx.graphics.getDeltaTime());
		pe.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

		if (pe.isComplete())
			pe.reset();
	}

	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 */
	private void draw() {
		canvas.begin();
		if (mode == CODE_SETTINGS){
			canvas.draw(settingsBackground, 0, 0);
			if (hover == HOVER_SAVE_CHANGES){
				canvas.draw(saveChangesHover, (1280 - saveChangesHover.getWidth()) / 2f, 50);
			} else {
				canvas.draw(saveChangesTexture, (1280 - saveChangesTexture.getWidth()) / 2f, 50);
			}
			if (hover == HOVER_BACK_TO_MAIN){
				canvas.draw(backToMainHover, 50, heightY - 75);
			} else {
				canvas.draw(backToMainTexture, 50, heightY - 75);
			}
			if (arrow){
				canvas.draw(wasdUnselectedTexture, 256 , (heightY - wasdUnselectedTexture.getHeight()) / 2f); // Temp code: Add select/unselect logic later
				canvas.draw(arrowSelectedTexture, 768, (heightY - arrowSelectedTexture.getHeight())/ 2f); // Temp code: Add select/unselect logic later
			} else {
				canvas.draw(wasdSelectedTexture, 256 , (heightY - wasdSelectedTexture.getHeight()) / 2f); // Temp code: Add select/unselect logic later
				canvas.draw(arrowUnselectedTexture, 768, (heightY - arrowSelectedTexture.getHeight())/ 2f); // Temp code: Add select/unselect logic later
			}
			if (soundOff){
				canvas.draw(volumeSelectedTexture, 598, 140);
			} else { // Sound off
				canvas.draw(volumeUnselectedTexture, 598, 140);

			}
		} else if (mode == CODE_LEVEL_SELECT){
			canvas.draw(levelSelectBackground, 0, 0);
			if (currentPage == 0){
				canvas.draw(levelsTexture, 125, 50);
				canvas.draw(nextArrowTexture, 1230 - nextArrowTexture.getWidth(), 50);
			} else if (currentPage == 1){
				canvas.draw(levels2Texture, 125, 50);
				canvas.draw(prevArrowTexture, 50, 50);
			}
			if (hover == HOVER_BACK_TO_MAIN){
				canvas.draw(backToMainHover, 50, heightY - 75);
			} else {
				canvas.draw(backToMainTexture, 50, heightY - 75);
			}
			drawLevelHover();
		} else { // Mode start
			canvas.draw(startBackground, 0, 0);
			if (hover == HOVER_LEVELS){
				canvas.draw(levelHover, 835, 69);
			} else if (hover == HOVER_SETTINGS){
				canvas.draw(settingsHover, 1018, 68);
			} else if (hover == HOVER_START){
				canvas.draw(startHover, 656, 67);
			}
		}
		canvas.drawParticle(pe);


//		if (playButton == null) {
//			drawProgress(canvas);
//		} else {
//			Color tint = (pressState == 1 ? Color.GRAY: Color.WHITE);
//			canvas.draw(playButton, tint, playButton.getWidth()/2, playButton.getHeight()/2,
//						centerX, centerY, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale, false);
//		}

		canvas.end();
	}

	private void drawLevelHover(){
		if (hoverLevel == 1){ canvas.draw(hover1, 107, 372); }
		else if (hoverLevel == 2){ canvas.draw(hover2, 334, 484); }
		else if (hoverLevel == 3){ canvas.draw(hover3, 319, 246); }
		else if (hoverLevel == 4){ canvas.draw(hover4, 614, 474); }
		else if (hoverLevel == 5){ canvas.draw(hover5, 508, 147); }
		else if (hoverLevel == 6){ canvas.draw(hover6, 715, 306); }
		else if (hoverLevel == 7){ canvas.draw(hover7, 719, 42); }
		else if (hoverLevel == 8){ canvas.draw(hover8, 987, 120); }

		else if (hoverLevel == 9){ canvas.draw(hover9, 107, 372); }
		else if (hoverLevel == 10){ canvas.draw(hover10, 334, 484); }
		else if (hoverLevel == 11){ canvas.draw(hover11, 319, 246); }
		else if (hoverLevel == 12){ canvas.draw(hover12, 614, 474); }
		else if (hoverLevel == 13){ canvas.draw(hover13, 508, 147); }
		else if (hoverLevel == 14){ canvas.draw(hover14, 715, 306); }
		else if (hoverLevel == 15){ canvas.draw(hover15, 719, 42); }
		else if (hoverLevel == 16){ canvas.draw(hover16, 987, 120); }

	}
	
	/**
	 * Updates the progress bar according to loading progress
	 *
	 * The progress bar is composed of parts: two rounded caps on the end, 
	 * and a rectangle in a middle.  We adjust the size of the rectangle in
	 * the middle to represent the amount of progress.
	 *
	 * @param canvas The drawing context
	 */	
//	private void drawProgress(GameCanvas canvas) {
//		canvas.draw(statusBkgLeft,   Color.WHITE, centerX-width/2, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
//		canvas.draw(statusBkgRight,  Color.WHITE, centerX+width/2-scale*PROGRESS_CAP, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
//		canvas.draw(statusBkgMiddle, Color.WHITE, centerX-width/2+scale*PROGRESS_CAP, centerY, width-2*scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
//
//		canvas.draw(statusFrgLeft,   Color.WHITE, centerX-width/2, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
//		if (progress > 0) {
//			float span = progress*(width-2*scale*PROGRESS_CAP)/2.0f;
//			canvas.draw(statusFrgRight,  Color.WHITE, centerX-width/2+scale*PROGRESS_CAP+span, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
//			canvas.draw(statusFrgMiddle, Color.WHITE, centerX-width/2+scale*PROGRESS_CAP, centerY, span, scale*PROGRESS_HEIGHT);
//		} else {
//			canvas.draw(statusFrgRight,  Color.WHITE, centerX-width/2+scale*PROGRESS_CAP, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
//		}
//	}

	// ADDITIONAL SCREEN METHODS
	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
//		System.out.println("rendering");
		if (active) {
			update(delta);
			draw();

			// We are are ready, notify our listener
			if (isReady() && listener != null) {
				pressState = 0;
				mode = CODE_START;
				listener.exitScreen(this, exitCode);
			}
		}
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
		// Compute the drawing scale
		float sx = ((float)width)/STANDARD_WIDTH;
		float sy = ((float)height)/STANDARD_HEIGHT;
		scale = (sx < sy ? sx : sy);
		
		this.width = (int)(BAR_WIDTH_RATIO*width);
		centerY = (int)(BAR_HEIGHT_RATIO*height);
		centerX = width/2;
		heightY = height;
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
	
	// PROCESSING PLAYER INPUT
	/** 
	 * Called when the screen was touched or a mouse button was pressed.
	 *
	 * This method checks to see if the play button is available and if the click
	 * is in the bounds of the play button.  If so, it signals the that the button
	 * has been pressed and is currently down. Any mouse button is accepted.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (playButton == null || pressState == 2) {
			return true;
		}
		// Flip to match graphics coordinates
		screenY = heightY-screenY;
		switch (mode) {
			case CODE_START:
				handleStartButtons(screenX, screenY);
				break;
			case CODE_SETTINGS:
				handleSettingsButtons(screenX, screenY);
				break;
			case CODE_LEVEL_SELECT:
				handleLevelSelectButtons(screenX, screenY);
				break;
		}
		return false;

	}

	private void handleLevelSelectButtons(int screenX, int screenY){
		if (screenX >= 41 && screenX <= 328 && screenY >= 628 && screenY <= 689) {
			mode = CODE_START;
		} else if (screenX >= 143 && screenX <= 266 && screenY >= 394 && screenY <= 512){
			currentLevel = 0 + currentPage*8;
			pressState = 1;
			exitCode = CODE_LEVEL_SELECT;
		} else if (screenX >= 377 && screenX <= 490 && screenY >= 510 && screenY <= 621){
			currentLevel = 1 + currentPage*8;
			pressState = 1;
			exitCode = CODE_LEVEL_SELECT;
		}else if (screenX >= 364 && screenX <= 472 && screenY >= 278 && screenY <= 380){
			currentLevel = 2 + currentPage*8;
			pressState = 1;
			exitCode = CODE_LEVEL_SELECT;
		}else if (screenX >= 660 && screenX <= 762 && screenY >= 505 && screenY <= 606){
			currentLevel = 3 + currentPage*8;
			pressState = 1;
			exitCode = CODE_LEVEL_SELECT;
		}else if (screenX >= 552 && screenX <= 657 && screenY >= 175 && screenY <= 275){
			currentLevel = 4 + currentPage * 8;
			pressState = 1;
			exitCode = CODE_LEVEL_SELECT;
		}else if (screenX >= 760 && screenX <= 860  && screenY >= 330 && screenY <= 440){
			currentLevel = 5 + currentPage*8;
			pressState = 1;
			exitCode = CODE_LEVEL_SELECT;
		}else if (screenX >= 763 && screenX <= 870 && screenY >= 68 && screenY <= 175){
			currentLevel = 6 + currentPage*8;
			pressState = 1;
			exitCode = CODE_LEVEL_SELECT;
		}else if (screenX >= 1030 && screenX <= 1135 && screenY >= 152 && screenY <= 254){
			currentLevel = 7 + currentPage * 8;
			pressState = 1;
			exitCode = CODE_LEVEL_SELECT;
		} else if (screenX >= 1190 && screenX <= 1245 && screenY >= 39 && screenY <= 127){
			currentPage = 1;
		} else if (screenX >= 43 && screenX <= 105 && screenY >= 39 && screenY <= 127){
			currentPage = 0;
		}
	}

	private void handleSettingsButtons(int screenX, int screenY) {
		if (screenX >= 41 && screenX <= 328 && screenY >= 628 && screenY <= 689) {
			mode = CODE_START;
		} else if (screenX >= 264 && screenX <= 525 && screenY >= 270 && screenY <= 437) {
			arrow = false;
		} else if (screenX >= 767 && screenX <= 1040 && screenY >= 270 && screenY <= 437) {
			arrow = true;
		} else if (screenX >= 562 && screenX <= 722 && screenY >= 43 && screenY <= 88) {
			mode = CODE_START;
		} else if (screenX >= 587 && screenX <= 694 && screenY >= 128 && screenY <= 232){
			soundOff = !soundOff;
		}
	}


	private void handleStartButtons(int screenX, int screenY){
		if (screenY >= 56 && screenY <= 135){
			if (screenX >= 704 && screenX <= 810){ // Start
				pressState = 1;
				exitCode = 0;
			} else if (screenX >= 872 && screenX <= 1003){
				mode = CODE_LEVEL_SELECT;
			} else if (screenX >= 1058 && screenX <= 1237){
				mode = CODE_SETTINGS;
			}
		}
	}
	
	/** 
	 * Called when a finger was lifted or a mouse button was released.
	 *
	 * This method checks to see if the play button is currently pressed down. If so, 
	 * it signals the that the player is ready to go.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { 
		if (pressState == 1) {
			pressState = 2;
			return false;
		}
		return true;
	}
	
	/** 
	 * Called when a button on the Controller was pressed. 
	 *
	 * The buttonCode is controller specific. This listener only supports the start
	 * button on an X-Box controller.  This outcome of this method is identical to 
	 * pressing (but not releasing) the play button.
	 *
	 * @param controller The game controller
	 * @param buttonCode The button pressed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean buttonDown (Controller controller, int buttonCode) {
		if (buttonCode == startButton && pressState == 0) {
			pressState = 1;
			return false;
		}
		return true;
	}
	
	/** 
	 * Called when a button on the Controller was released. 
	 *
	 * The buttonCode is controller specific. This listener only supports the start
	 * button on an X-Box controller.  This outcome of this method is identical to 
	 * releasing the the play button after pressing it.
	 *
	 * @param controller The game controller
	 * @param buttonCode The button pressed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean buttonUp (Controller controller, int buttonCode) {
		if (pressState == 1 && buttonCode == startButton) {
			pressState = 2;
			return false;
		}
		return true;
	}
	
	// UNSUPPORTED METHODS FROM InputProcessor

	/** 
	 * Called when a key is pressed (UNSUPPORTED)
	 *
	 * @param keycode the key pressed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean keyDown(int keycode) { 
		return true; 
	}

	/** 
	 * Called when a key is typed (UNSUPPORTED)
	 *
	 * @param character the key typed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean keyTyped(char character) { 
		return true; 
	}

	/** 
	 * Called when a key is released.
	 * 
	 * We allow key commands to start the game this time.
	 *
	 * @param keycode the key released
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean keyUp(int keycode) { 
		if (keycode == Input.Keys.N) {
			pressState = 2;
			return false;			
		}
		return true; 
	}
	
	/** 
	 * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean mouseMoved(int screenX, int screenY) {
		screenY = heightY-screenY;
		switch (mode) {
			case CODE_START:
				handleStartHover(screenX, screenY);
				break;
			case CODE_SETTINGS:
				handleSettingsHover(screenX, screenY);
				break;
			case CODE_LEVEL_SELECT:
				handleLevelSelectHover(screenX, screenY);
				break;
		}
		return false;
	}

	private void handleLevelSelectHover(int screenX, int screenY){
		if (screenX >= 41 && screenX <= 328 && screenY >= 628 && screenY <= 689) {
			hover = HOVER_BACK_TO_MAIN;
			hoverLevel = 0;
		} else if (screenX >= 143 && screenX <= 266 && screenY >= 394 && screenY <= 512) {// Level 0
			hoverLevel = 1 + 8 * currentPage;
			hover = 0;
		} else if (screenX >= 377 && screenX <= 490 && screenY >= 510 && screenY <= 621){ // level 1
			hoverLevel = 2 + 8 * currentPage;
			hover = 0;
		}else if (screenX >= 364 && screenX <= 472 && screenY >= 278 && screenY <= 380){ // level 2
			hoverLevel = 3 + 8 * currentPage;
			hover = 0;
		}else if (screenX >= 660 && screenX <= 762 && screenY >= 505 && screenY <= 606){ // level 3
			hoverLevel = 4 + 8 * currentPage;
			hover = 0;
		}else if (screenX >= 552 && screenX <= 657 && screenY >= 175 && screenY <= 275){ // level 4
			hoverLevel = 5 + 8 * currentPage;
			hover = 0;
		}else if (screenX >= 760 && screenX <= 860  && screenY >= 330 && screenY <= 440){ // level 5
			hoverLevel = 6 + 8 * currentPage;
			hover = 0;
		}else if (screenX >= 763 && screenX <= 870 && screenY >= 68 && screenY <= 175){ // level 6
			hoverLevel = 7 + 8 * currentPage;
			hover = 0;
		}else if (screenX >= 1030 && screenX <= 1135 && screenY >= 152 && screenY <= 254){ // level 7
			hoverLevel = 8 + 8 * currentPage;
			hover = 0;
		}else {
			hover = 0;
			hoverLevel = 0;
		}
	}

	private void handleSettingsHover(int screenX, int screenY){
		if (screenX >= 41 && screenX <= 328 && screenY >= 628 && screenY <= 689) {
			hover = HOVER_BACK_TO_MAIN;
		} else if (screenX >= 562 && screenX <= 722 && screenY >= 43 && screenY <= 88) {
			hover = HOVER_SAVE_CHANGES;
		} else {
			hover = 0;
		}
	}

	private void handleStartHover(int screenX, int screenY){
		if (screenY >= 56 && screenY <= 135){
			if (screenX >= 704 && screenX <= 810){ // Start
				hover = HOVER_START;
			} else if (screenX >= 872 && screenX <= 1003){
				hover = HOVER_LEVELS;
			} else if (screenX >= 1058 && screenX <= 1237){
				hover = HOVER_SETTINGS;
			} else {
				hover = 0;
			}
			return;
		}
		hover = 0;
	}

	/** 
	 * Called when the mouse wheel was scrolled. (UNSUPPORTED)
	 *
	 * @param amount the amount of scroll from the wheel
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean scrolled(int amount) { 
		return true; 
	}

	/** 
	 * Called when the mouse or finger was dragged. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners. 
	 */		
	public boolean touchDragged(int screenX, int screenY, int pointer) { 
		return true; 
	}
	
	// UNSUPPORTED METHODS FROM ControllerListener
	
	/**
	 * Called when a controller is connected. (UNSUPPORTED)
	 *
	 * @param controller The game controller
	 */
	public void connected (Controller controller) {}

	/**
	 * Called when a controller is disconnected. (UNSUPPORTED)
	 *
	 * @param controller The game controller
	 */
	public void disconnected (Controller controller) {}

	/** 
	 * Called when an axis on the Controller moved. (UNSUPPORTED) 
	 *
	 * The axisCode is controller specific. The axis value is in the range [-1, 1]. 
	 *
	 * @param controller The game controller
	 * @param axisCode 	The axis moved
	 * @param value 	The axis value, -1 to 1
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean axisMoved (Controller controller, int axisCode, float value) {
		return true;
	}

	/** 
	 * Called when a POV on the Controller moved. (UNSUPPORTED) 
	 *
	 * The povCode is controller specific. The value is a cardinal direction. 
	 *
	 * @param controller The game controller
	 * @param povCode 	The POV controller moved
	 * @param value 	The direction of the POV
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean povMoved (Controller controller, int povCode, PovDirection value) {
		return true;
	}

	/** 
	 * Called when an x-slider on the Controller moved. (UNSUPPORTED) 
	 *
	 * The x-slider is controller specific. 
	 *
	 * @param controller The game controller
	 * @param sliderCode The slider controller moved
	 * @param value 	 The direction of the slider
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean xSliderMoved (Controller controller, int sliderCode, boolean value) {
		return true;
	}

	/** 
	 * Called when a y-slider on the Controller moved. (UNSUPPORTED) 
	 *
	 * The y-slider is controller specific. 
	 *
	 * @param controller The game controller
	 * @param sliderCode The slider controller moved
	 * @param value 	 The direction of the slider
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean ySliderMoved (Controller controller, int sliderCode, boolean value) {
		return true;
	}

	/** 
	 * Called when an accelerometer value on the Controller changed. (UNSUPPORTED) 
	 * 
	 * The accelerometerCode is controller specific. The value is a Vector3 representing 
	 * the acceleration on a 3-axis accelerometer in m/s^2.
	 *
	 * @param controller The game controller
	 * @param accelerometerCode The accelerometer adjusted
	 * @param value A vector with the 3-axis acceleration
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return true;
	}

}