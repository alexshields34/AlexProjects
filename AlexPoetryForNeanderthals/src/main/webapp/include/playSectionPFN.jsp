<%-- This jsp resolves to js.

This is for storing the code to render the play section.

 --%>
<%@page import="alex.pfn.score.ScoreType"%>
<%@page import="alex.pfn.GameState"%>
<%@page language="java" contentType="text/javascript" %>

<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@page import="org.springframework.web.context.support.SpringBeanAutowiringSupport"%>
<%@page import="alex.pfn.FEHelper"%>
<%@page import="alex.pfn.GameServer"%>
<%@page import="alex.pfn.Constants"%>
<%@page import="alex.pfn.player.Player"%>

<%!
// I found this on this site, which allows for adding AutoWired beans to a jsp.
// https://stackoverflow.com/questions/2139121/what-is-the-cleanest-way-to-autowire-spring-beans-in-a-jsp
public void jspInit() 
{
    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
    	getServletContext());
}


@Autowired
private GameServer gameServer;
@Autowired
private FEHelper feHelper;

%>
<%
String playerId;
boolean isSelfDirector;

// Set a player id.  We do this to uniquely identify players, and also because players
// aren't required to log in.
playerId=(String)session.getAttribute(Constants.playerIdSessionAttributeName);



isSelfDirector = gameServer.getPlayerGroup().isDirector(playerId);

%>




// ------------------ Event handlers ---------------------
function scoreCard1Pt(event)
{
	scoreCard("<%= ScoreType.normal1 %>");
}
function scoreCard3Pt(event)
{
	scoreCard("<%= ScoreType.normal3 %>");
}
function scoreCardFailure(event)
{
	scoreCard("<%= ScoreType.failed %>");
}





// ----------------- Regular functions --------------------


function fillPlaySection(statusResponse)
{
	fillPlaySectionTeamList(statusResponse);
	
	fillPlaySectionCards(statusResponse);
	
	fillTimerSection(statusResponse);
	
	fillPlaySectionCenterArea(statusResponse);
}

function fillPlaySectionCenterArea(statusResponse)
{
	fillPlaySectionCurrentPlayerAndTeam(statusResponse);
	fillPlaySectionUpcomingTurns(statusResponse);
}

function fillPlaySectionCurrentPlayerAndTeam(statusResponse)
{
	let currentPlayerDiv, displayNameSpan, playerObject;
	
	if (globalModIds.currentPlayerModId != statusResponse.teamInfo.currentPlayerModId
		&& statusResponse.teamInfo.currentPlayerId)
	{
	
		currentPlayerDiv = document.getElementById("currentPlayerDiv");
		currentPlayerDiv.innerHTML = "";
		
		playerObject = globalPlayerInfo[statusResponse.teamInfo.currentPlayerId];
		
		displayNameSpan = buildDisplayNameSpan(playerObject,
			false,
			false,
			true,
			true);
			
		
		currentPlayerDiv.appendChild(displayNameSpan);
	}
}

function fillPlaySectionUpcomingTurns(statusResponse)
{
	let remainingTurnsSpan, upcomingTurnsContainer,
		shouldRerenderUpcomingTurns, player, br, playerCounter,
		span;
	
	shouldRerenderUpcomingTurns = statusResponse.teamInfo.teamMembershipModId != globalModIds.teamMembershipModId
		|| statusResponse.teamInfo.teamListModId != globalModIds.teamListModId
		|| statusResponse.teamInfo.currentPlayerModId != globalModIds.currentPlayerModId;

	if (shouldRerenderUpcomingTurns) {
		remainingTurnsSpan = document.getElementById("remainingTurnsSpan");
		remainingTurnsSpan.innerHTML = statusResponse.teamInfo.remainingTurnsIncludingCurrent;
		
		upcomingTurnsContainer = document.getElementById("upcomingTurnsContainer");
		upcomingTurnsContainer.innerHTML = "";
		
		playerCounter = 0;
		for (const playerId of statusResponse.teamInfo.upcomingTurns) {
			if (playerCounter>0) {
				br = document.createElement("br");
				upcomingTurnsContainer.appendChild(br);
			}
		
			player = globalPlayerInfo[playerId];
			span = buildDisplayNameSpan(player, false, false, true, true);
			upcomingTurnsContainer.appendChild(span);
			playerCounter++;
		}
	}
}





