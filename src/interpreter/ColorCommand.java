package interpreter;
import java.awt.*;

/**
 * @author David Matuszek
 * @version March 31, 2009
 */
public class ColorCommand implements TurtleCommand {
    Color color;
    
    /**
     * Constructs a ColorCommand.
     * @param colorName The name of the color.
     */
    public ColorCommand(String colorName) {
        this.color = Color.getColor(colorName);
        // TODO Implement this constructor
    }
    
    /**
     * Constructs a ColorCommand.
     * @param number The numeric value of the color.
     */
    public ColorCommand(int number) {
        this.color = new Color(number);
        // TODO Implement this constructor
    }
    
    /**
     * Constructs a ColorCommand.
     * @param red The numeric value of the first sRGB component.
     * @param green The numeric value of the second sRGB component.
     * @param blue The numeric value of the third sRGB component.
     */
    public ColorCommand(int red, int green, int blue) {
        this.color = new Color(red, green, blue);
        // TODO i added this
    }

    /**
     * @see TurtleCommand#execute(java.awt.Graphics)
     */
    @Override
    public void execute(Graphics g) {
        g.setColor(color);
    }
}
