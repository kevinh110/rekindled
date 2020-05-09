/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;

import edu.cornell.gdiac.util.*;

import static edu.cornell.gdiac.rekindled.Entity_Controller.Move_Direction.*;

/**
 * Class for reading player input. 
 *
 * This supports both a keyboard and X-Box controller. In previous solutions, we only 
 * detected the X-Box controller on start-up.  This class allows us to hot-swap in
 * a controller via the new XBox360Controller class.
 */
public class InputController extends Entity_Controller{
	/** The singleton instance of the input controller */
	private static InputController theController = null;


	/** 
	 * Return the singleton instance of the input controller
	 *
	 * @return the singleton instance of the input controller
	 */
	public static InputController getInstance() {
		if (theController == null) {
			theController = new InputController();
		}
		return theController;
	}
	
	// Fields to manage buttons
	/** Whether the reset button was pressed. */
	private boolean resetPressed;
	private boolean resetPrevious;
	/** Whether the button to advanced worlds was pressed. */
	private boolean nextPressed;
	private boolean nextPrevious;
	/** Whether the button to step back worlds was pressed. */
	private boolean prevPressed;
	private boolean prevPrevious;
	/** Whether the primary action button was pressed. */
	private boolean primePressed;
	private boolean primePrevious;
	/** Whether the secondary action button was pressed. */
	private boolean secondPressed;
	private boolean secondPrevious;
	/** Whether the teritiary action button was pressed. */
	private boolean tertiaryPressed;
	/** Whether the debug toggle was pressed. */
	private boolean debugPressed;
	private boolean debugPrevious;
	/** Whether the exit button was pressed. */
	private boolean exitPressed;
	private boolean exitPrevious;
	/** Whether the down button was pressed. */
	private boolean downPressed;
	private boolean downPrevious;
	/** Whether the up button was pressed. */
	private boolean upPressed;
	private boolean upPrevious;
	/** Whether the left button was pressed. */
	private boolean leftPressed;
	private boolean leftPrevious;
	/** Whether the right button was pressed. */
	private boolean rightPressed;
	private boolean rightPrevious;
	/** Whether the paused button was pressed */
	private boolean pausePressed;
	private boolean pausePrevious;

	/** Whether the shift key was pressed . */
	private boolean shiftPressed;

	/** Whether the zoom key (Z) was bressed */
	private boolean zoomPressed;
	private boolean zoomPrevious;

	/** Whether the mute key (M) was pressed */
	private boolean mutePressed;
	private boolean mutePrevious;

	/** whether the shift key was previously pressed */
	private boolean shiftPrevious;

	/** How much did we move horizontally? */
	private float horizontal;
	/** How much did we move vertically? */
	private float vertical;
	/** The crosshair position (for raddoll) */
	private Vector2 crosshair;
	/** The crosshair cache (for using as a return value) */
	private Vector2 crosscache;
	/** For the gamepad crosshair control */
	private float momentum;
	
	/** An X-Box controller (if it is connected) */
	XBox360Controller xbox;

	/**
	 * Returns the amount of sideways movement.
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways movement.
	 */
	public float getHorizontal() {
		return horizontal;
	}

	/**
	 * Returns the amount of vertical movement.
	 *
	 * -1 = down, 1 = up, 0 = still
	 *
	 * @return the amount of vertical movement.
	 */
	public float getVertical() {
		return vertical;
	}

	/**
	 * Returns the current position of the crosshairs on the screen.
	 *
	 * This value does not return the actual reference to the crosshairs position.
	 * That way this method can be called multiple times without any fair that
	 * the position has been corrupted.  However, it does return the same object
	 * each time.  So if you modify the object, the object will be reset in a
	 * subsequent call to this getter.
	 *
	 * @return the current position of the crosshairs on the screen.
	 */
	public Vector2 getCrossHair() {
		return crosscache.set(crosshair);
	}

	/**
	 * Returns true if the primary action button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the primary action button was pressed.
	 */
	public boolean didPrimary() {
		return primePressed && !primePrevious;
	}

	/**
	 * Returns true if the secondary action button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didSecondary() {
		return secondPressed;
	}

	/**
	 * Returns true if the shift key was pressed
	 */
	public boolean didShift() { return shiftPressed; }



	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didTertiary() {
		return tertiaryPressed;
	}

	/**
	 * Returns true if the reset button was pressed.
	 *
	 * @return true if the reset button was pressed.
	 */
	public boolean didReset() {
		return resetPressed && !resetPrevious;
	}

	/**
	 * Returns true if the player wants to go to the next level.
	 *
	 * @return true if the player wants to go to the next level.
	 */
	public boolean didAdvance() {
		return nextPressed && !nextPrevious;
	}

	public boolean didPause() {
		return pausePressed && !pausePrevious;
	}

	/**
	 * Returns true if the player wants to go to the previous level.
	 *
	 * @return true if the player wants to go to the previous level.
	 */
	public boolean didRetreat() {
		return prevPressed && !prevPrevious;
	}

	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.

	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didUp() {
		return upPressed;
	}

	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didDown() {
		return downPressed;
	}

	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didLeft() {
		return leftPressed;
	}

	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didRight() {
		return rightPressed;
	}

