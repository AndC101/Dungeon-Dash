import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.ImageIcon;

public class Player extends Rectangle {

    private Image afkAnimation = new ImageIcon("Images/KnightAfk.gif").getImage();
    private Image leftAnimation = new ImageIcon("Images/KnightRunLeft.gif").getImage();
    private Image rightAnimation = new ImageIcon("Images/KnightRunRight.gif").getImage();

    public boolean isLeft = false;
    public boolean isRight = false;
    public boolean isJumping = false;
	public boolean falling = false;
	public boolean left = false;
    public int jumpHeight = 100; // Adjust this value based on your needs
    public int jumpCount = 0;
    public int jumpLimit = 100; // Adjust this value based on your needs
    public int lBorder = 0;
    public int rBorder = 0;
    public int yVelocity;
    public int xVelocity;
    public int leftX = 0;
    public int moved = 0;
    public final int SPEED = 5; // movement speed
	public final int JUMP_SPEED = 6;
	public double initY = 0;
    public static boolean isCentered = true;

    // create the player at x, y coordinates on the screen with length, width
    public Player(int x, int y, int l, int w) throws IOException {
        super(x, y, l, w);
    }
    
    public void keyPressed(KeyEvent e) {
    	
    	if(isCentered) {
    		setXDirection(0);
    	}

        if (e.getKeyChar() == 'd') {
            isRight = true;
            if(!isCentered){
                setXDirection(SPEED);
            }
            move();

        } else if (e.getKeyChar() == 'a') {
            isLeft = true;
            isRight = false;
            if(!isCentered){
                setXDirection(SPEED*-1);
            }
            move();
        } else if (e.getKeyChar() == 'w' && !isJumping) {
            // Only allow jumping if not already jumping
            jump();
        } 
    }
    

    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'd') {
            isRight = false;
            
            setXDirection(0);
            move();
        } else if (e.getKeyChar() == 'a') {
            isLeft = false;
            setXDirection(0);
            move();
        } else if (e.getKeyChar() == 'w') {
            // You can add additional logic for releasing 'w' key if needed
        } else if (e.getKeyChar() == 's') {
            setYDirection(0);
            move();
        }
    }

    public void setXDirection(int xDirection) {
        xVelocity = xDirection;
    }

    
    public void setYDirection(int yDirection) {
        yVelocity = yDirection;
    }


    public void jump() {
		falling = false;
		initY = getY();
        isJumping = true;
        jumpCount = 0;
    }

    public void move() {
        if (isJumping) {
            if (jumpCount < jumpLimit) {
                // If jumping, move up until reaching jumpHeight

				//the if statement below slowly decrease the speed to simulate real life physics
                if (getY() - (initY - jumpHeight) > jumpHeight/2  && !falling) {
                    yVelocity = -JUMP_SPEED;
                } else if (getY() - Math.abs(initY - jumpHeight) > jumpHeight/3  && !falling) {
                    yVelocity = -6;
				} else if (getY() > initY-jumpHeight && ! falling) {
					yVelocity = -4;
				}
				//if reached the peak of jump, start falling
				else {
					falling = true;
                    jumpCount++;
                    yVelocity = JUMP_SPEED; // Start falling
					if(getY() >= initY) {
						y = (int)initY;
						yVelocity = 0;
						isJumping = false;
						falling = false;
					}
                }
            } else {
                isJumping = false;
                yVelocity = 0;
            }
        }
        x = x + xVelocity;
        y = y + yVelocity;
    }

    public void draw(Graphics g) {

        if( (left && isRight && x >= 420) || (!left && isLeft && x <= 420)) {
            isCentered = true;
            x = 420; //force the knight centered 
        }

        System.out.println("jump: " + isJumping + " center: " + isCentered + " left: " + isLeft + " right: " + isRight);

        
        
        g.setColor(Color.white);
        if (isLeft) {
            g.drawImage(leftAnimation, x, y, null);
        } else if (isRight) {
            g.drawImage(rightAnimation, x, y, null);
        } else {
            g.drawImage(afkAnimation, x, y, null);
        }
    }

}
