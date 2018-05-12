package powerup.server;

import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;
import powerup.field.Scale;

public class GameController {
	
	private Robot robot = null;
	

	public GameController(Robot robot) {
		this.robot = robot;
	}

	public void move(Field field) {
		int move = robot.move(field);
		FieldObject fo = field.find(robot.getName());
		if (move > Robot.STOP)
			System.out.println("RobotController: "+robot.getName()+" move:"+Robot.getCommandName(move) + " col:"+ fo.getCol()+ " row:"+ fo.getRow());
		
		switch (move) {
			case Robot.NORTH:
				field.move(fo,fo.getCol(),fo.getRow()-1);
				break;
			case Robot.SOUTH:
				field.move(fo,fo.getCol(),fo.getRow()+1);
				break;
			case Robot.EAST:
				field.move(fo,fo.getCol()+1,fo.getRow());
				break;
			case Robot.WEST:
				field.move(fo,fo.getCol()-1,fo.getRow());
				break;
			case Robot.STOP:
				break;
			case Robot.PICKUP:
				pickup(field);
				break;
			case Robot.SHOOT:
				shoot(field);
				break;
			default:
				System.out.println("move not implemented:"+move);
		}
		
		//System.out.println(robot.getName()+" x:"+x+" y:"+y);
	}
	
	public void pickup(Field field) {
		// check for a cube
		pickupCheck(field,robot.getCol()+1, robot.getRow());
		pickupCheck(field,robot.getCol(), robot.getRow()+1);
		pickupCheck(field,robot.getCol(), robot.getRow()-1);
		pickupCheck(field,robot.getCol()-1, robot.getRow());
	}
	
	private void pickupCheck(Field field, int c, int r) {
		FieldObject fo = field.getFieldObject(c, r);
		if (fo != null && fo instanceof Cube) {
			System.out.println("RobotController.found cube");
			if(robot.hasCube() == false) {
				robot.setHasCube(true);
				field.remove(c, r);
			}else {
				System.out.println("you already have a cube");
			}
		}		
	}
	public void shoot(Field field) {
		// check for a cube
		shootCheck(field,robot.getCol()+1, robot.getRow());
		shootCheck(field,robot.getCol(), robot.getRow()+1);
		shootCheck(field,robot.getCol(), robot.getRow()-1);
		shootCheck(field,robot.getCol()-1, robot.getRow());
	}
	
	private void shootCheck(Field field, int c, int r) {
		FieldObject fo = field.getFieldObject(c, r);
		if (fo != null && fo instanceof Scale) {
			Scale scale = (Scale) fo;
			System.out.println("RobotController.found scale");
			if(robot.hasCube() == true) {
				robot.setHasCube(false);
				robot.shotMade();
				scale.setNumCubes(1);
				System.out.println("NumCubes="+ scale.getNumCubes());
				
			}else {
				System.out.println("you have no cube");
			}
		}		
	}
	
	public void key(char key) {
		robot.key(key);
	}


	public Robot getRobot() {
		return robot;
	}



	public void setRobot(Robot robot) {
		this.robot = robot;
	}









}
