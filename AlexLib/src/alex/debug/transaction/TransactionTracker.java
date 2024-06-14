
package alex.debug.transaction;

import java.util.HashMap;

/**
 *
 * @author alex
 */
public class TransactionTracker
{

    private final static HashMap<Thread, TransactionData> threadMap;
	
    static {
        threadMap=new HashMap<Thread, TransactionData>();
    }
    
    
    public static void startTransaction() {
        TransactionData td=new TransactionData();
        threadMap.put(Thread.currentThread(), td);
    }
    
    public static void startTransaction(String firstMessage) {
        TransactionData td=new TransactionData();
        threadMap.put(Thread.currentThread(), td);
        td.addMessage(firstMessage);
    }

    public static TransactionData getTransactionData() {
        TransactionData retVal;

        retVal=threadMap.get(Thread.currentThread());
        if (retVal==null) {
            startTransaction();
            retVal=threadMap.get(Thread.currentThread());
        }

        return retVal;
    }

    public static String endTransactionAndGetMessages() {
        String retVal;
        TransactionData td;

        td=threadMap.remove(Thread.currentThread());

        retVal=td.endTransactionAndGetMessages();

        return retVal;
    }

    public static void endTransactionAndOutputMessages() {
        System.out.println(endTransactionAndGetMessages());
    }

}