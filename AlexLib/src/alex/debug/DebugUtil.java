package alex.debug;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author alex
 */
public class DebugUtil {
//    public static void outputArray(int[] arr) {
//        for (int i; i<arr.length; i++) {
//            
//        }
//    }
//    
//    public String arrayToString(int[] arr) {
//        
//    }
    
    
    public static void outputMap(Map<String, List<String>> map) {
        List<String> valueList;

        if (map!=null) {
            for (String key: map.keySet()) {
                valueList=map.get(key);

                System.out.println("key=["+key+"], valueList=["+valueList+"].");
            }
        }
    }
    
    
    
//    public static String generatePropertyContents() {
//    }
    
    public static void outputProperties(Properties prop) {
        Enumeration<Object> objectEnum;
        Object key, value;
        StringBuilder sb;
        
        sb=new StringBuilder(1000);
        
        objectEnum=prop.keys();
        while (objectEnum.hasMoreElements()) {
            key=objectEnum.nextElement();
            value=prop.get(key);
            sb.append("key=[")
                    .append(String.valueOf(key))
                    .append("], value=[")
                    .append(String.valueOf(value))
                    .append("]\n");
        }
        
//        Set<Object> keySet;
//        StringBuilder sb;
//        
//        sb=new StringBuilder(1000);
//        keySet=prop.keySet();
//        for (Object key: keySet) {
//            sb.append("key=[")
//                    .append(String.valueOf(key))
//                    .append("], value=[")
//                    .append(String.valueOf(prop.get(key)))
//                    .append("]\n");
//        }
        
        System.out.println(sb.toString());
    }
    

    /**
     */
    public static void out(String s) {
		StringBuffer buf;
	
		buf=new StringBuffer("DebugUtil: ");
		buf.append(s);
	
		System.out.println(buf.toString());
    }

    /**
     */
    public static String buildTrace(Throwable t) {
		java.io.StringWriter sw;
		java.io.PrintWriter pw;
	
		sw=new java.io.StringWriter();
		pw=new java.io.PrintWriter(sw);
		
		t.printStackTrace(pw);
	
		try {
		    pw.close();
		    sw.close();
		} catch (java.io.IOException ioe) {
		    // ignore
		}
	
		return sw.toString();
    }
    
    
    public static String dumpEnumeration(Enumeration<String> stringEnumeration) {
        StringBuilder retVal;
        String oneValue;
        
        retVal=new StringBuilder(1000);
        
        while (stringEnumeration.hasMoreElements()) {
            oneValue=stringEnumeration.nextElement();
            retVal.append(oneValue)
                    .append(", ");
        }

        // Remove the last ", ".
        if (retVal.length()>1) {
            retVal.setLength(retVal.length()-2);
        }

        return retVal.toString();
    }
}
