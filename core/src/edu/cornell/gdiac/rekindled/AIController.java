/*
 * AIController.java
 *
 * This class is an inplementation of InputController that uses AI and pathfinding
 * algorithms to determine the choice of input.
 *
 *
 * Based on AI Lab by Walker M. White and Cristian Zaloj
 * LibGDX version, 1/24/2015
 */
package edu.cornell.gdiac.rekindled;

import com.badlogic.gdx.math.Vector2;

import java.util.*;

/**
 * InputController corresponding to AI control.
 */
public class AIController {
    // taken from InputController in AI Lab
    // Constants for the control codes
    // We would normally use an enum here, but Java enums do not bitmask nicely
    /** Do not do anything */
    public static final int CONTROL_NO_ACTION  = 0x00;
    /** Move the ship to the left */
    public static final int CONTROL_MOVE_LEFT  = 0x01;
    /** Move the ship to the right */
    public static final int CONTROL_MOVE_RIGHT = 0x02;
    /** Move the ship to the up */
    public static final int CONTROL_MOVE_UP    = 0x04;
    /** Move the ship to the down */
    public static final int CONTROL_MOVE_DOWN  = 0x08;
    /** Fire the ship weapon */
    public static final int CONTROL_FIRE 	   = 0x10;

    /**
     * Enumeration to encode the finite state machine.
     */
    private static enum FSMState {
        /** The enemy just spawned */
        SPAWN,
        /** The enemy is patrolling around without a target */
        WANDER,
        /** The enemy has a target, but must get closer */
        CHASE,
        /** The enemy is inside a lit-up light source */
        LIT
    }

    // Constants for chase algorithms
    /** How close a target must be for us to chase it */
    private static final int CHASE_DIST  = 9;
    /** How close a target must be for us to attack it */
    private static final int ATTACK_DIST = 4;

    // Instance Attributes
    /** The enemy being controlled by this AIController */
    private Enemy enemy;
    /** The game board; used for pathfinding */
    private Board board;
    /** The player */
    private Player player;
    /** Whether the player is currently a target or not */
    private boolean target;
    /** The enemy's current state in the FSM */
    private FSMState state;
    /** The enemy's next action. */
    private int move; // A ControlCode
    /** The number of ticks since we started this controller */
    private long ticks;

    // Custom fields for AI algorithms

    /**
     * Creates an AIController for an enemy.
     *
     * @param enemy The enemy
     * @param board The game board (for pathfinding)
     * @param player The player (for targetting)
     */
    public AIController(Enemy enemy, Board board, Player player) {
        this.enemy = enemy;
        this.board = board;
        this.player = player;

        state = FSMState.SPAWN;
        move  = CONTROL_NO_ACTION;
        ticks = 0;

        // Select an initial target
        target = false;
//        selectTarget();
    }

    /**
     * Returns the action selected by this InputController
     *
     * The returned int is a bit-vector of more than one possible input
     * option. This is why we do not use an enumeration of Control Codes;
     * Java does not (nicely) provide bitwise operation support for enums.
     *
     * This function tests the environment and uses the FSM to chose the next
     * action of the ship. This function SHOULD NOT need to be modified.  It
     * just contains code that drives the functions that you need to implement.
     *
     * @return the action selected by this InputController
     */
    public int getAction() {
        // Increment the number of ticks.
        ticks++;

        // Do not need to rework ourselves every frame. Just every 10 ticks.
        if( ticks % 10 == 0) {
            // Process the FSM
            changeStateIfApplicable();

            // Pathfinding
        }

        int action = move;

        return action;
    }

    // FSM Code for Targeting (MODIFY ALL THE FOLLOWING METHODS)

    /**
     * Change the state of the ship.
     *
     * A Finite State Machine (FSM) is just a collection of rules that,
     * given a current state, and given certain observations about the
     * environment, chooses a new state. For example, if we are currently
     * in the ATTACK state, we may want to switch to the CHASE state if the
     * target gets out of range.
     */
    private void changeStateIfApplicable() {
        // Add initialization code as necessary
        Vector2 current_pos = enemy.getPosition();

        // Next state depends on current state.
        switch (state) {
            case SPAWN: // Do not pre-empt with FSMState in a case
                // Insert checks and spawning-to-??? transition code here
                //#region PUT YOUR CODE HERE
                if(board.isLitLightSource(current_pos)){
                    state = FSMState.LIT;
                } else {
                    if (target) {
                        // has target
                        state = FSMState.CHASE;
                    } else {
                        // has no target, start WANDERing
                        state = FSMState.WANDER;
                    }
                }
                //#endregion
                break;

            case WANDER: // Do not pre-empt with FSMState in a case
                // Insert checks and moving-to-??? transition code here
                //#region PUT YOUR CODE HERE
                // select target
                if(board.isLitLightSource(current_pos)){
                    state = FSMState.LIT;
                } else {
                    selectTarget();
                    if (target) {
                        // has target
                        state = FSMState.CHASE;
                    } // else: no target, stay in WANDER
                }
                //#endregion
                break;

            case CHASE: // Do not pre-empt with FSMState in a case
                // insert checks and chasing-to-??? transition code here
                //#region PUT YOUR CODE HERE
                if(board.isLitLightSource(current_pos)){
                    state = FSMState.LIT;
                } else {
                    if (!target) {
                        // has no target
                        state = FSMState.WANDER;
                    }   // else: has target, keep chasing
                }
                //#endregion
                break;

            case LIT: // Do not pre-empt with FSMState in a case
                // insert checks and attacking-to-??? transition code here
                //#region PUT YOUR CODE HERE

                if(!board.isLitLightSource(current_pos)){
                    state = FSMState.WANDER;
                } //else stay LIT
                //#endregion
                break;

            default:
                // Unknown or unhandled state, should never get here
                assert (false);
                state = FSMState.WANDER; // If debugging is off
                break;
        }
//		System.out.println("++changed state: "+state);
    }

    /**
     * Acquire a target to attack (and put it in field target).
     *
     * Insert your checking and target selection code here. Note that this
     * code does not need to reassign <c>target</c> every single time it is
     * called. Like all other methods, make sure it works with any number
     * of players (between 0 and 32 players will be checked). Also, it is a
     * good idea to make sure the ship does not target itself or an
     * already-fallen (e.g. inactive) ship.
     */
    private void selectTarget() {
    }

    // Add any auxiliary methods or data structures here
    //#region PUT YOUR CODE HERE

    //#endregion
}
