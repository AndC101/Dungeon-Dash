/*
 * Ethan Lin & Andrew Chen
 * January 12, 2023
 * Background class defines the behavior of the backgrounds of the edit and play screens
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;


public class Background {
	
	//global variables
	boolean drag = false;
	BufferedImage img;
    public int xVelocity; //current X Velocity
    public final int SPEED = 1; // movement speed
    public int x = 0;
    public int y = 0;
    
    //which keys are pressed
    public HashSet<Character> keysPressed = new HashSet<Character>();

    //constructor
    public Background (int x, int y, BufferedImage i) {
        this.x = x;
        this.y = y;
        img = i;
    }

	public void keyPressed(KeyEvent e) {
		keysPressed.add(e.getKeyChar());
    }

    public void keyReleased(KeyEvent e) {
    	keysPressed.remove(e.getKeyChar());
    }
    //sets the x velocity
    public void setXDirection(int xDirection) {
        xVelocity = xDirection;
    }

    //moves the background
    public void move() {
		
		//checks if a movement key is pressed
		if (keysPressed.contains('d')) {
			if (Player.isCentered) {
				setXDirection(-SPEED);
			}
		} else if (keysPressed.contains('a')) {
			if (Player.isCentered) {
				setXDirection(SPEED);
			}
		}
		//if on the play menu and the player isn't centered there is no need to move the background
		if(GamePanel.play && !Player.isCentered) {
			setXDirection(0);
		}
		//if the player is not moving there is no need to move the background
		if(!keysPressed.contains('a') && !keysPressed.contains('d')) setXDirection(0);
		
		//moves the background 
        x = x + xVelocity;
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

	//draws the backrgound
    public void draw(Graphics g) {

        //extends the background to 1000 times the original size to make it seem infinite
        for(int i = 0; i < 1000; i++) {
            g.drawImage(img, x + i*2000, y, 2000, 550, null);
            g.drawImage(img, x + i*-2000, y, 2000, 550, null);

        }
    }



}