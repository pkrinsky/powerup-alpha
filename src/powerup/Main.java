package powerup;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import powerup.engine.Block;
import powerup.engine.KeyHandler;
import powerup.engine.RobotController;
import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;
import powerup.field.Scale;
import powerup.field.Wall;
import powerup.robot.Autobot;
import powerup.robot.RobotRex;

@SuppressWarnings("serial")
public class Main extends Canvas {
	
	private boolean gameRunning = true;
	private BufferStrategy strategy;
	List<Block> blocks = new ArrayList<Block>();
	private List<RobotController> robotControllerList= new ArrayList<RobotController>();
	
	private Field field = new Field();
	private RobotController myController = null;
	private int redScore = 0;
	private int blueScore = 0;
	private long gamesecs = 0;
	private long lastscore = 0;
	
	private int HEIGHT = Field.ROWS*Block.BLOCKSIZE;
	private int WIDTH = Field.COLS*Block.BLOCKSIZE;
	
	
	private void setup() {
		JFrame container = new JFrame("Powerup");
		
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		panel.setLayout(null);
		
		setBounds(0,0,WIDTH,HEIGHT);
		panel.add(this);
		
		setIgnoreRepaint(true);
		
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		/*
		addMouseListener(new MouseAdapter() {
			public void  mouseClicked(MouseEvent e) {
				clickX = e.getX();
				clickY = e.getY();
				System.out.println("mouse clicked "+e.getX()+" "+e.getY());
				//blue.setTankDestination(clickX,  clickY);
		    }
			
		});
		*/
		
		addKeyListener(new KeyHandler(this));
		
		requestFocus();

		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
	}
	
