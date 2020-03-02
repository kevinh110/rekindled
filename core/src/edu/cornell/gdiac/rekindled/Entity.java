package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Entity {
    //Position of entity. Values represent grid indices, NOT screen location.
    private Vector2 position;

    private boolean moving;
    private float speed;
    private float goalX;
    private float goalY;

    private Texture mainTexture;
    private Texture secondaryTexture;

    /** The texture origin for drawing */
    protected Vector2 origin;

    private Direction direction;

    private boolean mainState;

    public static enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
    }

    public Entity(float x, float y, float speed, Direction direction){
        position = new Vector2(x, y);
        moving = false;
        this.speed = speed;
        this.direction = direction;
        origin = new Vector2();
    }

    /**
     At least one of delX, delY is zero.
     */
    public void move(float delX, float delY){
        if(delX == 0 && delY == 0){
            return;
        }
        if(!moving) {
            moving = true;
            goalX = position.x + delX;
            goalY = position.y + delY;
        }
    }

    public Direction getDirection() {return direction;}
    public void setDirection(Direction direction){this.direction = direction;}
    public void setMoving(boolean value){
        moving = value;
    }
    public boolean getMoving(){
      return moving;
    }

    public Vector2 getPosition(){
        return position;
    }

    public void update(){
        if(moving){
            float newXPosition = position.x + Math.signum(goalX - position.x)*speed;
            float newYPosition = position.y + Math.signum(goalY - position.y)*speed;
            position.set(newXPosition, newYPosition);
        }
    }

    public Vector2 getGoal(){
        return new Vector2(goalX, goalY);
    }

    /// Texture Information
    /**
     * Returns the object texture for drawing purposes.
     *
     * In order for drawing to work properly, you MUST set the drawScale.
     * The drawScale converts the physics units to pixels.
     *
     * @return the object texture for drawing purposes.
     */
    public Texture getTexture() {
        if(mainState)
            return mainTexture;
        return secondaryTexture;
    }

    /**
     * Sets the object texture for drawing purposes.
     *
     * In order for drawing to work properly, you MUST set the drawScale.
     * The drawScale converts the physics units to pixels.
     *
     * @param value  the object texture for drawing purposes.
     */
    public void setTexture(Texture value) {
        mainTexture = value;
        origin.set(mainTexture.getWidth()/2.0f, mainTexture.getHeight()/2.0f);
    }

    public void setPosition(float x, float y){
        position = new Vector2(x, y);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (mainTexture != null) {
            canvas.draw(mainTexture, Color.WHITE,origin.x,origin.y,position.x,position.y + 16,0,1,1);
        }
    }

    public void draw(GameCanvas canvas, Direction direction){
        if (mainTexture != null) {
            if(direction == Direction.LEFT){
                canvas.draw(mainTexture, Color.WHITE,origin.x,origin.y,position.x,position.y + 16,0,-1,1);
            } else {
                canvas.draw(mainTexture, Color.WHITE, origin.x, origin.y, position.x, position.y + 16, 0, 1, 1);
            }
        }
    }

}
