<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="alex.pfn.PathBuilder"%>
<%@page import="alex.pfn.player.PlayerRole"%>
<%@page import="com.fasterxml.jackson.databind.DeserializationFeature"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="alex.pfn.image.ImageGroup"%>
<%@page import="alex.pfn.image.ImagePrefixType"%>
<%@page import="java.util.EnumMap"%>
<%@page import="java.util.Set"%>
<%@page import="java.io.FilenameFilter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.List"%>
<%@page import="alex.string.JsonUtil"%>
<%@page import="alex.pfn.Configuration"%>
<%@page import="alex.pfn.FEHelper"%>
<%@page import="alex.pfn.GameServer"%>
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@page import="org.springframework.web.context.support.SpringBeanAutowiringSupport"%>
<%@page import="alex.http.HttpDebugUtil"%>
<%@page import="alex.pfn.player.Player"%>
<%@page import="alex.pfn.Constants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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

/**
 * @return Returns json.
 */
private String getTeamColors() {
	String retVal, s;
	String[] colors;
	

	s=Configuration.getConfigProperties().getProperty(Constants.config_client_team_colors);
	colors=s.split(",");
	
	retVal=JsonUtil.arrayToJson(colors);
	
	return retVal;
}



private String getAllCardFileNamesAsSelectList()
{
	StringBuilder sb;
	List<String> allCardFileNames;
	
	
	sb=new StringBuilder(100);
	allCardFileNames=feHelper.getAllCardFileNames();
	
	sb.append("<select name='newGameFileList' id='newGameFileList'>");
	for (String cardFileName: allCardFileNames)
	{
		sb.append("<option value='")
			.append(cardFileName)
			.append("'>")
			.append(cardFileName)
			.append("</option>\n");
	}
	sb.append("</select>");
	
	return sb.toString();
	
}


private String getAllCardFileNamesAsCheckboxes(boolean shouldAddBrs)
{
	StringBuilder sb;
	List<String> allCardFileNames;
	
	
	sb=new StringBuilder(100);
	allCardFileNames=feHelper.getAllCardFileNames();
	
	for (String cardFileName: allCardFileNames)
	{
		sb.append("<label for='newGameCard_")
			.append(cardFileName)
			.append("'>");
		sb.append("<input type='checkbox' name='newGameCard' id='newGameCard_")
			.append(cardFileName)
			.append("' value='")
			.append(cardFileName)
			.append("'/>\n");
		sb.append(cardFileName)
			.append("</label>\n");
		
		if (shouldAddBrs) {
			sb.append("<br/>\n");
		}
	}
	
	return sb.toString();
}






private String getAllSoundFileNames_usingResourcePaths() {
	String retVal, fileName;
	URL url;
	Set<String> paths;
	ArrayList<String> fileList;
	int index, mp3Index;
	
	fileList=new ArrayList<String>();
   	paths=this.getServletContext().getResourcePaths("/"+Constants.soundDirectoryURLPath);
   	for (String onePath: paths) {
   		mp3Index=onePath.toLowerCase().lastIndexOf(".mp3");
   		if (mp3Index==-1) {
   			continue;
   		}
   		index=onePath.lastIndexOf('/'); 
   		fileName=onePath.substring(index+1);
   		fileList.add(fileName);
   	}
	
	retVal=JsonUtil.arrayToJson(fileList);
	
	return retVal;
}




/**
 * Find the winner, loser, draw images using resource paths.
 * @return json.
 */
