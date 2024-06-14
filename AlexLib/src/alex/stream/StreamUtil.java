
package alex.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 *
 * @author alex
 */
public class StreamUtil {
    
    
    /**
     * Doesn't close the input stream.
     * 
     * @param is
     * @return 
     * @throws IOException
     */
    public static byte[] readByteStream(InputStream is)
            throws IOException
    {
        
        byte[] retVal;
        byte[] buffer;
        int numCharactersRead;
        ByteArrayOutputStream baos;
        
        baos=new ByteArrayOutputStream(10000);
        
        buffer=new byte[1024*1024];
        
        while (-1!=(numCharactersRead=is.read(buffer))) {
            baos.write(buffer, 0, numCharactersRead);
        }
        
        retVal=baos.toByteArray();
        baos.close();
        
        return retVal;
    }
    
    /**
     * Doesn't close the input stream.
     * 
     * @param is
     * @return 
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static String readCharacterStream(InputStream is)
            throws UnsupportedEncodingException, IOException
    {
        String retVal;
        byte[] byteContents;
        
        byteContents=readByteStream(is);
        
        retVal=new String(byteContents, "utf-8");
        
        return retVal;
    }
    
//    /**
//     * Doesn't close the input stream.
//     * 
//     * @param is
//     * @return 
//     * @throws UnsupportedEncodingException
//     * @throws IOException
//     */
//    public static String readCharacterStream(InputStream is)
//            throws UnsupportedEncodingException, IOException
//    {
//        
//        StringBuilder sb;
//        char[] buffer;
//        InputStreamReader isr;
//        int numCharactersRead;
//        
//        buffer=new char[1024*1024];
//        sb=new StringBuilder(1024*1024);
//        
//        isr=new InputStreamReader(is, "utf-8");
//        
//        while (-1!=(numCharactersRead=isr.read(buffer))) {
//            sb.append(new String(buffer, 0, numCharactersRead));
//        }
//        
//        isr.close();
//        
//        return sb.toString();
//    }
    
}
