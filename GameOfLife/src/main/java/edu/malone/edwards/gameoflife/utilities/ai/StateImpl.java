/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.malone.edwards.gameoflife.utilities.ai;

import edu.malone.edwards.admea.nodeUtils.State;

/**
 *
 * @author Cory Edwards
 */
public class StateImpl extends State<int[][]> {

    public StateImpl(int[][] state) {
        super(state);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        
        int[][] board = (int[][]) o;
        for(int i = 0; i < object.length; i++)
        {
            for(int x = 0; x < object.length; x++)
            {
                if(object[i][x] != board[i][x])
                    return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int[][] array = (int[][]) object;
        for(int i = 0; i < 10; i++)
        {
            for(int x = 0; x < 10; x++)
            {
                builder.append(array[i][x]);
            }
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }
    
}
