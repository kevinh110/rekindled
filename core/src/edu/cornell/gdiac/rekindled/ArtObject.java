package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rekindled.obstacle.FeetHitboxObstacle;
import com.badlogic.gdx.graphics.g2d.Animation;

public class ArtObject extends FeetHitboxObstacle {

    private float FRAME_RATE = 1/10f;
    private Animation animation;
    private Animation hitAnimation;
    private int tile_size;
    private int num_frames;
    private float timeElapsed;
    private boolean isLit;
    private boolean isHit; //whether the asset has just been touched ie. walking past a mushroom
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

    public void setHitAnimation(TextureRegion texture){
        hitAnimation = texture != null ? getAnimation(texture, tile_size, tile_size, 10, FRAME_RATE) : null;
    }

    public void setLit(boolean lit){
        if(isLit && !lit){
            isTransition = true;
            timeElapsed = 0;
            isLit = lit;
            isHit = false;
            animation.setPlayMode(Animation.PlayMode.REVERSED);
        } else if(!isLit && lit){
            isTransition = true;
            timeElapsed = 0;
            isLit = lit;
            animation.setPlayMode(Animation.PlayMode.NORMAL);
        }
    }

    public void setHit(){
        if(isLit){
            timeElapsed = 0;
            isHit = true;
        }

    }

    public void draw(GameCanvas canvas){
        if(isHit && hitAnimation != null){
            timeElapsed += Gdx.graphics.getDeltaTime();
            super.draw(canvas, hitAnimation, 1.31f, timeElapsed, tile_size);
            isHit = !hitAnimation.isAnimationFinished(timeElapsed);
        } else {
            if (isTransition || type == ASSET_TYPE.PICKUP) {
                timeElapsed += Gdx.graphics.getDeltaTime();
            }

            if (animation != null) {
                if (type == ASSET_TYPE.PICKUP) {
                    super.draw(canvas, animation, true, timeElapsed, tile_size, Color.WHITE);
                } else {
                    super.draw(canvas, animation, false, timeElapsed, tile_size, Color.WHITE);
                }
            }
        }
    }
}
