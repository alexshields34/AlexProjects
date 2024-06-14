package alex.pfn.score;

public enum ScoreType
{
	/**
	 * normal1 = a teammember scored this 1 point card for her team.
	 * normal3 = a teammember scored this 3 point card for her team.
	 * pass = a player passed on playing her card, and the other team scored this card.
	 * failed = a player says one of the words on her card, or she says a multisyllabic word.
	 * unscored = A card that is unguessed, and the player's team didn't guess it before time ran out.
	 */	
	normal1(1, "1 point"),
	normal3(3, "3 points"),
	unscored(0, "unscored"),
	pass(1, "passed"),
	failed(1, "failure");
	
	
	private int numericValue;
	private String printableString;
	
	private ScoreType(int numericValue, String printableString) {
		this.numericValue=numericValue;
		this.printableString=printableString;
	}

	public int getNumericValue() {
		return numericValue;
	}
	public String getPrintableString()
	{
		return printableString;
	}
	
}