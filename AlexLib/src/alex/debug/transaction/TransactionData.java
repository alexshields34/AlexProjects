package alex.debug.transaction;

import java.util.Date;
import alex.date.ParsedTime;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;


/**
 *
 * @author alex
 */
public class TransactionData
{
    private final static AtomicLong idCounter=new AtomicLong(0L);
    
    private long selfId;
    private Date startTime;
    private StringBuilder messages;

    private final Stack<TransactionData> nestedSections;

    public TransactionData() {
        selfId=idCounter.incrementAndGet();
        if (idCounter.get() >  Long.MAX_VALUE - 100000L) {
            idCounter.set(Long.valueOf(0L));
        } 
        nestedSections=new Stack<TransactionData>();
        messages=new StringBuilder();
        startTime=new Date();
    }
    

    /**
     * Newline is added after the string.
     * @param s
     */
    public void addMessage(String s) {
        messages.append(selfId)
                .append(": ")
                .append(new Date())
                .append(": ")
                .append(s)
                .append("\n");
    }

    public String getMessages() {
        return messages.toString();
    }
	
    public String endTransactionAndGetMessages() {
        ParsedTime pt;
        String retVal;
        Date endTime;
        StringBuilder sb;

        endTime=new Date();
        pt=ParsedTime.parse(endTime.getTime()-startTime.getTime());
        sb=new StringBuilder();
        
        sb.append("Start time and date: [")
                .append(startTime.toString())
                .append("]\nEnd time and date: [")
                .append(endTime.toString())
                .append("]\nElapsed time: [")
                .append(pt.toCompactString())
                .append("]");
        addMessage(sb.toString());

        retVal=messages.toString();

        startTime=null;
        messages=null;

        return retVal;
    }

    
    public void addMessageAndStartNestedChunk(String s) {
        TransactionData td;
        
        td=new TransactionData();
        td.addMessage(s);
        
        nestedSections.push(td);
    }

    
    public String addMessageAndEndNestedSection(String s) {
        TransactionData td;
        String retVal;
        
        td=nestedSections.pop();
        td.addMessage(s);
        retVal=td.endTransactionAndGetMessages();
        
        return retVal;
    }
    
    public void endNestedSectionAndOutputMessage(String s) {
        System.out.println(addMessageAndEndNestedSection(s));
    }	
}