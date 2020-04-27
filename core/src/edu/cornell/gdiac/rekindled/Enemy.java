package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rekindled.light.SightConeLight;
import edu.cornell.gdiac.rekindled.obstacle.FeetHitboxObstacle;
import edu.cornell.gdiac.util.FilmStrip;

import java.util.Arrays;

public class Enemy extends FeetHitboxObstacle {
    // Default physics values
    /** The density of this rocket */
    private static final float DEFAULT_DENSITY  =  1.0f;
    /** The friction of this rocket */
    private static final float DEFAULT_FRICTION = 0.1f;
    /** The restitution of this rocket */
    private static final float DEFAULT_RESTITUTION = 0.4f;


    private static final int TILE_SIZE = 75;
    private static final int NUMBER_FRAMES = 6;

    private Animation frontWalkingAnimation;
    private Animation backWalkingAnimation;
    private Animation leftWalkingAnimation;
    private Animation rightWalkingAnimation;
    private Animation frontAngryWalkingAnimation;
    private Animation backAngryWalkingAnimation;
    private Animation leftAngryWalkingAnimation;
    private Animation rightAngryWalkingAnimation;
    private Animation savedAnimation;
    private Animation transformationAnimation;
    private Animation currentAnimation;

    private float timeElapsed;

    public boolean angry;


    /** The force to apply to this rocket */
    private Vector2 force;

    private SightConeLight sight;

    // DEFAULT - 6
    public int speed = 6;

    public static final int WANDER_SPEED = 3;

    public static final int CHASE_SPEED = 5;

    public static final float FRAME_RATE = 1/10f;

    public int facingDirection;

    /** If the enemy was just stunned */
    public boolean stunned = false;

    private int type;

    public int[][] wander;
    private int pointer = 0; // Points to the current goal in wander
    private boolean forward = true; //Indicates if we are going forward in wander or backward

    private boolean isLit;//whether the enemy is lit or not
    private boolean isTransitioning; //keeps track of if the enemy is currently animating a transitioning

    public boolean getIsLit(){
        return isLit;
    }

    public void setChaseSpeed(){
        speed = CHASE_SPEED;
    }
    public void setWanderSpeed(){
        speed = WANDER_SPEED;
    }

    public void setIsLit(boolean value){

        if(!isLit && value) {
            timeElapsed = 0;
            isTransitioning = true;
            transformationAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        } else if(isLit && !value){
            timeElapsed = 0;
            isTransitioning = true;
            transformationAnimation.setPlayMode(Animation.PlayMode.REVERSED);
        }
        isLit = value;
        this.sight.setActive(!value);

        }


    public int getType(){
        return type;
    }

    public int getPointer(){return pointer;}

    public int[] getWanderGoal(){
        return Arrays.copyOf(wander[pointer], 2); }

    public void setWanderGoal(int[] goal){
        for (int i = 0; i < wander.length; i++){
            if (wander[i][0] == goal[0] && wander[i][1] == goal[1]){
                pointer = i;
            }
        }
    }

    public int[][] getWanderPath(){
        return wander;
    }

    public void updateWanderGoal(){
        if (wander.length == 1){ // Handle Edge case with Stationary Enemy
            return;
        }
        if (pointer == wander.length - 1){
            forward = false;
        }
        else if (pointer == 0){
            forward = true;
        }
        if (forward) {pointer++;} else {pointer--;}
    }

    public void setWander(JsonValue pathJson){
        this.wander = new int[pathJson.size][2];
        JsonValue coord = pathJson.child();
        int idx = 0;
        while (coord != null){
            this.wander[idx] = coord.asIntArray();
            coord = coord.next();
            idx++;
        }


    }

