/*
Pong game, developed by Bala Venkataraman for ICS4U.
PlayerBall class: used for the ball.
handles ball physics, power ups, scoring detection, and rendering.
*/
import java.awt.*;
import java.awt.event.*;

public class PlayerBall extends Rectangle{
  public static final double FR_MULTIPLIER = (double) 144/60;

  public double yVelocity; // stores velocity
  public double xVelocity;
  public int ballSpeed = 10; //movement speed of ball/
  public static final int BALL_DIAMETER = 20; //size of ball

  public double realX; // stores position, as float for accuracy. casted to integer when drawing.
  public double realY;

  public boolean canPerformSpeedBoost = false;
  public boolean currentlySpeedBoosting = false;


  public static double randRange(double min, double max){
    return(min + Math.random() * (max - min));
  }

  //constructor creates ball at given location with given dimensions
  public PlayerBall(int x, int y){
    super(x, y, BALL_DIAMETER, BALL_DIAMETER);
    this.realX = x;
    this.realY = y;
  }
  // Changes position and velocity. normalizes it. called when ball hits one of the sides.
  public void changePosVel(double newx, double newy, double newXV, double newYV){
    this.realX = newx;
    this.realY = newy;
    double vectModulus = Math.sqrt(newXV * newXV + newYV * newYV);
    this.xVelocity = this.ballSpeed * newXV / vectModulus; // normalizes it to keep the speed at the maximum.
    this.yVelocity = this.ballSpeed * newYV / vectModulus;
    // this is only called at the end of a turn, so this code segment resets the speed boost status.
    if(this.currentlySpeedBoosting){
      this.currentlySpeedBoosting = false;
      this.canPerformSpeedBoost = false;
    }
    else{
      this.canPerformSpeedBoost = true;
    }


  }
  // Collision method. Detects a collision and reacts. If the collision occurs with the top or bottom wall, and bounces according to physics.
  // detects if the collision occurs with the side wall
  // when the paddle strikes the ball, the vertical velocity of the paddle is transferred to the ball. Horizontal velocity is inverted. It will then be normalized so that the magnitude is always the maximum speed.
  public int checkCollision(int p1py,int p2py, int p1pv, int p2pv,int p1ps, int p2ps){ // takes in the position size and velocity of both the paddles size is needed as paddle size increase is a power up.
    // if it is on left side
    if(this.realX < 20){
      // check if it collides with val
      if(this.realY < p1py+p1ps && this.realY > p1py-p1ps){
        double nextYVelocity = p1pv + this.yVelocity;
        double nextXVelocity = -this.xVelocity; // avoids "edging" where the ball bounces on the inside edge of the paddle.
        changePosVel(20.1, this.realY, nextXVelocity, nextYVelocity); // re-normalize velocity and change position
        return 3; // hit the paddle
      }
      else{
        this.changePosVel(GamePanel.GAME_WIDTH/2, randRange(100, GamePanel.GAME_HEIGHT - 100), -randRange(1,2), randRange(-2,2)); // respawn the ball at a random place in the middle to fly towards the loser.
        return 2; // player 2 scored
      }
    }
    if(this.realX > GamePanel.GAME_WIDTH - (20 + BALL_DIAMETER)){ // same thing but for other side.
      // check if it collides with val
      if(this.realY < p2py+p2ps && this.realY > p2py-p2ps){
        double nextYVelocity = p2pv + this.yVelocity;
        double nextXVelocity = - this.xVelocity;
        changePosVel(GamePanel.GAME_WIDTH-(20.1 + BALL_DIAMETER), this.realY, nextXVelocity, nextYVelocity);
        return 3; // hit the paddle
      }
      else{
        this.changePosVel(GamePanel.GAME_WIDTH/2, randRange(100, GamePanel.GAME_HEIGHT - 100), randRange(1,2), randRange(-2,2));
        return 1; // player 1 scored
      }
    }
    if(this.realY > GamePanel.GAME_HEIGHT - BALL_DIAMETER){ // detect collission with top or bottom wall
      this.realY = GamePanel.GAME_HEIGHT - (0.1 + BALL_DIAMETER);
      this.yVelocity = -this.yVelocity; // bounce off realistically
    }
    if(this.realY < 0){
      this.realY = 0.1;
      this.yVelocity = -this.yVelocity;
    }
    return 0; // hit the ceiling
  }
  // detect if space key is pressed to engage power up if available.
  public void keyPressed(KeyEvent e){
    if(e.getKeyCode() == 32 && this.canPerformSpeedBoost && !this.currentlySpeedBoosting){
      this.currentlySpeedBoosting = true;
      this.xVelocity *= 2;
      this.yVelocity *= 2;
    }
  }


  //updates the current location of the ball
  public void move(){
    realY = realY + (yVelocity) / FR_MULTIPLIER;
    realX = realX + (xVelocity) / FR_MULTIPLIER;
    y = (int) (realY + 0.5);
    x = (int) (realX + 0.5);
  }

  //called frequently from the GamePanel class
  //draws the current location of the ball to the screen
  public void draw(Graphics g){
    g.setColor(Color.black);
    g.fillOval(x, y, BALL_DIAMETER, BALL_DIAMETER);
  }
  
}