private String getAllImageGroups() {
	
	String retVal;
	EnumMap<ImagePrefixType, ImageGroup> map;
// 	ImageGroup oneGroup;
//		Set<String> paths;
	
	ObjectMapper jsonObjectMapper;
	jsonObjectMapper=new ObjectMapper();
	jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	
	
// 	map=new EnumMap<ImagePrefixType, ImageGroup>(ImagePrefixType.class);
	map=feHelper.getAllImageGroupsAsMap();
	
// 	for (ImagePrefixType oneType: ImagePrefixType.values()) {
//			oneGroup=new ImageGroup(oneType, Configuration.getConfigProperties().getProperty(oneType.getConfigParameterForPrefix()));
//			map.put(oneType, oneGroup);
// 	}
	
//    	paths=this.getServletContext().getResourcePaths("/"+Constants.imageDirectoryURLPath);
//    	for (String onePath: paths) {
//    		for (ImagePrefixType oneType: ImagePrefixType.values()) {
//				oneGroup=map.get(oneType);
			
//    			if (onePath.contains(oneGroup.getImageFilePrefix())) {
//    				oneGroup.getImages().add(new ImageSpec(onePath));
//    			}
//     	}
//    	}
   	
   	try {
		retVal=jsonObjectMapper.writeValueAsString(map);
   	} catch (Exception e) {
   		retVal="";
   	}
   	
	return retVal;
}


%>
<%
Player player;
String playerId, dirParam, displayName, imageURLExpansionPlus, imageURLExpansionMinus,
	selfStarImagePath;
Boolean wasDisplayNameSet;
boolean isSelfDirector;

// Set a player id.  We do this to uniquely identify players, and also because players
// aren't required to log in.
playerId=(String)session.getAttribute(Constants.playerIdSessionAttributeName);
if (playerId==null) {
	playerId=Player.getNewPlayerId();

	session.setAttribute(Constants.playerIdSessionAttributeName, playerId);
}


displayName = request.getParameter("displayName");
if (displayName!=null && !displayName.isEmpty() && !displayName.isBlank()) {
	session.setAttribute(Constants.wasDisplayNameSetSessionAttributeName, Boolean.TRUE);
}

wasDisplayNameSet=(Boolean)session.getAttribute(Constants.wasDisplayNameSetSessionAttributeName);
wasDisplayNameSet=wasDisplayNameSet==null?Boolean.FALSE:wasDisplayNameSet;

// System.out.println("player.jsp:: time=["
// 		+(new java.util.Date().toString())
// 		+"], playerId=["
// 		+playerId
// 		+"], wasDisplayNameSet=["
// 		+wasDisplayNameSet
// 		+"], displayName=["
// 		+displayName
// 		+"], "
// 		+ alex.pfn.HttpDebugUtil.dumpRequestContents(request));


// If the display name is not set, send the user to the start.jsp page.
if (!wasDisplayNameSet.booleanValue()) {
	response.sendRedirect(PathBuilder.buildForwardedURLAsString(request) + "start.jsp");
} else {
	player=gameServer.getPlayerGroup().getOrAddPlayer(playerId, null, displayName, request);
	displayName = player.getDisplayName();
	
	// Use ?dir=me as the get string to grab director power.
	dirParam=request.getParameter("dir");
	if (dirParam!=null && dirParam.equals("me")) {
		gameServer.getPlayerGroup().giveRole(playerId, PlayerRole.director);
	}
}

imageURLExpansionPlus=Constants.imagesDirectory +"expansion_plus_sm.png";
imageURLExpansionMinus=Constants.imagesDirectory +"expansion_minus_sm.png";
selfStarImagePath = Constants.imagesDirectory +"small_yellow_star.png";

isSelfDirector = gameServer.getPlayerGroup().isDirector(playerId);

%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="include/globalPFN.css"></link>
<script type="text/javascript" src="include/commonPFN.js"></script>
<script type="text/javascript" src="include/SoundTypePFN.js"></script>
<script type="text/javascript" src="include/modalPanelPFN.jsp"></script>
<script type="text/javascript" src="include/ajaxCallsPFN.jsp"></script>
<script type="text/javascript" src="include/adminTeamSectionPFN.jsp"></script>
<script type="text/javascript" src="include/playSectionPFN.jsp"></script>
<script type="text/javascript">

//************* Global variables *****************

let globalStatusTimerObj=null;

let globalIsClickingOnControlPanelButton = false;

let soundOption = SoundType.muted;
const maximumActivityLogLines = <%= Configuration.getConfigProperties().getPropertyAsInteger(Constants.config_activityLog_client_maximum_logLines).intValue() %>;

const globalAllSoundFileNames = <%= getAllSoundFileNames_usingResourcePaths() %>;


