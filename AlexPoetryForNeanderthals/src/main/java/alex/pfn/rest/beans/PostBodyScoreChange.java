package alex.pfn.rest.beans;

import java.util.ArrayList;
import java.util.List;

import alex.pfn.score.ScoreType;

public class PostBodyScoreChange {
	private Integer id;
	private ScoreType newScoreType;
	private Boolean shouldDelete;
	
	// This is filled in by GameServer, not the frontend.
	private List<String> benefitingTeams;
	
	public PostBodyScoreChange() {
		benefitingTeams = new ArrayList<String>(); 
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ScoreType getNewScoreType() {
		return newScoreType;
	}
	public void setNewScoreType(ScoreType newScoreType) {
		this.newScoreType = newScoreType;
	}
	public Boolean getShouldDelete() {
		return shouldDelete;
	}
	public void setShouldDelete(Boolean shouldDelete) {
		this.shouldDelete = shouldDelete;
	}
	public List<String> getBenefitingTeams() {
		return benefitingTeams;
	}
	public void setBenefitingTeams(List<String> benefitingTeams) {
		this.benefitingTeams = benefitingTeams;
	}
	
	
}
