/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Goblin is a moveable enemy 
 */


import java.awt.*;
import java.io.*;


public class Goblin extends Block {
	

	public static int height = 50;
	public static int width = 50;
	boolean r, l = false;
	Image runL;
	Image runR;
	int runDist = 100;
	int initX;
	int SPEED = 1;
	public boolean isEnemy;

	public Goblin(int x, int y, int l, int w, Image left, Image right, boolean enemy) throws IOException{
		super(x,y,l,w);
		runL = left;
		runR = right;
		if(enemy) {
			initX = x;
			System.out.println("initx: "+  initX);
		}
		isEnemy = enemy;
	}
	
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		if(isEnemy) {
			g.setColor(Color.white);
			g.fillRect(initX-GamePanel.shift, 40, 10, 1000);

			if(r) {
				g.drawImage(runR, x, y, null);
			} else if (l) {
				g.drawImage(runL, x, y, null);
			} else {
				g.drawImage(runL, x, y, null);
			}
		} else {
			g.drawImage(runL, x, y, null);
		}
	}

	public void setXDirection(int xDirection) {
		xVelocity = xDirection;
	}

		
	public void move() {
		
		// x += xVelocity;
		if(isEnemy) {
			if(x <= initX-GamePanel.shift) {
				x = initX-GamePanel.shift;
				r = true;
				l = false;
				setXDirection(SPEED);
			} else if (x >= initX-GamePanel.shift+runDist) {
				x=initX-GamePanel.shift+runDist;
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
 