	private void setupField() {
		
		// need to randomize this
		String gamedata = "LRL";
		
		int col2 = Field.COL2;
		int col1 = Field.COL1;
		int col3 = Field.COL3;
				
		
		robotControllerList.add(new RobotController(new RobotRex("RexBM",Robot.BLUE,gamedata,Field.MIDDLE)));
		robotControllerList.add(new RobotController(new Autobot("AutobotBL",Robot.BLUE,gamedata,Field.LEFT)));
		robotControllerList.add(new RobotController(new Autobot("AutobotBR",Robot.BLUE,gamedata,Field.RIGHT)));
		robotControllerList.add(new RobotController(new Autobot("AutobotRL",Robot.RED,gamedata,Field.LEFT)));
		robotControllerList.add(new RobotController(new Autobot("AutobotRM",Robot.RED,gamedata,Field.MIDDLE)));
		robotControllerList.add(new RobotController(new Autobot("AutobotRR",Robot.RED,gamedata,Field.RIGHT)));
		myController = robotControllerList.get(0);
		
		for (RobotController rc:robotControllerList) {
			field.setup(rc.getRobot());	
		}
		
		field.setup(col2,3,new Scale("RS","R")); 
		field.setup(col2,4,new Wall());
		field.setup(col2,5,new Wall());
		field.setup(col2,6,new Wall());
		field.setup(col2,7,new Wall());
		field.setup(col2,8,new Wall());
		field.setup(col2,9,new Wall());
		field.setup(col2,10,new Wall());
		field.setup(col2,11,new Scale("BS","B"));

		field.setup(col1,4,new Scale("BNS","B"));
		field.setup(col1,5,new Wall());
		field.setup(col1,6,new Wall());
		field.setup(col1,7,new Wall());
		field.setup(col1,8,new Wall());
		field.setup(col1,9,new Wall());
		field.setup(col1,10,new Scale("RFS","R"));

		field.setup(col3,4,new Scale("BFS","B"));
		field.setup(col3,5,new Wall());
		field.setup(col3,6,new Wall());
		field.setup(col3,7,new Wall());
		field.setup(col3,8,new Wall());
		field.setup(col3,9,new Wall());
		field.setup(col3,10,new Scale("RNS","R"));
		
		
		for (int i=0;i<5;i++) {
			field.setup(col1+1,5+i,new Cube());
			field.setup(col1-1,5+i,new Cube());
			field.setup(col3+1,5+i,new Cube());
			field.setup(col3-1,5+i,new Cube());
		}
		
		
		field.print();

		// init all the blocks based on the field info
		String image = "";
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				FieldObject fo = field.getFieldObject(c,r);
				if (fo != null) {
					if (fo instanceof Scale) {
						Scale s = (Scale) fo;
						if (s.getAlliance().equals("R")) {
							image = "block-red-50.png";
						} else {
							image = "block-blue-50.png";
						}
						blocks.add(new Block(image,fo));	
					}
					if (field.getFieldObject(c,r) instanceof Robot) {
						Robot s = (Robot) fo;
						if (s.getAlliance().equalsIgnoreCase("R")) {
							image = "robot-red.png";
						} else {
							image = "robot.png";
						}
						blocks.add(new Block(image,fo));	
					}
					if (fo instanceof Cube) {
						blocks.add(new Block("block-yellow-50.png",fo));	
					}
					if (fo instanceof Wall) {
						blocks.add(new Block("block-gray-50.png",fo));	
					}
				}
			}
		}

	}	
		
	public void keyEvent(KeyEvent e) {
		System.out.println("Main.keyEvent char:"+e.getKeyChar()+" code:"+e.getKeyCode());

		if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		
		myController.key(e.getKeyChar());
	}	


	public void gameLoop() {
		long starttime = System.currentTimeMillis();

		while (gameRunning) {

			// init the graphics system to redraw the map
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,WIDTH,HEIGHT);
			g.setColor(Color.white);
			
			// calc and display the score
			gamesecs = (System.currentTimeMillis()-starttime)/1000;
			if (gamesecs != lastscore) {
				calcScore();
				lastscore = gamesecs;
			}
			g.drawString("Blue "+blueScore+" Red "+redScore+" Time "+gamesecs, WIDTH-400,20);
			
			// move the robots
			for (RobotController r:robotControllerList) {
				r.move(field);
			}
			
			// draw the blocks
			blocks.removeIf(b -> b.getFieldObject().isDeleted() == true);
			for (Block b:blocks) {
				b.draw(g);
			}
			
			//do this last so the numbers show on top
			printCubes(g);
			
			// show the redrawn map
			g.dispose();
			strategy.show();
			
			// wait for a little then start again
			try { Thread.sleep(250); } catch (Exception e) {}
		}
	}
	
	private void calcScore() {
		Scale r = (Scale) field.find("RS");
		Scale b = (Scale) field.find("BS");
		if (r.getNumCubes() > b.getNumCubes()) {
			redScore++;
		}
		if (b.getNumCubes() > r.getNumCubes()) {
			blueScore++;
		}
		r = (Scale) field.find("RFS");
		b = (Scale) field.find("BNS");
		if (b.getNumCubes() > r.getNumCubes()) {
			blueScore++;
		}
		r = (Scale) field.find("RNS");
		b = (Scale) field.find("BFS");
		if (r.getNumCubes() > b.getNumCubes()) {
			redScore++;
		}
	}

	private void printCubes(Graphics2D g) {
		Scale r = (Scale) field.find("RS");
		g.drawString(""+r.getNumCubes(),r.getCol()*Block.BLOCKSIZE+10,r.getRow()*Block.BLOCKSIZE+20);
		
		r = (Scale) field.find("BS");
		g.drawString(""+r.getNumCubes(),(r.getCol()*Block.BLOCKSIZE+10),r.getRow()*Block.BLOCKSIZE+20);

		r = (Scale) field.find("RFS");
		g.drawString(""+r.getNumCubes(),(r.getCol()*Block.BLOCKSIZE+10),r.getRow()*Block.BLOCKSIZE+20);

		r = (Scale) field.find("BFS");
		g.drawString(""+r.getNumCubes(),(r.getCol()*Block.BLOCKSIZE+10),r.getRow()*Block.BLOCKSIZE+20);

		r = (Scale) field.find("RNS");
		g.drawString(""+r.getNumCubes(),(r.getCol()*Block.BLOCKSIZE+10),r.getRow()*Block.BLOCKSIZE+20);
		
		r = (Scale) field.find("BNS");
		g.drawString(""+r.getNumCubes(),(r.getCol()*Block.BLOCKSIZE+10),r.getRow()*Block.BLOCKSIZE+20);
	}
	
	public static void main(String argv[]) {
		Main main = new Main();
		main.setupField();
		main.setup();
		main.gameLoop();
	}	
}

