
package alex.http;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author alex
 */
public final class URLHelper
{
    
    public static void outputURLConnectionInfo(HttpURLConnection urlConn)
    {
        StringBuilder sb, valuesSb;
        Map<String, List<String>> headerFields, fields;
        List<String> values;
        
        sb=new StringBuilder(1000);
        valuesSb=new StringBuilder(1000);
        
        sb.append("urlConn.getConnectTimeout()=[")
                .append(urlConn.getConnectTimeout())
                .append("]\n");
        sb.append("urlConn.getContentEncoding()=[")
                .append(urlConn.getContentEncoding())
                .append("]\n");
        sb.append("urlConn.getContentLength()=[")
                .append(urlConn.getContentLength())
                .append("]\n");
        sb.append("urlConn.getContentType()=[")
                .append(urlConn.getContentType())
                .append("]\n");
        sb.append("urlConn.getRequestMethod()=[")
                .append(urlConn.getRequestMethod())
                .append("]\n");
        
        try {
            sb.append("urlConn.getResponseCode()=[")
                    .append(urlConn.getResponseCode())
                    .append("]\n");
            sb.append("urlConn.getResponseMessage()=[")
                    .append(urlConn.getResponseMessage())
                    .append("]\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
//        fields=urlConn.getRequestProperties();
//        sb.append("Request properties:\n");
//        for (String key: fields.keySet())
//        {
//            values=fields.get(key);
//            
//            sb.append("key=[")
//                    .append(key)
//                    .append("], values=[");
//            
//            valuesSb.setLength(0);
//            for (String oneValue: values) {
//                valuesSb.append("[")
//                        .append(oneValue)
//                        .append("],");
//            }
//            if (valuesSb.length()>0) {
//                valuesSb.setLength(valuesSb.length()-1);
//            }
//            sb.append(valuesSb)
//                    .append("]\n");
//        }
        
        headerFields=urlConn.getHeaderFields();
        sb.append("Header fields:\n");
        for (String key: headerFields.keySet())
        {
            values=headerFields.get(key);
            
            sb.append("key=[")
                    .append(key)
                    .append("], values=[");
            
            valuesSb.setLength(0);
            for (String oneValue: values) {
                valuesSb.append("[")
                        .append(oneValue)
                        .append("],");
            }
            if (valuesSb.length()>0) {
                valuesSb.setLength(valuesSb.length()-1);
            }
            sb.append(valuesSb)
                    .append("]\n");
        }
        
        System.out.println(sb.toString());
        
        
    }
    
    public static String getFileNameFromURL(URL url)
    {
        String f;
        int index;

        f=url.toString();

        if (f!=null) {
            index=f.indexOf('?');
            if (index!=-1) {
                f=f.substring(0, index);
            }

            index=f.lastIndexOf('/');
            if (index!=-1) {
                f=f.substring(index+1, f.length());
            }
        }

        return f;
    }
    
    
    /**
     * Get the filename from the URLConnection's header if possible.
     * @return
     * @param urlConn 
     */
    public static String getFileNameFromContentDisposition(URLConnection urlConn)
    {
        int index;
        String contentDisposition, fileName;

        fileName=null;
        contentDisposition=urlConn.getHeaderField("Content-Disposition");

        if (contentDisposition!=null) {
            index=contentDisposition.indexOf("filename=");
            if (index!=-1) {
                fileName=contentDisposition.substring(index+9);
                fileName=cleanUpFileName(fileName);
            }
        }

        return fileName;
    }
    
    /**
     * Changes unwanted characters to underscores ('_').  This method handles
     * escaped characters (%20, %26, %27) in addition to non-escaped characters.
     * @param fileName
     * @return 
     */
    public static String cleanUpFileName(String fileName)
    {
        StringBuilder sb;
	char c;

	sb=new StringBuilder(fileName.length());
        for (int i=0; i<fileName.length(); i++) {
            c=fileName.charAt(i);
            
            // If we have a %20, change it to an underscore.  It'll get handled
            // further below.  Also, we want to move ahead an additional 2 characters.
            // %26 is &.
            // %27 is '.
            if (c=='%' && i<fileName.length()-2) {
                if (fileName.charAt(i+1)=='2' && fileName.charAt(i+2)=='0') {
                    c='_';
                    i+=2;
                } else if (fileName.charAt(i+1)=='2' && fileName.charAt(i+2)=='6') {
                    c='&';
                    i+=2;
                } else if (fileName.charAt(i+1)=='2' && fileName.charAt(i+2)=='7') {
                    c='\'';
                    i+=2;
                }
            }
            
            if (c=='.'
                    || c=='_'
                    || c=='-'
                    || Character.isLetterOrDigit(c))
            {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        
        // It's possible that there are underscores on both sides of the new
        // filename.  Remove all underscores from both sides of the filename.
        while (sb.length()>0 && sb.charAt(0)=='_') {
            sb.deleteCharAt(0);
        }
        while (sb.length()>0 && sb.charAt(sb.length()-1)=='_') {
            sb.deleteCharAt(sb.length()-1);
        }

	return sb.toString();
    }
    
    
    /**
     * Derive the file name from the input line.
     * @return
     * @param line
     * @param shouldUseSecondToLastPartOfFileName 
     */
    public static String getProperFileName(String line, boolean shouldUseSecondToLastPartOfFileName)
    {
	StringBuilder sb;
	char c;
	String fileName, secondToLastFileName;
        int index, firstIndex, secondIndex;

	sb=new StringBuilder();
	secondToLastFileName=null;

        // If there's a ? in the line, then cut from there to the end.
        index=line.indexOf('?');
        if (index!=-1) {
            line=line.substring(0, index);
        }

	if (shouldUseSecondToLastPartOfFileName) {
	    secondIndex=line.lastIndexOf('/');
	    firstIndex=line.lastIndexOf('/', secondIndex-1);
	    
	    secondToLastFileName=line.substring(firstIndex+1, secondIndex);
	}

	fileName=line.substring(1+line.lastIndexOf('/'));

	// If we found no filename.
	if (fileName==null || fileName.isEmpty()) {
	    for (int i=0; i<line.length(); i++) {
		c=line.charAt(i);
		if (Character.isLetterOrDigit(c)) {
		    sb.append(c);
		} else {
		    sb.append('_');
		}
	    }
	    sb.append(".html");
	    fileName=sb.toString();
	}

	// We want to reuse the extension from the filename that it would
	// normally have been gotten.
	if (shouldUseSecondToLastPartOfFileName) {
	    index=fileName.lastIndexOf('.');
	    fileName=secondToLastFileName+fileName.substring(index);
	}

	return fileName;
    }


    /**
     * 
     * @param urlAsString Look in this string. May not be null.
     * @param parameter Look for this string. May not be null.
     * @param defaultIfNotFound If parameter is not found, return this.  May be null.
     * @return 
     */
    public static String extractParameter(String urlAsString,
            String parameter,
            String defaultIfNotFound)
    {
        int paramIndex, equalsIndex, ampIndex;
        String retVal=defaultIfNotFound;
        
        paramIndex=urlAsString.indexOf(parameter);
        if (paramIndex!=-1) {
            equalsIndex=urlAsString.indexOf('=', paramIndex);
            if (equalsIndex!=-1) {
                ampIndex=urlAsString.indexOf('&', equalsIndex);
                // This one may be -1.  If it is, then the value for 
                // the parameter is everything after =.
                if (ampIndex==-1) {
                    ampIndex=urlAsString.length();
                }
                retVal=urlAsString.substring(equalsIndex+1, ampIndex);
            } 
        }
        
        return retVal;
    }
    
    
    /**
     * 
     * @param urlAsString May not be null.
     * @param parameter May not be null.
     * @param newValue May not be null.
     * @return 
     */
    public static String setParameter(String urlAsString,
            String parameter,
            String newValue)
    {
        int paramIndex, equalsIndex, ampIndex;
        String retVal=urlAsString;
        StringBuilder sb;
        
        paramIndex=urlAsString.indexOf(parameter);
        if (paramIndex!=-1) {
            equalsIndex=urlAsString.indexOf('=', paramIndex);
            if (equalsIndex!=-1) {
                
                // This one might be -1.
                ampIndex=urlAsString.indexOf('&', equalsIndex);
                
                sb=new StringBuilder(urlAsString.length());
                sb.append(urlAsString.substring(0, equalsIndex+1));
                sb.append(newValue);
                
                if (ampIndex!=-1) {
                    sb.append(urlAsString.substring(ampIndex));
                }
                retVal=sb.toString();
            } 
        }
        
        return retVal;
    }
    
    /**
     * For http and https URLs only.  In other words, ftp:// aren't handled
     * here.
     * 
     * @param urlAsString
     * @return 
     */
    public static boolean isAbsoluteURL(final String urlAsString) {
        String s;
        final String search1="http";
        final String search2="://";
        boolean retVal;
        
        s=urlAsString.toLowerCase().trim();
        
        retVal=s.startsWith(search1) && s.contains(search2);
        
        return retVal;
    }
    
    
    /**
     * Given a URL like "https://soundcloud.com/andrew-goldfarb-2/tracks", return
     * "https://soundcloud.com/andrew-goldfarb-2/"
     * 
     * The input string must contain http at the beginning.  If it doesn't, return
     * the input string.
     * 
     * @param urlString May not be null.
     * @return 
     */
    public static String getURLWithoutFile(String urlString) {
        String retVal;
        final String doubleSlash="//";
        final String http="http";
        int index1, index2, indexFromRear;
        
        retVal=urlString;
        if (urlString.startsWith(http)) {
            index1=urlString.indexOf(doubleSlash);
            
            indexFromRear=urlString.indexOf("?");
            if (indexFromRear==-1) {
                indexFromRear=urlString.length()-1;
            }
            index2=urlString.lastIndexOf("/", indexFromRear);
            
            if (index2!=-1) {
                retVal=urlString.substring(0, index2+1);
            }
        }
        
        return retVal;
    }
    
    /**
     * Given a URL like "https://soundcloud.com/andrew-goldfarb-2/tracks", return
     * "https://soundcloud.com"
     * 
     * The input string must contain http at the beginning.  If it doesn't, return
     * the input string.
     * 
     * @param urlString May not be null.
     * @return 
     */
    public static String getBaseURL(String urlString) {
        String retVal;
        final String doubleSlash="//";
        final String http="http";
        int index1, index2;
        
        retVal=urlString;
        if (urlString.startsWith(http)) {
            index1=urlString.indexOf(doubleSlash);
            index2=urlString.indexOf("/", index1+1+doubleSlash.length());
            if (index2!=-1) {
                retVal=urlString.substring(0, index2);
            }
        }
        
        return retVal;
    }

    
    
    /**
     * It's common that some URLs have funky characters and that they have a
     * more proper filename that is in the content disposition.
     * @param line
     * @param urlConn
     * @param shouldCleanupFinalFilename
     * @return 
     */
    public static String determineFileName(String line, URLConnection urlConn, boolean shouldCleanupFinalFilename)
    {
        String fileName;

        // Try to get a filename from the url.getFile from the URLConnection.
        // Then try to get one from the ContentDisposition.
        // Then just get one from the request URL.

        fileName=getFileNameFromContentDisposition(urlConn);
        System.out.println("Retrieved the filename from the content disposition, which is [" + fileName + "].");

        if (fileName==null) {
            fileName=getFileNameFromURL(urlConn.getURL());
            System.out.println("Retrieved the filename from the url, which is [" + fileName + "].");
        }

        if (fileName==null) {
            fileName=getProperFileName(line, false);
            System.out.println("Derived the filename from the line read from the input file, which is [" + fileName + "].");
        }

        if (shouldCleanupFinalFilename) {
            fileName=URLHelper.cleanUpFileName(fileName);
        }
        
        return fileName;
    }
        
    

    /**
     * For testing only.
     * @param url
     * @throws Exception
     */
    public void outputURLGuts(URL url)
            throws Exception
    {
        StringBuilder sb;
        URI uri;

        sb=new StringBuilder(1000);

        sb.append("url.getFile()=[")
                .append(url.getFile())
                .append("], url.getPath()=[")
                .append(url.getPath())
                .append("], url.getRef()=[")
                .append(url.getRef())
                .append("], url.toExternalForm()=[")
                .append(url.toExternalForm())
                .append("], url.toString()=[")
                .append(url.toString())
                .append("]\n");

        uri=url.toURI();
        sb.append("uri.getFragment()=[")
                .append(uri.getFragment())
                .append("]\n");

        System.out.println(sb.toString());
    }
}

