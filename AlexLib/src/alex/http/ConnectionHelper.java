
package alex.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alex
 */
public final class ConnectionHelper {
    private final static Map<String, String> commonStdHeaders;
    
    static {
        Map<String, String> map;
        map=new HashMap<>();
        
//        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        
        map.put("cookieEnabled", "true");
        map.put("connection", "keep-alive");
        map.put("accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//        urlConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        map.put("accept-language", "en-US,en;q=0.9");
        map.put("cache-control", "max-age=0");
        map.put("dnt", "1");
        map.put("sec-ch-ua", "\".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103\"");
        map.put("sec-ch-ua-mobile", "?0");
        map.put("sec-ch-ua-platform", "\"Windows\"");
        map.put("sec-fetch-dest", "document");
        map.put("sec-fetch-mode", "navigate");
        map.put("sec-fetch-site", "none");
        map.put("sec-fetch-user", "?1");
        map.put("upgrade-insecure-requests", "1");
    
        commonStdHeaders=Collections.unmodifiableMap(map);
    }
    
    
    /**
     * Given any map of headers, set them in the URLConnection as requestProperties.
     * @param hurlConn May not be null.
     * @param headers May be null.
     */
    public static void setRequestProperties(URLConnection hurlConn, Map<String, String> headers) {
        
        if (headers!=null && !headers.isEmpty()) {
            for (String key: headers.keySet()) {
                hurlConn.setRequestProperty(key, headers.get(key));
            }
        }
    }
    
    public static void setCommonRequestProperties(URLConnection hurlConn) {
        setRequestProperties(hurlConn, commonStdHeaders);
    }
    
    
    private static void setCustomOrCommonRequestProperties(HttpURLConnection hurlConn, Map<String, String> nonCommonHeaders)
    {
        if (nonCommonHeaders!=null) {
            setRequestProperties(hurlConn, nonCommonHeaders);
        } else {
            setCommonRequestProperties(hurlConn);
        }
    }
    
    /**
     * Connect to the URL, and handle http redirects. Return
     * an HttpURLConnection that is already open.
     * @param target
     * @return An open HttpURLConnection.
     * @throws IOException
     * @deprecated Use the version with otherHeaders
     */
    @Deprecated
    public static HttpURLConnection connectWithRedirect(URL target)
            throws IOException
    {
        return connectWithRedirect(target, null);
    }
    
    
        
    
    /**
     * Connect to the URL, and handle http redirects. Return
     * an HttpURLConnection that is already open.
     * @param target
     * @param otherHeaders
     * @return An open HttpURLConnection.
     * @throws IOException
     */
    public static HttpURLConnection connectWithRedirect(final URL target,
            final Map<String, String> otherHeaders)
            throws IOException
    {
        
        String locationValue;
        HttpURLConnection hurlConn;
        URL urlToOpen;
        
        urlToOpen=target;
        
        try {
            hurlConn=(HttpURLConnection)urlToOpen.openConnection();
            
            setCustomOrCommonRequestProperties(hurlConn, otherHeaders);
            
            hurlConn.connect();
            
            do {
                locationValue=hurlConn.getHeaderField("location");
                if (locationValue==null) {
                    locationValue=hurlConn.getHeaderField("Location");
                }


                if (locationValue!=null)
                {
                    hurlConn.disconnect();
                    System.out.println("There is a location set in the response from ["
                            +target.toString()
                            +"].  The location is ["
                            +locationValue
                            +"].  Redirecting to that new URL.");
                    urlToOpen=new URL(locationValue);
                    hurlConn=(HttpURLConnection)urlToOpen.openConnection();
                    setCustomOrCommonRequestProperties(hurlConn, otherHeaders);
                    hurlConn.connect();
                }
            } while (locationValue!=null);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        
        return hurlConn;
    }
    
    
        
}
