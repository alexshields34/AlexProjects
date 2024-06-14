package alex.pfn.rest.beans;


public class CardDTO
{
	private String id=null;
	private String target1Pt=null;
	private String target3Pt=null;
	// Do I need card color?
	
	public CardDTO() {
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getTarget1Pt() {
		return target1Pt;
	}

	public void setTarget1Pt(String target1Pt) {
		this.target1Pt = target1Pt;
	}

	public String getTarget3Pt() {
		return target3Pt;
	}

	public void setTarget3Pt(String target3Pt) {
		this.target3Pt = target3Pt;
	}
	
	
}