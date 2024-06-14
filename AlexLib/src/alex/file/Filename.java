
package alex.file;

/**
 * Utility for working with file extensions, mostly.  Does string manipulations
 * only.  The file system isn't touched.
 * 
 * @author alex
 */
public class Filename {
    
    private int numericSuffix;
    
    private String primaryChunk;
    
    // If there is no period in the filename, then extension is null.
    private String extension;
    
    public Filename(final Filename other) {
        this.primaryChunk=other.primaryChunk;
        this.extension=other.extension;
        numericSuffix=0;
    }
    
    /**
     * Use this method only if the filename doesn't have any directory part.
     * @param name 
     * @param hasNoExtension 
     */
    public Filename(final String name, final boolean hasNoExtension) {
        this(name, false, hasNoExtension);
    }
    
    /**
     * name may be a URL as a string or a regular full path.  The directory chunk
     * can be trimmed.
     * 
     * @param name
     * @param shouldRetainOnlyFileName If true, look for the last of
     * either / or \\, and only keep the part of the string after that part.
     * @param hasNoExtension If true, it is known that the filename has no extension.
     * That means, don't search for an extension.   This will prevent files
     * that have a period in their names from losing parts when an extension is set.
     */
    public Filename(final String name, final boolean shouldRetainOnlyFileName, final boolean hasNoExtension) {
        int index;
        String workingName;
        
        workingName=name;
        if (shouldRetainOnlyFileName) {
            index=name.lastIndexOf("/");
            if (index>-1) {
                workingName=workingName.substring(index+1);
            }
            
            index=name.lastIndexOf("\\");
            if (index>-1) {
                workingName=workingName.substring(index+1);
            }
        }
            
        
        index=workingName.lastIndexOf('.');
        if (hasNoExtension || index==-1) {
            extension=null;
            primaryChunk=workingName;
        } else {
            primaryChunk=workingName.substring(0, index);
            extension=workingName.substring(index+1);
        }
    }
    
    public String getExtension() {
        return extension;
    }
    
    public void setExtension(String s) {
        extension=s;
    }
    
    public String getFullName() {
        if (extension!=null) {
            return primaryChunk + "." + extension;
        } else {
            return primaryChunk;
        }
    }
    
    /**
     * Append a chunk to the filename, immediately before the extension.
     * @param chunk May not be null.
     */
    public void appendToFileName(String chunk) {
        primaryChunk+=chunk;
    }
    
    
    public void appendNumberToFileName(final int howMuchToAdd)
    {
        String[] parts;
        int suffix;
        
        parts=findNumericSuffix();
        if (parts[1].equals("")) {
            suffix=0;
            parts[0] = primaryChunk+"_";
        } else {
            suffix=Integer.parseInt(parts[1]);
        }
        suffix+=howMuchToAdd;
        
        primaryChunk=parts[0]+suffix;
    }
    
    /**
     * Call this when trying to create a unique filename.  It'll append a number
     * to the end of the filename, or add to the number at the end of the
     * filename.
     * 
     * Return a two element array.
     * [0]=everything before the last number.
     * [1]=the last number in the primaryChunk.
     * @return 
     */
    private String[] findNumericSuffix() {
        String[] retVal;
        int firstNonNumericCharacterIndex;
        
        retVal=new String[2];
        retVal[0]="";
        retVal[1]="";
        
        firstNonNumericCharacterIndex=-1;
        if (!primaryChunk.isEmpty()) {
            for (int index=primaryChunk.length()-1; index>=0; index--) {
                if (!Character.isDigit(primaryChunk.charAt(index))) {
                    firstNonNumericCharacterIndex=index;
                    break;
                }
            }
        }
        
        if (firstNonNumericCharacterIndex==-1) {
            retVal[0]=primaryChunk;
        } else {
//            if (firstNonNumericCharacterIndex==0) {
//                retVal[0]=
//                retVal[1]=primaryChunk;
//            } else {
                retVal[0]=primaryChunk.substring(0, firstNonNumericCharacterIndex+1);
                retVal[1]=primaryChunk.substring(firstNonNumericCharacterIndex+1);
//            }
        }
        
        return retVal;
    }
}
