package powerup.engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import powerup.field.Cube;
import powerup.field.Field;
import powerup.field.FieldObject;
import powerup.field.Robot;
import powerup.field.Scale;
import powerup.field.Wall;
import powerup.server.GameClient;

public class GraphicsController extends Canvas  {
	
	private static final long serialVersionUID = 1L;
	private static final int SCORE_AREA = 100;
	private static final int HEIGHT = (Field.ROWS*Block.BLOCKSIZE)+SCORE_AREA;
	private static final int WIDTH = Field.COLS*Block.BLOCKSIZE;
	private static final int SCORE_POSITION_Y = HEIGHT-80;

	private GameClient gameClient;
	private BufferStrategy strategy;
	private List<Block> blocks = new ArrayList<Block>();
	private BufferedImage[] imageArray = new BufferedImage[10];
	private Map<String,Block> robotMap = new HashMap<String,Block>();
	private Map<String,Block> cubeMap = new HashMap<String,Block>();
	
	public BufferedImage getImage(String filename) {
		BufferedImage sourceImage = null;
		
		try {
			URL url = this.getClass().getClassLoader().getResource(filename);
			if (url == null) {
				throw new RuntimeException("Can't find filename: "+filename);
			}
			sourceImage = ImageIO.read(url);
			//System.out.println("read image "+filename+" width:"+sourceImage.getWidth()+" height:"+sourceImage.getHeight());
		} catch (IOException e) {
			throw new RuntimeException("Failed to load: "+filename);
		}
		
		return sourceImage;
	}
	


	private void drawLabels(Graphics2D g,Field field) {
		String stats = "";
		
		// draw numbers on scales
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
		
		// draw numbers on robots
		for (Robot robot:field.getRobotList()) {
			stats = stats+robot.getName()+": cubes="+robot.getShotsMade()+"     ";
			g.drawString(""+robot.getName(),(robot.getCol()*Block.BLOCKSIZE+10),robot.getRow()*Block.BLOCKSIZE+20);
		}
		
		stats = stats + "  AI level:"+field.getRobotLevel(); 
		
		// draw stats and score
		g.setColor(Color.white);
		drawCenterX(g,"Blue "+field.getBlueScore(),SCORE_POSITION_Y,100);
		drawCenterX(g,"Red "+field.getRedScore(), SCORE_POSITION_Y,-100);
		drawCenterX(g,"Time "+field.getGameSecs(), SCORE_POSITION_Y,0);
		if (field.getCountDown() > 0)
			drawCenterX(g,"Game starting in "+field.getCountDown(), SCORE_POSITION_Y,-300);
		
		
		if (field.getGameSecs() == 0 && (field.getBlueScore() > 0 || field.getRedScore() >0)) {
			if (field.getBlueScore() > field.getRedScore()) {
				drawCenterX(g,"Blue is the winner! Press 0 to reset.", SCORE_POSITION_Y+25,0);
			}
			if (field.getRedScore() > field.getBlueScore()) {
				drawCenterX(g,"Red is the winner! Press 0 to reset.", SCORE_POSITION_Y+25,0);
			}
			if (field.getBlueScore() == field.getRedScore()) {
				drawCenterX(g,"Tie Game! Press 0 to reset.", SCORE_POSITION_Y+25,0);
			}
		}
		
		
		if (field.getGameSecs() == 0) {
			drawCenterX(g,"Press 1-6 to join the game, 7 to play against bots, 8 to increase difficulty (AI level:"+field.getRobotLevel()+")", SCORE_POSITION_Y+50,0);
			drawCenterX(g,"Press 9 to start, 0 to reset", SCORE_POSITION_Y+75,0);
		} else {
			drawCenterX(g, stats, SCORE_POSITION_Y+50,0);	
		}
	}
	
	private void drawCenterX(Graphics2D g, String s, int y, int offset) {
		int fontSize = g.getFont().getSize();
		int x = (WIDTH/2)-(s.length()*fontSize/2/2)-offset;
		g.drawString(s,x,y);
	}
	


	private void setupImages(Field field) {
		Util.log("GraphicsController.setupImages");
		
		imageArray[0] = getImage("robot-red.png");
		imageArray[1] = getImage("robot-red-cube.png");
		imageArray[2] = getImage("block-red-50.png");
		imageArray[3] = getImage("block-blue-50.png");
		imageArray[4] = getImage("robot-blue.png");
		imageArray[5] = getImage("robot-blue-cube.png");
		imageArray[6] = getImage("block-yellow-50.png");
		imageArray[7] = getImage("block-gray-50.png");
		
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
						addRobot(s);
					}
					if (fo instanceof Cube) {
						Cube s = (Cube) fo;
						addCube(s);
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
	
	private void addRobot(Robot s) {
		BufferedImage[] i;
		if (s.getAlliance().equalsIgnoreCase(Robot.RED)) {
			i = new BufferedImage[2];
			i[0] = imageArray[1];
			i[1] = imageArray[0];
		} else {
			i = new BufferedImage[2];
			i[0] = imageArray[5];
			i[1] = imageArray[4];
		}
		Block b = new Block(i,s);
		blocks.add(b);
		robotMap.put(s.getName(),b);
	}
	
	private void addCube(Cube s) {
		BufferedImage[] i = new BufferedImage[1];
		i[0] = imageArray[6];
		
		Block b = new Block(i,s);
		blocks.add(b);
		cubeMap.put(s.getName(),b);
	}
		
	public void setup(GameClient gameClient) {
		Util.log("GraphicsController.setup");
		
		this.gameClient = gameClient;
		
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
		
		Util.log("GraphicsController.setup complete");

	}
	
	

	public GameClient getGameClient() {
		return gameClient;
	}



	public void drawField(Field field) {
		Util.log("RobotController.drawField",10);

		if (imageArray[0] == null)
			setupImages(field);

		// init the graphics system to redraw the map
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0,HEIGHT-SCORE_AREA,WIDTH,HEIGHT);
		g.setColor(Color.black);
		g.fillRect(0,0,WIDTH,HEIGHT-SCORE_AREA);
		
		
		// remove any deleted robots
		for (Block b:blocks) {
			if (b.getFieldObject() instanceof Robot) {
				Robot r = (Robot) b.getFieldObject();
				if (field.find(r.getName()) == null) {
					r.setDeleted(true);
					if (robotMap.get(r.getName()) != null) {
						robotMap.remove(r.getName(),b);
					}
				}
			}
		}
		
		// add in any new robots
		for (Robot r:field.getRobotList()) {
			if (robotMap.get(r.getName()) == null) {
				addRobot(r);
			}
		}
		
		// add in any new cubes
		for (Cube c:field.getCubeList()) {
			if (cubeMap.get(c.getName()) == null) {
				addCube(c);
			}
		}


		// remove deleted blocks
		blocks.removeIf(b -> b.getFieldObject().isDeleted() == true);
		
		// draw the blocks
		for (Block b:blocks) {
			//Util.log("BLOCK "+b.getFieldObject().getName()+" "+b.getFieldObject().isDeleted());
			
			// update with latest field data
			FieldObject fo = field.find(b.getFieldObject().getName());
			if (fo != null) {
				b.setFieldObject(fo);
				b.draw(g);
			} else {
				//Util.log("FO not found for block "+b.getFieldObject().getName());
			}
		}
		
		//do this last so the labels show on top
		drawLabels(g,field);
		
		// show the redrawn map
		g.dispose();
		strategy.show();
		
		Util.log("RobotController.drawField complete",10);			
	}






}
