/**
 * Copied from empire.
 */

package alex.graphics.image;

import alex.graphics.color.ColorConstants;
import alex.graphics.color.ColorUtil;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageUtil
{

    //--------------------- Public statics ---------------------------

    /**
     * If x+width and y+height go outside the image, then silently ignore
     * those bad pixels.  If (x,y) is outside the image, throw an exception.
     *
     * Return an argb color which is an average of the area.
     * @param bi
     * @param startX
     * @param startY
     * @param width
     * @param height
     * @throws Exception
     * @return An argb color.
     */
    public static int computeAverageColorForSubImage(BufferedImage bi,
						     int startX,
						     int startY,
						     int width,
						     int height)
	throws Exception
    {
	int pixel, numPixels, alpha, red, green, blue, maxWidth, maxHeight,
	    x, y;
	long totalR, totalA, totalB, totalG;

	totalR=totalA=totalB=totalG=0;
	numPixels=0;
	maxWidth=startX+width;
	maxHeight=startY+height;

	// Does it start outside the image?
	try {
	    bi.getRGB(startX, startY);
	} catch (RuntimeException re) {
	    throw new Exception("startX, startY ("+startX+", "+startY+") is out of bounds.");
	}

	for (y=startY; y<maxHeight; y++) {
	    for (x=startX; x<maxWidth; x++) {
		try {
		    pixel=bi.getRGB(x, y);
		    alpha = (pixel >> 24) & 0xff;
		    red   = (pixel >> 16) & 0xff;
		    green = (pixel >> 8) & 0xff; 
		    blue  = (pixel      ) & 0xff;

		    totalA+=alpha;
		    totalR+=red;
		    totalG+=green;
		    totalB+=blue;

		    numPixels++;

		} catch (RuntimeException re) {
		    // Silently ignore bad locations.
		}
	    }
	}

	alpha=(int)(totalA/numPixels);
	red=(int)(totalR/numPixels);
	green=(int)(totalG/numPixels);
	blue=(int)(totalB/numPixels);

	alpha = (alpha<< 24);
	red   = (red << 16);
	green = (green << 8); 


	pixel=alpha | red | green | blue;

	return pixel;
    }

    /**
     * Return an image with all pixels as transparent.  
     * 
     * @param width
     * @param height
     * @return 
     */
    public static BufferedImage createImage(int width, int height) {
	BufferedImage retVal;

	retVal=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        emptyImage(retVal);

	return retVal;
    }


    public static BufferedImage duplicateImage(BufferedImage orig) {
	BufferedImage copy;

	int x, y, h, w, pixel;

	w=orig.getWidth();
	h=orig.getHeight();

	copy=new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

	for (x=0; x<w; x++) {
	    for (y=0; y<h; y++) {
		pixel=orig.getRGB(x, y);
		copy.setRGB(x, y, pixel);
	    }
	}

	return copy;
    }

    /**
     * Pixels that are fully transparent are considered to be 0x7f for all colors.
     * 
     * @param bi
     * @return 
     */
    public static Color determineAverageColor(BufferedImage bi) {

	int a, r, g, b, alpha, red, green, blue, w, h, x, y, pixel;
	Color newColor;
	
	w=bi.getWidth();
	h=bi.getHeight();

	a=b=r=g=0;
	for (x=0; x<w; x++) {
	    for (y=0; y<h; y++) {
		pixel=bi.getRGB(x, y);
		alpha = (pixel >> 24) & 0xff;
		red   = (pixel >> 16) & 0xff;
		green = (pixel >> 8) & 0xff; 
		blue  = (pixel      ) & 0xff;

		if (alpha==0) {
		    red=green=blue=0x7f;
		}

		r+=red;
		g+=green;
		b+=blue;
	    }
	}

	r/=(w*h);
	g/=(w*h);
	b/=(w*h);
	a=0xff;


	newColor=new Color(r, g, b, a);
		
	return newColor;
    }


    /**
     * Set all pixels to full transparency.
     * @param bi
     */
    public static void emptyImage(BufferedImage bi) {
	int x, y;

	for (x=bi.getMinX(); x<bi.getWidth(); x++) {
	    for (y=bi.getMinY(); y<bi.getHeight(); y++) {
		bi.setRGB(x, y, 0);
	    }
	}
    }
    
    /**
     * Magenta pixels are NOT made transparent.
     * @param file
     * @return An image.
     * @throws Exception
     */
    public static BufferedImage loadImage(File file)
	throws Exception
    {
	return loadImage(file, false);
    }

    /**
     * Magenta pixels are NOT made transparent.
     * @param filename
     * @return An image.
     * @throws Exception
     */
    public static BufferedImage loadImage(String filename)
	throws Exception
    {
	return loadImage(new File(filename), false);
    }
    
    
    /**
     * Magenta pixels are made transparent according to shouldMagentaPixelsBeTransparent.
     * @param file
     * @param shouldMagentaPixelsBeTransparent
     * @return An image.
     * @throws Exception
     */
    public static BufferedImage loadImage(File file, boolean shouldMagentaPixelsBeTransparent)
	throws Exception
    {
	BufferedImage img;

	img=ImageIO.read(file);

	// Make sure the image has the correct format.
	img=fixImageType(img);

	// Render magenta pixels transparent.
        if (shouldMagentaPixelsBeTransparent) {
            img=handleTransparency(img);
        }

	return img;
    }
    
    
    public static void writeJpeg(BufferedImage image, File outFile)
	throws java.io.IOException
    {
 	ImageIO.write(image, "jpeg", outFile);
    }


    /**
     * Always save as png.
     * @param image
     * @param outFile
     * @throws java.io.IOException 
     */
    public static void writeImage(BufferedImage image, File outFile)
	throws java.io.IOException
    {
 	ImageIO.write(image, "png", outFile);
    }

    /**
     * This doesn't specify what is the image type such as png or jpeg.
     * @param image
     * @param fileName
     * @throws java.io.IOException 
     */
    public static void writeImage(BufferedImage image, String fileName)
	throws java.io.IOException
    {
	File outFile;
	outFile=new File(fileName);

	writeImage(image, outFile);
    }
    
    
    /**
     * This is a bare bones method that reads any file, and uses the
     * ImageIO.read method to read it.  In other words, Java is handling
     * jpegs, pngs, and so on.
     * 
     * @param f May not be null.
     * @return null if the file couldn't be read, or it's not an image.
     */
    public static BufferedImage loadAnyImage(File f)
    {
	BufferedImage retVal;

        try {
            retVal=ImageIO.read(f);
        } catch (Exception e) {
            retVal=null;
        }
        
	return retVal;
    }


    /**
     * The image to be loaded already has transparent pixels.  Magenta doesn't
     * matter.
     * @param filename
     * @return
     * @throws Exception
     */
    public static BufferedImage loadImageWithTransparentPixels(String filename)
	throws Exception
    {
	File f;
	BufferedImage img;

	f=new File(filename);
	img=ImageIO.read(f);

	// Make sure the image has the correct format.
	img=fixImageType(img);


	return img;

    }

    /**
     * The image will not necessarily be in the format that I want.
     * I need BufferedImages that are in TYPE_INT_ARGB format only.
     * @param img
     * @return
     * @throws Exception
     */
    public static BufferedImage fixImageType(BufferedImage img)
	throws Exception
    {
	BufferedImage argb;
	int x, y, w, h, pixel, alphaMask;

	w=img.getWidth();
	h=img.getHeight();

	// Fix the image to be the correct format.
	argb=new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//	alphaMask=0xff << 24;
	for (x=0; x<w; x++) {
	    for (y=0; y<h; y++) {
		pixel=img.getRGB(x, y);
//		pixel|=alphaMask;
		argb.setRGB(x, y, pixel);
	    }
        }

	return argb;
    }

    
    /**
     * This makes magenta pixels transparent.  This is no longer needed for any
     * reason.
     * 
     * @param img
     * @return
     * @throws Exception
     * @deprecated
     */
    @Deprecated
    public static BufferedImage handleTransparency(BufferedImage img)
	throws Exception
    {
	int red, green, blue, alpha, pixel, x, y, w, h;

	// Make magenta pixels transparent.  The majority of this code came 
	// from the java.awt.image.PixelGrabber examples.
	w=img.getWidth();
	h=img.getHeight();

	for (x=0; x<w; x++) {
	    for (y=0; y<h; y++) {
		pixel=img.getRGB(x, y);
		alpha = (pixel >> ColorConstants.ALPHA_SHIFT_AMOUNT) & 0xff;
		red   = (pixel >> ColorConstants.RED_SHIFT_AMOUNT) & 0xff;
		green = (pixel >> ColorConstants.GREEN_SHIFT_AMOUNT) & 0xff; 
		blue  = (pixel      ) & 0xff;
		
		if ( red == 0xff && green==0 && blue==0xff) {
		    alpha=0;
                }

		pixel=(alpha<<ColorConstants.ALPHA_SHIFT_AMOUNT)
                        | (red<<ColorConstants.RED_SHIFT_AMOUNT)
                        | (green<<ColorConstants.GREEN_SHIFT_AMOUNT)
                        | (blue);

		img.setRGB(x, y, pixel);
	    }
        }
        
	return img;
    }

    
    /**
     * Given an image with transparent pixels on its periphery, return an image
     * that only has pixels that aren't transparent.  Find the boundaries of 
     * the nontransparent part, and return an image with just 
     * the nontransparent part.  This method will always return an image that's 
     * equal in size or smaller than the given image.
     * @param bi  May not be null.
     * @return A nonnull image that's smaller or equal in size to the given
     * image. The return value is a new image.
     */
    public static BufferedImage trimImage(BufferedImage bi) {
        
        BufferedImage retVal;
        
        // These variables represent the boundaries of the nontransparent image.
        int upperLeftX, upperLeftY, lowerRightX, lowerRightY;
        
        int color;
        
        upperLeftX=Integer.MAX_VALUE;
        upperLeftY=Integer.MAX_VALUE;
        lowerRightX=0;
        lowerRightY=0;
        
        
        for (int x=0; x<bi.getWidth(); x++) {
            for (int y=0; y<bi.getHeight(); y++) {
                
                color=bi.getRGB(x, y);
                if (!ColorUtil.isFullyTransparent(color)) {
                    if (x<upperLeftX) {
                        upperLeftX=x;
                    }
                    if (y<upperLeftY) {
                        upperLeftY=y;
                    }
                    if (x>lowerRightX) {
                        lowerRightX=x;
                    }
                    if (y>lowerRightY) {
                        lowerRightY=y;
                    }
                }
            }
        }
        
        
        retVal=createImage(1+lowerRightX-upperLeftX, 1+lowerRightY-upperLeftY);
        for (int x=0; x<retVal.getWidth(); x++) {
            for (int y=0; y<retVal.getHeight(); y++) {
                retVal.setRGB(x, y, bi.getRGB(x+upperLeftX, y+upperLeftY));
            }
        }
        
        return retVal;
    }
    
    /**
     * 
     * @param originalImage May not be null.
     * @param colors A list of 1 or more colors.  The first color herein makes
     * the first growth, the second color the second growth, etc.
     * @param growthRate A positive integer.  This is a pixel amount.
     * @return A new image is returned.  The old image is left alone.
     */
    public static BufferedImage growEdgesWithColor(final BufferedImage originalImage,
            final List<Color> colors,
            final int growthRate)
    {
        Graphics2D g2d;
        BufferedImage retVal, sourceImage, targetImage;
        int anyPixel, nearbyPixelColor, newPixelColor;
        
        sourceImage=ImageUtil.createImage((originalImage.getWidth()*(growthRate+1)),
                (originalImage.getHeight()*(growthRate+1)));
        
        // Just for the sole purpose of getting past a variable not set compile error.
        targetImage=sourceImage; 
        
        g2d=(Graphics2D)sourceImage.getGraphics();
        g2d.drawImage(originalImage, null, sourceImage.getWidth()/4, sourceImage.getHeight()/4);
        g2d.dispose();
        
        for (Color growthColor: colors) {
            newPixelColor=growthColor.getRGB();
            for (int growthRateStep=0; growthRateStep<growthRate; growthRateStep++) {
                sourceImage=targetImage;
                targetImage=ImageUtil.duplicateImage(sourceImage);
                for (int x=0; x<sourceImage.getWidth(); x++) {
                    for (int y=0; y<sourceImage.getHeight(); y++) {

                        anyPixel=sourceImage.getRGB(x, y);
                        if (!ColorUtil.isFullyTransparent(anyPixel)) {

                            // Get the four pixels around this one pixel. Make the change if
                            // the pixel is fully transparent.
                            if (x>0) {
                                nearbyPixelColor=sourceImage.getRGB(x-1, y);
                                if (ColorUtil.isFullyTransparent(nearbyPixelColor)) {
                                    targetImage.setRGB(x-1, y, newPixelColor);
                                }
                            }
                            if (x<sourceImage.getWidth()-1) {
                                nearbyPixelColor=sourceImage.getRGB(x+1, y);
                                if (ColorUtil.isFullyTransparent(nearbyPixelColor)) {
                                    targetImage.setRGB(x+1, y, newPixelColor);
                                }
                            }
                            if (y>0) {
                                nearbyPixelColor=sourceImage.getRGB(x, y-1);
                                if (ColorUtil.isFullyTransparent(nearbyPixelColor)) {
                                    targetImage.setRGB(x, y-1, newPixelColor);
                                }
                            }
                            if (y<sourceImage.getHeight()-1) {
                                nearbyPixelColor=sourceImage.getRGB(x, y+1);
                                if (ColorUtil.isFullyTransparent(nearbyPixelColor)) {
                                    targetImage.setRGB(x, y+1, newPixelColor);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        retVal=ImageUtil.trimImage(targetImage);
        
        return retVal;
    }
    
    /**
     * Create a new image, with each pixel inverted, and return it.
     * 
     * @param sourceImage
     * @return 
     */
    public static BufferedImage invertImage(final BufferedImage sourceImage) {
        BufferedImage retVal;
        int rgb;
        
        retVal=duplicateImage(sourceImage);
        
        for (int x=0; x<retVal.getWidth(); x++) {
            for (int y=0; y<retVal.getHeight(); y++) {
                rgb=retVal.getRGB(x, y);
                rgb=ColorUtil.invertColor(rgb);
                retVal.setRGB(x, y, rgb);
            }
        }
        
        return retVal;
    }
}
