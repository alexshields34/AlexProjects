package alex.pfn.image;

import alex.pfn.Constants;

public enum ImagePrefixType {
	winner(Constants.config_client_winnerImagePrefix),
	loser(Constants.config_client_loserImagePrefix),
	draw(Constants.config_client_drawImagePrefix),
	yourTurn(Constants.config_client_yourTurnImagePrefix);
	
	private String configParameterForPrefix;
	
	private ImagePrefixType(String configParameterForPrefix)
	{
		this.configParameterForPrefix=configParameterForPrefix;
	}

	public String getConfigParameterForPrefix() {
		return configParameterForPrefix;
	}
}
