package alex.pfn.rest.beans;



/**
 * A holder for a few values.
 * @author alex
 *
 */
public class ChangeTeamScoreResponse {
	
	// Should always be set.
	private String gameId=null;
	
	private String teamId=null;
	private Integer numericScore=null;
	private String scoreDescription=null;
	
	
	
	public String getScoreDescription() {
		return scoreDescription;
	}
	public void setScoreDescription(String scoreDescription) {
		this.scoreDescription = scoreDescription;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public Integer getNumericScore() {
		return numericScore;
	}
	public void setNumericScore(Integer numericScore) {
		this.numericScore = numericScore;
	}
	
		
}
