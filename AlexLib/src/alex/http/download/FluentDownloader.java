package alex.http.download;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;

/**
 * Use apache's fluent api to download.
 * 
 * @author alex
 */
public class FluentDownloader {


    public static String retrieveHtml(URL webPageUrl, String referer)
            throws IOException, URISyntaxException
    {
        Request request;
        Response response;
        String retVal;
        
        request=Request.create("GET", webPageUrl.toURI());
        setCommonHeaders(request);
        if (referer!=null) {
            request.addHeader("Referer", referer);
        }
        
        response=request.execute();
        
        retVal=response.returnContent().asString();
        
        return retVal;
    }
    
    private static void setCommonHeaders(Request request) {
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        request.addHeader("cookieEnabled", "true");
        request.addHeader("connection", "keep-alive");
        request.addHeader("accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//        urlConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        request.addHeader("accept-language", "en-US,en;q=0.9");
        request.addHeader("cache-control", "max-age=0");
        request.addHeader("dnt", "1");
        request.addHeader("sec-ch-ua", "\".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103\"");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("sec-ch-ua-platform", "\"Windows\"");
        request.addHeader("sec-fetch-dest", "document");
        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-site", "none");
        request.addHeader("sec-fetch-user", "?1");
        request.addHeader("upgrade-insecure-requests", "1");
    }
    
    
    public static String retrieveHtml(URL webPageUrl)
            throws Exception
    {
        return retrieveHtml(webPageUrl, null);
    }


}
