/**
 */
package alex.date;

/**
 * Keeps track of a start time and how much time has elapsed
 * since the start time.  Pauasable.
 * 
 * This class is basically a very fancy way of keeping track of a start
 * timestamp.
 * 
 * @author alex
 */
public class ElapsedTime {
    
    public static enum TimerState
    {
    	notStarted, running, paused, ended;
    }

    private static final long DONT_USE_THIS_TIMESTAMP=-1L;
    
    private static final int NO_MAXIMUM_SECONDS=-1;
    private final Object readWriteMonitor=new Object();

    private final long maximumMilliseconds;
    private final int maximumSeconds;
    private TimerState timerState;
    private long lastResumptionTime;
    private long previouslyElapsedTime;

    /**
     * Use ElapsedTime.NO_MAXIMUM_SECONDS for maximumSeconds
     * if you want no finished point.
     * 
     * @param maximumSeconds 
     */
    public ElapsedTime(int maximumSeconds) {
        this.maximumSeconds=maximumSeconds;
        this.maximumMilliseconds=maximumSeconds*1000L;
        
        timerState=TimerState.notStarted;
        lastResumptionTime=DONT_USE_THIS_TIMESTAMP;
        previouslyElapsedTime=0L;
    }


    /**
     * Can only start from a notStarted state.
     */
    public void start() {
        synchronized(readWriteMonitor) {
            if (isNotStarted() || isEnded()) {
                timerState=TimerState.running;
                lastResumptionTime=System.currentTimeMillis();
                previouslyElapsedTime=0L;
            }
        }
    }

    public void pause() {
        synchronized(readWriteMonitor) {
            if (isRunning()) {
                timerState=TimerState.paused;
                if (lastResumptionTime!=DONT_USE_THIS_TIMESTAMP) {
                    previouslyElapsedTime += System.currentTimeMillis()-lastResumptionTime;
                    lastResumptionTime=DONT_USE_THIS_TIMESTAMP;
                }
            }
        }
    }

    public void resume() {
        synchronized(readWriteMonitor) {
            if (isPaused()) {
                timerState=TimerState.running;
                lastResumptionTime=System.currentTimeMillis();
            }
        }
    }

    /**
     * Set elapsed time to 0.  Can only be done when paused or ended.
     * In addition to setting elapsed time to 0, 
     * set the state to not started.
     */
    public void reset() {
        synchronized(readWriteMonitor) {
            if (isPaused() || isEnded()) {
                previouslyElapsedTime=0L;
                
                timerState=TimerState.notStarted;
            }
        }
    }

    /**
     * Return true if the state is running.
     * @return 
     */
    public boolean isRunning() {
        synchronized(readWriteMonitor) {
            return timerState==TimerState.running;
        }
    }

    /**
     * Return true if the state is paused.
     * @return 
     */
    public boolean isPaused() {
        synchronized(readWriteMonitor) {
            return timerState==TimerState.paused;
        }
    }

    /**
     * Return true if the state is ended.
     * @return 
     */
    public boolean isEnded() {
        synchronized(readWriteMonitor) {
            return timerState==TimerState.ended;
        }
    }

    /**
     * Return true if the state is notStarted.
     * @return 
     */
    public boolean isNotStarted() {
        synchronized(readWriteMonitor) {
            return timerState==TimerState.notStarted;
        }
    }
    
    
    /**
     * Not just a getter!  Sets the state to ended if too many milliseconds have elapsed.
     * @return
     */
    public long getElapsedMilliseconds() {
        synchronized(readWriteMonitor) {
            long retVal;

            retVal=0L;

            if (!isNotStarted()) {

                if (isRunning()) {
                    retVal=previouslyElapsedTime + System.currentTimeMillis() - lastResumptionTime;
                } else if (isPaused()) {
                    retVal=previouslyElapsedTime;
                }
                
                if (retVal>=maximumMilliseconds) {
                    timerState=TimerState.ended;
                }
                
                if (isEnded()) {
                    retVal=maximumMilliseconds;
                }
            }

            

            return retVal;
        }
    }


    /**
     * Not just a getter!  Sets the state to ended if too many seconds have elapsed.
     * @return
     */
    public int getElapsedSeconds() {
        synchronized(readWriteMonitor) {
            return (int)(getElapsedMilliseconds() / 1000L);
        }
    }

    /**
     * If the remaining seconds is 0, the state must be set.
     * This is handled in getElapsedSeconds().
     * 
     * @return
     */
    public int getRemainingSeconds() {
        synchronized(readWriteMonitor) {
            int retVal;

            retVal=maximumSeconds - getElapsedSeconds();

            return retVal;
        }
    }
    

    public TimerState getTimerState() {
        synchronized(readWriteMonitor) {
            return this.timerState;
        }
    }
    
    
    /**
     * Return true if the remaining time is 0 and it's in an ended state.
     * @return
     */
    public boolean hasFinished() {
    	return getRemainingSeconds()==0 && isEnded();
    }
}

