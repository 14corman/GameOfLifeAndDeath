/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;

/**
 * The main class for the algorithm.
 * 
 * VERSION 2 IDEA:
 *      Turn performAction into submit action and have it submit a runnable to
 *          an exutorservice that can then be cancelled and have it checking periodically if
 *          if the current thread is being interuppted by Thread.currentThread.isInterreupted().
 *          This way the programmer does not have to worry about that. Just have a shutdown method called.
 * 
 * @author Cory Edwards
 */
public abstract class AIA {
    
    /**
     *A always existing list of all of the Nodes in the algorithm.
     *Analogous to a jar holding every Node, and the Nodes are free of each
     *other in the jar. 
     */
    public static final Nodes NODE_LIST = new Nodes();
    
    /**
     * Used to keep track of the last root Node that was used to update
     * its binomial data in the next iteration.
     */
    private Long lastNodeId = null;
    
    /**
     * Used to keep track of the last winning child Node that was used to update
     * its binomial data in the next iteration.
     */
    private Long lastStepId = null; //used child Node
    
    /**
     * Used to determine if debugging is needed.
     */
    public static boolean DEBUG = false;
    
    private Debugging debugger = new Debugging();
    
    /**
     * Determine which child to go to next from the root.
     * @param children all of the children from the root Node.
     * @return The winning child's state.
     */
    private Object getWinningPath(long[] children)
    {
        THashMap<Long, Double> finalScores = new THashMap(); //Will be the list of children and their best scores.
        for(long child : children)
        {
            //Get the child's scores and probabilities per path.
            ArrayList<double[]> pathScores = getScoreProbabilties(child);
            double[] results = new double[pathScores.size()];
            
            //Each path's final score (binomial probabilities * sum of scores).
            for(int i = 0; i < results.length; i++)
                results[i] = pathScores.get(i)[0] * pathScores.get(i)[1];
            
            //Find the winning path.
            double max = -Double.MAX_VALUE;
            for(double item : results)
                if(item > max)
                    max = item;
            
            //Put the child's Id into the list with its winning path's score.
            finalScores.put(child, max);
        }
        
        //Find the child with the highest score.
        long winningChild = children[0];
        double max = -Double.MAX_VALUE;
        for(long child : finalScores.keySet())
        {
            double item = finalScores.get(child);
            if(item >= max)
            {
                winningChild = child;
                max = item;
            }
        }
        
        //Return the winning child's state.
        return NODE_LIST.getNode(winningChild).getState();
    }
    
    /**
     * Recursively go over each child's tree to find all score, probability combinations
     * for all possible paths.
     * @param childId The current Node's child.
     * @return An ArrayList of all possible score, probability combinations for all current paths.
     */
    private ArrayList<double[]> getScoreProbabilties(long childId)
    {
        //If there are no children for the current Node then we have hit a leaf Node.
        if(NODE_LIST.getNode(childId).children.length == 0)
        {
            //Get the leaf Node's score and binomial probability.
            ArrayList<double[]> temp = new ArrayList();
            temp.add(new double[]{NODE_LIST.getNode(childId).calculateProbability(), NODE_LIST.getNode(childId).getMaxQScore()});
            return temp;
        }
        else //There are possibly branches so we need to go over them.
        {
            //Get an ArrayList ready to hold all paths.
            ArrayList<double[]> results = new ArrayList();
            
            //Go over current Node's children.
            for(long child : NODE_LIST.getNode(childId).children)
            {
                //Get all of the child's paths.
                ArrayList<double[]> tempResults = getScoreProbabilties(child);
                
                //Go over every path and combine the current Node's binomial probability
                //and score.
                for(double[] list : tempResults.subList(0, tempResults.size()))
                {
                    list[0] = list[0] * NODE_LIST.getNode(childId).calculateProbability();
                    list[1] = list[1] + NODE_LIST.getNode(childId).getMaxQScore();
                    results.add(list);
                }
            }
            
            //We are starting to go back to the root (which is a child of the actual root).
            return results;
        }
    }
    
