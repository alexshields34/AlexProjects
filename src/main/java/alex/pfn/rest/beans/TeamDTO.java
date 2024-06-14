package alex.pfn.rest.beans;

import java.util.ArrayList;

/**
 * This is for sending data to the front end.
 * 
 * @author alex
 *
 */
public class TeamDTO
{
	private final String teamId;
	private final String name;
	private String failuresGoTo=null;
	private String jsonDataForFrontEnd;
	private ArrayList<String> members;
	private Integer membersModId=null;
	private Integer nameModId=null;
	private Integer failuresGoToModId=null;
	private String cssColor=null;
	private Integer numericScore=null;
	private String scoreDescription=null;
	private Boolean isCurrentTeam=null;
	private Boolean isDeletable=null;
	private Boolean isDeleted=null;
	
	public TeamDTO(String teamId, String name)
	{
		this.teamId=teamId;
		this.name=name;
		jsonDataForFrontEnd="";
		this.members=new ArrayList<String>();
	}
	
	
	
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Boolean getIsDeletable() {
		return isDeletable;
	}
	public void setIsDeletable(Boolean isDeletable) {
		this.isDeletable = isDeletable;
	}
	public String getScoreDescription() {
		return scoreDescription;
	}
	public void setScoreDescription(String scoreDescription) {
		this.scoreDescription = scoreDescription;
	}
	public Boolean getIsCurrentTeam() {
		return isCurrentTeam;
	}
	public void setIsCurrentTeam(Boolean isCurrentTeam) {
		this.isCurrentTeam = isCurrentTeam;
	}

	public String getFailuresGoTo() {
		return failuresGoTo;
	}
	
	public void setFailuresGoTo(String failuresGoTo) {
		this.failuresGoTo = failuresGoTo;
	}

	public Integer getFailuresGoToModId() {
		return failuresGoToModId;
	}

	public void setFailuresGoToModId(Integer failuresGoToModId) {
		this.failuresGoToModId = failuresGoToModId;
	}

	public String getTeamId() {
		return teamId;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public String getJsonDataForFrontEnd() {
		return jsonDataForFrontEnd;
	}

	public void setJsonDataForFrontEnd(String jsonDataForFrontEnd) {
		this.jsonDataForFrontEnd = jsonDataForFrontEnd;
	}

	public Integer getMembersModId() {
		return membersModId;
	}

	public void setMembersModId(Integer membersModId) {
		this.membersModId = membersModId;
	}

	public Integer getNameModId() {
		return nameModId;
	}

	public void setNameModId(Integer nameModId) {
		this.nameModId = nameModId;
	}

	public String getCssColor() {
		return cssColor;
	}

	public void setCssColor(String cssColor) {
		this.cssColor = cssColor;
	}

	public Integer getNumericScore() {
		return numericScore;
	}
	public void setNumericScore(Integer numericScore) {
		this.numericScore = numericScore;
	}
		
	
}