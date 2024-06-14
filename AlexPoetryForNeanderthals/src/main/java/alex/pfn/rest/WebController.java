package alex.pfn.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import alex.pfn.GlobalState;
import alex.pfn.Constants;
import alex.pfn.GameServer;
import alex.pfn.rest.beans.PostBody;
import alex.pfn.rest.beans.StatusResponse;

/**
 * This is the interface between the front end and the back end.  For the player and the server.
 * 
 * The methods in this class shouldn't handle any game logic.  This should
 * just be an intermediary to send off requests to GameServer.  GlobalState 
 * shouldn't be called here either.  That should be done in GameServer and other
 * methods that GameServer calls.
 * 
 * @author alex
 *
 */
@RestController
public class WebController {
	
	// The value of this variable is filled by Spring.
	@Autowired
	private GameServer gameServer;
	
	
	
	@PostMapping("/unassignTeamMembers")
    public StatusResponse unassignTeamMembers(HttpServletRequest hsr,
			HttpServletResponse hsresp,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
    	String requestingPlayerId;
    	requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
    	
		gameServer.unassignPlayers(requestingPlayerId, changesBody.getSelectedPlayerIdList());
    	
		return generateStatus(requestingPlayerId);
	}
	

	@PostMapping("/resumeTimer")
    public StatusResponse resumeTimer(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		System.out.println("resumeTimer:: ENTERING");
    	String requestingPlayerId;
    	
    	requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
    	
    	gameServer.resumeTimer(requestingPlayerId);
    	
		return generateStatus(requestingPlayerId);
	}
	
	
	@PostMapping("/startTimer")
    public StatusResponse startTimer(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		System.out.println("startTimer:: ENTERING");
    	String requestingPlayerId;

    	requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
    	
    	gameServer.startTimer(requestingPlayerId);
    	
		return generateStatus(requestingPlayerId);
	}
    
	@PostMapping("/pauseTimer")
    public StatusResponse pauseTimer(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		System.out.println("pauseTimer:: ENTERING");
    	String requestingPlayerId;
    	
    	requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
    	
    	gameServer.pauseTimer(requestingPlayerId);
    	
		return generateStatus(requestingPlayerId);
	}
	
	
	@PostMapping("changeScore")
    public StatusResponse changeScore(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		System.out.println("changeScore:: ENTERING");
    	String requestingPlayerId;
    	
    	requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
    	
    	gameServer.changeScore(requestingPlayerId, changesBody.getScoreChangeList());
    	
		return generateStatus(requestingPlayerId);
	}
	
	
    
    
	
	/**
	 * We need the player doing the scoring, and what cardId she is
	 * trying to score.  We want the cardId because it's possible that the
	 * current user is seeing something old, and we want to know what card 
	 * that person is scoring.  This is especially important for scoring a
	 * failure.
	 * 
	 * @param hsr
	 * @param hs
	 * @param changesBody
	 * @return
	 */
	@PostMapping(value="scoreCard", consumes = "application/json")
	public StatusResponse scoreCard(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		String requestingPlayerId;
		StatusResponse retVal;
		
		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		gameServer.scoreCard(requestingPlayerId, changesBody.getScoreType(), changesBody.getCardId());
		
		retVal=generateStatus(requestingPlayerId, changesBody.getLastKnownLogLine());
		
		return retVal;
	}
	
	
    @PostMapping("/generateNewGame")
	public StatusResponse generateNewGame(HttpServletRequest hsr,
			HttpServletResponse hsresp,
			HttpSession hs,
			@RequestBody PostBody changesBody) 
	{

    	String requestingPlayerId;
    	StatusResponse retVal;
		
    	requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
    	
    	gameServer.generateNewGame(requestingPlayerId,
    			changesBody.getMaxTurnsForMembersOfLargestTeam(),
    			changesBody.getSelectedFileList());
    	
    	retVal=generateStatus(requestingPlayerId);
    	
    	return retVal;
	}
    
	
	@GetMapping("setDisplayName")
	public StatusResponse setDisplayName(HttpServletRequest hsr,
			HttpServletResponse hsresp,
			HttpSession hs,
			@RequestParam(required = true, name = "playerId") String playerIdStr,
			@RequestParam(required = true, name = "displayName") String displayName)
	{
		System.out.println(this.getClass().getName() + ":: setDisplayName:: ENTERING");
		StatusResponse retVal;
		String playerId;
		
		
		// This is an experiment to see if I could have the calling web page go
		// to start.jsp.
		playerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		if (playerId==null) {
			try {
				hsresp.sendRedirect("start.jsp");
				return null;
			} catch (IOException ioe) {
				// Don't output anything.
			}
		}
		
		gameServer.setDisplayName(playerIdStr, displayName);
		
		retVal=generateStatus(playerIdStr);
		
		return retVal;
	}
	
	

	@PostMapping(value="/deleteTeam", consumes = "application/json")
	public StatusResponse deleteTeam(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		
		StatusResponse retVal;
		String requestingPlayerId;

		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		gameServer.deleteTeam(requestingPlayerId, changesBody.getTeamId());
		
		retVal=generateStatus(requestingPlayerId);
		
		return retVal;
	}
	
	

	@PostMapping(value="/randomizeTeamAssignment", consumes = "application/json")
	public StatusResponse randomizeTeamAssignment(HttpServletRequest hsr,
			HttpServletResponse hsresp,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		StatusResponse retVal;
		String requestingPlayerId;

		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		gameServer.randomizeTeamAssignment(requestingPlayerId);
		
		retVal=generateStatus(requestingPlayerId);
		
		return retVal;
	}
	
