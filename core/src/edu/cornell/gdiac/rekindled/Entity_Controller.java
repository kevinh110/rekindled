package edu.cornell.gdiac.rekindled;

public class Entity_Controller {
    /**The last button pressed by the player*/
    public static enum Move_Direction {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        NO_MOVE,
    }
    public Move_Direction get_Next_Direction(){
        return Move_Direction.NO_MOVE;
    }
}
