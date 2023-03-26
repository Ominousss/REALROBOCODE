package prog;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

//region Interface
interface IRobotPart
{
    void init();

    void move();
}
//endregion


public class Penicillin extends AdvancedRobot {
    private enum State {

        circling,
        evade,
        hunt

    }
    private State _state;
    private Point2D.Double _coords;
    private byte _moveDirection = 1;
    private int _tooCloseToWall = 0;
    private byte _scanDirection = 1;
    private final int wallMargin_ = 60;
    private AdvancedEnemyBot _enemy = new AdvancedEnemyBot();
    private IRobotPart[] _parts = new IRobotPart[3];
    private PartStateFactory _partFactory;
    private EnemyDodgingMovement _edm;
    private ArrayList<RobotStatus> _enemyStatuses;
    private ArrayList<Point2D.Double> _enemyLocs;


    private static Hashtable _enemies = new Hashtable();
    private static Enemy _target;
    private static Point2D.Double _nextDestination;
    private static Point2D.Double _lastPosition;
    private static Rectangle2D.Double _field;
    private static double _myEnergy;
    private static double _myX;
    private static double _myY;
    private static double _timeSinceLastScan =0;


    public void run() {
        // Initialization of the robot should be put here
        Initialisation();

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


        while(_state.equals(State.circling)) {
            PartsHandler();
            execute();
        }

        while(_state.equals(State.evade)) {
            PartsHandler();
            execute();
        }

        while(_state.equals(State.hunt)) {
            PartsHandler();
            execute();
        }

        //while(_state.equals(State.defense)) {
        //SideHandler();
        //ahead(RNG(25, 80));
        //if(RNG(1, 2) == 1) turnRight(RNG(10, 85));
        //else turnLeft(RNG(10, 85));
        }

    private void PartsHandler() {
        for (int i = 0; true; i = (i + 1) % _parts.length) {
            _parts[i].move();
            if(i==0) execute();

            if(getOthers() > 10) {
                _parts[2] = _partFactory.EvadeTank();
            }
            else if(getOthers() == 1) {
                _parts[2] = _partFactory.HuntTank();
            }

        }
    }

    private void StayAwayFromWalls() {
        addCustomEvent(new Condition("too_close_to_walls") {
            public boolean test() {
                return (//How close to wall logic
                        //Too close to left wall
                        (getX() <= wallMargin_ ||
                                //Too close to right wall
                                getX() >= getBattleFieldWidth() - wallMargin_ ||
                                //Too close to bottom wall
                                getY() <= wallMargin_ ||
                                //Too close to top wall
                                getY() >= getBattleFieldHeight() - wallMargin_)
                );
            }
        });
    }
/*
    @Deprecated
    private void EvadeMovement() {
        for (RobotStatus r : _enemyStatuses) {
        Point2D.Double temp = new Point2D.Double(r.getX(), r.getY());
        if(!_enemyLocs.contains(temp))
            _enemyLocs.add(temp);
        }
        goTo(_edm.GetDestination(_enemyLocs));

    }

 */

    @Deprecated
    private void AggressiveMovement() {
        setTurnRight(_enemy.getBearing() + 90 - (10 * _moveDirection)); //Always place robot perpendicular to enemy
        setAhead(100 * _moveDirection);
        _moveDirection *= -1;

        if(_tooCloseToWall > 0) _tooCloseToWall--;

        if(getVelocity() == 0)
            _moveDirection *= -1;
    }

    private void CircleEnemyLogic() { //taken from Circler - slightly modified to be more accurate and suitable
        StayAwayFromWalls();
        setTurnRight(Helper.normaliseBearing(_enemy.getBearing() + 90 - (15 * _moveDirection))); //Modified online logic by testing

        if(_tooCloseToWall > 0) _tooCloseToWall--; //TIMER LOGIC - BUT THIS ALSO ALLOWS FOR OTHER CODE TO RUN

        if(DistTo(_enemy.getX(), _enemy.getY()) < 10) {
            setBack(-150 * _moveDirection);
        }
        //Unpredictability logic
        if(getTime() % 20  == 0) {
         _moveDirection *= -1;
         setAhead(150 * _moveDirection);
        }
        if (getVelocity() == 0) {
            _moveDirection *= -1;
            setAhead(10000 * _moveDirection);
        }
    }

    private void Initialisation() {
        EvadeInit();
        _partFactory = new PartStateFactory(this);
        _edm = new EnemyDodgingMovement(this);
        _enemyLocs = new ArrayList<Point2D.Double>();

        _parts[0] = new Radar();
        _parts[1] = new Gun();
        _parts[0].init();
        _parts[1].init();
        _parts[2] = _partFactory.EvadeTank();

        _enemy.Reset();
        _coords = new Point2D.Double( getX(), getY() );
        final double _xMiddle = getBattleFieldWidth() / 2;
        final double _yMiddle = getBattleFieldHeight() / 2;

        PartsHandler();
        /*
        if(getX() < _xMiddle) {
            SetSide(Side.left);
        }
        else {
            SetSide(Side.right);
        }
        SetState(State.test);
        SetSide(Side.left);

         */
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
    }
        /*
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

         */

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