	@PostMapping(value="/abortGame", consumes = "application/json")
	public StatusResponse abortGame(HttpServletRequest hsr,
			HttpServletResponse hsresp,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		StatusResponse retVal;
		String requestingPlayerId;

		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		gameServer.abortGame(requestingPlayerId);
		
		retVal=generateStatus(requestingPlayerId);
		
		return retVal;
	}
	
	

	/**
	 * 
	 * @param hsr
	 * @param hs 
	 * @param changesBody Should have displayName and playerIds in it.
	 * @return
	 */
	@PostMapping(value="/addTeamMembers", consumes = "application/json")
	public StatusResponse addTeamMembers(HttpServletRequest hsr,
			HttpServletResponse hsresp,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		
		
		StatusResponse retVal;
		String requestingPlayerId;

		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		// This is an experiment to see if I could have the calling web page go
		// to start.jsp.
		if (requestingPlayerId==null) {
			try {
				hsresp.sendRedirect("start.jsp");
				return null;
			} catch (IOException ioe) {
				// Don't output anything.
			}
		}
		
		gameServer.addTeamMembers(requestingPlayerId, changesBody.getSelectedPlayerIdList(), changesBody.getTeamId());
		
		
		retVal=generateStatus(requestingPlayerId, changesBody.getLastKnownLogLine());
		
		return retVal;
	}
	
	
	/**
	 * 
	 * @param hsReq
	 * @param hs 
	 * @param hsResp
	 * @param changesBody Should have displayName and playerId in it.
	 * @return
	 */
	@PostMapping(value="createPlayerAndSetDispalyName", consumes = "application/json")
	public StatusResponse createPlayerAndSetDispalyName(HttpServletRequest hsReq,
			HttpServletResponse hsResp,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		System.out.println("createPlayerAndSetDispalyName:: playerId=["
					+changesBody.getPlayerId()
					+"], displayName=["
					+changesBody.getDisplayName()
					+"]");
		
		StatusResponse retVal;
		
		gameServer.getPlayerGroup().getOrAddPlayer(changesBody.getPlayerId(), null, changesBody.getDisplayName(), hsReq);
		
		// TESTING ONLY. Create a second player if the player's playerId is Player_0.
//		if (changesBody.getPlayerId().endsWith("_0")) {
//			gameServer.getPlayerGroup().getOrAddPlayer("junkPlayer", null, "junkPlayer", hsr);
//		}
		
		hs.setAttribute(Constants.wasDisplayNameSetSessionAttributeName, Boolean.TRUE);
		
		GlobalState.getActivityLog().addLine(gameServer.getPlayerGroup().getPlayer(changesBody.getPlayerId()),
				"Created "
				+ gameServer.getPlayerGroup().getPlayer(changesBody.getPlayerId()).getDisplayName());
		
		retVal=generateStatus(changesBody.getPlayerId());
		
		return retVal;
	}
	
	
	
	@PostMapping(value="/addNewTeam", consumes = "application/json")
	public StatusResponse addNewTeam(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{

		StatusResponse retVal;
		String requestingPlayerId;

		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		gameServer.addNewTeam(requestingPlayerId, changesBody.getTeamName());
		
		retVal=generateStatus(requestingPlayerId);
		
		return retVal;
	}
	
	@PostMapping(value="supplantCurrentPlayer", consumes = "application/json")
	public StatusResponse supplantCurrentPlayer(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		StatusResponse retVal;
		String requestingPlayerId;

		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		gameServer.supplantCurrentPlayer(requestingPlayerId, changesBody.getPlayerId());
		
		retVal=generateStatus(requestingPlayerId);
		
		return retVal;
	}
	
	
	@PostMapping(value="endCurrentPlayerTurn", consumes = "application/json")
	public StatusResponse endCurrentPlayerTurn(HttpServletRequest hsr,
			HttpSession hs,
			@RequestBody PostBody changesBody)
	{
		StatusResponse retVal;
		String requestingPlayerId;

		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		gameServer.endCurrentPlayerTurn(requestingPlayerId);
		
		retVal=generateStatus(requestingPlayerId);
		
		return retVal;
	}
	
	
	

	
	
	@GetMapping("getStatus")
	public StatusResponse getStatus(HttpServletRequest hsr,
			HttpSession hs,
			@RequestParam(required = false, name = "lastKnownLogLine") Integer lastKnownLogLine,
			@RequestParam(required = false, name = "scoreModId") Integer scoreModId)
	{
		StatusResponse retVal;
		String requestingPlayerId;
		
		requestingPlayerId=(String)hs.getAttribute(Constants.playerIdSessionAttributeName);
		
		retVal=gameServer.generateStatus(requestingPlayerId, lastKnownLogLine, scoreModId);
		
		// If the user has been deleted, remove his playerId from the session.
		if (retVal!=null && retVal.getPlayerInfo().getDeletedPlayers().contains(requestingPlayerId)) {
			hs.removeAttribute(Constants.playerIdSessionAttributeName);
			hs.removeAttribute(Constants.wasDisplayNameSetSessionAttributeName);
			
			retVal=null;
		}
		
		return retVal;
	}
	
    
    


    
    
    private StatusResponse generateStatus(String requesterPlayerId)
    {
    	return generateStatus(requesterPlayerId,
        		null);
    }
    
    
    /**
     * @param requesterPlayerId
     * @param shouldSendPublicPlayerInfo
     * @param shouldSendPlayerScoreInfo
     * @return
     */
    private StatusResponse generateStatus(String requesterPlayerId,
    		Integer lastKnownLogLine)
    {
    	return gameServer.generateStatus(requesterPlayerId, lastKnownLogLine, null);
    	
    }
	
	
}
