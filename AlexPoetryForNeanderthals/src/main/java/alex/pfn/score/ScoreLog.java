package alex.pfn.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alex.pfn.card.Card;
import alex.pfn.rest.beans.PostBodyScoreChange;
import alex.pfn.rest.beans.ScoreLogDTO;
import alex.pfn.rest.beans.ScoreLogEntryDTO;

/**
 * This is a container of all scoring.  This is
 * in a single place so that it can be edited
 * easily for when players make mistakes.
 * 
 * GameServer should create just a single one of these.
 * 
 * This is the owner of the scores, and the Team class and Player classes
 * should get their scores from here.
 * 
 * @author alex
 *
 */
public class ScoreLog {
	
	private final Object threadMonitor=new Object();
	
	private final HashMap<String, Integer> scoresPerTeam;
	private final HashMap<String, PlayerCardValues> cardsPerPlayer;
	
	private final ArrayList<ScoreLogEntry> logEntries;

	
	public ScoreLog() {
		logEntries=new ArrayList<ScoreLogEntry>();
		scoresPerTeam=new HashMap<String, Integer>();
		cardsPerPlayer=new HashMap<String, PlayerCardValues>();
	}
	
	/**
	 * If not found, return null.
	 * 
	 * @param id
	 * @return
	 */
	public ScoreLogEntry getScoreLogEntry(Integer id) {
		for (ScoreLogEntry sle: this.logEntries) {
			if (id.intValue() == sle.getId()) {
				return sle;
			}
		}
		
		return null;
	}
	

	public void changeScore(List<PostBodyScoreChange> scoreChangeList)
	{
		synchronized (threadMonitor) {
			int indexCounter;
			ScoreLogEntry oneEntry, newEntry;
			boolean foundTheEntry, wasAnyChangeDone;
			
			wasAnyChangeDone=false;
			for (PostBodyScoreChange pbsc: scoreChangeList) {
				
				// Find the entry we don't want anymore.
				foundTheEntry=false;
				oneEntry=null;
				for (indexCounter=0; indexCounter<logEntries.size(); indexCounter++) {
					oneEntry=this.logEntries.get(indexCounter);
					if (oneEntry.getId()==pbsc.getId()) {
						foundTheEntry=true;
						break;
					}
				}
				
				if (foundTheEntry) {
					if (pbsc.getShouldDelete()!=null && pbsc.getShouldDelete()) {
						this.logEntries.remove(indexCounter);
					} else {
						newEntry=new ScoreLogEntry(oneEntry.getPersonWhoClickedId(),
								oneEntry.getCurrentPlayerAtTimeOfScoringPlayerId(),
								oneEntry.getCard(),
								pbsc.getNewScoreType(),
								pbsc.getNewScoreType().getNumericValue(),
								pbsc.getBenefitingTeams());
						this.logEntries.remove(indexCounter);
						this.logEntries.add(indexCounter, newEntry);
					}
					wasAnyChangeDone=true;
				}
			}

			if (wasAnyChangeDone) {
				rebuildScore();
			}
		}
	}			
				
	
	/**
	 * 
	 * @param cardId
	 * @param personWhoClicked
	 * @param currentPlayerAtTheTime
	 * @param scoreType
	 * @param teamsGainingThisNumericScore
	 */
	public void addScoredCard(Card card,
			String personWhoClicked,
			String currentPlayerAtTheTime,
			ScoreType scoreType,
			List<String> teamsGainingThisNumericScore)
	{
		synchronized (threadMonitor) {
			ScoreLogEntry sle;
			
			sle=new ScoreLogEntry(personWhoClicked, currentPlayerAtTheTime, card, scoreType, scoreType.getNumericValue(), teamsGainingThisNumericScore);
			
			this.logEntries.add(sle);
			
			updateScores(sle);
		}
	}
	
	private void rebuildScore()
	{
		// Destroy the whole score and rebuild it.
		this.cardsPerPlayer.clear();
		this.scoresPerTeam.clear();
		
		for (ScoreLogEntry sle: this.logEntries) {
			updateScores(sle);
		}
	}

	
	
	private void updateScores(ScoreLogEntry sle)
	{
		synchronized (threadMonitor) {
			Integer score;
			PlayerCardValues pv;
			
			if (sle.getTeamsBenefitingFromThisScoreLogEntry()!=null) {
				for (String teamId: sle.getTeamsBenefitingFromThisScoreLogEntry()) {
					score=this.scoresPerTeam.get(teamId);
					if (score==null) {
						score=0;
					}
					score+=sle.getNumericValueOfThisScoreLogEntry();
					scoresPerTeam.put(teamId, score);
				}
			}
			
			pv=cardsPerPlayer.get(sle.getCurrentPlayerAtTimeOfScoringPlayerId());
			if (pv==null) {
				pv=new PlayerCardValues();
				cardsPerPlayer.put(sle.getCurrentPlayerAtTimeOfScoringPlayerId(), pv);
			}
			switch (sle.getScoreType()) {
			case failed:
				pv.addToCardsFailed();
				break;
			case normal1:
				pv.addToScoredOnePointCards();
				break;
			case normal3:
				pv.addToScoredThreePointCards();
				break;
			case pass:
				pv.addToCardsFailed();
				break;
			default:
				// Do nothing.
			}
		}
	}
	
	
	public int getScoreForTeam(String teamId) {
		synchronized (threadMonitor) {
			int retVal;
			Integer val;
			
			val=this.scoresPerTeam.get(teamId);
			if (val==null) {
				retVal=0;
			} else {
				retVal=val.intValue();
			}
			
			return retVal;
		}
	}
	

	public void resetForNewGame()
	{
		synchronized (threadMonitor) {
			this.logEntries.clear();
			this.scoresPerTeam.clear();
			this.cardsPerPlayer.clear();
		}
	}
	
	public PlayerCardValues getPlayerCardValues(String playerId) {
		PlayerCardValues retVal;
		
		retVal=this.cardsPerPlayer.get(playerId);
		if (retVal==null) {
			retVal=new PlayerCardValues();
			this.cardsPerPlayer.put(playerId, retVal);
		}
		return retVal;
	}
	
	public ScoreLogDTO generateScoreLogDTO()
	{
		ScoreLogDTO retVal;
		ScoreLogEntryDTO oneEntry;
		
		retVal=new ScoreLogDTO();
		
		for (ScoreLogEntry sle: this.logEntries) {
			oneEntry = sle.generateScoreLogEntryDTO();
			retVal.getScoreLogEntryList().add(oneEntry);
		}
		
		return retVal;
	}
}
