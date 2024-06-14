package alex.pfn.card;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import alex.date.ElapsedTime;
import alex.pfn.Configuration;
import alex.pfn.Constants;



/**
 * This must handle the fact that some cards were already used in the past.
 * 
 * The instance for this class should be a singleton.
 * 
 * @author alex
 *
 */
public class CardPool
{
	
	private static final Object instanceMonitor=new Object();
    private final Random rnd;
    
    // These get reconstructed whenever the filesToUse are set. 
    private final ArrayList<CardFileStore> cardFileStores;
    
    
    // Should get cleared when the used file stores are emptied, and when filesToUse are set.
    // Should NOT be cleared when a new game is started.
	private final ArrayList<String> targetWordsUsedInCurrentGame;
	
    // Should get refilled when the used file stores are emptied, and when filesToUse are set.
    // Should NOT be cleared when a new game is started.
    private final ArrayList<Card> remainingCards;
    
	private ElapsedTime elapsedTime;
	

	// This shouldn't be used as a storage of available cards.  Rather, this
	// should just be used for searching for cards via id.
//	private HashMap<String, Card> cardsAndIds;
	
    /**
     * 
     * @param filesToUse May be null or empty.
     */
    public CardPool(List<String> filesToUse)
    {
    	synchronized(instanceMonitor) {
	    	rnd=new Random();
	        remainingCards=new ArrayList<Card>();
//	        cardsAndIds=new HashMap<String, Card>(); 
	        
	        targetWordsUsedInCurrentGame=new ArrayList<String>();

	        cardFileStores=new ArrayList<CardFileStore>();
	        
	        elapsedTime=new ElapsedTime(Configuration.getConfigProperties().getPropertyAsInteger(Constants.config_card_secondsDelayBeforeWritingUsedCards));
			elapsedTime.start();
	        
	        initLoader(filesToUse);
    	}
    }
    
    
    /**
     * 
     * @param filesToUse May not be null nor empty.
     */
    private void initLoader(final List<String> filesToUse) {
    	synchronized(instanceMonitor) {
    		CardFileStore cfs;
    		
//    		cardsAndIds.clear();;
    		cardFileStores.clear();
    		if (filesToUse!=null && !filesToUse.isEmpty()) {
	    		for (String fileName: filesToUse) {
	    			cfs = new CardFileStore(fileName);
	    			this.cardFileStores.add(cfs);
	    		}
    		}

	        targetWordsUsedInCurrentGame.clear();
    		remainingCards.clear();
    		fillRemainingCards();
    	}
    }
    

//	public Card getCard(String cardId)
//	{
//		return this.cardsAndIds.get(cardId);
//	}
	
    
    /**
     * @param filesToUse May not be null nor empty.
     */
    public void setFilesToUse(final List<String> filesToUse)
    {
    	synchronized(instanceMonitor) {
    		initLoader(filesToUse);
    	}
    }
    
    private void fillRemainingCards()
    {
    	synchronized(instanceMonitor) {
	    	for (CardFileStore cfs: this.cardFileStores) {
	    		remainingCards.addAll(cfs.getAvailableCards());
			}
    	}
    }
    
    private void resetCardFileStores()
    {
    	synchronized(instanceMonitor) {
	    	for (CardFileStore cfs: this.cardFileStores) {
	    		cfs.resetUsedCards();
			}
    	}
    }
    

    
    
    /**
     * Check to see if all cards have been used.
     * If they have, set the used cards to nothing, then reread all of the cards. 
     */
    private void checkIfAllCardsAreUsed(int threshholdBeforeReshuffling) {
    	synchronized(instanceMonitor) {
	    	if (remainingCards.size()<threshholdBeforeReshuffling) {
	    		targetWordsUsedInCurrentGame.clear();
	    		remainingCards.clear();
	    		
	    		resetCardFileStores();
	    		fillRemainingCards();
	    	}
    	}
    }
    
    

    /**
     * Don't call addUsedCard() here.
     * @return
     */
    private Card performGetRandomCard() {
    	synchronized(instanceMonitor) {
    		Card retVal;
    		
	    	checkIfAllCardsAreUsed(1);
	    	
	    	retVal = remainingCards.remove(rnd.nextInt(remainingCards.size()));

	    	return retVal;
    	}
    }
    
	
	public void flushUsedCardStores()
	{
    	synchronized(instanceMonitor) {
			for (CardFileStore cfs: this.cardFileStores) {
				cfs.flushUsedCards();
			}
    	}
	}
    
	
	/**
	 * Don't just get a card.  Also, add it as a used card.
	 * @return
	 */
    public Card getRandomCard() {
    	synchronized(instanceMonitor) {
			Card retVal;

//			retVal = getUniqueNewCard();
			retVal = performGetRandomCard();
	    	
			addUsedCard(retVal);
			
			return retVal;
    	}
    }

    
    /**
     * After a card has been used, then this method will
     * send it off to the storage to keep.
     * @param c May not be null.
     */
    public void addUsedCard(Card c) {
    	synchronized(instanceMonitor) {
    		c.getCardFileStore().addUsedCard(c);
    		
    		if (elapsedTime.hasFinished()) {
    			flushUsedCardStores();
				elapsedTime.start();
			}
    	}
    }
}
