/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 * Will give the method to Nodes that tells if 2 Nodes are equal or not.
 * @author Cory Edwards
 */
interface Equals {
    
    /**
     * 
     * @param stateA A Node's state.
     * @param stateB The new state.
     * @return True if and only if stateA = stateB
     */
    public boolean equal(Object stateA, Object stateB);
}