const globalTeamColors = <%= getTeamColors() %>;
const defaultMillisecondsBeforeTimingOutAjaxCalls = <%= Configuration.getConfigProperties().getProperty(Constants.config_client_millisecondsBeforeTimingOutAjaxCalls) %>;



//Player data structure associative array.  It's really just a json object,
//where the keys are playerIds.
const globalPlayerInfo = {};

//Players who are deleted have their playerIds added here.
const globalDeletedPlayers = [];


//See the function resetModIds(event) for the modIds and their initial values!
const unusedModId = -111222;
const globalModIds = {};


//Similar to globalPlayerInfo and globalDeletedPlayers.
const globalTeamInfo = {};
const globalDeletedTeams = [];


//See the function resetGlobalVarsToDefaults for the initial values in this object.
const globalVars = {};







// ************* Event functions **************
const activateButtonText = "<%= Constants.buttonActivateBackendPolling %>";
const deactivateButtonText = "<%= Constants.buttonDeactivateBackendPolling %>";
function toggleBackendPolling(event)
{
	let button;
	
	// If the polling object exists, then destroy it.
	// If the polling object is null, then create it.
	
	button = document.getElementById("toggleBackEndPollingButton");
	
	if (globalStatusTimerObj == null) {
		handleBackendPollingSlider(event);
		button.innerHTML = deactivateButtonText;
	} else {
		window.clearInterval(globalStatusTimerObj);
		globalStatusTimerObj=null;
		fillBackendPollingStatus();
		button.innerHTML = activateButtonText;
	}
}



function handleVolumeSlider(event) {
	
}

function handleClickingOutsidePanels(event)
{
	
}

function handleSoundRadioButton(event)
{
	
}


function handleBackendPollingSlider(event)
{
	let element, span, milliseconds;
	
	element=document.getElementById("backendPollingSlider");
	milliseconds=parseInt(element.value);
	
	span=document.getElementById("pollingMilliseconds");
	span.innerHTML=milliseconds;
	
	window.clearInterval(globalStatusTimerObj);
	globalStatusTimerObj=window.setInterval(getStatus, milliseconds);
	
	element = document.getElementById("toggleBackEndPollingButton");
	element.innerHTML = deactivateButtonText;
	
	
	fillBackendPollingStatus();
}

function displayNameEventHandler(event)
{
	isDisplayNameClean=false;
}

function displayNameEnterHandler(event)
{
	if (event.keyCode == 13) {
		setDisplayName(event);
	}
}

function resetModIds(event) {
	globalModIds.unassignedPlayersModId = unusedModId;
	globalModIds.playerDisplayNameModId = unusedModId;
	globalModIds.playerOtherChangesModId = unusedModId;
	globalModIds.playerRoleModId = unusedModId;
	globalModIds.teamMembershipModId = unusedModId;
	globalModIds.teamListModId = unusedModId;
	globalModIds.scoreModId = unusedModId;
	globalModIds.currentCardModId = unusedModId;
	globalModIds.gameStateModId = unusedModId;
	globalModIds.timeTrackerModId = unusedModId;
	globalModIds.currentPlayerModId = unusedModId;

	// Old stuff below
	globalModIds.gameIdModId = unusedModId;
}

function toggleDebugInfo(event)
{
	let div, button;
	
	div=document.getElementById("debugInfoDiv");
	button=document.getElementById("debugInfoButton");
	
	if (div.style.display=="none") {
		div.style.display="block";
		button.src = "<%= imageURLExpansionMinus %>";
	} else {
		div.style.display="none";
		button.src = "<%= imageURLExpansionPlus %>";
	}
}


function displayNameSpanToggler(event)
{
	let span;
	
	span = event.target;
	
	if (span) {
		if (globalVars.selectedDisplayNameSpans[span.dataset.playerId]) {
			unselectDisplayNameSpan(span);
		} else {
			globalVars.selectedDisplayNameSpans[span.dataset.playerId] = true;
			span.classList.toggle("selectedDisplayNameSpan");
		}
	}
    
	handleClickingOnDisplayNameSpanForAdminTeamSection();
}



