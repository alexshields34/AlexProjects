package alex.pfn.card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import alex.file.BufferedTextFile;
import alex.pfn.Configuration;
import alex.pfn.Constants;

/**
 * This handles both the card file to read from, and its used cards store.
 * Each individual card file should have one of these instances.
 * 
 * Cards created from this instance will have sourceFile.getName() + "_"
 * as the first part of the id of each card.
 * 
 * @author alex
 *
 */
public class CardFileStore {
	
	private final static File cardDirectory;
	static {
		cardDirectory = new File(Configuration.getConfigProperties().getProperty(Constants.config_dir_cards));
	}
	
	private File sourceFile;
	private File usedCardFile;
	private UsedCardStore usedCardStore;
	
	private ArrayList<Card> availableCards;
	
	
	public CardFileStore(final String sourceFileName) {
		sourceFile=new File(cardDirectory, sourceFileName);
		init(sourceFile);
	}

	public CardFileStore(final File sourceFile) {
		init(sourceFile);
	}
	
	private final void init(final File sourceFile) {
		this.sourceFile=sourceFile;
		
		this.usedCardFile=new File(sourceFile.getParent(), sourceFile.getName()+".used");
		
		// If the usedCardStore can't be constructed, then that is fatal to the application.
		try {
			usedCardStore=new UsedCardStore(usedCardFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		availableCards=new ArrayList<Card>();
		
		readAllCards();
	}
	

	public void addUsedCard(Card c) {
		this.usedCardStore.addCard(c);
	}
	
	public void flushUsedCards()
	{
		this.usedCardStore.flushCards();
	}
	
	
	/**
	 * Return all cards, not including the used ones.
	 * @return
	 */
	public List<Card> getAvailableCards() {
		return this.availableCards;
	}
	
	/**
	 * Empty the used card file.  The next call
	 * to getAvailableCards will return all of the cards.
	 */
	public void resetUsedCards() {
		usedCardStore.resetUsedCards();
		readAllCards();
	}
	

	/**
	 * Any exception from here is fatal.
	 * @throws IOException
	 */
	private void readAllCards() 
	{
		BufferedTextFile btf;
		String[] parts;
		String id, prefix;		
		int cardCounter=0;
		StringBuilder sb;
		ArrayList<String> unprocessedCards;
		Card card;
		
		availableCards.clear();
		unprocessedCards = new ArrayList<String>();
		
		
		try {
			btf=new BufferedTextFile(this.sourceFile);
			btf.dumpContents(unprocessedCards, 1, false, false, true);
			btf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

//		System.out.println(this.getClass().getName()
//				+":: readAllCards:: threadName=["
//				+Thread.currentThread().getName()
//				+"], sourceFile=["
//				+sourceFile.getName()
//				+"], unprocessedCards.size=["
//				+unprocessedCards.size()
//				+"]");

		try {
			unprocessedCards.removeAll(usedCardStore.getAllUsedCards());
		} catch (Exception e) {
			e.printStackTrace();
		}

//		System.out.println(this.getClass().getName()
//				+":: readAllCards:: threadName=["
//				+Thread.currentThread().getName()
//				+"], sourceFile=["
//				+sourceFile.getName()
//				+"], After removal of used cards, unprocessedCards.size=["
//				+unprocessedCards.size()
//				+"]");

		prefix = this.sourceFile.getName() + "_";
		sb=new StringBuilder(22);
		for (String line: unprocessedCards) {
			sb.setLength(0);
			sb.append(prefix);
			
			parts=line.split(";");
			
			sb.append(cardCounter);
			id=sb.toString();
			
			card=new Card(id, parts, line, this);
			availableCards.add(card);
			cardCounter++;
		}
		
	}
    
}
