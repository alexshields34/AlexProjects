/**
 */
package alex.date;

import java.util.concurrent.TimeUnit;

/**
 * This is for parsing strings like 2:00:30 into milliseconds.  Each unit
 * is separated by a colon.
 * 
 * @author alex
 */
public class TimeParser {
    
    /**
     * 
     * @param timeStr
     * @param lowestTimeUnit This is the unit of the rightmost number.  
     * Don't use anything smaller than milliseconds.
     * @return Milliseconds.
     */
    public static long parseString(String timeStr, TimeUnit lowestTimeUnit)
    {
        TimeUnit currentUnit;
        long retVal;
        int i;
        long duration;
        String remainingChunk, smallChunk;
        
        retVal=0L;
        remainingChunk=timeStr;
        currentUnit=lowestTimeUnit;
        while (!remainingChunk.isEmpty()) {
            i=remainingChunk.lastIndexOf(":");
            if (i==-1) {
                smallChunk=remainingChunk;
                remainingChunk="";
            } else {
                smallChunk=remainingChunk.substring(i+1, remainingChunk.length());
                remainingChunk=remainingChunk.substring(0, i);
            }
            
            duration=Long.valueOf(smallChunk);
            retVal+=currentUnit.convert(duration, TimeUnit.MILLISECONDS);

            switch (currentUnit) {
                case MILLISECONDS:
                     currentUnit=TimeUnit.SECONDS;
                     break;
                case SECONDS:
                     currentUnit=TimeUnit.MINUTES;
                     break;
                case MINUTES:
                     currentUnit=TimeUnit.HOURS;
                     break;
                case HOURS:
                     currentUnit=TimeUnit.DAYS;
                     break;
            }
            
        }
        
        return retVal;
    }
}
