package alex.pfn;

import alex.debug.DebugUtil;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
        

/**
 * Copied from AlexLib.
 * 
 * @author alex
 *
 */
public class HttpDebugUtil {
	
    public static String dumpRequestContents(HttpServletRequest hsr) {
        StringBuilder sb, headerChunk, attrChunk;
        Enumeration<String> attrNameEnumeration, 
                headerNameEnumeration, headerValueEnumeration;
        String headerName, attrName;
        Object attrValue;
        
        headerChunk=new StringBuilder(2000);
        sb=new StringBuilder(2000);
        attrChunk=new StringBuilder(2000);
        
        
        sb.append("Attributes: {");
        attrNameEnumeration=hsr.getAttributeNames();
        while (attrNameEnumeration.hasMoreElements()) {
            attrChunk.setLength(0);
            
            // A single header.  This block ends with the chunk ending with ", "
            attrName=attrNameEnumeration.nextElement(); 
            attrValue=hsr.getAttribute(attrName);
            attrChunk.append(attrName)
                .append("=[")
                .append(String.valueOf(attrValue))
                .append("], ");
            
            sb.append(attrChunk);
        }
        if (sb.lastIndexOf(", ") == sb.length()-2) {
            sb.setLength(sb.length()-2);
        }
        sb.append("}, ");
        
        
        
        sb.append("AuthType: {")
                .append(hsr.getAuthType())
                .append("}, ");
        sb.append("CharacterEncoding: {")
                .append(hsr.getCharacterEncoding())
                .append("}, ");
        sb.append("ContentLength: {")
                .append(hsr.getContentLength())
                .append("}, ");
        sb.append("ContentType: {")
                .append(hsr.getContentType())
                .append("}, ");
        sb.append("ContextPath: {")
                .append(hsr.getContextPath())
                .append("}, ");
        
                
        
        sb.append("Headers: {");
        headerNameEnumeration=hsr.getHeaderNames();
        while (headerNameEnumeration.hasMoreElements()) {
            headerChunk.setLength(0);
            
            // A single header.  This block ends with the chunk ending with ", "
            headerName=headerNameEnumeration.nextElement(); 
            headerValueEnumeration=hsr.getHeaders(headerName);
            headerChunk.append(headerName)
                .append("=[")
                .append(DebugUtil.dumpEnumeration(headerValueEnumeration))
                .append("], ");
            
            sb.append(headerChunk);
        }
        
        if (sb.lastIndexOf(", ") == sb.length()-2) {
            sb.setLength(sb.length()-2);
        }
        sb.append("}, ");
        
        
        sb.append("Method: {")
                .append(hsr.getMethod())
                .append("}, ");
        sb.append("PathInfo: {")
                .append(hsr.getPathInfo())
                .append("}, ");    
        sb.append("PathTranslated: {")
        		.append(hsr.getPathTranslated())
        		.append("}, ");
        sb.append("RequestURI: {")
                .append(hsr.getRequestURI())
                .append("}, "); 
        sb.append("RequestURL: {")
				.append(hsr.getRequestURL())
				.append("}, ");        
        sb.append("ServletPath: {")
				.append(hsr.getServletPath())
				.append("}"); 
        
        
        return sb.toString();
    }
}
