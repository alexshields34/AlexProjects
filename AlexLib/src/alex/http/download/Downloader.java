package alex.http.download;

import alex.http.ConnectionHelper;
import alex.http.URLHelper;
import alex.stream.GZIPUtil;
import alex.stream.StreamUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 */
public class Downloader
{
    private static final HashMap<String, String> imgurReferrerHeader;
    
    static {
        imgurReferrerHeader=new HashMap<String, String>();
        imgurReferrerHeader.put("Referer", "https://imgur.com/");
    }
    
    public static String retrieveHtml_usescharacters(final URL webPageUrl)
            throws Exception
    {
        char[] buffer;
        StringBuilder sb;
        InputStream is;
        InputStreamReader isr;
        URLConnection urlConn;
        int numCharactersRead;
        
        buffer=new char[1024*1024];
        sb=new StringBuilder(1024*1024);
        
        urlConn=webPageUrl.openConnection();
        
        is=urlConn.getInputStream();
        isr=new InputStreamReader(is, "utf-8");
        
        while (-1!=(numCharactersRead=isr.read(buffer))) {
            sb.append(new String(buffer, 0, numCharactersRead));
        }
        
        isr.close();
        is.close();
        
        return sb.toString();
    }

    
    public static String retrieveHtmlWithHeaders(final URL webPageUrl,
            final Collection<String[]> headers)
            throws IOException
    {
        byte[] buffer;
        StringBuilder sb;
        InputStream is;
        HttpURLConnection urlConn;
        int numCharactersRead;
        String cookieValue;
        
        buffer=new byte[1024*1024];
        sb=new StringBuilder(1024*1024);
        
        urlConn=(HttpURLConnection)webPageUrl.openConnection();
        
        if (headers!=null && !headers.isEmpty()) {
            for (String[] oneHeader: headers) {
                urlConn.setRequestProperty(oneHeader[0], oneHeader[1]);
            }
        }
        
        ConnectionHelper.setCommonRequestProperties(urlConn);
        
//        System.out.println("Prior to getting the input stream.");
//        DebugUtil.outputMap(urlConn.getHeaderFields());
        
        is=urlConn.getInputStream();
        
        while (-1!=(numCharactersRead=is.read(buffer))) {
            sb.append(new String(buffer, 0, numCharactersRead, "utf-8"));
        }
        
        
        is.close();
        urlConn.disconnect();
        
        return sb.toString();
    }
    
    /**
     * 
     * @param webPageUrl
     * @param referer
     * @param payLoad
     * @param targetFile
     * @param outputProgress
     * @throws IOException
     * @deprecated Use the method with the otherHeaders argument.
     */
    @Deprecated
    public static void retrieveAndSaveBinary(URL webPageUrl, String referer, String payLoad, File targetFile, boolean outputProgress)
            throws IOException
    {
        retrieveAndSaveBinary(webPageUrl, referer, payLoad, targetFile, outputProgress, null);
    }
    
            
    
    /**
     * If payLoad isn't null, then this is a POST operation. 
     * Else, this is a GET operation.
     * @param webPageUrl  May not be null.
     * @param referer May be null.
     * @param payLoad May be null.
     * @param targetFile May not be null.
     * @param outputProgress If true, output the total bytes read in
     * multiples of 10 megs.
     * @param otherHeaders May be null.
     * @throws IOException 
     */
    public static void retrieveAndSaveBinary(final URL webPageUrl,
            final String referer,
            final String payLoad,
            final File targetFile,
            final boolean outputProgress,
            final Map<String, String> otherHeaders)
            throws IOException
    {
        byte[] buffer, outBytes;
        InputStream is;
        HttpURLConnection urlConn;
        int bytesRead;
        OutputStream os;
        FileOutputStream fos;
        int periodicBytesRead;
        final int bytesReadForOutput=1024 * 1024 * 10;
        long totalBytesRead;
        
        buffer=new byte[1024*1024];
        
        urlConn=(HttpURLConnection)webPageUrl.openConnection();
        urlConn.setDoInput(true);
        
        urlConn.setRequestProperty("host", webPageUrl.getHost());
        
        if (referer!=null) {
            urlConn.setRequestProperty("Referer", referer);
        }
        
        
        
        if (otherHeaders!=null && !otherHeaders.isEmpty()) {
            for (String key: otherHeaders.keySet()) {
                if (!key.equals("Referer")
                        || (key.equals("Referer") && referer==null))
                {
                    urlConn.setRequestProperty(key, otherHeaders.get(key));
                }
            }
        } else { 
            ConnectionHelper.setCommonRequestProperties(urlConn);
        }
        

        if (payLoad!=null) {
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            os=urlConn.getOutputStream();
            outBytes=payLoad.getBytes("utf-8");
            os.write(outBytes, 0, outBytes.length);
            os.close();
        } else {
            urlConn.setRequestMethod("GET");
        }
        
        
        is=urlConn.getInputStream();
        fos=new FileOutputStream(targetFile);
        periodicBytesRead=0;
        totalBytesRead=0L;
        while (-1!=(bytesRead=is.read(buffer))) {
            
            if (outputProgress) {
                totalBytesRead+=bytesRead;
                periodicBytesRead+=bytesRead;
                
                if (periodicBytesRead>=bytesReadForOutput) {
                    periodicBytesRead=0;
                    System.out.println("Read [" + totalBytesRead + "] so far.");
                }
            }
            
            fos.write(buffer, 0, bytesRead);
        }
        
        fos.close();
        is.close();
        urlConn.disconnect();
        
    }
    
    
    