    /**
     * Main method to the algorithm. Call this on every iteration and the algorithm
     * does the rest.
     * @param state The current state in the process the algorithm is running in.
     * @return The next step to take in the process.
     */
    public Object performAction(Object state)
    {
        debugger.println("Starting process.");
        
        //If it is null, the algorithm is just starting.
        //Otherwise we need to see how the last root did.
        if(lastNodeId != null)
        {
            //If the proccess thinks the past Node pair was a success.
            if(isSuccess(NODE_LIST.getNode(lastStepId), state))
            {
                NODE_LIST.getNode(lastNodeId).addSuccess();
                NODE_LIST.getNode(lastStepId).addSuccess();
            }        
            
            //Update the Nodes' probabilities.
            updateProbabilities();
        }
        
        //This will get the Node with the given state, or make one and return it.
        Node root = NODE_LIST.getNode(state, (stateA, stateB) -> {return areStatesEqual(stateA, stateB);});
        
        debugger.println("Root Node Id: " + root.getNodeId());
        
        //When each Node is made there is a boolean that is set to true.
        //When isNew is called it gets that boolean then sets it to false.
        if(root.isNew())
        {
            debugger.println("Creating children.");
            //Create the new Node's children.
            root.children = setChildren(state);
            
            debugger.println("Children created.");
            debugger.println("Calculating score.");
            
            //Set the new Node's score.
            root.setScore(calculateScore(state));
            
            debugger.println("Score calculated.");
            debugger.println("Starting Q learning.");
            
            //Create policy for all Nodes and apply them.
            QLearning q = new QLearning();
            q.learn();
            
            debugger.println("Q learning done.");
            debugger.println("Given Nodes policies.");
            q.applyPolicies();
            debugger.println("Policies given.");
        }
        
        //Use the policy for the Node given the state along with combined 
        //binomial distributions to get the correct next Node's state to take.
        Object nextState = getWinningPath(root.children);
        
        debugger.println("Next path found.");
        
        //Get the root Node's Id for later.
        lastNodeId = root.getNodeId();
        
        //Add an occurence to both Nodes being used. (IE n++ in a binomial distribution)
        root.addOccurrence();
        NODE_LIST.getNode(nextState, (stateA, stateB) -> {return areStatesEqual(stateA, stateB);}).addOccurrence();
        
        lastStepId = NODE_LIST.getNode(nextState, (stateA, stateB) -> {return areStatesEqual(stateA, stateB);}).getNodeId();
        
        debugger.println("Next Node Id: " + lastStepId);
        debugger.println("Returning next state.");
        
        //Return the next state to take.
        return nextState;
    }
    
    /**
     * Use lastNodeId and lastStepId to call: NODE_LIST.getNode(lastNodeId).recalcProb();
     * Alternatively, someone could override this method and give a probability
     * rather than calculating it. This could be done by calling .giveProb(new probability)
     * instead of .recalcProb().
     */
    public void updateProbabilities()
    {
        NODE_LIST.getNode(lastNodeId).recalcProb();
        NODE_LIST.getNode(lastStepId).recalcProb(); 
    }
    
    /**
     * When a path's action is performed, it will try and see if it was successful or not.
     * @param lastNode The winning child Node from the last iteration.
     * @param newNodeState The new state in the current iteration.
     * @return True if and only if the child Node taken was successful.
     */
    public abstract boolean isSuccess(Node lastNode, Object newNodeState);
    
    /**
     * How the program will calculate the score to give to Q learning when a policy is being made.
     * @param state The state to get the score from.
     * @return The score that will go along with this state.
     */
    public abstract int calculateScore(Object state);
    
    /**
     * 
     * @param stateA A Node's state.
     * @param stateB The new state.
     * @return True if and only if stateA = stateB
     */
    public abstract boolean areStatesEqual(Object stateA, Object stateB);
    
    /**
     * Use the given state and NODE_LIST to create all of the children from 
     * the state, and return a list of their Ids.
     * @param state The parent's state
     * @return A long[] of all of the newly created child Ids.
     */
    public abstract long[] setChildren(Object state);
    
}
