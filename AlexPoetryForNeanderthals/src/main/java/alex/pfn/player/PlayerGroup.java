package alex.pfn.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import alex.pfn.FEEvent;
import alex.pfn.FEEventType;
import alex.pfn.FEHelper;
import alex.pfn.image.ImagePrefixType;
import alex.pfn.rest.beans.PlayerInfo;
import alex.pfn.team.TeamGroup;


public class PlayerGroup {
	private final Random random=new Random(System.currentTimeMillis());

	private final Object playerChangeBlock=new Object();
	
	private final HashMap<String, Player> allPlayers;
	private final ArrayList<String> playerOrder;
	private final ArrayList<String> deletedPlayers;
	private final ArrayList<String> unassignedPlayers;
	
	private int playerDisplayNameModId;
	private int playerOtherChangesModId;
	private int unassignedPlayersModId;
	private int playerRoleModId;

	private TeamGroup teamGroup;
	
	// Gotten from an external source.
	private FEHelper feHelper;
	

	public PlayerGroup()
	{
		allPlayers=new HashMap<String, Player>();
		playerOrder=new ArrayList<String>();
		deletedPlayers=new ArrayList<String>();
		unassignedPlayers=new ArrayList<String>();
		
		playerOtherChangesModId=-1;
		playerDisplayNameModId=-1;
		unassignedPlayersModId=-1;
		playerRoleModId=-1;
	}
	
	public void setTeamGroup(TeamGroup teamGroup) {
		this.teamGroup = teamGroup;
	}
	

	public void setFeHelper(FEHelper feHelper)
	{
		this.feHelper=feHelper;
	}
	
	
	public void setDisplayName(String playerId, String displayName)
	{
		synchronized(playerChangeBlock) {
			System.out.println(this.getClass().getName()
					+":: setDisplayName:: playerId=["
					+playerId
					+"], displayName=["
					+displayName
					+"]");
			Player p;
			
			p=allPlayers.get(playerId);
			
			if (p!=null) {
				p.setDisplayName(displayName);
				this.playerDisplayNameModId++;
			}
		}
	}
	
	/**
	 * This is only called when a new game is generated.
	 * @return
	 */
	public Player getRandomPlayer() {
		synchronized(playerChangeBlock) {
			int whichPlayer;
			
			whichPlayer=random.nextInt(this.playerOrder.size());
			
			return this.allPlayers.get(this.playerOrder.get(whichPlayer));
		}
	}

	
	/**
	 * Creates a new Player object if one doesn't exist.  Returns a player for the specified playerId,
	 * whether that Player is newly created or already extant.
	 * 
	 * If the allPlayers list is empty at the start of this method call, then the new
	 * player gets director and handler power.
	 * 
	 * If the specified playerId is in deletedPLayers, return null.
	 * 
	 * @param playerId May not be null.
	 * @param ip May be null.  If null, hsr may not be null.
	 * @param displayName May be null.  If null, the value is unchanged for this
	 * player.  If nonnull, the displayName of the player is changed to this.
	 * @param hsr May be null.  If null, ip may not be null.
	 * 
	 * @return null if the playerId is already in deletedPlayers.
	 */
	public Player getOrAddPlayer(final String playerId,
			final String ip,
			final String displayName,
			final HttpServletRequest hsr)
	{
		System.out.println(this.getClass().getName()+":: getOrAddPlayer:: ENTERING");

		Player player;
		String ipToUse=null;
		boolean shouldMakeDirector=false;

		player=null;
		synchronized(playerChangeBlock) {
			
			// If the playerId is already in the deletedPlayers list, do nothing.
			if (!deletedPlayers.contains(playerId)) {
				
				if (getAllPlayers().isEmpty()) {
					shouldMakeDirector=true;
				}
				
				player=getPlayer(playerId);
				if (player==null) {
					if (ip==null) {
						if (hsr!=null) {
							ipToUse=hsr.getRemoteAddr();
						}
					} else {
						ipToUse=ip;
					}
					player=addNewPlayer(ipToUse, playerId);
					if (displayName==null) {
						player.setDisplayName(playerId);
					}
	
					if (shouldMakeDirector) {
						giveRole(playerId, PlayerRole.director);
					}
				}
				if (displayName!=null) {
					player.setDisplayName(displayName);
				}
				
			}
		}
		return player;
	}
	
	

