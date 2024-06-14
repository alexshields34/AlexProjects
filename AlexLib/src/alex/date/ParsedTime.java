
package alex.date;

/**
 * Given a number of milliseconds in the form of a long,
 * parse it into milliseconds, seconds, minutes, hours, and days.
 * @author alex
 */
public class ParsedTime {
    
    
    private final static long SECOND_MILLISECONDS=1000;
    private final static long MINUTE_MILLISECONDS=60 * SECOND_MILLISECONDS;
    private final static long HOUR_MILLISECONDS=60 * MINUTE_MILLISECONDS;
    private final static long DAY_MILLISECONDS=24 * HOUR_MILLISECONDS;
    
    
    private final long originalTotal;

    private long milliseconds;
    private long seconds;
    private long minutes;
    private long hours;
    private long days;
        
    
    public static ParsedTime parse(long milliseconds)
    {
        ParsedTime retVal;
        long longVal;
        
        retVal=new ParsedTime(milliseconds);
        
        longVal=(long)(milliseconds / DAY_MILLISECONDS);
        retVal.days=longVal;
        milliseconds=milliseconds % DAY_MILLISECONDS;
        
        longVal=(long)(milliseconds / HOUR_MILLISECONDS);
        retVal.hours=longVal;
        milliseconds=milliseconds % HOUR_MILLISECONDS;
        
        longVal=(long)(milliseconds / MINUTE_MILLISECONDS);
        retVal.minutes=longVal;
        milliseconds=milliseconds % MINUTE_MILLISECONDS;
        
        longVal=(long)(milliseconds / SECOND_MILLISECONDS);
        retVal.seconds=longVal;
        milliseconds=milliseconds % SECOND_MILLISECONDS;
        
        // What's left is just milliseconds.
        retVal.milliseconds=milliseconds;
        
        return retVal;
    }

    /**
     * Friendly visibility.
     * @param originalTotal 
     */
    ParsedTime(long originalTotal) {
        this.originalTotal=originalTotal;

        milliseconds=
                seconds=
                minutes=
                hours=
                days=0;
    }
    
    public long getOriginalTime() {
        return originalTotal;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getMinutes() {
        return minutes;
    }
    
    public long getHours() {
        return hours;
    }

    public long getDays() {
        return days;
    }

    public String toCompactString() {
        StringBuilder sb;
        sb=new StringBuilder(100);

        if (getDays()!=0) {
            sb.append(getDays())
                    .append("d, ");
        }

        if (getHours()!=0) {
            sb.append(getHours())
                    .append("h, ");
        }

        if (getMinutes()!=0) {
            sb.append(getMinutes())
                    .append("m, ");
        }

        if (getSeconds()!=0 || getMilliseconds()!=0) {
            sb.append(getSeconds())                
                    .append(".")
                    .append(getMilliseconds())
                    .append("s");
        }

        if (sb.length()>0 && sb.charAt(sb.length()-2) ==',') {
            sb.setLength(sb.length()-2);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean shouldIncludeMillis) {
        StringBuilder sb=new StringBuilder(100);

        if (getDays()!=0) {
            sb.append(getDays())
                    .append(" days, ");
        }

        if (getHours()!=0) {
            sb.append(getHours())
                    .append(" hours, ");
        }

        if (getMinutes()!=0) {
            sb.append(getMinutes())
                    .append(" minutes, ");
        }

        if (getSeconds()!=0) {
            sb.append(getSeconds())
                    .append(" seconds, ");
        }

        if (shouldIncludeMillis && getMilliseconds()!=0) {
            sb.append(getMilliseconds())
                    .append(" milliseconds, ");
        }

        if (sb.length()>0) {
            sb.setLength(sb.length()-2);
        }

        return sb.toString();
    }


}