//********** Regular functions *****************
function isSelfCurrentPlayer(statusResponse)
{
	let retVal;
	
	retVal = false;
	
	if (statusResponse && statusResponse.teamInfo && statusResponse.teamInfo.currentPlayerId)
	{
		retVal = (statusResponse.teamInfo.currentPlayerId == "<%= playerId %>");
	}
	
	return retVal;
}


function fillPlayerList(statusResponse)
{
	let shouldRenderPlayerList, playerListDiv, span, br;
	
	shouldRenderPlayerList = globalModIds.unassignedPlayersModId != statusResponse.playerInfo.unassignedPlayersModId
		|| globalModIds.playerOtherChangesModId != statusResponse.playerInfo.playerOtherChangesModId
		|| globalModIds.playerDisplayNameModId != statusResponse.playerInfo.playerDisplayNameModId
		|| globalModIds.playerRoleModId != statusResponse.playerInfo.playerRoleModId;
	
	
	if (shouldRenderPlayerList) {
		playerListDiv = document.getElementById("playerListDiv");
		playerListDiv.innerHTML = "";
		for (const player of statusResponse.playerInfo.playerList) {

			br = document.createElement("br");
// 			span = document.createElement("span");
// 			span.innerHTML = player.displayName;
			
			span = buildDisplayNameSpan(player, false, false, true);
			playerListDiv.appendChild(span);
			playerListDiv.appendChild(br);
			
		}
	}
	
}

function unselectDisplayNameSpan(span)
{
	globalVars.selectedDisplayNameSpans[span.dataset.playerId] = false;
	span.classList.remove("selectedDisplayNameSpan");
}

function isDirector(player) {
	return arrayContains(player.roles, "<%= PlayerRole.director %>");
}

function fillBackendPollingStatus()
{
	let span;
	
	span = document.getElementById("backendPollingStatus");
	if (globalStatusTimerObj) {
		span.style.color = "green";
		span.innerHTML = "RUNNING";
	} else {
		span.style.color = "red";
		span.innerHTML = "STOPPED";
	}
	
}

function isPlayerSelf(onePlayer) {
	return onePlayer.playerId === "<%= playerId %>";
}

/**
 * Sets global variables specifically for players.
 * And the current card.
 * @param statusResponseObj The StatusResponse from the BE.
 */
function fillGlobalVariables(statusResponseObj)
{
	if (statusResponseObj.playerInfo
				&& statusResponseObj.playerInfo.playerList)
	{
		for (const onePlayer of statusResponseObj.playerInfo.playerList) {
			globalPlayerInfo[onePlayer.playerId] = onePlayer;
			
			if (isPlayerSelf(onePlayer)) {
				globalVars.selfPlayerObject = onePlayer;
			}
		} 
	}
	
	
	
	if (statusResponseObj.teamInfo
			&& statusResponseObj.teamInfo.teamList)
	{
		for (const oneTeam of statusResponseObj.teamInfo.teamList) {
			globalTeamInfo[oneTeam.teamId]=oneTeam;
		}
		
		if (statusResponseObj.teamInfo.deletedTeams
				&& getListSize(statusResponseObj.teamInfo.deletedTeams)>0)
		{
			for (const teamId of statusResponseObj.teamInfo.deletedTeams) {
				delete globalTeamInfo[teamId];
			}
		}
	}
	
	globalVars.currentCardId = statusResponse.currentCardId;
}



function showOrHideDirectorParts(statusResponse)
{
	let div, isDir;
	

	if (statusResponse.playerInfo.playerRoleModId != globalModIds.playerRoleModId)
	{
		
		isDir = isDirector(globalVars.selfPlayerObject);

		div = document.getElementById("administrationSectionDiv");
		div.style.display = isDir ? "block" : "none";
		
		//div = document.getElementById("gameStateChanger");
		//div.style.display = isDir ? "inline-block" : "none";
	}
}

