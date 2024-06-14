package alex.pfn.score;

import java.util.ArrayList;
import java.util.List;

import alex.pfn.card.Card;
import alex.pfn.rest.beans.ScoreLogEntryDTO;

/**
 * An individual line in the score log.
 * 
 * @author alex
 *
 */
public class ScoreLogEntry {
	private static int ScoreLogEntry_idCounter=1;  
	private static final Object idMonitor=new Object();
	
	
	// This is the order in which this log entry was added.  Lower numbers first.  Start at 1.
	private int id;
	private String personWhoClickedId;
	private String currentPlayerAtTimeOfScoringPlayerId;
	private Card card;
	private ScoreType scoreType;
	private int numericValueOfThisScoreLogEntry;
	private ArrayList<String> teamsBenefitingFromThisScoreLogEntry;
	
	/**
	 * 
	 * @param personWhoClickedId The person who clicked something for the card in question to get scored. May not be null.
	 * @param currentPlayerAtTimeOfScoringPlayerId Usually the same as scorerPlayerId.  May not be null.
	 * @param cardId The id of the card when scoring.  May not be null.
	 * @param scoreType  May not be null.
	 * @param numericValueOfThisScoreLogEntry 1 or 3.
	 * @param teamsBenefitingFromThisScoreLogEntry Should be a minimum of 1 team.  May not be null.
	 */
	public ScoreLogEntry(final String personWhoClickedId,
			final String currentPlayerAtTimeOfScoringPlayerId,
			final Card card,
			final ScoreType scoreType,
			final int numericValueOfThisScoreLogEntry,
			final List<String> teamsBenefitingFromThisScoreLogEntry)
	{
		this.personWhoClickedId = personWhoClickedId;
		this.currentPlayerAtTimeOfScoringPlayerId = currentPlayerAtTimeOfScoringPlayerId;
		this.card = card;
		this.scoreType = scoreType;
		this.numericValueOfThisScoreLogEntry = numericValueOfThisScoreLogEntry;
		this.teamsBenefitingFromThisScoreLogEntry=new ArrayList<String>();
		
		if (teamsBenefitingFromThisScoreLogEntry!=null) {
			this.teamsBenefitingFromThisScoreLogEntry.addAll(teamsBenefitingFromThisScoreLogEntry);
		}
		
		synchronized(idMonitor) {
			id=ScoreLogEntry_idCounter;
			
			ScoreLogEntry_idCounter++;
		}
	}
	
	
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public int getId() {
		return id;
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
	public ScoreType getScoreType() {
		return scoreType;
	}
	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}
	public int getNumericValueOfThisScoreLogEntry() {
		return numericValueOfThisScoreLogEntry;
	}
	public void setNumericValueOfThisScoreLogEntry(int numericValueOfThisScoreLogEntry) {
		this.numericValueOfThisScoreLogEntry = numericValueOfThisScoreLogEntry;
	}
	public ArrayList<String> getTeamsBenefitingFromThisScoreLogEntry() {
		return teamsBenefitingFromThisScoreLogEntry;
	}
	public void setTeamsBenefitingFromThisScoreLogEntry(ArrayList<String> teamsBenefitingFromThisScoreLogEntry) {
		this.teamsBenefitingFromThisScoreLogEntry = teamsBenefitingFromThisScoreLogEntry;
	}
	public ScoreLogEntryDTO generateScoreLogEntryDTO()
	{
		ScoreLogEntryDTO retVal;
		
		retVal=new ScoreLogEntryDTO();
		retVal.setCardText(card.getOriginalLineFromFile());
		retVal.setCurrentPlayerAtTimeOfScoringPlayerId(currentPlayerAtTimeOfScoringPlayerId);
		retVal.setId(id);
		retVal.setPersonWhoClickedId(personWhoClickedId);
		retVal.setScoreType(scoreType);
		
		return retVal;
	}
}
