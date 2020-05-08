package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rekindled.light.LightSourceLight;
import edu.cornell.gdiac.rekindled.obstacle.BoxObstacle;

public class LightSourceObject extends BoxObstacle {
    private final int TILE_SIZE = 75;
    private final int FRAME_NUMBER = 8;
    private final float FRAME_RATE = 1/10f;
    private float timeElapsed;
    boolean isTransitioning;
    boolean isLit;
    boolean touchingPlayer;
    Animation lightAnimation;
    TextureRegion litTexture;
    TextureRegion dimTexture;
    LightSourceLight light;
    private Sound onSound;
    private Sound offSound;
    private float volume;

    public LightSourceObject(int x, int y, int w, int h, boolean isLit){
        super(x, y, w, h);
        timeElapsed = 0f;
        this.isLit = isLit;
        this.touchingPlayer = false;
        this.getFilterData().categoryBits = Constants.BIT_SOURCE;
        onSound = Gdx.audio.newSound(Gdx.files.internal("sounds/on.mp3"));
        offSound = Gdx.audio.newSound(Gdx.files.internal("sounds/off.mp3"));
        volume = .75f;
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

    public void setAnimation(TextureRegion texture){
            TextureRegion [][] frames = texture.split(TILE_SIZE,TILE_SIZE);
            TextureRegion[] animationFrames = new TextureRegion[FRAME_NUMBER];
            for(int i = 0; i < FRAME_NUMBER; i++){
                animationFrames[i] = frames[0][i];
            }

            lightAnimation = new Animation(FRAME_RATE, animationFrames);
            if(isLit()){
                lightAnimation.setPlayMode(Animation.PlayMode.REVERSED);
            }

        }


    public boolean isLit() {
        return isLit;
    }

    public boolean toggleLit() {
        if (touchingPlayer) {
            isLit = !isLit;
            light.setActive(isLit);
            if (isLit) {
                offSound.play(volume);
                this.timeElapsed = 0;
                lightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
                isTransitioning = true;
            } else {
                onSound.play(volume);
                this.timeElapsed = 0;
                lightAnimation.setPlayMode(Animation.PlayMode.REVERSED);
                isTransitioning = true;
            }
            return true;
        }
        return false;
    }

    public void mute(){
        volume = 0.0f;
    }
    public void unmute(){
        volume = .75f;
    }

    public boolean getTouchingPlayer() {
        return this.touchingPlayer;
    }

    public boolean contains(Vector2 pos) {
        return this.light.contains(pos.x, pos.y);
    }

    public void setTouchingPlayer(boolean value){
        touchingPlayer = value;
    }

    public void draw(GameCanvas canvas){
        if(isTransitioning){
            timeElapsed += Gdx.graphics.getDeltaTime();
            isTransitioning = !lightAnimation.isAnimationFinished(timeElapsed);
        }
        super.draw(canvas, lightAnimation, false, timeElapsed, TILE_SIZE, Color.WHITE);
    }
}
