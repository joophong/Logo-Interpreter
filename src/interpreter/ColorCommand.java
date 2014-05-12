package interpreter;
import java.awt.*;

/**
 * @author Joopyo Hong
 * @version April 12, 2014
 */
public class ColorCommand implements TurtleCommand {
    Color color;
    
    /**
     * Constructs a ColorCommand.
     * @param colorName The name of the color.
     */
    public ColorCommand(String colorName) {
        this.color = Color.getColor(colorName);
    }
    
    /**
     * Constructs a ColorCommand.
     * @param number The numeric value of the color.
     */
    public ColorCommand(int number) {
        this.color = new Color(number);
    }
    
    /**
     * Constructs a ColorCommand.
     * @param red The numeric value of the first sRGB component.
     * @param green The numeric value of the second sRGB component.
     * @param blue The numeric value of the third sRGB component.
     */
    public ColorCommand(int red, int green, int blue) {
        this.color = new Color(red, green, blue);
    }

    /**
     * @see TurtleCommand#execute(java.awt.Graphics)
     */
    @Override
    public void execute(Graphics g) {
        g.setColor(color);
    }
}
