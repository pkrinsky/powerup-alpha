package powerup.robot;

import java.util.LinkedList;
import java.util.Queue;

import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.Robot;

public class BasicAutoBot extends Robot {
	
	public BasicAutoBot() {};
	
	public BasicAutoBot(String name, String alliance, String gamedata, char startPosition) {
		super(name, alliance, gamedata, startPosition);
	}
	
	public Queue<Integer> getAutonomousCommands() {
		Queue<Integer> commandList = new LinkedList<Integer>();
		
		if (BLUE.equals(alliance)) {
			if (Field.RIGHT == startPosition) {
				for (int i=0;i<11;i++) commandList.add(EAST);
				for (int i=0;i<1;i++) commandList.add(NORTH);
				commandList.add(SHOOT);
			}
			if (Field.LEFT == startPosition) {
				for (int i=0;i<5;i++) commandList.add(EAST);
				for (int i=0;i<2;i++) commandList.add(SOUTH);
				commandList.add(SHOOT);
			}
			if (Field.MIDDLE == startPosition) {
				for (int i=0;i<3;i++) commandList.add(NORTH);
				for (int i=0;i<4;i++) commandList.add(EAST);
				commandList.add(SHOOT);
			}
		} else {
			if (Field.RIGHT == startPosition) {
				for (int i=0;i<11;i++) commandList.add(WEST);
				for (int i=0;i<1;i++) commandList.add(SOUTH);
				commandList.add(SHOOT);
			}
			if (Field.LEFT == startPosition) {
				for (int i=0;i<5;i++) commandList.add(WEST);
				for (int i=0;i<2;i++) commandList.add(NORTH);
				commandList.add(SHOOT);
			}
			if (Field.MIDDLE == startPosition) {
				for (int i=0;i<3;i++) commandList.add(SOUTH);
				for (int i=0;i<4;i++) commandList.add(WEST);
				commandList.add(SHOOT);
			}
		}
		
		Util.log("returing auto commands:"+commandList.size());
		
		return commandList;
		
	}	


}
