package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rekindled.obstacle.BoxObstacle;

public class Wall extends BoxObstacle {
    private float draw_scale;
    TextureRegion[] wallTextures;

    public Wall(float x, float y, float width, float height){
        super(x, y, width, height);
    }

    public void setTextures(TextureRegion[] textures){
        this.wallTextures = textures;
    }

    public void setDraw_scale(float draw_scale){
        this.draw_scale = draw_scale;
    }


    public void setTexture(Board board){
        int x = (int)getX();
        int y = (int)getY();
		if (!board.isWall(x - 1, y) && !board.isWall(x + 1, y) && !board.isWall(x, y + 1) && board.isWall(x, y - 1)){
			setTexture(wallTextures[0]);
		}
		else if (board.isWall(x - 1, y) && !board.isWall(x + 1, y) && !board.isWall(x, y + 1) && board.isWall(x, y - 1) && board.isWall(x-1, y-1)){
            setTexture(wallTextures[1]);
        }
        else if (board.isWall(x - 1, y) && !board.isWall(x + 1, y) && !board.isWall(x, y + 1) && board.isWall(x, y - 1)){
            setTexture(wallTextures[2]);
        }
        else if (board.isWall(x - 1, y) && board.isWall(x + 1, y) && !board.isWall(x, y + 1) && board.isWall(x, y - 1)
                && board.isWall(x- 1, y -1) && board.isWall(x + 1, y -1)){
            setTexture(wallTextures[3]);
        }
        else if (!board.isWall(x - 1, y) && board.isWall(x + 1, y) && !board.isWall(x, y + 1) && board.isWall(x, y - 1) && board.isWall(x+1, y-1)){
            setTexture(wallTextures[4]);
        }
        else if (!board.isWall(x - 1, y) && board.isWall(x + 1, y) && !board.isWall(x, y + 1) && board.isWall(x, y - 1) && !board.isWall(x+1, y-1)){
            setTexture(wallTextures[5]);
        }
        else if (board.isWall(x - 1, y) && !board.isWall(x + 1, y) && !board.isWall(x, y + 1) && !board.isWall(x, y - 1)){
            setTexture(wallTextures[6]);
        }
        else if (board.isWall(x - 1, y) && board.isWall(x + 1, y) && !board.isWall(x, y + 1) && !board.isWall(x, y - 1)){
            setTexture(wallTextures[7]);
        }
        else if (board.isWall(x - 1, y) && !board.isWall(x + 1, y) && !board.isWall(x, y + 1) && board.isWall(x, y - 1) && !board.isWall(x - 1, y + 1)){
            setTexture(wallTextures[8]);
        }
        else if (!board.isWall(x - 1, y) && board.isWall(x + 1, y) && !board.isWall(x, y + 1) && !board.isWall(x, y - 1)){
            setTexture(wallTextures[9]);
        }
        else if (!board.isWall(x - 1, y) && !board.isWall(x + 1, y) && !board.isWall(x, y + 1) && !board.isWall(x, y - 1)){
            setTexture(wallTextures[10]);
        }
        else if (!board.isWall(x - 1, y) && !board.isWall(x + 1, y) && board.isWall(x, y + 1) && !board.isWall(x, y - 1)){
            setTexture(wallTextures[11]);
        }
        else if (!board.isWall(x - 1, y) && !board.isWall(x + 1, y) && board.isWall(x, y + 1) && board.isWall(x, y - 1)){
            setTexture(wallTextures[12]);
        }
        else if (board.isWall(x - 1, y) && !board.isWall(x + 1, y) && board.isWall(x, y + 1) &&
                board.isWall(x, y - 1) && board.isWall(x- 1, y - 1) && board.isWall(x - 1, y + 1)){
            setTexture(wallTextures[13]);
        }
        else if (board.isWall(x - 1, y) && board.isWall(x + 1, y) && board.isWall(x, y + 1) && board.isWall(x, y - 1)
                && board.isWall(x- 1, y - 1)&& board.isWall(x - 1, y + 1)
                && board.isWall(x + 1, y - 1)&& board.isWall(x +- 1, y + 1)){
            setTexture(wallTextures[14]);
        }
        else if (!board.isWall(x - 1, y) && board.isWall(x + 1, y) && board.isWall(x, y + 1) &&
                board.isWall(x, y - 1) && board.isWall(x + 1, y + 1) && board.isWall(x + 1, y - 1)){
            setTexture(wallTextures[15]);
        }
        else if (board.isWall(x - 1, y) && !board.isWall(x + 1, y) && board.isWall(x, y + 1) && !board.isWall(x, y - 1)){
            setTexture(wallTextures[16]);
        }
        else if (board.isWall(x - 1, y) && board.isWall(x + 1, y) && board.isWall(x, y + 1) && !board.isWall(x, y - 1)
                && board.isWall(x - 1, y + 1) && board.isWall(x + 1, y + 1)
        ){
            setTexture(wallTextures[17]);
        }
        else if (!board.isWall(x - 1, y) && board.isWall(x + 1, y) && board.isWall(x, y + 1) && !board.isWall(x, y - 1) && board.isWall(x + 1, y + 1)){
            setTexture(wallTextures[18]);
        }
        else if (!board.isWall(x - 1, y) && board.isWall(x + 1, y) && board.isWall(x, y + 1) && !board.isWall(x, y - 1) && !board.isWall(x + 1, y + 1)){
            setTexture(wallTextures[19]);
        }
        else if (board.isWall(x - 1, y) && board.isWall(x + 1, y) && !board.isWall(x, y + 1) &&
                board.isWall(x, y - 1) && !board.isWall(x- 1, y -1)&& !board.isWall(x + 1, y -1)){
            setTexture(wallTextures[20]);
        }
        else if (board.isWall(x - 1, y) && !board.isWall(x + 1, y) && board.isWall(x, y + 1) &&
                board.isWall(x, y - 1) && !board.isWall(x- 1, y - 1)&& !board.isWall(x - 1, y + 1)){
            setTexture(wallTextures[21]);
        }
        else if (board.isWall(x - 1, y) && board.isWall(x + 1, y) && board.isWall(x, y + 1) &&
                board.isWall(x, y - 1) && !board.isWall(x- 1, y - 1)&& !board.isWall(x - 1, y + 1)
                && !board.isWall(x + 1, y - 1)&& !board.isWall(x +- 1, y + 1)
        ){
            setTexture(wallTextures[22]);
        }
        else if (!board.isWall(x - 1, y) && board.isWall(x + 1, y) && board.isWall(x, y + 1) &&
                board.isWall(x, y - 1) && !board.isWall(x + 1, y + 1) && !board.isWall(x + 1, y - 1)){
            setTexture(wallTextures[23]);
        }
        else if (board.isWall(x - 1, y) && board.isWall(x + 1, y) && board.isWall(x, y + 1) &&
                !board.isWall(x, y - 1) && !board.isWall(x - 1, y + 1) && !board.isWall(x + 1, y + 1)){
            setTexture(wallTextures[24]);
        }
    }

