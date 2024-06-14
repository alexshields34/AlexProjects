package alex.pfn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import alex.date.ElapsedTime;
import alex.pfn.card.Card;
import alex.pfn.card.CardPool;
import alex.pfn.player.Player;
import alex.pfn.player.PlayerGroup;
import alex.pfn.rest.beans.StatusResponse;
import alex.pfn.score.ScoreLogEntry;
import alex.pfn.score.ScoreType;
import alex.pfn.team.TeamGroup;
import alex.pfn.rest.beans.ActivityLogDTO;
import alex.pfn.rest.beans.CardDTO;
import alex.pfn.rest.beans.PostBodyScoreChange;


/**
 * In my previous games, I put the game logic all over the place, usually
 * in TeamGroup.  GameServer really should have all or most of the game logic.
 * 
 * @author alex
 *
 */
public class GameServer {

    @Autowired
    private FEHelper feHelper;
    
    private static final Object gameServerStateMonitor=new Object();
    private static int gameIdCounter=0;
    
	private String gameId;
	private Date startTimeOfCurrentGame;
	private Date endTimeOfCurrentGame;
	
	private int secondsPerTurn;

    
    private GameState gameState;
	private int gameStateModId;
    
	private PlayerGroup playerGroup;
	private TeamGroup teamGroup;
	
	
	private Card currentCard;
	private int currentCardModId;

	// The entire reason for the existence of the previousState variable is
	// to keep track of when the game ended, and to activate an endgame
	// operation just once.
	private ElapsedTime timeTracker;
	private ElapsedTime.TimerState previousState;
	private int timeTrackerModId;

	private CardPool cardPool;
	
	private int scoreModId;
	
	
	public GameServer()
	{
		gameState=GameState.notStarted;
		gameId=null;
		playerGroup=new PlayerGroup();
		
		teamGroup=new TeamGroup(playerGroup, true);
		
		playerGroup.setTeamGroup(teamGroup);
		startTimeOfCurrentGame=endTimeOfCurrentGame=null;
		
		secondsPerTurn=Configuration.getConfigProperties().getPropertyAsInteger(Constants.config_game_defaultTimerSeconds).intValue();
		timeTracker=new ElapsedTime(secondsPerTurn);
		previousState=timeTracker.getTimerState();
		
		scoreModId=gameStateModId=timeTrackerModId=currentCardModId=0;

		cardPool=new CardPool(null);
		
	}
	
	@PostConstruct
	public void postConstruct()
	{
		System.out.println("GameServer:: postConstructor:: Setting feHelper in teamGroup and playerGroup, which is " + feHelper);
		playerGroup.setFeHelper(feHelper);
		teamGroup.setFeHelper(feHelper);
	}
	
	

