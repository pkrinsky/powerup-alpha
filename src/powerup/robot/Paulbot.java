package powerup.robot;

import java.util.LinkedList;
import java.util.Queue;

import powerup.engine.Util;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;
import powerup.field.Scale;

public class Paulbot extends Robot {
	
	public Paulbot(String name, String alliance, String gamedata, char startPosition) {
		super(name, alliance, gamedata, startPosition);
	}
	
	public Queue<Integer> getAutonomousCommands() {
		Queue<Integer> commandList = new LinkedList<Integer>();
		
		if (BLUE.equals(alliance)) {
			if (Field.RIGHT == startPosition) {
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(NORTH);
				commandList.add(SHOOT);
			}
			if (Field.LEFT == startPosition) {
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(SOUTH);
				commandList.add(SOUTH);
				commandList.add(SHOOT);
			}
			if (Field.MIDDLE == startPosition) {
				commandList.add(NORTH);
				commandList.add(NORTH);
				commandList.add(NORTH);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(EAST);
				commandList.add(SHOOT);
			}
		} else {
			if (Field.RIGHT == startPosition) {
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(SOUTH);
				commandList.add(SHOOT);
			}
			if (Field.LEFT == startPosition) {
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(NORTH);
				commandList.add(NORTH);
				commandList.add(SHOOT);
			}
			if (Field.MIDDLE == startPosition) {
				commandList.add(SOUTH);
				commandList.add(SOUTH);
				commandList.add(SOUTH);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(WEST);
				commandList.add(SHOOT);
			}
		}
		
		return commandList;
		
	}	

	private FieldObject findCube(Field field) {
		FieldObject fo = null;
		int dc = 0;
		int dr = 0;
		int d = 0;
		int distance = Integer.MAX_VALUE;
		for (Cube c:field.getCubeList()) {
			dc = c.getCol() - getCol();
			dr = c.getRow() - getRow();
			d = Math.abs(dc)+Math.abs(dr);
			if (d<distance) {
				distance = d;
				fo = c;
			}
		}
		
		return fo;
	}
	
	private int chooseDirection(Field field, int targetc, int targetr) {
		Util.log("Paulbot.chooseDirection "+name+" robot at c:"+getCol()+" r:"+getRow());
		int nextmove = Robot.STOP;
		
		// calculate possible moves
		int[][] distance = new int[Field.COLS][Field.ROWS];
		
		// set all to unknown
		for (int col=0;col<Field.COLS;col++) {
			for (int row=0;row<Field.ROWS;row++) {
				distance[col][row] = -1;
			}
		}
		
		// mark all blocked squares
		for (int row=0;row<Field.ROWS;row++) {
			for (int col=0;col<Field.COLS;col++) {
				FieldObject fo = field.getFieldObject(col, row); 
				if (fo != null) {
					distance[col][row] = -2;
				}
			}
		}
		
		
		// set end point
		distance[targetc][targetr] = 0;
		//print(distance);
		
		boolean done = false;
		//Util.log("Paulbot distance calc start");
		int loops = Field.COLS;
		while (!done && loops-- > 0) {
			done = true;
			// loop thru the array and calculate next steps
			for (int row=0;row<Field.ROWS;row++) {
				for (int col=0;col<Field.COLS;col++) {
					if (distance[col][row] == -1) {
						done = false;
					} else {
						// found a distance so check the neighbors
						//Util.log("checking r:"+row+" c:"+col+" "+distance[col][row]);
						
						// check the north
						if (row-1>=0 && distance[col][row-1] == -1)
							distance[col][row-1] = distance[col][row] + 1;
						
						// check the south
						if (row+1<Field.ROWS && distance[col][row+1] == -1)
							distance[col][row+1] = distance[col][row] + 1;
						
						// check the west
						if (col-1>=0 && distance[col-1][row] == -1)
							distance[col-1][row] = distance[col][row] + 1;
						
						// check the east
						if (col+1<Field.COLS && distance[col+1][row] == -1)
							distance[col+1][row] = distance[col][row] + 1;
					}
				}
			}
			//print(distance);
		}
		
		//Util.log("Paulbot distance calc done");
		//print(distance);
		
		// choose distance based upon shortest path
		// check the north
		
		int shortest = Integer.MAX_VALUE;
		
		int dist = 0;
		if (getRow()-1>=0) {
			dist = distance[getCol()][getRow()-1];
			Util.log("Paulbot North distance is "+distance[getCol()][getRow()-1]);
			if (dist > -1 && dist < shortest) {
				nextmove = Robot.NORTH;
				shortest = dist;
			}
		}
		
		if (getRow()+1<Field.ROWS) {
			dist = distance[getCol()][getRow()+1];
			Util.log("Paulbot South distance is "+distance[getCol()][getRow()+1]);
			if (dist > -1 && dist < shortest) {
				nextmove = Robot.SOUTH;
				shortest = dist;
			}
		}
		
		
		if (getCol()-1>=0) {
			dist = distance[getCol()-1][getRow()];
			Util.log("Paulbot West distance is "+distance[getCol()-1][getRow()]);
			if (dist > -1 && dist < shortest) {
				nextmove = Robot.WEST;
				shortest = dist;
			}
		}
		
		if (getCol()+1<Field.COLS) {
			dist = distance[getCol()+1][getRow()];
			Util.log("Paulbot East distance is "+distance[getCol()+1][getRow()]);
			if (dist > -1 && dist < shortest) {
				nextmove = Robot.EAST;
				shortest = dist;
			}
		}
		
		
		return nextmove;
	}
	
