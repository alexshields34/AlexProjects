package alex.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author alex
 */
public class CommonDate {
    
    // Common date format.
    private final static String commonDateParser_format="yyyyMMdd";
    private final static String commonDateWithDashesParser_format="yyyy-MM-dd";
    private final static String pacificDateWithHour_format="yyyyMMdd_ha";
    private final static String dateAndTimeNormalFormat_format="yyyy-MM-dd HH:mm:ss";
    
    private final static String dateAndHourFormatterForFileName_format="yyyyMMdd_HHmmss";
    
    //Sat, 15 Dec 2018 05:00:00 -0000
    private final static String dateAndHourParserForFullFormat_format="EEE, dd MMM yyyy HH:mm:ss Z";
    // Same as dateAndHourParserForFullFormat_format but for a 4 letter day.
    private final static String dateAndHourParserForFullFormat4LetterDay_format="EEEE, dd MMM yyyy HH:mm:ss Z";
    // Same as the full parser format but no spelled out day.
    private final static String dateAndHourParserForFullFormatNoDay_format="dd MMM yyyy HH:mm:ss Z";
    
    private final static SimpleDateFormat commonDateParser,
            commonDateWithDashesParser,
            pacificDateWithHourParser,
            dateAndHourParserForFullFormat,
            dateAndHourParserForFullFormat4LetterDay,
            dateAndHourParserForFullFormatNoDay,
            dateAndHourFormatterForFileName,
            dateAndTimeNormalFormat;
     
     
    static {
        commonDateParser=new SimpleDateFormat(commonDateParser_format);
        commonDateWithDashesParser=new SimpleDateFormat(commonDateWithDashesParser_format);
        dateAndHourFormatterForFileName=new SimpleDateFormat(dateAndHourFormatterForFileName_format);
        dateAndHourParserForFullFormat=new SimpleDateFormat(dateAndHourParserForFullFormat_format);
        
        dateAndHourParserForFullFormat4LetterDay=new SimpleDateFormat(dateAndHourParserForFullFormat4LetterDay_format);
        dateAndHourParserForFullFormatNoDay=new SimpleDateFormat(dateAndHourParserForFullFormatNoDay_format);
        pacificDateWithHourParser=new SimpleDateFormat(pacificDateWithHour_format);
        pacificDateWithHourParser.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        
        dateAndTimeNormalFormat=new SimpleDateFormat(dateAndTimeNormalFormat_format);
    }
    
    public static Date parseDateAndTimeNormalFormatString(String s)
            throws ParseException
    {
        return dateAndTimeNormalFormat.parse(s);
    }
    
    public static String getNowAsFilenameFormat()
    {
        return dateAndHourFormatterForFileName.format(new Date());
    }
    
    public static Date parseCommonDateString(String s)
            throws ParseException
    {
        return commonDateParser.parse(s);
    }
    
    public static Date parseCommonDateWithDashesString(String s)
            throws ParseException
    {
        return commonDateWithDashesParser.parse(s);
    }
    
    
    public static String createCommonDateWithDashesString(Date d)
            throws ParseException
    {
        return commonDateWithDashesParser.format(d);
    }
    
    public static String createCommonDateString(Date d)
    {
        return commonDateParser.format(d);
    }
    
    public static String formatPacificDateWithHour(Date d)
    {
        return pacificDateWithHourParser.format(d);
    }
    
    public static String getNowAsCommonDateString()
    {
        return createCommonDateString(new Date());
    }
    
    /**
     * Given a full date as a string, chop off its values smaller than a day,
     * and then return that date as a Date object.
     * @param fullDate
     * @return 
     * @throws ParseException
     */
    public static Date fullDateToCommonDateObject(String fullDate)
            throws ParseException
    {
        String s;
        Date d;
        
        s=fullDateToCommonDateString(fullDate);
        d=commonDateParser.parse(s);
        
        return d;
    }
    
    /**
     * Given a full date as a string, chop off its values smaller than a day,
     * and then return that date as a String.
     * 
     * It's possible that the incoming date has a 4 letter day instead of
     * a 3 letter day, which screws up the date parser.
     * 
     * @param fullDate
     * @return 
     * @throws ParseException
     */
    public static String fullDateToCommonDateString(final String fullDate)
            throws ParseException
    {
        String s, fixedDate;
        Date d;
        
        if (fullDate.contains("Tues,")) {
            fixedDate=fullDate.substring(0, 3) + fullDate.substring(4);
        } else {
            fixedDate=fullDate;
        }
        
        d=parseFullFormatDateString(fixedDate);
        s=commonDateParser.format(d);
        
        return s;
    }
    
    /**
     * Change the timestamp to a date object, and chop off the parts of the
     * date that are smaller than a day.  Then return the truncated date object.
     * @param timeStamp 
     * @return
     * @throws ParseException
     */
    public static Date timeStampToCommonDateObject(long timeStamp)
            throws ParseException
    {
        String s;
        Date d;
        
        s=commonDateParser.format(new Date(timeStamp));
        d=commonDateParser.parse(s);
        
        return d;
    }
    
    /**
     * If it's exactly 8 digits long then it's fine.
     * 
     * @param s
     * @return 
     */
    public static boolean isInCommonDateFormat(String s)
    {
        return s.matches("\\p{Digit}{8}");
    }
    
    /**
     * It's possible that the day is 4 characters long rather than 3.
     * Handle either one.
     * @param s
     * @return
     * @throws ParseException 
     */
    public static Date parseFullFormatDateString(String s)
            throws ParseException
    {
        Date retVal;
        
//        try {
            retVal=dateAndHourParserForFullFormat.parse(s);
//        } catch (Exception e) {
//            retVal=null;
//        }
//        
//        if (retVal==null) {
//            retVal=dateAndHourParserForFullFormat4LetterDay.parse(s);
//        }
        
        return retVal;
    }
    
    /**
     * Same as the full parser but with no day.
     * @param s
     * @return
     * @throws ParseException 
     */
    public static Date parseFullFormatNoDayDateString(String s)
            throws ParseException
    {
        Date retVal;
        
        retVal=dateAndHourParserForFullFormatNoDay.parse(s);
        
        return retVal;
    }
    
    
    
    public static Date fullDateStringToDateObject(String s)
            throws ParseException
    {
        return dateAndHourParserForFullFormat.parse(s);
    }
    
    
    
    public static void main(String[] args)
            throws Exception
    {
        Date d;
        String s="May 12, 2018";
        String pattern="MMMM d, yyyy";
        
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        
        d=sdf.parse(s);
        
        System.out.println(d);
        
        System.out.println(parseFullFormatDateString("Sat, 15 Dec 2018 05:00:00 -0000"));
        
        System.out.println(new Date( 1552581642000L));
    }
}
