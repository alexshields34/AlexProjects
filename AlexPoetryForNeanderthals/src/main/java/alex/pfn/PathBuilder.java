package alex.pfn;

import java.net.URI;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import alex.http.URLHelper;


/**
 * Should eventually be put into AlexLib.
 * 
 * @author alex
 *
 */
public class PathBuilder {
	
	private static final String X_FORWARDED_HOST="x-forwarded-host";
	private static final String X_FORWARDED_PROTO="x-forwarded-proto";
	
	public static URL buildForwardedURL(HttpServletRequest hsr)
	{
		URI uri;
		URL url;
		
		try {
			uri=new URI(buildForwardedURLAsString(hsr));
			url=uri.toURL();
		} catch (Exception e) {
			url=null;
			e.printStackTrace();
		}
		
		return url;
	}
	
	
	/**
	 * Look for the x-forwarded-host and x-forwarded-proto headers.
	 * If they exist, build a URL from them.  If they don't exist,
	 * return just the requestURL without the file portion.
	 * 
	 * Include a / at the end.
	 * 
	 * @param hsr
	 * @return
	 */
	public static String buildForwardedURLAsString(HttpServletRequest hsr)
	{

		StringBuilder sb;
		String forwardedHost, forwardedProto;
		
		
		sb=new StringBuilder(300);
		
		forwardedHost = hsr.getHeader(X_FORWARDED_HOST);
		forwardedProto = hsr.getHeader(X_FORWARDED_PROTO);
		
		if (forwardedHost!=null
				&& !forwardedHost.equals("")
				&& forwardedProto!=null
				&& !forwardedProto.equals(""))
		{
			sb.append(forwardedProto)
				.append("://")
				.append(forwardedHost);
			
			if (sb.charAt(sb.length()-1)!='/') {
				sb.append('/');
			}
		} else {
			// Remove the last part of the URL.
			sb.append(URLHelper.getURLWithoutFile(hsr.getRequestURL().toString()));
			
			if (sb.charAt(sb.length()-1)!='/') {
				sb.append('/');
			}
		}

		return sb.toString();
	}
}
