
package alex.string;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a single string that contains a bunch of
 * lines delineated by newlines.
 * 
 * This class contains a bunch of convenience methods for finding matching lines.
 * 
 * 
 * @author alex
 */
public class SearchableStringLines {
    private final ArrayList<String> lines;
    
    public SearchableStringLines(final String content) {
        this(content, false, false, false, null);
    }
    
    
    public SearchableStringLines(final String content,
            final boolean shouldDiscardEmptyLines,
            final boolean shouldTrimWhiteSpace)
    {
        this(content, shouldDiscardEmptyLines, shouldTrimWhiteSpace, false, null);
    }
    
    public SearchableStringLines(final String content,
            final boolean shouldDiscardEmptyLines,
            final boolean shouldTrimWhiteSpace,
            final boolean shouldDiscardComments,
            final String commentString)
    {
        lines=StringUtil.stringToList(content,
                shouldDiscardEmptyLines,
                shouldTrimWhiteSpace,
                shouldDiscardComments,
                commentString);
    }
    
    public SearchableStringLines(final SearchableStringLines other) {
        this(other.lines);
    }
    
    
    public SearchableStringLines(final List<String> otherLines) {
        lines=new ArrayList();
        lines.addAll(otherLines);
    }
    
    
    public boolean isEmpty()
    {
        return lines.isEmpty();
    }
    
    public int size()
    {
        return lines.size();
    }
    
    /**
     * Return the first line that contains the searchFor string.  Return null
     * if no line matches.
     * 
     * @param searchFor May not be null.
     * @return 
     */
    public String findLine(String searchFor)
    {
        for (String line: lines) {
            if (line.contains(searchFor)) {
                return line;
            }
        }
        
        return null;
    }
    
    /**
     * Return the contents of this searchable list as a List of String objects.
     * The returned list is a copy of self's lines list.  Any change to the 
     * returned List won't affect self's contents.
     * 
     * @return 
     */
    public List<String> linesAsList()
    {
        ArrayList<String> retVal;
        
        retVal=new ArrayList();
        
        retVal.addAll(this.lines);
        
        return retVal;
    }
        
    
    /**
     * Look for all of the lines that contain the searchFor String.  For each
     * line that matches, get the next line.  Return those next lines.  Return
     * an empty list if nothing matches.
     * 
     * @param searchFor May not be null.
     * @return 
     */
    public SearchableStringLines getLinesAfterMatchingLines(String searchFor)
    {
        SearchableStringLines retVal;
        boolean shouldAddNextLine;
        ArrayList<String> subLines;
        
        subLines=new ArrayList();
        shouldAddNextLine=false;
        
        for (String line: lines) {
            if (shouldAddNextLine) {
                subLines.add(line);
                shouldAddNextLine=false;
            }
            if (line.contains(searchFor)) {
                shouldAddNextLine=true;
            }
        }
        
        retVal=new SearchableStringLines(subLines);
        
        return retVal;
    }
    
    /**
     * Look for the first line that contains the searchFor String.  Return
     * the next line.  Return null if nothing matches.
     * 
     * Blank lines are ignored.
     * 
     * @param searchFor May not be null.
     * @return 
     */
    public String getLineAfterMatchingLine(String searchFor)
    {
        return getLineAfterMatchingLine(searchFor, false);
    }
            
    
    /**
     * Look for the first line that contains the searchFor String.  Return
     * the next line.  Return null if nothing matches.
     * 
     * If shouldIgnoreComments is true, then keep looking for following lines
     * until a line is found that doesn't start with #.
     * 
     * Blank lines are ignored.
     * 
     * @param searchFor May not be null.
     * @param shouldIgnoreFollowingComments If true, ignore following lines
     * that start with #.
     * @return 
     */
    public String getLineAfterMatchingLine(String searchFor,
            boolean shouldIgnoreFollowingComments)
    {
        boolean shouldGetNextLine;
        String retVal;
        
        shouldGetNextLine=false;
        retVal=null;
        
        for (String line: lines) {
            if (shouldGetNextLine) {
                
                if (shouldIgnoreFollowingComments && line.startsWith("#")) {
                    continue;
                }
                
                retVal=line;
                break;
            }
            if (line.contains(searchFor)) {
                shouldGetNextLine=true;
            }
        }
        
        return retVal;
    }
    
    
    /**
     * Look for all of the lines that contain the searchFor String.  For each
     * line that matches, add it to the retVal.
     * 
     * @param searchFor May not be null.
     * @return 
     */
    public SearchableStringLines getMatchingLines(String searchFor)
    {
        SearchableStringLines retVal;
        ArrayList<String> subLines;
        
        subLines=new ArrayList();
        
        for (String line: lines) {
            if (line.contains(searchFor)) {
                subLines.add(line);
            }
        }
        
        retVal=new SearchableStringLines(subLines);
        
        return retVal;
    }
}