	public Move_Direction get_Next_Direction(){
		if(didDown()){
			return MOVE_DOWN;
		}
		if (didUp()){
			return MOVE_UP;
		}
		if(didRight()){
			return  MOVE_RIGHT;
		}
		if(didLeft()){
			return  MOVE_LEFT;
		}
		return NO_MOVE;
	}

	public boolean didMute() { return mutePressed; }

	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didDebug() {
		return debugPressed && !debugPrevious;
	}
	
	/**
	 * Returns true if the exit button was pressed.
	 *
	 * @return true if the exit button was pressed.
	 */
	public boolean didExit() {
		return exitPressed && !exitPrevious;
	}
	
	/**
	 * Creates a new input controller
	 * 
	 * The input controller attempts to connect to the X-Box controller at device 0,
	 * if it exists.  Otherwise, it falls back to the keyboard control.
	 */
	public InputController() { 
		// If we have a game-pad for id, then use it.
		xbox = new XBox360Controller(0);
		crosshair = new Vector2();
		crosscache = new Vector2();
	}

	/**
	 * Reads the input for the player and converts the result into game logic.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the crosshair.  They cannot go outside of this zone.
	 *
	 * @param bounds The input bounds for the crosshair.  
	 * @param scale  The drawing scale
	 */
	public void readInput(Rectangle bounds, Vector2 scale) {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down
		primePrevious  = primePressed;
		secondPrevious = secondPressed;
		resetPrevious  = resetPressed;
		debugPrevious  = debugPressed;
		exitPrevious = exitPressed;
		nextPrevious = nextPressed;
		prevPrevious = prevPressed;
		shiftPrevious = shiftPressed;
		pausePrevious = pausePressed;
		zoomPrevious = zoomPressed;
		mutePrevious = mutePressed;

		
		// Check to see if a GamePad is connected
		if (xbox.isConnected()) {
			readGamepad(bounds, scale);
			readKeyboard(bounds, scale, true); // Read as a back-up
		} else {
			readKeyboard(bounds, scale, false);
		}
	}

	/**
	 * Reads input from an X-Box controller connected to this computer.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the crosshair.  They cannot go outside of this zone.
	 *
	 * @param bounds The input bounds for the crosshair.  
	 * @param scale  The drawing scale
	 */
	private void readGamepad(Rectangle bounds, Vector2 scale) {
		resetPressed = xbox.getStart();
		exitPressed  = xbox.getBack();
		nextPressed  = xbox.getRB();
		prevPressed  = xbox.getLB();
		primePressed = xbox.getA();
		debugPressed  = xbox.getY();

		// Increase animation frame, but only if trying to move
		horizontal = xbox.getLeftX();
		vertical   = xbox.getLeftY();
		secondPressed = xbox.getRightTrigger() > 0.6f;

		// Move the crosshairs with the right stick.
		tertiaryPressed = xbox.getA();
		crosscache.set(xbox.getLeftX(), xbox.getLeftY());
	}

	/**
	 * Reads input from the keyboard.
	 *
	 * This controller reads from the keyboard regardless of whether or not an X-Box
	 * controller is connected.  However, if a controller is connected, this method
	 * gives priority to the X-Box controller.
	 *
	 * @param secondary true if the keyboard should give priority to a gamepad
	 */
	private void readKeyboard(Rectangle bounds, Vector2 scale, boolean secondary) {
		// Give priority to gamepad results
		resetPressed = (secondary && resetPressed) || (Gdx.input.isKeyPressed(Input.Keys.R));
		debugPressed = (secondary && debugPressed) || (Gdx.input.isKeyPressed(Input.Keys.F));
		primePressed = (secondary && primePressed) || (Gdx.input.isKeyPressed(Input.Keys.UP));
		secondPressed = (secondary && secondPressed) || (Gdx.input.isKeyPressed(Input.Keys.SPACE));
		prevPressed = (secondary && prevPressed) || (Gdx.input.isKeyPressed(Input.Keys.P));
		nextPressed = (secondary && nextPressed) || (Gdx.input.isKeyPressed(Input.Keys.N));
		exitPressed  = (secondary && exitPressed) || (Gdx.input.isKeyPressed(Input.Keys.ESCAPE));
		upPressed  = (secondary && upPressed) || (Gdx.input.isKeyPressed(Input.Keys.UP))
				|| (Gdx.input.isKeyPressed(Input.Keys.W));
		downPressed  = (secondary && downPressed) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))
				|| (Gdx.input.isKeyPressed(Input.Keys.S));
		leftPressed  = (secondary && leftPressed) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))
				|| (Gdx.input.isKeyPressed(Input.Keys.A));
		rightPressed  = (secondary && rightPressed) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
				|| (Gdx.input.isKeyPressed(Input.Keys.D));
		shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
		pausePressed = (secondary && pausePressed) || (Gdx.input.isKeyPressed(Input.Keys.L));
		zoomPressed = (secondary && zoomPressed) || (Gdx.input.isKeyPressed(Input.Keys.Z));
		mutePressed = (Gdx.input.isKeyPressed(Input.Keys.M));

		// Directional controls
		horizontal = (secondary ? horizontal : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
			horizontal += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
			horizontal -= 1.0f;
		}

		vertical = (secondary ? vertical : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			vertical += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
			vertical -= 1.0f;
		}
	}

    public boolean didZoom() {
		return zoomPressed && !zoomPrevious;
    }
}