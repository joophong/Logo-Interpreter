package interpreter;

import java.awt.*;
/**
 * This is an example of the kind of command that can be
 * executed by an instance of the DrawingCanvas class.
 * 
 * @author David Matuszek
 * @version March 31, 2009
 *
 */
public class DrawLineCommand implements TurtleCommand {
    double x1, y1, x2, y2;
    
    /**
     * Constructs a DrawLineCommand.
     */
    DrawLineCommand(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    
    /**
     * Implements this command on the given Graphics.
     * 
     * @param g The graphics on which to do the drawing.
     */
    public void execute(Graphics g) {
        g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
    }
}