	@SuppressWarnings("unused")
	private void print(int[][] distance) {
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int row=0;row<Field.ROWS;row++) {
			for (int col=0;col<Field.COLS;col++) {
				sb.append(((distance[col][row])+"  ").substring(0, 2)+" ");
			}
			sb.append(" row "+row+"\n");
		}
		Util.log(sb.toString());
	}
	
	public int move(Field field) {
		Util.log("Paulbot.move "+name);
		FieldObject target = null;
		int thismove = Robot.STOP;
		String otherAlliance = Robot.RED.equals(alliance) ? Robot.BLUE : Robot.RED;
		
		
		// choose a new target if needed
		// if we have a cube put it on the switch
		if (hasCube()) {
			
			Scale us = (Scale) field.find(alliance+"S");
			Scale them = (Scale) field.find(otherAlliance+"S");
			
			if (target == null && them.getNumCubes() >= us.getNumCubes()) {
				target = us;
			}
			
			us = (Scale) field.find(alliance+"NS");
			them = (Scale) field.find(otherAlliance+"FS");
			if (target == null && them.getNumCubes() >= us.getNumCubes()) {
				target = us;
			}
			
			us = (Scale) field.find(alliance+"FS");
			them = (Scale) field.find(otherAlliance+"NS");
			if (target == null && them.getNumCubes() >= us.getNumCubes()) {
				target = us;
			}
			
			if (target == null) 
				target = field.find(alliance+"S");
			
			/*
			if(Field.LEFT == startPosition){
				target = field.find(alliance+"FS");
			}

			if(Field.MIDDLE == startPosition){
				target = field.find(alliance+"NS");
			}
			
			if(Field.RIGHT == startPosition){
				target = field.find(alliance+"S");
			}
			*/

		} else {
			target = findCube(field);
		}
		
		if (target == null) {
			Util.log("No more cubes");
		} else {
			Util.log("Paulbot.move "+name+" target is c:"+target.getCol()+" r:"+target.getRow());
			thismove = chooseDirection(field,target.getCol(),target.getRow());
			
			if (hasCube()) {
				if (target.getCol() == getCol() && target.getRow() == getRow()+1) {
					thismove = Robot.SHOOT;
				} 
				if (target.getCol() == getCol() && target.getRow() == getRow()-1) {
					thismove = Robot.SHOOT;
				}
				if (target.getCol() == getCol()+1 && target.getRow() == getRow()) {
					thismove = Robot.SHOOT;
				} 
				if (target.getCol() == getCol()-1 && target.getRow() == getRow()) {
					thismove = Robot.SHOOT;
				} 
			} else {
				if (field.getFieldObject(getCol(), getRow()+1) instanceof Cube) {
					thismove = Robot.PICKUP;
				} 
				if (field.getFieldObject(getCol(), getRow()-1) instanceof Cube) {
					thismove = Robot.PICKUP;
				}
				if (field.getFieldObject(getCol()+1, getRow()) instanceof Cube) {
					thismove = Robot.PICKUP;
				} 
				if (field.getFieldObject(getCol()-1, getRow()) instanceof Cube) {
					thismove = Robot.PICKUP;
				}
			}
		}

		// once the move has complete wait for next command
		command = Robot.STOP;
		return thismove;
	}

}