const timerBackgroundTabooWasSaidCSSImage = "url('media/taboo_was_said2.png')";
const timerBackgroundPausedCSSImage = "url('media/paused2.png')";
const timerBackgroundTimesUpCSSImage = "url('media/times_up_80.png')";
function fillTimerSection(statusResponse)
{
	let element, textState, newColor, buttonToDisplay, secondsSpan,
		shouldDisplayPausedBackgroundImage, timerTd, shouldShowStartTimerButton,
		shouldShowPauseTimerButton, shouldShowResumeTimerButton,
		shouldRerenderButtonsAndStuff;
	
	// Always fill the seconds remaining.
	if (statusResponse.remainingSeconds != null) {
		secondsSpan = document.getElementById("secondsSpan");
		secondsSpan.innerHTML = statusResponse.remainingSeconds;
	}
	
	shouldRerenderButtonsAndStuff = statusResponse.gameStateModId != globalModIds.gameStateModId
		|| statusResponse.timeTrackerModId != globalModIds.timeTrackerModId
		|| statusResponse.teamInfo.currentPlayerModId != globalModIds.currentPlayerModId;
		
	// The state label and buttons
	if (shouldRerenderButtonsAndStuff)
	{
		shouldDisplayPausedBackgroundImage = false;
		shouldShowStartTimerButton = shouldShowPauseTimerButton = shouldShowResumeTimerButton = false;
	
		if (statusResponse.gameState == "<%= String.valueOf(GameState.running) %>") {
			newColor = "green";
			textState = "RUNNING";
			shouldShowPauseTimerButton = isSelfCurrentPlayer(statusResponse);
		} else if (statusResponse.gameState == "<%= String.valueOf(GameState.failureHappened) %>"
			|| statusResponse.gameState == "<%= String.valueOf(GameState.paused) %>") {
			newColor = "red";
			textState = "PAUSED";
			shouldDisplayPausedBackgroundImage = true;
			shouldShowResumeTimerButton = isSelfCurrentPlayer(statusResponse);
		} else if (statusResponse.gameState == "<%= String.valueOf(GameState.waitingToStartTurn) %>") {
			newColor = "green";
			textState = "WAITING";
			shouldShowStartTimerButton = isSelfCurrentPlayer(statusResponse);
		} else {
			newColor = "red";
			textState = "GAME NOT RUNNING";
		}
		
		element = document.getElementById("timerStateLabel");
		element.innerHTML = textState;
		element.style.color = newColor;
		
		timerTd = document.getElementById("timerTd");
		if (shouldDisplayPausedBackgroundImage) {
			timerTd.style.backgroundImage = timerBackgroundPausedCSSImage;
		} else {
			timerTd.style.backgroundImage = "none";
		}
	
		
		button = document.getElementById("startTimerButton");
		button.style.display = (shouldShowStartTimerButton?"inline":"none");
		
		button = document.getElementById("pauseTimerButton");
		button.style.display = (shouldShowPauseTimerButton?"inline":"none");
		
		button = document.getElementById("resumeTimerButton");
		button.style.display = (shouldShowResumeTimerButton?"inline":"none");
		
	}
}


function fillPlaySectionCards(statusResponse)
{
	let element, target1PtAltText, target3PtAltText, shouldRenderCardInfo;
	
	
	shouldRenderCardInfo = (globalModIds.currentCardModId != statusResponse.currentCardModId
		|| globalModIds.teamMembershipModId != statusResponse.teamInfo.teamMembershipModId
		|| globalModIds.currentPlayerModId != statusResponse.teamInfo.currentPlayerModId
		|| globalModIds.gameStateModId != statusResponse.gameStateModId
		|| globalModIds.timeTrackerModId != statusResponse.timeTrackerModId);
	
	if (shouldRenderCardInfo) {
		
		target1PtAltText="";
		target3PtAltText="";
			
		if (isGameNotRunning(statusResponse.gameState)) {
			target1PtAltText="(GAME NOT RUNNING)";
			target3PtAltText="(GAME NOT RUNNING)";
		} else if (isSelfCurrentPlayer(statusResponse)) {
			if (statusResponse.gameState == "<%= GameState.waitingToStartTurn %>") {
				target1PtAltText="(Click the start button to see your card)";
				target3PtAltText="(Click the start button to see your card)";
			} else if (statusResponse.gameState == "<%= GameState.paused %>") {
				target1PtAltText="(GAME IS PAUSED)";
				target3PtAltText="(GAME IS PAUSED)";
			}
		} else if (globalVars.selfPlayerObject.teamId 
				&& statusResponse.teamInfo.currentTeamId
				&& globalVars.selfPlayerObject.teamId == statusResponse.teamInfo.currentTeamId)
		{
			target1PtAltText="(Listen to your teammate)";
			target3PtAltText="(Listen to your teammate)";
		}
		
		element = document.getElementById("currentCard1PointPart");
		element.innerHTML = target1PtAltText?target1PtAltText:(statusResponse.currentCard?statusResponse.currentCard.target1Pt:"");
		
		element = document.getElementById("currentCard3PointPart");
		element.innerHTML = target3PtAltText?target3PtAltText:(statusResponse.currentCard?statusResponse.currentCard.target3Pt:"");
	}
}


