<%-- This jsp resolves to js.

This is for storing the ajax calls so that the main file won't be humongous.

 --%>
 
<%@page import="alex.pfn.player.Player"%>
<%@page import="alex.pfn.player.PlayerState"%>
<%@page import="alex.pfn.GameServer"%>
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@page import="org.springframework.web.context.support.SpringBeanAutowiringSupport"%>
<%@page import="alex.pfn.player.PlayerState"%>
<%@page import="alex.pfn.GameState"%>
<%@page import="alex.pfn.Constants"%>

<%@page language="java" contentType="text/javascript" %>
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
%>
<%
Player player;
String playerId, displayName;

// Set a player id.  We do this to uniquely identify players, and also because players
// aren't required to log in.
playerId=(String)session.getAttribute(Constants.playerIdSessionAttributeName);
player=gameServer.getPlayerGroup().getPlayer(playerId);


%>


//********** Ajax call functions ***********
function supplantCurrentPlayerAjax(event)
{
	let xhttp, payload, data, statusResponseObj, supplantingPlayerId, selectList;
	
	selectList = document.getElementById("administration_supplantCurrentPlayerSelectionList");
	supplantingPlayerId = selectList.options[selectList.selectedIndex].value;

	data = {
		"playerId":supplantingPlayerId
		};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "supplantCurrentPlayer", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}


function endCurrentPlayerTurnAjax(event)
{
	let xhttp, payload, data, statusResponseObj;
	
	data = {};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "endCurrentPlayerTurn", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}



function setDisplayName(event)
{
	let xhttp, displayNameParam, displayName, textField, playerIdParam;
    
    // Always store the display name in the session since the user can change
    // it at any time.
    displayNameParam="";
    textField=document.getElementById("displayNameInput");
    if (textField) {
	    displayName = textField.value.trim();
	    displayName = encodeURIComponent(displayName);
	    displayNameParam = "&displayName="+displayName;
    }
    
    playerIdParam = "playerId=<%= playerId %>";
    
    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls; 
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
    	    
    	    globalVars.isDisplayNameClean = true;
        }
   };
  
   xhttp.open("GET", "setDisplayName?"+playerIdParam + displayNameParam, true);
   xhttp.send();
}



/**
 * This is for retrieving the status from the server once.  This
 * function can be called multiple times , like once per second.
 */
function getStatus()
{
    let lkllParam, xhttp, displayNameParam, 
    	getString, displayName, textField, playerIdParam;

    lkllParam = "lastKnownLogLine=" + globalVars.lastKnownLogLine;
    smiParam = "scoreModId="+globalModIds.scoreModId;
    getString = lkllParam + "&" + smiParam;
    
    
    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("GET", "getStatus?"+getString, true);
   xhttp.send();
}



function addNewTeamAjax(teamName)
{
	let xhttp, payload, data, statusResponseObj;
	
	data = {
			"teamName": teamName
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "addNewTeam", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);

}



function addTeamMembersAjax(teamId, selectedPlayerList)
{
	let xhttp, payload, data, statusResponseObj;
	
	data = {
			"teamId": teamId,
			"selectedPlayerIdList": selectedPlayerList
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "addTeamMembers", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}


function unassignTeamMembersAjax(selectedPlayerList)
{
	let xhttp, payload, data, statusResponseObj;
	
	data = {
			"selectedPlayerIdList": selectedPlayerList
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "unassignTeamMembers", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}



	
function generateNewGame(event)
{
	let xhttp, payload, data, statusResponseObj,
		selectedCardFileList, list, selectBox;
	
	selectedCardFileList = [];
	list = document.getElementsByName("newGameCard");
	for (const element of list) {
		if (element.checked) {
			selectedCardFileList.push(element.value);
		}
	}
	
	selectBox = document.getElementById("numberOfTurnsPerPersonInLargestTeam");
	data = {
			"selectedFileList": selectedCardFileList,
			"maxTurnsForMembersOfLargestTeam": selectBox.options[selectBox.selectedIndex].value
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "generateNewGame", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}


function randomizeTeamAssignment(event)
{
	let xhttp, payload, data, statusResponseObj,
		selectedCardFileList, list, selectBox;

	data = {
			// Nothing gets sent here.
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "randomizeTeamAssignment", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}

function abortGame(event)
{
	let xhttp, payload, data, statusResponseObj,
		selectedCardFileList, list, selectBox;

	data = {
			// Nothing gets sent here.
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "abortGame", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}


function timerStart(event)
{	
	let localPlayerId, xhttp, payload, data, statusResponseObj;

	data = {
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "startTimer", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}

function timerPause(event)
{
	let localPlayerId, xhttp, payload, data, statusResponseObj;

	data = {
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "pauseTimer", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}

function timerResume(event)
{
	let localPlayerId, xhttp, payload, data, statusResponseObj;

	data = {
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "resumeTimer", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}



/**
 * @param scoreType Can be "normal1", "normal3", "failed".  These
 * come from the java class ScoreType.
 */
function scoreCard(scoreType)
{
	let xhttp, payload, data, statusResponseObj;
	
	data = {
			"lastKnownLogLine": globalVars.lastKnownLogLine,
			"scoreType": scoreType,
			"cardId": globalVars.currentCardId
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "scoreCard", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}

function deleteTeamAjax(event)
{
	let xhttp, payload, data, statusResponseObj,
		button, teamId;
	
	button = event.target;
	teamId = button.dataset.teamId;
	
	data = {
			"teamId": teamId
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "deleteTeam", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}



/**
 * @param scoreChangeList The value here will go into the PostBody in
 * scoreChangeList.  It's an  array of objects and each object is
 * a PostBodyScoreChange
 */
function changeScoreAjax(scoreChangeList)
{
	let xhttp, payload, data, statusResponseObj,
		button, teamId;
	
	button = event.target;
	teamId = button.dataset.teamId;
	
	data = {
			"scoreChangeList": scoreChangeList
			};
	payload=JSON.stringify(data);

    xhttp = new XMLHttpRequest();
    
    xhttp.timeout = defaultMillisecondsBeforeTimingOutAjaxCalls;
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
    	    if (isString(this.responseText)) {
    	         statusResponse = JSON.parse(this.responseText);
    	    } else {
    		     statusResponse = this.responseText;
    	    }

    	    fillStatus(statusResponse);
        }
   };
  
   xhttp.open("POST", "changeScore", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}


