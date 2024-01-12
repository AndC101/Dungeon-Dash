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

	//global variables
	//player animations
    private Image afkAnimation = new ImageIcon("Images/KnightAfk.gif").getImage();
    private Image leftAnimation = new ImageIcon("Images/KnightRunLeft.gif").getImage();
    private Image rightAnimation = new ImageIcon("Images/KnightRunRight.gif").getImage();

    //location properties of the player
    public static boolean isLeft = false;
    public static boolean isRight = false;
    public boolean left = false;
    public static boolean isCentered = true;
    
    //has to do with jumping



    public boolean isJumping = false;
	public boolean falling = false;
    public int jumpHeight = 100; // will be edited
    public int jumpCount = 0;
    public int jumpLimit = 100; // will be edited
	public final int JUMP_SPEED = 6;
	public double initY = 0;
	public static boolean canJump = true;
    
	//has to do with speed

    public int yVelocity;
    public int xVelocity;
    public final int SPEED = 5; // movement speed

    public static boolean oneScreen = false;

    //contains the keys pressed
    public HashSet<Character> keysPressed = new HashSet<Character>();

    // create the player at x, y coordinates on the screen with length, width (constructor)
    public Player(int x, int y, int l, int w) throws IOException {
        super(x, y, l, w);
    }
    //handles key presses
    public void keyPressed(KeyEvent e) {
    	
    	keysPressed.add(e.getKeyChar());
    	
    }
    
    //handles key releases
    public void keyReleased(KeyEvent e) {
    	//removes the key from the set
    	keysPressed.remove(e.getKeyChar());
    	//checks to see if a movement key was released and stops the movement in that direction
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
    }

    //sets the xVelocity
    public void setXDirection(int xDirection) {
        xVelocity = xDirection;
    }

    //sets the yVelocity
    public void setYDirection(int yDirection) {
        yVelocity = yDirection;
    }

    //called when the player jumps and handles the logic 
    public void jump() {
		falling = false;
		initY = getY();
        isJumping = true;
        jumpCount = 0;
    }
    
    //moves the player
    public void move() {
    	
    	//no need to move if the screen is following the player
    	if(isCentered) {
    		setXDirection(0);
    	}

    	//if a movement key is pressed and the player is not centrered move the player in that direction
        if (keysPressed.contains('d')) {
            isRight = true;

            isLeft = false;

            if(!isCentered){
                setXDirection(SPEED);
            }

        } 
        else if (keysPressed.contains('a')) {
            isLeft = true;
            isRight = false;
            if(!isCentered){
                setXDirection(SPEED*-1);
            }
        } 
        
        if (keysPressed.contains('w') && !isJumping && canJump) {
            // Only allow jumping if not already jumping 
            jump();
            canJump = false;
        } 
    	
        ///if the player is jumping 
        if (isJumping) {
            if (jumpCount < jumpLimit) {
                // If jumping, move up until reaching jumpHeight

				//the if statement below slowly decrease the speed to simulate real life physics
                if (getY() - (initY - jumpHeight) > jumpHeight/2  && !falling) {
                    yVelocity = -JUMP_SPEED;
                } else if (getY() - Math.abs(initY - jumpHeight) > jumpHeight/3  && !falling) {
                    yVelocity = -6;
				} else if (getY() > initY-jumpHeight && ! falling) {
					yVelocity = -4;
				}
				//if reached the peak of jump, start falling
				else {
					falling = true;
                    jumpCount++;
                    yVelocity = JUMP_SPEED; // Start falling
					if(getY() >= initY) {
						y = (int)initY;
						yVelocity = 0;
						isJumping = false;
						falling = false;
					}
                }
            } else {
                isJumping = false;
                yVelocity = 0;
            }
        }
        //moves the player
        x = x + xVelocity;
        y = y + yVelocity;
    }
    //draws the player
    public void draw(Graphics g) {



    	//if the knight crosses the middle after coming from a side

        if(!oneScreen && ((left && isRight && x >= 420) || (!left && isLeft && x <= 420))) {
            isCentered = true;
            x = 420; //force the knight centered 
        }

        //draws a different animation depending on the state of the knight
        if (isLeft) {
            g.drawImage(leftAnimation, x, y, null);
        } else if (isRight) {
            g.drawImage(rightAnimation, x, y, null);
        } else {
            g.drawImage(afkAnimation, x, y, null);
        }
    }

}