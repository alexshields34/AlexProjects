package alex.pfn;

import alex.configuration.Properties;
import alex.configuration.ConfigurationParser;


public class Configuration
{
	private static final Object instantiateLock=new Object();
	
	private static Properties configProperties=null;
	
	public static Properties getConfigProperties() {
		return configProperties;
	}
	
	/**
	 * Only needs to be called once.
	 * @param path
	 */
	public static void setConfigFilePath(String path) {
		
		synchronized(instantiateLock) {
			if (configProperties==null) {
				ConfigurationParser cp=null;
				try {
					cp=new ConfigurationParser(path);
					configProperties=cp.getParsedProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}