	/**
	 * When this method is called, there should be no extant player with the given playerId.
	 * If there is already a player with the given playerId, then throw a RuntimeException.
	 * 
	 * @param ipAddress
	 * @param playerId May not be null.
	 */
	private Player addNewPlayer(String ipAddress, String playerId) {
		synchronized(playerChangeBlock) {
			System.out.println(this.getClass().getName()
					+":: addNewPlayer:: ipAddress=["
					+ipAddress
					+"], playerId=["
					+playerId
					+"]");
			Player retVal;
			
			retVal=addNewPlayer(ipAddress, playerId, null);
			
			return retVal;
		}
	}
	
	
	public Player getPlayer(String playerId) {
		synchronized(playerChangeBlock) {
			return this.allPlayers.get(playerId);
		}
	}
	
	public HashMap<String, Player> getAllPlayers() {
		synchronized(playerChangeBlock) {
			return allPlayers;
		}
	}
	
	
	public void resetForNewGame(String gameId)
	{
		synchronized(playerChangeBlock) {
			for (Player p: this.allPlayers.values()) {
				p.resetForNewGame(gameId);
			}
			
			this.playerOtherChangesModId++;
		}
	}

	
	
//
//	/**
//	 * This method conglomerates operations.  That is, it sets the next person
//	 * as the guesser, and sets the last guesser as +1 attempt at their card.
//	 *  
//	 */
//	public void nextTurn() {
//		System.out.println(this.getClass().getName()+":: nextTurn:: ENTERING");
//		
////		Player nextPlayer;
////		int indexOfCurrentPlayer;
////		
////		nextPlayer=null;
////		
////		indexOfCurrentPlayer=-1;
////		
////		// Only perform the relevant operations if the current player is set.
////		// I need to know who is next in order
////		if (currentPlayer!=null ) {
////			if (guesser!=null) {
////				guesser.addToNumberOfFailedAttemptsWithCurrentCard();
////				guesser=null;
////			}
////			
////			indexOfCurrentPlayer=playerOrder.indexOf(currentPlayer);
////			if (indexOfCurrentPlayer+1==playerOrder.size()) {
////				nextPlayer=playerOrder.get(0);
////			} else {
////				nextPlayer=playerOrder.get(indexOfCurrentPlayer+1);
////			}
////			
////			if (nextPlayer!=null) {
////				giveRole(nextPlayer.getPlayerId(), Role.currentPlayer);
////				currentPlayer=nextPlayer;
////			}
////			
////		}
//		
//	}
//	

	
	

	
	
	public void giveRole(String playerId, PlayerRole role)
	{
		
		synchronized(playerChangeBlock) {
			Player p;
			p=this.allPlayers.get(playerId);
		
			if (p!=null) {
				p.addRole(role);
				
				playerRoleModId++;
			}
		}
	}
	
	
	
	
	/**
	 * @param playerId
	 * @param role
	 * @return
	 */
	private boolean hasRole(String playerId, PlayerRole role)
	{
		synchronized(playerChangeBlock) {
			Player p;
			boolean retVal;
			
			retVal=false;
			p=this.allPlayers.get(playerId);
			if (p!=null) {
				retVal=p.hasRole(role);
			}
			
			return retVal;
		}
	}
	
	public boolean isDirector(String playerId)
	{
		return hasRole(playerId, PlayerRole.director);
	}
	
	
	public boolean isCurrentPlayer(String playerId)
	{
		return teamGroup.isCurrentPlayer(playerId);
	}
	

	public void deletePlayer(String playerId)
	{
		synchronized(playerChangeBlock) {
			System.out.println(this.getClass().getName()+":: deletePlayer:: playerId=["
					+playerId
					+"]");
			
			Player p, playerToModify;
			
			p=allPlayers.get(playerId);
			if (p!=null) {
				deletedPlayers.add(p.getPlayerId());
				allPlayers.remove(p.getPlayerId());
				playerOrder.remove(p.getPlayerId());

				for (String playerIdToModify: playerOrder) {
					playerToModify=allPlayers.get(playerIdToModify);
					if (playerToModify.getOrder()>p.getOrder()) {
						playerToModify.setOrder(playerToModify.getOrder()-1);
					}
				}
			}
		}
	}
	
	
	

	/**
	 * Send itsYourTurn and yourTurnEnded only to the two specified ids.
	 * @param previousPlayerId May be null.
	 * @param currentPlayerId May not be null.
	 */
	public void addEventForEndOfTurn(String previousPlayerId, String currentPlayerId)
	{
		synchronized(playerChangeBlock) {
			FEEvent e;
			
			if (previousPlayerId!=null) {
				e=new FEEvent();
				e.setEventType(FEEventType.yourTurnEnded);
				this.getPlayer(previousPlayerId).addEvent(e);
			}
			e=new FEEvent();
			e.setEventType(FEEventType.itsYourTurn);
			e.setImageUrl(feHelper.getRandomImageForPrefixType(ImagePrefixType.yourTurn).getPath());
			this.getPlayer(currentPlayerId).addEvent(e);
		}
	}
	
