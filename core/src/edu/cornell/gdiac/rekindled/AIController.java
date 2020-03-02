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

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
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
     *
     * @return the enemy of this AIController
     */
    public Enemy getEnemy() { return this.enemy; }

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
    }

    /**
     * Returns (delX, delY) representing next direction for this enemy to move
     */
    public Vector2 getNextDirection(){

        Queue<ArrayList<Integer>> q = new LinkedList<>();
        int[][][] parent = new int[board.getWidth()][board.getHeight()][2];

        // Set Goal
        setGoals();
        board.clearVisited();

        // Get the tile for this enemy
        int sx = board.screenToBoard(enemy.getPosition().x);
        int sy = board.screenToBoard(enemy.getPosition().y);
        ArrayList<Integer> s = new ArrayList<Integer>();
        s.add(sx); s.add(sy);
        board.setVisited(sx,sy); //visit s
        q.add(s);




        //BFS
        int[] reachedGoal = new int[2];
        reachedGoal[0] = -1;
        reachedGoal[1] = -1;
        while(!q.isEmpty()){
            s = q.poll(); //get first element in queue
            int xIdx = s.get(0);
            int yIdx = s.get(1);
            if(board.isGoal(xIdx,yIdx)){
                reachedGoal[0] = xIdx;
                reachedGoal[1] = yIdx;
                break;
            }
            //add each neighbor of s to queue if not visited yet
            if(!board.isEnemyMovable(xIdx+1,yIdx) && !board.isVisited(xIdx+1,yIdx)
            && board.isSafeAt(xIdx + 1, yIdx)){ //right
                board.setVisited(xIdx+1,yIdx);
                ArrayList<Integer> r = new ArrayList<Integer>();
                r.add(xIdx+1); r.add(yIdx);
                q.add(r);
                parent[xIdx+1][yIdx][0] = xIdx;
                parent[xIdx+1][yIdx][1] = yIdx;
            }
            if(!board.isEnemyMovable(xIdx-1,yIdx) && !board.isVisited(xIdx-1,yIdx)
                    && board.isSafeAt(xIdx - 1, yIdx)){ //left
                board.setVisited(xIdx-1,yIdx);
                ArrayList<Integer> l = new ArrayList<Integer>();
                l.add(xIdx-1); l.add(yIdx);
                q.add(l);
                parent[xIdx-1][yIdx][0] = xIdx;
                parent[xIdx-1][yIdx][1] = yIdx;
            }
            if(!board.isEnemyMovable(xIdx,yIdx+1) && !board.isVisited(xIdx,yIdx+1)
                    && board.isSafeAt(xIdx, yIdx + 1)){ //up
                board.setVisited(xIdx,yIdx+1);
                ArrayList<Integer> u = new ArrayList<Integer>();
                u.add(xIdx); u.add(yIdx+1);
                q.add(u);
                parent[xIdx][yIdx+1][0] = xIdx;
                parent[xIdx][yIdx+1][1] = yIdx;
            }
            if(!board.isEnemyMovable(xIdx,yIdx-1) && !board.isVisited(xIdx,yIdx-1)
                    && board.isSafeAt(xIdx, yIdx - 1)){ //down
                board.setVisited(xIdx,yIdx-1);
                ArrayList<Integer> d = new ArrayList<Integer>();
                d.add(xIdx); d.add(yIdx-1);
                q.add(d);
                parent[xIdx][yIdx-1][0] = xIdx;
                parent[xIdx][yIdx-1][1] = yIdx;
            }
        }
        int[] root = {sx,sy};
        int[] prev = reachedGoal;

        // If goal not found, don't move
        if (reachedGoal[0] == -1 && reachedGoal[1] == -1){
            return new Vector2(0, 0);
        }

        while(!Arrays.equals(prev,root) && !Arrays.equals(parent[prev[0]][prev[1]],root))  {
            prev = parent[prev[0]][prev[1]];
        }

        return new Vector2(prev[0] - root[0], prev[1] - root[1]);
    }

    /**
     * Sets goal for this enemy.
     * If player is on unlit tile, goal is player pos
     * Else, goal is nearest unlit tile to player
     */
    private void setGoals(){
        Queue<ArrayList<Integer>> q = new LinkedList<>();
        ArrayList<Integer> s = new ArrayList<Integer>();

        // Set Current Position to Visited
        int px = board.screenToBoard(player.getPosition().x);
        int py = board.screenToBoard(player.getPosition().y);
//        System.out.println(px + "," + py);
        s.add(px); s.add(py);
        board.setVisited(px,py); //visit s
        q.add(s);

        while(!q.isEmpty()) {
            s = q.poll(); //get first element in queue
            int xIdx = s.get(0);
            int yIdx = s.get(1);
            if (!board.isLitTileBoard(xIdx, yIdx)) {
                board.setGoal(xIdx, yIdx);
            }
            else {
                //add each neighbor of s to queue if not visited yet
                if (!board.isObstructedBoard(xIdx + 1, yIdx) && !board.isVisited(xIdx + 1, yIdx)
                        && board.isSafeAt(xIdx + 1, yIdx)) { //right
                    board.setVisited(xIdx + 1, yIdx);
                    ArrayList<Integer> r = new ArrayList<Integer>();
                    r.add(xIdx + 1);
                    r.add(yIdx);
                    q.add(r);
                }
                if (!board.isObstructedBoard(xIdx - 1, yIdx) && !board.isVisited(xIdx - 1, yIdx)
                        && board.isSafeAt(xIdx - 1, yIdx)) { //left
                    board.setVisited(xIdx - 1, yIdx);
                    ArrayList<Integer> l = new ArrayList<Integer>();
                    l.add(xIdx - 1);
                    l.add(yIdx);
                    q.add(l);
                }
                if (!board.isObstructedBoard(xIdx, yIdx + 1) && !board.isVisited(xIdx, yIdx + 1)
                        && board.isSafeAt(xIdx, yIdx + 1)) { //up
                    board.setVisited(xIdx, yIdx + 1);
                    ArrayList<Integer> u = new ArrayList<Integer>();
                    u.add(xIdx);
                    u.add(yIdx + 1);
                    q.add(u);
                }
                if (!board.isObstructedBoard(xIdx, yIdx - 1) && !board.isVisited(xIdx, yIdx - 1)
                        && board.isSafeAt(xIdx, yIdx - 1)) { //down
                    board.setVisited(xIdx, yIdx - 1);
                    ArrayList<Integer> d = new ArrayList<Integer>();
                    d.add(xIdx);
                    d.add(yIdx - 1);
                    q.add(d);
                }
            }
        }
//        System.out.println("Result: " + result.get(0) + "," + result.get(1));
    }

    /** Moves this enemy */
    public void move(){
        if (board.isCenterOfTile(enemy.getPosition())) {
            enemy.setMoving(false);
        }

        // Calculate direction to move
        if (!board.isLitTile(enemy.getPosition()) && hasLoS() &&
                (board.isLitTile(player.getPosition()) || board.isDimTile(player.getPosition()))) {
            Vector2 dir = getNextDirection();
            enemy.move(dir.x * (board.getTileSize() + board.getTileSpacing()),
                    (board.getTileSize() + board.getTileSpacing()) * dir.y);
        }

        if (board.isObstructed(enemy.getGoal()) || board.isLitTile(enemy.getGoal())) {
            enemy.setMoving(false);
        }
        enemy.update();
        board.clearMarks();

    }

    private boolean hasLoS(){
        int idx = 0;
        boolean result = true;
        while (idx < board.walls.length - 1){
            float[] vertices = new float[] {
                    board.walls[idx] + .03f, board.walls[idx+1] + .03f,
                    board.walls[idx] + .03f, board.walls[idx+1] + .97f,
                    board.walls[idx] + .97f, board.walls[idx + 1] + .03f,
                    board.walls[idx] + .97f, board.walls[idx + 1] + .97f
            };
            Polygon poly = new Polygon(vertices);
            Vector2 playerPos = board.screenToBoard(player.getPosition());
            Vector2 enemyPos = board.screenToBoard(enemy.getPosition());
            if (Intersector.intersectSegmentPolygon(playerPos,enemyPos, poly)){
                result = false;
            }
            idx +=2;
        }
        return result;
    }


    //#region PUT YOUR CODE HERE

    //#endregion
}
