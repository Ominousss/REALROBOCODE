package omi;
import robocode.*;
import robocode.Robot;
//import java.awt.Color;
import java.awt.*;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * OmiBot - a robot by (your name here)
 */
public class OmiBot extends Robot
{
	    private enum State {
        attack,
        defense,
        switchingSide

    }

    private enum Side {
        left,
        right,
    }

    private State _state;
    private Side _side;
    public void run() {
        // Initialization of the robot should be put here

        // After trying out your robot, try uncommenting the import at the top,
        // and the next line:

        setColors(Color.red,Color.blue,Color.green); // body,gun,radar

        // Robot main loop
        while(_state.equals(State.attack)) {
            // Replace the next 4 lines with any behavior you would like
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
        while(_state.equals(State.defense)) {
        ahead(RNG(1, 5));
        if(RNG(1, 2) == 1) turnRight(RNG(10, 50));
        else turnLeft(RNG(10, 50));
        }
    }

    private int RNG(int start, int end) {
        return ((int) Math.random() * (end - start)) + start;
    }

    private void SideHandler() { //Ik redundant if statement but it makes it more readable for me
        if(_side.equals(Side.left) && getX() > 0.5 && (getHeading() > 0 && getHeading() < 180) ) {
                if(getHeading() < 90) { //If player is in the 1st quartile of circle, turn right(shortest distance) till he is perpendicular to left wall.
                    turnRight(getHeading() + 90);
                }
                if(getHeading() > 90) { //Inverse of other
                    turnLeft(180 - getHeading() + 90);
                }
        }

    }

    private void SetState(State state) {
        _state = state;
    }

    private void SetSide(Side side) {
        _side = side;
    }

    private void StateHandler() {
        State lastState = _state;
        if(_side == Side.left && getX() > 0.5 || _side == Side.right && getX() < 0.5) {
            _state = State.switchingSide;
        }
        _state = lastState;
    }

    private void RadarHandler() {

    }

    private void AttackStateBehaviourOnScannedRobot(ScannedRobotEvent e) {
        double lastDistanceFromEnemy = e.getDistance();
        if(lastDistanceFromEnemy < e.getDistance()) {
            setAdjustRadarForRobotTurn(false);
            double headingDiff = getRadarHeading() - getHeading();
            while(headingDiff > 0) {
                turnRight(1);
            }
            while(headingDiff < 0) {
                turnLeft(1);
            }
        }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        while(Math.abs(getGunHeading() - getRadarHeading()) > 8) { //
            turnRadarRight(5);
        }
        while(Math.abs(getGunHeading() - getRadarHeading()) < 8 && Math.abs(getGunHeading() - getRadarHeading()) > 5) {
            turnRadarRight(1);
        }
        setAdjustRadarForGunTurn(true);
        fire(1);
        if(e.getDistance() < 100) {
            fire(3);
        }
    }

    //public boolean RadarOnRobot(ScannedRobotEvent event) {
    //}

    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        // Replace the next line with any behavior you would like
        back(10);
    }

    /**
     * onHitWall: What to do when you hit a wall
     */
    public void onHitWall(HitWallEvent e) {
        // Replace the next line with any behavior you would like

        back(20);
    }
}
