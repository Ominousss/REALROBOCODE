package prog;

import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

public class EnemyDodgingMovement {

    private static final int  _activityAreaZone = 20;
    private static final int _fov = 50;
    private static final int _dangerZone = _fov * 3;

    private final AdvancedRobot _robot;
    private final Rectangle _activityArea;

    public EnemyDodgingMovement(AdvancedRobot robot) {
        _robot = robot;

        //Maths
        _activityArea = new Rectangle(_activityAreaZone, _activityAreaZone,
                (int)robot.getBattleFieldWidth() - _activityAreaZone * 2,
                (int)robot.getBattleFieldHeight() - _activityAreaZone * 2);
    }

    private double CalculateAverageDistance(Point2D.Double point, Collection<Point2D.Double> enemyLocations) {
        double distanceSum = 0;
        int closeEnemyCount = 0;
        for(Point2D.Double p : enemyLocations) {
            final double distance = p.distance(point);
            //if enemy distance from us is more than danger zone cnotinue
            if(p.distance(_robot.getX(), _robot.getY()) > _dangerZone) {
                continue; //Skips rest of for loop
            }
            distanceSum += distance;
            closeEnemyCount++;
        }
        return distanceSum / (double) (closeEnemyCount) > 0 ? closeEnemyCount : 1;
    }

    private Collection<EDMPoint> GetPoints (double dist, Collection<Point2D.Double> enemiesLocation) {
        final Collection<EDMPoint> points = new LinkedList<EDMPoint>();
        final Point2D.Double myPos = new Point2D.Double(_robot.getX(), _robot.getY());
        for(double angle = 0; angle < Math.PI * 2; angle += Math.PI / 9 ) {
            final EDMPoint p = new EDMPoint(myPos.x + Math.sin(angle) * dist, myPos.y + Math.cos(angle) * dist);

            if(!_activityArea.contains(p)) {
                continue;
            }
            p._avgDistance =
        }
    }




    //Calculates safest area based on info
    public Point2D.Double GetDestination(Collection<Point2D.Double> enemiesLocation) {
        final Collection<EDMPoint> points =
    }



}
