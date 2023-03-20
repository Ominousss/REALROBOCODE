package prog;

import robocode.Robot;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

enum Parts {
    circle,
    dodge,
    hunt

}

public class PartStateFactory
{
    private Robot _myRobot;
    private Map<Parts, IRobotPart> _parts;

    public PartStateFactory(Penicillin myRobot) {
        _myRobot = myRobot;
            _parts = new HashMap<Parts, IRobotPart>(){{
                put(Parts.circle, myRobot.new CirclingTank());
                put(Parts.dodge, myRobot.new EvadeTank());
                put(Parts.hunt, myRobot.new HuntTank());
        }};
    }

    public IRobotPart CircleTank() {
        _parts.get(Parts.circle).init();
        return _parts.get(Parts.circle);
    }

    public IRobotPart EvadeTank() {
        _parts.get(Parts.dodge).init();
        return _parts.get(Parts.dodge);
    }

    public IRobotPart HuntTank() {
        _parts.get(Parts.hunt).init();
        return _parts.get(Parts.hunt);
    }
}
