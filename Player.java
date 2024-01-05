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
    public int jumpHeight = 100; // Adjust this value based on your needs
    public int jumpCount = 0;
    public int jumpLimit = 100; // Adjust this value based on your needs

    public int yVelocity;
    public int xVelocity;
    public final int SPEED = 5; // movement speed
	public final int JUMP_SPEED = 6;
	public double initY = 0;
    public static boolean isCentered = false;
    public boolean done = false;

    // create the player at x, y coordinates on the screen with length, width
    public Player(int x, int y, int l, int w) throws IOException {
        super(x, y, l, w);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'd') {
            isRight = true;
            
            if(!isCentered){
                setXDirection(SPEED);
            }
            move();

        } else if (e.getKeyChar() == 'a') {
            isLeft = true;
            if(!isCentered){
                setXDirection(SPEED*-1);
                
            }
            move();
        } else if (e.getKeyChar() == 'w' && !isJumping) {
            // Only allow jumping if not already jumping
            jump();
        } else if (e.getKeyChar() == 's') {
            setYDirection(SPEED);
            move();
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'd') {
            isRight = false;
            if(!isCentered){
                setXDirection(0);
                
            }
            move();
        } else if (e.getKeyChar() == 'a') {
            isLeft = false;
            if(!isCentered){
                setXDirection(0);
                
            }
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
        if(getCenterX() >= 400) {
            isCentered = true;
            if(!done) {
                xVelocity = 0;
            } 
            done = true;
        }

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
