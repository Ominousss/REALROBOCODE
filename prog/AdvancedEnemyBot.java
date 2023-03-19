package prog;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class AdvancedEnemyBot extends EnemyBot{

    private double _x;
    private double _y;


    public AdvancedEnemyBot() {
        Reset();
    }

    public void Update(ScannedRobotEvent event, Robot robot) {
        super.Update(event);
        double absBearingDeg = (robot.getHeading() + event.getBearing());
        if(absBearingDeg < 0) absBearingDeg += 360;

        _x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * event.getDistance(); //Trigonometry. Thank god for unity and sonic physics :^) Exact same way done for slopes check unity project A
        _y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * event.getDistance(); //Check Sonic retro on physics(I have no idea how I figured this out back in the day I don't even fully understand it now LMAO)

    }

    @Override
    public void Reset() {
        super.Reset();
        _x = 0;
        _y = 0;
    }
    public double getX() {
        return _x;
    }

    public double getFutureX(long when) {
        return _x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when; //GET ME PAPER I CAN PROVE THIS EZ
    }

    public double getFutureY(long when) {
        return _y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
    }

    public double getY() {
        return _y;
    }
}
