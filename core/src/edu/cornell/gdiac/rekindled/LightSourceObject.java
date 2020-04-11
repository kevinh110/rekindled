package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rekindled.light.LightSourceLight;
import edu.cornell.gdiac.rekindled.obstacle.BoxObstacle;

public class LightSourceObject extends BoxObstacle {
    boolean isLit;
    boolean touchingPlayer;
    TextureRegion litTexture;
    TextureRegion dimTexture;
    LightSourceLight light;

    public LightSourceObject(int x, int y, int w, int h, boolean isLit){
        super(x, y, w, h);
        this.isLit = isLit;
        this.touchingPlayer = false;
        this.getFilterData().categoryBits = Constants.BIT_SOURCE;
    }

    public void addLight(LightSourceLight light) {
        this.light = light;
        light.setPosition(this.getX(), this.getY());
        this.light.setActive(isLit);
    }

    public void setTextureCache(TextureRegion litTexture, TextureRegion dimTexture){
        this.litTexture = litTexture;
        this.dimTexture = dimTexture;
    }

    public boolean isLit() {
        return isLit;
    }

    public boolean getTouchingPlayer() {
        return this.touchingPlayer;
    }
    public void toggleLit(){
        isLit = !isLit;
        light.setActive(isLit);
        System.out.println("Light is on?:" + isLit);
        if(isLit)
            this.setTexture(litTexture);
        else
            this.setTexture(dimTexture);
    }

    public boolean contains(Vector2 pos) {
        return this.light.contains(pos.x, pos.y);
    }

    public void setTouchingPlayer(boolean value){
        touchingPlayer = value;
    }
}
