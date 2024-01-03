
/* GamePanel class acts as the main "game loop" - continuously runs the game and calls whatever needs to be called

Child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
	
	//imports for graphics
	Image image;
	public BufferedImage portalImage = ImageIO.read(new File("Images/Start_Portal.png"));
	public BufferedImage menuBackground = ImageIO.read(new File("Images/menu.png"));
	public BufferedImage openChestImage = ImageIO.read(new File("Images/OpenChest.png"));
	public BufferedImage closeedChestImage = ImageIO.read(new File("Images/ClosedChest.png"));
	public BufferedImage iceImage = ImageIO.read(new File("Images/Ice.png"));
	public BufferedImage ladderImage = ImageIO.read(new File("Images/Ladder.png"));
	public BufferedImage stoneImage = ImageIO.read(new File("Images/Stone.png"));
	public BufferedImage crackedStoneImage = ImageIO.read(new File("Images/CrackedStone.png"));
	
	public BufferedImage turretImage = ImageIO.read(new File("Images/turret.png"));
	
	
	Image afkAnimation = new ImageIcon("Images/KnightAfk.gif").getImage();
	Image runningAnimation = new ImageIcon("Images/KnightRunning.gif").getImage();
	Image goblinRunning = new ImageIcon("Images/GoblinRunning.gif").getImage();

	public BufferedImage playBackground = ImageIO.read(new File("Images/back.png"));


	public Player knight;

	//imports for text effects
	public int alpha = 0;

	//for states of the frame
	public boolean mainMenu = true;
	public boolean edit = false;
	public boolean levelSelect = false;
	public boolean alphaUp = true;
	public boolean sidebarPressed = false;
	public boolean fixed = false;
	public boolean play = false;

	public boolean running = false;
	public boolean jumping = false;
	public boolean falling = false;
	public boolean landing = false;


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
	public CrackedStone tabCrackedStone;
	public Goblin tabGoblin;
	public Turret tabTurret;

	ArrayList<Block> elements, blockSidebar, enemySidebar;
	Block hover = null;

	//for file IO
	public BufferedWriter writer;
	public boolean levelSaved = false;
	public String updatedSave = "";
	public ArrayList<String> names = new ArrayList<>(); // to prevent duplicate named levels

	public String newLevelTitle = ""; //if new level 
	public String prevSavedTitle = ""; //for files that exist and are revisited (level select -> play/edit button)

	public GamePanel(boolean levelSelect, boolean edit, boolean play, String levelName) throws IOException {
		if(levelSelect) {
			this.levelSelect = true; 
			mainMenu = false;
			this.edit = false;
			this.play = false;
		} else if(edit){
			this.levelSelect = false; 
			mainMenu = false;
			this.edit = true;
			this.play = false;
		} else if (play) {
			this.levelSelect = false; 
			mainMenu = false;
			this.edit = false;
			this.play = true;
		}

		//get the title of the save file
		prevSavedTitle = levelName; //only if the file already exists, otherwise this is ""

		//read the titles of each entry in LevelSave into arraylist names so no duplicate is made
		readFirstWords();


		this.setFocusable(true); // make everything in this class appear on the screen
		this.addKeyListener(this); // start listening for keyboard input
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		knight = new Player(200, 100, 47, 53);

		// code to rotate the text for the "block" description
		affineTransform.rotate(Math.toRadians(90), 0, 0);
		rotatedFont = font.deriveFont(affineTransform);
		

		elements = new ArrayList<Block>();
		blockSidebar = new ArrayList<Block>();
		enemySidebar = new ArrayList<Block>();

		readData(prevSavedTitle); //populates the elements arraylist with the blocks for the save level
		//if not applicable, prevSavedTitle = "";

		tabPortal = new Portal(TAB_X - 110, 20, Portal.width, Portal.height, portalImage);
		tabStone = new Stone(TAB_X - 110, 100, Stone.width, Stone.height, stoneImage);
		tabIce = new Ice(TAB_X - 110, 145, Ice.width, Ice.height, iceImage);
		tabLadder = new Ladder(TAB_X - 90, 190, Ladder.width, Ladder.height, ladderImage);
		tabCrackedStone = new CrackedStone(TAB_X - 110, 240, CrackedStone.width, CrackedStone.height, crackedStoneImage);
		
		tabGoblin = new Goblin(TAB_X - 110, 20, Goblin.width, Goblin.height, goblinRunning);
		tabTurret = new Turret(TAB_X - 110, 100, Turret.width, Turret.height, turretImage);
		
		blockSidebar.add(tabPortal);
		blockSidebar.add(tabStone);
		blockSidebar.add(tabIce);
		blockSidebar.add(tabLadder);
		blockSidebar.add(tabCrackedStone);
		
		enemySidebar.add(tabGoblin);
		enemySidebar.add(tabTurret);


		//total height for the scrollpane 
		//make the scrollpane height slightly bigger than the height of each button * the num of buttons which makes the levelSelect
		//frame scrollable and adjusts to however many buttons the app needs 
		//names.size() refers to the number of names of levels (how many levels AKA buttons since each level requires space for it's button label)
		totalHeight = names.size() * numButtons;
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
			//indicator pos allows user to use up and down keys to position their choice in the menu
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

			if (curSelected != null) {
				g.setColor(Color.yellow);
				if(curSelected.width < 0) {
					g.fillRect(curSelected.x + curSelected.width - 3, curSelected.y-3, -curSelected.width + 3, 3);
					g.fillRect(curSelected.x + curSelected.width - 3, curSelected.y-3, 3, curSelected.height + 3);
					g.fillRect(curSelected.x, curSelected.y, 3, curSelected.height + 3);
					g.fillRect(curSelected.x + curSelected.width, curSelected.y + curSelected.height, -curSelected.width + 3, 3);
				}
				else {
					g.fillRect(curSelected.x-3, curSelected.y-3, curSelected.width + 3, 3);
					g.fillRect(curSelected.x-3, curSelected.y-3, 3, curSelected.height + 3);
					g.fillRect(curSelected.x + curSelected.width, curSelected.y, 3, curSelected.height + 3);
					g.fillRect(curSelected.x, curSelected.y + curSelected.height, curSelected.width + 3, 3);
				}
				
			}

			// code for drawing the knight animation

			knight.draw(g);

		} else if (levelSelect) {
			// to be filled (draw the image background?)
		}

		if (play) {

			
		}
		
	}

	

	// call the move methods in other classes to update positions
	// this method is constantly called from run(). By doing this, movements appear
	// fluid and natural. If we take this out the movements appear sluggish and
	// laggy
	public void move() {
		knight.move();
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

	public void keyPressed(KeyEvent e) {


		if (mainMenu) {
	            if (e.getKeyCode() == 10 && indicatorPos == 320){
	                levelSelect = false;
	                edit = true;
	                mainMenu = false;
	            }
	            else if (e.getKeyCode() == 10 && indicatorPos == 250) {
	                //enter the levelSelect menu


	                mainMenu = false;
	                edit = false;
	                levelSelect = true;
	                //create a new gameframe in the levelSelect menu
	                try {
	                    new GameFrame(true, false, false, "");
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
	                    new GameFrame(false, false, false, "");
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }

	            }

		} else if (edit) {
			knight.keyPressed(e);
			if(e.getKeyCode() == 70 && curSelected != null) {
				elements.remove(curSelected);
				try {
					elements.add(hFlip(curSelected));
				} catch (IOException e1) {}
				curSelected = elements.get(elements.size()-1);
				curDragging = curSelected;
			}
			else if(e.getKeyCode() == 8) {
				if(curSelected != null) {
					elements.remove(curSelected);
					curSelected = null;
				}

				//check if file is saved OR saved and played
			} else if(e.getKeyCode() == KeyEvent.VK_1) {
				//save the file
				updatedSave = "";
				if (!levelSaved && prevSavedTitle.isEmpty()) { //if user is created a fresh new level
					// Create a JTextField for user input
					JTextField textField = new JTextField();

					// Show an input dialog with the text field
					int option = JOptionPane.showOptionDialog(this, textField,
							"Name your level!", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, null, null);

					// Check if the user clicked OK
					if (option == JOptionPane.OK_OPTION) {
						// Get the entered name from the text field
						newLevelTitle = textField.getText().stripTrailing(); // get the info from text box without whitespace
						if(!names.contains(newLevelTitle)) {
							for(Block b: elements) {
								updatedSave += b.toString() + ": ";
							}
							replaceLine(newLevelTitle, updatedSave);

							addTitle(newLevelTitle); //add the title

							// Display a message indicating that the level has been saved
							JOptionPane.showMessageDialog(this, "Level saved!", "Save Confirmation", JOptionPane.INFORMATION_MESSAGE);
							levelSaved = true;
						} else {
							// Display a message indicating that the level has not been saved
							JOptionPane.showMessageDialog(this, "Title already in use. Please try again.", "Invalid Save", JOptionPane.INFORMATION_MESSAGE);
						}
					} else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
						JOptionPane.showMessageDialog(this, "Save cancelled.", "Invalid Save", JOptionPane.INFORMATION_MESSAGE);
					}
				} else if (levelSaved && prevSavedTitle.isEmpty()) {
					//if previously saved, then update the entry
					for(Block b: elements) {
						updatedSave += b.toString() + ": ";
					}
					levelSaved = true;
					System.out.println(elements + "       " + updatedSave);
					replaceLine(newLevelTitle, updatedSave); //replace line with the entered title --> THIS CASAE AND ABOVE CASE ONLY OCCUR IF THE USER DIRECTLY CREATES THEIR NEW DUNGEON
					
					//display saved
					JOptionPane.showMessageDialog(this, "Level saved!", "Save Confirmation", JOptionPane.INFORMATION_MESSAGE);
				} else if (!prevSavedTitle.isEmpty() && !levelSaved) {
					for(Block b: elements) {
						updatedSave += b.toString() + ": ";
					}
					replaceLine(prevSavedTitle, updatedSave); //replace line with the given title
					
					//display saved
					JOptionPane.showMessageDialog(this, "Level saved!", "Save Confirmation", JOptionPane.INFORMATION_MESSAGE);
					

				}
				//FINISH THIS 
			} else if (e.getKeyCode() == KeyEvent.VK_2) {
				//enter play mode;
				edit = false;
				play = true;
			} 
						
		}

	}

	public void keyReleased(KeyEvent e) {
		knight.keyReleased(e);
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
				if (mousePressedBlock(b,e)) {
					b.mousePressed(e);
					curDragging = b;
					curSelected = b;
					sidebarPressed = false;
					chosen = true;
				}
			}

			if (!chosen)
				curSelected = null;

			// checks if the block pressed is from the sidebar
			if(tabPressed.equals("blocks")) {
				for (Block b : blockSidebar) {
					if (mousePressedBlock(b,e)) {
						b.mousePressed(e);
						curDragging = b;
						sidebarPressed = true;
						chosen = true;
					}
				}
			}
			else if(tabPressed.equals("enemies")) {
				for (Block b : enemySidebar) {
					if (mousePressedBlock(b,e)) {
						b.mousePressed(e);
						curDragging = b;
						sidebarPressed = true;
						chosen = true;
					}
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
				if (curDragging.x <= TAB_X) {//remove it if it's still on the sidebar and not dragged into the sandbox

					elements.remove(curDragging);
					curSelected = null;
				}  
				 else if (hover != null) { //if there IS a block being hovered over b the curDragging block

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
							curSelected = null;
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
					curSelected = null;
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
				
				if(tabPressed.equals("blocks")){
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
				
					else if (curDragging.equals(tabLadder)) {
						try {
							elements.add(new Ladder(TAB_X - 80, 190, Ladder.width, Ladder.height, ladderImage));
						} catch (IOException IOE) {
						}
						curDragging = elements.get(elements.size() - 1);
					}
					
					else if(curDragging.equals(tabCrackedStone)) {
						try {
							elements.add(new CrackedStone(TAB_X - 110, 230, CrackedStone.width, CrackedStone.height, crackedStoneImage));
						} catch (IOException IOE) {
						}
						curDragging = elements.get(elements.size() - 1);
					}
				}
				else if(tabPressed.equals("enemies")) {
					if (curDragging.equals(tabGoblin)) {
						try {
							elements.add(new Goblin(TAB_X - 110, 20, Goblin.width, Goblin.height, goblinRunning));
						} catch (IOException IOE) {
						}
						curDragging = elements.get(elements.size() - 1);
					}
					if (curDragging.equals(tabTurret)) {
						try {
							elements.add(new Turret(TAB_X - 110, 100, Turret.width, Turret.height, turretImage));
						} catch (IOException IOE) {
						}
						curDragging = elements.get(elements.size() - 1);
					}
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
						
						try {
							hover = decipherBlock(curDragging, b.x - curDragging.width, curDragging.y, curDragging.width, curDragging.height);
						} catch (IOException e1) {}
					}
					else if (Math.abs(b.x + b.width - centerX) <= Math.abs(b.x - centerX)
							&& Math.abs(b.x + b.width - centerX) <= Math.abs(b.y - centerY)
							&& Math.abs(b.x + b.width - centerX) <= Math.abs(b.y + b.height - centerY)) {

						try {
							hover = decipherBlock(curDragging,b.x + b.width, curDragging.y, curDragging.width, curDragging.height);
						} catch (IOException e1) {}

						curDragging.x++;

					}

					else if (Math.abs(b.y - centerY) <= Math.abs(b.x + b.width - centerX)
							&& Math.abs(b.y - centerY) <= Math.abs(b.x - centerX)
							&& Math.abs(b.y - centerY) <= Math.abs(b.y + b.height - centerY)) {

						try {
							hover = decipherBlock(curDragging,curDragging.x, b.y - curDragging.height, curDragging.width,
											curDragging.height);
						} catch (IOException e1) {}

						curDragging.y--;

					}

					else if (Math.abs(b.y + b.height - centerY) <= Math.abs(b.x - centerX)
							&& Math.abs(b.y + b.height - centerY) <= Math.abs(b.y - centerY)
							&& Math.abs(b.y + b.height - centerY) <= Math.abs(b.x - b.width - centerX)) {

						try {
							hover = decipherBlock(curDragging,curDragging.x, b.y + b.height, curDragging.width, curDragging.height);
						} catch (IOException e1) {}


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
			tabLadder.draw(g);
			tabCrackedStone.draw(g);
			
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
			
			tabGoblin.draw(g);
			tabTurret.draw(g);
			
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
			if (!block.equals(b) && block.intersects(b) && !b.equals(curDragging))
				return true;
		}
		return false;

	}


	//resizes an image to preferred width and height
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

	//add title if user creates a new level to the names.txt file to prevent duplicate titles.
	public void addTitle (String title){
		//adds a title to the names.txt file for easy checking if there are any duplicate names (bad)
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("names.txt", true));
			writer.write(title + ", ");
			writer.close();
		} catch (Exception e) {
			System.out.println("Problem adding name.");
		}
	}

	//rewrites the LevelSave.txt file into a temp input stream and then overwrites the previous save file to 
	//imitate the effect of "overwritting" a save level. (this is the only viable way to overwrite in java file io)
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
					
	//reads the first words (AKA titles) for each level into the names arraylist so no duplicate titles are made (our unique ID sys for levels relies on unique names)
	//also required for num of buttons and height of scrollpane
	public void readFirstWords() {
		//read titles to check for no duplicates

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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	//reads the data for a specific level and adds all blocks of that level into the elements arraylist 
	//so the level can be loaded and played/edited
	public void readData(String title) {
		//if the title is not found or there is no title

		if(title.equals("")) {
			return;
		} else {
			String filePath = "LevelSave.txt"; // Provide the path to your text file
			try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
				String line;

				while ((line = reader.readLine()) != null) {
					// Split the line into words using space as the delimiter
					if(line.startsWith(title)){
						String[] words = line.split(": ");
						for (int i = 1; i < words.length; i++) {
							//i = 1 skip over the title of the thing ASSUMES THAT the user doesn't enter : in the title itself
							String[] inputs = words[i].split(" "); //splits based on space
							if(inputs[0].equals("Ice")) {
								elements.add(new Ice(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), Ice.width, Ice.height, iceImage));
							} else if (inputs[0].equals("Stone")) {
								elements.add(new Stone(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), Stone.width, Stone.height, stoneImage));
							} else if (inputs[0].equals("Portal")) {
								elements.add(new Portal(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), Portal.width, Portal.height, portalImage));
							}
							else if (inputs[0].equals("Ladder")) {
								elements.add(new Ladder(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), Ladder.width, Ladder.height, ladderImage));
							}
							else if (inputs[0].equals("CrackedStone")) {
								elements.add(new CrackedStone(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), CrackedStone.width, CrackedStone.height, crackedStoneImage));
							}
							else if (inputs[0].equals("Goblin")) {
								elements.add(new Goblin(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), Goblin.width, Goblin.height, goblinRunning));
							}
							else if(inputs[0].equals("Turret")) {
								elements.add(new Turret(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), Turret.width, Turret.height, turretImage));
							}
						}
						return;
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}



	}

	public Block decipherBlock(Block b, int x, int y, int width, int height) throws IOException {
		Class<?> type = b.getClass();
		String className = type.getName();
		if(className.equals("Stone")){
				return new Stone(x, y, width, height,b.img);
		} else if (className.equals("Ice")) {
			return new Ice(x, y, width, height,b.img);
		} 
		else if(className.equals("Portal")){
			return new Portal(x, y, width, height,b.img);
		}
		else if(className.equals("Ladder")){
			return new Ladder(x, y, width, height,b.img);
		}
		else if(className.equals("CrackedStone")){
			return new CrackedStone(x, y, width, height,b.img);
		}
		else if(className.equals("Goblin")){
			return new Goblin(x, y, width, height,b.img);
		}
		else if(className.equals("Turret")) {
			return new Turret(x, y, width, height,b.img);
		}
		return b;
	}
	
	public Block hFlip(Block b) throws IOException {
		return decipherBlock(b ,b.x + b.width,b.y, -b.width, b.height);
	}
	
	public boolean mousePressedBlock(Block b, MouseEvent e) {
		if(b.width < 0) {
			return b.x + b.width <= e.getX() && b.x >= e.getX() && b.y <= e.getY() && b.y + b.height >= e.getY();
		}
		else {
			return b.x <= e.getX() && b.x + b.width >= e.getX() && b.y <= e.getY() && b.y + b.height >= e.getY();
		}
	}
	
}