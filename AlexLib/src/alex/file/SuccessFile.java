
package alex.file;

import alex.date.ElapsedTime;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The purpose of this file is to retain a list of items.  Another instance's
 * method will check to see if an item is in the list already, and if not,
 * do something with that item, and then add it to this file's list.  If the
 * item is already in the list, then don't perform whatever its operation is.
 * 
 * Example: If a method has a list of URLs, then the content at those URLs will
 * be downloaded unless the URL is already in this SuccessFile.  In other words,
 * only download things that haven't been downloaded yet.
 * 
 * Potentially save the file every 2 minutes.
 * 
 * @author alex
 */
public class SuccessFile {
    
    private final File targetFile;
    private final ArrayList<String> successfulSavings;
    private ElapsedTime timeTracker;
    private final boolean shouldWriteToDiskPeriodically;
    
    
    public SuccessFile(File targetFile, boolean shouldWriteToDiskPeriodically)
            throws FileNotFoundException, IOException
    {
        this.targetFile=targetFile;
        
        this.targetFile.createNewFile();
        
        successfulSavings=FileUtil.readTextFileAsArray(targetFile);
        this.shouldWriteToDiskPeriodically=shouldWriteToDiskPeriodically;
        if (shouldWriteToDiskPeriodically) {
            timeTracker=new ElapsedTime(2*60);
            timeTracker.start();
        }
    }
    
    public void add(String successfulItem)
            throws FileNotFoundException
    {
        successfulSavings.add(successfulItem);
        
        if (this.shouldWriteToDiskPeriodically) {
            if (timeTracker.hasFinished()) {
                saveFile();
                timeTracker.reset();
                timeTracker.start();
            }
        }
    }
    
    public void saveFile()
            throws FileNotFoundException
    {
        FileUtil.saveArrayAsTextFile(this.targetFile, successfulSavings);
    }
    
    public void addAndSaveFile(String successfulItem)
            throws FileNotFoundException
    {
        add(successfulItem);
        saveFile();
    }
    
    public boolean contains(String item) {
        return successfulSavings.contains(item);
    }
    
}
        



        
