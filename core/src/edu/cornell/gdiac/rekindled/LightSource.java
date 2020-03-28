package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rekindled.obstacle.BoxObstacle;

public class LightSource extends BoxObstacle {
    boolean isLit;
    boolean touchingPlayer;
    TextureRegion litTexture;
    TextureRegion dimTexture;

    public LightSource(int x, int y, int w, int h, boolean isLit){
        super(x, y, w, h);
        this.isLit = isLit;
        this.touchingPlayer = false;
    }

    public void setTextureCache(TextureRegion litTexture, TextureRegion dimTexture){
        this.litTexture = litTexture;
        this.dimTexture = dimTexture;
    }

    public boolean isLit() {
        return isLit;
    }

    public void toggleLit(){
        if(touchingPlayer) {
            isLit = !isLit;
            System.out.println("Light is on?:" + isLit);
            if(isLit)
                this.setTexture(litTexture);
            else
                this.setTexture(dimTexture);
        }
    }

    public void setTouchingPlayer(boolean value){
        touchingPlayer = value;
    }
}
