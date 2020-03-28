/*
 * CollisionController.java
 *
 * Unless you are making a point-and-click adventure game, every single
 * game is going to need some sort of collision detection.  In a later
 * lab, we will see how to do this with a physics engine. For now, we use
 * custom physics.
 *
 * This class is an example of subcontroller.  A lot of this functionality
 * could go into GameEngine (which is the primary controller).  However, we
 * have factored it out into a separate class because it makes sense as a
 * self-contained subsystem.  Unlike Lab 1, this controller stores a lot
 * information as fields.  This is to keep us from having to pass the same
 * parameters over and over again.
 *
 * Author: Walker M. White, Cristian Zaloj
 * Based on original AI Game Lab by Yi Xu and Don Holden, 2007
 * LibGDX version, 1/24/2015
 */
package edu.cornell.gdiac.rekindled;

import java.util.Random;
import com.badlogic.gdx.math.*;


/**
 * Class to handle basic collisions in the game.
 *
 * This is the simplest of physics engines.  In later labs, we will see how to work 
 * with more interesting engines.
 *
 * As a major subcontroller, this class must have a reference to all the models.
 */
public class CollisionController {
    /** Reference to the game board */
    public Board board;
    /** Reference to the player */
    public Player player;
    /** Reference to all enemies */
    public Enemy[] enemies;

    /** Cache attribute for calculations */
    private Vector2 tmp;
    /** Random number generator for nudging */
    private Random random;

    /**
     * Creates a CollisionController for the given models.
     *
     * @param b The game board
     * @param e The list of enemies
     * @param p The player
     */
    public CollisionController(Board b, Enemy[] e, Player p) {
        board = b;
        enemies = e;
        player = p;

        tmp = new Vector2();
        random = new Random();
    }

    /**
     * Updates all of the ships and photons, moving them forward.
     *
     * This is part of the collision phase, because movement can cause collisions!
     * That is why we do not combine this with the gameply controller. When we study
     * the sense-think-act cycle later, we will see another reason for this design.
     */
    public void update() {
    }

    /**
     * Returns the manhattan distance between two points
     *
     * @return the manhattan distance between two points
     */
    private float manhattan(float x0, float y0, float x1, float y1) {
        return Math.abs(x1 - x0) + Math.abs(y1 - y0);
    }

}