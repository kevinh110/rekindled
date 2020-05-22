package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rekindled.obstacle.BoxObstacle;
import edu.cornell.gdiac.rekindled.obstacle.FeetHitboxObstacle;
import com.badlogic.gdx.graphics.g2d.Animation;

public class TutorialObject extends BoxObstacle {
    private float draw_scale;
//    TextureRegion texture;

    public TutorialObject(float x, float y, int width, int height){
        super(x,y, width, height);

    }
    public void setTexture(TextureRegion t){
        super.setTexture(t);
    }

//    public void setDraw_scale(float draw_scale){
//        System.out.println("set tutorial draw scale");
//
//        this.draw_scale = draw_scale;
//    }

    public void draw(GameCanvas canvas){
        super.draw(canvas);

    }

}
