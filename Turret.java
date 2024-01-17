
/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * Turret shoots projectiles across the screen
 */

 import java.awt.*;
 import java.awt.event.KeyEvent;
 import java.awt.image.*;
 import java.io.*;
 import java.util.Date;
 import java.util.Timer;
 import java.util.TimerTask;
 
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
	 Projectile ballTwo; 
	 boolean spawned = false;
 
	 public Turret(int x, int y, int len, int w, BufferedImage i, boolean left, boolean right, boolean enemy) throws IOException{
		 super(x,y,len,w,i); //block constructor
		 l = left;
		 r = right;
		 isEnemy = enemy;
		 try {
			 ball = new Projectile(x, y-10, 30, 30, fireballLeft, fireballRight, true);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 createNewFireball();
 
	 }
	 
 
 
 
	 //draw the image from the block class
	 public void draw(Graphics2D g) {
 
		 super.draw(g);
		 if(ballTwo != null) {
			 ballTwo.draw(g);
		 }
		 ball.draw(g);
			 // System.out.println(ball.xVelocity);
	 }
	 public void keyReleased(KeyEvent e) {
		 super.keyReleased(e);
		 ball.keyReleased(e);
		 if(ballTwo != null) {
			 ballTwo.keyReleased(e);
		 }
 
	 }
 
 
	 public void keyPressed(KeyEvent e) {
		 ball.keyPressed(e);
		 super.keyPressed(e);
		 if(ballTwo != null) {
			 ballTwo.keyPressed(e);
		 }
 
	 }
 
	 public void move() {
		 if(r) {
			 ball.xBorder = x+GamePanel.shift+width;
		 } else {
			 ball.xBorder = x+GamePanel.shift;
		 }
		 super.move();
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
 
 
	 //returns name and constructor info
	 public String toString () {
		 return "Turret " + super.toString();
	 }
 
 
 
	 public void createNewFireball() {
		 TimerTask task = new TimerTask() {
			 public void run() {
				 System.out.println("Second fireball generated on: " + new Date() + "n" +
				 "Thread's name: " + Thread.currentThread().getName());
				 try {
					 ballTwo = new Projectile(x, y-10, 30, 30, fireballLeft, fireballRight, true);
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 };
		 Timer timer = new Timer("Timer");
		 
		 long delay = 1000L;
		 timer.schedule(task, delay);
	 }
 
 }
 
 