    public Enemy(float width, float height) {
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
    public Enemy(float x, float y, float width, float height) {
        super(x,y,width,height);
        force = new Vector2();
        setDensity(DEFAULT_DENSITY);
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        setName("enemy");

        this.getFilterData().categoryBits = Constants.BIT_ENEMY;
        facingDirection = Constants.BACK;
        timeElapsed = 0;
        isTransitioning = false;
    }

    public Enemy(float x, float y, float width, float height, int type) {
        this(x,y,width,height);
        this.type = type;
    }

    /** Initialized all animations. */
    public void setAnimations(TextureRegion frontTexture, TextureRegion backTexture, TextureRegion leftTexture,
                              TextureRegion rightTexture, TextureRegion transformation, TextureRegion saved){
        frontWalkingAnimation = getAnimation(frontTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        backWalkingAnimation = getAnimation(backTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        rightWalkingAnimation = getAnimation(rightTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        leftWalkingAnimation = getAnimation(leftTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        transformationAnimation = getAnimation(transformation, TILE_SIZE, TILE_SIZE, 8, FRAME_RATE);
        savedAnimation = getAnimation(saved, TILE_SIZE, TILE_SIZE, 8, FRAME_RATE);
    }

    public void setAngryAnimations(TextureRegion frontTexture, TextureRegion backTexture, TextureRegion leftTexture,
                              TextureRegion rightTexture, TextureRegion transformation, TextureRegion saved){
        frontAngryWalkingAnimation = getAnimation(frontTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        backAngryWalkingAnimation = getAnimation(backTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        rightAngryWalkingAnimation = getAnimation(rightTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        leftAngryWalkingAnimation = getAnimation(leftTexture, TILE_SIZE, TILE_SIZE, NUMBER_FRAMES, FRAME_RATE);
        transformationAnimation = getAnimation(transformation, TILE_SIZE, TILE_SIZE, 8, FRAME_RATE);
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



    public void moveOnTile(int goalX, int goalY, float delta){
        Vector2 pos = getPosition();
        float diffX = Math.abs(goalX - pos.x);
        float diffY = Math.abs(goalY - pos.y);

        // Fix Drift; This is a makeshift fix to a Box2D bug where
        // setting pos to (x, 4) actually sets to (x, 3.99998)
        if (diffX < .0001){
            setPosition(goalX, pos.y);
        }
        pos = getPosition();
        if (diffY < .0001){
            setPosition(pos.x, goalY);
        }
        pos = getPosition();

        if (pos.x == goalX && goalY < pos.y) { // Move down
//            System.out.println("move down");
            float newPosY = pos.y - speed * delta;
            if (goalY > newPosY){
                setPosition(goalX, goalY);
            }
            else{
                setPosition(goalX, newPosY);
            }
            facingDirection = Constants.FORWARD;
        }
        else if (pos.x == goalX && goalY > pos.y) { // Move up
//            System.out.println("move up");
            float newPosY = pos.y + speed * delta;
            if (goalY < newPosY){
                setPosition(goalX, goalY);
            }
            else {
                setPosition(goalX, newPosY);
            }
            facingDirection = Constants.BACK;
        }
        else if (goalX > pos.x && goalY == pos.y) { // Move right
//            System.out.println("move right");
            float newPosX = pos.x + speed * delta;
            if (goalX < newPosX){
                setPosition(goalX, goalY);
            }
            else {
                setPosition(newPosX, goalY);
            }
            facingDirection = Constants.RIGHT;
        }
        else if (goalX < pos.x && goalY == pos.y) { // Move left
//            System.out.println("move left");
            float newPosX = pos.x - speed * delta;
            if (goalX > newPosX){
                setPosition(goalX, goalY);
            }
            else {
                setPosition(newPosX, goalY);
            }
            facingDirection = Constants.LEFT;
        }
        else if(goalX == pos.x && goalY == pos.y){ // If goal did not change, don't move
            return;
        }
    }


    public void move(InputController.Move_Direction move){
        if (move == Entity_Controller.Move_Direction.MOVE_DOWN) {
            body.setLinearVelocity(0, -speed);
            facingDirection = Constants.BACK;
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_UP) {
            body.setLinearVelocity(0, speed);
            facingDirection = Constants.FORWARD;
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_RIGHT) {
            body.setLinearVelocity(speed, 0);
            facingDirection = Constants.RIGHT;
        }
        else if (move == Entity_Controller.Move_Direction.MOVE_LEFT) {
            body.setLinearVelocity(-speed, 0);
            facingDirection = Constants.LEFT;
        }
        else {
            body.setLinearVelocity(0, 0);
        }
    }

    public void addSight(SightConeLight light) {
        this.sight = light;
        this.sight.setPosition(this.getPosition());
        this.sight.setActive(true);
        updateSightCone();
    }
    public void updateSightCone() {
        if (this.sight.isActive()) {
            Color col = (angry) ? Color.RED : Color.CYAN;
            this.sight.setColor(col);
            this.sight.setPosition(this.getPosition());
//            System.out.println("Sight cone position: " + this.sight.getPosition());
            float angle =
                    (facingDirection == Constants.FORWARD) ? 270.0f :
                            (facingDirection == Constants.BACK) ? 90.f :
                                    (facingDirection == Constants.LEFT) ? 180.f :
                                            0f;
            this.sight.setDirection(angle);
        }
    }
    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        timeElapsed += Gdx.graphics.getDeltaTime();


        if(isTransitioning){
            currentAnimation = transformationAnimation;
            if(transformationAnimation.isAnimationFinished(timeElapsed)){
                isTransitioning = false;
            }
            super.draw(canvas, currentAnimation,false, timeElapsed, TILE_SIZE, Color.WHITE);

        } else if(isLit){
            currentAnimation = savedAnimation;
            super.draw(canvas, currentAnimation,true, timeElapsed, TILE_SIZE, Color.WHITE);

        }
        else {
            switch (facingDirection) {
                case Constants.FORWARD:
                    if (angry){ currentAnimation = frontAngryWalkingAnimation; }
                    else { currentAnimation = frontWalkingAnimation; }
                    break;
                case Constants.BACK:
                    if (angry){ currentAnimation = backAngryWalkingAnimation; }
                    else { currentAnimation = backWalkingAnimation; }
                    break;
                case Constants.RIGHT:
                    if (angry){ currentAnimation = rightAngryWalkingAnimation; }
                    else { currentAnimation = rightWalkingAnimation; }
                    break;
                case Constants.LEFT:
                    if (angry){ currentAnimation = leftAngryWalkingAnimation; }
                    else { currentAnimation = leftWalkingAnimation; }
                    break;
            }
            super.draw(canvas, currentAnimation,true, timeElapsed, TILE_SIZE, Color.WHITE);

        }
    }


    public boolean inSight(Vector2 position, float rad) {
//        return this.sight.contains(position.x, position.y) ||
//                this.sight.contains(position.x + rad, position.y) ||
//                this.sight.contains(position.x - rad, position.y) ||
//                this.sight.contains(position.x, position.y + rad) ||
//                this.sight.contains(position.x, position.y - rad);
        return this.sight.contains(position.x, position.y);
    }

    public int getFacingDirection() {
        return facingDirection;
    }
}