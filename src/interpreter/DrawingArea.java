package interpreter;

import java.awt.*;
import java.util.*;

import javax.swing.JPanel;

/**
 * Executes (and remembers) all the various drawing commands
 * that have been issued.
 * 
 * @author David Matuszek
 * @version March 30, 2009
 */
public class DrawingArea extends JPanel {
    private static final long serialVersionUID = 1L;
    private ArrayList<TurtleCommand> turtleCommands = new ArrayList<TurtleCommand>();
    private int turtleX1, turtleY1;
    private int turtleX2, turtleY2;
    private int turtleX3, turtleY3;

    /**
     * Executes the list of commands that have been added to
     * this DrawingCanvas.
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public synchronized void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        for (Iterator<TurtleCommand> iter = turtleCommands.iterator(); iter.hasNext();) {
            TurtleCommand turtleCommand = iter.next();
            turtleCommand.execute(g);
        }
        showTurtle(g);
    }
    
    /**
     * Clears the list of commands and erases the canvas.
     */
    public synchronized void clear() {
        turtleCommands.clear();
        repaint();
    }
    
    /**
     * Adds a command to the list of commands to be executed
     * in order to draw or redtaw this Canvas.
     * 
     * @param turtleCommand The command to be remembered.
     */
    public synchronized void addCommand(TurtleCommand turtleCommand) {
        turtleCommands.add(turtleCommand);
    }

    /**
     * Defines the points of a triangle representing the current
     * location and direction of the turtle.
     * 
     * @param x1 The x-coordinate of the first point of the triangle.
     * @param y1 The y-coordinate of the first point of the triangle.
     * @param x2 The x-coordinate of the second point of the triangle.
     * @param y2 The y-coordinate of the second point of the triangle.
     * @param x3 The x-coordinate of the third point of the triangle.
     * @param y3 The y-coordinate of the third point of the triangle.
     */
    public synchronized void setTurtleData(int x1, int y1, int x2, int y2, int x3, int y3) {
        turtleX1 = x1;
        turtleY1 = y1;
        turtleX2 = x2;
        turtleY2 = y2;
        turtleX3 = x3;
        turtleY3 = y3;
    }

    /**
     * Draws a triangle to represent the current position of this
     * Turtle. The triangle does <i>not</i> go into the DrawingCanvas's
     * list of commands.
     * 
     * @param g The Graphics on which to draw the triangle.
     */
    private synchronized void showTurtle(Graphics g) {
        Color savedColor = g.getColor();
        g.setColor(Color.GRAY);
        g.drawLine(turtleX1, turtleY1, turtleX2, turtleY2);
        g.drawLine(turtleX2, turtleY2, turtleX3, turtleY3);
        g.drawLine(turtleX3, turtleY3, turtleX1, turtleY1);
        g.setColor(savedColor);  
    }
}
