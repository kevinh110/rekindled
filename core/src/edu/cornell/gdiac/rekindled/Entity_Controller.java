package edu.cornell.gdiac.rekindled;

public class Entity_Controller {
    /**The last button pressed by the player*/
    public static enum Move_Direction {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        MOVE_DIAG_UP_RIGHT,
        MOVE_DIAG_UP_LEFT,
        MOVE_DIAG_DOWN_RIGHT,
        MOVE_DIAG_DOWN_LEFT,
        NO_MOVE,
    }
    public Move_Direction get_Next_Direction(){
        return Move_Direction.NO_MOVE;
    }
}
