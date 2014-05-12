package interpreter;

/**
 * @author Joopyo Hong
 * @version April 12, 2014
 */
public class Turtle {
    private DrawingArea canvas;
    private double x;
    private double y;
    private double degrees;
    private boolean penIsDown;
    private int paintDelay;
    private boolean paused;
    
    /**
     * Creates a Turtle in the center of the specified DrawingCanvas,
     * facing right.
     * 
     * @param canvas The DrawingCanvas for this turtle.
     */
    public Turtle(DrawingArea canvas) {
        this.canvas = canvas;
        initialize();
    }

    /**
     * Make sure this turtle (and its associated canvas)
     * is in a known initial state.
     */
    public void initialize() {
        penIsDown = true;
        paintDelay = 0;
        paused = false;
        home();
    }
    
    /**
     * Moves this Turtle forward the specified distance.
     * 
     * @param distance The approximate number of pixels to move.
     */
    public void forward(double distance) {
        // Compute this Turtle's new location
        double newX = x + computeDeltaX(distance, degrees);
        double newY = y + computeDeltaY(distance, degrees);
        if (penIsDown) {
            canvas.addCommand(new DrawLineCommand(x, y, newX, newY));
        }
        x = newX;
        y = newY;
        finish();
    }
    
    /**
     * Turns this Turtle to the right by the specified angle.
     * 
     * @param angle The number of degrees to turn to the right.
     */
    public void right(double angle) {
        setDegrees(bringIntoRange(degrees + angle));
        finish();
    }

    /**
     * Turns this Turtle to the left by the specified angle.
     * 
     * @param angle The number of degrees to turn to the left.
     */
    public void left(double angle) {
        setDegrees(bringIntoRange(degrees - angle));
        finish();
    }
    
    /**
     * Given a number of degrees, adjust the number to be in
     * the range 0 to 360.
     * @param oldDegrees Some number of degrees.
     * @return An equivalent number of degrees.
     */
    static double bringIntoRange(double oldDegrees) {
        double newDegrees = oldDegrees;
        while (newDegrees < 0) newDegrees += 360;
        while (newDegrees > 360) newDegrees -= 360;
        return newDegrees;
    }
    
    /**
     * Moves this Turtle to the center of the drawing area, facing right.
     */
    public void home() {
        x = canvas.getWidth() / 2.0;
        y = canvas.getHeight() / 2.0;
        setDegrees(0);
        finish();
    }
    
    /**
     * Moves this Turtle to a specified (x, y) location.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void jump(double x, double y) {
        this.x = x + canvas.getWidth() / 2.0;
        this.y = canvas.getHeight() / 2.0 - y;
        finish();
    }
    
    /**
     * Tells this turtle to raise the pen.
     */
    public void penup() {
        penIsDown = false;
    }

    /**
     * Tells this turtle to lower the pen.
     */
    public void pendown() {
        penIsDown = true;
    }
    
    /**
     * Tells this turtle which color pen to use.
     * 
     * @param colorName The name of the color to use.
     */
    public void color(String colorName) {
        canvas.addCommand(new ColorCommand(colorName));
    }
    
    /**
     * Tells this turtle which color pen to use.
     * 
     * @param colorNum1 The first RGB component of the color to use.
     * @param colorNum2 The second RGB component of the color to use.
     * @param colorNum3 The third RGB component of the color to use.
     */
    public void color(int colorNum1, int colorNum2, int colorNum3) {
        canvas.addCommand(new ColorCommand(colorNum1, colorNum2, colorNum3));
    }
    
//    /**
//     * Tells this turtle how log to pause between turtle operations.
//     * 
//     * @param ms The number of milliseconds (1/1000s of a second)
//     * to pause.
//     */
//    public void setPaintDelay(int ms) {
//        paintDelay = ms;
//    }

    /**
     * This is a garbage method, intended as another example
     * of writing a Turtle method.
     * TODO: Delete this method
     * 
     * @param message A string to display.
     * @param newX Where to display the string.
     * @param newY Where to display the string.
     */
    public void stringAt(String message, int newX, int newY) {
        x = newX;
        y = newY;
        canvas.addCommand(new DrawStringCommand(message, x, y));
        finish();
    }
    
    /**
     * Sets the degrees of direction the frog is facing. (0=right, 90=up, 180=left, 270=down) 
     * @param degrees of direction to set to
     */
    public void setDegrees(double degrees) {
        this.degrees = bringIntoRange(degrees);
    }
    
    /**
     * Gets the x location of the frog.
     * @return x location
     */
    public double getX() {
        return x - canvas.getWidth() / 2.0;
    }
    
    /**
     * Gets the y location of the frog.
     * @return y location
     */
    public double getY() {
        return canvas.getHeight() / 2.0 - y;
    }
    
    /**
     * If the parameter is true, causes the turtle to stop whatever
     * it is doing and wait for another call of this method with
     * the parameter false.
     * 
     * @param paused Whether the turtle should be paused.
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Sets the turtle speed. 
     * @param oldSpeed A value from 0 (stopped) to 100 (full speed).
     */
    public void setSpeed(int oldSpeed) {
        int speed = Math.min(Math.max(oldSpeed, 0), 100);
        paintDelay = 10000 - 100 * speed;
    }
    
    
// ------------------------ private helper methods
    
    /**
     * Computes how much to move to add to this Turtle's x-coordinate,
     * in order to displace the Turtle by (approximately) <code>distance</code>
     * pixels in direction <code>degrees</code>.
     * 
     * @param distance The distance to move.
     * @param degrees The direction in which to move.
     * @return The amount to be added to the x-coordinate.
     */
    private static double computeDeltaX(double distance, double degrees) {
        double radians = Math.toRadians(degrees);
        return distance * Math.cos(radians);
    }
    
    /**
     * Computes how much to move to add to this Turtle's y-coordinate,
     * in order to displace the Turtle by (approximately) <code>distance</code>
     * pixels in direction <code>degrees</code>.
     * 
     * @param distance The distance to move.
     * @param degrees The direction in which to move.
     * @return The amount to be added to the y-coordinate.
     */
    private static double computeDeltaY(double distance, double degrees) {
        double radians = Math.toRadians(degrees);
        return distance * Math.sin(radians);
    }
    
    /**
     * Updates the drawing area. This method should be called
     * at the conclusion of every Turtle command.
     */
    private void finish() {
        tellCanvasWhereIAm();
        canvas.repaint();
        do {
            try { Thread.sleep(paintDelay); }
            catch (InterruptedException e) {}
        } while (paused || paintDelay >= 10000);
    }
    

    
    /**
     * Computes the coordinates of the points of a triangle to represent
     * the current position and direction of this Turtle. The triangle
     * points are sent to the DrawingCanvas but do <i>not</i> go into
     * the DrawingCanvas's list of commands.
     */
    private void tellCanvasWhereIAm() {
        int x1 = (int) (x + computeDeltaX(12, degrees));
        int y1 = (int) (y + computeDeltaY(12, degrees));
        int x2 = (int) (x + computeDeltaX(6, degrees - 135));
        int y2 = (int) (y + computeDeltaY(6, degrees - 135));
        int x3 = (int) (x + computeDeltaX(6, degrees + 135));
        int y3 = (int) (y + computeDeltaY(6, degrees + 135));
        canvas.setTurtleData(x1, y1, x2, y2, x3, y3);
    }

    
    
}
