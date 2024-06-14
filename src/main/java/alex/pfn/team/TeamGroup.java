package alex.pfn.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import alex.pfn.Constants;
import alex.pfn.FEEvent;
import alex.pfn.FEEventType;
import alex.pfn.Configuration;
import alex.pfn.FEHelper;
import alex.pfn.GameState;
import alex.pfn.GlobalState;
import alex.pfn.image.ImagePrefixType;
import alex.pfn.player.Player;
import alex.pfn.player.PlayerGroup;
import alex.pfn.rest.beans.TeamInfo;
import alex.pfn.score.ScoreType;


/**
 * Teams and team assignment.
 * 
 * The current player is owned by this class.
 * 
 * See the comments about the teams, their size, and turn order at
 * the end of this file.
 * 
 * @author alex
 *
 */
public class TeamGroup
{
	
	// Given a value after outside of the constructor.  GameServer sets this.
	private FEHelper feHelper;
	
	private final Random random=new Random(System.currentTimeMillis());
	private final Object teamChangeBlock=new Object();
	
	private ArrayList<String> teamOrder;
	private HashMap<String, Team> allTeams;
	private ArrayList<String> deletedTeams;

	// This is an index within teamOrder.
	// This should always start at 0.
	// @deprecated Shouldn't need this anymore.
	@Deprecated
	private int currentTeamNumber;
	
	
	private int allTeamsModId;
	private int membershipModId;
	private int currentPlayerModId;
	
	private PlayerGroup playerGroup;
	
	// When any kind of team membership changes, this list must 
	// be destroyed and rebuilt.
	// This list is built to be larger than necessary.
	// It has enough elements that the first player can be randomly
	// determined, and the alternating of players happens as if everyone
	// sat in a circle.
	private final ArrayList<String> turnOrderList; 
	
	// 1 based.  So the last turn number is equal to this value.
	private int totalTurnsInGame;
	
	// Starts at 1.
	private int currentTurnNumber;
	
	// The maximum number of turns for each member of the largest team
	// for the current game.
	// The value of this is gotten when a new game is generated.
	// If the largest team has 3 people, and the max turns per person
	// is 2, then the number of turns 
	// in the game will be 2 * 3 * number_of_teams.
	private int maxTurnsForMembersOfLargestTeam;
	
	// The number of players in the team with the most people.
	private int largestTeamSize;
	
	// The number of turns that will happen if the people in the largest
	// team will have 1 turn each.
	// If the largest team has 3, and the other team has 2, then this value
	// is 6.  That is because of the alternating of whose turn it is 
	// from team to team.
	private int numberOfRoundsIfPeopleInLargestTeamHaveOneRound;
	
	// When starting a new game, this is the first player.
	// This is an index within turnOrderList.
	private int startingPlayerInTurnOrderList;
	
	// Convenience variable.
	private Player currentPlayer;
	
	
	
	
	
	/**
	 * 
	 * @param shouldCreateDefaultTeams If true, create 2 teams.
	 */
	public TeamGroup(final PlayerGroup playerGroup,
			final boolean shouldCreateDefaultTeams)
	{
		this.playerGroup=playerGroup;
		
		
		currentPlayerModId=allTeamsModId
				=membershipModId=-1;

		currentTeamNumber=0;
		teamOrder=new ArrayList<String>();
		allTeams=new HashMap<String, Team>();
		deletedTeams=new ArrayList<String>();
		turnOrderList=new ArrayList<String>();
		totalTurnsInGame=0;
		currentTurnNumber=0;
		largestTeamSize=0;
		numberOfRoundsIfPeopleInLargestTeamHaveOneRound=0;

		
		if (shouldCreateDefaultTeams) {
			addNewTeam(new Team(null, "Team 1", false));
			addNewTeam(new Team(null, "Team 2", false));
		}
		
	}
	
