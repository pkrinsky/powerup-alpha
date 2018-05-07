package powerup.field;

import powerup.engine.Util;

public class Field {
	public static final int COLS=23;
	public static final int ROWS=15;
	public static final int COL2 = 11;
	public static final int COL1 = 5;
	public static final int COL3 = COLS-6;
	public static final int ROW2 = 7;
	public static final int ROW1 = ROW2-6;
	public static final int ROW3 = ROW2+6;
	public static final char LEFT = 'L';
	public static final char MIDDLE = 'M';
	public static final char RIGHT = 'R';
	public static final String DELIM="|";
	public static final String ROW_DELIM="\n";

	private static final int GAME_SECS = 60;

	private int redScore = 0;
	private int blueScore = 0;
	private int gameSecs = GAME_SECS;
	private long lastTick = 0;
	
	

	private FieldObject[][] grid= new FieldObject[COLS][ROWS];

	public FieldObject[][] getGrid() {
		return grid;
	}
	
	public FieldObject getFieldObject(int c, int r) {
		return grid[c][r];
	}

	public void setGrid(FieldObject[][] grid) {
		this.grid = grid;
	}
	
	public FieldObject find(String name) {
		FieldObject fo = null;
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				//System.out.println("Field.find checking col:"+c+" r:"+r);
				if (grid[c][r] != null 
						&& grid[c][r].getName() != null 
						&& grid[c][r].getName().equalsIgnoreCase(name)) 
				{
					fo = grid[c][r];
					fo.setCol(c);
					fo.setRow(r);
					//System.out.println("Field.find found "+fo.name+" col:"+fo.getCol()+" r:"+fo.getRow());
					break;
				}
			}
		}
		
		if (fo == null) {
			System.out.println("Field.find Cannot find field object with name:"+name);
		}
		return fo;
		
	}
	
	
	public void print() {
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				if (grid[c][r] != null) {
					System.out.println("Field.print "+c+" "+r+" "+grid[c][r].name);
				} else {
					//System.out.println(c+" "+r+" Empty");
				}
			}
		}
		
	}
	
	public void setup(int col, int row, FieldObject fo) {
		System.out.println("Field.setup "+fo.getName()+" at col:"+col+" r:"+row);
		fo.setCol(col);
		fo.setRow(row);
		if (grid[col][row] == null) {
			grid[col][row] = fo;
		} else {
			throw new RuntimeException("Field.setup position already taken by "+grid[col][row].getName()); 
		}
	}
	
	public void setup(Robot r) {
		int row = 0;
		int col = 0;
		
		if (LEFT == r.getStartPosition()) {
			if (Robot.BLUE.equals(r.getAlliance())) {
				row = ROW1;
			} else {
				row = ROW3;
			}
		}
		if (MIDDLE == r.getStartPosition()) {
			row = ROW2;
		}
		if (RIGHT == r.getStartPosition()) {
			if (Robot.BLUE.equals(r.getAlliance())) {
				row = ROW3;
			} else {
				row = ROW1;
			}
		}
		
		if (Robot.BLUE.equals(r.getAlliance())) {
			col = 0;
		} else {
			col = COLS-1;
		}
		
		setup(col,row,r);
	}
	
	public void remove(int col, int row) {
		grid[col][row].setDeleted(true);
		grid[col][row] = null;
		Util.log("Field.remove is now empty col:"+col+" r:"+row);
	}
	
	public void move(FieldObject fo, int col, int row) {
		Util.log("Field.move "+fo.name+" from col:"+fo.getCol()+" r:"+fo.getRow()+" to col:"+col+" r:"+row);
		
		// make sure target is on the field
		if (row >= 0 && col >= 0 && row < ROWS && col < COLS) {
			// make sure target is empty
			if(grid[col][row] == null ) {
				int oldr = fo.getRow();
				int oldc = fo.getCol();
				fo.setCol(col);
				fo.setRow(row);
				grid[col][row] = fo;
				Util.log("Field.move was successful new position for "+fo.name+" col:"+col+" r:"+row);
				grid[oldc][oldr] = null;
				Util.log("Field.move is now empty "+" col:"+oldc+" r:"+oldr);
			} else {
				Util.log("Field.move target is occupied "+fo.name+" col:"+col+" r:"+row);
			}
		} else {
			Util.log("Field.move off the field "+fo.name+" col:"+col+" r:"+row);
		}
		//print();
	}
	
	public void move(String name, int move) {
		// tell the field to move it
		FieldObject fo = find(name);
		if (move > Robot.STOP) {
			Util.log("RobotController: "+name+" move:"+Robot.getCommandName(move) + " col:"+ fo.getCol()+ " row:"+ fo.getRow());
		
			switch (move) {
				case Robot.NORTH:
					move(fo,fo.getCol(),fo.getRow()-1);
					break;
				case Robot.SOUTH:
					move(fo,fo.getCol(),fo.getRow()+1);
					break;
				case Robot.EAST:
					move(fo,fo.getCol()+1,fo.getRow());
					break;
				case Robot.WEST:
					move(fo,fo.getCol()-1,fo.getRow());
					break;
				case Robot.STOP:
					break;
				case Robot.PICKUP:
					pickup((Robot)(fo));
					break;
				case Robot.SHOOT:
					shoot((Robot)(fo));
					break;
				default:
					Util.log("move not implemented:"+move);
			}
		}
	}
	
	public void pickup(Robot robot) {
		// check for a cube
		pickupCheck(robot.getCol()+1, robot.getRow(),robot);
		pickupCheck(robot.getCol(), robot.getRow()+1,robot);
		pickupCheck(robot.getCol(), robot.getRow()-1,robot);
		pickupCheck(robot.getCol()-1, robot.getRow(),robot);
	}
	
	private void pickupCheck(int c, int r, Robot robot) {
		FieldObject fo = getFieldObject(c, r);
		if (fo != null && fo instanceof Cube) {
			//Util.log("Field.pickupCheck found cube");
			if(robot.hasCube() == false) {
				robot.setHasCube(true);
				remove(c, r);
			}else {
				//System.out.println("you already have a cube");
			}
		}		
	}
	
	public void shoot(Robot robot) {
		// check for a cube
		shootCheck(robot.getCol()+1, robot.getRow(),robot);
		shootCheck(robot.getCol(), robot.getRow()+1,robot);
		shootCheck(robot.getCol(), robot.getRow()-1,robot);
		shootCheck(robot.getCol()-1, robot.getRow(),robot);
	}
	
	private void shootCheck(int c, int r, Robot robot) {
		FieldObject fo = getFieldObject(c, r);
		if (fo != null && fo instanceof Scale) {
			Scale scale = (Scale) fo;
			//System.out.println("RobotController.found scale");
			if(robot.hasCube() == true) {
				robot.setHasCube(false);
				robot.shotMade();
				scale.setNumCubes(1);
				//System.out.println("NumCubes="+ scale.getNumCubes());
				
			}else {
				//System.out.println("you have no cube");
			}
		}		
	}
	public int getRedScore() {
		return redScore;
	}

	public void increaseRedScore(int i) {
		this.redScore = this.redScore+i;
	}

	public int getBlueScore() {
		return blueScore;
	}

	public void increaseBlueScore(int i) {
		this.blueScore = this.blueScore+i;
	}

	public int getGameSecs() {
		return gameSecs;
	}

	public void decreaseGameSecs(int i) {
		long current = System.currentTimeMillis();
		if (current - lastTick > 1000) {
			this.gameSecs = this.gameSecs - 1;
			lastTick = current;
			Util.log("Field.decreaseGameSecs new time is:"+gameSecs);
		}

	}


	public String save() {
		StringBuffer sb = new StringBuffer();
		FieldObject fo = null;
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				if (grid[c][r] != null) {
					fo = grid[c][r];
					if (fo instanceof Cube 
							|| fo instanceof Scale
							|| fo instanceof Robot) {
						sb.append(grid[c][r].getClass().getName());
						sb.append(DELIM);
						sb.append(grid[c][r].getName());
						sb.append(DELIM);
						sb.append(grid[c][r].getCol());
						sb.append(DELIM);
						sb.append(grid[c][r].getRow());
						sb.append(ROW_DELIM);
					}
				}
			}
		}
		
		Util.log(sb.toString());
		return sb.toString();
		
	}
	
	public void load(String s) {
		
		// mark all the objects that can change as dirty
		// process all the loaded data
		// remove anything still marked as dirty as it must have been removed
		

		
	}

	
	
	

}
