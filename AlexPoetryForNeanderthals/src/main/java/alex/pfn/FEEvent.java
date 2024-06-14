package alex.pfn;

public class FEEvent
{

	private FEEventType eventType=null;
	private Boolean success=null;
	private Boolean failure=null;
	private Boolean isNewGame=null;
	private String imageUrl=null;
	

	public FEEvent(FEEvent other) {
		this.eventType=other.eventType;
		this.success=other.success;
		this.failure=other.failure;
		this.isNewGame=other.isNewGame;
		this.imageUrl=other.imageUrl;
	}
	public FEEvent() {
	}
	
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public FEEventType getEventType() {
		return eventType;
	}
	public void setEventType(FEEventType eventType) {
		this.eventType = eventType;
	}
	public Boolean getIsNewGame() {
		return isNewGame;
	}
	public void setIsNewGame(Boolean isNewGame) {
		this.isNewGame = isNewGame;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public Boolean getFailure() {
		return failure;
	}
	public void setFailure(Boolean failure) {
		this.failure = failure;
	}
	
}