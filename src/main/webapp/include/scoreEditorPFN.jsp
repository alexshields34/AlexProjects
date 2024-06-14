<%-- This jsp resolves to js.

 --%>
<%@page import="alex.pfn.score.ScoreType"%>
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
    
    
    private String generateScoreTypeSelectList()
    {
    	StringBuilder sb;
    	
    	sb=new StringBuilder(200);
    	
    	sb.append("<select size='1'>");
    	for (ScoreType st: ScoreType.values()) {
    		sb.append("<option value='")
    			.append(st.toString())
    			.append("'>")
    			.append(st.toString())
    			.append("</option>");
    	}
    	sb.append("</select>");
    	
    	return sb.toString();
    }
%>
<%
Player player;
String playerId, displayName;

// Set a player id.  We do this to uniquely identify players, and also because players
// aren't required to log in.
playerId=(String)session.getAttribute(Constants.playerIdSessionAttributeName);
player=gameServer.getPlayerGroup().getPlayer(playerId);


%>


// ************ Globals ************
const globalDirtyScoreLogEntries = [];


//********** Event functions ***************
function submitChangesToScore(event)
{
	let changesList, scoreTable, rowList, oneChange;
	
	changesList = [];
	
	// If globalDirtyScoreLogEntries has anything in it:
	 
	// Get scoreEditor_scoreTable
	// Loop through trs
	// Get info for it only if its id is within globalDirtyScoreLogEntries.
	//    Get its checkbox value and score type.
	
	if (globalDirtyScoreLogEntries.length > 0) {
		scoreTable = document.getElementById("scoreEditor_scoreTable");
		
		rowList = scoreTable.querySelectorAll(":scope tr[data-id]");
		
		for (const oneTr of rowList) {
			if (globalDirtyScoreLogEntries.includes(oneTr.dataset.id)) {
				oneChange = {};
				oneChange.id = oneTr.dataset.id;
				oneChange.newScoreType = oneTr.querySelector("select").value;
				oneChange.shouldDelete = oneTr.querySelector("input[type=checkbox]").checked;
				changesList.push(oneChange);
			}
		}
		
		changeScoreAjax(changesList);
		
		globalDirtyScoreLogEntries.splice(0, globalDirtyScoreLogEntries.length);
	}
}

function manageScoreChange(event)
{
	let element, index, parent, button, checkbox;
	
	element = event.target;
	
	if (element.dataset.isSelect) {
		globalDirtyScoreLogEntries.push(element.dataset.id);
		
		// Turn on the undo button.
		parent = getParentElementByType(element, "tr");
		button = parent.querySelector("button");
		button.disabled = false;
	} else if (element.dataset.isUndo) {
	
		element.disabled = true;
		
		while ((index = globalDirtyScoreLogEntries.indexOf(element.dataset.id)) != -1) {
			globalDirtyScoreLogEntries.splice(index, 1);
		}
		
		parent = getParentElementByType(element, "tr");
		checkbox = parent.querySelector("input[type='checkbox']");
		checkbox.checked = false;
		
	} else if (element.dataset.isDelete) {
		globalDirtyScoreLogEntries.push(element.dataset.id);
		
		// Turn on the undo button.
		parent = getParentElementByType(element, "tr");
		button = parent.querySelector("button");
		button.disabled = false;
	}
	
}


//********** Regular functions ***********
function createScoreTypeSelectList(selectedScoreType)
{
	let retVal, tempJunk, i, option;
	
	tempJunk = document.createElement("div");
	tempJunk.innerHTML = "<%= generateScoreTypeSelectList() %>";
	
	
	retVal = tempJunk.firstChild;
	
	for (i = 0; i < retVal.options.length; i++) {
		option = retVal.options[i];
		if (option.value == selectedScoreType) {
			option.selected = true;
			break;
		}
	}
	
	return retVal;
}


function fillScoreEditor(statusResponse) {
	
	let tbody, entry, table, checkbox, div, index, tr, td, textNode,
		shouldRerenderScoreLog, selectList, button;
		
	shouldRerenderScoreLog = statusResponse.scoreLog
		&& (globalModIds.scoreModId != statusResponse.scoreModId);
	
	if (shouldRerenderScoreLog) {
		tbody = document.getElementById("scoreEditor_scoreTableGuts");
		tbody.innerHTML = "";
		
		for (index=0;
			index < statusResponse.scoreLog.scoreLogEntryList.length;
			index++)
		{
			entry = statusResponse.scoreLog.scoreLogEntryList[index];
			
			tr = tbody.insertRow();
			tr.dataset.id = entry.id;
			
			td = tr.insertCell();
			td.innerHTML = entry.id;
			
			td = tr.insertCell();
			td.innerHTML = entry.personWhoClickedId?globalPlayerInfo[entry.personWhoClickedId].displayName:"";
			
			td = tr.insertCell();
			td.innerHTML = globalPlayerInfo[entry.currentPlayerAtTimeOfScoringPlayerId].displayName;
			
			td = tr.insertCell();
			td.innerHTML = entry.cardText;
			
			td = tr.insertCell();
			selectList = createScoreTypeSelectList(entry.scoreType);
			td.appendChild(selectList);
			selectList.addEventListener("change", manageScoreChange);
			selectList.dataset.isSelect = true;
			selectList.dataset.id = entry.id;
			
			td = tr.insertCell();
			td.innerHTML = "<button disabled='disabled'>Undo</button>";
			button = td.firstChild;
			button.addEventListener("click", manageScoreChange);
			button.dataset.id = entry.id;
			button.dataset.isUndo = true;
			
			td = tr.insertCell();
			td.innerHTML = "<input type='checkbox'></input>";
			checkbox = td.firstChild;
			checkbox.addEventListener("click", manageScoreChange);
			checkbox.dataset.id = entry.id; 
			checkbox.dataset.isDelete = true;
		}
		
		
		checkbox=document.getElementById("scoreEditor_autoscrollCheckbox");
		if (checkbox.checked) {
			div=document.getElementById("scoreEditor_scrollableDiv");
			div.scrollTop = div.scrollHeight;
		}
	}
}

