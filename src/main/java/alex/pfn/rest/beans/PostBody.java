package alex.pfn.rest.beans;

import java.util.List;

import alex.pfn.score.ScoreType;

/**
 *
 */
public class PostBody {

	private String playerId;
	private Boolean isAdding;
	private String teamId;
	private String teamName;
	private Integer lastKnownLogLine;
	private Integer scoreModId;

	private Boolean isAddingRole;
	private Boolean isReplacingCard;
	private Boolean isScoringCard;
	private Boolean isDrawingCard;
	private String cardId;
	private ScoreType scoreType;
	
	private Boolean isStartingTimer;
	private Boolean isResettingTimer;
	private Boolean isPausingTimer;
	private Boolean isResumingTimer;
	private String displayName;
	private Boolean shouldAddPoint;
	
	private List<String> selectedFileList;
	private List<String> selectedPlayerIdList;
	
	private Integer maxTurnsForMembersOfLargestTeam;
	
	private List<PostBodyScoreChange> scoreChangeList;
	
	
	public Integer getScoreModId() {
		return scoreModId;
	}
	public void setScoreModId(Integer scoreModId) {
		this.scoreModId = scoreModId;
	}
	public List<PostBodyScoreChange> getScoreChangeList() {
		return scoreChangeList;
	}
	public void setScoreChangeList(List<PostBodyScoreChange> scoreChangeList) {
		this.scoreChangeList = scoreChangeList;
	}
	public Boolean getShouldAddPoint() {
		return shouldAddPoint;
	}
	public void setShouldAddPoint(Boolean shouldAddPoint) {
		this.shouldAddPoint = shouldAddPoint;
	}
	public List<String> getSelectedPlayerIdList() {
		return selectedPlayerIdList;
	}
	public void setSelectedPlayerIdList(List<String> selectedPlayerIdList) {
		this.selectedPlayerIdList = selectedPlayerIdList;
	}
	public List<String> getSelectedFileList() {
		return selectedFileList;
	}
	public void setSelectedFileList(List<String> selectedFileList) {
		this.selectedFileList = selectedFileList;
	}
	public Integer getMaxTurnsForMembersOfLargestTeam() {
		return maxTurnsForMembersOfLargestTeam;
	}
	public void setMaxTurnsForMembersOfLargestTeam(Integer maxTurnsForMembersOfLargestTeam) {
		this.maxTurnsForMembersOfLargestTeam = maxTurnsForMembersOfLargestTeam;
	}
	public ScoreType getScoreType() {
		return scoreType;
	}
	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}
	public Integer getLastKnownLogLine() {
		return lastKnownLogLine;
	}
	public void setLastKnownLogLine(Integer lastKnownLogLine) {
		this.lastKnownLogLine = lastKnownLogLine;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Boolean getIsDrawingCard() {
		return isDrawingCard;
	}
	public void setIsDrawingCard(Boolean isDrawingCard) {
		this.isDrawingCard = isDrawingCard;
	}
	public Boolean getIsResumingTimer() {
		return isResumingTimer;
	}
	public void setIsResumingTimer(Boolean isResumingTimer) {
		this.isResumingTimer = isResumingTimer;
	}
	public Boolean getIsStartingTimer() {
		return isStartingTimer;
	}
	public void setIsStartingTimer(Boolean isStartingTimer) {
		this.isStartingTimer = isStartingTimer;
	}
	public Boolean getIsResettingTimer() {
		return isResettingTimer;
	}
	public void setIsResettingTimer(Boolean isResettingTimer) {
		this.isResettingTimer = isResettingTimer;
	}
	public Boolean getIsPausingTimer() {
		return isPausingTimer;
	}
	public void setIsPausingTimer(Boolean isPausingTimer) {
		this.isPausingTimer = isPausingTimer;
	}
//	public String getRequestingPlayerId() {
//		return requestingPlayerId;
//	}
//	public void setRequestingPlayerId(String requestingPlayerId) {
//		this.requestingPlayerId = requestingPlayerId;
//	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Boolean getIsAddingRole() {
		return isAddingRole;
	}
	public void setIsAddingRole(Boolean isAddingRole) {
		this.isAddingRole = isAddingRole;
	}
	public Boolean getIsReplacingCard() {
		return isReplacingCard;
	}
	public void setIsReplacingCard(Boolean isReplacingCard) {
		this.isReplacingCard = isReplacingCard;
	}
	public Boolean getIsScoringCard() {
		return isScoringCard;
	}
	public void setIsScoringCard(Boolean isScoringCard) {
		this.isScoringCard = isScoringCard;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public Boolean getIsAdding() {
		return isAdding;
	}
	public void setIsAdding(Boolean isAdding) {
		this.isAdding = isAdding;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	
	public void outputContents() {
		System.out.println(toString());
	}
	
	@Override
	public String toString() {
		StringBuilder sb;
		
		sb=new StringBuilder(100);
		
//		if (requestingPlayerId!=null) {
//			sb.append("requestingPlayerId=[")
//				.append(requestingPlayerId)
//				.append("], ");
//		}
		if (playerId!=null) {
			sb.append("playerId=[")
				.append(playerId)
				.append("], ");
		}
		
		
		if (isAddingRole!=null) {
			sb.append("isAddingRole=[")
				.append(isAddingRole)
				.append("], ");
		}
		
		if (isReplacingCard!=null) {
			sb.append("isReplacingCard=[")
				.append(isReplacingCard)
				.append("], ");
		}
		
		if (isScoringCard!=null) {
			sb.append("isScoringCard=[")
				.append(isScoringCard)
				.append("], ");
		}
		
		if (cardId!=null) {
			sb.append("cardId=[")
				.append(cardId)
				.append("], ");
		}
		
		
		if (sb.length()>2) {
			sb.setLength(sb.length()-2);
		}
		
		return sb.toString();
	}
}
		