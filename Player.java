import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.ImageIcon;

public class Player extends Rectangle {

    private Image afkAnimation = new ImageIcon("Images/KnightAfk.gif").getImage();
    private Image leftAnimation = new ImageIcon("Images/KnightRunLeft.gif").getImage();
    private Image rightAnimation = new ImageIcon("Images/KnightRunRight.gif").getImage();

	public boolean isLeft = false;
	public boolean isRight = false;
	public boolean isJumping = false;

	public int yVelocity;
	public int xVelocity;
	public final int SPEED = 2; //movement speed 
	public final int JUMP_SPEED = 2;
    public final int MAX_JUMP_HEIGHT = 100; // max jump height
	public int jumpCount = 0;
	//create the player at x, y coordinates on the screen with length, width
    public Player(int x, int y, int l, int w) throws IOException {
        super(x, y, l, w);
    }

	public void keyPressed(KeyEvent e) {
		if(e.getKeyChar() == 'd'){
			setXDirection(SPEED);
			isRight = true;
			move();
		}
	  
		if(e.getKeyChar() == 'a'){
			setXDirection(SPEED*-1);
			isLeft = true;
			move();
		}
	  
		if(e.getKeyChar() == 'w'){
			if (!isJumping) {
				setYDirection(-JUMP_SPEED);
				isJumping = true;
			}
	  	}
	  
		if(e.getKeyChar() == 's'){
			setYDirection(SPEED);
			move();
		}
	}

	public void keyReleased (KeyEvent e) {
		if(e.getKeyChar() == 'd'){
			setXDirection(0);
			isRight = false;
			move();
		  }
	  
		  if(e.getKeyChar() == 'a'){
			setXDirection(0);
			isLeft = false;
			move();
		  }
	  
		  if(e.getKeyChar() == 'w'){
			// setYDirection(0);
			// move();
		  }
	  
		  if(e.getKeyChar() == 's'){
			setYDirection(0);
			move();
		  }	  
	}

	public void setYDirection(int yDirection){
		yVelocity = yDirection;
	}
	
	public void setXDirection(int xDirection) {
		xVelocity = xDirection;
	}

	public void move(){
		y = y + yVelocity;
		x = x + xVelocity;

        if (isJumping) {
            jumpCount += 2;

            if (jumpCount > MAX_JUMP_HEIGHT) {
                isJumping = false;
                jumpCount = 0;
                setYDirection(JUMP_SPEED); // Stop moving upward
            }
		}

	}

	public void draw(Graphics g){
		if(isLeft) {
			g.drawImage(leftAnimation, x, y, null);
		} else if (isRight) {
			g.drawImage(rightAnimation, x, y, null);
		} else if (isJumping) {
			g.drawImage(afkAnimation, x, y, null);
		} else {
			g.drawImage(afkAnimation, x, y, null);
		}

	}
	

}