	/**
	 * The fact that the requestingPlayer is a director is NOT checked here.
	 * 
	 * @param requestingPlayerId May not be null.
	 * @param selectedPlayerIdList May not be null.
	 * @param teamId May not be null.
	 */
	public void addTeamMembers(final Player requestingPlayer,
			final List<String> selectedPlayerIdList,
			final String teamId)
	{
		Team t;
		Player p;
		StringBuilder sb;
		final String separator=", ";
		
		t=getTeam(teamId);
		
		if (t==null) {
			GlobalState.getActivityLog().addLine(requestingPlayer, "ERROR. Can't add team members. The team ["+teamId+"] doesn't exist.");
		} else if (t.isDeleted()) {
			GlobalState.getActivityLog().addLine(requestingPlayer, "ERROR. Can't add team members. The team ["+t.getName()+"] is deleted.");
		} else {
			sb = new StringBuilder(100);
			for (String playerId: selectedPlayerIdList) {
				p=playerGroup.getPlayer(playerId);
				this.addTeamMember(t, p);
				
				sb.append(p.getDisplayName())
					.append(separator);
			}
			if (sb.length()>0) {
				sb.setLength(sb.length()-separator.length());
			}

			GlobalState.getActivityLog().addLine(requestingPlayer, "Added ["+sb+"] to team ["+t.getName()+"]");
		}
	}
	

	public void unassignTeamMembers(final Player requestingPlayer,
			final List<String> selectedPlayerIdList)
	{
		StringBuilder sb;
		Player p;
		final String separator=", ";
		
		sb=new StringBuilder(100);
		
		for (String playerId: selectedPlayerIdList) {
			p=playerGroup.getPlayer(playerId);
			
			unassignPlayer(p);
			
			sb.append(p.getDisplayName())
				.append(separator);
		}
		if (sb.length()>0) {
			sb.setLength(sb.length()-separator.length());
		}
		GlobalState.getActivityLog().addLine(requestingPlayer, "Unassigned the players ["+sb+"]");
	}
	
	
	
	
	/**
	 * Called by GameServer.
	 * @param feHelper
	 */
	public void setFeHelper(FEHelper feHelper)
	{
		this.feHelper=feHelper;
	}

	/**
	 * Return the expected players who will have upcoming turns.  Return only
	 * Constants.config_client_upcomingTurnsToShow playerIds.
	 * @param gs
	 * @return
	 */
//	public ArrayList<String> getPlayerIdsForUpcomingTurns(GameState gs)
	public ArrayList<String> getPlayerIdsForUpcomingTurns()
	{
		ArrayList<String> retVal;
		String playerId;
		int numSubsequentTurns, maximumUpcomingTurns, startIndex, endIndex;
		
		retVal=new ArrayList<String>();
		
		numSubsequentTurns= getNumberOfRemainingTurnsIncludingCurrent() -1 ;
		maximumUpcomingTurns=Configuration.getConfigProperties().getPropertyAsInteger(Constants.config_client_upcomingTurnsToShow);
		
		// startIndex is the index of the first subsequent turn. It doesn't
		// include the current turn.  currentTurnNumber starts at 1, so that
		// means that simply adding currentTurnNumber will move past the
		// first player.
		startIndex=startingPlayerInTurnOrderList + currentTurnNumber;
		endIndex=startIndex + Math.min(numSubsequentTurns, maximumUpcomingTurns);
		
		for (int i=startIndex; i<endIndex; i++) {
//				if (numSubsequentTurns > 1 && index<turnOrderList.size()) {
				playerId = this.turnOrderList.get(i);
				retVal.add(playerId);
//				}
		}
			
		
		
		return retVal;
	}
	
	public int getNumberOfRemainingTurnsIncludingCurrent()
	{
		int retVal;
		
		retVal=this.totalTurnsInGame - (this.currentTurnNumber - 1);
		retVal = Math.max(0, retVal);
		
		return retVal;
	}
	
	
	/**
	 * Return true if each player has had their turns.
	 */
	public boolean hasGameEnded() {
		return currentTurnNumber > this.totalTurnsInGame;
	}
	
	
	/**
	 * 
	 * @param playerId May not be null.
	 * @return
	 */
	public boolean isCurrentPlayer(String playerId) {
		return this.currentPlayer!=null && this.currentPlayer.getPlayerId().equals(playerId);
	}
	
