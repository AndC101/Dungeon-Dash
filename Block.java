import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;

public class Block extends Rectangle {

	int diffX, diffY;
	boolean drag = false;
	BufferedImage img;
	public static int xVelocity;
	public static final int SPEED = 5; // movement speed
	
	public HashSet<Character> keysPressed = new HashSet<Character>();

	public Block(int x, int y, int l, int w, BufferedImage i) {
		super(x, y, l, w);
		img = i;
	}

	public Block(int x, int y, int l, int w) {
		super(x, y, l, w);
	}

	public void mousePressed(MouseEvent e) {
		diffX = e.getX() - x;
		diffY = e.getY() - y;
		drag = true;
	}

	public void mouseReleased(MouseEvent e) {
		drag = false;

	}

	public void mouseDragged(MouseEvent e) {
		x = e.getX() - diffX;
		y = e.getY() - diffY;
	}

	public String toString() {
		return x + " " + y + " " + width + " " + height;
	}

	public void keyPressed(KeyEvent e, boolean play) {
		keysPressed.add(e.getKeyChar());
	}

	public void keyReleased(KeyEvent e, boolean play) {
		keysPressed.remove(e.getKeyChar());
		if(e.getKeyChar() == 'a' || e.getKeyChar() == 'd') {
			if(play) {
				
				if(e.getKeyChar() == 'a' && keysPressed.contains('d')) {
					setXDirection(-5);
					Player.isRight = true;
				}
				else if(e.getKeyChar() == 'd' && keysPressed.contains('a')) {
					setXDirection(5);
					Player.isLeft = true;
				}
				else {
					setXDirection(0);
				}
				
			}
			else {
				setXDirection(0);
			}
		}
		
	}

	public void setXDirection(int xDirection) {
		xVelocity = xDirection;
	}

	public void move() {
	
			if (keysPressed.contains('d')) {
				if (Player.isCentered) {
					setXDirection(-SPEED);
				}
			} else if (keysPressed.contains('a')) {
				if (Player.isCentered) {
					setXDirection(SPEED);
				}
			}
			
			if(!Player.isCentered) setXDirection(0);
			
		
		x = x + xVelocity;
	}

	public void draw(Graphics2D g) {
		g.drawImage(img, x, y, width, height, null);
	}

}