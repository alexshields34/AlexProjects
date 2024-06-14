package alex.pfn.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import alex.pfn.image.ImageSpec;
import alex.pfn.player.Player;
import alex.pfn.Configuration;
import alex.pfn.Constants;
import alex.pfn.FEEvent;
import alex.pfn.GlobalState;
import alex.pfn.image.ImagePrefixType;
import alex.pfn.rest.beans.TeamDTO;

public class Team {	
	private final static AtomicInteger teamIdCounter=new AtomicInteger(1);
	private static String[] colorArray;
	
//	@Deprecated
//	private static final int NO_PREVIOUS_PLAYER=-1;
	
	static {
		colorArray=getTeamColors();
	}

	private final Object teamChangeBlock=new Object();

	private final Random random=new Random(System.currentTimeMillis());
	
	private final String teamId;
	private String name;
	
	// The back end doesn't care what's in here.  It's just sent to the 
	// front end whenever the front end requests information about teams.
	// For example, color goes here.	
	private String jsonDataForFrontEnd;
	
	private final ArrayList<String> memberPlayerIds;
	private final ArrayList<Player> members;
	
	
	private int memberPlayerIdsModId;
	
	private String cssColor;

	// For the current game, all members of the team will get one of
	// these as their end game image.
	private EnumMap<ImagePrefixType, ImageSpec> endGameImages;
	
	private boolean isDeleted;
	
	// If it's ok if the team is deleted, this is true.  Else, false.
	private final boolean isDeletable;
	
	/**
	 * Default to deletable.
	 * @param teamId
	 * @param name
	 */
	public Team(final String teamId,
			final String name)
	{
		this(teamId, name, true);
	}
	
