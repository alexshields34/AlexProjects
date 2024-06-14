package alex.pfn;

public class Constants {
	
	public static final String buttonDeactivateBackendPolling="Deactivate backend polling";
	public static final String buttonActivateBackendPolling="Activate backend polling";

	public static final String gameTitle="Alex's Poetry for Neanderthals";

	public static final Long LONG_ZERO=Long.valueOf(0L);
	public static final Integer INTEGER_ZERO=Integer.valueOf(0);
	public static final String playerIdSessionAttributeName="playerId";
	public static final String wasDisplayNameSetSessionAttributeName="wasDisplayNameSet";

	
	
	public static final String imagesDirectory = "media/";
    
    
    // The minimum number of milliseconds to wait before writing
    // to the used card storage. 
    public static final int secondsDelayBeforeWritingUsedCards=55;
    

    public static final String config_game_minimumPlayersToStartNewGame="game.minimumPlayersToStartNewGame";
    public static final String config_activityLog_client_maximum_logLines="activityLog.client.maximum.logLines";
    public static final String config_activityLog_maximum_logLines="activityLog.maximum.lines";
    public static final String config_client_millisecondsForClientToPollStatus="client.millisecondsForClientToPollStatus";
    public static final String config_client_millisecondsBeforeTimingOutAjaxCalls="client.millisecondsBeforeTimingOutAjaxCalls";
    public static final String config_game_default_number_of_rounds="game.default.numberOfRounds";
    public static final String config_game_defaultTimerSeconds="game.defaultTimerSeconds";


    
    public static final String config_dir_cards="dir.cards";
    public static final String config_card_secondsDelayBeforeWritingUsedCards="card.secondsDelayBeforeWritingUsedCards";
    public static final String config_client_team_colors="client.team.colors";
    
    
    public static final String config_client_winnerImagePrefix="client.winnerImagePrefix";
    public static final String config_client_loserImagePrefix="client.loserImagePrefix";
    public static final String config_client_drawImagePrefix="client.drawImagePrefix";
    public static final String config_client_yourTurnImagePrefix="client.yourTurnImagePrefix";
    public static final String imageDirectoryURLPath="/media/";
    public static final String config_client_upcomingTurnsToShow="client.upcomingTurnsToShow";
    public static final String soundDirectoryURLPath="media/sound";
    public static final String config_client_failureBuzzFileName="client.failureBuzzFileName";
}
