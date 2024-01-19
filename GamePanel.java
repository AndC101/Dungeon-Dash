
/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * GamePanel class is the main class to run the game
 */

 import java.awt.*;
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
	 public static final int FLOOR = GAME_HEIGHT - 25;
 
	 public Thread gameThread;
	 public Graphics graphics;
 
	 // font variables
	 AffineTransform affineTransform = new AffineTransform();
	 Font font = new Font(null, Font.PLAIN, 25);
	 Font rotatedFont;
 
	 // imports for graphics
	 Image image;
	 public BufferedImage portalImage = ImageIO.read(new File("Images/Start_Portal.png"));
	 public BufferedImage menuBackground = ImageIO.read(new File("Images/menu.png"));
	 public BufferedImage openChestImage = ImageIO.read(new File("Images/OpenChest.png"));
	 public BufferedImage closedChestImage = ImageIO.read(new File("Images/ClosedChest.png"));
	 public BufferedImage iceImage = ImageIO.read(new File("Images/Ice.png"));
	 public BufferedImage ladderImage = ImageIO.read(new File("Images/Ladder.png"));
	 public BufferedImage stoneImage = ImageIO.read(new File("Images/Stone.png"));
	 public BufferedImage crackedStoneImage = ImageIO.read(new File("Images/CrackedStone.png"));
	 public BufferedImage turretRight = ImageIO.read(new File("Images/turret.png"));
	 public BufferedImage turretLeft = ImageIO.read(new File("Images/turLeft.png"));
	 public BufferedImage oneUpImage = ImageIO.read(new File("Images/oneUp.png"));
	 public BufferedImage speedBoostImage = ImageIO.read(new File("Images/SpeedBoost.png"));
	 
	 public BufferedImage lifeImage = ImageIO.read(new File("Images/heart.png"));
 
	 // imports for sprites
	 Image afkAnimation = new ImageIcon("Images/KnightAfk.gif").getImage();
	 Image runningAnimation = new ImageIcon("Images/KnightRunning.gif").getImage();
	 Image goblinRunLeft = new ImageIcon("Images/GoblinRunLeft.gif").getImage();
	 Image goblinRunRight = new ImageIcon("Images/GoblinRunRight.gif").getImage();
 
	 public BufferedImage playBackground = ImageIO.read(new File("Images/back.png")); // background

	 // this is the player
	 public Player knight;
 
	 // imports for text effects
	 public int alpha = 0;
 
	 // for states of the frame
	 public static boolean mainMenu = true;
	 public static boolean edit = false;
	 public boolean levelSelect = false;
	 public boolean alphaUp = true;
	 public boolean sidebarPressed = false;
	 public boolean instructions = false;
	 public boolean fixed = false;
	 public static boolean play = false;
	 public static boolean spawn = true;
	 public boolean powerUpUp = true;
	 public boolean checkVertical = false;
	 public boolean win = false;
	 public boolean gameEnd = false;
	 public boolean running = true;
 
	 public int indicatorPos = 250; // > symbol for menu
 
	 public String tabPressed = "blocks";
 
	 // declare level widget variables
	 public int totalHeight;
	 public int numButtons = 0; // to be changed once file IO works
	 public int leftBorder = 1000000000, rightBorder = -1;
	 public static int shift = 0;
	 public int spawnX = 0;
	 public static int adjust = 0;
	 public int powerUpBob = 0;
	 public int buffer = 5;
	 public int gameEndAlpha = 0;
	 public int instructionsPage = 1;
	 public long startTime;
	 public long endTime = -1;
	 public int numPowers = 0;
 
	 public Block curDragging, curSelected;
 
	 // sidebar objects
	 public Portal tabPortal;
	 public Stone tabStone;
	 public Ice tabIce;
	 public Ladder tabLadder;
	 public CrackedStone tabCrackedStone;
	 public Chest tabChest;
	 public Goblin tabGoblin;
	 public Turret tabTurret;
	 public Turret tabTurretLeft;
 
	 public OneUp tabOneUp;
	 public SpeedBoost tabSpeedBoost;
 
	 public Portal spawnPortal;
	 public Chest endChest;
 
	 // all elements are in the elements ArrayList
	 // the sidebar ArrayLists contain the different sidebar objects
	 public ArrayList<Block> elements;
	 public ArrayList<Block> blockSidebar, enemySidebar, powerUpSidebar, remove;
	 Block hover = null;
 
	 public static HashSet<String> walkThrough;
 
	 // for file IO
	 public BufferedWriter writer;
	 public boolean levelSaved = false;
	 public String updatedSave = "";
	 public ArrayList<String> names = new ArrayList<>(); // to prevent duplicate named levels
 
	 public String newLevelTitle = ""; // if new level
	 public String prevSavedTitle = ""; // for files that exist and are revisited (level select -> play/edit button)
 
	 // creates the background image
	 public Background back = new Background(0, 0, playBackground);

 
	 // turret orientation testing
	 public boolean turLeft = false;
	 public boolean turRight = true;
	 public int turFlipNum = 0;
	 public int goblinFlipNum = 0;
 
	 // goblin hover
	 public boolean onTop = false;
 
	 public GamePanel(boolean levelSelect, boolean edit, boolean play, String levelName) throws IOException {
		 // initializes the variables handling the different screens
		 if (levelSelect) {
			 this.levelSelect = true;
			 mainMenu = false;
			 this.edit = false;
			 this.play = false;
		 } else if (edit) {
			 this.levelSelect = false;
			 mainMenu = false;
			 this.edit = true;
			 this.play = false;
		 } else if (play) {
			 spawn = true;
			 this.levelSelect = false;
			 mainMenu = false;
			 this.edit = false;
			 this.play = true;
		 } else {
			 mainMenu = true;
		 }
 
		 // initializes the Player
		 knight = new Player(this, 0, 0, 35, 45);
 
		 // get the title of the save file
		 prevSavedTitle = levelName; // only if the file already exists, otherwise this is ""
 
		 // read the titles of each entry in LevelSave into arraylist names so no
		 // duplicate is made
		 readFirstWords();
 
		 // make everything in this class appear on the screen
		 this.setFocusable(true);
 
		 // start listening for keyboard and mouse input
		 this.addKeyListener(this);
		 this.addMouseListener(this);
		 this.addMouseMotionListener(this);
 
		 // code to rotate the text for the "block" description
		 affineTransform.rotate(Math.toRadians(90), 0, 0);
		 rotatedFont = font.deriveFont(affineTransform);
 
		 // initializes the ArrayLists
		 elements = new ArrayList<Block>();
		 blockSidebar = new ArrayList<Block>();
		 enemySidebar = new ArrayList<Block>();
		 powerUpSidebar = new ArrayList<Block>();
		 remove = new ArrayList<Block>();
 
		 walkThrough = new HashSet<String>();
 
		 readData(prevSavedTitle); // populates the elements ArrayList with the blocks for the save level
		 // if not applicable, prevSavedTitle = "";
 
		 // creates the objects for the sidebar objects
		 tabPortal = new Portal(TAB_X - 110, 20, Portal.width, Portal.height, portalImage);
		 tabStone = new Stone(TAB_X - 110, 100, Stone.width, Stone.height, stoneImage);
		 tabIce = new Ice(TAB_X - 110, 145, Ice.width, Ice.height, iceImage);
		 tabLadder = new Ladder(TAB_X - 90, 190, Ladder.width, Ladder.height, ladderImage);
		 tabCrackedStone = new CrackedStone(TAB_X - 110, 240, CrackedStone.width, CrackedStone.height,
				 crackedStoneImage);
		 tabChest = new Chest(TAB_X - 110, 330, Chest.width, Chest.height, closedChestImage);
 
		 // enemies
		 tabGoblin = new Goblin(this, TAB_X - 110, 20, Goblin.width, Goblin.height, goblinRunLeft, goblinRunRight, false);
		 tabTurret = new Turret(this, TAB_X - 110, 100, Turret.width, Turret.height, turretRight, turLeft, turRight, false);
		 tabTurretLeft = new Turret(this, TAB_X - 110, 170, Turret.width, Turret.height, turretLeft, true, false, false);
 
		 // powerups
		 tabOneUp = new OneUp(TAB_X - 110, 20, OneUp.width, OneUp.height, oneUpImage);
		 tabSpeedBoost = new SpeedBoost(TAB_X - 110, 100, SpeedBoost.width, SpeedBoost.height, speedBoostImage);
 
		 // add the sidebar objects to the sidebar ArrayLists
		 blockSidebar.add(tabPortal);
		 blockSidebar.add(tabStone);
		 blockSidebar.add(tabIce);
		 blockSidebar.add(tabLadder);
		 blockSidebar.add(tabCrackedStone);
		 blockSidebar.add(tabChest);
 
		 enemySidebar.add(tabGoblin);
		 enemySidebar.add(tabTurret);
		 enemySidebar.add(tabTurretLeft);
 
		 powerUpSidebar.add(tabOneUp);
		 powerUpSidebar.add(tabSpeedBoost);
 
		 walkThrough.add("Portal");
		 walkThrough.add("Ladder");
		 walkThrough.add("Chest");
		 walkThrough.add("Goblin");
		 walkThrough.add("OneUp");
		 walkThrough.add("SpeedBoost");
 
		 // total height for the scrollpane
		 // make the scrollpane height slightly bigger than the height of each button *
		 // the num of buttons which makes the levelSelect
		 // frame scrollable and adjusts to however many buttons the app needs
		 // names.size() refers to the number of names of levels (how many levels AKA
		 // buttons since each level requires space for it's button label)
		 totalHeight = names.size() * numButtons;
		 this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
 
		 // allows this class to run at the same time as others
		 gameThread = new Thread(this);
		 gameThread.start();
	 }
 
	 // overriding the paint method
	 public void paint(Graphics g) {
		 // double buffering
		 image = createImage(GAME_WIDTH, GAME_HEIGHT); // draw off screen
		 graphics = image.getGraphics();
		 try {
			 draw((Graphics2D) graphics);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }// update the positions of everything on the screen
		 g.drawImage(image, 0, 0, this); // move the image on the screen
 
	 }
 
	 // draws everything on the screen
	 // call the draw methods in each class to update positions as things move
	 public void draw(Graphics2D g) throws IOException {
		 // checks which screen should be displayed
		 
		 if(instructions) {
			 g.setColor(Color.black);
			 g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
			 
			 
			 
			 if(instructionsPage == 1) {
				 g.setColor(Color.white);
				 g.setFont(new Font("Impact", Font.PLAIN, 40));
				 g.drawString("How to Play?", 350, 50);
				 g.setFont(new Font("Impact", Font.PLAIN, 20));
				 g.drawString("The goal of the game is to reach the treasure chest from the portal.", 20, 250);
				 g.drawImage(afkAnimation, 310, 165, null);
				 (new Chest(350,180,Chest.width,Chest.height,openChestImage)).draw(g);
				 (new Portal(100,145,Portal.width,Portal.height,portalImage)).draw(g);
				 g.drawImage(afkAnimation, 110, 165, null);
				 g.drawLine(170, 185, 280, 185);
				 g.drawLine(240, 165, 280, 185);
				 g.drawLine(240, 205, 280, 185);
				 
				 g.drawString("Do this while avoiding enemies", 100, 350);
				 g.drawImage(goblinRunRight, 500, 325, null);
				 (new Turret(this,600,320,Turret.width,Turret.height, turretLeft, true, false, true)).draw(g);
				 
				 (new OneUp(100,400,OneUp.width,OneUp.height,oneUpImage)).draw(g);
				 (new SpeedBoost(200,410,SpeedBoost.width,SpeedBoost.height,speedBoostImage)).draw(g);
				 g.drawString("and using powerups.", 400, 450);
				 
			 }
			 else if(instructionsPage == 2) {
				 g.setColor(Color.white);
				 g.setFont(new Font("Impact", Font.PLAIN, 40));
				 g.drawString("BLOCKS", 375, 50);
				 
				 g.setFont(new Font("Impact", Font.PLAIN, 20));
				 
				 g.drawString("Stone acts as a normal block.", 75, 175);
				 (new Stone(150,100,Stone.width,Stone.height,stoneImage)).draw(g);
				 
				 g.drawString("The player will move faster and slide on ice.", 25, 325);
				 (new Ice(150,250,Ice.width,Ice.height,iceImage)).draw(g);
				 
				 g.drawString("Players are able to climb up ladders.", 500, 175);
				 (new Ladder(640,100,Ladder.width,Ladder.height,ladderImage)).draw(g);
				 
				 g.drawString("Cracked stone dissapears after the player steps on it.", 430, 325);
				 (new CrackedStone(615,250,CrackedStone.width,CrackedStone.height,crackedStoneImage)).draw(g);
				 
				 g.drawString("The portal is the spawn point.", 60, 475);
				 (new Portal(150,375,Portal.width,Portal.height,portalImage)).draw(g);
				 
				 g.drawString("The chest is the win condition.", 510, 475);
				 (new Chest(600,400,Chest.width,Chest.height,closedChestImage)).draw(g);
			 }
			 else if(instructionsPage == 3) {
				 g.setColor(Color.white);
				 g.setFont(new Font("Impact", Font.PLAIN, 40));
				 g.drawString("ENEMIES & POWERUPS", 275, 50);
				 
				 g.setFont(new Font("Impact", Font.PLAIN, 20));
				 g.drawString("The goblin walks around on the floor it is on.", 25, 200);
				 g.drawImage(goblinRunRight, 160, 125, null);
				 
				 g.drawString("The turret shoots fireballs in a straight line.", 475, 200);
				 (new Turret(this, 625,110,Turret.width,Turret.height, turretLeft, true, false, true)).draw(g);
				 
				 (new SpeedBoost(175,275,SpeedBoost.width,SpeedBoost.height,speedBoostImage)).draw(g);
				 g.drawString("Speed up increases the movement speed of the player.", 10, 350);
		 
				 
				 g.drawString("Extra life awards the player with an extra life.", 500,  350);
				 
				 
			 }
			 
			 g.setFont(new Font("Impact", Font.PLAIN, 20));
			 g.drawString("USE ARROW KEYS TO MOVE THROUGH PAGES", 275, 525);
			 
		 }
		 else if (mainMenu) {

			g.setColor(Color.white);
			 g.drawImage(menuBackground, 0, 0, this);
			 // display text for the title 
 
			 g.setFont(new Font("Impact", Font.PLAIN, FONT_SIZE));

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
			 // indicator pos allows user to use up and down keys to position their choice in
			 // the menu
			 g.drawString("> ", 300, indicatorPos);
			 g.drawString("Level selection", 325, 250);
			 g.drawString("Create new dungeon", 325, 320);
			 g.drawString("How to Play?", 325, 400);
 
		 } else if (edit) {
 
			 // draw the background
			 back.draw(g);
 
			 // draw strings for user instruction for save and play
			 g.setColor(Color.white);
			 g.drawString("Enter \"1\" to SAVE level.", 200, 10);
			 g.drawString("Enter \"2\" to PLAY level.", 200, 25);
 
			 g.setColor(Color.black);
 
			 // draws all elements on the screen
			 for (Block b : elements) {
				 b.draw(g);
			 }
			 // draws the sidebar
			 drawSidebar(g);
 
			 // check if its being hovered and makes it like transparent
			 if (hover != null) {
				 Graphics2D g2d = (Graphics2D) g;
				 AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
				 g2d.setComposite(ac);
				 hover.draw(g2d);
			 }
			 // draws the outline of the the selected object
			 if (curSelected != null) {
				 g.setColor(Color.yellow);
				 // checks if the image is reflected
				 if (curSelected.width < 0) {
					 g.fillRect(curSelected.x + curSelected.width - 3, curSelected.y - 3, -curSelected.width + 3, 3);
					 g.fillRect(curSelected.x + curSelected.width - 3, curSelected.y - 3, 3, curSelected.height + 3);
					 g.fillRect(curSelected.x, curSelected.y, 3, curSelected.height + 3);
					 g.fillRect(curSelected.x + curSelected.width, curSelected.y + curSelected.height,
							 -curSelected.width + 3, 3);
				 } else {
					 g.fillRect(curSelected.x - 3, curSelected.y - 3, curSelected.width + 3, 3);
					 g.fillRect(curSelected.x - 3, curSelected.y - 3, 3, curSelected.height + 3);
					 g.fillRect(curSelected.x + curSelected.width, curSelected.y, 3, curSelected.height + 3);
					 g.fillRect(curSelected.x, curSelected.y + curSelected.height, curSelected.width + 3, 3);
				 }
 
			 }
 
		 } else if (levelSelect) {
			// backScroll.draw(g);
			 // to be filled (draw the image background?)
		 }
 
		 else if (play) {
			 // draws the background
			 back.draw(g);
			 
			 g.drawImage(lifeImage, 750, 30, 40, 40, null);
			 g.setFont(new Font("Impact", Font.PLAIN, 30));
			 g.setColor(Color.white);
			 g.drawString("X" + "   " + knight.lives, 800, 60);
 
			 // loops through all elements
 
			 if (spawn) { // spawns the knight in front of the portal
				 startTime = System.currentTimeMillis();
 
				 // finds the spawn portal and forces it to the center
				 for (Block b : elements) {
					 if (b.img != null && b.img.equals(portalImage)) {
						 spawnPortal = (Portal) b;
						 shift = spawnPortal.x - GAME_WIDTH / 2 + spawnPortal.width / 2;
					 }
					 if (b.img != null && b.img.equals(closedChestImage)) {
						 endChest = (Chest) b;
					 }
					 if(getClass(b).equals("SpeedBoost") || getClass(b).equals("OneUp")) {
						 numPowers++;
					 }
 
				 }
				 // moves all blocks to adjust for the shift to the portal and finds the left and
				 // rightmost block coordinates
				 for (Block b : elements) {
					 b.x -= shift;
					 leftBorder = Math.min(leftBorder, b.x);
					 rightBorder = Math.max(rightBorder, b.x + b.width);
				 }
 
				 // checks if the blocks are within 1 screen
				 if (rightBorder - leftBorder <= GAME_WIDTH) {
					 Player.oneScreen = true;
				 }
 
				 // saves the original position of the portal
				 spawnX = spawnPortal.x;
				 // moves the knight to the portal
 
				 knight.x = spawnPortal.x + Portal.width / 2 - knight.width / 2;
				 knight.y = spawnPortal.y + (Portal.height - knight.height);
 
			 }
 
			 // checks which half of the level the knight is on
			 if (Math.abs(leftBorder + spawnPortal.x - spawnX) <= (rightBorder + spawnPortal.x - spawnX - GAME_WIDTH)) {
				 knight.left = true;
			 } else {
				 knight.left = false;
			 }
 
			 // checks if a border is reached
			 if (leftBorder + spawnPortal.x - spawnX >= 0 || rightBorder + spawnPortal.x - spawnX <= GAME_WIDTH) {
				 // checks which border is reached
				 if (leftBorder + spawnPortal.x - spawnX >= 0) {
					 // calculates how much is needed to move so that the block appears right on the
					 // edge
					 adjust = leftBorder + spawnPortal.x - spawnX;
				 }
				 // same as above
				 else if (rightBorder + spawnPortal.x - spawnX <= GAME_WIDTH) {
					 adjust = (rightBorder + spawnPortal.x - spawnX) - GAME_WIDTH;
 
				 }
 
				 // moves all blocks accordingly and the knight if it was just spawned
				 for (Block b : elements) {
					 b.x -= adjust;
					 if (b instanceof Goblin) {
						 ((Goblin) b).xBorder -= adjust;
					 }
				 }
				 if (spawn) {
					 knight.x -= adjust;
				 }
 
				 // since a border is reached, the knight no longer needs to be centered
				 Player.isCentered = false;
			 }
 		 
			 for (Block b : elements) {
				 // checks if it is a powerup and if so make it bob up and down
				 if (getClass(b).equals("SpeedBoost") || getClass(b).equals("OneUp")) {
					 if (powerUpUp) {
						 powerUpBob++;
						 b.y++;
					 } else {
						 powerUpBob--;
						 b.y--;
					 }
 
					 if (powerUpBob >= 20 * numPowers)
						 powerUpUp = false;
					 if (powerUpBob <= -20 * numPowers)
						 powerUpUp = true;
 
				 }
				 // draws all the Blocks
				 b.draw(g);
			 }
			 
			 // draws the knight
			 knight.draw(g);
 
			 if (spawn)
				 spawn = false;
 
			 if (gameEnd)
				 gameEnd(g);
			 
			 // checks if the knight has touched the chest, trigger game end
			 else if (endChest != null && knight.intersects(endChest)) {
				 if (endTime == -1) {
					 endTime = System.currentTimeMillis();
				 }
				 win = true;
				 gameEnd(g);
			 }
			 else {
				 for (Block b : elements) {
					 if (getClass(b).equals("Goblin") || getClass(b).equals("Turret")) {
						 if (getClass(b).equals("Goblin")) {
							 if (knight.intersects(b)  && knight.invincibleStart == -1) {
								knight.lives--;
								 if(knight.lives == 0) {
								
									 win = false;
									 gameEnd = true;
								 }
								 knight.invincibleStart = System.currentTimeMillis();
							 }
						 } else if (getClass(b).equals("Turret")) {
 
							 if (((((Turret) b).ball != null && knight.intersects(((Turret) b).ball))
									 || (((Turret) b).ballTwo != null && knight.intersects(((Turret) b).ballTwo))) && knight.invincibleStart == -1) {
								 knight.lives--;
								 if(knight.lives == 0) {
									 win = false;
									 gameEnd = true;
								 }
								 knight.invincibleStart = System.currentTimeMillis();
							 }
						 }
					 }
				 }
			 }
 
			 
			 
			 if(knight.invincibleStart != -1 && System.currentTimeMillis() - knight.invincibleStart >= knight.iFrames) knight.invincibleStart = -1;
			 
 
		 }
 
	 }
 
	 // calls the move method of all other methods
	 public void move() {
		 knight.move();
		 for (Block b : elements) {
			 b.move();
		 }
		 back.move();
	 }
 
	 // handles all collision detection and responds accordingly
	 public void checkCollision() {
 
		 if (edit) {
			 // doesn't allow blocks to be dragged off the screen
			 if (curSelected != null) {
 
				 if (curSelected.x <= 0)
					 curSelected.x = 0;
				 if (curSelected.x + curSelected.width >= GAME_WIDTH)
					 curSelected.x = GAME_WIDTH - curSelected.width;
				 if (curSelected.y <= 0)
					 curSelected.y = 0;
				 if (curSelected.y + curSelected.height >= FLOOR)
					 curSelected.y = FLOOR - curSelected.height;
			 }
 
		 } else if (play && !spawn) {
 
			 // doesn't allow the player to walk off the screen
			 if (knight.x <= 0) {
				 knight.x = 0;
				 Player.setXDirection(0);
			 }
			 if (knight.x + knight.width >= GAME_WIDTH) {
				 knight.x = GAME_WIDTH - knight.width;
				 Player.setXDirection(0);
			 }
 
			 // checks collisions between the player and the blocks
			 for (Block b : elements) {
				 checkVertical = false;
 
				 // skips it if the block doesn't need collision
				 if (walkThrough.contains(getClass(b)))
					 continue;
 
				 // TOP COLLISION
				 if (((knight.x > b.x && knight.x < b.x + b.width)
						 || (knight.x + knight.width > b.x && knight.x + knight.width < b.x + b.width))
						 && knight.y + knight.height > b.y
						 && knight.y + knight.height < (double) (b.y + (double) (b.height) * 0.20)) {
					 // stops all vertical movement
					 knight.y = b.y - knight.height - 1;
					 knight.isJumping = false;
					 knight.falling = false;
					 knight.yVelocity = 0;
					 checkVertical = true;
				 }
 
				 // BOTTOM COLLISION
				 if (((knight.x > b.x && knight.x < b.x + b.width)
						 || (knight.x + knight.width > b.x && knight.x + knight.width < b.x + b.width))
						 && knight.y + knight.height > b.y + b.height && knight.y < b.y + b.height
						 && knight.y > (double) (b.y + (double) (b.height) * 0.80)) {
					 // checks to see if the player can fit between the floor and the block
					 if (b.y + b.height + knight.height + 1 <= FLOOR) {
						 // stops any jumping
						 knight.y = b.y + b.height + 1;
						 knight.isJumping = true;
						 knight.falling = true;
						 checkVertical = true;
 
					 }
 
				 }
 
				 // LEFT COLLISION
				 if (!checkVertical && knight.x <= b.x && knight.x + knight.width >= b.x
						 && ((knight.y >= b.y && knight.y <= b.y + b.height)
								 || (knight.y + knight.height > b.y && knight.y + knight.height <= b.y + b.height)
								 || knight.y <= b.y && knight.y + knight.height >= b.y + b.height)) {
					 knight.x = b.x - knight.width - 1;
					 // stops horizontal movement
					 if (!Player.isCentered) {
						 Player.setXDirection(0);
					 } else {
						 back.xVelocity = 0;
						 Block.xVelocity = 0;
					 }
				 }
 
				 // RIGHT COLLISION
				 if (!checkVertical && knight.x <= b.x + b.width && knight.x + knight.width >= b.x + b.width
						 && ((knight.y >= b.y && knight.y <= b.y + b.height)
								 || (knight.y + knight.height > b.y && knight.y + knight.height <= b.y + b.height)
								 || knight.y <= b.y && knight.y + knight.height >= b.y + b.height)) {
					 knight.x = b.x + b.width + 1;
					 // stops horizontal movement
					 if (!Player.isCentered) {
						 Player.setXDirection(0);
					 } else {
						 back.xVelocity = 0;
						 Block.xVelocity = 0;
					 }
				 }
 
			 }
			 
			 for(Block b: elements) {
				 if(getClass(b).equals("CrackedStone") && ((CrackedStone)b).startBreak != -1 && System.currentTimeMillis() - ((CrackedStone)b).startBreak >= CrackedStone.breakTime) {
					 remove.add(b);
				 }
				 else if(getClass(b).equals("SpeedBoost") && knight.intersects(b)) {
					 knight.extraSpeed = true;
					 knight.speedStart = System.currentTimeMillis();
					 remove.add(b);
					 numPowers--;
				 }
				 else if(getClass(b).equals("OneUp") && knight.intersects(b)) {
					 knight.lives++;
					 remove.add(b);
					 numPowers--;
				 }
			 }
			 
			 for(Block b: remove) {
				 elements.remove(b);
			 }
 
		 }
 
	 }
 
	 // run() method is what makes the game continue running without end.
	 // other methods to move objects, check for collision, and update the screen
	 public void run() {
		 // slows down the code
		 long lastTime = System.nanoTime();
		 double amountOfTicks = 60;
		 double ns = 1000000000 / amountOfTicks;
		 double delta = 0;
		 long now;
 
		 while (running) { // this is the infinite game loop
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
 
	 // handles keyPresses
	 public void keyPressed(KeyEvent e) {
		 // checks which screen is displayed
		 if(instructions) {
			 if(e.getKeyCode() == KeyEvent.VK_LEFT && instructionsPage != 1) {
				 instructionsPage--;
			 }
			 else if(e.getKeyCode() == KeyEvent.VK_RIGHT && instructionsPage != 10) {
				 instructionsPage++;
			 }
			 
			 if (e.getKeyCode() == 27) {
				 try {
					 GameFrame.currentGameFrame.dispose();
					 new GameFrame(false
							 , false, false, "");
					 running = false;
							 
				 } catch (IOException e1) {
					 e1.printStackTrace();
				 }
			 }
			 
		 }
		 else if (gameEnd) {
			 if (e.getKeyCode() == 27) {
				 try {
					 GameFrame.currentGameFrame.dispose();
					 new GameFrame(false
							 , false, false, "");
					 running = false;
							 
				 } catch (IOException e1) {
					 e1.printStackTrace();
				 }
			 }
		 } else if (mainMenu) {
			 // checks which option is being selected
 
			 if (e.getKeyCode() == 10 && indicatorPos == 320) {
				 levelSelect = false;
				 edit = true;
				 mainMenu = false;
			 } else if (e.getKeyCode() == 10 && indicatorPos == 250) {
				 // enter the levelSelect menu
 
				 mainMenu = false;
				 edit = false;
				 levelSelect = true;
				 // create a new gameframe in the levelSelect menu
				 try {
					 GameFrame.currentGameFrame.dispose();
 
					 new GameFrame(true, false, false, "");
					 running = false;
				 } catch (IOException e1) {
					 e1.printStackTrace();
				 }
			 }
			 else if (e.getKeyCode() == 10 && indicatorPos == 400) {
				 // enter the instructions page
				 instructions = true;
				 mainMenu = false;
			 }
			 // if arrow keys are pressed, move the indicator
			 else if (e.getKeyCode() == KeyEvent.VK_UP && indicatorPos == 320) {
				 indicatorPos = 250;
			 } else if (e.getKeyCode() == KeyEvent.VK_DOWN && indicatorPos == 250) {
				 indicatorPos = 320;
			 }
			 else if(e.getKeyCode() == KeyEvent.VK_DOWN && indicatorPos == 320) {
				 indicatorPos = 400;
			 }
			 else if(e.getKeyCode() == KeyEvent.VK_UP && indicatorPos == 400) {
				 indicatorPos = 320;
			 }
		 } else if (edit) {
			 // checks arrow keys and moves the selected block by 1 pixel
			 if (e.getKeyCode() == 37) {
				 if (curSelected != null && !checkAllIntersection(curSelected))
					 curSelected.x--;
			 } else if (e.getKeyCode() == 39) {
				 if (curSelected != null && !checkAllIntersection(curSelected))
					 curSelected.x++;
			 } else if (e.getKeyCode() == 38) {
				 if (curSelected != null && !checkAllIntersection(curSelected))
					 curSelected.y--;
			 } else if (e.getKeyCode() == 40) {
				 if (curSelected != null && !checkAllIntersection(curSelected))
					 curSelected.y++;
			 } else if (e.getKeyChar() == 'a' || e.getKeyChar() == 'd') {
				 // scrolls the screen
				 back.keyPressed(e);
				 for (Block b : elements) {
					 b.keyPressed(e);
				 }
 
			 } else if (e.getKeyCode() == 27) {
				 edit = false;
				 try {
					 GameFrame.currentGameFrame.dispose();
 
					 new GameFrame(true, false, false, "");
					 running = false;
				 } catch (IOException e1) {
					 e1.printStackTrace();
				 }
			 } else if (e.getKeyCode() == 70 && curSelected != null) {
				 // if the objected is flipped, we replace the original object with a flipped
				 // version
				 elements.remove(curSelected);
				 if (curSelected instanceof Goblin || curSelected instanceof Turret) {
					 try {
						 // adds the flipped image
						 elements.add(hFlip(curSelected));
					 } catch (IOException e1) {
					 }
 
				 }
				 // changes the selected and dragging accordingly
				 curSelected = elements.get(elements.size() - 1);
				 curDragging = curSelected;
			 } else if (e.getKeyCode() == 8) {
				 // if backspace is pressed, delete the selected block
				 if (curSelected != null) {
					 elements.remove(curSelected);
					 curSelected = null;
				 }
 
				 // check if file is saved OR saved and played
			 } else if (e.getKeyCode() == KeyEvent.VK_1) {
				 // save the file
				 updatedSave = "";
				 if (!levelSaved && prevSavedTitle.isEmpty()) { // if user is created a fresh new level
					 // Create a JTextField for user input
					 JTextField textField = new JTextField();
 
					 // Show an input dialog with the text field
					 int option = JOptionPane.showOptionDialog(this, textField, "Name your level!",
							 JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
 
					 // Check if the user clicked OK
					 if (option == JOptionPane.OK_OPTION) {
						 // Get the entered name from the text field
						 newLevelTitle = textField.getText().stripTrailing(); // get the info from text box without
																				 // whitespace
 
						 // add the new level title
						 if (!names.contains(newLevelTitle)) {
							 for (Block b : elements) {
								 updatedSave += b.toString() + ": ";
							 }
							 replaceLine(newLevelTitle, updatedSave);
 
							 addTitle(newLevelTitle); // add the title
 
							 // Display a message indicating that the level has been saved
							 JOptionPane.showMessageDialog(this, "Level saved!", "Save Confirmation",
									 JOptionPane.INFORMATION_MESSAGE);
							 levelSaved = true;
						 } else {
							 // Display a message indicating that the level has not been saved
							 JOptionPane.showMessageDialog(this, "Title already in use. Please try again.",
									 "Invalid Save", JOptionPane.INFORMATION_MESSAGE);
						 }
					 } else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
						 // if user exits trigger cancellation message
						 JOptionPane.showMessageDialog(this, "Save cancelled.", "Invalid Save",
								 JOptionPane.INFORMATION_MESSAGE);
					 }
				 } else if (levelSaved && prevSavedTitle.isEmpty()) {
					 // if previously saved, then update the entry
					 for (Block b : elements) {
						 updatedSave += b.toString() + ": ";
					 }
					 levelSaved = true;
					 // System.out.println(elements + " " + updatedSave);
					 replaceLine(newLevelTitle, updatedSave); // replace line with the entered title --> THIS CASAE AND
																 // ABOVE CASE ONLY OCCUR IF THE USER DIRECTLY CREATES
																 // THEIR NEW DUNGEON
 
					 // display saved
					 JOptionPane.showMessageDialog(this, "Level saved!", "Save Confirmation",
							 JOptionPane.INFORMATION_MESSAGE);
				 } else if (!prevSavedTitle.isEmpty() && !levelSaved) {
					 // if from a previous save file, then execute this
					 for (Block b : elements) {
						 updatedSave += b.toString() + ": ";
					 }
					 replaceLine(prevSavedTitle, updatedSave); // replace line with the given title
 
					 // display saved
					 JOptionPane.showMessageDialog(this, "Level saved!", "Save Confirmation",
							 JOptionPane.INFORMATION_MESSAGE);
 
				 }
			 } else if (e.getKeyCode() == KeyEvent.VK_2) {
				 // enter play mode from the level editor
				 edit = false;
 
				 // Intake elements from file io and start game
				 if (!newLevelTitle.isEmpty()) {
					 try {
						 GameFrame.currentGameFrame.dispose();
 
						 new GameFrame(false, false, true, newLevelTitle);
						 running = false;
					 } catch (IOException e1) {
						 e1.printStackTrace();
					 }
				 } else {
					 try {
						 GameFrame.currentGameFrame.dispose();
 
						 new GameFrame(false, false, true, prevSavedTitle);
						 running = false;
					 } catch (IOException e1) {
						 e1.printStackTrace();
					 }
				 }
				 play = true;
			 }
 
		 }
 
		 else if (play) {
			 
			 if(e.getKeyCode() == 27) {
				 play = false;
				 try {
					 GameFrame.currentGameFrame.dispose();
 
					 new GameFrame(false, false, false, "");
					 running = false;
				 } catch (IOException e1) {
					 e1.printStackTrace();
				 }
			 }
 
			 // checks the knight, Blocks, and the background to see what should change to
			 // each one
			 knight.keyPressed(e);
			 back.keyPressed(e);
 
			 for (Block b : elements) {
				 b.keyPressed(e);
			 }
			 // a.keyPressed(e);`
		 }
		 else if(levelSelect) {
			 if(e.getKeyCode() == 27) {
				 try {
					 GameFrame.currentGameFrame.dispose();
 
					 new GameFrame(false, false, false, "");
					 running = false;
				 } catch (IOException e1) {
					 e1.printStackTrace();
				 }
			 }
		 }
 
	 }
 
	 // executes when keys are released
	 public void keyReleased(KeyEvent e) {
 
		 // if the game has ended, don't do anything
		 if (gameEnd)
			 return;
 
		 // checks which screen is displayed
		 else if (edit) {
			 // calls keyReleased for background and all blocks
			 back.keyReleased(e);
			 for (Block b : elements) {
				 b.keyReleased(e);
			 }
		 }
 
		 else if (play) {
			 // calls keyReleased for background, the knight, and all blocks
			 knight.keyReleased(e);
 
			 back.keyReleased(e);
			 for (Block b : elements) {
				 b.keyReleased(e);
 
			 }
		 }
	 }
 
	 public void keyTyped(KeyEvent e) {
 
	 }
 
	 public void mouseClicked(MouseEvent e) {
	 }
 
	 // handles mousePresses
	 public void mousePressed(MouseEvent e) {
		 // checks which screen is displayed`
		 if (edit) {
			 // checks if an existing block is pressed
			 boolean chosen = false;
			 for (Block b : elements) {
				 if (mousePressedBlock(b, e)) {
					 // calls the block's mousePressed, the block can now be dragged, is selected,
					 // and is not part of the sidebar
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
			 // splits the sidebar based on tabs
			 if (tabPressed.equals("blocks")) {
				 for (Block b : blockSidebar) {
					 if (mousePressedBlock(b, e)) {
						 // calls mousePressed from b, b is now being dragged, the sideBar was pressed
						 b.mousePressed(e);
						 curDragging = b;
						 sidebarPressed = true;
						 chosen = true;
					 }
				 }
			 } else if (tabPressed.equals("enemies")) {
				 for (Block b : enemySidebar) {
					 if (mousePressedBlock(b, e)) {
						 b.mousePressed(e);
						 curDragging = b;
						 sidebarPressed = true;
						 chosen = true;
					 }
				 }
			 } else {
				 for (Block b : powerUpSidebar) {
					 if (mousePressedBlock(b, e)) {
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
 
	 // handles when the mouse is released
	 public void mouseReleased(MouseEvent e) {
 
		 if (edit) {
 
			 // if there IS a block being dragged
			 if (curDragging != null) {
				 // checks if it is still on the sidebar
				 if (curDragging.x <= TAB_X) {
					 // remove it if it's still on the sidebar and not dragged into the sandbox
					 elements.remove(curDragging);
					 curSelected = null;
				 } else if (hover != null) { // if there IS a block being hovered over b the curDragging block
 
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
						 if (hover instanceof Goblin && !onTop) {
							 elements.remove(hover);
						 }
 
						 curSelected = hover;
					 }
					 // checks if a block is trying to be placed on an existing block
				 } else if (hover == null && checkAllIntersection(curDragging)) {
					 elements.remove(curDragging);
					 curSelected = null;
				 } else {
					 if (hover == null && curDragging instanceof Goblin) {
						 elements.remove(curDragging);
					 }
				 }
			 }
			 // once mouse is released
			 curDragging = null;
			 hover = null;
		 }
 
	 }
 
	 public void mouseEntered(MouseEvent e) {
 
	 }
 
	 public void mouseExited(MouseEvent e) {
 
	 }
 
	 // handles when the mouse is dragged
	 public void mouseDragged(MouseEvent e) {
		 // variable declarations
		 int tmpX;
		 int tmpY;
		 int centerX;
		 int centerY;
 
		 if (edit && curDragging != null) {
			 // the selected block is now dragged
			 curSelected = curDragging;
			 // checks if the sidebar is being pressed
			 if (sidebarPressed) {
				 // checks for the element pressed and makes a new element of the correct type
				 if (tabPressed.equals("blocks")) {
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
 
					 else if (curDragging.equals(tabCrackedStone)) {
						 try {
							 elements.add(new CrackedStone(TAB_X - 110, 230, CrackedStone.width, CrackedStone.height,
									 crackedStoneImage));
						 } catch (IOException IOE) {
						 }
						 curDragging = elements.get(elements.size() - 1);
					 } else if (curDragging.equals(tabChest)) {
						 try {
							 elements.add(new Chest(TAB_X - 110, 340, Chest.width, Chest.height, closedChestImage));
						 } catch (IOException IOE) {
						 }
						 curDragging = elements.get(elements.size() - 1);
					 }
				 } else if (tabPressed.equals("enemies")) {
					 if (curDragging.equals(tabGoblin)) {
						 try {
							 elements.add(new Goblin(this, TAB_X - 110, 20, Goblin.width, Goblin.height, goblinRunLeft,
									 goblinRunRight, false));
 
						 } catch (IOException IOE) {
						 }
						 curDragging = elements.get(elements.size() - 1);
					 }
					 if (curDragging.equals(tabTurret)) {
						 try {
							 elements.add(new Turret(this, TAB_X - 110, 100, Turret.width, Turret.height, turretRight, turLeft,
									 turRight, true));
						 } catch (IOException IOE) {
						 }
						 curDragging = elements.get(elements.size() - 1);
					 }
					 if (curDragging.equals(tabTurretLeft)) {
						 try {
							 elements.add(new Turret(this, TAB_X - 110, 100, Turret.width, Turret.height, turretLeft, true,
									 false, true));
						 } catch (IOException IOE) {
						 }
						 curDragging = elements.get(elements.size() - 1);
					 }
				 } else {
					 if (curDragging.equals(tabOneUp)) {
						 try {
							 elements.add(new OneUp(TAB_X - 110, 20, OneUp.width, OneUp.height, oneUpImage));
						 } catch (IOException IOE) {
						 }
						 curDragging = elements.get(elements.size() - 1);
					 } else if (curDragging.equals(tabSpeedBoost)) {
						 try {
							 elements.add(new SpeedBoost(TAB_X - 110, 100, SpeedBoost.width, SpeedBoost.height,
									 speedBoostImage));
						 } catch (IOException IOE) {
						 }
						 curDragging = elements.get(elements.size() - 1);
					 }
				 }
 
				 sidebarPressed = false;
			 }
			 // calls mouseDragged of the dragged block
			 curDragging.mouseDragged(e);
 
			 // loops through all the elements
			 boolean intersected = false;
			 onTop = false; // checks if the goblin is on top of a block
			 for (int i = 0; i < elements.size(); i++) {
				 Block b = elements.get(i);
				 // skips the block if it is the one being dragged
				 if (b == curDragging)
					 continue;
				 // checks if it is dragged onto another piece
				 if (curDragging.intersects(b)) {
 
					 tmpX = curDragging.x;
					 tmpY = curDragging.y;
					 centerX = tmpX + curDragging.width / 2;
					 centerY = tmpY + curDragging.height / 2;
					 // looks for the nearest edge and forces it there
 
					 // LEFT EDGE
					 if (Math.abs(b.x - centerX) <= Math.abs(b.x + b.width - centerX)
							 && Math.abs(b.x - centerX) <= Math.abs(b.y - centerY)
							 && Math.abs(b.x - centerX) <= Math.abs(b.y + b.height - centerY)) {
 
						 try {
							 // creates a holographic image of where the block would be if released
							 hover = decipherBlock(curDragging, b.x - curDragging.width, curDragging.y,
									 curDragging.width, curDragging.height);
 
						 } catch (IOException e1) {
						 }
					 }
					 // RIGHT EDGE
					 else if (Math.abs(b.x + b.width - centerX) <= Math.abs(b.x - centerX)
							 && Math.abs(b.x + b.width - centerX) <= Math.abs(b.y - centerY)
							 && Math.abs(b.x + b.width - centerX) <= Math.abs(b.y + b.height - centerY)) {
 
						 try {
							 hover = decipherBlock(curDragging, b.x + b.width, curDragging.y, curDragging.width,
									 curDragging.height);
						 } catch (IOException e1) {
						 }
 
						 curDragging.x++;
 
					 }
					 // TOP EDGE
					 else if (Math.abs(b.y - centerY) <= Math.abs(b.x + b.width - centerX)
							 && Math.abs(b.y - centerY) <= Math.abs(b.x - centerX)
							 && Math.abs(b.y - centerY) <= Math.abs(b.y + b.height - centerY)) {
 
						 try {
							 hover = decipherBlock(curDragging, curDragging.x, b.y - curDragging.height,
									 curDragging.width, curDragging.height);
							 onTop = true;
						 } catch (IOException e1) {
						 }
						 curDragging.y--;
 
					 }
					 // BOTTOM EDGE
					 else if (Math.abs(b.y + b.height - centerY) <= Math.abs(b.x - centerX)
							 && Math.abs(b.y + b.height - centerY) <= Math.abs(b.y - centerY)
							 && Math.abs(b.y + b.height - centerY) <= Math.abs(b.x - b.width - centerX)) {
 
						 // if hover is an instance of turret, then create a new turret block based on
						 // its orientation
						 try {
							 if (hover instanceof Turret) {
								 if (((Turret) hover).l) {
									 hover = new Turret(this, curDragging.x, b.y + b.height, curDragging.width,
											 curDragging.height, turretLeft, true, false, true);
								 } else if (((Turret) hover).r) {
									 hover = new Turret(this, curDragging.x, b.y + b.height, curDragging.width,
											 curDragging.height, turretRight, true, false, true);
								 }
							 } else {
								 hover = decipherBlock(curDragging, curDragging.x, b.y + b.height, curDragging.width,
										 curDragging.height);
 
							 }
						 } catch (IOException e1) {
						 }
 
					 }
					 // if the hover is intersecting an existing block it can not be placed
					 if (hover != null && checkAllIntersection(hover)) {
						 hover = null;
					 }
					 intersected = true;
				 }
			 }
			 // no hover is no intersection
			 if (!intersected)
				 hover = null;
		 }
 
	 }
 
	 public void mouseMoved(MouseEvent e) {
 
	 }
 
	 // draws the sidebar
	 public void drawSidebar(Graphics2D g) {
		 // sets the colour of the rectangle and draws it
		 g.setColor(Color.white);
		 g.fillRect(0, 0, 128, GAME_HEIGHT);
		 g.setColor(Color.black);
 
		 g.fillRect(TAB_X - 5, 0, 5, GAME_HEIGHT);
 
		 // checks which tab to load, and draw the sidebar associated with it
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
			 tabChest.draw(g);
 
		 } else {
			 // if not loaded then the tab looks different
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
			 tabTurretLeft.draw(g);
 
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
 
			 tabOneUp.draw(g);
			 tabSpeedBoost.draw(g);
 
		 } else {
			 g.setColor(Color.CYAN);
			 g.fillRect(TAB_X, 2 * TAB_HEIGHT + 20, TAB_WIDTH, TAB_HEIGHT + 40);
			 g.setColor(Color.black);
			 g.setFont(rotatedFont);
			 g.drawString("Powerups", GAME_WIDTH / 7 + 10, 2 * TAB_HEIGHT + 30);
		 }
 
	 }
 
	 // checks if a block is intersecting with the Block block
	 public boolean checkAllIntersection(Block block) {
		 // loops all elements
		 for (int i = 0; i < elements.size() - 1; i++) {
			 Block b = elements.get(i);
			 // if intersecting return true
			 if (!block.equals(b) && block.intersects(b) && !b.equals(curDragging))
				 return true;
		 }
		 return false;
 
	 }
 
	 // resizes an image to preferred width and height
	 public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		 Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		 BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
 
		 Graphics2D g2d = dimg.createGraphics();
		 g2d.drawImage(tmp, 0, 0, null);
		 g2d.dispose();
 
		 return dimg;
	 }
 
	 // add title if user creates a new level to the names.txt file to prevent
	 // duplicate titles.
	 public void addTitle(String title) {
		 // adds a title to the names.txt file for easy checking if there are any
		 // duplicate names (bad)
		 try {
			 BufferedWriter writer = new BufferedWriter(new FileWriter("names.txt", true));
			 writer.write(title + ", ");
			 writer.close();
		 } catch (Exception e) {
			 System.out.println("Problem adding name.");
		 }
	 }
 
	 // rewrites the LevelSave.txt file into a temp input stream and then overwrites
	 // the previous save file to
	 // imitate the effect of "overwritting" a save level. (this is the only viable
	 // way to overwrite in java file io)
	 public static void replaceLine(String title, String save) {
 
		 try {
			 // input the (modified) file content to the StringBuffer "input"
			 BufferedReader file = new BufferedReader(new FileReader("LevelSave.txt"));
			 StringBuffer inputBuffer = new StringBuffer();
			 String line;
			 boolean containedTitle = false;
 
			 while ((line = file.readLine()) != null) {
 
				 if (line.startsWith(title)) {
					 line = title + ": " + save; // replace the line here
					 inputBuffer.append(line);
					 inputBuffer.append('\n');
					 containedTitle = true;
				 } else {
					 inputBuffer.append(line);
					 inputBuffer.append('\n');
				 }
 
			 }
 
			 if (!containedTitle) {
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
 
	 // reads the first words (AKA titles) for each level into the names arraylist so
	 // no duplicate titles are made (our unique ID sys for levels relies on unique
	 // names)
	 // also required for num of buttons and height of scrollpane
	 public void readFirstWords() {
		 // read titles to check for no duplicates
 
		 String filePath = "names.txt"; // Provide the path to your text file
		 try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			 String line;
 
			 while ((line = reader.readLine()) != null) {
				 // Split the line into words using space as the delimiter
				 String[] words = line.split(", ");
				 for (String name : words) {
					 names.add(name);
				 }
			 }
 
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
 
	 }
 
	 // reads the data for a specific level and adds all blocks of that level into
	 // the elements arraylist
	 // so the level can be loaded and played/edited
	 public void readData(String title) {
		 // if the title is not found or there is no title
 
		 if (title.equals("")) {
			 return;
		 } else {
			 String filePath = "LevelSave.txt"; // Provide the path to your text file
			 try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
				 String line;
 
				 while ((line = reader.readLine()) != null) {
					 // Split the line into words using space as the delimiter
					 if (line.startsWith(title)) {
						 String[] words = line.split(": ");
						 for (int i = 1; i < words.length; i++) {
							 // i = 1 skip over the title of the thing ASSUMES THAT the user doesn't enter :
							 // in the title itself
							 String[] inputs = words[i].split(" "); // splits based on space
							 if (inputs[0].equals("Ice")) {
 
								 elements.add(new Ice(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), iceImage));
							 } else if (inputs[0].equals("Stone")) {
								 elements.add(new Stone(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), stoneImage));
							 } else if (inputs[0].equals("Portal")) {
								 elements.add(new Portal(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), portalImage));
							 } else if (inputs[0].equals("Ladder")) {
								 elements.add(new Ladder(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), ladderImage));
							 } else if (inputs[0].equals("CrackedStone")) {
								 elements.add(new CrackedStone(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), crackedStoneImage));
							 } else if (inputs[0].equals("Chest")) {
								 elements.add(new Chest(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), closedChestImage));
 
							 } else if (inputs[0].equals("Goblin")) {
								 elements.add(new Goblin(this, Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), goblinRunLeft,
										 goblinRunRight, true));
								 // System.out.println(a);
							 } else if (inputs[0].equals("Turret")) {
								 if (inputs[5].equals("true")) {
									 elements.add(new Turret(this, Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
											 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), turretLeft, true,
											 false, true));
 
								 } else {
									 elements.add(new Turret(this, Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
											 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), turretRight,
											 turLeft, turRight, true));
 
								 }
 
							 } else if (inputs[0].equals("OneUp")) {
								 elements.add(new OneUp(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), oneUpImage));
							 } else if (inputs[0].equals("SpeedBoost")) {
								 elements.add(new SpeedBoost(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]),
										 Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), speedBoostImage));
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
 
	 // returns a Block as a more specific class depending on what the className is
	 public Block decipherBlock(Block b, int x, int y, int width, int height) throws IOException {
		 Class<?> type = b.getClass();
		 String className = type.getName();
		 if (className.equals("Stone")) {
			 return new Stone(x, y, width, height, b.img);
		 } else if (className.equals("Ice")) {
			 return new Ice(x, y, width, height, b.img);
		 } else if (className.equals("Portal")) {
			 return new Portal(x, y, width, height, b.img);
		 } else if (className.equals("Ladder")) {
			 return new Ladder(x, y, width, height, b.img);
		 } else if (className.equals("CrackedStone")) {
			 return new CrackedStone(x, y, width, height, b.img);
		 } else if (className.equals("Chest")) {
			 return new Chest(x, y, width, height, b.img);
		 } else if (className.equals("Goblin")) {
			 return new Goblin(this, x, y, width, height, goblinRunLeft, goblinRunRight, false);
			 // }
			 // else if (className.equals("Turret")) {
			 // return new Turret(x, y, width, height, b.img, turLeft, turRight, true);
		 } else if (className.equals("OneUp")) {
			 return new OneUp(x, y, width, height, b.img);
		 } else if (className.equals("SpeedBoost")) {
			 return new SpeedBoost(x, y, width, height, b.img);
		 }
 
		 return b;
	 }
 
	 // returns the name of the class of Block b
	 public static String getClass(Block b) {
		 Class<?> type = b.getClass();
		 return type.getName();
	 }
 
	 // returns a Block that is horizontally flipped compared to b
	 public Block hFlip(Block b) throws IOException {
		 if (b instanceof Turret) {
			 turFlipNum++; // used to account for turret flips
			 System.out.println("hi " + turFlipNum);
		 }
		 if (b instanceof Goblin) {
			 goblinFlipNum++;
		 }
		 return decipherBlock(b, b.x, b.y, b.width, b.height);
	 }
 
	 // checks if the mouse is on a block, flip is counted for
	 public boolean mousePressedBlock(Block b, MouseEvent e) {
		 if (b.width < 0) {
			 return b.x + b.width <= e.getX() && b.x >= e.getX() && b.y <= e.getY() && b.y + b.height >= e.getY();
		 } else {
			 return b.x <= e.getX() && b.x + b.width >= e.getX() && b.y <= e.getY() && b.y + b.height >= e.getY();
		 }
	 }
 
	 // called when the player either wins or loses
	 public void gameEnd(Graphics2D g) {
 
		 // stops all movement
		 knight.keysPressed.clear();
		 back.keysPressed.clear();
		 Block.keysPressed.clear();
		 Player.xVelocity = 0;
		 Player.yVelocity = 0;
		 Block.xVelocity = 0;
		 back.xVelocity = 0;
 
		 // checks to see if the player won
		 if (win) {
			 // draws the translucent film on the screen
			 g.setColor(new Color(255, 255, 255, 150));
			 g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
			 repaint();
 
			 // opens the chest and displays the winning message
			 endChest.img = openChestImage;
			 g.setFont(new Font("Impact", Font.PLAIN, TITLE_SIZE));
			 g.setColor(new Color(0, 0, 0, gameEndAlpha > 255 ? 255 : gameEndAlpha));
			 g.drawString("YOU WIN", 250, 300);
			 g.setColor(new Color(0, 0, 0, gameEndAlpha < 200 ? 0 : gameEndAlpha - 200));
			 g.setFont(new Font("Impact", Font.PLAIN, FONT_SIZE));
			 g.drawString("Time: " + (endTime - startTime) / 1000, 380, 350);
			 if (gameEndAlpha < 455)
				 gameEndAlpha++;
		 } else {
			 g.setColor(new Color(255, 0, 0, 150));
			 g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
			 repaint();
 
			 g.setFont(new Font("Impact", Font.PLAIN, TITLE_SIZE));
			 g.setColor(new Color(0, 0, 0, gameEndAlpha > 255 ? 255 : gameEndAlpha));
			 g.drawString("YOU DIED", 250, 300);
			 g.setColor(new Color(0, 0, 0, gameEndAlpha));
			 if (gameEndAlpha < 255)
				 gameEndAlpha++;
 
		 }
 
	 }
 
	 public void reset() {
		 
	 }
	 
 }