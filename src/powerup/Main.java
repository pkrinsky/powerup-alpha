package powerup;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
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
	private long GAME_SECS = 60;
	private long gameSecs = GAME_SECS;
	private long lastScoreSecs = 0;
	private int redScore = 0;
	private int blueScore = 0;
	
	private int HEIGHT = Field.ROWS*Block.BLOCKSIZE;
	private int WIDTH = Field.COLS*Block.BLOCKSIZE;
	
	private BufferedImage[] imageArray = new BufferedImage[10];
	
	public BufferedImage getImage(String filename) {
		BufferedImage sourceImage = null;
		
		try {
			URL url = this.getClass().getClassLoader().getResource(filename);
			if (url == null) {
				throw new RuntimeException("Can't find filename: "+filename);
			}
			sourceImage = ImageIO.read(url);
			System.out.println("read image "+filename+" width:"+sourceImage.getWidth()+" height:"+sourceImage.getHeight());
		} catch (IOException e) {
			throw new RuntimeException("Failed to load: "+filename);
		}
		
		return sourceImage;
	}
	
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
		// [close switch][scale][far switch]
		
		String gamedata = "LRL";
		
		int col2 = Field.COL2;
		int col1 = Field.COL1;
		int col3 = Field.COL3;
				
		imageArray[0] = getImage("robot-red.png");
		imageArray[1] = getImage("robot-red-cube.png");
		imageArray[2] = getImage("block-red-50.png");
		imageArray[3] = getImage("block-blue-50.png");
		imageArray[4] = getImage("robot-blue.png");
		imageArray[5] = getImage("robot-blue-cube.png");
		imageArray[6] = getImage("block-yellow-50.png");
		imageArray[7] = getImage("block-gray-50.png");
		
		robotControllerList.add(new RobotController(new RobotRex("001",Robot.BLUE,gamedata,Field.MIDDLE)));
		robotControllerList.add(new RobotController(new Autobot("002",Robot.BLUE,gamedata,Field.LEFT)));
		robotControllerList.add(new RobotController(new Autobot("003",Robot.BLUE,gamedata,Field.RIGHT)));
		robotControllerList.add(new RobotController(new Autobot("004",Robot.RED,gamedata,Field.LEFT)));
		robotControllerList.add(new RobotController(new Autobot("005",Robot.RED,gamedata,Field.MIDDLE)));
		robotControllerList.add(new RobotController(new Autobot("006",Robot.RED,gamedata,Field.RIGHT)));
		myController = robotControllerList.get(0);
		
		for (RobotController rc:robotControllerList) {
			field.setup(rc.getRobot());	
		}
		
		field.setup(col2,3,new Scale("RS",Robot.RED)); 
		field.setup(col2,4,new Wall());
		field.setup(col2,5,new Wall());
		field.setup(col2,6,new Wall());
		field.setup(col2,7,new Wall());
		field.setup(col2,8,new Wall());
		field.setup(col2,9,new Wall());
		field.setup(col2,10,new Wall());
		field.setup(col2,11,new Scale("BS",Robot.BLUE));

		field.setup(col1,4,new Scale("BNS",Robot.BLUE));
		field.setup(col1,5,new Wall());
		field.setup(col1,6,new Wall());
		field.setup(col1,7,new Wall());
		field.setup(col1,8,new Wall());
		field.setup(col1,9,new Wall());
		field.setup(col1,10,new Scale("RFS",Robot.RED));

		field.setup(col3,4,new Scale("BFS",Robot.BLUE));
		field.setup(col3,5,new Wall());
		field.setup(col3,6,new Wall());
		field.setup(col3,7,new Wall());
		field.setup(col3,8,new Wall());
		field.setup(col3,9,new Wall());
		field.setup(col3,10,new Scale("RNS",Robot.RED));
		
		
		for (int i=0;i<5;i++) {
			field.setup(col1+1,5+i,new Cube());
			field.setup(col1-1,5+i,new Cube());
			field.setup(col3+1,5+i,new Cube());
			field.setup(col3-1,5+i,new Cube());
		}
		
		
		field.print();

		// init all the blocks based on the field info
		for (int r=0;r<Field.ROWS;r++) {
			for (int c=0;c<Field.COLS;c++) {
				FieldObject fo = field.getFieldObject(c,r);
				if (fo != null) {
					if (fo instanceof Scale) {
						Scale s = (Scale) fo;
						if (s.getAlliance().equals(Robot.RED)) {
							BufferedImage[] i = new BufferedImage[1];
							i[0] = imageArray[2];
							blocks.add(new Block(i,fo));	
						} else {
							BufferedImage[] i = new BufferedImage[1];
							i[0] = imageArray[3];
							blocks.add(new Block(i,fo));
						}
							
					}
					if (field.getFieldObject(c,r) instanceof Robot) {
						Robot s = (Robot) fo;
						if (s.getAlliance().equalsIgnoreCase(Robot.RED)) {
							BufferedImage[] i = new BufferedImage[2];
							i[0] = imageArray[1];
							i[1] = imageArray[0];
							blocks.add(new Block(i,fo));
						} else {
							BufferedImage[] i = new BufferedImage[2];
							i[0] = imageArray[5];
							i[1] = imageArray[4];
							blocks.add(new Block(i,fo));
						}
							
					}
					if (fo instanceof Cube) {
						BufferedImage[] i = new BufferedImage[1];
						i[0] = imageArray[6];
						blocks.add(new Block(i,fo));
					}
					if (fo instanceof Wall) {
						BufferedImage[] i = new BufferedImage[1];
						i[0] = imageArray[7];
						blocks.add(new Block(i,fo));
							
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
			
			// if there is time left calc the score and move the robots
			if (gameSecs > 0) {
				// calc and display the score but only do it once a second
				gameSecs = GAME_SECS - (System.currentTimeMillis()-starttime)/1000;
				if (gameSecs != lastScoreSecs) {
					calcScore();
					lastScoreSecs = gameSecs;
				}
					
				g.drawString("Blue "+blueScore+" Red "+redScore+" Time "+gameSecs, WIDTH-400,20);
				
				// move the robots
				for (RobotController r:robotControllerList) {
					r.move(field);
				}
			} else {
				g.drawString("Game Over. Blue "+blueScore+" Red "+redScore, WIDTH-400,20);
			}
				
			// draw the blocks
			blocks.removeIf(b -> b.getFieldObject().isDeleted() == true);
			for (Block b:blocks) {
				b.draw(g);
			}
			
			//do this last so the labels show on top
			drawLabels(g);
			
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

	private void drawLabels(Graphics2D g) {
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
		
		g.setColor(Color.black);
		for (RobotController rc:robotControllerList) {
			Robot robot = rc.getRobot();
			g.drawString(""+robot.getName(),(robot.getCol()*Block.BLOCKSIZE+10),robot.getRow()*Block.BLOCKSIZE+20);	
		}
		
		
	}
	
	public static void main(String argv[]) {
		Main main = new Main();
		main.setupField();
		main.setup();
		main.gameLoop();
	}	
}

