package interpreter;

import java.awt.*;
/**
 * This is an example of the kind of command that can be
 * executed by an instance of the DrawingCanvas class.
 * 
 * @author David Matuszek
 * @version March 29, 2009
 *
 */
public class DrawStringCommand implements TurtleCommand {
    int x, y;
    String message;
    
    /**
     * This is an example of a constructor for a subclass of
     * DrawingCommand. The constructor should typically take whatever
     * arguments are needed for the command (this particular
     * example takes three) and just saves them in instance
     * variables for later use.
     * @param message The string to be displayed.
     * @param x The x location at which the string begins.
     * @param y The y location of the bottom of the string.
     */
    DrawStringCommand(String message, double x, double y) {
        this.x = (int) x;
        this.y = (int) y;
        this.message = message;
    }
    
    /**
     * Implements this command on the given Graphics. In this
     * example, the method just draws a string on the Graphics;
     * the particular string, as well as the particular x-y
     * coordinates, were determined when this DrawStringCommand
     * was created.<p>
     * 
     * Note that not all commands need to draw something. For
     * instance, some commands may change the color to be used,
     * or otherwise change the state of the Graphics object.
     * 
     * @param g The graphics on which to do the drawing.
     */
    @Override
    public void execute(Graphics g) {
        g.drawString(message, x, y);
    }
}