	public void addEventForAllPlayers(FEEvent e)
	{
		synchronized(playerChangeBlock) {
			for (Player p: this.getAllPlayers().values()) {
				p.addEvent(e);
			}
		}
	}

	
	
	
	
	/**
	 * 
	 * @param e
	 * @param requestingPlayerId May be null.
	 * @param shouldIncudeForRequestingPlayer Only relevant if requestPlayerId is not null.
	 * @param shouldIncudeForCurrentPlayer 
	 */
	public void addEventForAllPlayers(FEEvent e,
			String requestingPlayerId,
			boolean shouldIncudeForRequestingPlayer,
			boolean shouldIncudeForCurrentPlayer)
	{
		synchronized(playerChangeBlock) {
			for (Player p: this.getAllPlayers().values()) {
				if (requestingPlayerId!=null && requestingPlayerId.equals(p.getPlayerId())) {
					if (shouldIncudeForRequestingPlayer) {
						p.addEvent(e);
					}
				} else if (teamGroup.getCurrentPlayer()!=null
						&& teamGroup.getCurrentPlayer().getPlayerId().equals(p.getPlayerId()))
				{
					if (shouldIncudeForCurrentPlayer) {
						p.addEvent(e);
					}
				} else { 
					p.addEvent(e);
				}
			}
		}
	}


	private Player addNewPlayer(String ipAddress, String playerId, String gameId)
	{
		synchronized(playerChangeBlock) {
			System.out.println(this.getClass().getName()
					+":: addNewPlayer:: ipAddress=["
					+ipAddress
					+"], playerId=["
					+playerId
					+"], gameId=["
					+gameId
					+"]");
			Player p;
			
			p=allPlayers.get(playerId);
			if (p!=null) {
				throw new RuntimeException("The playerId=["+playerId+"] is already assigned to ipAddress=["+ipAddress+"].");
			}
			
			p=new Player(ipAddress, playerId, null, allPlayers.size());
			p.resetForNewGame(gameId);
			
			allPlayers.put(playerId, p);
			playerOtherChangesModId++;
			unassignedPlayers.add(playerId);
			this.unassignedPlayersModId++;
			playerOrder.add(playerId);
			
			return p;
		}
	}
	
	
	
	
	/**
	 * 
	 * @param player May not be null.
	 */
	public void updateUnassignedPlayers(Player player) {
		synchronized(this.playerChangeBlock) {
			
			if (player.getTeam()==null) {
				this.unassignedPlayers.add(player.getPlayerId());
			} else {
				this.unassignedPlayers.remove(player.getPlayerId());
			}
			this.unassignedPlayersModId++;
		}
	}
	
	/**
	 * Look at the team assignment of each player.  For all players who have
	 * a team, remove them from the unassignedPlayers.  For all players who
	 * don't have a team, add them to the list.
	 */
	public void updateUnassignedPlayers() {
		synchronized(this.playerChangeBlock) {
			this.unassignedPlayers.clear();
			
			for (Player player: this.allPlayers.values()) {
				updateUnassignedPlayers(player);
			}
			this.unassignedPlayersModId++;
		}
	}
	
	
	
	/**
	 * @param requesterPlayerId
	 * @return
	 */
	public PlayerInfo buildPlayerInfo(String requesterPlayerId)
	{
		PlayerInfo retVal;
		Player p;
		
		retVal=new PlayerInfo();
		
		retVal.getDeletedPlayers().addAll(this.deletedPlayers);
		retVal.getUnassignedPlayers().addAll(this.unassignedPlayers);
		retVal.setPlayerDisplayNameModId(playerDisplayNameModId);
		retVal.setPlayerOtherChangesModId(playerOtherChangesModId);
		retVal.setUnassignedPlayersModId(unassignedPlayersModId);
		retVal.setPlayerRoleModId(playerRoleModId);
		
		
//		for (Player p: this.allPlayers.values()) {
		for (String playerId: this.playerOrder) {
			p=getPlayer(playerId);
			retVal.getPlayerList().add(p.buildDTO(isCurrentPlayer(p.getPlayerId())));
		}
		
		return retVal;
	}
}
