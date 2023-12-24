
/* GamePanel class acts as the main "game loop" - continuously runs the game and calls whatever needs to be called

Child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	// dimensions of window
	public static final int GAME_WIDTH = 900;
	public static final int GAME_HEIGHT = 550;
	public static final int TITLE_SIZE = 120;
	public static final int FONT_SIZE = 30;
	public static final int TAB_HEIGHT = 100;
	public static final int TAB_WIDTH = 30;
	public static final int TAB_X = GAME_WIDTH/7 + 5;
	
	
	
	public Thread gameThread;
	public Graphics graphics;
	
	
	AffineTransform affineTransform = new AffineTransform();
	Font font = new Font(null, Font.PLAIN, 25);  
	Font rotatedFont;
	
	Image image;
	
	public BufferedImage portalImage = ImageIO.read(new File("Images/Start_Portal.png"));
	public BufferedImage menuBackground = ImageIO.read(new File("Images/menu.png"));
	public BufferedImage openChestImage = ImageIO.read(new File("Images/OpenChest.png"));
	public BufferedImage closeedChestImage = ImageIO.read(new File("Images/ClosedChest.png"));
	public BufferedImage iceImage = ImageIO.read(new File("Images/Ice.png"));
	public BufferedImage ladderImage = ImageIO.read(new File("Images/Ladder.png"));
	public BufferedImage stoneImage = ImageIO.read(new File("Images/Stone.png"));
	
	public int alpha = 0;

	public boolean mainMenu = true;
	public boolean edit = true;
	public boolean alphaUp = true;
	public boolean sidebarPressed = false;
	
	public String tabPressed = "blocks";

	public Block curDragging;

	public Portal tabPortal;
	public Stone tabStone;
	
	
	ArrayList<Block> elements, sidebar;

	public GamePanel() throws IOException {
		this.setFocusable(true); // make everything in this class appear on the screen
		this.addKeyListener(this); // start listening for keyboard input
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		affineTransform.rotate(Math.toRadians(90), 0, 0);
		rotatedFont = font.deriveFont(affineTransform);

		elements = new ArrayList<Block>(); sidebar = new ArrayList<Block>();
		
		tabPortal = new Portal(TAB_X - 110, 20, Portal.width, Portal.height, portalImage);
		tabStone = new Stone(TAB_X - 110, 100, Stone.width, Stone.height, stoneImage);
		
		sidebar.add(tabPortal); sidebar.add(tabStone);
		
		// add the MousePressed method from the MouseAdapter - by doing this we can
		// listen for mouse input. We do this differently from the KeyListener because
		// MouseAdapter has SEVEN mandatory methods - we only need one of them, and we
		// don't want to make 6 empty methods

		this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

		// make this class run at the same time as other classes (without this each
		// class would "pause" while another class runs). By using threading we can
		// remove lag, and also allows us to do features like display timers in real
		// time!
		gameThread = new Thread(this);
		gameThread.start();
	}

	
	public void paint(Graphics g) {
		//double buffering
		image = createImage(GAME_WIDTH, GAME_HEIGHT); // draw off screen
		graphics = image.getGraphics();
		draw(graphics);// update the positions of everything on the screen
		g.drawImage(image, 0, 0, this); // move the image on the screen

	}

	// call the draw methods in each class to update positions as things move
	public void draw(Graphics g) {

		if (mainMenu) {
			g.drawRect(0, 0, GAME_WIDTH, GAME_HEIGHT);			
			g.setFont(new Font("Impact", Font.PLAIN, FONT_SIZE));
			g.drawImage(menuBackground, 0, 0, this);
			g.setColor(new Color(255,255,255,alpha));
			
			if(alphaUp) alpha+=2;
			else alpha-=2;
			
			if(alpha >= 250) alphaUp = false;
			if(alpha <= 5) alphaUp = true;
			
			g.drawString("Press Enter to Continue", 325, 350);
			
		}
		else if(edit) {
			drawSidebar(g);		
			for(Block b: elements) {
				b.draw(g);
			}
		}

	}

	// call the move methods in other classes to update positions
	// this method is constantly called from run(). By doing this, movements appear
	// fluid and natural. If we take this out the movements appear sluggish and
	// laggy
	public void move() {

	}

	// handles all collision detection and responds accordingly
	public void checkCollision() {

	}

	// run() method is what makes the game continue running without end. It calls
	// other methods to move objects, check for collision, and update the screen
	public void run() {
		// the CPU runs our game code too quickly - we need to slow it down! The
		// following lines of code "force" the computer to get stuck in a loop for short
		// intervals between calling other methods to update the screen.
		long lastTime = System.nanoTime();
		double amountOfTicks = 60;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long now;

		while (true) { // this is the infinite game loop
			now = System.nanoTime();
			delta = delta + (now - lastTime) / ns;
			lastTime = now;

			// only move objects around and update screen if enough time has passed
			if (delta >= 1) {
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
	}

	// if a key is pressed, we'll send it over to the PlayerBall class for
	// processing
	public void keyPressed(KeyEvent e) {

		if(mainMenu) {
			if(e.getKeyCode() == 10) {
				mainMenu = false;
				edit = true;
			}
		}
		
	}

	// if a key is released, we'll send it over to the PlayerBall class for
	// processing
	public void keyReleased(KeyEvent e) {

	}


	public void keyTyped(KeyEvent e) {

		
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (mainMenu) {

			
		} else if (edit) {
			//checks if a block is pressed then allow it to be dragged
			for (Block b : elements) {
				if (b.x <= e.getX() && b.x + b.width >= e.getX() && b.y <= e.getY() && b.y + b.height >= e.getY()) {
					b.mousePressed(e);
					curDragging = b;
					sidebarPressed = false;
				}
			}
			
			for (Block b : sidebar) {
				if (b.x <= e.getX() && b.x + b.width >= e.getX() && b.y <= e.getY() && b.y + b.height >= e.getY()) {
					b.mousePressed(e);
					curDragging = b;
					sidebarPressed = true;
				}
			}
			
			//checks if a tab is pressed
			if(e.getX() >= TAB_X && e.getX() <= TAB_X + TAB_WIDTH && e.getY() <= 3 * TAB_HEIGHT + 60) {
				if(e.getY() >= 2 * TAB_HEIGHT + 20) {
					//powerups was chosen
					tabPressed = "powerups";
				}
				else if(e.getY() >= TAB_HEIGHT) {
					//enemies was chosen
					tabPressed = "enemies";
				}
				else {
					//blocks was chosen
					tabPressed = "blocks";
				}
			}
			
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		curDragging = null;

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (edit && curDragging != null) {
			if(sidebarPressed) {
				if(curDragging.equals(tabPortal)) {
					try {elements.add(new Portal(TAB_X - 110, 20, Portal.width, Portal.height, portalImage));} catch(IOException IOE) {}
					curDragging = elements.get(elements.size() - 1);
				}
				
				if(curDragging.equals(tabStone)) {
					try {elements.add(new Stone(TAB_X - 110, 100, Stone.width, Stone.height, stoneImage));} catch(IOException IOE) {}
					curDragging = elements.get(elements.size() - 1);
				}
				
				sidebarPressed = false;
			}
			curDragging.mouseDragged(e);
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {


	}
	
	public void drawSidebar(Graphics g) {
		g.fillRect(TAB_X - 5, 0, 5, GAME_HEIGHT);
		
		if(tabPressed.equals("blocks")) {
			g.setColor(Color.green);
			g.fillRect(TAB_X, 0, TAB_WIDTH + 20, TAB_HEIGHT);
			g.setColor(Color.black); g.setFont(rotatedFont); 
			g.drawString("Blocks", GAME_WIDTH/7 + 20, 10);
			
			tabPortal.draw(g);
			tabStone.draw(g);
			
		}else {
			g.setColor(Color.green);
			g.fillRect(TAB_X, 0, TAB_WIDTH, TAB_HEIGHT);
			g.setColor(Color.black); g.setFont(rotatedFont); 
			g.drawString("Blocks", GAME_WIDTH/7 + 10, 10);
		}
		
		if(tabPressed.equals("enemies")) {
			g.setColor(Color.orange);
			g.fillRect(TAB_X, TAB_HEIGHT, TAB_WIDTH + 20, TAB_HEIGHT + 20);
			g.setColor(Color.black); g.setFont(rotatedFont); 
			g.drawString("Enemies", GAME_WIDTH/7 + 20, 10 + TAB_HEIGHT);
		}else {
			g.setColor(Color.orange);
			g.fillRect(TAB_X, TAB_HEIGHT, TAB_WIDTH, TAB_HEIGHT + 20);
			g.setColor(Color.black); g.setFont(rotatedFont); 
			g.drawString("Enemies", GAME_WIDTH/7 + 10, 10 + TAB_HEIGHT);
		}
		
		if(tabPressed.equals("powerups")) {
			g.setColor(Color.CYAN);
			g.fillRect(TAB_X, 2*TAB_HEIGHT+20, TAB_WIDTH + 20, TAB_HEIGHT + 40);
			g.setColor(Color.black); g.setFont(rotatedFont); 
			g.drawString("Powerups", GAME_WIDTH/7 + 20, 2 * TAB_HEIGHT + 30);
		}else {
			g.setColor(Color.CYAN);
			g.fillRect(TAB_X, 2*TAB_HEIGHT+20, TAB_WIDTH, TAB_HEIGHT + 40);
			g.setColor(Color.black); g.setFont(rotatedFont); 
			g.drawString("Powerups", GAME_WIDTH/7 + 10, 2 * TAB_HEIGHT + 30);
		}
		
		
		
	}
	
}