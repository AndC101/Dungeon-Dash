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

	GamePanel gamePanel;

	Projectile ball; // projectiles that come out of turret
	Projectile ballTwo;

	public Turret(GamePanel gp, int x, int y, int len, int w, BufferedImage i, boolean left, boolean right,
			boolean enemy) throws IOException {
		super(x, y, len, w, i); // block constructor
		l = left;
		r = right;
		isEnemy = enemy;
		gamePanel = gp;

		// create first projectile
		try {
			ball = new Projectile(x, y - 10, Projectile.height, Projectile.width, fireballLeft, fireballRight, l, r, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create the second projectile 1 second after using threads
		if (isEnemy) {
			createNewFireball();
		}

	}

	// draw the image from the block class
	public void draw(Graphics2D g) {

		super.draw(g);
		// draw the projectiles
		if (isEnemy) {
			if (ballTwo != null) {
				ballTwo.draw(g, y);
			}
			if(ball != null)ball.draw(g, y);

		}
	}

	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		// logic for projectile keyreleased
		if (isEnemy) {
			ball.keyReleased(e);
			if (ballTwo != null) {
				ballTwo.keyReleased(e);
			}
		}

	}

	// keypressed logic
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if (isEnemy) {
			ball.keyPressed(e);
			if (ballTwo != null) {
				ballTwo.keyPressed(e);
			}

		}

	}

	public void move() {

		// move at relative speed to appear as if moving
		if (isEnemy) {
			if (ball != null) {
				if (r) {
					ball.xBorder = x + GamePanel.shift + width;
				} else {
					ball.xBorder = x + GamePanel.shift;
				}
				ball.move();
			}
			if (ballTwo != null) {
				if (r) {
					ballTwo.xBorder = x + GamePanel.shift + width;
				} else {
					ballTwo.xBorder = x + GamePanel.shift;
				}

				ballTwo.move();
			}
		}
		super.move();


	}

	// returns name and constructor info
	public String toString() {
		return "Turret " + super.toString() + " " + l;
	}

	// thread to create a new fireball 1 second after
	public void createNewFireball() {
		TimerTask task = new TimerTask() {
			public void run() {
				try {
					ballTwo = new Projectile(x, y - 10, Projectile.height, Projectile.width, fireballLeft, fireballRight, l, r, true);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};
		Timer timer = new Timer("Timer");

		long delay = 1300L;
		timer.schedule(task, delay);
	}

}