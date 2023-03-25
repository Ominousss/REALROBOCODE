package prog;

import java.awt.geom.Point2D;


/**
 * Class that extends Point2D.Double to help in EnemyDodgingMovement. We are able to get the location of the enemy and average distance
 */
public class EDMPoint extends Point2D.Double {

    public double _avgDistance;
    public EDMPoint(double x, double y) {
        super(x,y);
    }
}
