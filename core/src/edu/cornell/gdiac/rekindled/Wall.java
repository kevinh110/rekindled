package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rekindled.obstacle.BoxObstacle;

public class Wall extends BoxObstacle {
    static float DRAW_SCALE = .33f;
    TextureRegion[] wallTextures;

    int TextureIndex;

    public Wall(float x, float y, float width, float height){
        super(x, y, width, height);
    }

    public void setTextures(TextureRegion[] textures){
        this.wallTextures = textures;
    }

    public void setTexture(Board board){
//		if (!board.isWall(x - 1, y) && !board.isWall(x + 1, y) && !board.isWall(x, y - 1)){
//			return wallFrontTexture;
//		}
//		else if (!board.isWall(x - 1, y) && board.isWall(x + 1, y) && !board.isWall(x, y - 1)){
//			return wallLeftTexture;
//		}
//		else if (board.isWall(x - 1, y) && !board.isWall(x + 1, y) && !board.isWall(x, y - 1)){
//			return wallRightTexture;
//		}
//		else if (board.isWall(x - 1, y) && board.isWall(x + 1, y) && !board.isWall(x, y - 1)){
//			return wallMidTexture;
//		}
//		else {
//			return wallBackTexture;
//		}
        setTexture(wallTextures[0]);
    }

    public void draw(GameCanvas canvas){
        super.draw(canvas, DRAW_SCALE, DRAW_SCALE);
    }
    
}
