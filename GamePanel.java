
/* GamePanel class acts as the main "game loop" - continuously runs the game and calls whatever needs to be called

Child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/
import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;

//imports for file io
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;


public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	// dimensions of window
	public static final int GAME_WIDTH = 900;
	public static final int GAME_HEIGHT = 550;
	public static final int TITLE_SIZE = 120;
	public static final int FONT_SIZE = 30;
	public static final int TAB_HEIGHT = 100;
	public static final int TAB_WIDTH = 30;
	public static final int TAB_X = GAME_WIDTH / 7 + 5;

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
	public BufferedImage playBackground = ImageIO.read(new File("Images/back.png"));

	
	public Player knight;

	public int alpha = 0;

	public boolean mainMenu = true;
	public boolean edit = false;
	public boolean levelSelect = false;
	public boolean alphaUp = true;
	public boolean sidebarPressed = false;
	public boolean fixed = false;
	public boolean play = false;

	public int indicatorPos = 250;

	public String tabPressed = "blocks";

	// declare level widget variables
	public int totalHeight;
	public int numButtons = 18; // to be changed once file IO works

	public Block curDragging, curSelected;

	public Portal tabPortal;
	public Stone tabStone;
	public Ice tabIce;
	public Ladder tabLadder;

	ArrayList<Block> elements, sidebar;
	Block hover = null;

	public BufferedWriter writer;
	public boolean levelSaved = false;
	public String displaySaved = "LEVEL SAVED :)";
	public ArrayList<String> levels = new ArrayList<>();
	public String title = "";
	public String updatedSave = "";
	public ArrayList<String> names = new ArrayList<>();

	public GamePanel(boolean levelSelect) throws IOException {
		if(levelSelect) {
			this.levelSelect = true; 
			mainMenu = false;
			edit = false;
		}
		//read the titles of each entry in LevelSave into arraylist names so no duplicate is made
		readFirstWords();

		this.setFocusable(true); // make everything in this class appear on the screen
		this.addKeyListener(this); // start listening for keyboard input
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		knight = new Player(200,100,20,30);

		// code to rotate the text for the "block" description
		affineTransform.rotate(Math.toRadians(90), 0, 0);
		rotatedFont = font.deriveFont(affineTransform);

		elements = new ArrayList<Block>();
		sidebar = new ArrayList<Block>();

		tabPortal = new Portal(TAB_X - 110, 20, Portal.width, Portal.height, portalImage);
		tabStone = new Stone(TAB_X - 110, 100, Stone.width, Stone.height, stoneImage);
		tabIce = new Ice(TAB_X - 110, 145, Ice.width, Ice.height, iceImage);

		sidebar.add(tabPortal);
		sidebar.add(tabStone);
		sidebar.add(tabIce);

		// add the MousePressed method from the MouseAdapter - by doing this we can
		// listen for mouse input. We do this differently from the KeyListener because
		// MouseAdapter has SEVEN mandatory methods - we only need one of them, and we
		// don't want to make 6 empty methods

		// 62 represents the amount of pixels one new level entry takes
		totalHeight = 62 * numButtons;

		this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

		// make this class run at the same time as other classes (without this each
		// class would "pause" while another class runs). By using threading we can
		// remove lag, and also allows us to do features like display timers in real
		// time!
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void paint(Graphics g) {
		// double buffering
		image = createImage(GAME_WIDTH, GAME_HEIGHT); // draw off screen
		graphics = image.getGraphics();
		draw((Graphics2D) graphics);// update the positions of everything on the screen
		g.drawImage(image, 0, 0, this); // move the image on the screen

	}

	// call the draw methods in each class to update positions as things move
	public void draw(Graphics2D g) {

		if (mainMenu) {   
			g.setFont(new Font("Impact", Font.PLAIN, FONT_SIZE));
			g.drawImage(menuBackground, 0, 0, this);
			g.setColor(Color.white);
			// display text for the title
			g.drawString("Dungeon Dash", GAME_WIDTH / 2 - 100, 60);
			g.setColor(new Color(255, 255, 255, alpha));


			// causes the text to fade in and out by adjusting transparancy value
			if (alphaUp)
				alpha += 2;
			else
				alpha -= 2;

			if (alpha >= 250)
				alphaUp = false;
			if (alpha <= 100)
				alphaUp = true;

			// display text for the menu
			g.drawString("> ", 300, indicatorPos);


			g.drawString("Level selection", 325, 250);
			g.drawString("Create new dungeon", 325, 320);

			// g.drawString("Press Enter to Continue", 325, 350);

		} else if (edit) {

			//draw strings for user instruction for save and play 
			
			g.drawImage(resize(playBackground, GAME_WIDTH*2, GAME_HEIGHT), GAME_WIDTH/7, 0, this);
			g.setColor(Color.white);
			g.drawString("Enter \"1\" to SAVE level.", 200, 10);
			g.drawString("Enter \"2\" to PLAY level.", 200, 25);
			
			g.setColor(Color.black);
			drawSidebar(g);
			for (Block b : elements) {
				b.draw(g);
			}



			// check if its being hovered and makes it like transparent
			if (hover != null) {
				Graphics2D g2d = (Graphics2D) g;
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
				g2d.setComposite(ac);
				hover.draw(g2d);
			}
			
			if(curSelected != null) {
				g.setColor(Color.yellow);
				g.fillRect(curSelected.x, curSelected.y, curSelected.width, 3);
				g.fillRect(curSelected.x, curSelected.y, 3, curSelected.height);
				g.fillRect(curSelected.x + curSelected.width, curSelected.y, 3, curSelected.height + 3);
				g.fillRect(curSelected.x, curSelected.y + curSelected.height, curSelected.width, 3);
			}
			
			//code for drawing the knight animation
			knight.draw(g);



		} else if (levelSelect) {
			// to be filled (draw the image background?)

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

		if (mainMenu) {
			if (e.getKeyCode() == 10 && indicatorPos == 320){
				levelSelect = false;
				edit = true;
				mainMenu = false;
			}
			else if (e.getKeyCode() == 10 && indicatorPos ==250) {
				//enter the levelSelect menu


				mainMenu = false;
				edit = false;
				levelSelect = true;

				//create a new gameframe in the levelSelect menu
				try {
					new GameFrame(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_UP && indicatorPos != 250) {
				indicatorPos = 250;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN && indicatorPos == 250) {
				indicatorPos = 320;
			} 
		} else if (levelSelect) {
			if (e.getKeyCode() == 10) {
				levelSelect = false;
				edit = true;

				try {
					new GameFrame(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}

		} else if (edit) {
			if(e.getKeyCode() == 8) {
				if(curSelected != null) {
					elements.remove(curSelected);
					curSelected = null;
				}

				//check if file is saved OR saved and played
			} else if(e.getKeyCode() == KeyEvent.VK_1) {
				//save the file

				updatedSave = "";
				if (!levelSaved) {
					// Create a JTextField for user input
					JTextField textField = new JTextField();

					// Show an input dialog with the text field
					int option = JOptionPane.showOptionDialog(this, textField,
							"Name your level!", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, null, null);

					// Check if the user clicked OK
					if (option == JOptionPane.OK_OPTION) {
						// Get the entered name from the text field
						title = textField.getText().stripTrailing(); // get the info from text box without whitespace
						if(!names.contains(title)) {
							for(Block b: elements) {
								updatedSave += b.toString() + " ";
							}
							System.out.println(title);
							replaceLine(title, updatedSave);

							addTitle(title); //add the title

							System.out.println(names);
							// Display a message indicating that the level has been saved
							JOptionPane.showMessageDialog(this, "Level saved!", "Save Confirmation", JOptionPane.INFORMATION_MESSAGE);
							levelSaved = true;
						} else {
							// Display a message indicating that the level has not been saved
							JOptionPane.showMessageDialog(this, "Title already in use. Please try again.", "Invalid Save", JOptionPane.INFORMATION_MESSAGE);
						}

						
						
						
						
					} else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
						JOptionPane.showMessageDialog(this, "Save cancelled.", "Save Confirmation", JOptionPane.INFORMATION_MESSAGE);

					}
				} else if (levelSaved) {
					//if previously saved, then update the entry
					for(Block b: elements) {
						updatedSave += b.toString() + " ";
					}
					System.out.println(elements + "       " + updatedSave);
					replaceLine(title, updatedSave);
				}
				 


				
		


			} else if (e.getKeyCode() == KeyEvent.VK_2) {
				//enter play mode;
				edit = false;
				play = true;

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
			// checks if an existing block is pressed then allow it to be dragged
			boolean chosen = false;
			for (Block b : elements) {
				if (b.x <= e.getX() && b.x + b.width >= e.getX() && b.y <= e.getY() && b.y + b.height >= e.getY()) {
					b.mousePressed(e);
					curDragging = b;
					curSelected = b;
					sidebarPressed = false;
					chosen = true;
				}
			}
			
			if(!chosen) curSelected = null;
			
			// checks if the block pressed is from the sidebar
			for (Block b : sidebar) {
				if (b.x <= e.getX() && b.x + b.width >= e.getX() && b.y <= e.getY() && b.y + b.height >= e.getY()) {
					b.mousePressed(e);
					curDragging = b;
					sidebarPressed = true;
					chosen = true;
				}
			}
			
			

			// checks if a tab is pressed
			if (e.getX() >= TAB_X && e.getX() <= TAB_X + TAB_WIDTH && e.getY() <= 3 * TAB_HEIGHT + 60) {
				if (e.getY() >= 2 * TAB_HEIGHT + 20) {
					// powerups was chosen
					tabPressed = "powerups";
				} else if (e.getY() >= TAB_HEIGHT) {
					// enemies was chosen
					tabPressed = "enemies";
				} else {
					// blocks was chosen
					tabPressed = "blocks";
				}
			}

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		if (edit) {

			//if there IS a block being dragged
			if (curDragging != null) {
				// checks if it is still on the sidebar
				if (curDragging.x <= TAB_X) {
					elements.remove(curDragging); //remove it if it's still on the sidebar and not dragged into the sandbox
				} else if (hover != null) { //if there IS a block being hovered over b the curDragging block
					// loops through all the blocks
					boolean works = true;
					for (int i = 0; i < elements.size(); i++) {
						Block b = elements.get(i);
						// if its the one that is already being dragged continue (no point on checking
						// it to itself)
						if (b == curDragging)
							continue;
						// if the snapped piece is also going to intersect a piece then don't add it
						if (hover.intersects(b)) {
							elements.remove(curDragging);
							works = false;
							break;
						}
					}
					// if the snapped piece does not intersect another piece then you can add it
					if (works) {
						elements.remove(curDragging);
						elements.add(hover);
						curSelected = hover;
					}

				} else if (hover == null && checkAllIntersection(curDragging)) {
					elements.remove(curDragging);
				}
			}

		}
		curDragging = null;
		hover = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (edit && curDragging != null) {
			curSelected = curDragging;
			// checks if the sidebar is being pressed
			if (sidebarPressed) {
				// checks for the element pressed and makes a new element
				if (curDragging.equals(tabPortal)) {
					try {
						elements.add(new Portal(TAB_X - 110, 20, Portal.width, Portal.height, portalImage));
					} catch (IOException IOE) {
					}
					curDragging = elements.get(elements.size() - 1);
				}

				else if (curDragging.equals(tabStone)) {
					try {
						elements.add(new Stone(TAB_X - 110, 100, Stone.width, Stone.height, stoneImage));
					} catch (IOException IOE) {
					}
					curDragging = elements.get(elements.size() - 1);
				}

				else if (curDragging.equals(tabIce)) {
					try {
						elements.add(new Ice(TAB_X - 110, 145, Ice.width, Ice.height, iceImage));
					} catch (IOException IOE) {
					}
					curDragging = elements.get(elements.size() - 1);
				}

				sidebarPressed = false;
			}

			curDragging.mouseDragged(e);

			// loops through all the elements
			boolean intersected = false;
			for (int i = 0; i < elements.size(); i++) {
				Block b = elements.get(i);
				if (b == curDragging)
					continue;
				// checks if it is dragged onto another piece
				if (curDragging.intersects(b)) {
					int tmpX = curDragging.x;
					int tmpY = curDragging.y;
					int centerX = tmpX + curDragging.width / 2;
					int centerY = tmpY + curDragging.height / 2;

					// looks for the nearest edge and forces it there

					//LEFT EDGE
					if (Math.abs(b.x - centerX) <= Math.abs(b.x + b.width - centerX)
							&& Math.abs(b.x - centerX) <= Math.abs(b.y - centerY)
							&& Math.abs(b.x - centerX) <= Math.abs(b.y + b.height - centerY)) {

						//gets the name of the subclass for curDragging so it can create a hover block of the same type 
						//important for file IO
						Class<?> type = curDragging.getClass();
						String className = type.getName();
						if(className.equals("Stone")){
							try {
								hover = new Stone(b.x - curDragging.width, curDragging.y, curDragging.width, curDragging.height,
										curDragging.img);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else if (className.equals("Ice")) {
							try {
								hover = new Ice(b.x - curDragging.width, curDragging.y, curDragging.width, curDragging.height,
										curDragging.img);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} 
					}

					else if (Math.abs(b.x + b.width - centerX) <= Math.abs(b.x - centerX)
							&& Math.abs(b.x + b.width - centerX) <= Math.abs(b.y - centerY)
							&& Math.abs(b.x + b.width - centerX) <= Math.abs(b.y + b.height - centerY)) {
						curDragging.x++;

						//gets the name of the subclass for curDragging so it can create a hover block of the same type 
						//important for file IO
						Class<?> type = curDragging.getClass();
						String className = type.getName();
						if(className.equals("Stone")){
							try {
								hover = new Stone(b.x + b.width, curDragging.y, curDragging.width, curDragging.height,
										curDragging.img);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else if (className.equals("Ice")) {
							try {
								hover = new Ice(b.x + b.width, curDragging.y, curDragging.width, curDragging.height,
										curDragging.img);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} 


					}

					else if (Math.abs(b.y - centerY) <= Math.abs(b.x + b.width - centerX)
							&& Math.abs(b.y - centerY) <= Math.abs(b.x - centerX)
							&& Math.abs(b.y - centerY) <= Math.abs(b.y + b.height - centerY)) {
						curDragging.y--;

						//gets the name of the subclass for curDragging so it can create a hover block of the same type 
						//important for file IO
						Class<?> type = curDragging.getClass();
						String className = type.getName();
						if(className.equals("Stone")){
							try {
								hover = new Stone(curDragging.x, b.y - curDragging.height, curDragging.width,
										curDragging.height, curDragging.img);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else if (className.equals("Ice")) {
							try {
								hover = new Ice(curDragging.x, b.y - curDragging.height, curDragging.width,
										curDragging.height, curDragging.img);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} 


					}

					else if (Math.abs(b.y + b.height - centerY) <= Math.abs(b.x - centerX)
							&& Math.abs(b.y + b.height - centerY) <= Math.abs(b.y - centerY)
							&& Math.abs(b.y + b.height - centerY) <= Math.abs(b.x - b.width - centerX)) {
						curDragging.y++;

						//gets the name of the subclass for curDragging so it can create a hover block of the same type 
						//important for file IO
						Class<?> type = curDragging.getClass();
						String className = type.getName();
						if(className.equals("Stone")){
							try {
								hover = new Stone(curDragging.x, b.y + b.height, curDragging.width, curDragging.height,
										curDragging.img);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else if (className.equals("Ice")) {
							try {
								hover = new Ice(curDragging.x, b.y + b.height, curDragging.width, curDragging.height,
										curDragging.img);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} 


					}
					if (hover != null && checkAllIntersection(hover)) {
						hover = null;
					}
					intersected = true;
				}
			}
			if (!intersected)
				hover = null;
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	public void drawSidebar(Graphics2D g) {
		g.fillRect(TAB_X - 5, 0, 5, GAME_HEIGHT);

		if (tabPressed.equals("blocks")) {
			g.setColor(Color.green);
			g.fillRect(TAB_X, 0, TAB_WIDTH + 20, TAB_HEIGHT);
			g.setColor(Color.black);
			g.setFont(rotatedFont);
			g.drawString("Blocks", GAME_WIDTH / 7 + 20, 10);

			tabPortal.draw(g);
			tabStone.draw(g);
			tabIce.draw(g);

		} else {
			g.setColor(Color.green);
			g.fillRect(TAB_X, 0, TAB_WIDTH, TAB_HEIGHT);
			g.setColor(Color.black);
			g.setFont(rotatedFont);
			g.drawString("Blocks", GAME_WIDTH / 7 + 10, 10);
		}

		if (tabPressed.equals("enemies")) {
			g.setColor(Color.orange);
			g.fillRect(TAB_X, TAB_HEIGHT, TAB_WIDTH + 20, TAB_HEIGHT + 20);
			g.setColor(Color.black);
			g.setFont(rotatedFont);
			g.drawString("Enemies", GAME_WIDTH / 7 + 20, 10 + TAB_HEIGHT);
		} else {
			g.setColor(Color.orange);
			g.fillRect(TAB_X, TAB_HEIGHT, TAB_WIDTH, TAB_HEIGHT + 20);
			g.setColor(Color.black);
			g.setFont(rotatedFont);
			g.drawString("Enemies", GAME_WIDTH / 7 + 10, 10 + TAB_HEIGHT);
		}

		if (tabPressed.equals("powerups")) {
			g.setColor(Color.CYAN);
			g.fillRect(TAB_X, 2 * TAB_HEIGHT + 20, TAB_WIDTH + 20, TAB_HEIGHT + 40);
			g.setColor(Color.black);
			g.setFont(rotatedFont);
			g.drawString("Powerups", GAME_WIDTH / 7 + 20, 2 * TAB_HEIGHT + 30);
		} else {
			g.setColor(Color.CYAN);
			g.fillRect(TAB_X, 2 * TAB_HEIGHT + 20, TAB_WIDTH, TAB_HEIGHT + 40);
			g.setColor(Color.black);
			g.setFont(rotatedFont);
			g.drawString("Powerups", GAME_WIDTH / 7 + 10, 2 * TAB_HEIGHT + 30);
		}

	}

	public boolean checkAllIntersection(Block block) {

		for (int i = 0; i < elements.size() - 1; i++) {
			Block b = elements.get(i);
			if (!block.equals(b) && block.intersects(b))
				return true;
		}
		return false;

	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

	public void addTitle (String title){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("names.txt", true));
			writer.write(title + ", ");
			writer.close();
		} catch (Exception e) {
			System.out.println("Problem adding name.");
		}
	}

	public static void replaceLine(String title, String save) {
		try {
			// input the (modified) file content to the StringBuffer "input"
			BufferedReader file = new BufferedReader(new FileReader("LevelSave.txt"));
			StringBuffer inputBuffer = new StringBuffer();
			String line;
			boolean containedTitle = false;

			while ((line = file.readLine()) != null) {

				if(line.startsWith(title)) {
					line = title + ": " + save; // replace the line here
					inputBuffer.append(line);
					inputBuffer.append('\n');
					containedTitle = true;
				} else {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}

			}

			if(!containedTitle) {
				line = title + ": " + save; // replace the line here
				inputBuffer.append(line);
				inputBuffer.append('\n');
			}

			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("LevelSave.txt");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}
					
	public void readFirstWords() {
		String filePath = "names.txt"; // Provide the path to your text file
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;

			while ((line = reader.readLine()) != null) {
				// Split the line into words using space as the delimiter
				String[] words = line.split(", ");
				for (String name: words) {
					names.add(name);
				}
			}
			System.out.println(names);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	

}