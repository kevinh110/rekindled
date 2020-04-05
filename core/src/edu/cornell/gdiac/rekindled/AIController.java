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

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

/**
 * InputController corresponding to AI control.
 */
public class AIController extends Entity_Controller {
    // taken from InputController in AI Lab
    // Constants for the control codes
    // We would normally use an enum here, but Java enums do not bitmask nicely
    /** Do not do anything */

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
    private Move_Direction move; // A ControlCode
    /** The number of ticks since we started this controller */
    private long ticks;

    private Enemy[] enemies;

    // Custom fields for AI algorithms
    public float delta;

    /**
     * Creates an AIController for an enemy.
     *
     * @param enemy The enemy
     * @param board The game board (for pathfinding)
     * @param player The player (for targetting)
     */
    public AIController(Enemy enemy, Board board, Player player, Enemy[] enemies) {
        this.enemy = enemy;
        this.board = board;
        this.player = player;
        this.enemies = enemies;

        state = FSMState.SPAWN;
        move = Move_Direction.NO_MOVE;
        ticks = 0;

        // Select an initial target
        target = false;
//        selectTarget();
    }

    public AIController(Enemy enemy, Board board, Player player, Enemy[] enemies, float del){
        this(enemy, board, player, enemies);
        delta = del;
    }

    public FSMState getState(){
        return this.state;
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
    public Move_Direction getAction() {
        // Increment the number of ticks.
        ticks++;

        // Do not need to rework ourselves every frame. Just every 10 ticks.
        if( ticks % 10 == 0) {
            // Process the FSM
            changeStateIfApplicable();

            // Pathfinding
            setGoals();
            move = get_Next_Direction();
        }

        return move;
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
        System.out.println("Changing state");
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
    public Move_Direction get_Next_Direction(){
        if(board.isLitTile(enemy.getPosition()) || !hasLoS()){ return Move_Direction.NO_MOVE; }

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
            && board.isSafeAt(xIdx + 1, yIdx) && noEnemyAt(xIdx +1, yIdx)){ //right
                board.setVisited(xIdx+1,yIdx);
                ArrayList<Integer> r = new ArrayList<Integer>();
                r.add(xIdx+1); r.add(yIdx);
                q.add(r);
                parent[xIdx+1][yIdx][0] = xIdx;
                parent[xIdx+1][yIdx][1] = yIdx;
            }
            if(!board.isEnemyMovable(xIdx-1,yIdx) && !board.isVisited(xIdx-1,yIdx)
                    && board.isSafeAt(xIdx - 1, yIdx) && noEnemyAt(xIdx -1, yIdx)){ //left
                board.setVisited(xIdx-1,yIdx);
                ArrayList<Integer> l = new ArrayList<Integer>();
                l.add(xIdx-1); l.add(yIdx);
                q.add(l);
                parent[xIdx-1][yIdx][0] = xIdx;
                parent[xIdx-1][yIdx][1] = yIdx;
            }
            if(!board.isEnemyMovable(xIdx,yIdx+1) && !board.isVisited(xIdx,yIdx+1)
                    && board.isSafeAt(xIdx, yIdx + 1) && noEnemyAt(xIdx, yIdx+1)){ //up
                board.setVisited(xIdx,yIdx+1);
                ArrayList<Integer> u = new ArrayList<Integer>();
                u.add(xIdx); u.add(yIdx+1);
                q.add(u);
                parent[xIdx][yIdx+1][0] = xIdx;
                parent[xIdx][yIdx+1][1] = yIdx;
            }
            if(!board.isEnemyMovable(xIdx,yIdx-1) && !board.isVisited(xIdx,yIdx-1)
                    && board.isSafeAt(xIdx, yIdx - 1) && noEnemyAt(xIdx, yIdx-1)){ //down
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
            return Move_Direction.NO_MOVE;
        }

        while(!Arrays.equals(prev,root) && !Arrays.equals(parent[prev[0]][prev[1]],root))  {
            prev = parent[prev[0]][prev[1]];
        }
        if(root[0] > prev[0]){
            return Move_Direction.MOVE_LEFT;
        } else if(root[0] < prev[0]){
            return Move_Direction.MOVE_RIGHT;
        } else if(root[1] > prev[1]){
            return Move_Direction.MOVE_DOWN;
        } else if(root[1] < prev[1]){
            return Move_Direction.MOVE_UP;
        } else {
            return Move_Direction.NO_MOVE;
        }
//        return new Vector2(prev[0] - root[0], prev[1] - root[1]);
    }

    private boolean noEnemyAt(int x, int y){
        for (Enemy e : enemies){
            if (e != enemy){
                if (board.screenToBoard(e.getPosition().x) == x && board.screenToBoard(e.getPosition().y) == y){
                    return false;
                }
            }
        }
        return true;
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
    }

    public void move(){
        state = FSMState.WANDER; // temp code; state change on yet implemented

        switch (state) {
            case WANDER:
                int[] goal = enemy.getWanderGoal();
                Vector2 pos = enemy.getPosition();
                if (pos.x == goal[0] && pos.y == goal[1]){
                    enemy.updateWanderGoal();
                    goal = enemy.getWanderGoal();
                }
                enemy.moveOnTile(goal[0], goal[1], delta);
        }
    }


    /** Moves this enemy */
//    public void move(){
//        if (board.isCenterOfTile(enemy.getPosition())) {
//            enemy.setMoving(false);
//        }
//
//        // Calculate direction to move
//        if (!board.isLitTile(enemy.getPosition()) && hasLoS()) {
//            Vector2 dir = getNextDirection();
//            enemy.move(dir.x * (board.getTileSize() + board.getTileSpacing()),
//                    (board.getTileSize() + board.getTileSpacing()) * dir.y);
//        }
//
//        if (board.isObstructed(enemy.getGoal()) || board.isLitTile(enemy.getGoal())) {
//            enemy.setMoving(false);
//        }
//        enemy.update();
//        board.clearMarks();
//    }

    private boolean hasLoS(){
        int idx = 0;
        boolean result = true;
        while (idx < board.walls.length - 1){
            float[] vertices = new float[] {
//                    board.walls[idx] + .01f, board.walls[idx+1] + .01f,
//                    board.walls[idx] + .01f, board.walls[idx+1] + .99f,
//                    board.walls[idx] + .99f, board.walls[idx + 1] + .01f,
//                    board.walls[idx] + .99f, board.walls[idx + 1] + .99f

                    board.walls[idx] , board.walls[idx+1] ,
                    board.walls[idx], board.walls[idx+1] + 1f,
                    board.walls[idx] + 1f, board.walls[idx + 1] ,
                    board.walls[idx] + 1f, board.walls[idx + 1] + 1f
            };
            Polygon poly = new Polygon(vertices);
            Vector2 playerPos = new Vector2(board.screenToBoard(player.getPosition().x) + .5f,
                    board.screenToBoard(player.getPosition().y) + .5f);
            Vector2 enemyPos = new Vector2(board.screenToBoard(enemy.getPosition().x) + .5f,
                    board.screenToBoard(enemy.getPosition().y) + .5f);
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
