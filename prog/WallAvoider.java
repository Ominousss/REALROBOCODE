package prog;

import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;

public class WallAvoider extends AdvancedRobot {
    protected int _tooCloseToWall = 0;

    private final int wallMargin_ = 60;

    //Taken from online. Basically making a condition to identify when we are too close to the wall
    public void addCustomEvent(Condition condition) {
        super.addCustomEvent(new Condition("too_close_to_walls") {
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

}
