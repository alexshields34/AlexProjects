package alex.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author alex
 */
public class GZIPUtil {
    public static byte[] decompress(byte[] data)
            throws IOException
    {
        ByteArrayInputStream bais;
        GZIPInputStream gis;
        byte[] retVal;
        
        bais=new ByteArrayInputStream(data);
        gis=new GZIPInputStream(bais);
        
        retVal=StreamUtil.readByteStream(gis);
        
        gis.close();
        bais.close();
        
        return retVal;        
    }
    
    public static String decompressToString(byte[] data)
            throws IOException
    {
        String retVal;
        
        retVal=new String(decompress(data), "utf-8");
        
        return retVal;
    }
}
