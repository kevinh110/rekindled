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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Constants;

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
    public static enum FSMState {
        /** The enemy just spawned */
        SPAWN,
        /** The enemy is patrolling around without a target */
        WANDER,
        /** The enemy has a target, but must get closer */
        CHASE,
        /** The enemy goes to last target without searching for a new target */
        GOTO,
        /** the enemy waits for a while before returning to spawn point */
        WAIT,
        /** the enemy returns to its wander path */
        RETURN,
        /** The enemy is inside a lit-up light source */
        LIT,
        /** The enemy is stunned */
        STUNNED,
        /** The enemy pauses before chasing */
        PAUSED;
    }


    // Instance Attributes
    /** The enemy being controlled by this AIController */
    private Enemy enemy;
    /** The game board; used for pathfinding */
    private Board board;
    /** The player */
    private Player player;
    /** The enemy's current state in the FSM */
    private FSMState state;
    /** Timer used for stun/wait/pause */
    private long timer;
    /** The enemies next goal tile. This is a tile adjacent to at least one axis */
    private int[] goal;
    /** The target tile the enemy eventually would like to reach */
    private int[] target;
    /** How many ticks to wait before returning to wander */
    private final int WAIT_TIME = 300;
    /** How many ticks the enemy is stunned for */
    private final int STUN_TIME = 70;
    /** How many times the enemy changes direction while waiting */
    private final int SPIN_NUM = 8;
    /** How long to pause before changing */
    private final int PAUSE_TIME = 65;
    /** the number of ticks between spins for stationary enemies */
    private final int SPIN_TIME = 38;

    private Enemy[] enemies;

    // Custom fields for AI algorithms
    public float delta;

    private boolean tookLight; // If the player took light; jank way to deal with paused->chase edge case
    private boolean threwlight; // If the player threw light; jank way to deal with throw light case

    private Sound alarmSound;
    private static final float ALARM_DELAY = 1.5f;
    private boolean soundPlaying;
    private float soundTimer;

    private Sound enemySound;
    private boolean enemySoundPlaying;
    private float volume;

    /**
     * Creates an AIController for an enemy.
     *
     * @param enemy The enemy
     * @param board The game board (for pathfinding)
     * @param player The player (for targetting)
     */
    public AIController(Enemy enemy, Board board, Player player, Enemy[] enemies, float volume) {
        this.enemy = enemy;
        this.board = board;
        this.player = player;
        this.enemies = enemies;
        this.target = new int[2];
        this.target[0] = (int) enemy.getPosition().x;
        this.target[1] = (int) enemy.getPosition().y;

        state = FSMState.SPAWN;
        Vector2 pos = enemy.getPosition();
        goal = new int[]{(int) pos.x, (int) pos.y};
        timer = 0;

        alarmSound = Gdx.audio.newSound(Gdx.files.internal("sounds/alarm.mp3"));
        enemySound = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy.mp3"));
        this.volume = volume;
    }

    public AIController(Enemy enemy, Board board, Player player, Enemy[] enemies, float del, float vol){
        this(enemy, board, player, enemies, vol);
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
    private void changeStateIfApplicable(boolean playerLit) {
        Vector2 pos = enemy.getPosition();
        // Next state depends on current state.
        if (board.isLitTileBoard((int) pos.x, (int) pos.y)){
            if(state != FSMState.LIT){
                enemy.setIsLit(true);
            }
            state = FSMState.LIT;
            timer = 0;
            threwlight = false;
            return;
        }
        if (enemy.stunned && state != FSMState.STUNNED){
            state = FSMState.STUNNED;
            timer = 0;
            threwlight = false;
            return;
        }
        enemy.setIsLit(false);

        // Handle thrown light case separately
        if (player.insideThrownLight && hasLoSNoConeCheck()){
            threwlight = true;
            timer = 0;
            target[0] = Math.round(player.getPosition().x);
            target[1] = Math.round(player.getPosition().y);
            state = FSMState.PAUSED;
            return;
        }

        switch (state) {
            case SPAWN:
                soundPlaying = false;
                soundTimer = 0;
                if(enemySoundPlaying){
                    enemySound.stop();
                    enemySoundPlaying = false;
                }
                if (hasLoS(playerLit)) {// has LoS
                    state = FSMState.PAUSED;
                } else { // no line of sight; wander
                    state = FSMState.WANDER;
                }
                break;

            case WANDER:
                if(enemySoundPlaying){
                    enemySound.stop();
                    enemySoundPlaying = false;
                }
                soundPlaying = false;
                soundTimer = 0;
                if (hasLoS(playerLit)) {// has target
                    timer = 0;
                    state = FSMState.PAUSED;
                }
                break;

            case PAUSED:
                if(enemySoundPlaying){
                    enemySound.stop();
                    enemySoundPlaying = false;
                }
                if (player.isTakingLights()){
                    tookLight = true;
                }
                timer++;
                if (timer % PAUSE_TIME == 0){
                    if ((!hasLoSNoConeCheck() && !tookLight) || threwlight){ // Took light deals with an edge case
                        state = FSMState.GOTO; // Go to prev goal
                        tookLight = false;
                        threwlight = false;
                    } else {
                        state = FSMState.CHASE;
                    }
                    if(!enemySoundPlaying) {
                        enemySound.loop(volume);
                        enemySoundPlaying = true;
                    }
                    timer = 0;
                }
                break;

            case CHASE:
                soundPlaying = false;
                soundTimer = 0;
                if (!hasLoSNoConeCheck()) {
                    // has no target
                    state = FSMState.GOTO;
                    if(!enemySoundPlaying) {
                        enemySound.loop(volume);
                        enemySoundPlaying = true;
                    }
                }   // else: has target, keep chasing

                break;

            case GOTO:
                soundPlaying = false;
                soundTimer = 0;
                if (hasLoS(playerLit)){
                    state = FSMState.CHASE;
                    if(!enemySoundPlaying) {
                        enemySound.loop(volume);
                        enemySoundPlaying = true;
                    }
                }
                else if (pos.x == target[0] && pos.y == target[1]){
                    state = FSMState.WAIT;
                }

                break;

            case WAIT:
                if(enemySoundPlaying){
                    enemySound.stop();
                    enemySoundPlaying = false;
                }
                if (hasLoS(playerLit)){
                    if (timer < WAIT_TIME / SPIN_NUM){
                        state = FSMState.CHASE;
                    } else {
                        state = FSMState.PAUSED;
                    }
                    if(!enemySoundPlaying) {
                        enemySound.loop(volume);
                        enemySoundPlaying = true;
                    }
                    timer = 0;
                }
                else {
                    timer++;
                    if (timer % WAIT_TIME == 0){
                        timer = 0;
                        state = FSMState.RETURN;
                    }
                }
                break;

            case RETURN:
                if(enemySoundPlaying){
                    enemySound.stop();
                    enemySoundPlaying = false;
                }
                soundPlaying = false;
                soundTimer = 0;
                if (hasLoS(playerLit)){
                    state = FSMState.PAUSED;
                }
                else if (pos.x == target[0] && pos.y == target[1]){
                    enemy.setFacingDirectionToInitial();
                    state = FSMState.WANDER;
                }
                break;

            case LIT:
                if(enemySoundPlaying){
                    enemySound.stop();
                    enemySoundPlaying = false;
                }
                soundPlaying = false;
                soundTimer = 0;
                if(!board.isLitTileBoard((int) pos.x,(int) pos.y)){
                    timer++;
                    if (timer % PAUSE_TIME == 0){
                        state = FSMState.WAIT;
                        timer = 0;
                    }
                }
                break;

            case STUNNED:
                if(enemySoundPlaying){
                    enemySound.stop();
                    enemySoundPlaying = false;
                }
                soundPlaying = false;
                soundTimer = 0;
                timer++;
                if (timer % STUN_TIME == 0){
                    enemy.stunned = false;
                    timer = SPIN_TIME + 1; // Edge Case
                    state = FSMState.WAIT;
                }
                break;

            default:
                // Unknown or unhandled state, should never get here
                assert (false);
                state = FSMState.WANDER; // If debugging is off
                break;
        }
    }

    public int[] getChaseGoal(){
        // Set Goal
        setChaseGoalTiles();
        board.clearVisited();
        return bfs();
    }

    public int[] getWanderGoal() {
        target = enemy.getWanderGoal();
        int posX = Math.round(enemy.getPosition().x);
        int posY = Math.round(enemy.getPosition().y);
        if (posX == target[0] && target[1] < posY) {
            goal[0] = posX;
            goal[1] = posY - 1;
        } else if (posX == target[0] && target[1] > posY) {
            goal[0] = posX;
            goal[1] = posY + 1;
        } else if (posX < target[0] && target[1] == posY) {
            goal[0] = posX + 1;
            goal[1] = posY;
        } else if (posX > target[0] && target[1] == posY) {
            goal[0] = posX - 1;
            goal[1] = posY;
        } else {
            goal[0] = posX;
            goal[1] = posY;
        }
        return goal;
    }

    public int[] getReturnGoal(){
        // Set Goal
        setReturnGoalTiles();
        board.clearVisited();
        goal = bfs();
        enemy.setWanderGoal(target);
        return goal;

    }

    /**
     *  Runs breadth first search
     * @return int[] representing the next direction
     */
    public int[]  bfs(){
        Vector2 pos = enemy.getPosition();
        int[] root = {Math.round(pos.x), Math.round(pos.y)};

        Queue<ArrayList<Integer>> q = new LinkedList<>();
        int[][][] parent = new int[board.getWidth()][board.getHeight()][2];

        // Get the tile for this enemy
        int sx = root[0];
        int sy = root[1];
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
        int[] prev = reachedGoal;

        // If goal not found, don't move
        if (reachedGoal[0] == -1 && reachedGoal[1] == -1){
            target[0] = root[0];
            target[1] = root[1];
            board.clearMarks();
            return root;
        }

        target[0] = reachedGoal[0];
        target[1] = reachedGoal[1];

        while(!Arrays.equals(prev,root) && !Arrays.equals(parent[prev[0]][prev[1]],root))  {
            prev = parent[prev[0]][prev[1]];
        }
        board.clearMarks();
        return prev;
    }

    private boolean noEnemyAt(int x, int y){
        for (Enemy e : enemies){
            if (e != enemy){
                if (e.goal[0] == x && e.goal[1] == y){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets the facing direction for the enemy when it loses sight of the player.
     * Needed to avoid cases where the AI loses sight due to how cone works
     */
    private void setFacingDirWaiting(){
        Vector2 ppos = player.getPosition();
        Vector2 epos = enemy.getPosition();
        float diffX = ppos.x - epos.x;
        float diffY = ppos.y - epos.y;
        if (Math.max(Math.abs(diffX), Math.abs(diffY)) == Math.abs(diffX)){
            if (Math.signum(diffX) == -1){ // player to the left
                enemy.facingDirection = Constants.LEFT;
            }
            else{ // player to the right
                enemy.facingDirection = Constants.RIGHT;
            }
        }else{
            if (Math.signum(diffY) == -1){ // player below
                enemy.facingDirection = Constants.FORWARD;
            }
            else { // player above
                enemy.facingDirection = Constants.BACK;
            }
        }
    }

    /**
     * Spins the enemy clockwise.
     * @param dir the current direction
     */
    private void spinEnemy(int dir) {
        enemy.facingDirection =
                (dir == Constants.BACK) ? Constants.RIGHT :
                        (dir == Constants.RIGHT) ? Constants.FORWARD :
                                (dir == Constants.FORWARD) ? Constants.LEFT :
                                        Constants.BACK;
    }



    /**
     * Sets goal for this enemy.
     * If player is on unlit tile, goal is player pos
     * Else, goal is nearest unlit tile to player
     */
    private void setChaseGoalTiles(){
        Queue<ArrayList<Integer>> q = new LinkedList<>();
        ArrayList<Integer> s = new ArrayList<Integer>();

        // Set Current Position to Visited
        int px = Math.round(player.getPosition().x);
        int py = Math.round(player.getPosition().y);
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

    public void setReturnGoalTiles(){
        int[][] wander_path = enemy.getWanderPath();
        if (wander_path.length == 0){
            board.setGoal(enemy.spawn[0], enemy.spawn[1]);
        } else {
            for (int[] pos : wander_path){
                board.setGoal(pos[0], pos[1]);
            }
        }
    }

    /**
     *
     * @param x x-coord
     * @param y y-coord
     * @return true if both x and y are within 0001 of the nearest integer
     */
    private boolean isCentered(float x, float y){
        if (x - Math.floor(x) < .00001 || Math.ceil(x) - x < .00001){
            return y - Math.floor(y) < .00001 || Math.ceil(y) - y < .00001;
        }
        return false;
    }

    public void move(boolean playerLit, float dt){
        Vector2 pos = enemy.getPosition();
        if (isCentered(pos.x, pos.y)){
            enemy.setPosition(Math.round(pos.x), Math.round(pos.y)); // Center pos to account for slight drift
            pos = enemy.getPosition();
            changeStateIfApplicable(playerLit);
//            System.out.println("State: " + state);
//            System.out.println("Enemy Pos: " + pos);
//            System.out.println("Goal: " + goal[0] + ", " + goal[1]);
//            System.out.println("Player Pos: " + player.getPosition());
//            System.out.println("-----------");
            switch (state) {
                case WANDER:
                    enemy.setWanderSpeed(dt);
                    if (enemy.getWanderPath().length == 0){
                        timer++;
                        if (timer % SPIN_TIME == 0){
                            spinEnemy(enemy.facingDirection);
                        }
                        goal[0] = (int) pos.x;
                        goal[1] = (int) pos.y;
                    } else {
                        if (pos.x == target[0] && pos.y == target[1]){// If goal reached update the pointer
                            enemy.updateWanderGoal();
                        }
                        goal = getWanderGoal();
                        if (!isGoalAccessible()){ // Deal with cases where wander path obstructed
                            goal[0] = (int) pos.x;
                            goal[1] = (int) pos.y;
                            target[0] = (int) pos.x;
                            target[1] = (int) pos.y;
                        }
                    }
                    break;

                case CHASE:
                    enemy.setChaseSpeed(dt);
                    goal = getChaseGoal();
                    if (playerLit && goal[0] == pos.x && goal[1] == pos.y){ // If waiting for player in light, face player
                        setFacingDirWaiting();
                    }
                    break;

                case GOTO:
                    enemy.setChaseSpeed(dt);
                    board.setGoal(target[0], target[1]);
                    goal = bfs();
                    break;

                case PAUSED:
                    setFacingDirWaiting();
                    if (hasLoSNoConeCheck() && !threwlight){
                        getChaseGoal(); // Set target but don't do anything with goal
                    }
                    goal[0] = (int) pos.x;
                    goal[1] = (int) pos.y;
                    break;

                case WAIT:
                    if (timer == 1){
                        setFacingDirWaiting();
                    }
                    if (timer % (WAIT_TIME / SPIN_NUM) == 0){
                        spinEnemy(enemy.facingDirection);
                    }
                    break;

                case STUNNED:
                case LIT:
                    goal[0] = (int) pos.x;
                    goal[1] = (int) pos.y;
                    break;

                case RETURN:
                    enemy.setWanderSpeed(dt);
                    goal = getReturnGoal(); // This really only needs to be calculated once
                    break;
            }
        }
        enemy.goal = this.goal;
        enemy.moveOnTile(goal[0], goal[1], delta);
    }

    private boolean isGoalAccessible(){
        int posX = Math.round(enemy.getPosition().x);
        int posY = Math.round(enemy.getPosition().y);
        if (posX == goal[0] && goal[1] < posY) { // Move down
            return !board.isLitTileBoard(posX, posY - 1);
        }
        else if (posX == goal[0] && goal[1] > posY) { // Move up
            return !board.isLitTileBoard(posX, posY + 1);

        }
        else if (goal[0] > posX && goal[1] == posY) { // Move right
            return !board.isLitTileBoard(posX + 1, posY);
        }
        else if (goal[0] < posX && goal[1] == posY) { // Move left
            return !board.isLitTileBoard(posX - 1, posY);
        }
        else { // Goal malformed
            return false;
        }
    }

    private boolean hasLoS(boolean playerLit){
        if (playerLit) {
            int idx = 0;
            boolean result = true;
            while (idx < board.walls.length - 1) {
                float[] vertices = new float[]{
                        board.walls[idx], board.walls[idx + 1],
                        board.walls[idx], board.walls[idx + 1] + 1f,
                        board.walls[idx] + 1f, board.walls[idx + 1],
                        board.walls[idx] + 1f, board.walls[idx + 1] + 1f
                };
                Polygon poly = new Polygon(vertices);
                Vector2 playerPos = new Vector2(player.getPosition().x + .5f,
                        player.getPosition().y + .5f);
//                System.out.println("Player: " + playerPos);
                Vector2 enemyPos = new Vector2(enemy.getPosition().x + .5f,
                        enemy.getPosition().y + .5f);
//                System.out.println("Enemy: " + enemyPos);
                if (Intersector.intersectSegmentPolygon(playerPos, enemyPos, poly)) {
                    result = false;
                }
                idx += 2;
            }
            return result;
        }
        return enemy.inSight(player.getPosition(), player.getWidth());
    }

    public boolean hasLoSNoConeCheck(){
        int idx = 0;
        boolean result = true;
        while (idx < board.walls.length - 1) {
            float[] vertices = new float[]{
                    board.walls[idx], board.walls[idx + 1],
                    board.walls[idx], board.walls[idx + 1] + 1f,
                    board.walls[idx] + 1f, board.walls[idx + 1],
                    board.walls[idx] + 1f, board.walls[idx + 1] + 1f
            };
            Polygon poly = new Polygon(vertices);
            Vector2 playerPos = new Vector2(player.getPosition().x + .5f,
                    player.getPosition().y + .5f);
            Vector2 enemyPos = new Vector2(enemy.getPosition().x + .5f,
                    enemy.getPosition().y + .5f);
            if (Intersector.intersectSegmentPolygon(playerPos, enemyPos, poly)) {
                result = false;
            }
            idx += 2;
        }
        return result;
    }

    public void playAlarm(){
        if(soundPlaying) {
            soundTimer += Gdx.graphics.getDeltaTime();
            if (soundTimer >= ALARM_DELAY){
                soundTimer = 0;
                soundPlaying = false;
            }
        }
        else {
            alarmSound.play(volume);
            soundPlaying = true;
        }
    }

    public void mute(){
        volume = 0.0f;
        if(enemySoundPlaying){
            enemySound.stop();
        }
    }
    public void unmute(){
        volume = .5f;
        if(enemySoundPlaying){
            enemySound.play(volume);
        }
    }

    public void resetSound(){
        enemySound.stop();
        enemySoundPlaying = false;
    }
}