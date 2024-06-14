package alex.pfn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class AppConfig {
	
	@Value("${configFile}")
    private String configFilePath;
	
    @Bean
    public GameServer getGameServer() {

		System.out.println(this.getClass().getName()+":: getGameServer():: Setting configFilePath=["+configFilePath+"]");
		alex.pfn.Configuration.setConfigFilePath(configFilePath);
		
		return new GameServer();
    }
    
    @Bean
    public FEHelper getFEHelper() {
    	return new FEHelper();
    }
    
}
