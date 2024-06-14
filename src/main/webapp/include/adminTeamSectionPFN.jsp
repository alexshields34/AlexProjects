<%-- This jsp resolves to js.

This is for storing the code to render the admin section for teams.

 --%>
<%@page language="java" contentType="text/javascript" %>



// ------------------ Event handlers ---------------------
/**
 * Present the user with a list of all cards, scored and unscored.
 * Unscored cards are cards that a player was shown, that that player
 * didn't finish with because time ran out and that player's teammembers didn't guess it.
 * Allow the user to switch the score type to failed, 1pt, 3pt, or unscored.
 * Allow the user to 
 */
function editScore(event)
{
}


function createNewTeam(event)
{
	let text;
	
	text = document.getElementById("administration_createNewTeamText");
	
	if ( ! text.value) {
		setAdministrationMessage("You must specify a team name.", false);
	} else {
		setAdministrationMessage("");
		
		addNewTeamAjax(text.value);
	}
}



function addTeamMember(event)
{
	let playerSpans, playerId, selectedPlayerList, teamId, div;

	
	selectedPlayerList = [];
	playerSpans = document.getElementsByClassName("selectedDisplayNameSpan");
	if (playerSpans.length > 0) {
		for (const playerSpan of playerSpans) {
			selectedPlayerList.push(playerSpan.dataset.playerId);
		}
	}
	
	div = getParentElementByType(event.target, "div");
	
	addTeamMembersAjax(div.dataset.teamId, selectedPlayerList);
	
	unselectAllDisplayNameSpans();
	changeAssignmentButtonsState(false);
}

/**
 * All displayNameSpans that are currently selected get unassigned from their current team.
 * Then, unselect them.
 */
function unassignTeamMember(event)
{
	let playerSpans, playerId, selectedPlayerList, teamId, div;

	
	selectedPlayerList = [];
	playerSpans = document.getElementsByClassName("selectedDisplayNameSpan");
	if (playerSpans.length > 0) {
		for (const playerSpan of playerSpans) {
			selectedPlayerList.push(playerSpan.dataset.playerId);
		}
	}
	
	unassignTeamMembersAjax(selectedPlayerList);
	
	unselectAllDisplayNameSpans();
	changeAssignmentButtonsState(false);
}


// ----------------- Regular functions --------------------

/**
 * @param message String
 * @param isSuccessMessage Boolean If true, this is a good message and it should be green.
 * Else, it should be red.
 */
function setAdministrationMessage(message, isSuccessMessage = true)
{
	let span;
	
	span = document.getElementById("administration_messages");
	
	span.innerHTML = message;
	
	if (isSuccessMessage) {
		span.classList.add("greenText");
	} else {
		span.classList.add("redText");
	}
}

function changeAssignmentButtonsState(shouldEnableButtons)
{
	let buttonElementArray, unassignButton;
	
	buttonElementArray = document.getElementById('teamListSectionDiv').getElementsByTagName('button')
	for (const button of buttonElementArray) {
		if (button.dataset.isAddToTeamButton) {
			if (shouldEnableButtons) {
				button.removeAttribute("disabled");
			} else {
				button.disabled = true;
			}
		}
	}
	
	unassignButton = document.getElementById("unassignSelectedPlayersButton");
	if (shouldEnableButtons) {
		unassignButton.removeAttribute("disabled");
	} else {
		unassignButton.disabled = true;
	}
}



function unselectAllDisplayNameSpans()
{
	let spanList;
	
	spanList = document.querySelectorAll("span.selectedDisplayNameSpan");
	//spanList = document.querySelectorAll("span[data-is-display-name-span='true']");
	
	for (const span of spanList) {
		unselectDisplayNameSpan(span);
	}
}


/**
 * If the user clicked on any user's displayNameSpan, then enable the Add to Team buttons.
 */
function handleClickingOnDisplayNameSpanForAdminTeamSection()
{
	let shouldEnableButtons, buttonElementArray, unassignButton;
	
	shouldEnableButtons = false;
	
	// Go through all of the globalVars.selectedDisplayNameSpans to see if any are selected.
	for (const playerId in globalVars.selectedDisplayNameSpans) {
		if (globalVars.selectedDisplayNameSpans[playerId]) {
			shouldEnableButtons = true;
			break;
		}
	}

	changeAssignmentButtonsState(shouldEnableButtons);
}


/**
 * Return a div.  It'll have all team information.
 */