	/**
	 * 
	 * @param teamId May be null.  If null, then a new teamId is generated.
	 * @param name If null, then the team name is the same as its id.
	 * @param isDeletable If true, then this team may not be deleted using deleteTeam.
	 */
	public Team(final String teamId,
			final String name,
			final boolean isDeletable)
	{
		
		synchronized(teamIdCounter) {
				
			memberPlayerIdsModId=-1;
	
			cssColor=generateCssColor();
			
			if (teamId==null) {
				this.teamId=buildNewTeamId();
			} else {
				this.teamId=teamId;
			}
			
			if (name==null) {
				this.name=this.teamId.replace('_', ' ');
			} else {
				this.name=name;
			}
			
			members=new ArrayList<Player>();
			memberPlayerIds=new ArrayList<String>();
			
			isDeleted=false;
			
			this.isDeletable=isDeletable;
		}
	}
	
	
	public boolean isDeleteable()
	{
		return this.isDeletable;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * Every time a new game is generated, this must be updated.
	 * @param egi
	 */
	public void setEndGameImages(EnumMap<ImagePrefixType, ImageSpec> egi) {
		this.endGameImages=egi;
	}
	
	/**
	 * Send the event only to members of this team.
	 * 
	 * @param baseEvent A new event will be created and will be copied from this one.
	 * @param endGameVictoryType If the game is over, this should be set.  Otherwise, this must be null.
	 */
	public void addEventForAllPlayers(final FEEvent baseEvent,
			final ImagePrefixType endGameVictoryType)
	{
		FEEvent e;
		
		e=new FEEvent(baseEvent);
		
		switch (endGameVictoryType) {
		case draw:
			e.setImageUrl(this.endGameImages.get(ImagePrefixType.draw).getPath());
			break;
		case loser:
			e.setImageUrl(this.endGameImages.get(ImagePrefixType.loser).getPath());
			break;
		case winner:
			e.setImageUrl(this.endGameImages.get(ImagePrefixType.winner).getPath());
			break;
		default:
			// Set nothing.
		}
		
		for (Player p: this.members) {
			p.addEvent(e);
		}
	}
	
	
	public void resetForNewGame(String gameId, EnumMap<ImagePrefixType, ImageSpec> egi) {
		synchronized(teamChangeBlock) {
			this.setEndGameImages(egi);
		}
	}
	
	
	private String generateCssColor() {

		synchronized(teamChangeBlock) {
			int r, g, b;
			String retVal, rString, gString, bString;
			if (teamIdCounter.get()>=colorArray.length) {
				r=random.nextInt(256);
				g=random.nextInt(256);
				b=random.nextInt(256);
				
				rString=Integer.toHexString(r);
				if (rString.length()==1) {
					rString="0"+rString;
				}
				
				gString=Integer.toHexString(g);
				if (gString.length()==1) {
					gString="0"+gString;
				}
				
				bString=Integer.toHexString(b);
				if (bString.length()==1) {
					bString="0"+bString;
				}
				
				retVal="#"+rString+gString+bString;
			} else {
	
				retVal=colorArray[teamIdCounter.get()];
			}
		
			return retVal;
		}
	}
	
	
//	/**
//	 * If there are 0 team members, this will throw an exception.
//	 * @return
//	 */
//	@Deprecated
//	public Player getCurrentClueGiver() {
//		synchronized(teamChangeBlock) {
//			if (currentClueGiver >= members.size()) {
//				currentClueGiver=0;
//			}
//			this.assignCurrentPlayerRole(members.get(currentClueGiver).getPlayerId());
//			return members.get(currentClueGiver);
//		}
//	}
	
//	/**
//	 * This sets the current clue giver.  This is intended
//	 * to be called when creating a new game only. This is 
//	 *  
//	 * @param indexOfPlayer
//	 */
//	@Deprecated
//	public void setCurrentPlayerForNewGame(int indexOfPlayer)
//	{
//		synchronized(teamChangeBlock) {
//			this.currentClueGiver=indexOfPlayer;
//		}
//	}
	
	
//	/**
//	 * This method updates currentClueGiver.
//	 * @return
//	 */
//	@Deprecated
//	public void advanceClueGiver() {
//		synchronized(teamChangeBlock) {
//			currentClueGiver++;
//			if (currentClueGiver>=members.size()) {
//				currentClueGiver=0;
//			}
//			
//			this.assignCurrentPlayerRole(this.memberPlayerIds.get(currentClueGiver));
//		}
//	}
	
	
    private static String[] getTeamColors() {
    	String s;
    	String[] colors;
    	

		s=Configuration.getConfigProperties().getProperty(Constants.config_client_team_colors);
		colors=s.split(",");
    	
    	
    	return colors;
    }
    
    
	
	
	public String getCssColor() {
		return cssColor;
	}

	public void setCssColor(String cssColor) {
		this.cssColor = cssColor;
	}

	public String getTeamId() {
		return teamId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getJsonDataForFrontEnd() {
		return jsonDataForFrontEnd;
	}

	public void setJsonDataForFrontEnd(String jsonDataForFrontEnd) {
		this.jsonDataForFrontEnd = jsonDataForFrontEnd;
	}
	
	
	public ArrayList<String> getPlayerIdList() {
		return this.memberPlayerIds;
	}
	
	public void addPlayer(final Player player) {
		synchronized(teamChangeBlock) {
			this.members.add(player);
			this.memberPlayerIds.add(player.getPlayerId());
			
			Collections.sort(this.memberPlayerIds);
			Collections.sort(this.members);
			
			memberPlayerIdsModId++;
		}
	}
	
	public void removePlayer(final Player player) {
		synchronized(teamChangeBlock) {
			player.setTeam(null);
			this.members.remove(player);
			this.memberPlayerIds.remove(player.getPlayerId());
			
			Collections.sort(this.memberPlayerIds);
			Collections.sort(this.members);
			
			memberPlayerIdsModId++;
		}
	}
	
	public void removeAllPlayers() {
		synchronized(teamChangeBlock) {
			
			while (!this.members.isEmpty()) {
				removePlayer(this.members.get(0));
			}
			
			memberPlayerIdsModId++;
		}
	}
	
	/**
	 * Return true if the deletion operation completed.
	 * @return
	 */
	public boolean delete()
	{
		boolean retVal;
		
		retVal=false;
		
		if (this.isDeletable) {
			this.removeAllPlayers();
			this.isDeleted=true;
			retVal=true;
		}
		
		return retVal;
	}
	

	public static String buildNewTeamId()
	{
		synchronized(teamIdCounter) {
			int newTeamNumber;
			
			newTeamNumber=Integer.valueOf(teamIdCounter.getAndAdd(1));
			
			return "Team_"+newTeamNumber;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return o!=null && o instanceof Team && ((Team)o).teamId.equals(this.teamId);
	}
	
	@Override
	public int hashCode() {
		return teamId.hashCode();
	}
	
	public TeamDTO buildTeamDTO(boolean isCurrentTeam)
	{
		TeamDTO retVal;
		
		retVal=new TeamDTO(teamId, this.getName());
		
		retVal.setMembersModId(memberPlayerIdsModId);
		retVal.getMembers().addAll(this.memberPlayerIds);
		retVal.setJsonDataForFrontEnd(jsonDataForFrontEnd);
		retVal.setCssColor(cssColor);
		retVal.setNumericScore(GlobalState.getScoreLog().getScoreForTeam(teamId));
		retVal.setIsCurrentTeam(Boolean.valueOf(isCurrentTeam));
		retVal.setIsDeletable(isDeletable);
		retVal.setIsDeleted(isDeleted);
		
		return retVal;
	}
}
