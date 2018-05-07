package powerup.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.Robot;
import powerup.field.Scale;
import powerup.robot.Autobot;

class FieldTest {

	@Test
	void testSaveAndLoad() {
		
		// setup test field
		Field field = new Field();
		field.setup(1, 1, new Cube());
		field.setup(new Autobot("004",Robot.RED,"LRL",Field.LEFT));
		field.setup(2,2,new Scale("RS",Robot.RED));
		
		// save as string
		String s1 = field.save();
		
		// create new field and load from string
		field = new Field();
		field.load(s1);
		
		// make sure fields are the same
		String s2 = field.save();
		assertEquals(s1, s2);
	}

}
