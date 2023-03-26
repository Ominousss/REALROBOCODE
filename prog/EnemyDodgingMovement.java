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
            p._avgDistance = CalculateAverageDistance(p, enemiesLocation);
            points.add(p);
        }
        return points;
    }


    //Calculates safest area based on info
    public Point2D.Double GetDestination(Collection<Point2D.Double> enemiesLocation) {
        final Collection<EDMPoint> points = GetPoints(_fov, enemiesLocation);

        double maxAvgDist = 0;
        EDMPoint destination = null;

        for(EDMPoint p : points) {
            double avgDist = CalculateAverageDistance(p, enemiesLocation);
            if(avgDist > maxAvgDist) {
                maxAvgDist = avgDist;
                destination = p;
            }
        }
        return destination;
    }

    public void Paint(Graphics2D graphicsToPaint, Collection<Point2D.Double> enemiesLocations) {
        graphicsToPaint.setColor(Color.white);
        final Collection<EDMPoint> points = GetPoints(_fov, enemiesLocations);
        double maxAvgDist = 0;
        double minAvgDist = Double.MAX_VALUE;
        for(EDMPoint edmPoint : points) {
            if(edmPoint._avgDistance < minAvgDist) {
                minAvgDist = edmPoint._avgDistance;
            }
            if(edmPoint._avgDistance > maxAvgDist) {
                maxAvgDist = edmPoint._avgDistance;
            }
        }

        for(EDMPoint p : points) {
            int radius = 4;
            int gb = (int) (255 * (p._avgDistance - minAvgDist) / (maxAvgDist - minAvgDist));
            if (gb < 0) {
                gb = 0;
            } else if (gb > 255) {
                gb = 255;
            }
            graphicsToPaint.setColor(new Color(255, gb, gb));
            graphicsToPaint.fillOval((int) Math.round(p.x - radius / 2), (int) Math.round(p.y - radius / 2), radius, radius);
            if (p._avgDistance == maxAvgDist) {
                radius = 6;
                graphicsToPaint.drawOval((int) Math.round(p.x - radius / 2), (int) Math.round(p.y - radius / 2), radius, radius);
            }
        }

        graphicsToPaint.setColor(Color.blue);
        final int fovRadius = _dangerZone * 2;
        graphicsToPaint.drawOval((int)_robot.getX() - fovRadius / 2, (int)_robot.getY() - fovRadius / 2, fovRadius, fovRadius);
    }



}
