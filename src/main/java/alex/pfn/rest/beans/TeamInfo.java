package alex.pfn.rest.beans;

import java.util.ArrayList;
import java.util.List;


/**
 * A holder for a few team specific values.
 * @author alex
 *
 */
public class TeamInfo {
	
	private String currentTeamId=null;
	private String currentPlayerId=null;
	
	private final ArrayList<TeamDTO> teamList;
	private final ArrayList<String> deletedTeams;
	private final ArrayList<String> highestScoringTeams;
	private Integer highestScoringTeamsModId=null;
	
	private Integer teamMembershipModId=null;
	private Integer teamListModId=null;
	
	private Integer currentPlayerModId=null;
	

	private List<String> winningTeamIds=null;
	

	private Integer remainingTurnsIncludingCurrent=null;
	
	// PlayerIds.
	private ArrayList<String> upcomingTurns;
	
	
	public TeamInfo() {
		teamList=new ArrayList<TeamDTO>();
		deletedTeams=new ArrayList<String>();
		highestScoringTeams=new ArrayList<String>();
	}
	


	public Integer getRemainingTurnsIncludingCurrent() {
		return remainingTurnsIncludingCurrent;
	}
	public void setRemainingTurnsIncludingCurrent(Integer remainingTurnsIncludingCurrent) {
		this.remainingTurnsIncludingCurrent = remainingTurnsIncludingCurrent;
	}
	public ArrayList<String> getUpcomingTurns() {
		return upcomingTurns;
	}
	public void setUpcomingTurns(ArrayList<String> upcomingTurns) {
		this.upcomingTurns = upcomingTurns;
	}
	public List<String> getWinningTeamIds() {
		return winningTeamIds;
	}
	public void setWinningTeamIds(List<String> winningTeamIds) {
		this.winningTeamIds = winningTeamIds;
	}
	public ArrayList<TeamDTO> getTeamList() {
		return teamList;
	}
	public ArrayList<String> getDeletedTeams() {
		return deletedTeams;
	}

	public ArrayList<String> getHighestScoringTeams() {
		return highestScoringTeams;
	}

	public Integer getTeamListModId() {
		return teamListModId;
	}

	public void setTeamListModId(Integer teamListModId) {
		this.teamListModId = teamListModId;
	}

	public Integer getHighestScoringTeamsModId() {
		return highestScoringTeamsModId;
	}

	public void setHighestScoringTeamsModId(Integer highestScoringTeamsModId) {
		this.highestScoringTeamsModId = highestScoringTeamsModId;
	}

	public Integer getTeamMembershipModId() {
		return teamMembershipModId;
	}

	public void setTeamMembershipModId(Integer teamMembershipModId) {
		this.teamMembershipModId = teamMembershipModId;
	}

	public Integer getCurrentPlayerModId() {
		return currentPlayerModId;
	}

	public void setCurrentPlayerModId(Integer currentPlayerModId) {
		this.currentPlayerModId = currentPlayerModId;
	}

	public String getCurrentTeamId() {
		return currentTeamId;
	}

	public void setCurrentTeamId(String currentTeamId) {
		this.currentTeamId = currentTeamId;
	}

	public String getCurrentPlayerId() {
		return currentPlayerId;
	}

	public void setCurrentPlayerId(String currentPlayerId) {
		this.currentPlayerId = currentPlayerId;
	}


	@Override
	public String toString() {
		return "TeamInfo [currentTeamId=" + currentTeamId + ", currentPlayerId=" + currentPlayerId 
				+ ", teamList=" + teamList + ", deletedTeams=" + deletedTeams + ", highestScoringTeams="
				+ highestScoringTeams + ", highestScoringTeamsModId=" + highestScoringTeamsModId
				+ ", teamMembershipModId=" + teamMembershipModId + ", teamListModId=" + teamListModId
				+ ", currentPlayerModId=" + currentPlayerModId
				+ ", winningTeamIds=" + winningTeamIds + ", remainingTurnsIncludingCurrent="
				+ remainingTurnsIncludingCurrent + ", upcomingTurns=" + upcomingTurns + "]";
	}

}