	public boolean isCurrentTeam(String teamId) {
		return currentPlayer!=null && currentPlayer.getTeam().getTeamId().equals(teamId);
	}
	
	public void supplantCurrentPlayer(String supplantingPlayerId)
	{	
		synchronized(teamChangeBlock) {
			if (this.currentPlayer!=null && !this.currentPlayer.getPlayerId().equals(supplantingPlayerId)) {
				this.currentPlayer=playerGroup.getPlayer(supplantingPlayerId);
				this.currentPlayerModId++;
			}
		}
		
	}

	/**
	 * 
	 * @param maxTurnsForMembersOfLargestTeam
	 */
	private void rebuildTurnOrderList(final int maxTurnsForMembersOfLargestTeam)
	{
		int playerIndex, teamOrderIndex, teamSize;
		Team oneTeam;
		String playerId;
		
		// The number of members in this list is equal to the number of teams.
		// This is where we keep track of the current person we're looking at
		// in each iteration of trying to find a player to put into the turn list.
		// The index of each element corresponds to the index in the teamOrder list.
		// The value in this is the index of a player in a team.
		ArrayList<Integer> currentPlayerPerTeam;
		
		// Find the largest team.
		// Calculate:
		//    numberOfRoundsIfPeopleInLargestTeamHaveOneRound = number_of_teams * number_of_people_in_largest_team
		//
		//    totalTurnsInGame = numberOfRoundsIfPeopleInLargestTeamHaveOneRound * maxTurnsForMembersOfLargestTeam

		
		this.maxTurnsForMembersOfLargestTeam=maxTurnsForMembersOfLargestTeam;
		this.turnOrderList.clear();
		
		currentPlayerPerTeam = new ArrayList<Integer>();
		
		this.largestTeamSize=0;
		for (String teamId: this.teamOrder) {
			oneTeam=this.allTeams.get(teamId);
			largestTeamSize=Math.max(oneTeam.getPlayerIdList().size(), largestTeamSize);
			
			currentPlayerPerTeam.add(Constants.INTEGER_ZERO);
		}
		
		// The number of rounds if people in the largest team have one round =
		//    number of people in the largest team
		//    times the number of teams
		// Multiplying the number of people by the number of teams means
		// every player gets at least one turn.
		numberOfRoundsIfPeopleInLargestTeamHaveOneRound = this.teamOrder.size() * largestTeamSize;
		
		totalTurnsInGame = numberOfRoundsIfPeopleInLargestTeamHaveOneRound * maxTurnsForMembersOfLargestTeam;

		
		for (int doItAFewTimes=0; doItAFewTimes < (maxTurnsForMembersOfLargestTeam+1); doItAFewTimes++)
		{
			for (int numberOfAssigneesPerTeam=0; numberOfAssigneesPerTeam<largestTeamSize; numberOfAssigneesPerTeam++)
			{
				
				teamOrderIndex=0;
				for (String teamId: this.teamOrder) {
					oneTeam = this.allTeams.get(teamId);
					
					// Get the playerId of a player and store it in turnOrderList.
					playerIndex = currentPlayerPerTeam.get(teamOrderIndex).intValue();
					playerId = oneTeam.getPlayerIdList().get(playerIndex);
					teamSize = oneTeam.getPlayerIdList().size();
					this.turnOrderList.add(playerId);
					
					
					// Store the next index of a player in the currentPlayerPerTeam list.				
					playerIndex++;
					if (playerIndex >= teamSize) {
						playerIndex=0;
					}
					currentPlayerPerTeam.set(teamOrderIndex, Integer.valueOf(playerIndex));
					
					teamOrderIndex++;
				}
			}
		}
	}

	
	/**
	 * @param gameId Should not be null.
	 * @param maxTurnsForMembersOfLargestTeam This controls how many turns total there are in the game.
	 * The team with the largest number of members will determine how many turns total there will be.  If a
	 * team has 3 people, and another has 2, and the turns for the largest team is 1, then the total turns is 6.
	 * Someone on the smaller team will go twice.
	 */
	public void resetForNewGame(final String gameId,
			final int maxTurnsForMembersOfLargestTeam)
	{
		
		synchronized (teamChangeBlock) {
			String playerId;
			
			this.allTeamsModId++;
			this.currentPlayerModId++;
			this.membershipModId++;
			
//			this.cardPool.setFilesToUse(selectedFileList);
			
			// Global values must be set here.
			rebuildTurnOrderList(maxTurnsForMembersOfLargestTeam);
			
			this.currentTurnNumber=1;
			this.startingPlayerInTurnOrderList=this.random.nextInt(this.numberOfRoundsIfPeopleInLargestTeamHaveOneRound);
			playerId = this.turnOrderList.get(startingPlayerInTurnOrderList);
			currentPlayer = playerGroup.getPlayer(playerId);
			
			
			for (Team t: this.allTeams.values()) {
				t.resetForNewGame(gameId, feHelper.getRandomImageForAllVictoryTypes());
			}
			
			
		}
	}
	
