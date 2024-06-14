
package alex.graphics.color;

import java.awt.Color;

/**
 *
 * @author alex
 */
public class ColorConstants {
    
    // These constants denote how much 2 byte integer needs to be shifted.
    public final static int BLUE_SHIFT_AMOUNT=0;
    public final static int GREEN_SHIFT_AMOUNT=8;
    public final static int RED_SHIFT_AMOUNT=16;
    public final static int ALPHA_SHIFT_AMOUNT=24;
    
    public final static int BLUE_MASK=0xff;
    public final static int GREEN_MASK=0xff << GREEN_SHIFT_AMOUNT;
    public final static int RED_MASK=0xff << RED_SHIFT_AMOUNT;
    public final static int ALPHA_MASK=0xff << ALPHA_SHIFT_AMOUNT;
 
    
    public final static Color transparentBlackColor=new Color(0, 0, 0, 0);
    
    public final static Color PURPLE=new Color(0xac01d8);
}
