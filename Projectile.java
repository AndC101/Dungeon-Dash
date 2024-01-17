/*
* Ethan Lin & Andrew Chen
* January 11, 2023
* Projectile is a bullet emerging from the turret
*/

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.HashSet;

public class Projectile extends Rectangle {

	public static int height = 50;
	public static int width = 50;
	public boolean r, l = false;
	public Image runL;
	public Image runR;
	public int shootDist = 400;
	public int xBorder;
	public int SPEED = 2;
	public boolean isEnemy;
	public int xVelocity;
	public HashSet<Character> keysPressed = new HashSet<Character>();

	public Projectile(int x, int y, int l, int w, Image left, Image right, boolean enemy) throws IOException {
		super(x, y, l, w);
		runL = left;
		runR = right;
		if (enemy) {
			xBorder = x;
		}
		isEnemy = enemy;
	}

	public void keyPressed(KeyEvent e) {
		keysPressed.add(e.getKeyChar());
	}

	public void keyReleased(KeyEvent e) {
		// check for errors
		keysPressed.remove(e.getKeyChar());
		// System.out.println(keysPressed + " " + keysPressed.size());

		if(e.getKeyChar() == 'a' || e.getKeyChar() == 'd') {

			if(e.getKeyChar() == 'a') {
				if(l) {
					setXDirection(-SPEED);
				} else {
					setXDirection(SPEED);
				}
			}
			else if(e.getKeyChar() == 'd') {
				if(l) {
					setXDirection(-SPEED);
				} else {
					setXDirection(SPEED);
				}
			}

		}
	}

	

	// draw the image from the block class
	public void draw(Graphics2D g) {
		if(isEnemy) {
			g.setColor(Color.red);
			g.fillRect(xBorder-GamePanel.shift, 40, 10, 1000); //debugging
			if (r) {
				g.drawImage(runR, x, y, null);
			} else if (l) {
				g.drawImage(runL, x-10, y, null);
			} else {
				g.drawImage(runR, x, y, null);
			}
		} else {

			g.drawImage(runR, x, y, null);
		}
	}

	public void setXDirection(int xDirection) {
		xVelocity = xDirection;
	}

	public void move() {
		if (keysPressed.contains('d')) {
			if (Player.isCentered && Player.isRight) {

				if (r) {
					setXDirection(0); // good
				} else if (l) {
					setXDirection(-4); // good

				}
				// xBorder+=-5;
			}
		} else if (keysPressed.contains('a')) {
			if (Player.isCentered && Player.isLeft) {

				if (r) {
					setXDirection(4); // good

				} else if (l) {
					setXDirection(0);
				}

				// xBorder+=5;
			}
		}

		if (!Player.isCentered) {
			if (l) {
				setXDirection(-SPEED);

			} else if (r) {
				setXDirection(SPEED);
			}
		}

		// System.out.println(x);

		x += xVelocity;

		if (isEnemy) {
			if (x <= xBorder - GamePanel.shift) {
				x = xBorder - GamePanel.shift;
				r = true;
				l = false;
				setXDirection(SPEED);
			} else if (x >= xBorder - GamePanel.shift + shootDist) {

				x = xBorder - GamePanel.shift;
				r = true;
				l = false;
				setXDirection(SPEED);
			}
		}
	}

	public String toString() {
		return "Projectile " + super.toString();
	}
}
