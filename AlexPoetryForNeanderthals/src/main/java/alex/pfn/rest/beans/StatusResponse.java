package alex.pfn.rest.beans;


import java.util.ArrayList;
import alex.date.ElapsedTime;
import alex.pfn.FEEvent;
import alex.pfn.GameState;

/**
 * A holder for a few values.
 * @author alex
 *
 */
public class StatusResponse {
	
	// Used for a small number of ajax calls.
	private Boolean isSuccessful=null;
	
	// Should always be set.
	private String gameId=null;
	private Integer gameIdModId=null;
	
	// Shows the fact that the game is running, or that the player is in 1st
	// place, or last place, or whatever.
	private String message=null;
	
	// For all players, including the player who called getStatus. 
	private PlayerInfo playerInfo=null;
	
	private Integer remainingSeconds=null;
	private ElapsedTime.TimerState timerState=null;
	private Integer timeTrackerModId=null;
	private Boolean hasGameEnded=null;

	private TeamInfo teamInfo=null;
	
	private ArrayList<String> logMessages=null;
	private Integer logMessagesModId=null;
	
	private ActivityLogDTO activityLog=null;
	
	private GameState gameState=null;
	
	private FEEvent frontEndEvent=null;
	
	private Long uptime=null;
	
	private String currentCardId=null;
	private CardDTO currentCard=null;
	private Integer currentCardModId=null;
	private Integer gameStateModId=null;
	private Integer scoreModId=null;
	
	
	private ScoreLogDTO scoreLog=null;
	
	

	public ScoreLogDTO getScoreLog() {
		return scoreLog;
	}
	public void setScoreLog(ScoreLogDTO scoreLog) {
		this.scoreLog = scoreLog;
	}
	public Integer getScoreModId() {
		return scoreModId;
	}
	public void setScoreModId(Integer scoreModId) {
		this.scoreModId = scoreModId;
	}
	public String getCurrentCardId() {
		return currentCardId;
	}
	public void setCurrentCardId(String currentCardId) {
		this.currentCardId = currentCardId;
	}
	public Integer getGameStateModId() {
		return gameStateModId;
	}
	public void setGameStateModId(Integer gameStateModId) {
		this.gameStateModId = gameStateModId;
	}
	public Long getUptime() {
		return uptime;
	}
	public void setUptime(Long uptime) {
		this.uptime = uptime;
	}
	public FEEvent getFrontEndEvent() {
		return frontEndEvent;
	}
	public void setFrontEndEvent(FEEvent frontEndEvent) {
		this.frontEndEvent = frontEndEvent;
	}
	public GameState getGameState() {
		return gameState;
	}
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	public Integer getTimeTrackerModId() {
		return timeTrackerModId;
	}
	public void setTimeTrackerModId(Integer timeTrackerModId) {
		this.timeTrackerModId = timeTrackerModId;
	}
	public ActivityLogDTO getActivityLog() {
		return activityLog;
	}
	public void setActivityLog(ActivityLogDTO activityLog) {
		this.activityLog = activityLog;
	}
	public Boolean getHasGameEnded() {
		return hasGameEnded;
	}
	public void setHasGameEnded(Boolean hasGameEnded) {
		this.hasGameEnded = hasGameEnded;
	}
	public ElapsedTime.TimerState getTimerState() {
		return timerState;
	}
	public void setTimerState(ElapsedTime.TimerState timerState) {
		this.timerState = timerState;
	}
	public Integer getRemainingSeconds() {
		return remainingSeconds;
	}
	public void setRemainingSeconds(Integer remainingSeconds) {
		this.remainingSeconds = remainingSeconds;
	}
	
	/**
	 * Never return null.
	 * @return
	 */
	public TeamInfo getTeamInfo() {
		if (teamInfo==null) {
			teamInfo=new TeamInfo();
		}
		return teamInfo;
	}
	
	public void setTeamInfo(TeamInfo teamInfo) {
		this.teamInfo=teamInfo;
	}
	
	
	/**
	 * Never return null.
	 * @return
	 */
	public PlayerInfo getPlayerInfo() {
		if (playerInfo==null) {
			playerInfo=new PlayerInfo();
		}
		return playerInfo;
	}
	public void setPlayerInfo(PlayerInfo playerInfo) {
		this.playerInfo = playerInfo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getIsSuccessful() {
		return isSuccessful;
	}
	public void setIsSuccessful(Boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public Integer getGameIdModId() {
		return gameIdModId;
	}
	public void setGameIdModId(Integer gameIdModId) {
		this.gameIdModId = gameIdModId;
	}
	public ArrayList<String> getLogMessages() {
		return logMessages;
	}
	public void setLogMessages(ArrayList<String> logMessages) {
		this.logMessages = logMessages;
	}
	public Integer getLogMessagesModId() {
		return logMessagesModId;
	}
	public void setLogMessagesModId(Integer logMessagesModId) {
		this.logMessagesModId = logMessagesModId;
	}
	public CardDTO getCurrentCard() {
		return currentCard;
	}
	public void setCurrentCard(CardDTO currentCard) {
		this.currentCard = currentCard;
	}
	public Integer getCurrentCardModId() {
		return currentCardModId;
	}
	public void setCurrentCardModId(Integer currentCardModId) {
		this.currentCardModId = currentCardModId;
	}
	
	
}
