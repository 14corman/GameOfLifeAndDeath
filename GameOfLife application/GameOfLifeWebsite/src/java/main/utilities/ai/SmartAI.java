/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.utilities.ai;

import edu.malone.edwards.admea.ASystem;
//import edu.malone.edwards.admea.nodeUtils.GraphBuilder;
import edu.malone.edwards.admea.nodeUtils.Node;
import edu.malone.edwards.admea.nodeUtils.State;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import main.servlets.GameOfLifeAndDeathGame;
import org.apache.commons.lang3.ArrayUtils;
import org.openide.util.Exceptions;

/**
 * 
 * @author Cory Edwards
 */
public class SmartAI extends ASystem<StateImpl> implements Serializable{
    
    private final int length; //Length of a row / column of currentGenerationAI.
    
    //The singleton instance.
    private static SmartAI instance = null;
    
    private int player = 2;
    
    protected SmartAI(int n) 
    {
       length = n;
    }
    
    public static SmartAI getInstance(int n) 
    {
        if(instance == null)
           instance = new SmartAI(n);

        return instance;
    }
    
    private ArrayList<Point> setGeneration(int[][] current)
    {
        ArrayList<Point> computerCells = new ArrayList();
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < length; j++)
            {
                if(current[i][j] == player)
                    computerCells.add(new Point(j, i));
            }
        }
        
        return computerCells;
    }
    
    private int calculateDifference(int[][] state)
    {
        int totalPlayerCells = 0;
        int totalComputerCells = 0;
        
        for(int[] row : state)
        {
            for(int i : row)
            {
                if(i == player)
                    totalPlayerCells++;
                else
                    totalComputerCells++;
            }
        }
        return (totalComputerCells - totalPlayerCells);
    }
    
    /**
     * An exact copy of the game's iterate method. This way the AI knows the
     * rules of Conway's Game of Life to make an educated guess on what to do.
     */
    private int[][] iterate(int[][] currentState) 
    {
        int[][] nextState = new int[length][length];
        
        //Keep track of which player the cells around the current one belong to.
        int ones;
        int twos;
        
        //Go over each cell.
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < length; j++)
            {
                ones = 0;
                twos = 0;
                
                //If the cell is not on the right most side of the board.
                if (j < length - 1) 
                {
                    //right
                    if(currentState[i][j + 1] == 1)
                        ones++;
                    else if(currentState[i][j + 1] == 2)
                        twos++;

                    //If the cell is not at the bottom of the grid.
                    if(i > 0)
                    {
                        //upper right
                        if(currentState[i - 1][j + 1] == 1)
                            ones++;
                        else if(currentState[i - 1][j + 1] == 2)
                            twos++;
                    }

                    //If the cell is not at the top of the grid.
                    if (i < length - 1)
                    {
                        //Lower right
                        if(currentState[i + 1][j + 1] == 1)
                            ones++;
                        else if(currentState[i + 1][j + 1] == 2)
                            twos++;
                    }
                }

                //If the cell is not at the left most side of the board.
                if (j > 0) 
                {
                    //left
                    if(currentState[i][j - 1] == 1)
                        ones++;
                    else if(currentState[i][j - 1] == 2)
                        twos++;

                    //If the cell is not at the bottom of the currentGenerationAI.
                    if (i > 0)
                    {
                        //upper left
                        if(currentState[i - 1][j - 1] == 1)
                            ones++;
                        else if(currentState[i - 1][j - 1] == 2)
                            twos++;
                    }

                    //If the cell is not at the top of the grid.
                    if (i < length - 1)
                    {
                        //lower left
                        if(currentState[i + 1][j - 1] == 1)
                            ones++;
                        else if(currentState[i + 1][j - 1] == 2)
                            twos++;
                    }
                }

                //If the cell is not at the bottom of the grid.
                if (i > 0)
                {
                    //up
                    if(currentState[i - 1][j] == 1)
                        ones++;
                    else if(currentState[i - 1][j] == 2)
                        twos++;
                }

                //If the cell is not at the top of the currentGenerationAI.
                if (i < length - 1)
                {
                    //down
                    if(currentState[i + 1][j] == 1)
                        ones++;
                    else if(currentState[i + 1][j] == 2)
                        twos++;
                }

                //Now go through and update the next generation.
                int sum = ones + twos;

                if(currentState[i][j] != 0) //If the cell is not dead.
                {
                    //If the cell has less than 2 neighbor cells then it does from starvation.
                    //If it has more than 3 then it dies from overcrowding.
                    if (sum < 2 || sum > 3)
                        nextState[i][j] = 0;
                    else
                        nextState[i][j] = currentState[i][j]; //Otherwise make both grids equal.
                }
                else //If the cell is dead.
                {
                    //The cell needs to have 3 neighbors to be reborn.
                    if(sum == 3)
                    {
                        //Whichever player has the most cells determines which
                        //side the newly born cell goes to.
                        if(ones > twos)
                            nextState[i][j] = 1;
                        else
                            nextState[i][j] = 2;
                    }
                    else //The cell died.
                        nextState[i][j] = 0;
                }
            }
        }
        
        return nextState;
    }
    
    private String[] calculateOptions(int[][] state, ArrayList<Point> computerCells)
    {
        ArrayList<String> childrenIds = new ArrayList();
        
        int[][] currentState = new int[length][length];
        
        //To copy state to currentState.
        for(int i = 0; i < length; i++)
        {
            System.arraycopy(state[i], 0, currentState[i], 0, length);
        }
        
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < length; j++)
            {
                //To reset state.
                for(int x = 0; x < length; x++)
                {
                    System.arraycopy(currentState[x], 0, state[x], 0, length);
                }
                
                if(state[i][j] != 0)
                {
                    state[i][j] = 0;
                    
                    Node node = getNode(new StateImpl(iterate(state)));
                    
                    //Set the new Node's score.
                    node.setScore(calculateScore(new StateImpl(iterate(state))));
                                
                    childrenIds.add(node.getNodeId());
                }
                else
                {
                    for(Point p1 : computerCells)
                    {
                        for(Point p2 : computerCells)
                        {
                            if(!(p1.x == p2.x && p1.y == p2.y))
                            {
                                //To reset state.
                                for(int x = 0; x < length; x++)
                                {
                                    System.arraycopy(currentState[x], 0, state[x], 0, length);
                                }

                                state[i][j] = player;
                                state[p1.y][p1.x] = 0;
                                state[p2.y][p2.x] = 0;
                                
                                Node node = getNode(new StateImpl(iterate(state)));
                                
                                //Set the new Node's score.
                                node.setScore(calculateScore(new StateImpl(iterate(state))));
                                
                                childrenIds.add(node.getNodeId());
                            }
                        }
                    }
                }
            }
        }
        
        return childrenIds.toArray(new String[0]);
    }
    
    private Point[] getMove(int[][] state, ArrayList<Point> computerCells, int[][] newState)
    {
        int[][] currentState = new int[length][length];
        
        //To copy state to currentState.
        for(int i = 0; i < length; i++)
        {
            System.arraycopy(state[i], 0, currentState[i], 0, length);
        }
        
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < length; j++)
            {
                //To reset state.
                for(int x = 0; x < length; x++)
                {
                    System.arraycopy(currentState[x], 0, state[x], 0, length);
                }
                
                if(state[i][j] != 0)
                {
                    state[i][j] = 0;
                    
                    if(areStatesEqual(new StateImpl(iterate(state)), new StateImpl(newState)))
                        return new Point[]{new Point(j, i)};
                }
                else
                {
                    for(Point p1 : computerCells)
                    {
                        for(Point p2 : computerCells)
                        {
                            if(!(p1.x == p2.x && p1.y == p2.y))
                            {
                                //To reset state.
                                for(int x = 0; x < length; x++)
                                {
                                    System.arraycopy(currentState[x], 0, state[x], 0, length);
                                }

                                state[i][j] = player;
                                state[p1.y][p1.x] = 0;
                                state[p2.y][p2.x] = 0;
                                
                                if(areStatesEqual(new StateImpl(iterate(state)), new StateImpl(newState)))
                                    return new Point[]{new Point(j, i), p1, p2};
                            }
                        }
                    }
                }
            }
        }
        
        return new Point[0];
    }
    
    /**
     * When it is player 1's turn. Used to see what move player 1 will make.
     * @param s
     * @param g 
     */
    public synchronized void thinkMove(int[][] s, GameOfLifeAndDeathGame g)
    {
        player = 1;

        int[][] currentStateCopy = new int[length][length];

        //To copy state to currentState.
        for(int i = 0; i < length; i++)
        {
            System.arraycopy(s[i], 0, currentStateCopy[i], 0, length);
        }

        if(Thread.currentThread().isInterrupted())
            return;

        //To debug what is going on in ADMEA.
        DEBUG = true;

        if(Thread.currentThread().isInterrupted())
            return;

//        System.out.println("Thinking");

        StateImpl state = new StateImpl(currentStateCopy);
//        System.out.println("Hash code 1: " + state.hashCode());
//        System.out.println("Hash code 2: " + state.hashCode());
//        printState(state.object);

        State<int[][]> newState = performAction(state);
//        System.out.println("Hash code 1: " + newState.hashCode());
//        System.out.println("Hash code 2: " + newState.hashCode());
//        printState(newState.object);
    }
    
    public synchronized void makeMove(int[][] s, GameOfLifeAndDeathGame g, AsyncContext c)
    {
        c.start(new Runnable() 
        {
            private final int[][] state = s;
            private final GameOfLifeAndDeathGame game = g;
            private final AsyncContext context = c;
            
            @Override
            public void run() 
            {
                try
                {
                    player = 2;
                    int[][] currentStateCopy = new int[length][length];
                    
                    //To copy state to currentState.
                    for(int i = 0; i < length; i++)
                    {
                        System.arraycopy(state[i], 0, currentStateCopy[i], 0, length);
                    }
                    
                    if(Thread.currentThread().isInterrupted())
                        return;

                    //To debug what is going on in ADMEA.
                    DEBUG = true;
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch(Exception ex){}

                    if(Thread.currentThread().isInterrupted())
                        return;
                    
                    StateImpl oldState = new StateImpl(currentStateCopy);
                    
                    StateImpl newTempState = performAction(oldState);
//                    try 
//                    {
//                        GraphBuilder.getInstance().build("C:\\Server Files\\GameOfLife");
//                    }
//                    catch (Exception ex) 
//                    {
//                        Exceptions.printStackTrace(ex);
//                    }

                    int[][] newState = newTempState.object;
                    
//                    System.out.println("Making move");
//                    
//                    System.out.println("Hash code 1: " + oldState.hashCode());
//                    System.out.println("Hash code 2: " + oldState.hashCode());
//                    printState(oldState.object);
//
//                    System.out.println("Hash code 1: " + newTempState.hashCode());
//                    System.out.println("Hash code 2: " + newTempState.hashCode());
//                    printState(newTempState.object);

                    if(Thread.currentThread().isInterrupted())
                        return;

                    Point[] move = getMove(state, setGeneration((int[][]) state), newState);

                    if(Thread.currentThread().isInterrupted())
                        return;

                    switch (move.length) 
                    {
                        case 1:
                            game.makeMove(move[0].y, move[0].x, 2);
                            break;
                        case 3:
                            game.makeMove(move[0].y, move[0].x, 2);
                            try
                            {
                                Thread.sleep(2000);
                            }
                            catch(Exception ex){}

                            if(Thread.currentThread().isInterrupted())
                                return;

                            game.makeMove(move[1].y, move[1].x, 2);
                            try
                            {
                                Thread.sleep(2000);
                            }
                            catch(Exception ex){}

                            if(Thread.currentThread().isInterrupted())
                                return;

                            game.makeMove(move[2].y, move[2].x, 2);
                            break;
                        default:
                            game.makeMove(0, 0, 2);
                            break;
                    }

                    if(Thread.currentThread().isInterrupted())
                        return;

                    try
                    {
                        Thread.sleep(3000);
                    }
                    catch(Exception ex){}

                    if(Thread.currentThread().isInterrupted())
                        return;

                    game.applyMove(2);
                    game.saveGame((HttpServletResponse) context.getResponse());
                    context.complete();
                }
                //If when the thread is suddenly shut down these 2 errors may get
                //called due to classes trying to be called after shutdown.
                catch(Exception ex){ex.printStackTrace();}
            }
        });
    }
    
    public void thinkMoveIndependent(int[][] s, GameOfLifeAndDeathGame g)
    {
        player = 2;

        int[][] currentStateCopy = new int[length][length];

        //To copy state to currentState.
        for(int i = 0; i < length; i++)
        {
            System.arraycopy(s[i], 0, currentStateCopy[i], 0, length);
        }

        //To debug what is going on in ADMEA.
//        DEBUG = true;

        StateImpl state = new StateImpl(currentStateCopy);
        performAction(state);
    }
    
    public void makeMoveIndependent(int[][] state, GameOfLifeAndDeathGame game)
    {
        try
        {
            player = 1;
            int[][] currentStateCopy = new int[length][length];

            //To copy state to currentState.
            for(int i = 0; i < length; i++)
            {
                System.arraycopy(state[i], 0, currentStateCopy[i], 0, length);
            }

            //To debug what is going on in ADMEA.
            DEBUG = true;

            StateImpl oldState = new StateImpl(currentStateCopy);
            StateImpl newTempState = performAction(oldState);
            
//            try 
//            {
//                GraphBuilder.getInstance().build("C:\\Server Files\\GameOfLife");
//            }
//            catch (Exception ex) 
//            {
//                Exceptions.printStackTrace(ex);
//            }

            int[][] newState = newTempState.object;

//            System.out.println("Making move");
//
//            System.out.println("Hash code 1: " + oldState.hashCode());
//            System.out.println("Hash code 2: " + oldState.hashCode());
//            printState(oldState.object);
//
//            System.out.println("Hash code 1: " + newTempState.hashCode());
//            System.out.println("Hash code 2: " + newTempState.hashCode());
//            printState(newTempState.object);

            Point[] move = getMove(state, setGeneration((int[][]) state), newState);

            switch (move.length) 
            {
                case 1:
                    game.makeMove(move[0].y, move[0].x, 1);
                    break;
                case 3:
                    game.makeMove(move[0].y, move[0].x, 1);
                    game.makeMove(move[1].y, move[1].x, 1);
                    game.makeMove(move[2].y, move[2].x, 1);
                    break;
                default:
                    game.makeMove(0, 0, 1);
                    break;
            }

            game.applyMove(1);
        }
        catch(Exception ex){ex.printStackTrace();}
    }
    
    private void printState(int[][] s)
    {
        for(int[] row : s)
            System.out.println(ArrayUtils.toString(row));
    }

    @Override
    public boolean isSuccess(Node lastNode, StateImpl newNodeState)
    {
        String winningChild = lastNode.getMaxQChild();
        String currentChild = getNode(newNodeState).getNodeId();
        
        return (winningChild == null ? currentChild != null : !winningChild.equals(currentChild));
    }

    @Override
    public int calculateScore(StateImpl state) 
    {
        return calculateDifference(state.object);
    }

    @Override
    public boolean areStatesEqual(StateImpl stateA, StateImpl stateB) {
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < length; j++)
            {
                if(stateA.object[i][j] != stateB.object[i][j])
                    return false;
            }
        }
        
        return true;
    }

    @Override
    public String[] setChildren(StateImpl state) 
    {
        return calculateOptions(state.object, setGeneration(state.object));
    }
}