    /**
     * If payLoad isn't null, then this is a POST operation. 
     * Else, this is a GET operation.
     * @param webPageUrl  May not be null.
     * @param referer May be null.
     * @param payLoad May be null.
     * @return
     * @throws IOException 
     * @deprecated Use the one with the otherHeaders argument.
     */
    @Deprecated
    public static String retrieveHtml(URL webPageUrl, String referer, String payLoad)
            throws IOException
    {
        return retrieveHtml(webPageUrl, referer, payLoad, null);
    }
    
    
    /**
     * If payLoad isn't null, then this is a POST operation. 
     * Else, this is a GET operation.
     * @param webPageUrl May not be null.
     * @param referer May be null. If this is not null, this is used rather than
     * any Referer that might be in the otherHeaders map.
     * @param payLoad May be null.
     * @param otherHeaders May be null. These headers are set on the URLConnection object.
     * @return
     * @throws IOException 
     */
    public static String retrieveHtml(final URL webPageUrl,
            final String referer,
            final String payLoad,
            final Map<String, String> otherHeaders)
            throws IOException
    {
        byte[] outBytes;
        StringBuilder sb;
        InputStream is;
        HttpURLConnection urlConn;
        OutputStream os;
        
        sb=new StringBuilder(1024*1024);
        
        urlConn=(HttpURLConnection)webPageUrl.openConnection();
        urlConn.setDoInput(true);
        
        urlConn.setRequestProperty("host", webPageUrl.getHost());
        
        if (referer!=null) {
            urlConn.setRequestProperty("Referer", referer);
        }
        
        if (otherHeaders!=null && !otherHeaders.isEmpty()) {
            for (String key: otherHeaders.keySet()) {
                if (!key.equals("Referer")
                        || (key.equals("Referer") && referer==null))
                {
                    urlConn.setRequestProperty(key, otherHeaders.get(key));
                }
            }
        } else { 
            ConnectionHelper.setCommonRequestProperties(urlConn);
        }
        
        if (payLoad!=null) {
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            os=urlConn.getOutputStream();
            outBytes=payLoad.getBytes("utf-8");
            os.write(outBytes, 0, outBytes.length);
            os.close();
        } else {
            urlConn.setRequestMethod("GET");
        }
        
        is=urlConn.getInputStream();
        sb.append(StreamUtil.readCharacterStream(is));
        
        is.close();
        
        urlConn.disconnect();
        
        return sb.toString();
    }
    
    /**
     * If payLoad isn't null, then this is a POST operation. 
     * Else, this is a GET operation.
     * @param webPageUrl May not be null.
     * @param referer May be null. If this is not null, this is used rather than
     * any Referer that might be in the otherHeaders map.
     * @param payLoad May be null.
     * @param otherHeaders May be null. These headers are set on the URLConnection object.
     * @return
     * @throws IOException 
     */
    public static String retrieveHtmlAndHanldeGzip(final URL webPageUrl,
            final String referer,
            final String payLoad,
            final Map<String, String> otherHeaders)
            throws IOException
    {
        byte[] outBytes, data;
        StringBuilder sb;
        InputStream is;
        HttpURLConnection urlConn;
        OutputStream os;
        String contentEncoding, decompressed;
        
        sb=new StringBuilder(1024*1024);
        
        urlConn=(HttpURLConnection)webPageUrl.openConnection();
        urlConn.setDoInput(true);
        
        urlConn.setRequestProperty("host", webPageUrl.getHost());
        
        if (referer!=null) {
            urlConn.setRequestProperty("Referer", referer);
        }
        
        if (otherHeaders!=null && !otherHeaders.isEmpty()) {
            for (String key: otherHeaders.keySet()) {
                if (!key.equals("Referer")
                        || (key.equals("Referer") && referer==null))
                {
                    urlConn.setRequestProperty(key, otherHeaders.get(key));
                }
            }
        } else { 
            ConnectionHelper.setCommonRequestProperties(urlConn);
        }
        

        if (payLoad!=null) {
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            os=urlConn.getOutputStream();
            outBytes=payLoad.getBytes("utf-8");
            os.write(outBytes, 0, outBytes.length);
            os.close();
        } else {
            urlConn.setRequestMethod("GET");
        }
        
        
        is=urlConn.getInputStream();
        data=StreamUtil.readByteStream(is);
        
        contentEncoding=urlConn.getContentEncoding();
        if (contentEncoding!=null && contentEncoding.contains("gzip")) {
            decompressed=GZIPUtil.decompressToString(data);
            sb.append(decompressed);
        } else {
            sb.append(new String(data, "utf-8"));
        }
        
        is.close();
        urlConn.disconnect();
        
        return sb.toString();
    }
    
    
    public static String retrieveHtml(URL webPageUrl, String referer)
            throws IOException
    {
        return retrieveHtml(webPageUrl, referer, null);
    }
        
    
    
