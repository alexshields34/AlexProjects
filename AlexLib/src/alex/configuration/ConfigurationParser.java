package alex.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.util.Properties;
import java.io.FileInputStream;
import java.util.Set;

/**
 * This class parses text files that have contents that are properties like:
 * a=the value of a
 * b=c
 * d=${b}
 * e=${a} ${d}
 * f=zz
 * g=f
 * h=${${g}}
 * 
 * e would resolve to the string "the value of a c".
 * h would resolve to "zz".
 * 
 * INFINITE CYCLES AREN'T DETECTED!  Examples:
 * a=${a}
 * 
 * a=${b}
 * b=${a}
 * 
 * a=c
 * b=${a}
 * c=${${b}}
 * 
 * 
 * Variables in the text file that are improperly formatted, such as "$z}",
 * are left unchanged.
 * 
 * Special variables:
 * ${self.directory} : The directory that contains the properties file that's being
 * parsed.
 * ${self.path} : The directory plus file name (the full path) of the properties
 * file that's being parsed.
 * 
 * 
 * Comments are denoted by # and those are mostly handled by the
 * java.util.Properties class.  I added support for comments after a value on a
 * line.  For example:
 * a=b #c
 * 
 * @author alex
 */
public class ConfigurationParser
{
    public static final String SELF_PATH_VAR="self.path";
    public static final String SELF_DIRECTORY_VAR="self.directory";
    
    
    private final File configFile;
    private final Properties rawProperties;
    private final Properties resolvedProperties;
    
    private final String selfPath;
    private final String selfDirectory;

    public ConfigurationParser(String configFilePath)
            throws Exception
    {
        configFile=new File(configFilePath); 
        rawProperties=new Properties();
        resolvedProperties=new Properties();
        
        selfPath=configFilePath;
        selfDirectory=configFile.getAbsoluteFile().getParent();
        
        parseProperties();
    }
    
    public Properties getParsedProperties() {
        return resolvedProperties;
    }

    private void fillRawProperties()
        throws FileNotFoundException, IOException
    {
        Set<String> propNames;
        String value;
        FileInputStream fr;

        fr=new FileInputStream(configFile);
        
        rawProperties.load(fr);
        
        fr.close();
        
        propNames=rawProperties.stringPropertyNames();
        for (String oneName: propNames) {
            value=rawProperties.getProperty(oneName);
            
            if (value.indexOf('#')!=-1) {
                value=value.substring(0, value.indexOf('#'));
                
                if (value.isEmpty()) {
                    rawProperties.remove(oneName);
                } else {
                    rawProperties.put(oneName, value.trim());
                }
            }
        }
    }
    
    
    /**
     * Make a single pass through the source string.  Evaluate a single ${} 
     * variable.
     * 
     * @param source
     * @param rawStorage
     * @param finishedStorage
     * @return 
     */
    private String evaluateOnce(final String unevaluatedString,
            final Properties rawStorage,
            final Properties finishedStorage)
    {
        int index1, index2;
        String variableName, value, retVal;
        StringBuilder sb;
        
        sb=new StringBuilder();
        retVal=unevaluatedString;
        
        // I'm starting with lastIndexOf ${ so that it would find nested variables.
        // After that index, I find the first }, which also, works for nested variables.
        
        index1=unevaluatedString.lastIndexOf("${");
        if (index1!=-1) {
            index2=unevaluatedString.indexOf("}", index1);
            if (index2!=-1) {
                variableName=unevaluatedString.substring(index1+2, index2);
                
                if (variableName.equals(ConfigurationParser.SELF_DIRECTORY_VAR)) {
                    value=this.selfDirectory;
                } else if (variableName.equals(ConfigurationParser.SELF_PATH_VAR)) {
                    value=this.selfPath;
                } else {
                    value=rawStorage.getProperty(variableName);
                }
                
                if (value==null) {
                    value=finishedStorage.getProperty(variableName);
                }
                
                if (index1>0) {
                    sb.append(unevaluatedString.substring(0, index1));
                }
                sb.append(value);
                if (index2 < unevaluatedString.length()-1) {
                    sb.append(unevaluatedString.substring(index2+1));
                }
                retVal=sb.toString();
            }
        }

        return retVal;
    }

    private void parseProperties()
            throws FileNotFoundException, IOException
    {
        int index1, index2;
        String value, newValue;
        boolean shouldRemoveKeyFromProperties;

        fillRawProperties();
        
        
        while (!rawProperties.isEmpty()) {
            for (String key: rawProperties.stringPropertyNames()) {
                shouldRemoveKeyFromProperties=false;
                value=rawProperties.getProperty(key);
                index1=value.lastIndexOf("${");
                if (index1==-1) {
                    this.resolvedProperties.put(key, value);
                    shouldRemoveKeyFromProperties=true;
                }

                if (!shouldRemoveKeyFromProperties) {
                    index2=value.indexOf("}", index1);
                    if (index2==-1) {
                        this.resolvedProperties.put(key, value);
                        shouldRemoveKeyFromProperties=true;
                    }
                }

                if (!shouldRemoveKeyFromProperties) {
                    newValue=evaluateOnce(value,
                        rawProperties,
                        resolvedProperties);

                    // Did the value change?   If it didn't, then evaluating it
                    // further will result in no changes, so move it to the
                    // finishedProperties.
                    if (value.equals(newValue)) {
                        this.resolvedProperties.put(key, value);
                        shouldRemoveKeyFromProperties=true;
                    } else {
                        rawProperties.setProperty(key, newValue);
                    }
                }

                if (shouldRemoveKeyFromProperties) {
                    rawProperties.remove(key);
                }
            }
        }
    }
	
    
    public static void main(String[] args)
            throws Exception
    {
        ConfigurationParser cp;
        Properties props;
        
        cp=new ConfigurationParser("C:/tmp/testprops.properties");
        props=cp.getParsedProperties();
        
        System.out.println("g="+props.getProperty("g"));
        
        props.store(System.out, "This is generated by ConfigurationParser");
    }
}