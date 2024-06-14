
package alex.string;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 *
 * @author alex
 */
public class StringUtil {
    
    /**
     * If the given string is surrounded by "" or '', then return the string
     * without those enclosing characters.
     * 
     * If any of these criteria are false, make no change to the given string
     * and just return it.
     * 
     * @param s Should not be null, and its size should be >=2.
     * @return 
     */
    public static String removeEnclosingQuotes(final String s) {
        char quoteChar, endQuoteChar;
        String retVal;
        
        retVal=s;
        if (s!=null && s.length()>=2) {
            
            quoteChar=s.charAt(0);
            if (quoteChar=='"' || quoteChar=='\'') {
                endQuoteChar=s.charAt(s.length()-1);
                
                if (endQuoteChar==quoteChar) {
                    retVal=s.substring(1, s.length()-1);
                }
            }
        }
        
        return retVal;
    }
    
    /**
     * Remove everything that's not a letter.  Return the newly formed string.
     * 
     * @param s May not be null.  May be empty.
     * @return 
     */
    public static String retainLettersOnly(String s) {
        String retVal;
        StringBuilder sb;
        
        retVal="";
               
        if (!s.isEmpty()) {
            sb=new StringBuilder(s.length());
            for (char c: s.toCharArray()) {
                if (Character.isLetter(c)) {
                    sb.append(c);
                }
            }
            
            retVal=sb.toString();
        }
        
        return retVal;
    }
    
    /**
     * Split a string by line delimiter.  Throw away comments.  Potentially
     * discard blank lines.
     * 
     * @param content
     * @param commentSymbol Throw away all text from this symbol to the end of
     * the line.  If null, nothing is thrown away as a comment.
     * @param shouldTrimWhiteSpace If true, call trim on each line.
     * @param shouldDiscardBlankLines If true, throw away lines that have
     * 0 characters.
     * @return 
     * @deprecated Call the stringToList method with all the boolean arguments.
     */
    @Deprecated
    public static ArrayList<String> splitIntoListAndHandleComments(String content,
            String commentSymbol,
            boolean shouldTrimWhiteSpace,
            boolean shouldDiscardBlankLines)
    {
        ArrayList<String> retVal, firstPass;
        boolean shouldCutComments;
        int index;
        
        firstPass=stringToList(content);
        shouldCutComments=commentSymbol!=null;
        
        if (shouldTrimWhiteSpace
                || shouldDiscardBlankLines
                || shouldCutComments) 
        {
            retVal=new ArrayList();
            for (String oneLine: firstPass)
            {
                if (shouldCutComments) {
                    index=oneLine.indexOf(commentSymbol);
                    if (index!=-1) {
                        oneLine=oneLine.substring(0, index);
                    }
                }
                
                if (shouldTrimWhiteSpace) {
                    oneLine=oneLine.trim();
                }
                
                // This should be last.
                if (!(shouldDiscardBlankLines && oneLine.isEmpty())) {
                    retVal.add(oneLine);
                }
                
            }
        } else {
            // No changes need to be made.
            retVal=firstPass;
        }
        
        return retVal;
    }
    
    
    
    /**
     * Split a list using the line terminator as the place 
     * to perform the splitting.  This is typically meant to split file contents
     * into lines.
     * 
     * Swallow all exceptions.
     * 
     * @param content May not be null.
     * @return 
     */
    public static ArrayList<String> stringToList(String content)
    {
        return stringToList(content, false, false, false, null);
    }
    
    
    
    
    /**
     * Split a list using the line terminator as the place 
     * to perform the splitting.  This is typically meant to split file contents
     * into lines.
     * 
     * Swallow all exceptions.
     * 
     * @param content The string to split up by the line terminator.
     * @param shouldDiscardEmptyLines If true, don't include empty lines in the
     * returned list.
     * @param shouldTrimWhitespace If true, trim all whitespace from the
     * beginning and end of each line.
     * @param shouldHandleComments If true, remove comments.
     * @param commentStarter May not be null if shouldHandleComments is true.
     * This should be the String that starts a comment, such as # or //.
     * @return 
     */
    public static ArrayList<String> stringToList(final String content,
            final boolean shouldDiscardEmptyLines,
            final boolean shouldTrimWhitespace,
            final boolean shouldHandleComments,
            final String commentStarter)
    {
        ArrayList<String> retVal;
        String line;
        StringReader sr;
        BufferedReader br;
        int index1;
        
        sr=new StringReader(content);
        br=new BufferedReader(sr);
        retVal=new ArrayList();
        
        try {
            while (null!=(line=br.readLine())) {
                if (shouldTrimWhitespace) {
                    line=line.trim();
                }
                
                if (shouldHandleComments) {
                    index1=line.indexOf(commentStarter);
                    if (index1==0) {
                        line="";
                    } else if (index1!=-1) {
                        line=line.substring(0, index1);
                    }
                }
                
                if (shouldDiscardEmptyLines) {
                    if (line.isEmpty()) {
                        continue;
                    }
                }
                
                retVal.add(line);
            }
        }  catch (Exception e) {
            // Should never happen.
            e.printStackTrace();
        }
        
        try {
            br.close();
        } catch (Exception e) {
            // Do nothing.
        }
        
        sr.close();
            
        return retVal;
    }
    
}
