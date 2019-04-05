/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gnu.trove.map.hash.THashMap;
import java.util.Random;

/**
 * Holds all Nodes in the application, and can create, get, and work with all
 * of them.
 * 
 * @author Cory Edwards
 */
public class Nodes extends THashMap<Long, Node> {
    
    /**
     * @return The number of Nodes in the system.
     */
    public int numberOfNodes()
    {
        return size();
    }
    
    /**
     * Get the node with the given state. If a Node does not exist with that
     * state, then a new node will be made.
     * @param state The state to check against.
     * @param states The method created by the process to see if 2 states are equal.
     * @return The Node with that state.
     */
    public Node getNode(Object state, Equals states)
    {
        //See if there is a Node in the list with that state.
        for(Node node : values())
        {
            if(states.equal(node.getState(), state))
                return node;
        }
        
        //If the Node is not in the list then create it and return it.
        return get(createNode(state));
    }
    
    /**
     * Will get the Node with the given Id.
     * @param id The id of the wanted Node. Cannot be null.
     * @return The Node with the given Id.
     */
    public Node getNode(long id)
    {
        return get(id);
    }
    
    /**
     * Create a new Node with a unique Id and the given state.
     * @param state The state to give the new Node.
     */
    private long createNode(Object state)
    {
        //Give the Node a new random Id.
        Random random = new Random();
        long newId = random.nextLong();
        
        //Make sure the Id is unique.
        while(containsKey(newId))
            newId = random.nextLong();
        
        //Create the new Node and put it into the list.
        put(newId, new Node(state, newId));
        
        //Return the new Node's Id.
        return newId;
    }
}
