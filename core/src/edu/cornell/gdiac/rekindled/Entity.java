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

    private boolean mainState;

    public Entity(float x, float y, float speed){
        position = new Vector2(x, y);
        moving = false;
        this.speed = speed;
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

    public void setMoving(boolean value){
        moving = value;
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

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (mainTexture != null) {
            canvas.draw(mainTexture, Color.WHITE,origin.x,origin.y,position.x,position.y,0,1,1);
        }
    }

}
