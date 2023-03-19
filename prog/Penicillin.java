package prog;

import robocode.*;

import java.awt.*;
import java.awt.geom.Point2D;

public class Penicillin extends AdvancedRobot {
    private enum State {
        attack,
        defense,
        switchingSide,
        test

    }

    private enum SubSide {
        none,
        top,
        bottom,
    }

    private enum Side {
        left,
        right,
        none
    }

    private State _state;
    private Side _side;
    private SubSide _subSide;
    private Point2D.Double _coords;
    private byte _moveDirection = 1;
    private int _tooCloseToWall = 0;
    private byte _scanDirection = 1;
    private final int wallMargin_ = 60;
    private AdvancedEnemyBot _enemy = new AdvancedEnemyBot();

    public void run() {
        // Initialization of the robot should be put here
        Initialisation();
        addCustomEvent(new Condition("too_close_to_walls") {
            public boolean test() {
                return (//How close to wall logic
                        //Too close to left wall
                        (       getX() <= wallMargin_ ||
                                //Too close to right wall
                                getX() >= getBattleFieldWidth() - wallMargin_ ||
                                //Too close to bottom wall
                                getY() <= wallMargin_ ||
                                //Too close to top wall
                                getY() >= getBattleFieldHeight() - wallMargin_)
                );
            }
        });

        // After trying out your robot, try uncommenting the import at the top,
        // and the next line:

        setColors(Color.red,Color.blue,Color.green); // body,gun,radar
/*
        // Robot main loop
        while(_state.equals(State.attack)) {
            // Replace the next 4 lines with any behavior you would like
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
        */


        while(_state.equals(State.test)) {
            turnRadarRight(360);
        }

        while(_state.equals(State.defense)) {
        SideHandler();
        //ahead(RNG(25, 80));
        //if(RNG(1, 2) == 1) turnRight(RNG(10, 85));
        //else turnLeft(RNG(10, 85));
        }
    }

    private void AggressiveMovement() {
        setTurnRight(_enemy.getBearing() + 90 - (10 * _moveDirection)); //Always place robot perpendicular to enemy
        setAhead(100 * _moveDirection);
        _moveDirection *= -1;

        if(_tooCloseToWall > 0) _tooCloseToWall--;

        if(getVelocity() == 0)
            _moveDirection *= -1;
    }

    private void CircleEnemyLogic() { //taken from Circler - slightly modified to be more accurate and suitable
        setTurnRight(Helper.normaliseBearing(_enemy.getBearing() + 90 - (15 * _moveDirection))); //Modified online logic by testing

        if(_tooCloseToWall > 0) _tooCloseToWall--; //TIMER LOGIC - BUT THIS ALSO ALLOWS FOR OTHER CODE TO RUN

        //Unpredictability logic
        if(getTime() % 20  == 0) {
         _moveDirection *= -1;
         setAhead(150 * _moveDirection);
        }
    }

    private void Initialisation() {
        setAdjustRadarForRobotTurn(true);
        _enemy.Reset();
        _coords = new Point2D.Double( getX(), getY() );
        final double _xMiddle = getBattleFieldWidth() / 2;
        final double _yMiddle = getBattleFieldHeight() / 2;
        if(getX() < _xMiddle) {
            SetSide(Side.left);
        }
        else {
            SetSide(Side.right);
        }
        SetState(State.test);
        SetSide(Side.left);
    }
    //Cba to put in helper class
    public double DistTo(double x, double y) {
        double dx=x-_coords.x;
        double dy=y-_coords.y;

        return Math.sqrt(dx*dx + dy*dy);
    }

    private void SideHandler() {
        final double _xMiddle = getBattleFieldWidth() / 2;
        final double _yMiddle = getBattleFieldHeight() / 2;
        double distanceToMiddle = (Math.abs(_xMiddle - getX()));
        boolean leftOnRight = _side.equals(Side.left) && getX() > _xMiddle;
        boolean rightOnLeft = _side.equals(Side.right) && getX() < _xMiddle;


        //Found more efficient way to do this using helper method normalizeBearing but would rather not touch this again
        if(leftOnRight) {
            MiddleFromLeftLogic();
        }

         if(rightOnLeft) {
            MiddleFromRightLogic();
        }

        if(leftOnRight || rightOnLeft)
                ahead(distanceToMiddle);
        }

