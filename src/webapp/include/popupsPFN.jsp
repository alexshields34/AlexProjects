<%-- This isn't a library.  This file handles all modal windows in the application. --%>

<%@page import="alex.pfn.FEEventType"%>
<%@page import="alex.pfn.FEEvent"%>
<%@page import="alex.pfn.Constants"%>
<%@page import="alex.pfn.GameServer"%>
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@page import="org.springframework.web.context.support.SpringBeanAutowiringSupport"%>

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
String yourTurnToBlabURLAsString, justListenURLAsString;

yourTurnToBlabURLAsString = Constants.imagesDirectory + "/yourturn.jpeg";
justListenURLAsString = Constants.imagesDirectory + "/just-listen-75.jpg";

%>



function performCloseModalPanel()
{
	let div, img;
	div = document.getElementById("modalPanel");
	
	img = document.getElementById("modalPanelImage");
	img.src = "";
	
	div.style.display = "none";
	globalVars.isModalPanelShowing = false;
}


function closeModalPanel(event)
{	
	performCloseModalPanel();
}



function openModalPanel(imageUrl)
{

	let numberOfImages, victoryImage, whichImageIndex, img,
		div, obj, rect, victoryType;
	
	if (!globalVars.isModalPanelShowing) {
		globalVars.isModalPanelShowing = true;
		
		div = document.getElementById("modalPanel");
	
		div.style.display = "inline-block";

		img = document.getElementById("modalPanelImage");
		img.src = imageUrl;
	}
}

/**
 * @param feEventType A value from the enum FEEvent.EventType.
 * @deprecated This was originally for KrazyWordz and it needs changing for PfN.
 */
function openResultsModalPanel(feEventType)
{
	let imageURLToShow;
	
	if (feEventType == "<%= FEEventType.successfullyScoredCard %>") {
		imageURLToShow = "<%= yourTurnToBlabURLAsString %>";
	} else if (feEventType == "<%= FEEventType.yourTurnEnded %>") {
		imageURLToShow = "<%= justListenURLAsString %>";
	}
	
	openModalPanel(imageURLToShow);
}





function moveVictoryPanelIfNecessary(event) {
	let obj, div, rect;

	div = document.getElementById("victoryPanel");
	if (div.style.display != "none" ) {
	
		// Put the victory panel at the upper left corner of the play area.
		obj=document.getElementById("currentPlayerStatusMessageSpan");
		rect=obj.getBoundingClientRect();
		div.style.top=rect.top+"px";
		div.style.left=rect.left+"px";
	}

}
