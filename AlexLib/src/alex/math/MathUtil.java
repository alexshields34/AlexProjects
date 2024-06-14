/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alex.math;

/**
 *
 * @author alex
 */
public class MathUtil {
    public static int boundaryCheck(int a, int lowest, int highest)
    {
        if (lowest>highest) {
            throw new java.lang.IllegalArgumentException("lowest cannot be bigger than highest. lowest=["
                    +lowest
                    +"], highest=["
                    +highest
                    +"]");
        }
        return a>highest?highest:(a<lowest?lowest:a);
    }
}
