package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.controllers.Controller;
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
    protected Vector2 secondaryOrigin;

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
        secondaryOrigin = new Vector2();
        mainState = true;
    }

    public void setMainState(boolean value){
        mainState = value;
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
    public void move(Entity_Controller.Move_Direction direction, Board board){
        if (board.isCenterOfTile(getPosition())){
            setMoving(false);
            //reset to center
            setPosition(board.boardToScreen(board.screenToBoard(getPosition().x)),
                    board.boardToScreen(board.screenToBoard(getPosition().y)));
        }
        switch (direction) {
            case MOVE_UP:
                if (getDirection() == Entity.Direction.UP) {
                    move(0, board.getTileSize() + board.getTileSpacing());
                } else {
                    setDirection(Entity.Direction.UP);
                }
                break;
            case MOVE_DOWN:
                if (getDirection() == Entity.Direction.DOWN) {
                    move(0, -board.getTileSize() - board.getTileSpacing());
                } else {
                    setDirection(Entity.Direction.DOWN);
                }
                break;
            case MOVE_RIGHT:
                if (getDirection() == Entity.Direction.RIGHT) {
                    move(board.getTileSize() + board.getTileSpacing(), 0);
                } else {
                    setDirection(Entity.Direction.RIGHT);
                }
                break;
            case MOVE_LEFT:
                if (getDirection() == Entity.Direction.LEFT) {
                    move(-board.getTileSize() - board.getTileSpacing(), 0);
                } else {
                    setDirection(Entity.Direction.LEFT);
                }
                break;
        }
        if (board.isObstructed(getGoal())){
            setMoving(false);
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


    public void setSecondaryTexture(Texture value) {
        secondaryTexture = value;
        secondaryOrigin.set(secondaryTexture.getWidth()/2.0f, secondaryTexture.getHeight()/2.0f);
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
        if(mainState == false){
            canvas.draw(secondaryTexture, Color.WHITE, secondaryOrigin.x, secondaryOrigin.y, position.x, position.y, 0, 1, 1);
        }
        else if (mainTexture != null) {
            canvas.draw(mainTexture, Color.WHITE,origin.x,origin.y,position.x,position.y + 16,0,1,1);
        }
    }

    public void draw(GameCanvas canvas, Direction direction, boolean cooldown){
        Color tint = cooldown ? Color.RED : Color.WHITE;
        if (mainTexture != null) {
            if(direction == Direction.RIGHT){
                canvas.draw(mainTexture, tint,origin.x,origin.y,position.x,position.y + 32,0,-.25f / 3,.25f / 3);
            } else {
                canvas.draw(mainTexture, tint, origin.x, origin.y, position.x, position.y + 32, 0, .25f / 3, .25f / 3);
            }
        }
    }

}
