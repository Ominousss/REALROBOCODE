package prog;

import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;

public class EnemyBot {

    //Information gathering class for enemy bot so we can target solely 1 bot
    private double _bearing;
    private double _distance;
    private double _energy;
    private double _heading;
    private String _name;
    private double _velocity;


    public EnemyBot() {
        Reset(); //Resets on construction to ensure everything is default
    }
    public void Update(ScannedRobotEvent event) {
        _bearing = event.getBearing();
        _distance = event.getDistance();
        _energy = event.getEnergy();
        _heading = event.getHeading();
        _name = event.getName();
        _velocity = event.getVelocity();
    }

    public void Reset() {
        _bearing = 0;
        _distance = 0;
        _energy = 0;
        _heading = 0;
        _name = "";
        _velocity = 0;

    }

    public boolean None() {
        //Calls true when reset
        return _name.equals("");
    }
    public double getBearing() {
        return _bearing;
    }

    public double getDistance() {
        return _distance;
    }

    public double getEnergy() {
        return _energy;
    }

    public double getHeading() {
        return _heading;
    }

    public String getName() {
        return _name;
    }

    public double getVelocity() {
        return _velocity;
    }

}
