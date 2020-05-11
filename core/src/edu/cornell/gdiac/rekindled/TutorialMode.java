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
import com.badlogic.gdx.assets.*;

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
public class TutorialMode implements Screen, InputProcessor, ControllerListener {

    /** file location for tutorials */
//    private static final String TUTORIAL_FIREFLY_FILE = "tutorial/firefly.png";
//    private static final String TUTORIAL_NEXT_FILE = "tutorial/next.png";
//    private static final String TUTORIAL_PLAY_FILE = "tutorial/play.png";
//
//    private static final String TUTORIAL_111 = "tutorial/lv1/1.1.1.png";
//    private static final String TUTORIAL_112 = "tutorial/lv1/1.1.2.png";
//    private static final String TUTORIAL_121 = "tutorial/lv1/1.2.1.png";
//    private static final String TUTORIAL_122 = "tutorial/lv1/1.2.2.png";
//    private static final String TUTORIAL_131 = "tutorial/lv1/1.3.1.png";
//    private static final String TUTORIAL_132 = "tutorial/lv1/1.3.2.png";
//    private static final String TUTORIAL_141 = "tutorial/lv1/1.4.1.png";
//    private static final String TUTORIAL_151 = "tutorial/lv1/1.5.1.png";
//    private static final String TUTORIAL_211 = "tutorial/lv2/2.1.1.png";
//    private static final String TUTORIAL_221 = "tutorial/lv2/2.2.1.png";
//    private static final String TUTORIAL_311 = "tutorial/lv3/3.1.1.png";
//    private static final String TUTORIAL_411 = "tutorial/lv4/4.1.1.png";
//    private static final String TUTORIAL_412 = "tutorial/lv4/4.1.2.png";
//
//    private static final String BACKGROUND_1 = "tutorial/tutorial_backgrounds/tutorial_backgrounds.001.png";
//    private static final String BACKGROUND_2 = "tutorial/tutorial_backgrounds/tutorial_backgrounds.002.png";
//    private static final String BACKGROUND_3 = "tutorial/tutorial_backgrounds/tutorial_backgrounds.003.png";
//    private static final String BACKGROUND_4 = "tutorial/tutorial_backgrounds/tutorial_backgrounds.004.png";

    private static final String LV1_1 = "tutorial/level1_slides/level1_slides.001.png";
    private static final String LV1_2 = "tutorial/level1_slides/level1_slides.002.png";
    private static final String LV1_3 = "tutorial/level1_slides/level1_slides.003.png";
    private static final String LV1_4 = "tutorial/level1_slides/level1_slides.004.png";
    private static final String LV1_5 = "tutorial/level1_slides/level1_slides.005.png";

    private static final String LV2_1 = "tutorial/level2_slides/level2_slides.001.png";
    private static final String LV2_2 = "tutorial/level2_slides/level2_slides.002.png";

    private static final String LV3_1 = "tutorial/level3_slides/level3_slides.001.png";

    private static final String LV4_1 = "tutorial/level4_slides/level4_slides.001.png";
    private static final String LV4_2 = "tutorial/level4_slides/level4_slides.002.png";




    private static final String PLAY_BTN_FILE = "images/play.png";


    /**
     * Texture for Tutorial Text
     */
//    private Texture tutorialFirefly;
//    private Texture tutorialNext;
//    private Texture tutorialPlay;
//
//    private Texture tutorial111;
//    private Texture tutorial112;
//    private Texture tutorial121;
//    private Texture tutorial122;
//    private Texture tutorial131;
//    private Texture tutorial141;
//    private Texture tutorial151;
//    private Texture tutorial211;
//    private Texture tutorial221;
//    private Texture tutorial311;
//    private Texture tutorial411;
//    private Texture tutorial412;
//
//    private Texture backgroundLv1;
//    private Texture backgroundLv2;
//    private Texture backgroundLv3;
//    private Texture backgroundLv4;

    private Texture lv1Slides1;
    private Texture lv1Slides2;
    private Texture lv1Slides3;
    private Texture lv1Slides4;
    private Texture lv1Slides5;

    private Texture lv2Slides1;
    private Texture lv2Slides2;

    private Texture lv3Slides1;

    private Texture lv4Slides1;
    private Texture lv4Slides2;

    /** Play button to display when done */
    private Texture playButton;


    private int LEVEL_ID;

    /** Current mode the screen is on */
    private int mode;
    private int defaultMode;

