/*
 * Ethan Lin & Andrew Chen
 * January 12, 2023
 * Player class defines all the properties and behaviour of a Player
 */

//imports
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.ImageIcon;

public class Player extends Rectangle {

	// global variables
	// player animations
	private Image afkAnimation = new ImageIcon("Images/KnightAfk.gif").getImage();
	private Image leftAnimation = new ImageIcon("Images/KnightRunLeft.gif").getImage();
	private Image rightAnimation = new ImageIcon("Images/KnightRunRight.gif").getImage();

	// location properties of the player
	public static boolean isLeft = false;
	public static boolean isRight = false;
	public boolean left = false;
	public static boolean isCentered = true;
	
	public AlphaComposite TRANSLUCENT = java.awt.AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F);
	public AlphaComposite OPAQUE = java.awt.AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0F);

	// has to do with jumping
	public boolean isJumping = false;
	public boolean falling = false;
	public int jumpHeight = 100; // will be edited
	public int jumpCount = 0;
	public int jumpLimit = 100; // will be edited
	public final int JUMP_SPEED = 6;
	public double initY = 0;
	public static boolean canJump = true;
	public static int lastMoved = 0;
	public boolean extraSpeed = false;
	public boolean opaque = true;
	public int CLIMB_SPEED = 3
	;


	// has to do with speed

	public static int yVelocity;
	public static int xVelocity;
	public static int SPEED = 4; // movement speed
	public int fallCounter = 0;
	public int lives = 1;
	
	public long speedStart = -1;
	
	public int flashCnt = 0;
	
	public long invincibleStart = -1;
	public int iFrames = 2000;
	
	public GamePanel gamePanel;

	public static boolean oneScreen = false;

	public static Block under = null;
	public boolean onLadder = false;
	public boolean onTopLadder = false;
	// contains the keys pressed
	public HashSet<Character> keysPressed = new HashSet<Character>();

	// create the player at x, y coordinates on the screen with length, width
	// (constructor)
	public Player(GamePanel gp, int x, int y, int l, int w) throws IOException {
		super(x, y, l, w);
		gamePanel = gp;
	}

	// handles key presses
	public void keyPressed(KeyEvent e) {
		keysPressed.add(e.getKeyChar());
	}

	// handles key releases
	public void keyReleased(KeyEvent e) {
		// removes the key from the set
		keysPressed.remove(e.getKeyChar());
		// checks to see if a movement key was released and stops the movement in that
		// direction
		if (e.getKeyChar() == 'd') {
			isRight = false;
			setXDirection(0);
			move();
		} else if (e.getKeyChar() == 'a') {
			isLeft = false;
			setXDirection(0);
			move();
		} else if (e.getKeyChar() == 'w') {
			canJump = true;
		}

		// if(onLadder && e.getKeyChar() ==  'w'){
		// 	y -= 10;
		// 	falling = false;
		// 	onTopLadder = true;
		// }

	}

	// sets the xVelocity
	public static void setXDirection(int xDirection) {
		xVelocity = xDirection;
	}

	// sets the yVelocity
	public void setYDirection(int yDirection) {
		yVelocity = yDirection;
	}

	// called when the player jumps and handles the logic
	public void jump() {
		falling = false;
		initY = getY();
		isJumping = true;
		jumpCount = 0;
	}

	// moves the player
	public void move() {
		// System.out.println(" onladder: " + onLadder + " top: " + onTopLadder);

		
		//climb ladders, when w or s are pressed
		if(onLadder && keysPressed.contains('w')){
			setYDirection(-CLIMB_SPEED);
		} else if (( onLadder || onTopLadder) && keysPressed.contains('s') ){
			setYDirection(CLIMB_SPEED);
		} 
		else{
			setYDirection(0);
		}


		if(extraSpeed && System.currentTimeMillis() - speedStart >= SpeedBoost.duration) {
			extraSpeed = false;
			speedStart = -1;
		}
		
		if(extraSpeed) SPEED = 6;
		if(!extraSpeed) SPEED = 4;
		
		// no need to move if the screen is following the player
		if (isCentered) {
			setXDirection(0);
		}

		// if a movement key is pressed and the player is not centrered move the player
		// in that direction
		if (keysPressed.contains('d')) {
			isRight = true;
			isLeft = false;
			lastMoved = 1;
			if (!isCentered) {
				setXDirection(SPEED + ((under != null && GamePanel.getClass(under).equals("Ice"))? 2:0));
			}

		} else if (keysPressed.contains('a')) {
			isLeft = true;
			isRight = false;
			lastMoved = -1;
			if (!isCentered) {
				setXDirection(-SPEED + ((under != null && GamePanel.getClass(under).equals("Ice"))? -2:0));
			}
		}
		else {
			if(under != null && GamePanel.getClass(under).equals("Ice")) {
				setXDirection(2*lastMoved);
			}
			else {
				setXDirection(0);
			}
		}

		if (keysPressed.contains('w') && !isJumping && canJump && !falling && !onLadder) {
			// Only allow jumping if not already jumping
			jump();
			canJump = false;
		}

		/// if the player is jumping
		if (isJumping) {

			// If jumping, move up until reaching jumpHeight

			// the if statement below slowly decrease the speed to simulate real life
			// physics
			if (getY() - (initY - jumpHeight) > jumpHeight / 2 && !falling) {
				yVelocity = -JUMP_SPEED;
			} else if (getY() - Math.abs(initY - jumpHeight) > jumpHeight / 3 && !falling) {
				yVelocity = -6;
			} else if (getY() > initY - jumpHeight && !falling) {
				yVelocity = -4;
			}
			// if reached the peak of jump, start falling
			else {

				//code to make knight fall
				falling = true;
				jumpCount++;
				yVelocity = JUMP_SPEED; // Start falling
				if (getY() >= initY) {
					y = (int) initY;
					yVelocity = 0;
					isJumping = false;
					falling = false;
				}
								//force knight on top of ladder
								// if(onLadder) {
								// 	System.out.println("hey");
								// 	y-=50;
								// 	onTopLadder =true;
								// 	falling = false;
								// }
				
			}
			if (x != 0 &&!canFall() && falling) {
				isJumping = false;
				falling = false;
				
				yVelocity = 0;
			}

		} else if (!onLadder && !onTopLadder){
			if (x != 0 && canFall()) {
				y += 8;
				falling = true;
				
			} else {
				falling = false;
			}
		}
		// moves the player
		if(x != 0 && x != Integer.MIN_VALUE) {
			x = x + xVelocity;
			if(x <= 0) x = 1;
		}
		
		if(under != null && GamePanel.getClass(under).equals("CrackedStone") && ((CrackedStone)under).startBreak == -1) {
			((CrackedStone)under).startBreak = System.currentTimeMillis();
		}
		
		y = y + yVelocity;

	}

	// draws the player
	public void draw(Graphics2D g) {
		canFall();
		// if the knight crosses the middle after coming from a side

		if (!oneScreen && ((left && isRight && x >= 420) || (!left && isLeft && x <= 420))) {
			isCentered = true;
			x = 420; // force the knight centered
		}

		// draws a different animation depending on the state of the knight
		if(invincibleStart != -1) {
			if(opaque && flashCnt % 3 == 0) {
				g.setComposite(TRANSLUCENT);
				opaque = !opaque;
			}
			else if(!opaque && flashCnt % 3 == 0) {
				g.setComposite(OPAQUE);
				opaque = !opaque;
			}
			flashCnt++;
		}
		if (isLeft) {
			g.drawImage(leftAnimation, x, y, null);
		} else if (isRight) {
			g.drawImage(rightAnimation, x, y, null);
		} else {
			g.drawImage(afkAnimation, x, y, null);
		}
	}

	public boolean canFall() {
		int botRightX = x + width;
		int botRightY = y + height;
		for (Block b : gamePanel.elements) {
			if (GamePanel.walkThrough.contains(GamePanel.getClass(b))) continue;
			
			if ((((x >= b.x && x <= b.x + b.width) || (botRightX >= b.x && botRightX <= b.x + b.width)
					|| (x >= b.x && botRightX <= b.x + b.width)) && botRightY >= b.y - 1 && y <= b.y)) {
				under = b;
				//System.out.println("false" + under);
				return false;
			}
		}

		if (botRightY >= GamePanel.FLOOR) {
			y = GamePanel.FLOOR - height;
			//System.out.println("false floor");
			under = null;
			return false;
		}

		under = null;
		return true;

	}
}