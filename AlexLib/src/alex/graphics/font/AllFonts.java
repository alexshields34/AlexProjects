
package alex.graphics.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author alex
 */
public class AllFonts {
    
    private final static List<Font> allFonts;
    
    static {
        allFonts=new ArrayList<Font>();
    }
    
    
    
    public static List<Font> getAllFonts()
    {
        if (allFonts.isEmpty()) {
            GraphicsEnvironment ge;
            ArrayList<Font> goodFontList;
            
            ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
            goodFontList=new ArrayList<Font>();
            for (Font f: ge.getAllFonts()) {
                if (f.getFamily().contains("Dialog")) {
                    if (f.getName().contains("Dialog")) {
                        goodFontList.add(f);                        
                    }
                } else {
                    goodFontList.add(f);
                }
                
            }


//            allFonts.addAll(Arrays.asList(ge.getAllFonts()));
            allFonts.addAll(goodFontList);
        }
        
        return allFonts;
    } 
    
    
    public static List<Font> getAllFonts_old()
    {
        if (allFonts.isEmpty()) {
            GraphicsEnvironment ge;

            ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            allFonts.addAll(Arrays.asList(ge.getAllFonts()));
        }
        
        return allFonts;
    }
    
    
    public static void outputAllNames(PrintStream pw, boolean shouldOutputLineNumbers) {
        StringBuilder sb;
        int lineNumber;
        
        sb=new StringBuilder();
        lineNumber=0;
        for (Font f: getAllFonts()) {
            sb.setLength(0);
            lineNumber++;
            
            if (shouldOutputLineNumbers) {
                sb.append(lineNumber)
                        .append(".\t");
            }
            
            sb.append("name=[")
                    .append(f.getName())
                    .append("], ")
                    .append("family=[")
                    .append(f.getFamily())
                    .append("], ")
                    .append("fontName=[")
                    .append(f.getFontName())
                    .append("], ")
                    .append("PSName=[")
                    .append(f.getPSName())
                    .append("], ")
                    .append("numGlyphs=[")
                    .append(f.getNumGlyphs())
                    .append("], ")
                    .append("style=[")
                    .append(f.getStyle())
                    .append("], ")
                    .append("hasLayoutAttributes=[")
                    .append(f.hasLayoutAttributes())
                    .append("], ")
                    .append("hasUniformLineMetrics=[")
                    .append(f.hasUniformLineMetrics())
                    .append("]");
            



            pw.println(sb.toString());
        }
    }
    
    public static Font findFont(String name) {
        for (Font f: getAllFonts()) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        
        return null;
    }
    
    
    
}
