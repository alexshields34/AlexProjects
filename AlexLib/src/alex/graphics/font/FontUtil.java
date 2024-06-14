/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alex.graphics.font;

import alex.graphics.image.ImageUtil;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author alex
 */
public class FontUtil {
    public static Dimension determineDimensionsBasedOnFont(FontMetrics fm, int rows, int columns)
    {
        int height, width;
        
        height=fm.getHeight()*rows;
        
        width=(fm.charWidth('A')+fm.getMaxAdvance())*columns;
        
        return new Dimension(width, height);
    }
    
    
    
    /**
     * IMPORTANT! The y coordinate of the returned Rectangle2D value is the
     * baseline of the text in the specified Font.
     * @param text May not be null.
     * @param font May not be null.
     * @return 
     */
    public static Rectangle2D determineTextImageBoundsForFont(String text, Font font)
    {
        Rectangle2D retVal;
        Graphics2D g2d;
        FontRenderContext frc;
        TextLayout tl;
        BufferedImage tempBi;
        
        tempBi=ImageUtil.createImage(1, 1);
        g2d=(Graphics2D)tempBi.getGraphics();
        
        frc=g2d.getFontRenderContext();
        tl=new TextLayout(text, font, frc);
        
        retVal=tl.getBounds();
        
        g2d.dispose();
        
        return retVal;
    }
    
    /**
     * The font is already assigned to the Graphics2D object.  
     * The Graphics2D object isn't disposed when this method is done.
     * @param g2d
     * @param text
     * @param font
     * @return 
     */
    public static TextLayout buildTextLayoutForFont(Graphics2D g2d, String text, Font font)
    {
        FontRenderContext frc;
        TextLayout retVal;
        
        frc=g2d.getFontRenderContext();
        retVal=new TextLayout(text, font, frc);
        
        return retVal;
    }
}
