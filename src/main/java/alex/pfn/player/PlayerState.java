package alex.pfn.player;

public enum PlayerState {
	// The user can't do anything.
	gameNotStarted,
	
	buildingPhrase,
	finishedBuildingPhrase,
	matchingPhrasesToDefinitions,
	
	finishedMatchingPhrasesToDefinitions,

	// Use these two for the round end.
	// waitingToShowResults is probably not really needed,
	// but I think it'll be easier for me to have this extra state.
	showingResultsWithoutDefinition,
	showingResultsWithDefinition,
	waitingToShowResults,
	
	// After you have finished showing your results, you go into
	// this state.
	// If all players have gone into this state, advance the game
	// to either next round or end game.
	roundEnd;
}
