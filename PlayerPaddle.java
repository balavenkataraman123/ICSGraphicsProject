/*
Pong game, developed by Bala Venkataraman for ICS4U.
Player paddle class: used for each paddle.
handles player controls, power ups, and rendering.
*/
import java.awt.*;
import java.awt.event.*;

public class PlayerPaddle extends Rectangle{
    public int yPosition;
    public int xPosition;
    public static final int DEFAULT_SIZE = 40;
    public static final int EXPANDED_SIZE = 80;

    public static final int DEFAULT_SPEED = 8;

    public int currentSize = DEFAULT_SIZE;



    public int yMaxSpeed = DEFAULT_SPEED;
    public int yCurrentSpeed = 0;

    public char upKey;
    public char downKey;
    public char boostKey;
    public char sizeupKey;

    public boolean canPowerUp = true;
    public int boosts = 3;
    public int sizeups = 3;


    //constructor creates paddle at given location with given dimensions
    public PlayerPaddle(int x, int y, char i_upKey, char i_downKey, char i_boostKey, char i_sizeupkey){
        super(x, y);
        this.yPosition = y;
        this.xPosition = x;
        this.upKey = i_upKey;
        this.downKey = i_downKey;
        this.sizeupKey = i_sizeupkey;
        this.boostKey = i_boostKey;
    }
    public void keyPressed(KeyEvent e){
        char k = e.getKeyChar();
        if(k == this.upKey){
            this.yCurrentSpeed = -this.yMaxSpeed;
        }
        else if(k == this.downKey){
            this.yCurrentSpeed = this.yMaxSpeed;
        }
        if(this.canPowerUp && k == this.sizeupKey && this.sizeups > 0){
            this.currentSize = EXPANDED_SIZE;
            this.canPowerUp = false;
            this.sizeups -= 1;
        }
        if(this.canPowerUp && k == this.boostKey && this.boosts > 0){
            this.yMaxSpeed = DEFAULT_SPEED * 3;
            this.canPowerUp = false;
            this.boosts -= 1;
        }
    }
    public void resetPowerUps() { // resets power ups when ball hits paddle or when scored.
        this.canPowerUp = true;
        this.yMaxSpeed = DEFAULT_SPEED;
        this.currentSize = DEFAULT_SIZE;
    }

    //called from GamePanel when any key is released (no longer being pressed down)
    //Makes the ball stop moving in that direction
    public void keyReleased(KeyEvent e){
        if(e.getKeyChar() == this.upKey ){
            this.yCurrentSpeed = 0;
        }

        if (e.getKeyChar() == this.downKey){
            this.yCurrentSpeed = 0;
        }
    }
    public void move(){
        this.yPosition = (int) Math.min(GamePanel.GAME_HEIGHT - this.currentSize, Math.max(0, yPosition + yCurrentSpeed));
    }

    //called frequently from the GamePanel class
    //draws the current location of the ball to the screen
    public void draw(Graphics g){
        g.setColor(Color.black);
        g.fillRect(this.xPosition, this.yPosition, 15, this.currentSize);
    }


}