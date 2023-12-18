/*
Pong game, developed by Bala Venkataraman for ICS4U.
OnScreenDisplay class used to draw text on screen to show information to the user.
handles rendering.
*/
import java.awt.*;

public class OnScreenDisplay extends Rectangle{

    public int xcoordinate; // location
    public int ycoordinate;
    public int fontsize; // size
    public String value; // value

    //constructor sets score to 0 and establishes dimensions of game window
    public OnScreenDisplay(String initValue, int x, int y, int initFontsize){
        this.value = initValue;
        this.xcoordinate = x;
        this.ycoordinate = y;
        this.fontsize = initFontsize;
    }

    //called frequently from GamePanel class
    //updates the current score and draws it to the screen
    public void draw(Graphics g){
        g.setColor(Color.black);
        g.setFont(new Font("Consolas", Font.PLAIN, fontsize));
        g.drawString(value, xcoordinate, ycoordinate); //setting location of score to be about the middle
    }
}