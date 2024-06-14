package alex.pfn.score;

/**
 * Numeric values for players.
 * 
 * @author alex
 *
 */
public class PlayerCardValues {

	private int scoredThreePointCards;
	private int scoredOnePointCards;
	private int cardsFailed;
	
	public PlayerCardValues()
	{
		this.scoredThreePointCards = this.scoredOnePointCards = this.cardsFailed = 0;
	}
	
	public int getScoredThreePointCards() {
		return scoredThreePointCards;
	}
	public void setScoredThreePointCards(int scoredThreePointCards) {
		this.scoredThreePointCards = scoredThreePointCards;
	}
	public int getScoredOnePointCards() {
		return scoredOnePointCards;
	}
	public void setScoredOnePointCards(int scoredOnePointCards) {
		this.scoredOnePointCards = scoredOnePointCards;
	}
	public int getCardsFailed() {
		return cardsFailed;
	}
	public void setCardsFailed(int cardsFailed) {
		this.cardsFailed = cardsFailed;
	}
	
	public void addToCardsFailed()
	{
		this.cardsFailed++;
	}
	
	public void addToScoredOnePointCards() {
		this.scoredOnePointCards++;
	}
	
	public void addToScoredThreePointCards() {
		this.scoredThreePointCards++;
	}
}
