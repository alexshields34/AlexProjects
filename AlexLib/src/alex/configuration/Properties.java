package alex.configuration;

/**
 *
 * @author alex
 */
public class Properties 
    extends java.util.Properties
{
    public Properties() {
        super();
    }
    
    /**
     * Can throw a RuntimeException.
     * 
     * @param propertyName
     * @return 
     */
    public Integer getPropertyAsInteger(String propertyName) {
        String s;
        Integer retVal;
        
        s=super.getProperty(propertyName);
        retVal=Integer.parseInt(s);
        
        return retVal;
    }
    
    /**
     * Can throw a RuntimeException.
     * 
     * @param propertyName
     * @return 
     */
    public Boolean getPropertyAsBoolean(String propertyName) {
        String s;
        Boolean retVal;
        
        s=super.getProperty(propertyName);
        retVal=Boolean.parseBoolean(s);
        
        return retVal;
    }
    
}
