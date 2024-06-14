package alex.pfn.rest.beans;

import java.util.ArrayList;
import java.util.List;

import alex.pfn.ActivityLog;

public class ActivityLogDTO {
	
	// This is the lowest line number included in this dto.
	private int lowestLineNumber;
	
	// The highest line number included in this dto.
	private int highestLineNumber;
	
	// This list won't always have every single log line.
	// Only the last few will be sent to the front end.  Each client must
	// keep track of the last log line that they have received.  They must
	// send that number to the getStatus request.
	private ArrayList<String> lines;
	
	/**
	 * 
	 * @param activityLog
	 * @param clientLastKnownLineNumber
	 */
	public ActivityLogDTO(ActivityLog activityLog, int getNewLinesFromThisPoint) {
		StringBuilder sb;
		ActivityLog.LineAndNumber oneLan;
		List<ActivityLog.LineAndNumber> sublist;
		
		sublist=activityLog.getLinesFrom(getNewLinesFromThisPoint);
		
		if (!sublist.isEmpty()) {
			oneLan=sublist.get(0);
			this.lowestLineNumber=oneLan.getNumber();
			
			oneLan=sublist.get(sublist.size()-1);
			this.highestLineNumber=oneLan.getNumber();
		}
		
		lines=new ArrayList<String>(); 
		
		sb=new StringBuilder(100);
		for (ActivityLog.LineAndNumber lan: sublist) {
			sb.setLength(0);
			sb.append(lan.getNumber())
				.append(": ")
				.append(lan.getLine());
			lines.add(sb.toString());
		}
		
	}
	
	

	public int getLowestLineNumber() {
		return lowestLineNumber;
	}
	
	public int getHighestLineNumber() {
		return highestLineNumber;
	}

	public ArrayList<String> getLines() {
		return lines;
	}
}