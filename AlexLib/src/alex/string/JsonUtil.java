
package alex.string;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

/**
 *
 * @author alex
 */
public class JsonUtil {
    
    private static final ObjectMapper jsonObjectMapper;
    static {
        jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * Return a json string.  It contains just the array as a json array.  In other
     * words, it's like '["a", "b", ...]'.
     * @param list May not be null.
     * @return A json string.  It contains just the array as a json array.  In other
     * words, it's like '["a", "b", ...]'.
     */
    public static String arrayToJson(String[] list) {
    	
    	StringBuilder retVal, listChunk;
    	retVal=new StringBuilder(1000);
        listChunk=new StringBuilder(1000);
    	
    	retVal.append("[");
    	for (String oneItem: list) {
            listChunk.append("'")
                    .append(oneItem)
                    .append("', ");
    	}
        if (listChunk.length()>0) {
            listChunk.setLength(listChunk.length()-2);
            retVal.append(listChunk);
        }
    	retVal.append("]");

        return retVal.toString();
    }
    
    public static String arrayToJson(ArrayList<String> list) {
    	
    	StringBuilder retVal, listChunk;
    	retVal=new StringBuilder(1000);
        listChunk=new StringBuilder(1000);
    	
    	retVal.append("[");
    	for (String oneItem: list) {
            listChunk.append("'")
                    .append(oneItem)
                    .append("', ");
    	}
        if (listChunk.length()>0) {
            listChunk.setLength(listChunk.length()-2);
            retVal.append(listChunk);
        }
    	retVal.append("]");

        return retVal.toString();
    }
    
    
    /**
     * 
     * @param o May not be null.
     * @return
     * @throws JsonProcessingException 
     */
    public static String objectToJson(Object o)
            throws JsonProcessingException
    {
        return jsonObjectMapper.writeValueAsString(o);
    }
}