	/**
	 * Never send a success event to the current player.  Send the event for all other players.
	 * 
	 * @param requestingPlayerId
	 * @param shouldIncludeRequestingPlayer
	 * @param isSuccess
	 */
	public void addEventForScoringCard(ScoreType scoreType, boolean isSuccess)
	{
		FEEvent fee;
		FEEventType feet;
		
		fee=new FEEvent();
		
		switch (scoreType) {
		case normal1:
			feet=FEEventType.successfullyScoredCard;
			break;
		case normal3:
			feet=FEEventType.successfullyScoredCard3;
			break;
		default:
			feet=FEEventType.failedWithCard;
		}
		
		fee.setEventType(feet);
		playerGroup.addEventForAllPlayers(fee, null, false, !isSuccess);
	}
	
	
	public void sendEventsForNewGame(String gameId, String requestingPlayerId) 
	{

		FEEvent e;

		// Tell all frontends that a new game was initiated.
		e=new FEEvent();
		e.setIsNewGame(Boolean.TRUE);
		playerGroup.addEventForAllPlayers(e, requestingPlayerId, false, true);
		
		

		e=new FEEvent();
		e.setEventType(FEEventType.itsYourTurn);
		e.setImageUrl(feHelper.getRandomImageForPrefixType(ImagePrefixType.yourTurn).getPath());
		currentPlayer.addEvent(e);
		
		
	}
	
	
	
	public Player getCurrentPlayer()
	{
		return this.currentPlayer;
	}
	
	
	
