import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class Block extends Rectangle {
	
	int diffX, diffY;
	boolean drag = false;
	BufferedImage img;
	
	public Block(int x, int y, int l, int w, BufferedImage i){
		super(x,y,l,w);
		img = i;
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }



	public void keyListener(MouseEvent e) {
		
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

	public void draw(Graphics g) {
		g.fillRect(x, y, width, height);
	}

}