    private void StateHandler() {
        State lastState = _state;

        //Switch wont work for some reason
        if(_parts[2] == _partFactory.CircleTank()) {
            _state = State.circling;
        }
        else if(_parts[2] == _partFactory.EvadeTank()) {
            _state = State.evade;
        }
        else if(_parts[2] == _partFactory.HuntTank()) {
            _state = State.hunt;
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        if(!_enemyStatuses.contains(e.getStatus())) {
            _enemyStatuses.add(e.getStatus());
        }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {

        if(_state.equals(State.circling)) {
            CircleOnScannedRobot(e);
            CircleEnemyLogic();
        }

        if(_state.equals(State.evade))
            //EvadeMovement();
            EvadeOnScannedRobot(e);
        execute();
    }

    private void CircleOnScannedRobot(ScannedRobotEvent e) {
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

        //(getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) //Checks that gun isnt on cd to not waste a turn adn checks if gun is nearly finished turning to prevent premature shooting
        // setFire(Math.min(500 / _enemy.getDistance(), 3)); //Further away = less power. Closer = more power. Capped at 3.

        _scanDirection *= -1;
        setTurnRadarRight(360 * _scanDirection * getTime()); //Wobble the radar for info
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
        _moveDirection *= -1;
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
    public void onHitRobot(HitRobotEvent event) {
        if(_state.equals(State.evade))
        EvadeOnHitRobot(event);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if(event.getName().equals(_enemy.getName()))
            _enemy.Reset();
        if(_state.equals(State.evade))
            EvadeOnRobotDeath(event);
    }

    @Override
    public void onCustomEvent(CustomEvent e) {
        if(e.getCondition().getName().equals("too_close_to_walls")) {
            if(_tooCloseToWall <= 0) {
                //Can use a boolean but I understood this better since we use it like a timer
                _tooCloseToWall += wallMargin_;
                //setMaxVelocity(0); //Force stop
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


    //REST OF IDEAS:
    //IF ENEMYDISTANCE > 300 MOVE CLOSER IF ONLY 1 ENEMY LEFT
    //AGGRO MODE 1v1
    //EVADE MODE WHEN BETWEEN 1-10
    //IDK FOR MORE THAN THAT

    //region Inner Classes implementing IRobotPart
    public class Radar implements IRobotPart {

        @Override
        public void init() {
        setAdjustRadarForGunTurn(true);
        }

        @Override
        public void move() {
            if(_state.equals(State.evade))
                EvadeRadar();
                else
            setTurnRadarRight(360);
        }
    }

    public class Gun implements IRobotPart {

        @Override
        public void init() {
        setAdjustGunForRobotTurn(true);
        }

        @Override
        public void move() {

            if(_state.equals(State.evade))
                EvadeGunning();
            else if(getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) //Checks that gun isnt on cd to not waste a turn adn checks if gun is nearly finished turning to prevent premature shooting
                setFire(Math.min(500 / _enemy.getDistance(), 3)); //Further away = less power. Closer = more power. Capped at 3.

        }
    }

    public class CirclingTank implements IRobotPart {

        @Override
        public void init() {
        _state = State.circling;
        setColors(Color.MAGENTA, Color.gray, Color.black);
        }

        @Override
        public void move() {

        }
    }

    public class EvadeTank implements IRobotPart {

        @Override
        public void init() {
            _state = State.evade;
            setColors(Color.yellow, Color.yellow, Color.yellow);
        }

        @Override
        public void move() {
           // if(Math.abs(getX() - getBattleFieldWidth()) < 20) {
                //if(Math.abs(getY() - getBattleFieldHeight()) < 20)
                 //   moveAwayFromCenter();
            EvadeRun();
           // }
        }
    }

    public class HuntTank implements IRobotPart {

        @Override
        public void init() {
            _state = State.circling;
            setColors(Color.black, Color.black, Color.black);
        }

        @Override
        public void move() {

        }
    }


    //endregion


    public void setState(State _state) {
        this._state = _state;
    }

    private void goTo(Point2D.Double points) {
        /* Transform our coordinates into a vector */
        points.x -= getX();
        points.y -= getY();

        /* Calculate the angle to the target position */
        double angleToTarget = Math.atan2(points.x, points.y);

        /* Calculate the turn required get there */
        double targetAngle = Utils.normalRelativeAngle(angleToTarget - getHeadingRadians());

        /*
         * The Java Hypot method is a quick way of getting the length
         * of a vector. Which in this case is also the distance between
         * our robot and the target location.
         */
        double distance = Math.hypot(points.x, points.y);

        /* This is a simple method of performing set front as back */
        double turnAngle = Math.atan(Math.tan(targetAngle));
        setTurnRightRadians(turnAngle);
        if(targetAngle == turnAngle) {
            setAhead(distance);
        } else {
            setBack(distance);
        }
    }

    private void moveAwayFromCenter() {
        double centerAngle = Math.atan2(getBattleFieldWidth()/2-getX(), getBattleFieldHeight()/2-getY());
        setTurnRightRadians(Utils.normalRelativeAngle(centerAngle - getHeadingRadians()));
        setBack(100);
    }

    public void EvadeInit()
    {
        _field =new Rectangle2D.Double(36,36,getBattleFieldWidth()-72,getBattleFieldHeight()-72);
        _nextDestination = _lastPosition =new Point2D.Double(getX(),getY());
        _target = new Enemy();
    }
    public void EvadeOnScannedRobot(ScannedRobotEvent e){
        Enemy en=(Enemy) _enemies.get(e.getName());
        if(en==null){
            en=new Enemy();
            _enemies.put(e.getName(),en);
        }
        en.energy=e.getEnergy();
        en.live=true;
        double x= _myX + Math.sin(e.getBearingRadians()+getHeadingRadians())*e.getDistance();
        double y= _myY + Math.cos(e.getBearingRadians()+getHeadingRadians())*e.getDistance();
        en.location=new Point2D.Double(x,y);
        en.distance=e.getDistance();
        en.velocity=e.getVelocity();
        en.heading=e.getHeadingRadians();
        en.bearing=e.getBearingRadians();
        en.name=e.getName();
        if(!_target.live || (en.distance< _target.distance*0.8 && en.energy<= _target.energy*1.1) || (en.energy< _target.energy*0.8 && en.distance<= _target.distance*1.15)){
            _target = en;
        }
        if(_target.name.equals(e.getName())){
            _timeSinceLastScan =0;
        }
    }
    public void EvadeOnRobotDeath(RobotDeathEvent e) {
        Enemy en=(Enemy) _enemies.get(e.getName());
        if(en!=null){
            en.live=false;
        }
    }

    public void EvadeRun() {
        EvadeMovement();
        _myEnergy=getEnergy();
        _myX=getX();
        _myY=getY();
        _timeSinceLastScan++;
        execute();
    }
    public void EvadeOnHitRobot(HitRobotEvent e){
        Enemy en=(Enemy) _enemies.get(e.getName());
        if(en==null){
            en=new Enemy();
            _enemies.put(e.getName(),en);
        }
        en.energy=e.getEnergy();
        en.live=true;
        en.bearing=e.getBearingRadians();
        en.name=e.getName();
        _target =en;
        setTurnGunRightRadians(en.bearing+getHeadingRadians()-getGunHeadingRadians());
        setFire(3);
    }
    private void EvadeMovement(){
        if(getVelocity() == 0)
            setBack(100);
        for(int i=0;i<250;i++){
            if(_target !=null){
                double ang=2*Math.PI*Math.random();
                double dist=150+250*Math.random();
                double testX= _myX +Math.sin(ang)*dist;
                double testY= _myY +Math.cos(ang)*dist;
                if(_field.contains(testX,testY)){
                    if(evaluate(new Point2D.Double(testX,testY))<evaluate(_nextDestination)){
                        _nextDestination.setLocation(testX,testY);
                    }
                }
            }
        }
        double ang=calcAngle(new Point2D.Double(_myX, _myY), _nextDestination)-getHeadingRadians();
        double dist= _nextDestination.distance(_myX, _myY);
        setTurnRightRadians(Utils.normalRelativeAngle(ang));
        setAhead(dist);
        setMaxVelocity((Math.abs(Utils.normalRelativeAngle(ang))>1) ? 2.25 : 8);
    }
   private double evaluate(Point2D.Double destination){
        double risk=0;
        Enumeration e= _enemies.elements();
        while(e.hasMoreElements()){
            Enemy en=(Enemy)e.nextElement();
            if(en.live){
                double eratio=Math.min((en.energy*2)/ _myEnergy,2.5);
                double perp=Math.abs(Math.cos(calcAngle(destination,new Point2D.Double(_myX, _myY))-calcAngle(destination,en.location)));
                risk+=(eratio*(1+perp))/destination.distance(en.location);
            }
        }
        return risk;
    }
    private double calcAngle(Point2D.Double s,Point2D.Double t){
        return Math.atan2(t.getX()-s.getX(),t.getY()-s.getY());
    }

private void EvadeGunning(){
    if(getGunTurnRemainingRadians()<0.01){
        setTurnGunRightRadians(Utils.normalRelativeAngle(_target.bearing+getHeadingRadians()-getGunHeadingRadians()));

    }
    if(getGunHeat()==0 && getEnergy()>5){
        double firePower=Math.min(Math.min(_myEnergy/10,1300/_target.distance),_target.energy/4);
        setFire(firePower);
    }
}
    private void EvadeRadar(){
        if(_timeSinceLastScan<10 && getOthers()==1){
            setTurnRadarRightRadians(Utils.normalRelativeAngle(_target.bearing+getHeadingRadians()-getRadarHeadingRadians())*2);
        }else{
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
    }
    class Enemy{
        boolean live;
        Point2D.Double location;
        double energy;
        double distance;
        double velocity;
        double heading;
        double bearing;
        String name;
    }
}