function buildTeamListSectionElement(teamDTO)
{
	let originalDiv, retValDiv, span, memberListDiv, index, memberPlayerId,
		memberPlayer, button;
	
	originalDiv = document.getElementById("teamListSectionDiv_chlidTeamGridItem");
	retValDiv = duplicateElement(originalDiv);
	
	span = document.createElement("span");
	span.innerHTML = teamDTO.name;
	retValDiv.appendChild(span);
	
	// If the team is deletable, then add a delete team button.
	if (teamDTO.isDeletable) {
		button = document.createElement("button");
		button.innerHTML = "Delete team";
		button.addEventListener("click", deleteTeamAjax);
		button.dataset.teamId = teamDTO.teamId;
		span.appendChild(document.createTextNode(" "));
		span.appendChild(button);
	}
	
	retValDiv.dataset.teamId = teamDTO.teamId;
	
	// Button for adding / removing team members.
	button = document.createElement("button");
	button.innerHTML = "Add to team";
	button.disabled = true;
	button.dataset.isAddToTeamButton = true;
	button.addEventListener("click", addTeamMember);
	retValDiv.appendChild(button);
	
	memberListDiv = document.createElement("div");
	
	for (index=0;
		index < teamDTO.members.length;
		index++)
	{
		if (index > 0) {
			memberListDiv.appendChild(document.createElement("br"));
		}
		
		memberPlayerId = teamDTO.members[index];
		
		span = buildDisplayNameSpan(globalPlayerInfo[memberPlayerId],
				true,
				true);
		
		memberListDiv.appendChild(span);
	}
	
	retValDiv.appendChild(memberListDiv);
	
	return retValDiv;
}



/**
 * Append team sections to the parentDivContainr argument.  Don't
 * return anything.
 */
function buildAdminTeamLists(parentDivContainer, statusResponse)
{
	let teamDTO;
	
	for (const key in globalTeamInfo) {
		teamDTO = globalTeamInfo[key];
		
		if (!teamDTO.isDeleted) {
			oneTeamDiv = buildTeamListSectionElement(teamDTO);
		
			parentDivContainer.appendChild(oneTeamDiv);
		}
	}
}



function fillAdminTeamSection(statusResponse)
{	
	let isDir, teamListSectionDiv, teamDTO, oneTeamDiv,
		shouldRebuildSection;
	
	shouldRebuildSection = statusResponse.teamInfo.teamMembershipModId != globalModIds.teamMembershipModId
		|| statusResponse.teamInfo.teamListModId != globalModIds.teamListModId
		|| statusResponse.playerInfo.playerDisplayNameModId != globalModIds.playerDisplayNameModId;
	
	isDir = isDirector(globalVars.selfPlayerObject);
	
	if (isDir && shouldRebuildSection)
	{
		teamListSectionDiv = document.getElementById("teamListSectionDiv");
		teamListSectionDiv.innerHTML = "";
		
		buildAdminTeamLists(teamListSectionDiv, statusResponse);
		
		fillSupplantCurrentPlayerList();
	}
}


function fillSupplantCurrentPlayerList()
{
	let selectList, optionElement;
	
	selectList = document.getElementById("administration_supplantCurrentPlayerSelectionList");
	
	selectList.innerHTML = "";
	
	for (const playerId in globalPlayerInfo) {
		optionElement = document.createElement("option");
		selectList.appendChild(optionElement);
		
		optionElement.value = playerId;
		optionElement.innerHTML = globalPlayerInfo[playerId].displayName;
	}

}
		

function fillUnassignedPlayerList(statusResponse)
{
	let unassignedPlayerListDiv, unassignedPlayerNameDiv, playerNameDiv,
		onePlayer, span, shouldRenderUnassignedPlayerList;
		
	shouldRenderUnassignedPlayerList = globalModIds.unassignedPlayersModId != statusResponse.playerInfo.unassignedPlayersModId
			|| statusResponse.playerInfo.playerDisplayNameModId != globalModIds.playerDisplayNameModId;
	
	if (shouldRenderUnassignedPlayerList)
	{
		unassignedPlayerListDiv = document.getElementById("unassignedPlayerList");
		unassignedPlayerNameDiv = document.getElementById("unassignedPlayerNameDiv");
		
		unassignedPlayerListDiv.innerHTML = "";
		
		for (const unassignedPlayerId of statusResponse.playerInfo.unassignedPlayers) {
			// Get player object
			// Build span with player id and display name
			// add to div.
			
			onePlayer = globalPlayerInfo[unassignedPlayerId];
			span = buildDisplayNameSpan(onePlayer, true, true);
			

			playerNameDiv = duplicateElement(unassignedPlayerNameDiv);
			playerNameDiv.appendChild(span);
			
			unassignedPlayerListDiv.appendChild(playerNameDiv);
		}
		

	}
}

