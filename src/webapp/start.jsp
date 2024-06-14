<?xml version="1.0" encoding="utf-8" ?>
<%@page import="alex.pfn.PathBuilder"%>
<%@page import="alex.pfn.Configuration"%>
<%@page import="alex.http.HttpDebugUtil"%>
<%@page import="alex.pfn.GameServer"%>
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@page import="org.springframework.web.context.support.SpringBeanAutowiringSupport"%>
<%@ page import="alex.pfn.player.Player"%>
<%@ page import="alex.pfn.Constants"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
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
%>
<%
Player player;
String playerId, dirParam, displayName;
Boolean wasDisplayNameSet;

// Set a player id.  We do this to uniquely identify players, and also because players
// aren't required to log in.
playerId=(String)session.getAttribute(Constants.playerIdSessionAttributeName);
if (playerId==null) {
	playerId=Player.getNewPlayerId();

	session.setAttribute(Constants.playerIdSessionAttributeName, playerId);
}



wasDisplayNameSet=(Boolean)session.getAttribute(Constants.wasDisplayNameSetSessionAttributeName);
wasDisplayNameSet = wasDisplayNameSet==null?Boolean.FALSE:wasDisplayNameSet;

// System.out.println("start.jsp:: time=["
// 		+(new java.util.Date().toString())
// 		+"], playerId=["
// 		+playerId
// 		+"], wasDisplayNameSet=["
// 		+wasDisplayNameSet
// 		+"], "
// 		+ alex.pfn.HttpDebugUtil.dumpRequestContents(request));


// If the display name is already set, send the user to the player.jsp page.
if (wasDisplayNameSet.booleanValue()) {
	response.sendRedirect(PathBuilder.buildForwardedURLAsString(request) + "player.jsp");
}



%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8" />
<title><%= Constants.gameTitle %>: Set display name</title>
<link rel="stylesheet" href="include/globalPFN.css"></link>
<script type="text/javascript" src="include/commonPFN.js"></script>
<script type="text/javascript">
const defaultMillisecondsBeforeTimingOutAjaxCalls=<%= Configuration.getConfigProperties().getProperty(Constants.config_client_millisecondsBeforeTimingOutAjaxCalls) %>;



function createPlayerAndSetDispalyName()
{
	console.log("createPlayerAndSetDispalyName():: ENTERING");
	
	let localPlayerId, xhttp, payload, data, statusResponseObj, displayName,
		element;
	
	displayName=document.getElementById("displayName").value;

	data = {
			"playerId":"<%= playerId %>",
			"displayName": displayName
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
    	    
    	    element=document.getElementById("displayNameSubmitter");
    	    element.style.display="none";
    	    element=document.getElementById("goToPlayerForm");
    	    element.style.display="inline";

    	    if (statusResponse.isSuccessful) {
    	    	window.location.replace("player.jsp");
    	    }
        }
   };
  
   xhttp.open("POST", "createPlayerAndSetDispalyName", true);
   xhttp.setRequestHeader("Content-type", "application/json");
   xhttp.send(payload);
}



function displayNameEnterHandler(event)
{
	if (event.keyCode == 13) {
		createPlayerAndSetDispalyName(event);
	}
}


function init() {
	let element, rad, elements;

	console.log("start.jsp:: init():: Entering");
	
	element = document.getElementById("displayName");
	element.addEventListener("keyup", displayNameEnterHandler);
	
}

</script>
</head>
<body onload="init();">
<%= Constants.gameTitle %><br/>
This page generated on: <span style="background-color: #efefef"><%= (new java.util.Date()) %></span><br/>
<br/>

<div style="font-size:3em">
	Set your display name:<br/>
	<input id="displayName" type="text" value="<%= playerId %>"/><br/>
	<input id="displayNameSubmitter" onclick="createPlayerAndSetDispalyName(event)" type="submit" value="Set your display name"/>
</div>
		
<form id="goToPlayerForm" action="player.jsp" method="get" style="display:none">
	If your browser doesn't automatically go to player.jsp, 
	<input type="submit" value="click here!"></input>
</form>


</body>
</html>