function fillStatus(statusResponse)
{
	// This must be first.
	fillGlobalVariables(statusResponse);
	
	fillDebugInfo(statusResponse);
	fillActivityLog(statusResponse.activityLog);
	
	<% if (isSelfDirector) { %>
	showOrHideDirectorParts(statusResponse);
	fillAdminTeamSection(statusResponse);
	fillUnassignedPlayerList(statusResponse);
	<% } %>
	
	fillPlayerList(statusResponse);
	
	fillPlaySection(statusResponse);
	
	// This should probably be last in the fillStatus() function.
	assignLatestModIds(statusResponse);
}


function assignLatestModIds(statusResponse)
{
	globalModIds.playerOtherChangesModId = statusResponse.playerInfo.playerOtherChangesModId;
	globalModIds.playerRoleModId = statusResponse.playerInfo.playerRoleModId;
	globalModIds.teamMembershipModId = statusResponse.teamInfo.teamMembershipModId;
	globalModIds.teamListModId = statusResponse.teamInfo.teamListModId;
	globalModIds.scoreModId = statusResponse.scoreModId;
	globalModIds.currentPlayerModId = statusResponse.teamInfo.currentPlayerModId;
	globalModIds.currentCardModId = statusResponse.currentCardModId;
	globalModIds.gameStateModId = statusResponse.gameStateModId;
	globalModIds.timeTrackerModId = statusResponse.timeTrackerModId;
	globalModIds.scoreModId = statusResponse.scoreModId; 
	globalModIds.playerDisplayNameModId = statusResponse.playerInfo.playerDisplayNameModId;
	globalModIds.unassignedPlayersModId = statusResponse.playerInfo.unassignedPlayersModId;
}



function buildTeamNameSpan(teamObject,
		shouldSetABlackBorder = false,
		shouldShowSelfStar = true,
		shouldBeBigger = false)
{
	let retVal, img, origImg, element, origElement;
	
	retVal = document.createElement("span");
	
	retVal.innerHTML = teamObject.name;
	
	retVal.dataset.playerId = teamObject.teamId;
	
	if (shouldSetABlackBorder) {
		retVal.classList.add("solidBlackBorder");
	}
	
	if (shouldShowSelfStar && globalVars.selfPlayerObject.teamId == teamObject.teamId) {
		origImg = document.getElementById("selfStarImg");
		img = duplicateElement(origImg);
		retVal.append(" ")
		retVal.appendChild(img);
	}
	
	if (shouldBeBigger) {
		retVal.classList.add("mediumHeading");
	}
	
	return retVal;
	
}



/**
 * Create a span tag with a player's display name as its content.
 * Set the data id to be the playerId.
 * Optionally give it a solid black border.
 *
 * Return the span element object.
 */
function buildDisplayNameSpan(playerObject,
		shouldSetABlackBorder = true,
		shouldSetClickEventFunction = false,
		shouldShowSelfStar = true,
		shouldMarkDirector = true)
{
	let retVal, img, origImg, element, origElement;
	
	retVal = document.createElement("span");
	
	retVal.innerHTML = playerObject.displayName;
	
	retVal.dataset.playerId = playerObject.playerId;
	retVal.dataset.isDisplayNameSpan = true;
	
	if (shouldSetABlackBorder) {
		retVal.setAttribute("class", "solidBlackBorder");
	}
	
	if (shouldSetClickEventFunction) {
		retVal.addEventListener("click", displayNameSpanToggler);
	}
	
	if (shouldShowSelfStar && isPlayerSelf(playerObject)) {
		origImg = document.getElementById("selfStarImg");
		img = duplicateElement(origImg);
		retVal.append(" ")
		retVal.appendChild(img);
	}
	
	if (shouldMarkDirector && isDirector(playerObject)) {
		origElement = document.getElementById("directorLabel");
		element = duplicateElement(origElement, true);
		retVal.append(" ")
		retVal.appendChild(element);
	}
	
	return retVal;
}


