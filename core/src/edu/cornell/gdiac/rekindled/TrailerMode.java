package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.util.ScreenListener;

import java.awt.*;

public class TrailerMode implements Screen {

    private static String TRAILER_0 = "trailer/spritesheet1.png";
    private static String TRAILER_1 = "trailer/spritesheet2.png";
    private static String TRAILER_2 = "trailer/spritesheet3.png";
    private static String TRAILER_3 = "trailer/spritesheet4.png";
    /**
     * AssetManager to be loading in the background
     */
    private AssetManager manager;
    /**
     * Reference to GameCanvas created by the root
     */
    private GameCanvas canvas;
    /**
     * Listener that will update the player mode when we are done
     */

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 450;
    private static final int FRAMES_PER_TEXTURE = 16;
    private static final float SCALE = 1.7f;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    private boolean active;
    private Animation[] trailer;
    private Texture[] texture;
    private float elapsedTime;
    private int trailer_number;

    public TrailerMode(GameCanvas canvas, AssetManager manager) {
        this.canvas = canvas;
        this.manager = manager;

        trailer_number = 0;
        trailer = new Animation[4];
        texture = new Texture[4];
        texture[0] = new Texture(TRAILER_0);
        texture[1] = new Texture(TRAILER_1);
        texture[2] = new Texture(TRAILER_2);
        texture[3] = new Texture(TRAILER_3);

        for(int i = 0; i < 4; i++){
            TextureRegion region = new TextureRegion(texture[i]);
            TextureRegion[][] frames = region.split(FRAME_WIDTH, FRAME_HEIGHT);
            TextureRegion[] animationFrames = new TextureRegion[FRAMES_PER_TEXTURE];
            for(int j = 0; j < FRAMES_PER_TEXTURE; j++){
                animationFrames[j] = frames[0][j];
            }
            trailer[i] = new Animation(1/10f, animationFrames);
        }


    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    public void show() {
        active = true;

    }

    ;

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    public void render(float delta) {
        draw(delta);
    }

    public void draw(float delta){
        if (active) {
            elapsedTime += delta;
            if(trailer[trailer_number].isAnimationFinished(elapsedTime)){
                if(trailer_number == 3){
                    active = false;
                    listener.exitScreen(this, 0);
                    return;
                } else {
                    elapsedTime = 0;
                    trailer_number = trailer_number + 1;
                }
            }
            canvas.begin();
//            TextureRegion region = (TextureRegion)trailer[trailer_number].getKeyFrame(elapsedTime);
//            canvas.draw(region, Color.WHITE, 50, 50, FRAME_WIDTH, FRAME_HEIGHT, 0, 1, 1);
              canvas.draw(trailer[trailer_number], elapsedTime, (FRAME_WIDTH / (2.5f * SCALE)) - 200 , (FRAME_HEIGHT / (2.5f * SCALE)) - 120f, FRAME_WIDTH, FRAME_HEIGHT, SCALE);
//            canvas.draw(trailer[trailer_number], elapsedTime, false, 0, 0 , FRAME_WIDTH / 2, FRAME_HEIGHT / 2, 0, 2, 2, Color.WHITE);
            canvas.end();
        }
    }

    /**
     * @see ApplicationListener#resize(int, int)
     */
    public void resize(int width, int height) {

    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    /**
     * @see ApplicationListener#pause()
     */
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    public void dispose() {
        for(Texture t : texture){
            t.dispose();
        }
    }
}

