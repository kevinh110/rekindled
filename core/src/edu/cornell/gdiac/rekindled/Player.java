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

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.rekindled.*;
import edu.cornell.gdiac.rekindled.obstacle.*;

/**
 * Player avatar for the rocket lander game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class Player extends FeetHitboxObstacle {


    // Default physics values
    /** The density of this rocket */
    private static final float DEFAULT_DENSITY  =  1.0f;
    /** The friction of this rocket */
    private static final float DEFAULT_FRICTION = 0.1f;
    /** The restitution of this rocket */
    private static final float DEFAULT_RESTITUTION = 0.4f;
    /** The thrust factor to convert player input into thrust */
    private static final float DEFAULT_THRUST = 30.0f;

    /** The force to apply to this rocket */
    private Vector2 force;

    /** The texture filmstrip for the left animation node */
    FilmStrip mainBurner;
    /** The associated sound for the main afterburner */
    String mainSound;
    /** The animation phase for the main afterburner */
    boolean mainCycle = true;

    /** The texture filmstrip for the left animation node */
    FilmStrip leftBurner;
    /** The associated sound for the left side burner */
    String leftSound;
    /** The animation phase for the left side burner */
    boolean leftCycle = true;

    /** The texture filmstrip for the left animation node */
    FilmStrip rghtBurner;
    /** The associated sound for the right side burner */
    String rghtSound;
    /** The associated sound for the right side burner */
    boolean rghtCycle  = true;

    /** Cache object for transforming the force according the object angle */
    public Affine2 affineCache = new Affine2();
    /** Cache object for left afterburner origin */
    public Vector2 leftOrigin = new Vector2();
    /** Cache object for right afterburner origin */
    public Vector2 rghtOrigin = new Vector2();

    private Texture mainTexture;
    private Entity.Direction direction;
    private Vector2 position;

    /** The number of frames for the afterburner */
    public static final int SPEED = 5;


    public int lightCounter;
    private float delayTimer;
    private boolean cooldown;
    private static final int TURN_ON_DELAY = 2;

    private boolean touchingLight;

    /**
     * Returns the force applied to this rocket.
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public Player(float width, float height) {
        this(0,0,width,height);
    }

    /**
     * Creates a new rocket at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the box center
     * @param y  		Initial y position of the box center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public Player(float x, float y, float width, float height) {
        super(x,y,width,height);
        force = new Vector2();
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        setName("rocket");
        lightCounter = 0;
        delayTimer = 0;
        cooldown = false;
        touchingLight = false;
        Filter filter = new Filter();
        filter.categoryBits = Filters.BIT_PLAYER;
        this.setFilterData(filter);
    }

    public Player(float x, float y, float width, float height, int lights) {
        this(x,y,width,height);
        lightCounter = lights;
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method to keep your ship from spinning.
     *
     * @param world Box2D world to store body
     *
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

    public void move(InputController.Move_Direction move){
        if (move == Entity_Controller.Move_Direction.MOVE_DOWN) {
            body.setLinearVelocity(0, -SPEED);
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_UP) {
            body.setLinearVelocity(0, SPEED);
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_RIGHT) {
            body.setLinearVelocity(SPEED, 0);
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_LEFT) {
            body.setLinearVelocity(-SPEED, 0);
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_DIAG_DOWN_LEFT) {
            body.setLinearVelocity(-(float)Math.sqrt(SPEED*SPEED/2), -(float)Math.sqrt(SPEED*SPEED/2));
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_DIAG_DOWN_RIGHT) {
            body.setLinearVelocity((float)Math.sqrt(SPEED*SPEED/2), -(float)Math.sqrt(SPEED*SPEED/2));
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_DIAG_UP_LEFT) {
            body.setLinearVelocity(-(float)Math.sqrt(SPEED*SPEED/2), (float)Math.sqrt(SPEED*SPEED/2));
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_DIAG_UP_RIGHT) {
            body.setLinearVelocity((float)Math.sqrt(SPEED*SPEED/2), (float)Math.sqrt(SPEED*SPEED/2));
        }
        else {
            body.setLinearVelocity(0, 0);
        }
    }

    public Fixture createFixture(FixtureDef fd) {
        return body.createFixture(fd);
    }

    /**
     * Applies the force to the body of this ship
     *
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

    public void updateCooldown(float dt){
        if (cooldown) {
            delayTimer+= dt;
            if (delayTimer >= TURN_ON_DELAY) {
                cooldown = false;
            }
        }
    }

    public void takeLight(){
        delayTimer = 0;
        cooldown = true;
        lightCounter += 1;
    }

    public void setTouchingLight(boolean value){
        touchingLight = value;
    }

    public boolean getTouchingLight(){
        return touchingLight;
    }

    public boolean getCooldown(){
        return cooldown;
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        super.draw(canvas);  // Player
    }
}