function fillActivityLog(activityLog) {
	
	let tbody, line, table, checkbox, div, index, tr, td, textNode;
	
	if (activityLog && activityLog.lines) {
		
		if (typeof activityLog.highestLineNumber === 'number') {
			if (activityLog.highestLineNumber > globalVars.lastKnownLogLine) {

				globalVars.lastKnownLogLine = activityLog.highestLineNumber;
				tbody = document.getElementById("activityLogGuts");
				
	
				for (index=0;
					index<activityLog.lines.length;
					index++)
				{
					line=activityLog.lines[index];
					
					tr=tbody.insertRow();
					td=tr.insertCell();
					textNode=document.createTextNode(line);
					td.appendChild(textNode);
				}	
				

				// Remove older lines if there are too many.
				while (tbody.rows.length > maximumActivityLogLines) {
					tbody.deleteRow(0);
				}
				
				checkbox=document.getElementById("autoscrollCheckbox");
				if (checkbox.checked) {
					div=document.getElementById("scrollableDiv");
					div.scrollTop = div.scrollHeight;
				}
			}
		}
	}
}



function fillDebugInfo(statusResponse)
{
	let span;
	
	span = document.getElementById("debugSectionGameState");
	span.innerHTML = statusResponse.gameState;

	span = document.getElementById("debugSectionGameId");
	span.innerHTML = statusResponse.gameId;
	
}

function resetGlobalVarsToDefaults()
{
// 	const globalPlayerInfo={};

	//Players who are deleted have their playerIds added here.
// 	const globalDeletedPlayers=[];

// 	const globalTeamInfo={};
// 	const globalDeletedTeams=[];

// 	const globalVars={};

	globalVars.lastKnownLogLine=-1;
	globalVars.selfPlayerObject = null;
	globalVars.previousPlayerState = null;
    globalVars.isDisplayNameClean = true;
    
    globalVars.selectedDisplayNameSpans = [];
    
    globalVars.currentCardId = null;
}


// ************* INIT ONLY *********************
function init()
{

	let element, rad, elements;

	console.log("init():: Entering");
	
	resetModIds(null);
	resetGlobalVarsToDefaults();

	element=document.getElementById("displayNameInput");
	element.addEventListener("input", displayNameEventHandler);
	element.addEventListener("keyup", displayNameEnterHandler);
	

	// Make the radio buttons work in Seamonkey.
	rad = document.soundForm.soundOption;
	for (let i = 0; i < rad.length; i++) {
    	rad[i].addEventListener('change', handleSoundRadioButton);
	}

	elements=document.getElementsByTagName("body");
	elements[0].addEventListener("click", handleClickingOutsidePanels);
	
	element=document.getElementById("volumeSlider");
	element.addEventListener("change", handleVolumeSlider);
	
	element=document.getElementById("backendPollingSlider");
	element.addEventListener("change", handleBackendPollingSlider);
	
	globalStatusTimerObj=window.setInterval(getStatus, <%= Configuration.getConfigProperties().getProperty(Constants.config_client_millisecondsForClientToPollStatus) %>);

	fillBackendPollingStatus();
	
	// temp turn off polling
	let button;
	
	button=document.getElementById("debugInfoButton");
	button.click();

	button=document.getElementById("requestStatusButton");
	button.click();
	
	//button=document.getElementById("toggleBackEndPollingButton");
	//button.click();
	
}


</script>
<title><%= Constants.gameTitle %></title>
</head>
<body onload="init();">
<%= Constants.gameTitle %><br/>
This page generated on: <span style="background-color: #efefef"><%= (new java.util.Date()) %></span><br/>



<div class="divTable">

<div class="divTableRow">
	<div class="divTableCell rightBlackBorder">
		<span class="selfTableCell">Display name: <input id="displayNameInput" type="text" value="<%= displayName %>"/></span><br/>
		<button onclick="setDisplayName(event)">Set display name</button><br/>
		<span id="message"></span>
	</div>
	<div class="divTableCell rightBlackBorder">
		Sound effects:<br/>
		<form name="soundForm">
			<input id="radioNoSound" type="radio" name="soundOption" checked/>No sound<br/>
			<input id="radioDing" type="radio" name="soundOption"/>Normal<br/>
 			<input id="radioSilly" type="radio" name="soundOption" />Silly<br/>
			<label for="volumeSlider">Volume:</label>
	  		<input type="range" id="volumeSlider" min="0" max="100" step="5" value="15"/>
		</form>
	</div>
	<div class="divTableCell">
		Activity log
		<span class="solidBlackBorder"><input type="checkbox" id="autoscrollCheckbox" checked></input> Autoscroll</span>
		<br/>
		<div class="scrollableTable solidBlackBorder" id="scrollableDiv">
			<table class="scrollableTable" id="activityLogTable">
				<tbody id="activityLogGuts"></tbody>
			</table>
		</div>
	</div>
