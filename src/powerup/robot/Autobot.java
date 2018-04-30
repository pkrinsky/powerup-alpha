package powerup.robot;

import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;

public class Autobot extends Robot {

	public Autobot(String name, String alliance, String gamedata, char startPosition) {
		super(name, alliance, gamedata, startPosition);
	}

	public int move(Field field) {
		int thismove = Robot.STOP;
		FieldObject target = null;
		// if we have a cube put it on the switch
		if (hasCube()) {
			if(Field.LEFT == startPosition){
				if( gamedata == "LRL") {
					target = field.find(alliance+"S");
					System.out.println("Autobot.move "+name+" target scale:"+target.getCol()+" "+target.getRow());
				} else {
					target = field.find(alliance+"FS");
					System.out.println("Autobot.move "+name+" target switch:"+target.getCol()+" "+target.getRow());

				}
				if (target.getCol()+1 < getCol()) {
					thismove = Robot.WEST;
				}
				if (target.getCol()-1 > getCol()) {
					thismove = Robot.EAST;
				}
				if (thismove == Robot.STOP) {
					thismove = Robot.SHOOT;
				}
				if (target.getRow() > getRow()) {
					thismove = Robot.SOUTH;
				}
				if (target.getRow() < getRow()) {
					thismove = Robot.NORTH;
				}
			}
			if(Field.RIGHT == startPosition){
				if( gamedata == "LRL") {
					target = field.find(alliance+"FS");
					System.out.println("Autobot.move "+name+" target scale:"+target.getCol()+" "+target.getRow());
				} else {
					target = field.find(alliance+"S");
					System.out.println("Autobot.move "+name+" target switch:"+target.getCol()+" "+target.getRow());

				}
				if (target.getCol()+1 < getCol()) {
					thismove = Robot.WEST;
				}
				if (target.getCol()-1 > getCol()) {
					thismove = Robot.EAST;
				}
				if (thismove == Robot.STOP) {
					thismove = Robot.SHOOT;
				}
				if (target.getRow() > getRow()) {
					thismove = Robot.SOUTH;
				}
				if (target.getRow() < getRow()) {
					thismove = Robot.NORTH;
				}
			}


		}


		// once the move has complete wait for next command
		command = Robot.STOP;
		return thismove;
	}

}
