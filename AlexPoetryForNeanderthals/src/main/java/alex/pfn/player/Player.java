package alex.pfn.player;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import alex.pfn.FEEvent;
import alex.pfn.GlobalState;
import alex.pfn.rest.beans.PlayerDTO;
import alex.pfn.team.Team;


public class Player
	implements java.lang.Comparable<Player>
{

	private final static AtomicInteger playerIdCounter=new AtomicInteger(0);
	private final Object dirtyLock=new Object();

	// The only reason this variable exists in this class is so that it can be
	// given to the DTO to send to the front end.
	private int order;
	
	private final HashSet<PlayerRole> roles;
	private final String playerId;
	private String ipAddress;
	private String displayName;
	
	private boolean isDeleted;
	
	// I'm a member of this team.
	private Team team;
	
	private int displayNameModId;
	private int rolesModId;
	
	// When a team gets a word correct, or if a word is passed 
	// or tabooed, then this list will have an indicator saying that the 
	// front end of 'this' player needs to have a one time indicator.
	// When this list is read, the event should be removed.  Always
	// remove from index 0.
	private final ArrayList<FEEvent> frontBoundEvents;
	
	private int turnsUsed;
	
	// This player is playing in the game with this gameId only.
	private String gameId;
	
	/**
	 * If playerId is null, then generate a new playerId.
	 * 
	 * @param ipAddress May be null.
	 * @param playerId May be null.
	 * @param displayName May be null.
	 */
	public Player(String ipAddress, String playerId, String displayName, int order)
	{

		synchronized(playerIdCounter) {
			if (playerId==null) {
				this.playerId=getNewPlayerId();
			} else {
				this.playerId=playerId;
			}
			
			frontBoundEvents=new ArrayList<FEEvent>();
			
			isDeleted=false;
			this.order=order;
			this.ipAddress=ipAddress;
			this.displayName=displayName;
			roles=new HashSet<PlayerRole>();
			turnsUsed=0;
			
			gameId=null;
		}
	}
	
	
	public int getTurnsUsed() {
		return turnsUsed;
	}

	public void addToTurnsUsed() {
		synchronized(dirtyLock) {
			this.turnsUsed++;
		}
	}


	public void addEvent(FEEvent e) {
		synchronized(dirtyLock) {
			this.frontBoundEvents.add(e);
		}
	}
	
	public FEEvent getFirstEvent() {
		synchronized(dirtyLock) {
			if (!this.frontBoundEvents.isEmpty()) {
				return this.frontBoundEvents.remove(0);
			}
			
			return null;
		}
	}
	
	
	/**
	 * A test for isActive is included in this method.
	 * @return
	 */
	public boolean isDirector() {
		return isActive() && hasRole(PlayerRole.director);
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	public boolean isActive() {
		return !isDeleted();
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * I've seen that two different players on two computers seemingly had the same player id.
	 * I expected that that shouldn't have happened with AtomicInteger, 
	 * but it happened anyway, so I added a synchronized block.
	 * @return
	 */
	public static String getNewPlayerId()
	{
		synchronized(playerIdCounter) {
			int newPlayerId;
			
			newPlayerId=Integer.valueOf(playerIdCounter.getAndAdd(1));
			
			return "Player_"+newPlayerId;
		}
	}
	
	
	
	public int getOrder() {
		return order;
	}


	public void setOrder(int order) {
		synchronized(dirtyLock) {
			this.order = order;
		}
	}


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		synchronized(dirtyLock) {
			this.displayName = displayName;
			this.displayNameModId++;
		}
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getPlayerId() {
		return playerId;
	}

	

	
	/**
	 * Don't reset:
	 * isActive
	 * the roles for players when a new game starts
	 * 
	 * @param newGameId
	 */
	public void resetForNewGame(String gameId) {
		synchronized(dirtyLock) {
			turnsUsed=0;
			this.gameId=gameId;
		}
	}
	
	public boolean hasRole(PlayerRole role) {
		return roles.contains(role);
	}
	
	public void removeRole(PlayerRole role) {
		synchronized(dirtyLock) {
			roles.remove(role);
			this.rolesModId++;
		}
	}
	
	public void addRole(PlayerRole role) {
		synchronized(dirtyLock) {
			roles.add(role);
			this.rolesModId++;
		}
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public PlayerDTO buildDTO(boolean isCurrentPlayer)
	{
		PlayerDTO retVal;
		
		retVal=new PlayerDTO();
		
		retVal.setRoles(roles);
		retVal.setRolesModId(rolesModId);
		retVal.setDisplayName(displayName);
		retVal.setDisplayNameModId(displayNameModId);
		retVal.setPlayerId(playerId);
		retVal.setTeamId(team!=null?team.getTeamId():null);
		retVal.setIsDeleted(isDeleted);
		retVal.setOrder(order);
		retVal.setIsCurrentPlayer(Boolean.valueOf(isCurrentPlayer));
		retVal.setTurnsUsed(this.turnsUsed);
		retVal.setCurrentGameId(this.gameId);
		
		retVal.setScoredOnePointCards(GlobalState.getScoreLog().getPlayerCardValues(playerId).getScoredOnePointCards());
		retVal.setScoredThreePointCards(GlobalState.getScoreLog().getPlayerCardValues(playerId).getScoredThreePointCards());
		retVal.setCardsFailed(GlobalState.getScoreLog().getPlayerCardValues(playerId).getCardsFailed());

		return retVal;
	}

	@Override
	public int hashCode() {
		return  playerId.hashCode();
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		Player other = (Player) obj;
		if (playerId == null) {
			if (other.playerId != null)
				return false;
		} else if (!playerId.equals(other.playerId)) {
			return false;
		}
		return true;
	}
	

	@Override
	public int compareTo(Player o)
	{
		return this.playerId.compareTo(o.playerId);
	}
	
}