</div>

</div>
<br/>


<hr/>
<%-- Main Play area --%>

<div class="divTable">
<div class="divTableRow">

	<!-- Left side with the current card -->
	<div class="divTableCell mediumHeading">
	
		<!-- Timer area -->
		<table class="solidBlackBorder">
			<tr>
				<td id="timerTd">
					<span id="timerStateLabel" style="color:green;"></span><br/>
					Seconds remaining: <span id="secondsSpan" style="font-size:2.5em">
					<%= Configuration.getConfigProperties().getPropertyAsInteger(Constants.config_game_defaultTimerSeconds) %> 
					</span><br/>
					<button id="startTimerButton" style="display:none" onclick="timerStart(event)"><img src="media/play_icon_10.png"></img> Start</button>
					<button id="pauseTimerButton" style="display:none" onclick="timerPause(event)"><img src="media/pause_icon_10.png"></img> Pause</button>
					<button id="resumeTimerButton" style="display:none" onclick="timerResume(event)"><img src="media/play_icon_10.png"></img> Resume</button>
				</td>
			</tr>
		</table>
	
	
	
		<div class="thickGreenBorder" onclick="scoreCard1Pt(event)">
			1 point word<br/>
			<span id="currentCard1PointPart" style="font-size:2em"></span><br/>
		</div>
		<div class="thickGreenBorder" onclick="scoreCard3Pt(event)">
			3 point word or phrase<br/>
			<span id="currentCard3PointPart" style="font-size:2em"></span><br/>
		</div>
		<div class="thickRedBorder" onclick="scoreCardFailure(event)"><img src="media/no-stick_transparent_50.png"/></div>
	</div>
	
	
	<div class="divTableCell solidBlackBorder">
	
		<div style="display:flex; flex-direction:column; height:100%;">
			<%-- Current team section --%>
			<div id="upperTeamMessageDiv">
				<table>
					<tr id="currentTeamMessageTd" style="vertical-align: top;"></tr>
				</table>
			</div>
			
			<div style="flex-grow: 1; flex-shrink:0">&nbsp;</div>
			
			<div style="vertical-align:bottom">
			
				<div id="upcomingTurnsDiv">
					Remaining turns in the game<br/>
					including the current turn:
					<span id="remainingTurnsSpan"></span><br/>
					Upcoming turns:<br/>
					<div id="upcomingTurnsContainer"></div>
				</div>
			</div>
		</div>
	
	</div>
	
	<!--  Right side is the team list. -->
	<div class="divTableCell solidBlackBorder">
		<table id="playSection_TeamListTable"></table>
	</div>
	
</div>
</div>

<hr/>




