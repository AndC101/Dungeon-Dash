import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class Block extends Rectangle {
	
	int diffX, diffY;
	boolean drag = false;
	BufferedImage img;
    public int xVelocity;
    public final int SPEED = 5; // movement speed

	public Block(int x, int y, int l, int w, BufferedImage i){
		super(x,y,l,w);
		img = i;
	}

	public Block(int x, int y, int l, int w){
		super(x,y,l,w);
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
		x = e.getX() - diffX; y = e.getY() - diffY;
	}

	public String toString() {
		return x + " " + y + " " + width + " " + height;
	}


	public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'd') {
            if(Player.isCentered) {
                setXDirection(-SPEED);
                move();

            }
        } else if (e.getKeyChar() == 'a') {
            if(Player.isCentered) {
                setXDirection(SPEED);
                move();

            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'd') {
            if(Player.isCentered) {
                setXDirection(0);
                move();
            }
        } else if (e.getKeyChar() == 'a') {
            if(Player.isCentered) {
                setXDirection(0);
                move();

            }
        } 
    }

    public void setXDirection(int xDirection) {
        xVelocity = xDirection;
    }


    public void move() {
        x = x + xVelocity;
    }

    public void draw(Graphics g) {
        g.drawImage(img, x, y, width, height, null);
    }


}