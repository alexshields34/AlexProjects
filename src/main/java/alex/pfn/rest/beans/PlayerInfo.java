package alex.pfn.rest.beans;

import java.util.ArrayList;

/**
 * A holder for a few player specific values.
 * @author alex
 *
 */
public class PlayerInfo {
	
	private final ArrayList<String> unassignedPlayers;
	private final ArrayList<PlayerDTO> playerList;
	private final ArrayList<String> deletedPlayers;
	
	private Integer playerDisplayNameModId=null;
	private Integer playerOtherChangesModId=null;
	private Integer unassignedPlayersModId=null;
	private Integer playerRoleModId=null;
	
	public PlayerInfo() {
		playerList=new ArrayList<PlayerDTO>();
		deletedPlayers=new ArrayList<String>();
		unassignedPlayers=new ArrayList<String>();
	}

	public ArrayList<String> getDeletedPlayers() {
		return deletedPlayers;
	}
	
	public ArrayList<PlayerDTO> getPlayerList() {
		return playerList;
	}

	public ArrayList<String> getUnassignedPlayers() {
		return unassignedPlayers;
	}

	public Integer getPlayerDisplayNameModId() {
		return playerDisplayNameModId;
	}

	public void setPlayerDisplayNameModId(Integer playerDisplayNameModId) {
		this.playerDisplayNameModId = playerDisplayNameModId;
	}

	public Integer getPlayerOtherChangesModId() {
		return playerOtherChangesModId;
	}

	public void setPlayerOtherChangesModId(Integer playerOtherChangesModId) {
		this.playerOtherChangesModId = playerOtherChangesModId;
	}

	public Integer getUnassignedPlayersModId() {
		return unassignedPlayersModId;
	}

	public void setUnassignedPlayersModId(Integer unassignedPlayersModId) {
		this.unassignedPlayersModId = unassignedPlayersModId;
	}

	public Integer getPlayerRoleModId() {
		return playerRoleModId;
	}

	public void setPlayerRoleModId(Integer playerRoleModId) {
		this.playerRoleModId = playerRoleModId;
	}



}