package alex.pfn;

/**
 * This state enum takes precedence over the timer
 * object's state, regarding the state of the game.
 * @author alex
 *
 */
public enum GameState
{
	// The game has been forcefully ended before it completed. Do I need this?
	gameAborted,
	
	// The current player just changed.  The timer is in the notStarted or ended state.
	// We're waiting for the new current player person to start the timer.
	waitingToStartTurn,
	
	// The current player said a bad word or
	// passed the current card.
	failureHappened,
	
	// The game has ended.
	ended,
	
	// No game has been generated
	notStarted,
	
	// The current player paused the game.
	paused,
	
	// The timer is running or paused, a card was given to the current player
	running;
}