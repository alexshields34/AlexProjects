package alex.pfn.rest.beans;


import alex.pfn.score.ScoreType;

public class ScoreLogEntryDTO {
	private Integer id;
	private String personWhoClickedId=null;
	private String currentPlayerAtTimeOfScoringPlayerId=null;
	private String cardText=null;
	private ScoreType scoreType=null;
	
	public ScoreLogEntryDTO() {
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPersonWhoClickedId() {
		return personWhoClickedId;
	}
	public void setPersonWhoClickedId(String personWhoClickedId) {
		this.personWhoClickedId = personWhoClickedId;
	}
	public String getCurrentPlayerAtTimeOfScoringPlayerId() {
		return currentPlayerAtTimeOfScoringPlayerId;
	}
	public void setCurrentPlayerAtTimeOfScoringPlayerId(String currentPlayerAtTimeOfScoringPlayerId) {
		this.currentPlayerAtTimeOfScoringPlayerId = currentPlayerAtTimeOfScoringPlayerId;
	}
	public String getCardText() {
		return cardText;
	}
	public void setCardText(String cardText) {
		this.cardText = cardText;
	}
	public ScoreType getScoreType() {
		return scoreType;
	}
	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}
	
	
}
