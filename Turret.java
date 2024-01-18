/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Turret shoots projectiles across the screen
 */

 import java.awt.*;
 import java.awt.event.KeyEvent;
 import java.awt.image.*;
 import java.io.*;
 import java.util.Timer;
 import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
 
 
 public class Turret extends Block {
	 
	
	 public static int width = 60;
	 public static int height = 60;


	 public boolean l, r = false;
	 public boolean isEnemy = false;
	 public int fireDist = 100;
	 Image fireballLeft = new ImageIcon("Images/fireballLeft.gif").getImage();
	 Image fireballRight = new ImageIcon("Images/fireballRight.gif").getImage();
	public BufferedImage turretRight = ImageIO.read(new File("Images/turret.png"));
	public BufferedImage turretLeft = ImageIO.read(new File("Images/turLeft.png"));

	 Projectile ball;
	 Projectile ballTwo; 
	 public int flipNum;
 
	 public Turret(int x, int y, int len, int w, BufferedImage i, boolean left, boolean right, boolean enemy) throws IOException{
		 super(x,y,len,w,i); //block constructor
		 l = left;
		 r = right;
		 isEnemy = enemy;
		 if(r) {
			try {
				ball = new Projectile(x, y-10, 30, 30, fireballLeft, fireballRight, l, r, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
   
		 } else if (l) {
			try {
				ball = new Projectile(x, y-10, 30, 30, fireballLeft, fireballRight, l, r, true);
			} catch (IOException e) {
				e.printStackTrace();
			}

		 }

		 if(isEnemy) {
			createNewFireball();
		 }


	 }
	 
 
 
 
	 //draw the image from the block class
	 public void draw(Graphics2D g) {
 
		 super.draw(g);
		 if(isEnemy){
			if(ballTwo != null) {
				ballTwo.draw(g, y);
			}
			ball.draw(g, y);
   
		 }
	 }
	 public void keyReleased(KeyEvent e) {
		 super.keyReleased(e);
		 if(isEnemy) {
			ball.keyReleased(e);
			if(ballTwo != null) {
				ballTwo.keyReleased(e);
			}
		 }
		 
 
	 }
 
 
	 public void keyPressed(KeyEvent e) {
		 super.keyPressed(e);
		 if(isEnemy){
			ball.keyPressed(e);
			if(ballTwo != null) {
				ballTwo.keyPressed(e);
			}
   
		 }
 
	 }
 
	 public void move() {
		if(isEnemy) {
			if(r) {
				ball.xBorder = x+GamePanel.shift+width;
			} else {
				ball.xBorder = x+GamePanel.shift;
			}
			ball.move();
			if(ballTwo != null) {
				if(r) {
					ballTwo.xBorder = x+GamePanel.shift+width;
				} else {
					ballTwo.xBorder = x+GamePanel.shift;
				}
		
				ballTwo.move();
			}
		}
		 super.move();
 
	 }
 
 
	 //returns name and constructor info
	 public String toString () {
		 return "Turret " + super.toString() + " " + l;
	 }
 
 
 
	 public void createNewFireball() {
		 TimerTask task = new TimerTask() {
			 public void run() {
				 if(r) {
					try {
						ballTwo = new Projectile(x, y-10, 30, 30, fireballLeft, fireballRight, l, r, true);
					} catch (IOException e) {
						e.printStackTrace();
					}   
				 } else {
					try {
						ballTwo = new Projectile(x, y-10, 30, 30, fireballLeft, fireballRight, l, r, true);
					} catch (IOException e) {
						e.printStackTrace();
					}   

				 }
			 }
		 };
		 Timer timer = new Timer("Timer");
		 
		 long delay = 1000L;
		 timer.schedule(task, delay);
	 }
 
 }