package alex.pfn.card;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import alex.file.BufferedTextFile;
import alex.file.FileUtil;
import alex.pfn.Configuration;
import alex.pfn.Constants;
import alex.date.ElapsedTime;

/**
 * Newly used cards get put here for the purpose of writing to disk.
 * @author alex
 *
 */
public class UsedCardStore
{
	
	
	private final Object writeMonitor=new Object();
	
	// Cards that have not yet been written to disk.
	private ArrayList<Card> unwrittenCards;
	
	private File fileStore;
	
	
	public UsedCardStore(File usedFileStore)
		throws IOException
	{
		fileStore=usedFileStore;
		
		if (!fileStore.exists()) {
			fileStore.createNewFile();
		}
		
		unwrittenCards=new ArrayList<Card>();
		
	}
	
	
	
	
	/**
	 * Clear the contents of the used cards file.
	 */
	public void resetUsedCards()
	{
		synchronized(writeMonitor) {
//			System.out.println(this.getClass().getName()+":: resetUsedCards:: ENTERING. File=["+this.fileStore.getName()+"]");

			try {
				FileUtil.clearFile(fileStore);
				this.unwrittenCards.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	
	/**
	 * @return Retrieve all used cards in the file store.
	 */
	public HashSet<String> getAllUsedCards()
		throws IOException
	{
		
		synchronized(writeMonitor) {
//			System.out.println(this.getClass().getName()+":: getAllUsedCards:: ENTERING. File=["+fileStore.getName()+"].");
			BufferedTextFile btf;
			HashSet<String> retVal;
			
			btf=new BufferedTextFile(fileStore);
			retVal=new HashSet<String>();
			
			btf.dumpContents(retVal, 1, false, false, true);
			
			btf.close();
			
			return retVal;
		}
	}
	
	/**
	 * This should only be called once per card.
	 * @param c May not be null.
	 */
	public void addCard(Card c) {
		synchronized(writeMonitor) {
//			System.out.println(this.getClass().getName()
//					+":: addCard:: threadName=["
//					+Thread.currentThread().getName()
//					+"], c.getCardText()=["
//					+c.getTargetWord()
//					+"],  file=["
//					+fileStore.getName()
//					+"].");
			
			unwrittenCards.add(c);
		}
	}
	
	/**
	 * Writes the unwrittenCards to disk regardless of how much time has elapsed.
	 */
	public void flushCards()
	{

		synchronized(writeMonitor) {
//			System.out.println(this.getClass().getName()+":: flushCards:: ENTERING");
			
			FileWriter fw;
			BufferedWriter bw;
			int numCardsWritten=0;
			
			try {
				if (!unwrittenCards.isEmpty()) {
					
					fw=new FileWriter(fileStore, true);
					bw=new BufferedWriter(fw);
					
					for (Card c: unwrittenCards) {
						bw.write(c.getOriginalLineFromFile());
						bw.newLine();
						numCardsWritten++;
					}
					
					bw.close();
					fw.close();
	
					unwrittenCards.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
					
					
			System.out.println(this.getClass().getName()+":: flushCards:: Wrote ["+numCardsWritten+"] cards to ["+this.fileStore.getName()+"].");
		}
	}
	
	
	
}