    private void MiddleFromLeftLogic() {
        if (getHeading() < 90) { //If player is in the 1st quartile of circle, turn right(shortest distance) till he is perpendicular to left wall.
            turnLeft(getHeading() + 90);
        }
        if (getHeading() > 90) { //Inverse of other
            turnRight(180 - getHeading() + 90);
        }
    }

    private void MiddleFromRightLogic() {
        if (getHeading() < 270) { //Inversed.
            turnLeft(getHeading() - 90);
        }
        if (getHeading() > 270) {
            turnRight(360 - getHeading() + 90);
        }
    }

    private void SubSideHandler() {
        //if(_subSide == SubSide.top && getY() < 0.5 && (getHeading() > ))
    }

    private void SetState(State state) {
        _state = state;
    }

    private void SetSide(Side side) {
        _side = side;
    }

    private void SetSubSide(SubSide side) {
        _subSide = side;
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
        //Updating enemy on certain conditions
        if(_enemy.None() || e.getName().equals(_enemy.getName()) || e.getDistance() < _enemy.getDistance() - 70)
            _enemy.Update(e, this); //TRACK ENEMY INFO

        double bearingDiff = Math.abs(e.getBearing() - getGunHeading()); //If angle of enemy bearing relative to ours is big, we speed up turning
        /*
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

         */
        //setTurnRight(e.getBearing() + ((e.getVelocity() * 0.2f) * (e.getDistance() *  0.05f)));
        double multiplier = Math.abs(((1 + (e.getVelocity() * 3)) * (1 + (e.getDistance() *  0.5f))));
        //setTurnRight((getHeading() - getRadarHeading()) + (_enemy.getBearing() + bearingDiff)); //getheading - getradarheading to get bearing of scanned robot relative to radar pos not tank pos

        double firePower = Math.min(500/ _enemy.getDistance(), 3);
        double bulletSpeed = 20 - firePower * 3; //RATE ACCORDING TO ROBOCODE FAQ
        //distance = rate * time therefore time equation - TO FIGURE OUT HOW LONG IT WILL TAKE FOR BULLET TO HIT
        long time = (long)(_enemy.getDistance() / bulletSpeed); //casted to long not int due to long being of a higher range

        //Calculating Gun to predicted location
        double futureX = _enemy.getFutureX(time);
        double futureY = _enemy.getFutureY(time);
        double absDeg = Helper.absoluteBearing(getX(), getY(), futureX, futureY);
        //Turning the gun
        setTurnGunRight(Helper.normaliseBearing(absDeg - getGunHeading()));

        if(getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) //Checks that gun isnt on cd to not waste a turn adn checks if gun is nearly finished turning to prevent premature shooting
        setFire(Math.min(500 / _enemy.getDistance(), 3)); //Further away = less power. Closer = more power. Capped at 3.

        _scanDirection *= -1;
        setTurnRadarRight(360 * _scanDirection * getTime()); //Wobble the radar for info

        CircleEnemyLogic();
        execute();
    }

    //public boolean RadarOnRobot(ScannedRobotEvent event) {
    //}

    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // Replace the next line with any behavior you would like
       // back(10);
    }

    /**
     * onHitWall: What to do when you hit a wall
     */
    @Override
    public void onHitWall(HitWallEvent e) {
        // Replace the next line with any behavior you would like
        //back(20);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if(event.getName().equals(_enemy.getName()))
            _enemy.Reset();
    }

    @Override
    public void onCustomEvent(CustomEvent e) {
        if(e.getCondition().getName().equals("too_close_to_walls")) {
            if(_tooCloseToWall <= 0) {
                //Can use a boolean but I understood this better since we use it like a timer
                _tooCloseToWall += wallMargin_;
                setMaxVelocity(0); //Force stop
            }
        }
    }

    /*
    @Deprecated
    public void onCustomEvent(CustomEvent e) {
        if(e.getCondition().getName().equals("too_close_to_walls")) {
            //Switch directions and move away
            _moveDirection *= -1;
            setAhead(10000 * _moveDirection);
        }
    }
     */
}