<%-- Non admin players can't see this section at all. --%>
<% if (isSelfDirector) { %>
<div id="administrationSectionDiv" style="display:none" class="solidRedBorder">
<%-- Team administration section --%>
<span class="sectionHeaderText">Administration</span><br/>
Messages:<span id="administration_messages"></span><br/>

<div style="display:inline-block">
	Teams:<br/>
	<button id="randomizeTeamAssignmentButton" onclick="randomizeTeamAssignment(event)">Randomize team assignment</button><br/>
	<div id="teamListSectionDiv" style="display:grid; grid-template-rows:1fr; grid-auto-flow:column"></div>
</div><br/>
<br/>

Create new team<br/>
<input id="administration_createNewTeamText" type="text"/>
<button onclick="createNewTeam(event);">Create Team</button>
<br/> 
<br/>
<div class="solidBlackBorder" style="display:inline-block">
	Unassigned players<br/>
	<button id="unassignSelectedPlayersButton" onclick="unassignTeamMember(event);" disabled="disabled">Unassign selected players</button><br/>
	<div id="unassignedPlayerList" style="display:grid"></div>
</div>


<hr/>
<div class="solidRedLine">
Generate new game<br/>
<button id="generateNewGameButton" onclick="generateNewGame(event)">Generate new game</button><br/>
Number of turns per person in the largest team: 
<select size="1" id="numberOfTurnsPerPersonInLargestTeam">
	<option value="1" selected="selected">1</option>
	<option value="2">2</option>
	<option value="3">3</option>
</select><br/>

Choose one or more card files to use.  If one isn't chosen, then a random card file will be used.<br/> 
<%= getAllCardFileNamesAsCheckboxes(true) %><br/>

</div>



<div>
<button id="abortGameButton" onclick="abortGame(event)">Abort game</button>
</div>

<div>
<button id="scoreEditorButton" onclick="editScore(event)">Edit the scored cards in the game</button>
</div>

</div> <!--  End administration section -->
<% } %>


<div class="solidBlueBorder">
<span class="sectionHeaderText">Player list</span><br/>

<div id="playerListDiv"></div>
</div>




<hr/>
<%-- Rules section --%>

Rules go here.<br/>
<a target="_blank" href="media/PoetryForNeanderthals_Core_Instructions_20240130.pdf">PDF rules. 6MB.</a><br/>



<hr/>
<%-- Debug section --%>

Debug info <img id="debugInfoButton" src="<%= imageURLExpansionPlus %>" onclick="toggleDebugInfo(event);"></img>
<div id="debugInfoDiv" style="display:none" class="solidBlackBorder">


Build 202YMMDD, AlexLib v1.5<br/>
Player id: <%= playerId %><br/>
Game state: <span id="debugSectionGameState"></span><br/>
Game Id: <span id="debugSectionGameId"></span><br/>
<br/>
<button id="requestStatusButton" onclick="console.log('Calling getStatus');getStatus(event);" >Request Status</button><br/>
<button id="resetModIds" onclick="console.log('Reseting mod ids');resetModIds(event);">Reset mod ids</button><br/>
<button id="toggleBackEndPollingButton" onclick="console.log('Toggling backend polling');toggleBackendPolling(event);"><%= Constants.buttonDeactivateBackendPolling %></button><br/>
<br/>

Backend polling: <span id="backendPollingStatus"></span><br/>
<label for="backendPollingSlider">Backend polling time in milliseconds:</label>
<input type="range" id="backendPollingSlider" min="400" max="2000" step="1"
	   value="<%= Configuration.getConfigProperties().getPropertyAsInteger(Constants.config_client_millisecondsForClientToPollStatus) %>"/>
Polling milliseconds:
<span id="pollingMilliseconds"><%= Configuration.getConfigProperties().getPropertyAsInteger(Constants.config_client_millisecondsForClientToPollStatus) %></span>
	
</div>





<%-- Should never be visible. Storage for copied objects. --%>
<div style="display: none">
	<div id="unassignedPlayerNameDiv" style="display: grid-row"></div>
	<div id="teamListSectionDiv_chlidTeamGridItem" style="display:inline-grid" class="solidBlackBorder"></div>
	<img id="selfStarImg" src="<%= selfStarImagePath %>"/>
	<span id="directorLabel" class="invertedColors">Director</span>
	
	<table>
		<tr id="playSection_memberTableColumnHeadings">
		  <th class="rightBlackBorder bottomBlackBorder"></th>
		  <th class="rightBlackBorder bottomBlackBorder">Team score</th>
		  <th class="rightBlackBorder bottomBlackBorder">Member</th>
		  <th class="rightBlackBorder bottomBlackBorder">Turns taken</th>
		  <th class="rightBlackBorder bottomBlackBorder">1 pt scored</th>
		  <th class="rightBlackBorder bottomBlackBorder">3 pt scored</th>
		  <th class="bottomBlackBorder">Failed</th>	    
		</tr>
	</table>
	
	
</div>


</body>
</html>