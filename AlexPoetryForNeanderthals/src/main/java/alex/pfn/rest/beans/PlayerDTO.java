package alex.pfn.rest.beans;

import java.util.Collection;

import alex.pfn.player.PlayerRole;

/**
 * This is for sending data to the front end.
 * It seems like this class could be the data holder within the Player class
 * but I'd rather keep the DTOs separate from the working classes.
 * 
 * @author alex
 *
 */
public class PlayerDTO {

	private String playerId=null;
	private String displayName=null;
	private Integer order=null;
	private String teamId=null;
	private Collection<PlayerRole> roles=null;
	private String currentGameId=null;
	private Boolean isDeleted=null;
	
	private Integer displayNameModId=null;
	private Integer teamIdModId=null;
	private Integer rolesModId=null;
	
	private Integer turnsUsed=null;
	
	private Integer scoredThreePointCards=null;
	private Integer scoredOnePointCards=null;
	private Integer cardsFailed=null;
	
	private Boolean isCurrentPlayer=null;
	
	
	
	public PlayerDTO() {
	}
	
	
	
	public Integer getScoredThreePointCards() {
		return scoredThreePointCards;
	}
	public void setScoredThreePointCards(Integer scoredThreePointCards) {
		this.scoredThreePointCards = scoredThreePointCards;
	}
	public Integer getScoredOnePointCards() {
		return scoredOnePointCards;
	}
	public void setScoredOnePointCards(Integer scoredOnePointCards) {
		this.scoredOnePointCards = scoredOnePointCards;
	}
	public Integer getTurnsUsed() {
		return turnsUsed;
	}
	public void setTurnsUsed(Integer turnsUsed) {
		this.turnsUsed = turnsUsed;
	}
	public Integer getCardsFailed() {
		return cardsFailed;
	}
	public void setCardsFailed(Integer cardsFailed) {
		this.cardsFailed = cardsFailed;
	}

	public Boolean getIsCurrentPlayer() {
		return isCurrentPlayer;
	}

	public void setIsCurrentPlayer(Boolean isCurrentPlayer) {
		this.isCurrentPlayer = isCurrentPlayer;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public Collection<PlayerRole> getRoles() {
		return roles;
	}

	public void setRoles(Collection<PlayerRole> roles) {
		this.roles = roles;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getCurrentGameId() {
		return currentGameId;
	}

	public void setCurrentGameId(String currentGameId) {
		this.currentGameId = currentGameId;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getDisplayNameModId() {
		return displayNameModId;
	}

	public void setDisplayNameModId(Integer displayNameModId) {
		this.displayNameModId = displayNameModId;
	}

	public Integer getTeamIdModId() {
		return teamIdModId;
	}

	public void setTeamIdModId(Integer teamIdModId) {
		this.teamIdModId = teamIdModId;
	}

	public Integer getRolesModId() {
		return rolesModId;
	}

	public void setRolesModId(Integer rolesModId) {
		this.rolesModId = rolesModId;
	}

	
}
