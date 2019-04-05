/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *  Will print out Debugging information if DEBUG is set to true in AIA.
 * @author Cory Edwards
 */
public class Debugging {
    
    /**
     * Calls System.out.print()
     * @param string The String that will be printed.
     */
    public void print(String string)
    {
        if(AIA.DEBUG)
            System.out.print(string);
    }
    
    /**
     * Calls System.out.println()
     * @param string The String that will be printed.
     */
    public void println(String string)
    {
        if(AIA.DEBUG)
            System.out.println(string);
    }
}