	public void changeScore(String requestingPlayerId, List<PostBodyScoreChange> scoreChangeList)
	{
		synchronized (gameServerStateMonitor) {
			boolean isSuccessfulScoring;
			ScoreLogEntry sle;
			Player formerCurrentPlayer;
			
			if (playerGroup.isDirector(requestingPlayerId)) {
				
				for (PostBodyScoreChange psc: scoreChangeList) {
					
					sle=GlobalState.getScoreLog().getScoreLogEntry(psc.getId());
					formerCurrentPlayer=playerGroup.getPlayer(sle.getCurrentPlayerAtTimeOfScoringPlayerId());
					
					isSuccessfulScoring=psc.getNewScoreType()==ScoreType.normal1 || psc.getNewScoreType()==ScoreType.normal3;
					if (isSuccessfulScoring) {
						psc.getBenefitingTeams().add(formerCurrentPlayer.getTeam().getTeamId());
					} else {
						psc.getBenefitingTeams().addAll(this.teamGroup.getEveryOtherTeam(formerCurrentPlayer.getTeam().getTeamId()));
					}
				}

				GlobalState.getScoreLog().changeScore(scoreChangeList);
				GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId), "Changed ["+scoreChangeList.size()+"] scores.");
				this.scoreModId++;
			}
		}
	}
	
	
	/**
	 * Adds the current card to the used card list.
	 * @param currentCard
	 */
	private void setCurrentCard(Card currentCard) {
		synchronized (gameServerStateMonitor) {
			this.currentCard = currentCard;
			this.currentCardModId++;
			cardPool.addUsedCard(currentCard);
		}
	}


	private int getRemainingSeconds()
	{
		return this.timeTracker.getRemainingSeconds();
	}
	
	
	/**
	 * In addition to advancing the turn, whatever is the current
	 * card gets written to the score log as unscored.
	 */
	private void advanceTurn()
	{
		synchronized(gameServerStateMonitor) {
			
			GlobalState.getScoreLog().addScoredCard(this.currentCard,
					null,
					teamGroup.getCurrentPlayer().getPlayerId(),
					ScoreType.unscored,
					null);
			this.scoreModId++;

			// This moves the game to the next player.
			teamGroup.advanceTurn();
			
			if (teamGroup.hasGameEnded()) {
				this.cardPool.flushUsedCardStores();
				this.gameState=GameState.ended;
			} else {
				this.timeTrackerModId++;
				this.gameState=GameState.waitingToStartTurn;
				this.timeTracker.pause();
				this.timeTracker.reset();
				
				this.advanceCard();
				
				GlobalState.getActivityLog().addLine("New turn for player: ["+teamGroup.getCurrentPlayer().getDisplayName()+"]");
			}
		}
	}
	
	
	
	private String generateNewGameId()
	{
		String retVal;
		int num;
		
		synchronized (gameServerStateMonitor) {
			num=gameIdCounter++;
			retVal = "Game#"+num+"_"+(new java.util.Date());
			retVal = retVal.replace(' ', '_');
			
			return retVal;
		}
	}
	

	public void endCurrentPlayerTurn(String requestingPlayerId)
	{
		synchronized (gameServerStateMonitor) {
			if (playerGroup.isDirector(requestingPlayerId)) {
				GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId),
						"Ending the turn of ["+teamGroup.getCurrentPlayer().getDisplayName()+"]");
				this.advanceTurn();
			}
		}
	}
	
	/**
	 * If the game is currently running (not in an ended state),
	 * then reset it to waitingToStartTurn, and set the timer to not started yet.
	 * This method is primarily for supplantCurrentPlayer.
	 */
	private void resetTimerAndGameToWaiting()
	{
		if (this.isGameRunning()) {
			if (this.gameState==GameState.running || this.gameState==GameState.paused || this.gameState==GameState.failureHappened) {
				this.gameState=GameState.waitingToStartTurn;
				this.gameStateModId++;
			}
			
			this.timeTracker.pause();
			this.timeTracker.reset();
			timeTrackerModId++;
		}
	}

	public void supplantCurrentPlayer(String requestingPlayerId, String supplantingPlayerId)
	{
		synchronized (gameServerStateMonitor) {
			String s;
			
			if (playerGroup.isDirector(requestingPlayerId)) {

				if (teamGroup.getCurrentPlayer()!=null && !teamGroup.getCurrentPlayer().getPlayerId().equals(supplantingPlayerId)) {
					s=teamGroup.getCurrentPlayer().getDisplayName();

					resetTimerAndGameToWaiting();
					
					teamGroup.supplantCurrentPlayer(supplantingPlayerId);
					
					GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId),
							"Setting new current player: ["+teamGroup.getCurrentPlayer().getDisplayName()+"]. "
							+"Previous current player: ["+s+"]");
					
					this.advanceCard();
				}
			}
		}
	}
	
	
	/**
	 * @deprecated This should change to private or just be deleted.
	 * @return
	 */
	@Deprecated
	public PlayerGroup getPlayerGroup()
	{
		return playerGroup;
	}

	public Date getStartTimeOfCurrentGame() {
		return startTimeOfCurrentGame;
	}

	public void resumeTimer(String requestingPlayerId) {
		synchronized(gameServerStateMonitor) {
			if (teamGroup.getCurrentPlayer().getPlayerId().equals(requestingPlayerId)
					&& (this.gameState == GameState.failureHappened || this.gameState==GameState.paused)
					&& this.timeTracker.isPaused())
			{
				
				if (this.gameState==GameState.failureHappened) {
					this.advanceCard();
				}
				
				this.timeTracker.resume();
				this.timeTrackerModId++;
				this.gameState=GameState.running;
				this.gameStateModId++;
			}
		}
		
	}

	
	
	public void pauseTimer(String requestingPlayerId) {
		synchronized(gameServerStateMonitor) {
			if (teamGroup.getCurrentPlayer().getPlayerId().equals(requestingPlayerId))
			{
				this.performPauseTimer(GameState.paused);
			}
		}
	}
	
	/**
	 * Same as pauseTimer except it doesn't
	 * check that the requester is the current player.
	 */
	private void performPauseTimer(GameState newGameState) {
		synchronized(gameServerStateMonitor) {
			if (this.gameState == GameState.running
					&& this.timeTracker.isRunning())
			{
				this.timeTracker.pause();
				this.timeTrackerModId++;
				this.gameState = newGameState;
				this.gameStateModId++;
			}
		}
	}
	
	
	/**
	 * If the requesting player is the current player
	 * and the current game status is waiting
	 * then start the timer and change the game state.
	 * @param requestingPlayerId
	 */
	public void startTimer(String requestingPlayerId) {
		synchronized(gameServerStateMonitor) {
			if (teamGroup.getCurrentPlayer().getPlayerId().equals(requestingPlayerId)
					&& this.gameState == GameState.waitingToStartTurn)
			{
				this.timeTracker.start();
				this.timeTrackerModId++;
				this.gameState=GameState.running;
				this.gameStateModId++;
			}
		}
	}
	
	
	private void checkTurnStatus() {
		synchronized(gameServerStateMonitor) {
			
			if (previousState!=ElapsedTime.TimerState.ended && timeTracker.isEnded()) {
				
				advanceTurn();
				
				timeTrackerModId++;
			}
			previousState=timeTracker.getTimerState();
		}
	}

	

	private void advanceCard() {
		synchronized(gameServerStateMonitor) {
			this.setCurrentCard(cardPool.getRandomCard());
			
			this.currentCardModId++;
		}
	}
	
	
	public boolean isGameRunning()
	{
		synchronized(gameServerStateMonitor) {
			return !(this.gameState == GameState.ended
					|| this.gameState == GameState.gameAborted
					|| this.gameState == GameState.notStarted);
		}
	}

	/**
	 * 
	 * @param requestingPlayerId May not be null.
	 */
	public void abortGame(String requestingPlayerId)
	{
		synchronized(gameServerStateMonitor) {
			Player requestingPlayer;
			
			requestingPlayer = playerGroup.getPlayer(requestingPlayerId);
					
			if (requestingPlayer!=null) {
				if (requestingPlayer.isDirector()) {
					
					if (isGameRunning()) {
						this.gameState=GameState.gameAborted;
						gameStateModId++;
						this.endTimeOfCurrentGame=new Date();
						
						GlobalState.getActivityLog().addLine(requestingPlayer,
								"Aborted game");
					} else {
						GlobalState.getActivityLog().addLine(requestingPlayer,
								"Can't abort the game because the game is not running.");
					}
				} else {
					GlobalState.getActivityLog().addLine(requestingPlayer,
							"Doesn't have permission to abort the game");
				}
			} else {
				GlobalState.getActivityLog().addLine("Abort game: ["+requestingPlayerId+"] doesn't exist");
			}
		}
	}
	
	/**
	 * 
	 * @param requestingPlayerId May not be null.
	 */
	public void randomizeTeamAssignment(String requestingPlayerId)
	{
		synchronized(gameServerStateMonitor) {
			Player requestingPlayer;
			
			requestingPlayer = playerGroup.getPlayer(requestingPlayerId);
					
			if (requestingPlayer!=null) {
				if (requestingPlayer.isDirector()) {
					
					if (!isGameRunning()) {
						teamGroup.randomizeTeamAssignment(requestingPlayerId);
						
						GlobalState.getActivityLog().addLine(requestingPlayer,
								"Randomized team assignment");
					} else {
						GlobalState.getActivityLog().addLine(requestingPlayer,
								"Can't randomize team assignment because the game is running.");
					}
				} else {
					GlobalState.getActivityLog().addLine(requestingPlayer,
							"Doesn't have permission to randomize team assignment");
				}
			} else {
				GlobalState.getActivityLog().addLine("Randomize team assignment: ["+requestingPlayerId+"] doesn't exist");
			}
		}
	}
	

	/**
	 * If the selectedFileList is empty, generate a random file to use.
	 * 
	 * @param requestingPlayerId May not be null.
	 * @param maxTurnsForMembersOfLargestTeam Must be greater than 0.
	 * @param selectedFileList May not be null, but may have 0 elements.
	 */
	public void generateNewGame(final String requestingPlayerId,
			final int maxTurnsForMembersOfLargestTeam,
			final List<String> selectedFileList)
	{
		synchronized(gameServerStateMonitor) {
			Player requestingPlayer;
			String randomlyDeterminedFile;
			
			requestingPlayer = playerGroup.getPlayer(requestingPlayerId);
					
			if (requestingPlayer!=null) {
				if (requestingPlayer.isDirector()) {
					
					if (!isGameRunning()) {
						this.gameId=generateNewGameId();
						this.gameState=GameState.waitingToStartTurn;
						gameStateModId++;
						this.playerGroup.resetForNewGame(getGameId());
						this.startTimeOfCurrentGame=new Date();
						GlobalState.getScoreLog().resetForNewGame();
						scoreModId++;
						
						
						if (selectedFileList.isEmpty()) {
							randomlyDeterminedFile=feHelper.getSingleRandomCardFile();
							System.out.println(this.getClass().getName()
									+":: generateNewGame:: Randomly determined to use the file ["
									+randomlyDeterminedFile
									+"]");
							selectedFileList.add(randomlyDeterminedFile);
						}
						this.teamGroup.resetForNewGame(requestingPlayerId, maxTurnsForMembersOfLargestTeam);
	
						this.cardPool.setFilesToUse(selectedFileList);
						this.currentCard=this.cardPool.getRandomCard();
						
						currentCardModId++;
	
						GlobalState.getActivityLog().addLine(requestingPlayerId,
								"Generate new game");
				
						GlobalState.getActivityLog().addLine("NEW GAME: First team and player: "
								+ teamGroup.getCurrentPlayer().getTeam().getName()
								+ ", "
								+ teamGroup.getCurrentPlayer().getDisplayName());
					} else {
						GlobalState.getActivityLog().addLine(requestingPlayer,
								"Can't generate a new game because the game is running.");
					}
				} else {
					GlobalState.getActivityLog().addLine(requestingPlayer.getDisplayName(),
							"Tried to generate a new game but isn't a director.");
				}
			} else {
				GlobalState.getActivityLog().addLine("["
						+requestingPlayerId
						+"] tried to generate a new game but ["
						+requestingPlayerId
						+"] doesn't exist as a player.");	
			}
		}
	}
	
	
	public void addNewTeam(String requestingPlayerId, String teamName)
	{
		synchronized(gameServerStateMonitor) {
			Player requestingPlayer;
			boolean result;
			
			requestingPlayer = playerGroup.getPlayer(requestingPlayerId);
			
			if (!requestingPlayer.isDirector()) {
				GlobalState.getActivityLog().addLine(requestingPlayer,
						"Lacks director role to create new team.");
			} else {
				result = teamGroup.addNewTeam(teamName);
				
				if (result) {
	
					GlobalState.getActivityLog().addLine(requestingPlayer,
							"Added team " + teamName);
				} else {
					GlobalState.getActivityLog().addLine(requestingPlayer,
							"Failed to add team " + teamName);
				}
			}
		}
	}
	

	/**
	 * 
	 * @param requestingPlayerId May not be null.
	 * @param scoreType May not be null.
	 * @param cardId May not be null.
	 */
	public void scoreCard(final String requestingPlayerId,
			final ScoreType scoreType,
			final String cardId)
	{
		synchronized(gameServerStateMonitor) {
			ArrayList<String> benefitingTeams;
			boolean isSuccessfulScoring;
			
			// If the requesting player is the current player, then we will allow
			// any scoreType.
			// If the requesting player isn't on the current player's team,
			// then we will allow only failed as the scoreType.
			
			// If the requesting player is a director, allow any scoreType
			if (teamGroup.getCurrentPlayer()!=null
					&& this.gameState == GameState.running)
			{
				if (this.currentCard.getId().equals(cardId)) {
					isSuccessfulScoring=false;
					benefitingTeams=new ArrayList<String>();
					
					if (requestingPlayerId.equals(teamGroup.getCurrentPlayer().getPlayerId()))
					{
						isSuccessfulScoring = scoreType==ScoreType.normal1 || scoreType==ScoreType.normal3;
						if (isSuccessfulScoring) {
							benefitingTeams.add(teamGroup.getCurrentPlayer().getTeam().getTeamId());
						} else {
							benefitingTeams.addAll(this.teamGroup.getEveryOtherTeam(teamGroup.getCurrentPlayer().getTeam().getTeamId()));
						}
						GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId),
								"Scoring the card with text [" + this.currentCard.toCompactString() + "] as [" + scoreType.getPrintableString()+"]");
						
						GlobalState.getScoreLog().addScoredCard(this.currentCard, requestingPlayerId, teamGroup.getCurrentPlayer().getPlayerId(), scoreType, benefitingTeams);
						scoreModId++;
						teamGroup.addEventForScoringCard(scoreType, isSuccessfulScoring);
						
						if (scoreType == ScoreType.failed) {
							this.performPauseTimer(GameState.failureHappened);
						} else {
							advanceCard();
						}
					} else if (!teamGroup.getCurrentPlayer().getTeam().equals(playerGroup.getPlayer(requestingPlayerId).getTeam())) {
						if (scoreType == ScoreType.failed) {
							benefitingTeams.addAll(this.teamGroup.getEveryOtherTeam(teamGroup.getCurrentPlayer().getTeam().getTeamId()));
							GlobalState.getScoreLog().addScoredCard(this.currentCard, requestingPlayerId, teamGroup.getCurrentPlayer().getPlayerId(), scoreType, benefitingTeams);
							GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId),
									"Scoring the card with text [" + this.currentCard.toCompactString() + "] as [" + scoreType.getPrintableString()+"]");
							this.performPauseTimer(GameState.failureHappened);
							scoreModId++;
							teamGroup.addEventForScoringCard(scoreType, false);
						}
					}
				} else {
					GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId),
							"FAILED scoring the card with text [" + this.currentCard.toCompactString() + "] as [" + scoreType.getPrintableString()+"]");
				}
			}
		}
	}
	
	public String getGameId()
	{
		return gameId;
	}
	
	public GameState getGameState()
	{
		return gameState;
	}
	
	
	/**
	 * This method isn't synchronized in GameServer.
	 * Synchronization can happen elsewhere.
	 * @param playerIdStr
	 * @param displayName
	 */
	public void setDisplayName(String playerIdStr, String displayName)
	{

		String oldName;
		
		oldName=playerGroup.getPlayer(playerIdStr).getDisplayName();
		
		playerGroup.setDisplayName(playerIdStr, displayName);
		
		GlobalState.getActivityLog().addLine(oldName,
				"Changed name to "
						+ displayName);
		
	}
	
	
	/**
	 * Only allow deleting the team if the requestingUser is a director.
	 * Deleting a team while the game is running should probably be ok.
	 * Team members should be put into the unassigned group.
	 * 
	 * @param requestingPlayerId Should never be null.
	 * @param teamId May not be null.
	 */
	public void deleteTeam(String requestingPlayerId, String teamId)
	{
		synchronized(gameServerStateMonitor) {
			String goodString, lackRoleString, cantString;
			boolean result;
			
			if (playerGroup.isDirector(requestingPlayerId)) {
	
				cantString="FAILURE: Couldn't delete the team ["
						+ teamGroup.getTeam(teamId).getName()
						+"]";
				goodString="Deleted the team ["
						+ teamGroup.getTeam(teamId).getName()
						+"]";
				result=teamGroup.deleteTeam(teamId);
				if (result) {
					GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId), goodString);
				} else {
					GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId), cantString);
				}
			} else {
				lackRoleString= "Failed deleting the team ["
						+ teamGroup.getTeam(teamId).getName()
						+"] due to lacking director role";
				GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestingPlayerId), lackRoleString);
			}
		}
	}
	

	/**
	 * The requesting player must be non-deleted and a director.
	 * The selected players must be playing the same game as the requester.
	 * Ignore any players who are in the selected list who are deleted.
	 * The team must be non-deleted and it must exist.  If it doesn't, set
	 * an error message in the log and do nothing.
	 * 
	 * @param requestingPlayerId May not be null.
	 * @param selectedPlayerIdList May not be null.
	 * @param teamId May not be null.
	 */
	public void addTeamMembers(final String requestingPlayerId,
			final List<String> selectedPlayerIdList,
			final String teamId)
	{
		synchronized(gameServerStateMonitor) {
			Player p;
			
			p=playerGroup.getPlayer(requestingPlayerId);
			
			if (p!=null && p.isActive()) {
				if (p.isDirector()) {
			
					teamGroup.addTeamMembers(p,
							selectedPlayerIdList,
							teamId);
				} else {
					GlobalState.getActivityLog().addLine(p, "ERROR. "+p.getDisplayName()+" has no permission to add team members.");
				}
			}
		}
	}

	public void unassignPlayers(final String requestingPlayerId, 
			final List<String> selectedPlayerIdList)
	{
		synchronized(gameServerStateMonitor) {
			Player requestingPlayer;
			
			requestingPlayer=playerGroup.getPlayer(requestingPlayerId);
			
			if (requestingPlayer!=null && requestingPlayer.isActive()) {
				if (requestingPlayer.isDirector()) {
					teamGroup.unassignTeamMembers(requestingPlayer, selectedPlayerIdList);
				} else {
					GlobalState.getActivityLog().addLine(requestingPlayer,
							"ERROR. "+requestingPlayer.getDisplayName()+" has no permission to add team members.");
				}
			}
		}
	}
	

    /**
     * The current card won't always go to the front.  
     * 
     * 0. Only send the current card if the game is in waitingToStartTurn, 
     * tabooWasSaid, paused, or running.  If the game is not in one of
     * those states, do not set the current card.
     * 1. Get the current player's team.
     * 2. Don't set the card if the requesting player isn't the current 
     * player but is on the current player's team.
     * 3. Don't set the card if the requesting player == the current player and the game state 
     * is waitingToStartTurn.
     * 4. Set the current card in all other cases.
     * 
     * @param requestingPlayer
     * @return
     */
    private CardDTO generateCardDTO(final Player requestingPlayer) {
    	
    	CardDTO retVal;
    	Player currentPlayer;
    	
    	retVal=null;
    	
    	if (this.currentCard!=null) {
	    	switch (this.getGameState()) 
	    	{
	    	case paused:
	    	case running:
	    	case failureHappened:
	    	case waitingToStartTurn:
	    		
	    		currentPlayer=this.teamGroup.getCurrentPlayer();
	    		if (requestingPlayer.equals(currentPlayer)) {
	    			if (!(this.gameState==GameState.waitingToStartTurn || this.gameState==GameState.paused)) {
	        			retVal=this.currentCard.buildDTO();
	    			}
	    		} else if (requestingPlayer.getTeam()!=null && requestingPlayer.getTeam().equals(currentPlayer.getTeam())) {
	    			retVal=null;
	    		} else {
	    			retVal=this.currentCard.buildDTO();
	    		}
	    		
	    		break;
	    	default:
	    		// Do nothing.
	    	}
    	}

    	return retVal;
    }
    
    
	/**
	 * Potentially return null if the requestingPlayerId is in the deleted list
	 * or isn't in PlayerGroup.
	 * @param requesterPlayerId
	 * @param lastKnownLogLine
	 * @return
	 */
    public StatusResponse generateStatus(final String requestingPlayerId,
    		final Integer lastKnownLogLine,
    		final Integer feScoreModId)
    {
    	StatusResponse retVal;
    	Player p;
    	boolean isInvalidPlayer;
    	
    	retVal=null;
    	p=playerGroup.getPlayer(requestingPlayerId);
    	isInvalidPlayer=p==null || p.isDeleted()
    			|| (this.gameId!=null && p.getGameId()!=null && !p.getGameId().equals(this.gameId));
    	if (!isInvalidPlayer) {

    		checkTurnStatus();
    		
	    	retVal=new StatusResponse();
	    	
	    	retVal.setGameState(getGameState());
	    	retVal.setGameId(getGameId());
			retVal.setTeamInfo(teamGroup.buildTeamInfo(this.getGameState()));
	    	
		    retVal.setPlayerInfo(playerGroup.buildPlayerInfo(requestingPlayerId));
		    
		    retVal.setCurrentCardId(this.currentCard!=null?this.currentCard.getId():null);
		    retVal.setCurrentCard(generateCardDTO(p));
		    retVal.setCurrentCardModId(currentCardModId);
		    retVal.setGameStateModId(gameStateModId);
		    retVal.setScoreModId(scoreModId);
			
	    	try {
	    		retVal.setFrontEndEvent(playerGroup.getPlayer(requestingPlayerId).getFirstEvent());
	    	} catch (NullPointerException npe) {
	    		// This should happen only when the game restarts and users still have their web
	    		// browsers open to the old game.  In that case, let's ignore the exception.
	    	}
	    	
	    	retVal.setRemainingSeconds(getRemainingSeconds());
	    	retVal.setTimerState(timeTracker.getTimerState());
	    	retVal.setTimeTrackerModId(timeTrackerModId);
	    	
	    	retVal.setIsSuccessful(Boolean.TRUE);
	    	
	
	    	if (lastKnownLogLine!=null) {
	    		retVal.setActivityLog(new ActivityLogDTO(GlobalState.getActivityLog(), lastKnownLogLine+1));
	    	}
	
	    	retVal.setHasGameEnded(this.teamGroup.hasGameEnded());
	    	
	    	if (playerGroup.isDirector(requestingPlayerId)
	    			&& (feScoreModId==null || feScoreModId!=this.scoreModId))
	    	{
	    		retVal.setScoreLog(GlobalState.getScoreLog().generateScoreLogDTO());
	    	}
    	}
    	
    	return retVal;
    }
	
}
