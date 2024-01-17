/*
 * Ethan Lin & Andrew Chen
 * January 12, 2023
 * Describes properties and behavior of all blocks
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;

public class Block extends Rectangle {
	// global variables

	// has to do with drag
	int diffX, diffY;
	boolean drag = false;

	BufferedImage img;
	public static int xVelocity;
	public static final int SPEED = 2; // movement speed

	// current keys pressed
	public static HashSet<Character> keysPressed = new HashSet<Character>();

	// Block constructor
	public Block(int x, int y, int l, int w, BufferedImage i) {
		super(x, y, l, w);
		img = i;
	}

	// overloaded Block constructor
	public Block(int x, int y, int l, int w) {
		super(x, y, l, w);
	}

	// handles when the mouse is pressed
	public void mousePressed(MouseEvent e) {
		diffX = e.getX() - x;
		diffY = e.getY() - y;
		drag = true;
	}

	// handles when the mouse is released
	public void mouseReleased(MouseEvent e) {
		drag = false;
	}

	// handles when the mouse is dragged
	public void mouseDragged(MouseEvent e) {
		x = e.getX() - diffX;
		y = e.getY() - diffY;
	}

	// converts the block to a string describing the block
	public String toString() {
		return x + " " + y + " " + width + " " + height;
	}

	// handles when a key is pressed
	public void keyPressed(KeyEvent e) {
		keysPressed.add(e.getKeyChar());
	}

	// handles when a key is released
	public void keyReleased(KeyEvent e) {
		keysPressed.remove(e.getKeyChar());
	}

	// sets the xVelocity variable
	public void setXDirection(int xDirection) {
		xVelocity = xDirection;
	}

	// moves the block
	public void move() {
		// checks if a movement key is pressed
		if (keysPressed.contains('d')) {
			if (Player.isCentered) {
				setXDirection(-SPEED);
			}
		} else if (keysPressed.contains('a')) {
			if (Player.isCentered) {
				setXDirection(SPEED);
			}
		} else {
			setXDirection(0);
		}
		// if the player is not centered, blocks should not move
		if (!Player.isCentered)
			setXDirection(0);

		// move the blocks
		x = x + xVelocity;
	}

	// draws the block
	public void draw(Graphics2D g) {
		g.drawImage(img, x, y, width, height, null);
	}

}