    public static String retrieveHtml(URL webPageUrl)
            throws Exception
    {
        return retrieveHtml(webPageUrl, null);
    }

    
    /**
     * 
     * @param source
     * @param shouldOutputProgress
     * @param readHowMuchThreshold
     * @return
     * @throws IOException 
     * @deprecated Use the version that takes otherHeaders
     */
    @Deprecated
    public static byte[] retrieveBinary(final URL source,
            final boolean shouldOutputProgress,
            final int readHowMuchThreshold)
            throws IOException
    {
        return retrieveBinary(source, shouldOutputProgress, readHowMuchThreshold, null);
    }

    /**
     * 
     * @param source
     * @param shouldOutputProgress If true, output how much is downloaded
     * at a time.
     * @param readHowMuchThreshold Relevant if progress is being output.
     * When this many bytes have been read, a message is outputted.
     * @param otherHeaders May be null.
     * @return
     * @throws IOException 
     */
    public static byte[] retrieveBinary(final URL source,
            final boolean shouldOutputProgress,
            final int readHowMuchThreshold,
            final Map<String, String> otherHeaders)
            throws IOException
    {
        HttpURLConnection connection;
        byte[] allData;
        
        connection=ConnectionHelper.connectWithRedirect(source, otherHeaders);
        
        allData=retrieveBinaryDataFromURLConnection(connection, shouldOutputProgress, readHowMuchThreshold);
        connection.disconnect();
        
        return allData;
    }
    
    
    /**
     * This is identical to retrieveBinary except the name of the downloaded
     * file (which is downloaded as a byte array) is attempted to be determined.
     * 
     * If the URL is an imgur URL, set a header for imgur.
     * 
     * @param source
     * @param shouldOutputProgress If true, output how much is downloaded
     * at a time.
     * @param readHowMuchThreshold Relevant if progress is being output.
     * When this many bytes have been read, a message is outputted.
     * @param otherHeaders May be null.
     * @return
     * @throws IOException 
     */
    public static DataWithName retrieveBinaryWithName(final URL source,
            final boolean shouldOutputProgress,
            final int readHowMuchThreshold,
            final Map<String, String> otherHeaders)
            throws IOException
    {
        
        HttpURLConnection connection;
        byte[] allData;
        DataWithName retVal;
        String fileName;
        HashMap<String, String> headersToUse;
        
        headersToUse=null;
        if (source.getHost().contains(".imgur")) {
            headersToUse=new HashMap();
            if (otherHeaders!=null) {
                headersToUse.putAll(otherHeaders);
            }
            headersToUse.putAll(imgurReferrerHeader);
        } else {
            if (otherHeaders!=null) {
                headersToUse=new HashMap();
                headersToUse.putAll(otherHeaders);
            }
        }
        
        connection=ConnectionHelper.connectWithRedirect(source, headersToUse);
        
        fileName=URLHelper.determineFileName(source.toString(), connection, true);
        
        allData=retrieveBinaryDataFromURLConnection(connection, shouldOutputProgress, readHowMuchThreshold);
        connection.disconnect();
        
        retVal=new DataWithName(fileName, allData);
        
        return retVal;
    }
    
    private static byte[] retrieveBinaryDataFromURLConnection(final HttpURLConnection connection,
            final boolean shouldOutputProgress,
            final int readHowMuchThreshold)
            throws IOException
    {
        ByteArrayOutputStream baos;
        byte[] buffer;
        int bytesRead, currentChunk;
        InputStream is;
        long totalBytes;
        
        is=connection.getInputStream();
        
        buffer=new byte[1024 * 1024];
        baos=new ByteArrayOutputStream();
        totalBytes=0L;
        currentChunk=0;
        
        while (true) {
            bytesRead=is.read(buffer);
            

            if (bytesRead==-1) {
                break;
            }
            
            currentChunk+=bytesRead;
            totalBytes+=bytesRead;
            
            if (shouldOutputProgress && currentChunk>=readHowMuchThreshold) {
                currentChunk=0;
                System.out.println("Read " + totalBytes + " so far.");
            }

            baos.write(buffer, 0, bytesRead);
        }
        
        is.close();
        
        return baos.toByteArray();
    }
}
