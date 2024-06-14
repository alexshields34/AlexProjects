package alex.pfn;

import alex.pfn.score.ScoreLog;

public final class GlobalState
{
	private final static ActivityLog activityLog;
	
	private final static ScoreLog scoreLog;
	
	static {
		scoreLog=new ScoreLog();
		activityLog=new ActivityLog(Configuration.getConfigProperties().getPropertyAsInteger(Constants.config_activityLog_maximum_logLines).intValue());
	}
	
	
	public static ActivityLog getActivityLog() {
		return activityLog;
	}
	
	public static ScoreLog getScoreLog() {
		return scoreLog;
	}
}