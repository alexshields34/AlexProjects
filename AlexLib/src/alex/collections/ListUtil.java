
package alex.collections;

/**
 * Provides some static methods for commonly used list operations.
 * 
 * @author alex
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ListUtil {
    
    private static final Random rng;
    
    static {
        rng=new Random(System.currentTimeMillis());
    }
    
    /**
     * Return a new List which is copied from the source list, but in random
     * order.  The returned list isn't immutable.
     * 
     * @param sourceList
     * @param <E> Any class.  It looks like specifying this generic class in
     * the erasure of the method is not actually necessary.  At least, not in
     * java 1.8.  By specifying this generic class, I wanted to avoid calling
     * classes from having to cast the returned value to a class with a
     * parameterized class.
     * @return 
     */
    public static <E> ArrayList<E> randomizeList(final List<E> sourceList) {
        E o;
        int index;
        ArrayList<E> tempList;
        ArrayList<E> targetList;
        
        targetList=new ArrayList<E>();
        tempList=new ArrayList<E>();
        tempList.addAll(sourceList);
        
        
        while (!tempList.isEmpty()) {
            index=rng.nextInt(tempList.size());
            o=tempList.remove(index);
            targetList.add(o);
        }
        
        return targetList;
    }
    
}