    private static final int EXIT_MODE = 0;

    private static final int LV1_MODE1 = 1;
    private static final int LV1_MODE2 = 2;
    private static final int LV1_MODE3 = 3;
    private static final int LV1_MODE4 = 4;
    private static final int LV1_MODE5 = 5;

    private static final int LV2_MODE1 = 6;
    private static final int LV2_MODE2 = 7;

    private static final int LV3_MODE1 = 8;

    private static final int LV4_MODE1 = 9;
    private static final int LV4_MODE2 = 10;


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

//    private int currentLevel;
    private int currentPage;

    /** true iff sound is on */
    private boolean soundOff;

//    public int getCurrentLevel(){
//        return currentLevel;
//    }

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
    public TutorialMode(GameCanvas canvas, AssetManager manager) {
        this(canvas, manager,DEFAULT_BUDGET,1);
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
    public TutorialMode(GameCanvas canvas, AssetManager manager, int millis, int id) {
        this.canvas = canvas;
        this.manager = manager;
        budget = millis;
        LEVEL_ID = id;

        // Compute the dimensions from the canvas
        resize(canvas.getWidth(),canvas.getHeight());

        // Load the next two images immediately.
        playButton = null;
        System.out.println("load tutorial assets");
//        backgroundLv1 = new Texture(BACKGROUND_1);
//        backgroundLv2 = new Texture(BACKGROUND_2);
//        backgroundLv3 = new Texture(BACKGROUND_3);
//        backgroundLv4 = new Texture(BACKGROUND_4);

        lv1Slides1 = new Texture(LV1_1);
        lv1Slides2 = new Texture(LV1_2);
        lv1Slides3 = new Texture(LV1_3);
        lv1Slides4 = new Texture(LV1_4);
        lv1Slides5 = new Texture(LV1_5);

        lv2Slides1 = new Texture(LV2_1);
        lv2Slides2 = new Texture(LV2_2);

        lv3Slides1 = new Texture(LV3_1);

        lv4Slides1 = new Texture(LV4_1);
        lv4Slides2 = new Texture(LV4_2);

//        tutorialFirefly = new Texture(TUTORIAL_FIREFLY_FILE);
//        tutorialNext = new Texture(TUTORIAL_NEXT_FILE);
//        tutorialPlay = new Texture(TUTORIAL_PLAY_FILE);
//
//        tutorial111 = new Texture(TUTORIAL_111);
//        tutorial112 = new Texture(TUTORIAL_112);
//        tutorial121 = new Texture(TUTORIAL_121);
//        tutorial122 = new Texture(TUTORIAL_122);
//        tutorial131 = new Texture(TUTORIAL_131);
//        tutorial141 = new Texture(TUTORIAL_141);
//        tutorial151 = new Texture(TUTORIAL_151);
//
//        tutorial211 = new Texture(TUTORIAL_211);
//        tutorial221 = new Texture(TUTORIAL_221);
//
//        tutorial311 = new Texture(TUTORIAL_311);
//
//        tutorial411 = new Texture(TUTORIAL_411);
//        tutorial412 = new Texture(TUTORIAL_412);


        // No progress so far.
        progress   = 0;
        pressState = 0;
        active = false;
        if(LEVEL_ID == 1){
            defaultMode = LV1_MODE1;
        } else if(LEVEL_ID == 2){
            defaultMode = LV2_MODE1;
        } else if(LEVEL_ID == 3){
            defaultMode = LV3_MODE1;
        } else if(LEVEL_ID == 4){
            defaultMode = LV4_MODE1;
        } else {
            defaultMode = EXIT_MODE;
        }
        mode = defaultMode;


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
//        Gdx.input.setInputProcessor(this);

//        pe = new ParticleEffect();
//        pe.load(Gdx.files.internal("particles/mouse.party"), Gdx.files.internal(""));
//        for (ParticleEmitter e : pe.getEmitters()) {
//            e.setPosition(Gdx.input.getX(), Gdx.input.getY());
//        }
//        pe.start();
        // Let ANY connected controller start the game.
        for(Controller controller : Controllers.getControllers()) {
            controller.addListener(this);
        }
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

//        lv1Slides1.dispose();
//        lv1Slides2.dispose();
//        lv1Slides3.dispose();
//        lv1Slides4.dispose();
//        lv1Slides5.dispose();
//		 statusBar.dispose();
//        lv1Slides1 = null;
//        lv1Slides2 = null;
//        lv1Slides3 = null;
//        lv1Slides4 = null;
//        lv1Slides5 = null;
//		 statusBar  = null;
        if (playButton != null) {
            playButton.dispose();
            playButton = null;
        }
//        pe.dispose();
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

//        pe.update(Gdx.graphics.getDeltaTime());
//        pe.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
//
//        if (pe.isComplete())
//            pe.reset();
    }

    /**
     * Draw the status of this player mode.
     *
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     */
    private void draw() {
//        System.out.println("TutorialMode draw()");
        canvas.begin();
        if (LEVEL_ID == 1){
//            System.out.println("drawing lv 1");
            if(mode == LV1_MODE1){
//                System.out.println("drawing lv 1 slide 1");
                canvas.draw(lv1Slides1, 0,0);
            } else if(mode == LV1_MODE2){
//                System.out.println("drawing lv 1 slide 2");
                canvas.draw(lv1Slides2, 0,0);
            } else if(mode == LV1_MODE3){
//                System.out.println("drawing lv 1 slide 3");
                canvas.draw(lv1Slides3, 0,0);
            } else if(mode == LV1_MODE4){
//                System.out.println("drawing lv 1 slide 4");
                canvas.draw(lv1Slides4, 0,0);
            } else if(mode == LV1_MODE5){
//                System.out.println("drawing lv 1 slide 4");
                canvas.draw(lv1Slides5, 0,0);
            }
//            canvas.draw(backgroundLv1, 0, 0);
        } else if (LEVEL_ID == 2){
            if(mode == LV2_MODE1){
//                System.out.println("drawing lv 1 slide 1");
                canvas.draw(lv2Slides1, 0,0);
            } else if(mode == LV2_MODE2){
//                System.out.println("drawing lv 1 slide 2");
                canvas.draw(lv2Slides2, 0,0);
            }
        } else if (LEVEL_ID == 3){
            if(mode == LV3_MODE1){
//                System.out.println("drawing lv 1 slide 1");
                canvas.draw(lv3Slides1, 0,0);
            }
        } else if (LEVEL_ID == 4){
            if(mode == LV4_MODE1){
//                System.out.println("drawing lv 1 slide 1");
                canvas.draw(lv4Slides1, 0,0);
            } else if(mode == LV4_MODE2){
//                System.out.println("drawing lv 1 slide 2");
                canvas.draw(lv4Slides2, 0,0);
            }
        }
//        canvas.drawParticle(pe);


//		if (playButton == null) {
//			drawProgress(canvas);
//		} else {
//			Color tint = (pressState == 1 ? Color.GRAY: Color.WHITE);
//			canvas.draw(playButton, tint, playButton.getWidth()/2, playButton.getHeight()/2,
//						centerX, centerY, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale, false);
//		}

        canvas.end();
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
//                mode = CODE_START;
                if(mode == EXIT_MODE) {
                    System.out.println("ready to exit");
                    mode = defaultMode;
                    listener.exitScreen(this, 0);
                }
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
            case LV1_MODE1:
                if (screenY >= 75 && screenY <= 150){
                    if (screenX >= 250 && screenX <= 400){ // Next
                        pressState = 1;
                        mode = LV1_MODE2;
                        System.out.println("switch to lv1 mode2");
                    }
                }
                break;
            case LV1_MODE2:
                if (screenY >= 75 && screenY <= 150){
                    if (screenX >= 250 && screenX <= 400){ // Next
                        pressState = 1;
                        mode = LV1_MODE3;
                        System.out.println("switch to lv1 mode3");
                    }
                }
                break;
            case LV1_MODE3:
                if (screenY >= 75 && screenY <= 150){
                    if (screenX >= 250 && screenX <= 400){ // Next
                        pressState = 1;
                        mode = LV1_MODE4;
                        System.out.println("switch to lv1 mode4");
                    }
                }
                break;
            case LV1_MODE4:
                if (screenY >= 75 && screenY <= 150){
                    if (screenX >= 250 && screenX <= 400){ // Next
                        pressState = 1;
                        mode = LV1_MODE5;
                        System.out.println("switch to lv1 mode5");
                    }
                }
                break;
            case LV1_MODE5:
                if (screenY >= 75 && screenY <= 150){
                    if (screenX >= 255 && screenX <= 405){ // Next
                        pressState = 1;
                        mode = EXIT_MODE;
//                        readyToExit = true;
                        System.out.println("lv1 mode5. switch to exit mode");
                    }
                }
            case LV2_MODE1:
                if (screenY >= 75 && screenY <= 150){
                    if (screenX >= 250 && screenX <= 400){ // Next
                        pressState = 1;
                        mode = LV2_MODE2;
                        System.out.println("switch to lv2 mode2");
                    }
                }
                break;
            case LV2_MODE2:
                if (screenY >= 75 && screenY <= 150){
                    if (screenX >= 255 && screenX <= 405){ // Next
                        pressState = 1;
                        mode = EXIT_MODE;
                        System.out.println("lv2 mode2. switch to exit mode");
                    }
                }
                break;
            case LV3_MODE1:
                if (screenY >= 545 && screenY <= 620){
                    if (screenX >= 960 && screenX <= 1110){ // Next
                        pressState = 1;
                        mode = EXIT_MODE;
                        System.out.println("lv3 mode1. switch to exit mode");
                    }
                }
                break;
            case LV4_MODE1:
                if (screenY >= 80 && screenY <= 155){
                    if (screenX >= 250 && screenX <= 400){ // Next
                        pressState = 1;
                        mode = LV4_MODE2;
                        System.out.println("switch to lv4 mode2");
                    }
                }
                break;
            case LV4_MODE2:
                if (screenY >= 80 && screenY <= 155){
                    if (screenX >= 255 && screenX <= 405){ // Next
                        pressState = 1;
                        mode = EXIT_MODE;
                        System.out.println("lv4 mode2. switch to exit mode");
                    }
                }
                break;
            case EXIT_MODE:
                pressState = 1;
                System.out.println("exit");
                break;
        }
        return false;

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
        if (keycode == Input.Keys.N || keycode == Input.Keys.P) {
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
//        screenY = heightY-screenY;
//        switch (mode) {
//            case CODE_START:
//                handleStartHover(screenX, screenY);
//                break;
//            case CODE_SETTINGS:
//                handleSettingsHover(screenX, screenY);
//                break;
//            case CODE_LEVEL_SELECT:
//                handleLevelSelectHover(screenX, screenY);
//                break;
//        }
//        return false;
        return true;
    }

//    private void handleLevelSelectHover(int screenX, int screenY){
//        if (screenX >= 41 && screenX <= 328 && screenY >= 628 && screenY <= 689) {
//            hover = HOVER_BACK_TO_MAIN;
//        } else if (screenX >= 143 && screenX <= 266 && screenY >= 394 && screenY <= 512) {// Level 0
//
//        } else if (screenX >= 377 && screenX <= 490 && screenY >= 510 && screenY <= 621){ // level 1
//
//        }else if (screenX >= 364 && screenX <= 472 && screenY >= 278 && screenY <= 380){ // level 2
//
//        }else if (screenX >= 660 && screenX <= 762 && screenY >= 505 && screenY <= 606){ // level 3
//
//        }else if (screenX >= 552 && screenX <= 657 && screenY >= 175 && screenY <= 275){ // level 4
//
//        }else if (screenX >= 760 && screenX <= 860  && screenY >= 330 && screenY <= 440){ // level 5
//
//        }else if (screenX >= 763 && screenX <= 870 && screenY >= 68 && screenY <= 175){ // level 6
//
//        }else if (screenX >= 1030 && screenX <= 1135 && screenY >= 152 && screenY <= 254){ // level 7
//
//        }else {
//            hover = 0;
//        }
//    }
//
//    private void handleSettingsHover(int screenX, int screenY){
//        if (screenX >= 41 && screenX <= 328 && screenY >= 628 && screenY <= 689) {
//            hover = HOVER_BACK_TO_MAIN;
//        } else if (screenX >= 562 && screenX <= 722 && screenY >= 43 && screenY <= 88) {
//            hover = HOVER_SAVE_CHANGES;
//        } else {
//            hover = 0;
//        }
//    }

//    private void handleStartHover(int screenX, int screenY){
//        if (screenY >= 56 && screenY <= 135){
//            if (screenX >= 704 && screenX <= 810){ // Start
//                hover = HOVER_START;
//            } else if (screenX >= 872 && screenX <= 1003){
//                hover = HOVER_LEVELS;
//            } else if (screenX >= 1058 && screenX <= 1237){
//                hover = HOVER_SETTINGS;
//            } else {
//                hover = 0;
//            }
//            return;
//        }
//        hover = 0;
//    }

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