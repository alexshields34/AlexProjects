package alex.debug;

import java.util.Properties;


/**
 *
 * @author alex
 */
public class SystemInfo {
    
    public static void outputSystemInformation() {
        DebugUtil.outputProperties(System.getProperties());
    }
    
//    public static String generateSystemInformation() {
//        StringBuilder sb;
//        Properties prop;
//        
//        sb=new StringBuilder();
//        
//        prop=System.getProperties();
//        
//        prop.
//    }
}
