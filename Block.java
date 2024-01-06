import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Block extends Rectangle {

	int diffX, diffY;
	boolean drag = false;
	BufferedImage img;
	public int xVelocity;
	public static final int SPEED = 5; // movement speed

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
		if (play) {
			if (e.getKeyChar() == 'd') {
				if (Player.isCentered) {
					setXDirection(-SPEED);
					move();
				}
			} else if (e.getKeyChar() == 'a') {
				if (Player.isCentered) {
					setXDirection(SPEED);
					move();
				}
			}
			
			if(!Player.isCentered) setXDirection(0);
			
		} else {

			if (e.getKeyChar() == 'd') {

				setXDirection(-SPEED);
				move();

			} else if (e.getKeyChar() == 'a') {

				setXDirection(SPEED);
				move();

			}
		}

	}

	public void keyReleased(KeyEvent e, boolean play) {
		if(e.getKeyChar() == 'a' || e.getKeyChar() == 'd') {
			if(play) {
				
					setXDirection(0);
				
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
		x = x + xVelocity;
	}

	public void draw(Graphics2D g) {
		g.drawImage(img, x, y, width, height, null);
	}

}