package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rekindled.obstacle.FeetHitboxObstacle;
import com.badlogic.gdx.graphics.g2d.Animation;

public class ArtObject extends FeetHitboxObstacle {

    private float FRAME_RATE = 1/10f;
    private Animation animation;
    private int tile_size;
    private int num_frames;
    private float timeElapsed;
    private boolean isLit;
    private boolean isTransition;
    public ASSET_TYPE type;
    public boolean isTaken; // Only used for pickup to fix multiple pickups

    enum ASSET_TYPE {
        MUSHROOM,
        GRASS,
        PICKUP
    }

    public ArtObject(float x, float y, int width, int height, int tile_size, int num_frames, ASSET_TYPE type){
        super(x,y, width, height);
        this.tile_size = tile_size;
        this.num_frames = num_frames;
        this.type = type;
        timeElapsed = 0;
        isLit = false;
        isTransition = false;

    }

    public void setAnimation(TextureRegion texture){
        animation = getAnimation(texture, tile_size, tile_size, num_frames, FRAME_RATE);
    }

    public void setLit(boolean lit){
        if(isLit && !lit){
            isTransition = true;
            timeElapsed = 0;
            isLit = lit;
            animation.setPlayMode(Animation.PlayMode.REVERSED);
        } else if(!isLit && lit){
            isTransition = true;
            timeElapsed = 0;
            isLit = lit;
            animation.setPlayMode(Animation.PlayMode.NORMAL);
        }
    }

    public void draw(GameCanvas canvas){
        if(isTransition || type == ASSET_TYPE.PICKUP) {
            timeElapsed += Gdx.graphics.getDeltaTime();
        }

        if(animation != null){
            if (type == ASSET_TYPE.PICKUP){
                super.draw(canvas, animation, true, timeElapsed, tile_size, Color.WHITE);
            } else {
                super.draw(canvas, animation, false, timeElapsed, tile_size, Color.WHITE);
            }
        }
    }
}
