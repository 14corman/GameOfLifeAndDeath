/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.utilities;

import java.io.Serializable;
import java.util.Comparator;
/**
 *
 * @author Cory Edwards
 */
public class DifferenceComparator implements Comparator<Object[]>, Serializable
{
    @Override
    public int compare(Object[] x, Object[] y)
    {
        if ((int) x[0] > (int) y[0])
        {
            return -1;
        }
        else if ((int) x[0] < (int) y[0])
        {
            return 1;
        }
        else // equal
        {  
            return 0;
        }
    }
}
