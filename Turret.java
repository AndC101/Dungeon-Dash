
/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Turret shoots projectiles across the screen
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.*;

import javax.swing.ImageIcon;


public class Turret extends Block {
	
	public static int width = 60;
	public static int height = 60;
	public BufferedImage i; //img for turret
	public boolean l, r = false;
	public boolean isEnemy = false;
	public int fireDist = 100;
	Image fireballLeft = new ImageIcon("Images/fireballLeft.gif").getImage();
	Image fireballRight = new ImageIcon("Images/fireballRight.gif").getImage();
	Projectile ball;

	public Turret(int x, int y, int len, int w, BufferedImage i, boolean left, boolean right, boolean enemy) throws IOException{
		super(x,y,len,w,i); //block constructor
		l = left;
		r = right;
		isEnemy = enemy;
		try {
			ball = new Projectile(x, y+5, 30, 30, fireballLeft, fireballRight, true);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//draw the image from the block class
	public void draw(Graphics2D g) {
		super.draw(g);
			try {
				ball = new Projectile(x, y-10, 30, 30, fireballLeft, fireballRight, true);

			} catch (IOException e) {
				e.printStackTrace();
			}
			ball.draw(g);


	}
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e, true);
		ball.keyReleased(e, true);
	}


	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		ball.keyPressed(e);
	}

	public void move() {
		super.move();
		ball.move();
	}


	//returns name and constructor info
	public String toString () {
		return "Turret " + super.toString();
	}

}
