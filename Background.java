import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class Background {
	
	boolean drag = false;
	BufferedImage img;
    public int xVelocity;
    public final int SPEED = 1; // movement speed
    public int x = 0;
    public int y = 0;

    private int lastDrawnX = 0;

    public Background (int x, int y, BufferedImage i) {
        this.x = x;
        this.y = y;
        img = i;
    }

	public void keyPressed(KeyEvent e, boolean play) {
		if (play) {
			
			if(!Player.isCentered) {
				setXDirection(0);
			}
			
			else if (e.getKeyChar() == 'd') {
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
    	if(play) {
			
				setXDirection(0);
			
		}
		else {
			setXDirection(0);
		}
    }

    public void setXDirection(int xDirection) {
        xVelocity = xDirection;
    }


    public void move() {
        x = x + xVelocity;
    }
	//resizes an image to preferred width and height
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }


    public void draw(Graphics g) {
        lastDrawnX = x+GamePanel.GAME_WIDTH;
        // System.out.println(lastDrawnX + "   " + ((- (x-GamePanel.GAME_WIDTH/7)  % 2000) + GamePanel.GAME_WIDTH - GamePanel.GAME_WIDTH/7) );

        for(int i = 0; i < 100; i++) {
            g.drawImage(img, x + i*2000, y, 2000, 550, null);
            g.drawImage(img, x + i*-2000, y, 2000, 550, null);

        }
    }

    public int getCurX() {
        return lastDrawnX;
    }


}