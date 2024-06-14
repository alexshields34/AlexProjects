package alex.pfn.card;


import alex.pfn.rest.beans.CardDTO;

public class Card {

	private String id;
	private String target1Pt, target3Pt;
	private final CardFileStore cfs;
	
	// This is the line that comes from the card file.
	private String originalLineFromFile;
	

	public Card(String id, String target1Pt, String target3Pt, String originalLineFromFile, CardFileStore cfs) {
		init(id, target1Pt, target3Pt, originalLineFromFile);
		this.cfs=cfs;
	}
	

	public Card(String id, String[] targets, String originalLineFromFile, CardFileStore cfs) {
		init(id, targets[0], targets[1], originalLineFromFile);
		this.cfs=cfs;
	}
	
	
	private void init(String id, String target1Pt, String target3Pt, String originalLineFromFile) {

		
		this.originalLineFromFile=originalLineFromFile;
		this.id=id;
		
		this.target1Pt=target1Pt.trim();
		this.target3Pt=target3Pt.trim();
	}
	
	
	public String getTarget1Pt() {
		return target1Pt;
	}


	public String getTarget3Pt() {
		return target3Pt;
	}


	public String getId() {
		return id;
	}


	public String getOriginalLineFromFile() {
		return originalLineFromFile;
	}
	

	public CardFileStore getCardFileStore() {
		return cfs;
	}
	
	
	public String toCompactString()
	{
		return originalLineFromFile;
	}
	
	public CardDTO buildDTO()
	{
		CardDTO retVal;
		
		retVal=new CardDTO();
		
		retVal.setId(id);
		retVal.setTarget1Pt(target1Pt);
		retVal.setTarget3Pt(target3Pt);
		
		return retVal;
	}
	
	
}