	/**
	 * If there is fewer than 1 team, or if there are fewer
	 * than 1 team member per team, then a RuntimeException will be thrown.
	 * 
	 * Advances to the next player.
	 */
	public void advanceTurn() {
		synchronized(teamChangeBlock) {
			String playerId, previousPlayerId;
			Player previousPlayer;
			
			if (getCurrentPlayer()!=null) {
				previousPlayer=getCurrentPlayer();
				previousPlayer.addToTurnsUsed();
				previousPlayerId=previousPlayer.getPlayerId();
			} else {
				previousPlayerId=null;
			}
			
			this.currentTurnNumber++;
			currentPlayerModId++;
			playerId = this.turnOrderList.get(startingPlayerInTurnOrderList + currentTurnNumber - 1);
			currentPlayer = playerGroup.getPlayer(playerId);
			
			
			if (this.hasGameEnded()) {
				addEvent(FEEventType.gameEnd);
			} else {
				playerGroup.addEventForEndOfTurn(previousPlayerId, currentPlayer.getPlayerId());
			}
		}
	}
	
	
	/**
	 * Return true if the team has been deleted.
	 * 
	 * Don't throw exceptions.  Catch any exception and return false
	 * if an exception happens.
	 * 
	 * @param requestingPlayerId May not be null.
	 * @param teamId May not be null.
	 */
	public boolean deleteTeam(String teamId)
	{
		synchronized(teamChangeBlock) {
			Team team;
			boolean retVal;
			
			try {
				System.out.println(this.getClass().getName() + ":: deleteTeam:: Deleting team ["+teamId+"]");
				team=allTeams.get(teamId);
				
				retVal=team.delete();
				if (retVal) {
					this.playerGroup.updateUnassignedPlayers();
					
					this.deletedTeams.add(team.getTeamId());
					this.teamOrder.remove(team.getTeamId());
					this.allTeams.remove(team.getTeamId());
					// TODO: turnOrderList should be updated here.
					
					allTeamsModId++;
					this.membershipModId++;
	
					if (currentTeamNumber>=this.allTeams.size()) {
						currentTeamNumber=0;
					} else {
						currentTeamNumber++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				retVal=false;
			}
			
			return retVal;
		}
	}
	
	
	/**
	 * Get a list of teams excluding the given team argument.
	 * @param t
	 * @return
	 */
	public List<String> getEveryOtherTeam(String teamId) {
		List<String> retVal;
		
		retVal=new ArrayList<String>();
		
		for (Team oneTeam: this.allTeams.values()) {
			if (oneTeam.getTeamId().equals(teamId)) {
				continue;
			}
			
			retVal.add(oneTeam.getTeamId());
		}
		
		return retVal;
	}
	
	
//	/**
//	 * Return true if everything worked fine.  False otherwise.
//	 * 
//	 * Do NOT advance the card.
//	 * 
//	 * TODO: Make sure that the requesting player is the current player if scoreType==normal.
//	 * @param requestingPlayerId May be null.
//	 * @param scoreType
//	 * @param cardId Might be null.  This operation will only occur if the cardId
//	 * specified here matches the current card's cardId. 
//	 * @return Always return true.  Eventually get rid of this.
//	 */
//	private boolean scoreCard(final String requestingPlayerId,
//			final ScoreType scoreType,
//			final String cardId)
//	{
//		boolean retVal, areInputsGood, isCardGood, isCurrentPlayerGood, isCurrentPlayerValid;
//		
//		retVal=false;
//		
////		synchronized(teamChangeBlock) {
////			ArrayList<Team> scoringTeams;
////			
////			isCardGood=isCurrentPlayerGood=false;
////
////			// Only people who are not deleted may perform this operation.
////			isCurrentPlayerValid = !this.playerGroup.getPlayer(requestingPlayerId).isDeleted();
////			
////			if (isCurrentPlayerValid) {
////				isCardGood=(this.getCurrentCard()!=null
////						&& this.getCurrentTeamAndPlayer()!=null
////						&& this.getCurrentCard().getId().equals(cardId));
////				
////				// If the scoreType is normal or pass, then the player doing
////				// this must be the current player.
////				isCurrentPlayerGood = (((scoreType==ScoreType.normal || scoreType==ScoreType.pass)
////							&& this.getCurrentTeamAndPlayer().getPlayer().getPlayerId().equals(requestingPlayerId))
////							|| scoreType!=ScoreType.normal);
////				
////				// Also allow the director to score a card.
////				isCurrentPlayerGood = isCurrentPlayerGood || this.playerGroup.getPlayer(requestingPlayerId).isDirector();
////			}
////					
////			
////			areInputsGood = isCurrentPlayerValid && isCardGood && isCurrentPlayerGood;
////			
////			if (areInputsGood)
////			{
////				scoringTeams=new ArrayList<Team>();
////				addEventForCard(scoreType, false);
////				
////				switch (scoreType) {
////				case normal:
////					scoringTeams.add(this.getCurrentTeam());
////					//TODO: CHANGE FOR 3 POINTS OR 1 POINT
//////					this.getCurrentTeamAndPlayer().getPlayer().addToCardsScored();
////					break;
////				case pass:
////				case taboo:
////				default:
////					scoringTeams.addAll(this.getEveryOtherTeam(this.getCurrentTeam()));
////					this.getCurrentTeamAndPlayer().getPlayer().addToCardsFailed();
////					break;
////				}
////				
////				
////				for (Team oneScoringTeam: scoringTeams) {
////					oneScoringTeam.getScore().scoreCard(this.getCurrentCard(),
////							scoreType,
////							this.getCurrentTeamAndPlayer().getPlayer());
////				}
////				
////				scoreModId++;
////				
////				retVal=true;
////			}
////		}
//		
//		return retVal;
//	}
	
	
	private void addEventForCard(final ScoreType scoreType, final boolean shouldIncudeForCurrentPlayer)
	{
		FEEvent e;
		
		e=new FEEvent();
		e.setEventType(FEEventType.successfullyScoredCard);
		if (scoreType==ScoreType.normal1 || scoreType==ScoreType.normal3) {
			e.setSuccess(Boolean.TRUE);
		} else {
			e.setFailure(Boolean.TRUE);
		}
		
		playerGroup.addEventForAllPlayers(e, null, false, shouldIncudeForCurrentPlayer);
	}
	
	/**
	 * For gameEnd or turnEnd. Send an event to all players on all teams.
	 * 
	 * @param eventType
	 */
	private void addEvent(final FEEventType eventType)
	{
		FEEvent e;
		ImagePrefixType victoryType, winningVictoryType;
		List<String> winningTeamIds;

		winningTeamIds=null;
		
		e=new FEEvent();
		e.setEventType(eventType);

		winningVictoryType=ImagePrefixType.winner;
		if (this.hasGameEnded()) {

			winningTeamIds=this.getWinningTeamIds();
			if (winningTeamIds.size()>=2) {
				winningVictoryType=ImagePrefixType.draw;
			}
		}
		for (Team oneTeam: this.allTeams.values()) {
			if (winningTeamIds!=null && winningTeamIds.contains(oneTeam.getTeamId())) {
				victoryType=winningVictoryType;
			} else {
				victoryType=ImagePrefixType.loser;
			}
			oneTeam.addEventForAllPlayers(e, victoryType);
		}
		
	}
	

	
	
	public void randomizeTeamAssignment(String requestingPlayerId) {
		ArrayList<Team> teams;
		ArrayList<Player> players;
		Player player;
		Team team;
		int membersPerTeam, remainder, counter;
		
		synchronized(teamChangeBlock) {
			
			if (playerGroup.isDirector(requestingPlayerId)) {
				teams=new ArrayList<Team>();
				players=new ArrayList<Player>();
				teams.addAll(this.allTeams.values());
				players.addAll(playerGroup.getAllPlayers().values());
				
				membersPerTeam=(players.size() / this.teamOrder.size());
				remainder=(players.size() % this.teamOrder.size());
				
				
				while (!teams.isEmpty()) {
					team=teams.remove(random.nextInt(teams.size()));
					
					counter=0;
					while (counter<membersPerTeam) {
						player=players.remove(random.nextInt(players.size()));
						addTeamMember(team, player);
						counter++;
					}
				}

				// The remainder of players get assigned to teams in team order.
				if (remainder>0) {
					for (String teamId: this.teamOrder)
					{
						if (players.isEmpty()) {
							break;
						}
						team=allTeams.get(teamId);
						player=players.remove(random.nextInt(players.size()));
						addTeamMember(team, player);
	
					}
				}
				
			}
		}
	}
	
	
	
	/**
	 * Find if the team with that name already exists.  
	 * All team names must be unique.  If there is already a team with that
	 * name, return false. If the team gets created, return true.
	 * @param teamName
	 * @return
	 */
	public boolean addNewTeam(String teamName)
	{
		boolean retVal;
		Team newTeam;
		
		synchronized(teamChangeBlock) {
			retVal=true;
			teamName=teamName.trim();
			for (Team t: this.allTeams.values()) {
				if (t.getName().equalsIgnoreCase(teamName)) {
					retVal=false;
					break;
				}
			}
			
			if (retVal) {
				newTeam=new Team(null, teamName);
				addNewTeam(newTeam);
			}
			return retVal;
		}
	}

	/**
	 * Increments allTeamsModId.
	 * 
	 * @param t
	 */
	public void addNewTeam(Team t) {
		synchronized(teamChangeBlock) {
			teamOrder.add(t.getTeamId());
			allTeams.put(t.getTeamId(), t);
			allTeamsModId++;
		}
	}
	
	

	
	
	public int getMembershipModId() {
		return membershipModId;
	}

	public ArrayList<String> getTeamOrder() {
		return teamOrder;
	}

	public HashMap<String, Team> getAllTeams() {
		return allTeams;
	}

	public ArrayList<String> getDeletedTeams() {
		return deletedTeams;
	}

	public Team getCurrentTeam() {
		synchronized(teamChangeBlock) {
			return this.currentPlayer.getTeam();
		}
	}
	
	
	
	public Team getTeam(String teamId) {
		return this.allTeams.get(teamId);
	}
	

	
	
	public void addTeamMember(Team team, Player player) {
		synchronized(teamChangeBlock) {
			
			if (player.getTeam()!=null) {
				player.getTeam().removePlayer(player);
			}
			player.setTeam(team);
			
			team.addPlayer(player);
			playerGroup.updateUnassignedPlayers(player);
			membershipModId++;
		}
	}
	
	/**
	 * Remove the specified player from her team, and put her in the unassigned group.
	 * Don't check for permissions here.
	 * 
	 * @param playerId
	 */
	public void unassignPlayer(String playerId)
	{
		synchronized(teamChangeBlock) {
			Player player;
			
			player=playerGroup.getPlayer(playerId);
			unassignPlayer(player);
		}
	}
	
	public void unassignPlayer(Player player)
	{
		synchronized(teamChangeBlock) {
			if (player.getTeam()!=null) {
	
				player.getTeam().removePlayer(player);
				player.setTeam(null);
				
				playerGroup.updateUnassignedPlayers(player);
				membershipModId++;
				allTeamsModId++;
			}
		}
	}
	
	
	/**
	 * Remove the specified player from her team, and put her in the unassigned group.
	 * @param requestingPlayerId
	 * @param playerId
	 */
	public void unassignPlayer(String requestingPlayerId, String playerId)
	{
		synchronized(teamChangeBlock) {
			Player player;
			
			if (playerGroup.isDirector(requestingPlayerId)) {
				player=playerGroup.getPlayer(playerId);
				if (player.getTeam()!=null) {
	
					player.getTeam().removePlayer(player);
					player.setTeam(null);
					
					playerGroup.updateUnassignedPlayers(player);
					membershipModId++;
					allTeamsModId++;
				}
			}
		}
	}
	
	
	
	public void addTeamMember(String requestinPlayerId, String playerId, String teamId) {
		synchronized(teamChangeBlock) {
			Player player;
			Team team;
			
			if (playerGroup.isDirector(requestinPlayerId)
					&& playerId!=null)
			{
				
				player=this.playerGroup.getPlayer(playerId);
				team=this.getTeam(teamId);
				
				addTeamMember(team, player);
				

				GlobalState.getActivityLog().addLine(playerGroup.getPlayer(requestinPlayerId),
						"Added "
								+ playerGroup.getPlayer(playerId).getDisplayName()
								+ " to "
								+ getTeam(teamId).getName());
			}
		}
		
		

	}
	
	
	/**
	 * Return all of the highest scoring teams.  This will return just
	 * a single team if there is one team that's scored higher than the others.
	 * Return more than one team in case of a tie.
	 * @return
	 */
	public List<Team> getWinningTeams() {
		int highestScore=0;
		ArrayList<Team> retVal;
		int currentTeamScore=0;
		
		retVal=new ArrayList<Team>();
	
//		for (Team oneTeam: this.allTeams.values()) {
//			currentTeamScore=oneTeam.getScore().getNumericScore();
//			if (currentTeamScore>highestScore) {
//				highestScore=currentTeamScore;
//				retVal.clear();
//				retVal.add(oneTeam);
//			} else if (currentTeamScore==highestScore) {
//				retVal.add(oneTeam);
//			}
//		}
		
		return retVal;
	}
	
	
	public List<String> getWinningTeamIds() {
		ArrayList<String> retVal;
		
		retVal=new ArrayList<String>();
		for (Team oneTeam: getWinningTeams()) {
			retVal.add(oneTeam.getTeamId());
		}
		
		return retVal;
	}
	

	public TeamInfo buildTeamInfo(GameState gs) {
		TeamInfo retVal;
		Team team;
		
		retVal=new TeamInfo();
		

		retVal.setTeamListModId(allTeamsModId);
		retVal.setCurrentPlayerModId(currentPlayerModId);
		retVal.setTeamMembershipModId(membershipModId);
		
		
		retVal.getDeletedTeams().addAll(this.getDeletedTeams());
		retVal.setTeamMembershipModId(this.membershipModId);
		
		
		retVal.setCurrentPlayerId(this.currentPlayer!=null?this.currentPlayer.getPlayerId():null);
		retVal.setCurrentTeamId((this.currentPlayer!=null && this.currentPlayer.getTeam()!=null)?this.currentPlayer.getTeam().getTeamId():null);
		
		for (String teamId: teamOrder) {
			team=this.allTeams.get(teamId);
			retVal.getTeamList().add(team.buildTeamDTO(isCurrentTeam(teamId)));
		}
		
		
		if (this.hasGameEnded()) {
			retVal.setWinningTeamIds(this.getWinningTeamIds());
		}
		
//shouldn't need gamestate.
//    	retVal.setUpcomingTurns(getPlayerIdsForUpcomingTurns(gs));
//    	retVal.setRemainingTurnsIncludingCurrent(getNumberOfRemainingTurnsIncludingCurrent(gs));
    	retVal.setUpcomingTurns(getPlayerIdsForUpcomingTurns());
    	retVal.setRemainingTurnsIncludingCurrent(getNumberOfRemainingTurnsIncludingCurrent());

		
		return retVal;
	}
}


/*
Turn order and how it alternates.

Example teams with different sizes.
Team A
APlayer_1
APlayer_3
APlayer_4

Team B
BPlayer_2
BPlayer_5

Some values:

largestTeamSize = 3
numberOfRoundsIfPeopleInLargestTeamHaveOneRound = 6 (which is largestTeamSize * number_of_teams)

If maxTurnsForMembersOfLargestTeam = 2, then the content of turnOrderList would be this:
APlayer_1
BPlayer_2
APlayer_3
BPlayer_5
APlayer_4
BPlayer_2
APlayer_1
BPlayer_5
APlayer_3
BPlayer_2
APlayer_4
BPlayer_5
APlayer_1
BPlayer_2
APlayer_3
BPlayer_5
APlayer_4
BPlayer_2

The index of the first player should be randomly determined
between 0 and numberOfRoundsIfPeopleInLargestTeamHaveOneRound-1, inclusive

Let's say the first player is APlayer_3.  The resultant turns would be this:
APlayer_3
BPlayer_5
APlayer_4
BPlayer_2
APlayer_1
BPlayer_5
APlayer_3
BPlayer_2
APlayer_4
BPlayer_5
APlayer_1
BPlayer_2



*/