import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.util.Hashtable;
import java.util.Enumeration;
import java.awt.geom.*;

/** Smash - a robot by Starrynte
*/

public class Smash extends AdvancedRobot
{
	static Hashtable enemies = new Hashtable();
	static Enemy target;
	static Point2D.Double nextDestination;
	static Point2D.Double lastPosition;
	static Rectangle2D.Double field;
	static double myEnergy;	
	static double myX;
	static double myY;
	static double timeSinceLastScan=0;	

	public void run()
	{
		field=new Rectangle2D.Double(36,36,getBattleFieldWidth()-72,getBattleFieldHeight()-72);
		nextDestination=lastPosition=new Point2D.Double(getX(),getY());
		target=new Enemy();
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		while(true){
			doMovement();
			doGunning();
			doRadar();
			myEnergy=getEnergy();
			myX=getX();
			myY=getY();
			timeSinceLastScan++;
			execute();
		}	
	}
	public void onScannedRobot(ScannedRobotEvent e){
		Enemy en=(Enemy)enemies.get(e.getName());
		if(en==null){
			en=new Enemy();
			enemies.put(e.getName(),en);
		}
		en.energy=e.getEnergy();
		en.live=true;
		double x=myX + Math.sin(e.getBearingRadians()+getHeadingRadians())*e.getDistance();
		double y=myY + Math.cos(e.getBearingRadians()+getHeadingRadians())*e.getDistance();
		en.location=new Point2D.Double(x,y);
		en.distance=e.getDistance();
		en.velocity=e.getVelocity();
		en.heading=e.getHeadingRadians();
		en.bearing=e.getBearingRadians();
		en.name=e.getName();
		if(!target.live || (en.distance<target.distance*0.8 && en.energy<=target.energy*1.1) || (en.energy<target.energy*0.8 && en.distance<=target.distance*1.15)){
			target=en;
		}
		if(target.name.equals(e.getName())){
			timeSinceLastScan=0;
		}
	}
	public void onRobotDeath(RobotDeathEvent e) {
		Enemy en=(Enemy)enemies.get(e.getName());
		if(en!=null){
			en.live=false;
		}
	}
	public void onHitRobot(HitRobotEvent e){
		Enemy en=(Enemy)enemies.get(e.getName());
		if(en==null){
			en=new Enemy();
			enemies.put(e.getName(),en);
		}
		en.energy=e.getEnergy();
		en.live=true;
		en.bearing=e.getBearingRadians();
		en.name=e.getName();
		target=en;
		setTurnGunRightRadians(en.bearing+getHeadingRadians()-getGunHeadingRadians());
		setFire(3);
	}
	void doMovement(){
		for(int i=0;i<250;i++){
			if(target!=null){
				double ang=2*Math.PI*Math.random();
				double dist=150+250*Math.random();
				double testX=myX+Math.sin(ang)*dist;
				double testY=myY+Math.cos(ang)*dist;
				if(field.contains(testX,testY)){
					if(evaluate(new Point2D.Double(testX,testY))<evaluate(nextDestination)){
						nextDestination.setLocation(testX,testY);
					}
				}
			}
		}
		double ang=calcAngle(new Point2D.Double(myX,myY),nextDestination)-getHeadingRadians();
		double dist=nextDestination.distance(myX,myY);
		setTurnRightRadians(Utils.normalRelativeAngle(ang));
		setAhead(dist);
		setMaxVelocity((Math.abs(Utils.normalRelativeAngle(ang))>1) ? 2.25 : 8);
	}
	void doGunning(){
		if(getGunTurnRemainingRadians()<0.01){
			setTurnGunRightRadians(Utils.normalRelativeAngle(target.bearing+getHeadingRadians()-getGunHeadingRadians()));
			
		}
		if(getGunHeat()==0 && getEnergy()>5){
			double firePower=Math.min(Math.min(myEnergy/10,1300/target.distance),target.energy/4);			
			setFire(firePower);
		}
	}
	void doRadar(){
		if(timeSinceLastScan<10 && getOthers()==1){
			setTurnRadarRightRadians(Utils.normalRelativeAngle(target.bearing+getHeadingRadians()-getRadarHeadingRadians())*2);
		}else{
			setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		}
	}
	double evaluate(Point2D.Double destination){
		double risk=0;
		Enumeration e=enemies.elements();
		while(e.hasMoreElements()){
			Enemy en=(Enemy)e.nextElement();
			if(en.live){
				double eratio=Math.min((en.energy*2)/myEnergy,2.5);
				double perp=Math.abs(Math.cos(calcAngle(destination,new Point2D.Double(myX,myY))-calcAngle(destination,en.location)));
				risk+=(eratio*(1+perp))/destination.distance(en.location);
			}
		}
		return risk;
	}
	double calcAngle(Point2D.Double s,Point2D.Double t){
		return Math.atan2(t.getX()-s.getX(),t.getY()-s.getY());
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