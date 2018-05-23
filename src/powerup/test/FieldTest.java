package powerup.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.Robot;
import powerup.field.Scale;
import powerup.field.Wall;
import powerup.robot.Paulbot;

class FieldTest {
	
	
	private Field getStaticField() {
		// setup test field
		Field field1 = new Field();
		field1.set(2,2,new Scale("RS",Robot.RED));
		field1.set(3,4,new Wall());
		return field1;
	}

	@Test
	void testSaveAndLoad() {
		
		// setup test field
		Field field1 = getStaticField();
		field1.set(1, 1, new Cube());
		Robot robot = new Paulbot("004",Robot.RED,"LRL",Field.LEFT);
		robot.setHasCube(true);
		field1.setup(robot);
		
		// save as string
		String s1 = field1.save();
		
		// create new field and load from string
		Field field2 = getStaticField();
		field2.load(s1);
		
		// make sure fields are the same
		String s2 = field2.save();
		assertEquals(s1, s2);
	}

}
