package powerup.field;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import powerup.engine.Util;
import powerup.network.GameClient;

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
	private static final String DELIM = GameClient.DELIM;
	private static final String ROW_DELIM = GameClient.ROW_DELIM;

	public static final int GAME_SECS = 60;

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
		return find(name,false);
	}
	
	public FieldObject find(String name, boolean warn) {
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
		
		if (fo == null && (warn || (Util.getDebugLevel() >=5))) {
			Util.log("WARNING: Field.find cannot find field object with name:"+name);
			new Exception().printStackTrace(System.out);
		}
		return fo;
		
	}
	
	public List<Robot> getRobotList() {
		List<Robot> robotList = new ArrayList<Robot>();
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				if (grid[c][r] != null && grid[c][r] instanceof Robot) {
					robotList.add((Robot)grid[c][r]);
				}
			}
		}
		return robotList;
	}	
	
	public void print() {
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				if (grid[c][r] != null) {
					Util.log("Field.print "+c+" "+r+" "+grid[c][r].name);
				} else {
					//System.out.println(c+" "+r+" Empty");
				}
			}
		}
		
	}
	
	public synchronized void set(int col, int row, FieldObject fo) {
		fo.setCol(col);
		fo.setRow(row);
		if (grid[col][row] == null) {
			//Util.log("Field.setup "+fo.getName()+" at col:"+col+" r:"+row);
			grid[col][row] = fo;
		} else {
			Util.log("Field.setup position already taken by "+grid[col][row].getName()); 
		}
	}
	
	public synchronized void setup(Robot r) {
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
		
		set(col,row,r);
	}
	
	private void remove(int col, int row) {
		grid[col][row].setDeleted(true);
		grid[col][row] = null;
		Util.log("Field.remove is now empty col:"+col+" r:"+row);
	}
	
	public synchronized void move(FieldObject fo, int col, int row) {
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
	
	public synchronized void move(String name, int move) {
		// tell the field to move it
		FieldObject fo = find(name);
		if (move > Robot.STOP) {
			Util.log("Field.move "+name+" move:"+Robot.getCommandName(move) + " col:"+ fo.getCol()+ " row:"+ fo.getRow());
		
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
		boolean found = false;
		if (!found) found = pickupCheck(robot.getCol()+1, robot.getRow(),robot);
		if (!found) found = pickupCheck(robot.getCol(), robot.getRow()+1,robot);
		if (!found) found = pickupCheck(robot.getCol(), robot.getRow()-1,robot);
		if (!found) found = pickupCheck(robot.getCol()-1, robot.getRow(),robot);
	}
	
	private boolean pickupCheck(int c, int r, Robot robot) {
		boolean found = false;
		FieldObject fo = getFieldObject(c, r);
		if (fo != null && fo instanceof Cube) {
			Util.log("Field.pickupCheck found cube "+fo.getName());
			if(robot.hasCube() == false) {
				robot.setHasCube(true);
				remove(c, r);
				found = true;
			}else {
				Util.log("Field.pickupCheck you already have a cube");
			}
		}
		return found;
	}
	
	public void shoot(Robot robot) {
		// check for a shot
		boolean shot = false;
		if (!shot) shot = shootCheck(robot.getCol()+1, robot.getRow(),robot);
		if (!shot) shot = shootCheck(robot.getCol(), robot.getRow()+1,robot);
		if (!shot) shot = shootCheck(robot.getCol(), robot.getRow()-1,robot);
		if (!shot) shot = shootCheck(robot.getCol()-1, robot.getRow(),robot);
	}
	
	private boolean shootCheck(int c, int r, Robot robot) {
		boolean shot = false;
		FieldObject fo = getFieldObject(c, r);
		if (fo != null && fo instanceof Scale) {
			Scale scale = (Scale) fo;
			Util.log("RobotController.found scale");
			if(robot.hasCube() == true) {
				robot.setHasCube(false);
				robot.shotMade();
				scale.setNumCubes(scale.getNumCubes()+1);
				shot = true;
				Util.log("NumCubes="+ scale.getNumCubes());
				
			}else {
				Util.log("you have no cube");
			}
		}		
		return shot;
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

	private void setRedScore(int redScore) {
		this.redScore = redScore;
	}

	private void setBlueScore(int blueScore) {
		this.blueScore = blueScore;
	}

	private void setGameSecs(int gameSecs) {
		this.gameSecs = gameSecs;
	}

	public synchronized void decreaseGameSecs(int i) {
		long current = System.currentTimeMillis();
		if (current - lastTick > 1000) {
			this.gameSecs = this.gameSecs - 1;
			lastTick = current;
			//Util.log("Field.decreaseGameSecs new time is:"+gameSecs);
		}

	}
	
	public static Field getStaticField() {
		Util.log("Field.getStaticField");
		
		Field field = new Field();
		
		int col2 = Field.COL2;
		int col1 = Field.COL1;
		int col3 = Field.COL3;
				
		field.set(col2,3,new Scale("RS",Robot.RED)); 
		field.set(col2,4,new Wall());
		field.set(col2,5,new Wall());
		field.set(col2,6,new Wall());
		field.set(col2,7,new Wall());
		field.set(col2,8,new Wall());
		field.set(col2,9,new Wall());
		field.set(col2,10,new Wall());
		field.set(col2,11,new Scale("BS",Robot.BLUE));

		field.set(col1,4,new Scale("BNS",Robot.BLUE));
		field.set(col1,5,new Wall());
		field.set(col1,6,new Wall());
		field.set(col1,7,new Wall());
		field.set(col1,8,new Wall());
		field.set(col1,9,new Wall());
		field.set(col1,10,new Scale("RFS",Robot.RED));

		field.set(col3,4,new Scale("BFS",Robot.BLUE));
		field.set(col3,5,new Wall());
		field.set(col3,6,new Wall());
		field.set(col3,7,new Wall());
		field.set(col3,8,new Wall());
		field.set(col3,9,new Wall());
		field.set(col3,10,new Scale("RNS",Robot.RED));
		
		return field;
	}	
	


	public synchronized String save() {
		StringBuffer sb = new StringBuffer();
		FieldObject fo = null;
		
		sb.append(this.getClass().getName());
		sb.append(DELIM);
		sb.append(redScore);
		sb.append(DELIM);
		sb.append(blueScore);
		sb.append(DELIM);
		sb.append(gameSecs);
		sb.append(ROW_DELIM);
		
		//Util.log("Field.save\n"+sb.toString());
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				if (grid[c][r] != null) {
					fo = grid[c][r];
					if (fo instanceof Cube 
							|| fo instanceof Scale
							|| fo instanceof Robot) 
					{
						if (fo instanceof Robot) {
							sb.append("powerup.robot.Robot");
						} else {
							sb.append(grid[c][r].getClass().getName());
						}
						sb.append(DELIM);
						sb.append(grid[c][r].getName());
						sb.append(DELIM);
						sb.append(grid[c][r].getCol());
						sb.append(DELIM);
						sb.append(grid[c][r].getRow());
						if (fo instanceof Scale ) {
							sb.append(DELIM);
							sb.append(((Scale)(grid[c][r])).getAlliance());
							sb.append(DELIM);
							sb.append(((Scale)(grid[c][r])).getNumCubes());
						}
						if (fo instanceof Robot ) {
							sb.append(DELIM);
							sb.append(((Robot)(grid[c][r])).getAlliance());
							sb.append(DELIM);
							sb.append(((Robot)(grid[c][r])).getGamedata());
							sb.append(DELIM);
							sb.append(((Robot)(grid[c][r])).getStartPosition());
							sb.append(DELIM);
							sb.append(((Robot)(grid[c][r])).hasCube());
							sb.append(DELIM);
							sb.append(((Robot)(grid[c][r])).getShotsMade());
							//Util.log("Field.save\n"+sb.toString());
						}
						sb.append(ROW_DELIM);
					}
				}
			}
		}
		
		
		return sb.toString();
		
	}
	
	private void resetCubes() {
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				if (grid[c][r] != null && grid[c][r] instanceof Cube) {
						grid[c][r] = null;
				}
			}
		}		
	}
	
	public synchronized void load(String s) {
		boolean debug = false;
		resetCubes();
		StringTokenizer rowTokens = new StringTokenizer(s, ROW_DELIM);
		while (rowTokens.hasMoreTokens()) {
			StringTokenizer fieldTokens = new StringTokenizer(rowTokens.nextToken(), DELIM);
			List<String> fieldList = new ArrayList<String>();
			while (fieldTokens.hasMoreTokens()) {
				fieldList.add(fieldTokens.nextToken());
			}
			
			if (debug) {
				Util.log("Field.load "+fieldList.get(0));
				for (String f:fieldList) {
					Util.log(f);
				}
			}
			
			if ("powerup.field.Field".equals(fieldList.get(0))) {
				setRedScore(new Integer(fieldList.get(1)));
				setBlueScore(new Integer(fieldList.get(2)));
				setGameSecs(new Integer(fieldList.get(3)));
			}
			if ("powerup.field.Cube".equals(fieldList.get(0))) {
				// delete if already exists
				//FieldObject fo = find(fieldList.get(1));
				//if (fo != null) grid[fo.getCol()][fo.getRow()] = null;
				
				// create in new spot
				Cube o = new Cube();
				o.setName(fieldList.get(1));
				o.setCol(new Integer(fieldList.get(2)));
				o.setRow(new Integer(fieldList.get(3)));
				set(o.getCol(),o.getRow(),o);
			}
			if ("powerup.field.Scale".equals(fieldList.get(0))) {
				Scale o = (Scale) find(fieldList.get(1));
				o.setNumCubes(new Integer(fieldList.get(5)));
			}
			if ("powerup.robot.Robot".equals(fieldList.get(0))) {
				Robot o = (Robot) find(fieldList.get(1));
				if (o != null) {
					grid[o.getCol()][o.getRow()] = null;
				} else {
					o = new Robot(fieldList.get(1),fieldList.get(4),fieldList.get(5),fieldList.get(6).charAt(0));
				}
				o.setCol(new Integer(fieldList.get(2)));
				o.setRow(new Integer(fieldList.get(3)));
				o.setHasCube((new Boolean(fieldList.get(7))));
				o.setShotsMade((new Integer(fieldList.get(8))));
				set(o.getCol(),o.getRow(),o);
			}
		}
	}
}
