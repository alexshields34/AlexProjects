package alex.pfn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alex.pfn.player.Player;

public class ActivityLog
{
	public final static int NO_LIMIT=-1;
	public final static int NOT_FOUND=-2;
	
	private final Object changeLock=new Object();
	
	private final ArrayList<LineAndNumber> lines;
	private int limit;
	private int lastUsedLineNumber;
	
	/**
	 * 
	 * @param limit Must be greater than 0.
	 * @throws IllegalArgumentException if limit is lower than 0.
	 */
	public ActivityLog(int limit)
			throws IllegalArgumentException
	{
		if (limit<0) {
			throw new IllegalArgumentException("limit may not be less than 0.  Given value is " + limit);
		}
		lines=new ArrayList<LineAndNumber>();
		this.limit=limit;
		
		reset();
		lastUsedLineNumber=-1;
	}
	
	public ActivityLog() {
		this(10000);
	}

	private void reset() {
		synchronized(changeLock) {
			this.lastUsedLineNumber=-1;
			this.lines.clear();
		}
	}
	
	public void clear() {
		reset();
	}
	
	public void addLine(Player p, String message) {
		synchronized(changeLock) {
			addLine(p.getDisplayName()+": "+message);
		}
	}
	
	
	public void addLine(String displayName, String message) {
		synchronized(changeLock) {
			addLine(displayName+": "+message);
		}
	}
	
	
	public void addLine(String s) {
		
		synchronized(changeLock) {
			int difference;
			LineAndNumber lan;
			
			lan=new LineAndNumber(++this.lastUsedLineNumber, s);
			this.lines.add(lan);
			
			
			if (limit!=NO_LIMIT) {
				difference=lines.size()-limit;
				while (difference>0) {
					 this.lines.remove(0);
					 difference--;
				}
			}
		}
	}
	
	
	public int getLastUsedLineNumber() {
		return this.lastUsedLineNumber;
	}
	
	public int getSize() {
		synchronized(changeLock) {
			return this.lines.size();
		}
	}
	
	
	/**
	 * 
	 * @param fromThisLineNumber Must be not negative.
	 * If it's higher than the largest line number in this.lines, return
	 * an empty list.
	 * @return
	 */
	public List<LineAndNumber> getLinesFrom(int fromThisLineNumber) {
		synchronized(changeLock) {
			List<LineAndNumber> retVal;
			int startIndex, exclusiveLastIndex;
			
			startIndex=getIndexOfLineNumber(fromThisLineNumber);
			if (startIndex==NOT_FOUND) {
				retVal=Collections.emptyList();
			} else {
				exclusiveLastIndex=lines.size();
				
				retVal=lines.subList(startIndex, exclusiveLastIndex);
			}
			
			return retVal;
		}
	}
	
	
	/**
	 * Given a lineNumber, find the index within lines of 
	 * the line that has the lineNumber in question.
	 * 
	 * If the lineNumber is smaller than the lineNumber at index 0, then return 0.
	 * If this.lines is empty, return NOT_FOUND.
	 * If the lineNumber is greater than the lineNumber of the last element in
	 * lines, then return NOT_FOUND.
	 * 
	 * @param lineNumber
	 * @return
	 */
	private int getIndexOfLineNumber(int lineNumber)
	{
		synchronized(changeLock) {
			int retVal, indexCounter;
			LineAndNumber last;
			
			retVal=NOT_FOUND;
			if (!lines.isEmpty()) {
			
				last=lines.get(lines.size()-1);
				if (last.getNumber()<lineNumber) {
					retVal=NOT_FOUND;
				} else {
					indexCounter=-1;
					for (LineAndNumber lan: this.lines) {
						indexCounter++;
						if (lan.getNumber()>=lineNumber) {
							retVal=indexCounter;
							break;
						}
					}
				}
			}
			
			return retVal;
		}
	}
	
	
	/**
	 * Use ActivityLog.NO_LIMIT to return all lines.
	 * 
	 * This method can return active lists.  Don't modify them.
	 * 
	 * @param numberOfLinesToReturn
	 * @return
	 */
	public List<LineAndNumber> getLastLines(int numberOfLinesToReturn) {
		synchronized(changeLock) {
			List<LineAndNumber> retVal;
			int startIndex, exclusiveLastIndex;
			
			if (numberOfLinesToReturn==NO_LIMIT) {
				retVal=lines;
			} else {
				exclusiveLastIndex=lines.size();
				startIndex=exclusiveLastIndex-numberOfLinesToReturn;
				if (startIndex<0) {
					startIndex=0;
				}
				retVal=lines.subList(startIndex, exclusiveLastIndex);
			}
			
			return retVal;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb;
		
		sb=new StringBuilder(100);
		sb.append("ActivityLog: [");
		for (LineAndNumber lan: this.lines) {
			sb.append(lan.getNumber())
				.append(": ")
				.append(lan.getLine())
				.append("\n");
		}
		
		// Remove the last \n.
		if (sb.length()>0) {
			sb.setLength(sb.length()-1);
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	public void outputGuts()
	{
		System.out.println(this.toString());
	}
	
	
	public static class LineAndNumber
	{
		private int number;
		private String line;
		private String hashThingy;
		
		public LineAndNumber(int lineNumber, String line) {
			this.number=lineNumber;
			this.line=line;
			
			hashThingy=this.number + "_" + line;
		}
		
		public int getNumber() {
			return number;
		}
		public String getLine() {
			return line;
		}
		
		@Override
		public int hashCode() {
			return hashThingy.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			return hashThingy.equals(o);
		}
	}
	
	
	public static final void maina(String[] args)
			throws Exception
	{
		ActivityLog al;
		
		al=new ActivityLog(2);
		
		al.addLine("first line");
		al.addLine("second line");
		al.addLine("third line");
		
		al.outputGuts();
		
		al=new ActivityLog(5);
		al.addLine("first line");
		al.addLine("second line");
		al.addLine("third line");
		al.addLine("fourth line");
		al.addLine("fifth line");
		System.out.println("al.getLastUsedLineNumber()="+al.getLastUsedLineNumber());
		System.out.println("al.getLinesFrom(2)=["+outputList(al.getLinesFrom(2))+"]");
		System.out.println("al.getLastLines(2)=["+outputList(al.getLastLines(2))+"]");
	}
	
	private static String outputList(List<LineAndNumber> lanList) {
		StringBuilder sb;
		
		sb=new StringBuilder(100);
		sb.append("ActivityLog: [");
		for (LineAndNumber lan: lanList) {
			sb.append(lan.getNumber())
				.append(": ")
				.append(lan.getLine())
				.append("\n");
		}
		
		// Remove the last \n.
		if (sb.length()>0) {
			sb.setLength(sb.length()-1);
		}
		sb.append("]");
		
		return sb.toString();
	}
	
}