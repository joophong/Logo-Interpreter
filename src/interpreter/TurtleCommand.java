package interpreter;

import java.awt.Graphics;

/**
 * This is an abstract superclass for commands that draw on a
 * Graphics, or otherwise interact with it (for example,
 * change color, or move without drawing.)
 * 
 * @author David Matuszek
 * @version March 30, 2009
 *
 */
interface TurtleCommand {
    
    /**
     * Executes this command (whatever it is).
     * 
     * @param g The graphics on which something is to be done.
     */
    public abstract void execute(Graphics g);
}