/**
 * Don't destroy the team and player list table.
 */
function updatePlayerAndTeamScoresOnly(statusResponse)
{
//UNFINISHED
	let parentTable, teamCounter;
	
	parentTable = document.getElementById("playSection_TeamListTable");
	
 	teamCounter = 0;
	for (const oneTeam of statusResponse.teamInfo.teamList) {
		
		teamMemberCounter = 0;
		isLastTeamMember = false;
		for (const memberPlayerId of oneTeam.members) {
		
		
			if (teamMemberCounter == 0) {
				
				// Output the score for the team.

				td.innerHTML = oneTeam.numericScore;					
				td.classList.add("rightBlackBorder");					
				td.classList.add("bottomBlackBorder");
				td.setAttribute("rowspan", numberOfTeamMembers);
			}
			
			if (teamMemberCounter+1 == numberOfTeamMembers) {
				isLastTeamMember = true;
			}
			
			
			memberTd = document.createElement("td");
			tr.appendChild(memberTd);
			span = buildDisplayNameSpan(globalPlayerInfo[memberPlayerId], false, false, true, true);
			memberTd.appendChild(span);
			memberTd.classList.add("rightBlackBorder");
			if (isLastTeamMember) {
				memberTd.classList.add("bottomBlackBorder");
			}
			
			memberTd = document.createElement("td");
			tr.appendChild(memberTd);
			memberTd.innerHTML = globalPlayerInfo[memberPlayerId].turnsUsed;
			memberTd.classList.add("rightBlackBorder");
			if (isLastTeamMember) {
				memberTd.classList.add("bottomBlackBorder");
			}
			
			memberTd = document.createElement("td");
			tr.appendChild(memberTd);
			memberTd.innerHTML = globalPlayerInfo[memberPlayerId].scoredOnePointCards;
			memberTd.dataset.playerId = memberPlayerId;
			memberTd.classList.add("rightBlackBorder");
			if (isLastTeamMember) {
				memberTd.classList.add("bottomBlackBorder");
			}
			
			memberTd = document.createElement("td");
			tr.appendChild(memberTd);
			memberTd.innerHTML = globalPlayerInfo[memberPlayerId].scoredThreePointCards;
			memberTd.dataset.playerId = memberPlayerId;
			memberTd.classList.add("rightBlackBorder");
			if (isLastTeamMember) {
				memberTd.classList.add("bottomBlackBorder");
			}
			
			memberTd = document.createElement("td");
			tr.appendChild(memberTd);
			memberTd.innerHTML = globalPlayerInfo[memberPlayerId].cardsFailed;
			memberTd.dataset.playerId = memberPlayerId;
			if (isLastTeamMember) {
				memberTd.classList.add("bottomBlackBorder");
			}
			
			teamMemberCounter++;
		}
		
		teamCounter++;
	}
}


	
function fillPlaySectionTeamList(statusResponse)
{
	let parentTable, td, shouldRerenderTeamSection,
		tr, memberTable, memberTr, memberTd, shouldUpdateScoresOnly,
		span, isLastTeam, isLastTeamMember,
		teamMemberCounter, numberOfTeamMembers, teamCounter, numberOfTeams;
	
	shouldRerenderTeamSection = statusResponse.teamInfo.teamList
		&& (statusResponse.teamInfo.teamListModId != globalModIds.teamListModId
		|| statusResponse.teamInfo.teamMembershipModId != globalModIds.teamMembershipModId
		|| statusResponse.teamInfo.currentPlayerModId != globalModIds.currentPlayerModId
		|| statusResponse.playerInfo.playerDisplayNameModId != globalModIds.playerDisplayNameModId);
		
	shouldUpdateScoresOnly = statusResponse.teamInfo.teamList
		&& !shouldRerenderTeamSection
		&& statusResponse.scoreModId != globalModIds.scoreModId;
		
	// If the only thing that changed was the score, then update scores only.
	// Don't destroy the team list table.
	//if (shouldUpdateScoresOnly) {
	//	updatePlayerAndTeamScoresOnly(statusResponse);
	//}
	
	if (shouldRerenderTeamSection || shouldUpdateScoresOnly)
	{

		parentTable = document.getElementById("playSection_TeamListTable");
		parentTable.innerHTML = "";
		
		// Header row first.
		tr = duplicateElement(document.getElementById("playSection_memberTableColumnHeadings"), true);
		parentTable.appendChild(tr);
		
  
  
		isLastTeam = false; 
  		teamCounter = 0;
  		numberOfTeams = getListSize(statusResponse.teamInfo.teamList);
		for (const oneTeam of statusResponse.teamInfo.teamList) {
			if (teamCounter+1 == numberOfTeams) {
				isLastTeam = true;
			}
			
			
			numberOfTeamMembers = getListSize(oneTeam.members);
			
			teamMemberCounter = 0;
			isLastTeamMember = false;
			for (const memberPlayerId of oneTeam.members) {
			
				tr = document.createElement("tr");
				parentTable.appendChild(tr);
			
				if (teamMemberCounter == 0) {
				
					// Set the values for the first td, such as rowspan and the bottom border.
					td = document.createElement("td");
					tr.appendChild(td);
					span = buildTeamNameSpan(oneTeam, false, true, true);
					td.appendChild(span);
					td.classList.add("rightBlackBorder");
					td.classList.add("bottomBlackBorder");
					td.setAttribute("rowspan", numberOfTeamMembers);
					
					// Output the score for the team.
					td = document.createElement("td");
					tr.appendChild(td);
					td.innerHTML = oneTeam.numericScore;
					td.classList.add("rightBlackBorder");
					td.classList.add("bottomBlackBorder");
					td.setAttribute("rowspan", numberOfTeamMembers);
				}
				
				if (teamMemberCounter+1 == numberOfTeamMembers) {
					isLastTeamMember = true;
				}
				
				
				memberTd = document.createElement("td");
				tr.appendChild(memberTd);
				span = buildDisplayNameSpan(globalPlayerInfo[memberPlayerId], false, false, true, true);
				memberTd.appendChild(span);
				memberTd.classList.add("rightBlackBorder");
				if (isLastTeamMember) {
					memberTd.classList.add("bottomBlackBorder");
				}
				
				memberTd = document.createElement("td");
				tr.appendChild(memberTd);
				memberTd.innerHTML = globalPlayerInfo[memberPlayerId].turnsUsed;
				memberTd.classList.add("rightBlackBorder");
				if (isLastTeamMember) {
					memberTd.classList.add("bottomBlackBorder");
				}
				
				memberTd = document.createElement("td");
				tr.appendChild(memberTd);
				memberTd.innerHTML = globalPlayerInfo[memberPlayerId].scoredOnePointCards;
				memberTd.dataset.playerId = memberPlayerId;
				memberTd.classList.add("rightBlackBorder");
				if (isLastTeamMember) {
					memberTd.classList.add("bottomBlackBorder");
				}
				
				memberTd = document.createElement("td");
				tr.appendChild(memberTd);
				memberTd.innerHTML = globalPlayerInfo[memberPlayerId].scoredThreePointCards;
				memberTd.dataset.playerId = memberPlayerId;
				memberTd.classList.add("rightBlackBorder");
				if (isLastTeamMember) {
					memberTd.classList.add("bottomBlackBorder");
				}
				
				memberTd = document.createElement("td");
				tr.appendChild(memberTd);
				memberTd.innerHTML = globalPlayerInfo[memberPlayerId].cardsFailed;
				memberTd.dataset.playerId = memberPlayerId;
				if (isLastTeamMember) {
					memberTd.classList.add("bottomBlackBorder");
				}
				
				teamMemberCounter++;
			}
			
			teamCounter++;
		}
	}
}
	
	
	
