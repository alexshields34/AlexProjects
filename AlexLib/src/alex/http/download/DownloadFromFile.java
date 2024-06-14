/**
 */
package alex.http.download;

import alex.file.Filename;
import alex.http.URLHelper;
import alex.string.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Download many files.  The list of files to download is within a specified
 * file.  The given file can also include junk that's not relevant.  For example,
 * this kind of line:
 * 
 * "BackURL": "http://cloud-3.steamusercontent.com/ugc/1011562618093845464/80687C9319FA2015F3D9F7CBEB4C55FBF045B27D/",
 * 
 * Look for "http" and use the rest of the line as the entire URL.  That means,
 * stuff like "BackURL" will just be completely ignored.  
 * 
 * After finding http in a line, look to see if the " character immediately 
 * precedes the http.  If so, then we want the entire line up to the next ".
 * That'll be the URL to use.
 * @author alex
 */
public class DownloadFromFile {
    private final String listOfURLs;
    private final File targetSaveDirectory;
    private final boolean shouldDownloadDuplicates;
    private final boolean isVerbose;
    
    // key=urlAsString, value=filename.
    private final HashMap<String, String> previouslyDownloadedFiles;
    
    public DownloadFromFile(String listOfURLs,
            File targetDirectory,
            boolean shouldDownloadDuplicates,
            boolean isVerbose)
    {
        this.listOfURLs=listOfURLs;
        this.targetSaveDirectory=targetDirectory;
        this.shouldDownloadDuplicates=shouldDownloadDuplicates;
        this.isVerbose=isVerbose;
        
        previouslyDownloadedFiles=new HashMap<String, String>();
    }
    
    
    
    public void downloadAll()
            throws Exception
    {
        FileOutputStream fos;
        ArrayList<String> lineList;
        int lineNumber;
        String urlAsString, targetFileName;
        File targetFile;
        HttpURLConnection huc;
        URL singleURL;
        byte[] data;
        HashMap<String, String> imgurHeader, headerToUse;
        
        lineList=StringUtil.splitIntoListAndHandleComments(listOfURLs, "#", true, true);
        
        imgurHeader=new HashMap();
        imgurHeader.put("Referer", "https://imgur.com/");
        
        
        lineNumber=0;
        for (String line: lineList)
        {
            lineNumber++;
            
            urlAsString=getURLFromLine(line);
            if (urlAsString==null) {
                continue;
            }
            
            if (!shouldDownloadDuplicates && previouslyDownloadedFiles.containsKey(urlAsString))
            {
                continue;
            }
            
            singleURL=null;
            try {
                singleURL=new URL(urlAsString);
            } catch (Exception e) {
                // Any kind of error means to just continue the loop.
                continue;
            }
            
            huc=(HttpURLConnection)singleURL.openConnection();
            
            targetFileName=URLHelper.determineFileName(line, huc, true);
            
//            targetFile=new File(targetSaveDirectory, targetFileName);
            targetFile=generateUniqueFile(targetSaveDirectory, targetFileName);
            
            
            if (isVerbose) {
                System.out.println("Line ["
                        +lineNumber
                        +"]. Saving ["
                        + urlAsString
                        + "] to the target ["
                        +targetFile.toString()
                        +"]");
            }
            
            
            // The file already exists, skipping it.
            if (!shouldDownloadDuplicates && targetFile.exists()) {
                if (isVerbose) {
                    System.out.println("The file exists.  Continuing.");
                }
                continue;
            }
                
            try {
                // Set referer.
                if (urlAsString.contains(".imgur")) {
                    headerToUse=imgurHeader;
                } else {
                    headerToUse=null;
                }
                
                data=Downloader.retrieveBinary(singleURL, false, 1024*1024, headerToUse);
            } catch (Exception e) {
                if (isVerbose) {
                    System.out.println("Couldn't download the data.  Continuing to the next line.");
                } else {
                    e.printStackTrace();
                }
                continue;
            }
            
            try {
                fos=new FileOutputStream(targetFile);
                fos.write(data);
                fos.close();
            } catch (Exception e) {
                if (isVerbose) {
                    System.out.println("Couldn't save the fie.  Continuing to the next line.");
                } else {
                    e.printStackTrace();
                }
                continue;
            }
            
            
            previouslyDownloadedFiles.put(urlAsString, targetFileName);
        }
        
    }

    /**
     * Look for "http" in the line.  Include everything from that point to the
     * end of the line.  If "http" is preceded by a " character, then only get
     * the stuff that's within "" characters.
     * @param line
     * @return 
     */
    private String getURLFromLine(String line)
    {
        char c;
        String retVal;
        int indexStart, indexEnd, index2;
        final String search1="http";
        final String search2="://";
        
        retVal=null;
        
        indexStart=line.indexOf(search1);
        index2=line.indexOf(search2);
        indexEnd=line.length();
        if (indexStart!=-1 && index2!=-1) {
            if (indexStart>0) {
                c=line.charAt(indexStart-1);
                if (c=='"') {
                    indexEnd=line.indexOf("\"", indexStart);
                    if (indexEnd==-1) {
                       indexEnd=line.length();
                    }
                }
            }
            
            retVal=line.substring(indexStart, indexEnd);
        }
        
        return retVal;
    }    
    
    
    /**
     * If the specified file exists, then add a number to it until we find
     * a filename that doesn't already exist.
     * @return 
     * @param targetFileName 
     * @param targetSaveDirectory 
     */
    private File generateUniqueFile(File targetSaveDirectory, String targetFileName) {
        
        Filename fn;
        File f;
        int appendee;
        
        f=new File(targetSaveDirectory, targetFileName);
        
        if (f.exists()) {
            appendee=0;
        
            while (f.exists()) {
                appendee++;
                fn=new Filename(targetFileName, false);
                fn.appendToFileName(String.valueOf(appendee));
                
                f=new File(targetSaveDirectory, fn.getFullName());
            }
        }
        
        return f;
    }
    
}
