package edu.cornell.gdiac.rekindled;/*
 * RocketModel.java
 *
 * This is one of the files that you are expected to modify. Please limit changes to
 * the regions that say INSERT CODE HERE.
 *
 * Note how this class combines physics and animation.  This is a good template
 * for models in your game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.rekindled.light.AuraLight;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.rekindled.obstacle.*;

/**
 * Player avatar for the rocket lander game.
 * <p>
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class Player extends FeetHitboxObstacle {


    // Default physics values
    /**
     * The density of this rocket
     */
    private static final float DEFAULT_DENSITY = 1.0f;
    /**
     * The friction of this rocket
     */
    private static final float DEFAULT_FRICTION = 0.1f;
    /**
     * The restitution of this rocket
     */
    private static final float DEFAULT_RESTITUTION = 0.4f;
    /**
     * The thrust factor to convert player input into thrust
     */
    private static final float DEFAULT_THRUST = 30.0f;

    /**
     * the size of a tile of the sprite sheet
     */
    private static final int TILE_SIZE = 100;

    /**
     * the number of frames of the sprite sheet
     */
    private static final int NUMBER_FRAMES = 10;

    /**
     * number of frames for the idle animation
     **/
    private static final int IDLE_FRAMES = 8;

    /**
     * number of frames for the throwing animation
     **/
    private static final int THROW_FRAMES = 17;

    private static final float FRAME_RATE = 1 / 10f;
    private static final float THROW_RATE = 1 / 10f;


    private Sound grassStep;
    private float volume;


    /**
     * The force to apply to this rocket
     */
    private Vector2 force;

    /**
     * The texture filmstrip for the left animation node
     */
    FilmStrip mainBurner;
    /**
     * The associated sound for the main afterburner
     */
    String mainSound;
    /**
     * The animation phase for the main afterburner
     */
    boolean mainCycle = true;

    /**
     * The texture filmstrip for the left animation node
     */
    FilmStrip leftBurner;
    /**
     * The associated sound for the left side burner
     */
    String leftSound;
    /**
     * The animation phase for the left side burner
     */
    boolean leftCycle = true;

    /**
     * The texture filmstrip for the left animation node
     */
    FilmStrip rghtBurner;
    /**
     * The associated sound for the right side burner
     */
    String rghtSound;
    /**
     * The associated sound for the right side burner
     */
    boolean rghtCycle = true;

    /**
     * Cache object for transforming the force according the object angle
     */
    public Affine2 affineCache = new Affine2();
    /**
     * Cache object for left afterburner origin
     */
    public Vector2 leftOrigin = new Vector2();
    /**
     * Cache object for right afterburner origin
     */
    public Vector2 rghtOrigin = new Vector2();

    private Texture mainTexture;
    private Animation frontWalkingAnimation;
    private Animation backWalkingAnimation;
    private Animation leftWalkingAnimation;
    private Animation rightWalkingAnimation;
    private Animation currentAnimation;
    private Animation frontPlacingAnimation;
    private Animation frontTakingAnimation;
    private Animation leftPlacingAnimation;
    private Animation leftTakingAnimation;
    private Animation rightPlacingAnimation;
    private Animation rightTakingAnimation;
    private Animation frontThrowAnimation;
    private Animation backThrowAnimation;
    private Animation leftThrowAnimation;
    private Animation rightThrowAnimation;
    private Animation frontIdleAnimation;
    private Animation backIdleAnimation;
    private Animation leftIdleAnimation;
    private Animation rightIdleAnimation;
    private Animation deathAnimation;

    public static final int SPEED = 4;

    public int lightCounter;
    private float toggleDelayTimer;
    private boolean toggleCooldown;
    private static final float TURN_ON_DELAY = 1.5f;

    private boolean dying;
    private boolean placingLight;
    private boolean takingLight;
    private boolean throwingLight;
    private boolean touchingLight;
    private boolean idle;
    private float throwDelayTimer;
    private boolean throwCooldown;

    private static final float SOUND_DELAY = .5f;
    private float soundTimer;
    private boolean soundPlaying;

    private Sound throwSound;

    /**
     * Returns the force applied to this rocket.
     * <p>
     * This method returns a reference to the force vector, allowing it to be modified.
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the force applied to this rocket.
     */
    public Vector2 getForce() {
        return force;
    }

    /**
     * Returns the x-component of the force applied to this rocket.
     * <p>
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the x-component of the force applied to this rocket.
     */
    public float getFX() {
        return force.x;
    }

    /**
     * Sets the x-component of the force applied to this rocket.
     * <p>
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this rocket.
     */
    public void setFX(float value) {
        force.x = value;
    }

    /**
     * Returns the y-component of the force applied to this rocket.
     * <p>
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the y-component of the force applied to this rocket.
     */
    public float getFY() {
        return force.y;
    }

    /**
     * Sets the x-component of the force applied to this rocket.
     * <p>
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this rocket.
     */
    public void setFY(float value) {
        force.y = value;
    }

    /**
     * Returns the amount of thrust that this rocket has.
     * <p>
     * Multiply this value times the horizontal and vertical values in the
     * input controller to get the force.
     *
     * @return the amount of thrust that this rocket has.
     */
    public float getThrust() {
        return DEFAULT_THRUST;
    }

    /**
     * Creates a new rocket at the origin.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public Player(float width, float height) {
        this(0, 0, width, height);
    }

    /**
     * Creates a new rocket at the given position.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x      Initial x position of the box center
     * @param y      Initial y position of the box center
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public Player(float x, float y, float width, float height) {
        super(x, y, width, height);
        super.setDirection(Direction.FRONT);

        force = new Vector2();
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        setName("rocket");
        lightCounter = 0;
        toggleDelayTimer = 0;
        toggleCooldown = false;
        touchingLight = false;
        placingLight = false;
        takingLight = false;
        throwingLight = false;
        dying = false;
        idle = true;
        throwSound = Gdx.audio.newSound(Gdx.files.internal("sounds/throw.mp3"));
        grassStep = Gdx.audio.newSound(Gdx.files.internal("sounds/grassStep.mp3"));

        this.getFilterData().categoryBits = Constants.BIT_PLAYER;
        volume = 1.0f;
    }

    public Player(float x, float y, float width, float height, int lights) {
        this(x, y, width, height);
        lightCounter = lights;
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     * <p>
     * This method overrides the base method to keep your ship from spinning.
     *
     * @param world Box2D world to store body
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // Get the box body from our parent class
        if (!super.activatePhysics(world)) {
            return false;
        }

        //#region INSERT CODE HERE
        // Insert code here to prevent the body from rotating
        body.setFixedRotation(true);
        //#endregion

        return true;
    }

    public void move(InputController.Move_Direction move) {
        idle = false;
        if (move == Entity_Controller.Move_Direction.MOVE_DOWN) {
            body.setLinearVelocity(0, -SPEED);
            super.setDirection(Direction.FRONT);
        } else if (move == Entity_Controller.Move_Direction.MOVE_UP) {
            body.setLinearVelocity(0, SPEED);
            super.setDirection(Direction.BACK);
        } else if (move == Entity_Controller.Move_Direction.MOVE_RIGHT) {
            body.setLinearVelocity(SPEED, 0);
            super.setDirection(Direction.RIGHT);
        } else if (move == Entity_Controller.Move_Direction.MOVE_LEFT) {
            body.setLinearVelocity(-SPEED, 0);
            super.setDirection(Direction.LEFT);
        } else {
            idle = true;
            body.setLinearVelocity(0, 0);
            timeElapsed += Gdx.graphics.getDeltaTime();
        }

        if(soundPlaying){
            soundTimer += Gdx.graphics.getDeltaTime();
            if (soundTimer >= SOUND_DELAY){
                grassStep.stop();
                soundPlaying = false;
                soundTimer = 0;
            }
        }
        else if (idle == false){
            grassStep.play(volume);
            soundPlaying = true;
        }
    }

    public Fixture createFixture(FixtureDef fd) {
        return body.createFixture(fd);
    }

    /**
     * Applies the force to the body of this ship
     * <p>
     * This method should be called after the force attribute is set.
     */
    public void applyForce() {
        if (!isActive()) {
            return;
        }

        // Orient the force with rotation.
        affineCache.setToRotationRad(getAngle());
        affineCache.applyTo(force);

        //#region INSERT CODE HERE
        // Apply force to the rocket BODY, not the rocket
        body.applyForce(force, getPosition(), true);
        //#endregion
    }

    public void updateCooldown(float dt) {
        if (toggleCooldown) {
            toggleDelayTimer += dt;
            if (toggleDelayTimer >= TURN_ON_DELAY) {
                toggleCooldown = false;
            }
        }

        if (throwCooldown) {
            throwDelayTimer += dt;
            if (throwDelayTimer >= TURN_ON_DELAY) {
                throwCooldown = false;
            }
        }

    }

    public void takeLight() {
        toggleDelayTimer = 0;
        toggleCooldown = true;
        lightCounter += 1;
        takingLight = true;
        super.timeElapsed = 0;
    }

    public void placeLight() {
        toggleDelayTimer = 0;
        toggleCooldown = true;
        lightCounter -= 1;
        placingLight = true;
        super.timeElapsed = 0;
    }

    public void die() {
        dying = true;
        timeElapsed = 0;
        setDirection(Direction.FRONT);
    }

    public void throwLight() {
        throwSound.play(volume);
        toggleDelayTimer = 0;
        toggleCooldown = true;
        throwDelayTimer = 0;
        throwCooldown = true;
        lightCounter -= 1;
        throwingLight = true;
        super.timeElapsed = 0;
    }

    public int getLightCounter() {
        return this.lightCounter;
    }

    public void setAnimations(TextureRegion frontTexture, TextureRegion backTexture, TextureRegion leftTexture,
                              TextureRegion rightTexture, TextureRegion frontPlace, TextureRegion frontTake,
                              TextureRegion leftPlace, TextureRegion leftTake, TextureRegion rightPlace,
                              TextureRegion rightTake, TextureRegion frontIdle, TextureRegion backIdle,
                              TextureRegion leftIdle, TextureRegion rightIdle, TextureRegion frontThrow,
                              TextureRegion backThrow, TextureRegion rightThrow, TextureRegion leftThrow, TextureRegion death) {
        frontWalkingAnimation = getAnimation(frontTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        backWalkingAnimation = getAnimation(backTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        rightWalkingAnimation = getAnimation(rightTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        leftWalkingAnimation = getAnimation(leftTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        frontPlacingAnimation = getAnimation(frontPlace, TILE_SIZE, TILE_SIZE, THROW_FRAMES, FRAME_RATE);
        frontTakingAnimation = getAnimation(frontTake, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        leftPlacingAnimation = getAnimation(leftPlace, TILE_SIZE, TILE_SIZE, THROW_FRAMES, FRAME_RATE);
        leftTakingAnimation = getAnimation(leftTake, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        rightPlacingAnimation = getAnimation(rightPlace, TILE_SIZE, TILE_SIZE, THROW_FRAMES, FRAME_RATE);
        rightTakingAnimation = getAnimation(rightTake, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        frontThrowAnimation = getAnimation(frontThrow, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, THROW_RATE);
        backThrowAnimation = getAnimation(backThrow, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, THROW_RATE);
        leftThrowAnimation = getAnimation(leftThrow, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, THROW_RATE);
        rightThrowAnimation = getAnimation(rightThrow, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, THROW_RATE);
        frontIdleAnimation = getAnimation(frontIdle, TILE_SIZE, TILE_SIZE, IDLE_FRAMES, FRAME_RATE);
        backIdleAnimation = getAnimation(backIdle, TILE_SIZE, TILE_SIZE, IDLE_FRAMES, FRAME_RATE);
        leftIdleAnimation = getAnimation(leftIdle, TILE_SIZE, TILE_SIZE, IDLE_FRAMES, FRAME_RATE);
        rightIdleAnimation = getAnimation(rightIdle, TILE_SIZE, TILE_SIZE, IDLE_FRAMES, FRAME_RATE);
        deathAnimation = getAnimation(death, TILE_SIZE, TILE_SIZE, 5, 1/6f);

    }

    public void mute(){
        volume = 0.0f;
    }
    public void unmute(){
        volume = 1.0f;
    }

    public void setTouchingLight(boolean value) {
        touchingLight = value;
    }

    public boolean getTouchingLight() {
        return touchingLight;
    }

    public boolean getToggleCooldown() {
        return toggleCooldown;
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        Color tint = (toggleCooldown) ? Color.CYAN : Color.WHITE;
        if (dying) {
            super.timeElapsed += Gdx.graphics.getDeltaTime();
            currentAnimation = deathAnimation;
            super.draw(canvas, currentAnimation, false, super.getTimeElapsed(), TILE_SIZE, tint);
        } else if (placingLight) {
            super.timeElapsed += Gdx.graphics.getDeltaTime();
            switch (super.getDirection()) {
                case FRONT:
                    currentAnimation = frontPlacingAnimation;
                    break;
                case LEFT:
                    currentAnimation = leftPlacingAnimation;
                    break;
                case RIGHT:
                    currentAnimation = rightPlacingAnimation;
                    break;
                case BACK:
                    currentAnimation = backWalkingAnimation;
                    break;
            }
            super.draw(canvas, currentAnimation, false, super.getTimeElapsed(), TILE_SIZE, tint);
            if (frontPlacingAnimation.isAnimationFinished(super.timeElapsed)) {
                placingLight = false;
                super.timeElapsed = 0;
            }
        } else if (throwingLight) {
            super.timeElapsed += Gdx.graphics.getDeltaTime();
            switch (super.getDirection()) {
                case FRONT:
                    currentAnimation = frontThrowAnimation;
                    break;
                case LEFT:
                    currentAnimation = leftThrowAnimation;
                    break;
                case RIGHT:
                    currentAnimation = rightThrowAnimation;
                    break;
                case BACK:
                    currentAnimation = backThrowAnimation;
                    break;
            }
            super.draw(canvas, currentAnimation, false, super.getTimeElapsed(), TILE_SIZE, tint);
            if (frontThrowAnimation.isAnimationFinished(super.timeElapsed)) {
                throwingLight = false;
                super.timeElapsed = 0;
            }
        } else if (takingLight) {
            super.timeElapsed += Gdx.graphics.getDeltaTime();
            switch (super.getDirection()) {
                case FRONT:
                    currentAnimation = frontTakingAnimation;
                    break;
                case LEFT:
                    currentAnimation = leftTakingAnimation;
                    break;
                case RIGHT:
                    currentAnimation = rightTakingAnimation;
                    break;
                case BACK:
                    currentAnimation = backWalkingAnimation;
                    break;
            }
            super.draw(canvas, currentAnimation, false, super.getTimeElapsed(), TILE_SIZE, tint);
            if (frontPlacingAnimation.isAnimationFinished(super.timeElapsed)) {
                takingLight = false;
                super.timeElapsed = 0;
            }
        } else if (idle) {
            switch (super.getDirection()) {
                case LEFT:
                    currentAnimation = leftIdleAnimation;
                    break;
                case RIGHT:
                    currentAnimation = rightIdleAnimation;
                    break;
                case FRONT:
                    currentAnimation = frontIdleAnimation;
                    break;
                case BACK:
                    currentAnimation = backIdleAnimation;
                    break;
            }
            super.draw(canvas, currentAnimation, true, super.getTimeElapsed(), TILE_SIZE, tint);
        } else {
            {
                switch (super.getDirection()) {
                    case LEFT:
                        currentAnimation = leftWalkingAnimation;
                        break;
                    case RIGHT:
                        currentAnimation = rightWalkingAnimation;
                        break;
                    case FRONT:
                        currentAnimation = frontWalkingAnimation;
                        break;
                    case BACK:
                        currentAnimation = backWalkingAnimation;
                        break;
                }
                super.draw(canvas, currentAnimation, true, super.getTimeElapsed(), TILE_SIZE, tint);
            }


        }
    }




    public boolean getThrowCooldown() {
        return this.throwCooldown;
    }
}
