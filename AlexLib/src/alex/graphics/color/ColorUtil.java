package alex.graphics.color;

import alex.math.MathUtil;
import java.awt.Color;

/**
 *
 * @author alex
 */
public class ColorUtil {
    
    /**
     * Return a grayscale value based on the luminosity of the Color argument.
     * Don't return an average of the three colors in the Color argument.  
     * Preserve the alpha of the Color argument.
     * 
     * @param argb A 32 bit color.
     * @return A Color representing the gray value of 
     * the color.
     * 
     * Relevant URLs
     * https://www.tutorialspoint.com/dip/grayscale_to_rgb_conversion.htm#:~:text=Average%20method%20is%20the%20most,get%20your%20desired%20grayscale%20image.
     * https://stackoverflow.com/questions/687261/converting-rgb-to-grayscale-intensity
     * http://cadik.posvete.cz/color_to_gray_evaluation/
     * https://en.wikipedia.org/wiki/Luma_(video)
     * 
     */
    public static Color determineGrayScaleValueFromColor(Color argb) {
       
        double weightedRed, weightedGreen, weightedBlue;
        int eachColor;
        Color retVal;
        
        weightedRed=argb.getRed() * 0.3;
        weightedGreen=argb.getGreen() * 0.59;
        weightedBlue=argb.getBlue() * 0.11;
        eachColor = (int)(weightedRed + weightedGreen + weightedBlue);
        retVal=new Color(eachColor, eachColor, eachColor, argb.getAlpha());
        
        return retVal;
    }
    
    public static Color invertColor(Color c) {
        
        return new Color(invertColor(c.getRGB()));
    }
    
    public static int invertColor(int color) {
        int alpha, red, green, blue, retVal;
        
        alpha=color & ColorConstants.ALPHA_MASK;
        red=color & ColorConstants.RED_MASK >> ColorConstants.RED_SHIFT_AMOUNT;
        green=color & ColorConstants.GREEN_MASK >> ColorConstants.GREEN_SHIFT_AMOUNT;
        blue=color & ColorConstants.BLUE_MASK;
        
        red=0xff-red;
        green=0xff-green;
        blue=0xff-blue;
        
        red=red << ColorConstants.RED_SHIFT_AMOUNT;
        green=green << ColorConstants.GREEN_SHIFT_AMOUNT;
        
        retVal=alpha | red | green | blue;
        
        return retVal;
    }
    
    public static boolean isFullyTransparent(int color) {
        return ((0x00 == (color & ColorConstants.ALPHA_MASK) >> ColorConstants.ALPHA_SHIFT_AMOUNT));
    }
    
    /**
     * If the value in rgba are too large or small, then the boundary is made
     * to be 0 or 255.
     * 
     * @param c Color object. May not be null.
     * @param r Use 0 for no change.   Otherwise, use a positive or negative integer.
     * @param b Use 0 for no change.   Otherwise, use a positive or negative integer.
     * @param g Use 0 for no change.   Otherwise, use a positive or negative integer.
     * @param a Use 0 for no change.   Otherwise, use a positive or negative integer.
     * @return A new Color object.
     */
    public static Color alter(Color c, int r, int b, int g, int a) {
        int newA, newR, newB, newG;
        
        newA=c.getAlpha()+a;
        newA=MathUtil.boundaryCheck(newA, 0, 255);
        newR=c.getRed()+r;
        newR=MathUtil.boundaryCheck(newR, 0, 255);
        newG=c.getGreen()+g;
        newG=MathUtil.boundaryCheck(newG, 0, 255);
        newB=c.getBlue()+b;
        newB=MathUtil.boundaryCheck(newB, 0, 255);
        
        return new Color(newR, newG, newB, newA);
    }
    
}
