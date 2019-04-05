/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import static main.AIA.DEBUG;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @author Cory Edwards
 */
public class SmartAI extends AIA implements Serializable{
    
    private final int length; //Length of a row / column of currentGenerationAI.
    
    //The job queue for the AI.
    //We want it to only run 1 thread at a time.
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();
    
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
                if(i == 1)
                    totalPlayerCells++;
                else if(i == 2)
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
    
    private Long[] calculateOptions(int[][] state, ArrayList<Point> computerCells)
    {
        ArrayList<Long> childrenIds = new ArrayList();
        
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
                    
                    Node node = NODE_LIST.getNode(new State<>(iterate(state)));
                    
                    //Set the new Node's score.
                    node.setScore(calculateScore(new State<>(iterate(state))));
                                
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
                                
                                Node node = NODE_LIST.getNode(new State<>(iterate(state)));
                                
                                //Set the new Node's score.
                                node.setScore(calculateScore(new State<>(iterate(state))));
                                
                                childrenIds.add(node.getNodeId());
                            }
                        }
                    }
                }
            }
        }
        
        return childrenIds.toArray(new Long[0]);
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
                    
                    if(areStatesEqual(new State<>(iterate(state)), new State<>(newState)))
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

                                state[i][j] = 2;
                                state[p1.y][p1.x] = 0;
                                state[p2.y][p2.x] = 0;
                                
                                if(areStatesEqual(new State<>(iterate(state)), new State<>(newState)))
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
    public synchronized void thinkMove(int[][] s, AtomicReference<GameOfLifeAndDeathGame> g)
    {
        executorService.execute(new Runnable() 
        {
            @Override
            public void run() 
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

                //To debug what is going on in AIA.
                DEBUG = true;

                if(Thread.currentThread().isInterrupted())
                    return;

//                System.out.println("Thinking");
//
//                State<int[][]> state = new State<>(currentStateCopy);
//                System.out.println("Hash code 1: " + state.hashCode());
//                System.out.println("Hash code 2: " + state.hashCode());
//                printState(state.object);
//
//                State<int[][]> newState = performAction(state);
//                System.out.println("Hash code 1: " + newState.hashCode());
//                System.out.println("Hash code 2: " + newState.hashCode());
//                printState(newState.object);
            }
        });
    }
    
    public synchronized void makeMove(int[][] s, AtomicReference<GameOfLifeAndDeathGame> g)
    {
        executorService.execute(new Runnable() 
        {
            private final int[][] state = s;
            private final AtomicReference<GameOfLifeAndDeathGame> game = g;
            
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

                    //To debug what is going on in AIA.
                    DEBUG = true;
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch(Exception ex){}

                    if(Thread.currentThread().isInterrupted())
                        return;
                    
                    State<int[][]> oldState = new State<>(currentStateCopy);
                    
                    State<int[][]> newTempState = performAction(oldState);

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
                            game.get().makeMove(move[0].y, move[0].x, 2);
                            break;
                        case 3:
                            game.get().makeMove(move[0].y, move[0].x, 2);
                            try
                            {
                                Thread.sleep(2000);
                            }
                            catch(Exception ex){}

                            if(Thread.currentThread().isInterrupted())
                                return;

                            game.get().makeMove(move[1].y, move[1].x, 2);
                            try
                            {
                                Thread.sleep(2000);
                            }
                            catch(Exception ex){}

                            if(Thread.currentThread().isInterrupted())
                                return;

                            game.get().makeMove(move[2].y, move[2].x, 2);
                            break;
                        default:
                            game.get().makeMove(0, 0, 2);
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

                    game.get().applyMove(2);
                }
                //If when the thread is suddenly shut down these 2 errors may get
                //called due to classes trying to be called after shutdown.
                catch(IllegalStateException | NoClassDefFoundError ex){}
            }
        });
    }
    
    private void printState(int[][] s)
    {
        for(int[] row : s)
            System.out.println(ArrayUtils.toString(row));
    }

    @Override
    public boolean isSuccess(Node lastNode, State<?> newNodeState)
    {
        long winningChild = lastNode.getMaxQChild();
        long currentChild = NODE_LIST.getNode(newNodeState).getNodeId();
        
        return winningChild != currentChild;
    }

    @Override
    public int calculateScore(State<?> state) 
    {
        return calculateDifference((int[][]) state.object);
    }

    @Override
    public boolean areStatesEqual(State<?> stateA, State<?> stateB) {
        boolean same = true;
        
        int[][] tempA = (int[][]) stateA.object;
        int[][] tempB = (int[][]) stateB.object;
        
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < length; j++)
            {
                if(same)
                    same = tempA[i][j] == tempB[i][j];
                else
                    break;
            }
        }
        
        return same;
    }

    @Override
    public long[] setChildren(State<?> state) 
    {
        return ArrayUtils.toPrimitive(calculateOptions((int[][]) state.object, setGeneration((int[][]) state.object)));
    }
}
