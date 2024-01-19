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


	//variables
	public static int height = 50;
	public static int width = 50;
	boolean r, l = false;
	Image runL;
	Image runR;
	int runDist = 400;
	public int xBorder;
	public int SPEED = 1;
	public boolean isEnemy; //makes goblin move
	public int xVelocity;
	public HashSet<Character> keysPressed = new HashSet<Character>();
	public boolean play = false;
	
	public GamePanel gamePanel;
	
	public Block under;


	public Goblin(GamePanel gp, int x, int y, int l, int w, Image left, Image right, boolean enemy) throws IOException{
		super(x,y,l,w);
		runL = left;
		runR = right;
		
		//if enemy, the goblin starts moving and x border init
		if(enemy) {
			xBorder = x;
			System.out.println("new gob: " + xBorder);
		}
		isEnemy = enemy;
		
	}


	
	public void keyPressed(KeyEvent e) {
		keysPressed.add(e.getKeyChar()); //add to hashset

	}

	public void keyReleased(KeyEvent e) {
		//check for errors
		keysPressed.remove(e.getKeyChar()); 

		//if enemy, start movement
		if(isEnemy) {
			if(e.getKeyChar() == 'a' || e.getKeyChar() == 'd') {

				//set speed to appropriate levels when key released
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

	}

	
	//draw the image from the block class
	public void draw(Graphics2D g) {

		//update the sprite
		if(isEnemy) {
			// System.out.println(GamePanel.adjust);
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

	//set the speed
	public void setXDirection(int xDirection) {
		xVelocity = xDirection;
	}

	public void move() {
		if (keysPressed.contains('d')) {
			if (Player.isCentered && Player.isRight) {
				//updates the relative speed to blocks to make it appear like its moving
				if(r) {
					setXDirection(-Block.SPEED+SPEED); //good
				} else if (l) {
					setXDirection(-Block.SPEED-SPEED); //good
				}
				// xBorder-=Block.SPEED;
			}
			if(Player.isCentered) {
				xBorder-=Block.SPEED;
			}

			//in edit the speed is adjusted like the blocks
			if(GamePanel.edit){
				x-=Block.SPEED;
			}

			//same as above but for left movement
		} else if (keysPressed.contains('a')) {
			if (Player.isCentered && Player.isLeft) {

				if(r) {
					setXDirection(Block.SPEED+SPEED); //good
				} else if (l) {
					setXDirection(Block.SPEED-SPEED);
				}

				// xBorder+=Block.SPEED;
			}
			if(Player.isCentered || GamePanel.edit) {
				xBorder+=Block.SPEED;
			}
			if(GamePanel.edit){
				System.out.println("hi");
				x+=Block.SPEED;
			}


		}
		

		//if knight not centered
		if(!Player.isCentered) {

			if(l) {
				setXDirection(-SPEED);
			} else if (r) {
				setXDirection(SPEED);
			}
		} 

		x += xVelocity;
		//make goblin move left to right and back again
		if(isEnemy) {
			// System.out.println("new xborder " + (xBorder-GamePanel.shift));
			if(x <= xBorder-GamePanel.shift-GamePanel.adjust) {
				x = xBorder-GamePanel.shift-GamePanel.adjust;
				r = true;

				l = false;
				setXDirection(SPEED);
			} else if (x >= xBorder-GamePanel.shift-GamePanel.adjust+runDist) {
				x=xBorder-GamePanel.shift-GamePanel.adjust+runDist;
				l = true;
				r = false;
				setXDirection(-SPEED);
			} 	
		}
	}
	
	public boolean canFall() {
		int botRightX = x + width;
		int botRightY = y + height;
		for (Block b : gamePanel.elements) {
			if (GamePanel.walkThrough.contains(gamePanel.getClass(b))) continue;
			
			if ((((x >= b.x && x <= b.x + b.width) || (botRightX >= b.x && botRightX <= b.x + b.width)
					|| (x >= b.x && botRightX <= b.x + b.width)) && botRightY >= b.y - 1 && y <= b.y)) {
				under = b;
				return false;
			}
		}

		if (botRightY >= GamePanel.FLOOR) {
			y = GamePanel.FLOOR - height;
			under = null;
			return false;
		}

		under = null;
		return true;

	}

	
	public String toString () {
		return "Goblin " + super.toString();
	}
}