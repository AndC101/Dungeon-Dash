/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Goblin is a moveable enemy 
 */


import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.HashSet;


public class Goblin extends Block {
	

	public static int height = 50;
	public static int width = 50;
	boolean r, l = false;
	Image runL;
	Image runR;
	int runDist = 400;
	int xBorder;
	public int SPEED = 1;
	public boolean isEnemy;
	public int xVelocity;
	public HashSet<Character> keysPressed = new HashSet<Character>();
	// int x, y, l, w;


	public Goblin(int x, int y, int l, int w, Image left, Image right, boolean enemy) throws IOException{
		super(x,y,l,w);
		runL = left;
		runR = right;
		if(enemy) {
			xBorder = x;
		}
		isEnemy = enemy;
	}
	
	public void keyPressed(KeyEvent e) {
		keysPressed.add(e.getKeyChar());

	}

	public void keyReleased(KeyEvent e, boolean play) {

		//check for errors
		keysPressed.remove(e.getKeyChar());
		if(e.getKeyChar() == 'a' || e.getKeyChar() == 'd') {
			if(play) {
				if(e.getKeyChar() == 'a') {
					if(l) {
						setXDirection(-1);
					} else {
						setXDirection(1);
					}
				}
				else if(e.getKeyChar() == 'd') {
					if(l) {
						setXDirection(-1);
					} else {
						setXDirection(1);
					}
				}
			}
		}
		
	}

	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		if(isEnemy) {
			// System.out.println("working");
			g.setColor(Color.white);
			g.fillRect(xBorder-GamePanel.shift, 40, 10, 1000); //debugging

			if(r) {
				g.drawImage(runR, x, y, null);
			} else if (l) {
				g.drawImage(runL, x, y, null);
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

				if(r) {
					setXDirection(-4); //good
				} else if (l) {
					setXDirection(-6); //good
				}
				xBorder+=-5;
			}
		} else if (keysPressed.contains('a')) {
			if (Player.isCentered && Player.isLeft) {

				if(r) {
					setXDirection(6); //good
				} else if (l) {
					setXDirection(4);
				}

				xBorder+=5;
			}
		}
		
		if(!Player.isCentered) {
			if(l) {
				setXDirection(-1);
			} else if (r) {
				setXDirection(1);
			}
		} 

		// System.out.println(x);

		x += xVelocity;

		if(isEnemy) {
			if(x <= xBorder-GamePanel.shift) {
				x = xBorder-GamePanel.shift;
				r = true;
				l = false;
				setXDirection(SPEED);
			} else if (x >= xBorder-GamePanel.shift+runDist) {
				x=xBorder-GamePanel.shift+runDist;
				l = true;
				r = false;
				setXDirection(-SPEED);
			} 	
		}
	}

	
	public String toString () {
		return "Goblin " + super.toString();
	}
}

 