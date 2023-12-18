/*
Pong game, developed by Bala Venkataraman for ICS4U.

GamePanel class acts as the main "game loop" - continuously runs the game and calls whatever needs to be called

Child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener{

  //dimensions of window
  public static final int GAME_WIDTH = 640;
  public static final int GAME_HEIGHT = 480;

  // game tracking variables
  public boolean playingGame = false; // displays the instructions if not playing game. otherwise plays the game.
  public boolean timedMatch = false; // whether or not to end the game in one minute
  public boolean gameEnded = false; // locks out the key press after game finishes.
  public int matchEndFrame = 0; // the frame to end the game at
  public int currentFrame = 0; // current frame the game is on

  // variables of objects for gameplay
  public Thread gameThread;
  public Image image;
  public Graphics graphics;
  public PlayerBall ball;
  public PlayerPaddle playerOnePaddle;
  public PlayerPaddle playerTwoPaddle;

  // information for user
  public OnScreenDisplay playerOneScoreDisplay;
  public OnScreenDisplay playerTwoScoreDisplay;
  public OnScreenDisplay playerOnePowerUps;
  public OnScreenDisplay playerTwoPowerUps;
  public OnScreenDisplay ballSpeedBoost;

  public OnScreenDisplay instructionsDisplay;

  // scores
  public int playerOneScore = 0;
  public int playerTwoScore = 0;




  public GamePanel(){ // constructor, which defines all the objects which will be used.
    ball = new PlayerBall(GAME_WIDTH/2, GAME_HEIGHT/2); //create a player controlled ball, set start location to middle of screen
    ball.changePosVel(GamePanel.GAME_WIDTH/2, PlayerBall.randRange(100, GamePanel.GAME_HEIGHT - 100), -PlayerBall.randRange(1,3), PlayerBall.randRange(-2,2)); // sets up the game objects.
    playerOnePaddle = new PlayerPaddle(0, GAME_HEIGHT/2, 'w', 's', 'd', 'a');
    playerTwoPaddle = new PlayerPaddle(GAME_WIDTH - 15, GAME_HEIGHT/2, 'i', 'k', 'j', 'l');
    playerOneScoreDisplay = new OnScreenDisplay("0", GAME_WIDTH/2 - 50, 50,50); // defines all on screen displays.
    playerTwoScoreDisplay = new OnScreenDisplay("0", GAME_WIDTH/2 + 50, 50,50);
    ballSpeedBoost = new OnScreenDisplay("PRESS 1 FOR QUICK MATCH. PRESS 2 FOR REGULAR MATCH.", 0, GAME_HEIGHT + 25,20);
    playerOnePowerUps = new OnScreenDisplay("", 20, GAME_HEIGHT + 25,10);
    playerTwoPowerUps = new OnScreenDisplay("", GAME_WIDTH - 120, GAME_HEIGHT + 25,10);
    instructionsDisplay = new OnScreenDisplay("PONG GAME. WASD for player one. IJKL for player two. Check instructions.docx file for more instructions.", 50, 100,10);

    this.setFocusable(true); //make everything in this class appear on the screen
    this.addKeyListener(this); //start listening for keyboard input
    //add the MousePressed method from the MouseAdapter - by doing this we can listen for mouse input. We do this differently from the KeyListener because MouseAdapter has SEVEN mandatory methods - we only need one of them, and we don't want to make 6 empty methods
    this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT + 50)); // set the size to 50 more than game size, so the power ups can be shown below

    //make this class run at the same time as other classes (without this each class would "pause" while another class runs). By using threading we can remove lag, and also allows us to do features like display timers in real time!
    gameThread = new Thread(this);
    gameThread.start();
  }

  //paint is a method in java.awt library that we are overriding. It is a special method - it is called automatically in the background in order to update what appears in the window. You NEVER call paint() yourself
  public void paint(Graphics g){
    //we are using "double buffering here" - if we draw images directly onto the screen, it takes time and the human eye can actually notice flashes of lag as each pixel on the screen is drawn one at a time. Instead, we are going to draw images OFF the screen, then simply move the image on screen as needed. 
    image = createImage(GAME_WIDTH, GAME_HEIGHT + 50); //draw off screen
    graphics = image.getGraphics();
    graphics.setColor(Color.black);
    graphics.fillRect(0, GAME_HEIGHT, GAME_WIDTH, 2); // draw a line to sction the window for the game and the power up displays
    draw(graphics);//update the positions of everything on the screen 
    g.drawImage(image, 0, 0, this); //move the image on the screen


  }

  //call the draw methods in each class to update positions as things move
  public void draw(Graphics g){
    playerOnePaddle.draw(g);
    playerTwoPaddle.draw(g);
    ball.draw(g);
    playerOneScoreDisplay.draw(g);
    playerTwoScoreDisplay.draw(g);
    playerOnePowerUps.draw(g);
    playerTwoPowerUps.draw(g);
    ballSpeedBoost.draw(g);
    if(!playingGame && !gameEnded){
      instructionsDisplay.draw(g); // draw instructions before game is started
    }
  }

  //call the move methods in other classes to update positions
  //this method is constantly called from run(). By doing this, movements appear fluid and natural. If we take this out the movements appear sluggish and laggy
  public void move(){
    ball.move();
    playerOnePaddle.move();
    playerTwoPaddle.move();
  }

  // displays who the winner is, and ends the game.
  public void endGame(){
    playerOnePowerUps.value = "";
    playerTwoPowerUps.value = "";
    ballSpeedBoost.xcoordinate = 0; // as the text is longer, moves the power up display to the left of the screen.
    if(playerOneScore > playerTwoScore) {
      ballSpeedBoost.value = "PLAYER ONE WINS. RESTART PROGRAM TO REPLAY";
    }
    else if(playerTwoScore > playerOneScore){
      ballSpeedBoost.value = "PLAYER TWO WINS. RESTART PROGRAM TO REPLAY";
    }
    else{
      ballSpeedBoost.value = "PLAYERS ARE TIED";
    }
    gameEnded = true;
    playingGame = false;
  }
  //Monitors scores to check if end condition has been reached. checks the power ups of the players and the ball.
  public void checkGameCondition(){


    // check end conditions, whether time is up or score gap is 5 or over.
    if(timedMatch && currentFrame > matchEndFrame){
      System.out.println("ENDING");
      endGame();
    }
    else if(!timedMatch && Math.abs(playerOneScore - playerTwoScore) >= 5){
      System.out.println("ENDING");
      endGame();
    }
    else{ // regular game operation, if its not ended
      int result = ball.checkCollision(playerOnePaddle.yPosition, playerTwoPaddle.yPosition, playerOnePaddle.yCurrentSpeed, playerTwoPaddle.yCurrentSpeed, playerOnePaddle.currentSize, playerTwoPaddle.currentSize);
      if(result != 0){ // reset the paddles power ups either if it is scored or if the ball bounces off the paddle.
        playerOnePaddle.resetPowerUps();
        playerTwoPaddle.resetPowerUps();
      }
      // if player 1 scores
      if(result == 1){
        playerOneScore += 1;
        playerOneScoreDisplay.value = playerOneScore + "";
      }
      // if player two scores
      else if(result == 2){
        playerTwoScore += 1;
        playerTwoScoreDisplay.value = playerTwoScore + "";
      }

      // display status of ball
      if(ball.currentlySpeedBoosting){
        ballSpeedBoost.value = "ball speed boost engaged";
      }
      else if(ball.canPerformSpeedBoost){
        ballSpeedBoost.value = "ball speed boost available";
      }
      else{
        ballSpeedBoost.value = "ball speed boost unavailable";
      }

      // display the power up status for players.
      playerOnePowerUps.value = + playerOnePaddle.boosts +" boosts, " + playerOnePaddle.sizeups + " sizeups";
      playerTwoPowerUps.value = + playerTwoPaddle.boosts +" boosts, " + playerTwoPaddle.sizeups + " sizeups";
    }
  }

  //run() method is what makes the game continue running without end. It calls other methods to move objects,  check for collision, and update the screen
  public void run(){
    //the CPU runs our game code too quickly - we need to slow it down! The following lines of code "force" the computer to get stuck in a loop for short intervals between calling other methods to update the screen.
    long lastTime = System.nanoTime();
    double ns = 1000000000/60;
    double delta = 0;
    long now;

    while(true){ //this is the infinite game loop
      now = System.nanoTime();
      delta = delta + (now-lastTime)/ns;
      lastTime = now;

      //only move objects around and update screen if enough time has passed
      if(delta >= 1){
        if(playingGame) { // only move the objects if the game is running.
          move();
          checkGameCondition();
        }
        repaint();
        delta--;
        currentFrame += 1;
      }
    }
  }

  //if a key is pressed, we'll send it over to the PlayerBall class for processing
  public void keyPressed(KeyEvent e){
    if(playingGame){ // check controls for other objects only if game is running
      playerOnePaddle.keyPressed(e);
      playerTwoPaddle.keyPressed(e);
      ball.keyPressed(e);
    }
    else if(!gameEnded){ // start the game only if game has not ended yet.
      if(e.getKeyChar() == '1'){
        ballSpeedBoost.xcoordinate = (GAME_WIDTH/2 - 130); // move the display for power ups to correct place.
        playingGame = true; // set variables to start the game
        timedMatch = true;
        matchEndFrame = currentFrame + 3600; // set end time for 60 seconds from now, or 3600 frames;
      }
      else if(e.getKeyChar() == '2'){
        ballSpeedBoost.xcoordinate = (GAME_WIDTH/2 - 130);
        playingGame = true; // set variables to start the game
        timedMatch = false;
      }
    }

  }

  //checks if a key is released in each class.
  public void keyReleased(KeyEvent e){
    if(playingGame) {
      playerOnePaddle.keyReleased(e);
      playerTwoPaddle.keyReleased(e);
    }
  }


  //left empty because we don't need it; must be here because it is required to be overridded by the KeyListener interface
  public void keyTyped(KeyEvent e){

  }
}
