package alex.pfn.rest.beans;

import java.util.ArrayList;

public class ScoreLogDTO {

	private ArrayList<ScoreLogEntryDTO> scoreLogEntryList;

	public ScoreLogDTO() {
		this.scoreLogEntryList = new ArrayList<ScoreLogEntryDTO>();
	}

	public ArrayList<ScoreLogEntryDTO> getScoreLogEntryList() {
		return scoreLogEntryList;
	}
	
}