    /**Yeah this code is copy pasted.  Who tf cares??? */
    public void setHoleTexture(Board board){
        int x = (int)getX();
        int y = (int)getY();
        if (!board.isHole(x - 1, y) && !board.isHole(x + 1, y) && !board.isHole(x, y + 1) && board.isHole(x, y - 1)){
            setTexture(wallTextures[0]);
        }
        else if (board.isHole(x - 1, y) && !board.isHole(x + 1, y) && !board.isHole(x, y + 1) && board.isHole(x, y - 1) && board.isHole(x-1, y-1)){
            setTexture(wallTextures[1]);
        }
        else if (board.isHole(x - 1, y) && !board.isHole(x + 1, y) && !board.isHole(x, y + 1) && board.isHole(x, y - 1)){
            setTexture(wallTextures[2]);
        }
        else if (board.isHole(x - 1, y) && board.isHole(x + 1, y) && !board.isHole(x, y + 1) && board.isHole(x, y - 1)
                && board.isHole(x- 1, y -1) && board.isHole(x + 1, y -1)){
            setTexture(wallTextures[3]);
        }
        else if (!board.isHole(x - 1, y) && board.isHole(x + 1, y) && !board.isHole(x, y + 1) && board.isHole(x, y - 1) && board.isHole(x+1, y-1)){
            setTexture(wallTextures[4]);
        }
        else if (!board.isHole(x - 1, y) && board.isHole(x + 1, y) && !board.isHole(x, y + 1) && board.isHole(x, y - 1) && !board.isHole(x+1, y-1)){
            setTexture(wallTextures[5]);
        }
        else if (board.isHole(x - 1, y) && !board.isHole(x + 1, y) && !board.isHole(x, y + 1) && !board.isHole(x, y - 1)){
            setTexture(wallTextures[6]);
        }
        else if (board.isHole(x - 1, y) && board.isHole(x + 1, y) && !board.isHole(x, y + 1) && !board.isHole(x, y - 1)){
            setTexture(wallTextures[7]);
        }
        else if (board.isHole(x - 1, y) && !board.isHole(x + 1, y) && !board.isHole(x, y + 1) && board.isHole(x, y - 1) && !board.isHole(x - 1, y + 1)){
            setTexture(wallTextures[8]);
        }
        else if (!board.isHole(x - 1, y) && board.isHole(x + 1, y) && !board.isHole(x, y + 1) && !board.isHole(x, y - 1)){
            setTexture(wallTextures[9]);
        }
        else if (!board.isHole(x - 1, y) && !board.isHole(x + 1, y) && !board.isHole(x, y + 1) && !board.isHole(x, y - 1)){
            setTexture(wallTextures[10]);
        }
        else if (!board.isHole(x - 1, y) && !board.isHole(x + 1, y) && board.isHole(x, y + 1) && !board.isHole(x, y - 1)){
            setTexture(wallTextures[11]);
        }
        else if (!board.isHole(x - 1, y) && !board.isHole(x + 1, y) && board.isHole(x, y + 1) && board.isHole(x, y - 1)){
            setTexture(wallTextures[12]);
        }
        else if (board.isHole(x - 1, y) && !board.isHole(x + 1, y) && board.isHole(x, y + 1) &&
                board.isHole(x, y - 1) && board.isHole(x- 1, y - 1) && board.isHole(x - 1, y + 1)){
            setTexture(wallTextures[13]);
        }
        else if (board.isHole(x - 1, y) && board.isHole(x + 1, y) && board.isHole(x, y + 1) && board.isHole(x, y - 1)
                && board.isHole(x- 1, y - 1)&& board.isHole(x - 1, y + 1)
                && board.isHole(x + 1, y - 1)&& board.isHole(x +- 1, y + 1)){
            setTexture(wallTextures[14]);
        }
        else if (!board.isHole(x - 1, y) && board.isHole(x + 1, y) && board.isHole(x, y + 1) &&
                board.isHole(x, y - 1) && board.isHole(x + 1, y + 1) && board.isHole(x + 1, y - 1)){
            setTexture(wallTextures[15]);
        }
        else if (board.isHole(x - 1, y) && !board.isHole(x + 1, y) && board.isHole(x, y + 1) && !board.isHole(x, y - 1)){
            setTexture(wallTextures[16]);
        }
        else if (board.isHole(x - 1, y) && board.isHole(x + 1, y) && board.isHole(x, y + 1) && !board.isHole(x, y - 1)
                && board.isHole(x - 1, y + 1) && board.isHole(x + 1, y + 1)
        ){
            setTexture(wallTextures[17]);
        }
        else if (!board.isHole(x - 1, y) && board.isHole(x + 1, y) && board.isHole(x, y + 1) && !board.isHole(x, y - 1) && board.isHole(x + 1, y + 1)){
            setTexture(wallTextures[18]);
        }
        else if (!board.isHole(x - 1, y) && board.isHole(x + 1, y) && board.isHole(x, y + 1) && !board.isHole(x, y - 1) && !board.isHole(x + 1, y + 1)){
            setTexture(wallTextures[19]);
        }
        else if (board.isHole(x - 1, y) && board.isHole(x + 1, y) && !board.isHole(x, y + 1) &&
                board.isHole(x, y - 1) && !board.isHole(x- 1, y -1)&& !board.isHole(x + 1, y -1)){
            setTexture(wallTextures[20]);
        }
        else if (board.isHole(x - 1, y) && !board.isHole(x + 1, y) && board.isHole(x, y + 1) &&
                board.isHole(x, y - 1) && !board.isHole(x- 1, y - 1)&& !board.isHole(x - 1, y + 1)){
            setTexture(wallTextures[21]);
        }
        else if (board.isHole(x - 1, y) && board.isHole(x + 1, y) && board.isHole(x, y + 1) &&
                board.isHole(x, y - 1) && !board.isHole(x- 1, y - 1)&& !board.isHole(x - 1, y + 1)
                && !board.isHole(x + 1, y - 1)&& !board.isHole(x +- 1, y + 1)
        ){
            setTexture(wallTextures[22]);
        }
        else if (!board.isHole(x - 1, y) && board.isHole(x + 1, y) && board.isHole(x, y + 1) &&
                board.isHole(x, y - 1) && !board.isHole(x + 1, y + 1) && !board.isHole(x + 1, y - 1)){
            setTexture(wallTextures[23]);
        }
        else if (board.isHole(x - 1, y) && board.isHole(x + 1, y) && board.isHole(x, y + 1) &&
                !board.isHole(x, y - 1) && !board.isHole(x - 1, y + 1) && !board.isHole(x + 1, y + 1)){
            setTexture(wallTextures[24]);
        }
    }



    public void draw(GameCanvas canvas){
        super.draw(canvas, draw_scale, draw_scale);
    }

}
