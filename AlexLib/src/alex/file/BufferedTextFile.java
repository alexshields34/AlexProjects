package alex.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a conglomeration of BufferedReader and a regular text file.
 * This program provides auto-closure if the last line is read.
 * 
 * @author alex
 * 
 * moved to AlexLib on 20200810.
 *
 */
public class BufferedTextFile {
	
	private final File sourceFile;
	private BufferedReader br;
	private FileReader fr;
	private boolean isOpen;
	
	public BufferedTextFile(File f)
			throws java.io.FileNotFoundException
	{
		sourceFile=f;
		reopenFile();
	}
	
	/**
	 * Close the open readers.  Create new readers.
	 */
	private void reopenFile()
		throws java.io.FileNotFoundException
	{
		try {
			br.close();
		} catch (Exception ignore) {
//			ignore.printStackTrace();
		}
		
		try {
			fr.close();
		} catch (Exception ignore) {
//			ignore.printStackTrace();			
		}
		
		fr=new FileReader(sourceFile);
		br=new BufferedReader(fr);
		isOpen=true;
	}

	public void restartFromBeginning() {
		
		try {
			reopenFile();
		} catch (java.io.FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		
	}
        
        public String nextLine(boolean shouldDiscardComments, boolean shouldTrimLines)
		throws java.io.IOException
	{
            return nextLine(shouldDiscardComments, shouldTrimLines, false);
        }
        
        
	public String nextLine(boolean shouldDiscardComments,
                boolean shouldTrimLines,
                boolean shouldDiscardEmptyLines)
		throws java.io.IOException
	{
            String retVal;
            StringBuilder sb;
            int index1;
            
            sb=nextLineAsStringBuilder();
            retVal=null;
            
            if (sb!=null) {
                if (sb.length()==0) {
                    retVal="";
                } else {
                    if (shouldDiscardComments) {
                        index1=sb.indexOf("#");
                        if (index1==0) {
                            sb.setLength(0);
                        } else if (index1>0) {
                            sb.setLength(index1-1);
                        }
                    }

                    retVal=sb.toString();
                    if (shouldTrimLines) {
                        retVal=retVal.trim();
                    }
                }
                
                if (shouldDiscardEmptyLines && retVal.isEmpty()) {
                    retVal=nextLine(shouldDiscardComments,
                        shouldTrimLines,
                        shouldDiscardEmptyLines);
                }
            }
            
            
            return retVal;
        }
        
        
	public StringBuilder nextLineAsStringBuilder()
		throws java.io.IOException
	{
            StringBuilder retVal;
            String line;
            
            line=nextLine();
            retVal=null;
            
            if (line!=null) {
                retVal=new StringBuilder(line);
            }
            
            return retVal;
        }
	
	/**
	 * Return null if the file is closed.
         * 
	 * @return Return null if the file is closed.
         * @throws java.io.IOException
	 */
	public String nextLine()
		throws java.io.IOException
	{
		String line;
		
		if (!isOpen) {
			return null;
		}
		
		line=br.readLine();
		
		if (line==null) {
			close();
		}
		
		return line;
	}
	
	public void close() {
		isOpen=false;
		try {
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add the entire contents of the file into the collection.  The lines of
	 * the file are added as members of the collection.  Whitespace is trimmed.
         * Lines of length 0 are discarded.
	 * 
	 * @param collection May not be null.
	 * @param minimumLineLength If the line is shorter than this length, it is
	 * discarded.  Use -1 to ignore this parameter.
	 * @param shouldLowerCaseLines If true, then make each line lowercase
	 * before putting it into the collection.
	 * @param shouldOnlyKeepAlphabeticWords If the line contains any character
	 * other than alphabetic characters, then discard it.
         * @param shouldDiscardTextAfterPoundSign If there is a # sign in the
         * line, everything after and including the # is discarded.   This is
         * done before other tests.
         * @throws java.io.IOException
	 */
	public void dumpContents(Collection<String> collection,
			int minimumLineLength,
			boolean shouldLowerCaseLines,
			boolean shouldOnlyKeepAlphabeticWords,
                        boolean shouldDiscardTextAfterPoundSign)
		throws java.io.IOException
	{
            int index;
            String line;
		
		while (null != (line=nextLine(shouldDiscardTextAfterPoundSign, true)) ) {
//                    if (shouldDiscardTextAfterPoundSign
//                            && (index=line.indexOf('#'))!=-1)
//                    {
//                        line=line.substring(0, index);
//                    }
                    
//                    line=line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                        
                        
                    if (minimumLineLength!=-1 && line.length()<minimumLineLength) {
                        continue;
                    }
                    if (shouldOnlyKeepAlphabeticWords) {
                        if (!line.matches("[A-Za-z]+")) {
                            continue;
                        }
                    }
                    if (shouldLowerCaseLines) {
                        line=line.toLowerCase();
                    }
                    collection.add(line);
		}
	}
	
	/**
	 * Add the entire contents of the file into the collection.  The lines of
	 * the file are added as members of the collection.
	 * 
	 * @param collection May not be null.
         * @throws java.io.IOException
	 */
	public void dumpContents(Collection<String> collection)
		throws java.io.IOException
	{
		dumpContents(collection, -1, false, false, false);
	}
	
        
        
        public static void main(String[] args)
                throws Exception
        {
            BufferedTextFile btf;
            ArrayList<String> unprocessed;
            File[] list;
            
            File f;
            
            f=new File("C:/alex/games/AlexKahoots/cards");
            unprocessed=new ArrayList<String>();
            
            
            list=f.listFiles(new FilenameFilter() {
                public boolean accept(File dir,
                    String name)
                {
                    if (name.endsWith(".txt")) {
                        return true;
                    }
                    
                    return false;
                }
            });
            
            for (File oneCardFile: list) {
                btf=new BufferedTextFile(oneCardFile);
                btf.dumpContents(unprocessed, -1, false, false, true);
                btf.close();
            }
            
            System.out.println(unprocessed);
            System.out.println("The size of unprocessed is " + unprocessed.size());
        }
}
