/**
 */
package alex.graphics.color;

import java.awt.Color;

/**
 *
 * @author alex
 */
public class ARGBPieces {
    
    private int alpha;
    private int blue;
    private int red;
    private int green;
    private Color color;
    
    public ARGBPieces()
    {
        color=ColorConstants.transparentBlackColor;
        separateColorChannels();
    }
    
    public ARGBPieces(Color c) 
    {
        this.color=c;
        separateColorChannels();
    }
    
    private void separateColorChannels()
    {
//        int argb=color.getRGB();
        
//        alpha=argb & ColorConstants.ALPHA_MASK >> ColorConstants.ALPHA_SHIFT_AMOUNT;
//        red=argb & ColorConstants.RED_MASK >> ColorConstants.RED_SHIFT_AMOUNT;
//        green=argb & ColorConstants.GREEN_MASK >> ColorConstants.GREEN_SHIFT_AMOUNT;
//        blue=argb & ColorConstants.BLUE_MASK;

        red=color.getRed();
        blue=color.getBlue();
        green=color.getGreen();
        alpha=color.getAlpha();
    }
    
    public Color getColor()
    {
        if (this.color==null) {
            color=new Color(red, green, blue, alpha);
        }
        
        return color;
    }
    
    public void setColor(Color c) {
        this.color=c;
        separateColorChannels();
    }

    public int getAlpha() {
        return alpha;
    }

    public int getBlue() {
        return blue;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public void alterAlpha(int amount) {
        this.color=null;
        this.alpha+=amount;
    }

    public void alterBlue(int amount) {
        this.color=null;
        this.blue+=amount;
    }

    public void alterRed(int amount) {
        this.color=null;
        this.red+=amount;
    }

    public void alterGreen(int amount) {
        this.color=null;
        this.green+=amount;
    }
    
    
}
