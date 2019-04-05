/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflifeanddeath;

import java.awt.Point;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 *
 * @author Cory Edwards
 */
public class AverageAI {
    
    public final int[][] oldGenerationAI; //old
    public final int[][] currentGenerationAI; //current
    public final int[][] nextGenerationAI; //future
    
    public ArrayList<Point> computerCells = new ArrayList();
    
    private final int length; //Length of a row / column of currentGenerationAI.
    
    final GameOfLifeAndDeathGame game;
    
    PriorityQueue<Object[]>  cellChange = new PriorityQueue(1, new DifferenceComparator());
    
    AverageAI(GameOfLifeAndDeathGame game, int n)
    {
        this.game = game;
        oldGenerationAI = new int[n][n];
        currentGenerationAI = new int[n][n];
        nextGenerationAI = new int[n][n];
        length = n;
    }
    
    public void setGeneration(int[][] current)
    {
        computerCells.clear();
        for(int i = 0; i < length; i++)
        {
            System.arraycopy(current[i], 0, oldGenerationAI[i], 0, oldGenerationAI[i].length);
            System.arraycopy(current[i], 0, currentGenerationAI[i], 0, currentGenerationAI[i].length);
            System.arraycopy(current[i], 0, nextGenerationAI[i], 0, nextGenerationAI[i].length);
            
            for(int j = 0; j < length; j++)
            {
                if(current[i][j] == 2)
                    computerCells.add(new Point(j, i));
            }
        }
    }
    
    private void reset()
    {
        for(int i = 0; i < length; i++)
        {
            System.arraycopy(oldGenerationAI[i], 0, nextGenerationAI[i], 0, length);
            System.arraycopy(oldGenerationAI[i], 0, currentGenerationAI[i], 0, length);
        }
    }
    
    private void calculateOptions()
    {
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < length; j++)
            {
                reset();
                
                if(currentGenerationAI[i][j] != 0)
                {
                    currentGenerationAI[i][j] = 0;
                    iterate();
                    cellChange.add(new Object[]{calculateDifference(), new Point(j, i)});
                }
                else
                {
                    for(Point p1 : computerCells)
                    {
                        for(Point p2 : computerCells)
                        {
                            if(p1.x != p2.x && p1.y != p2.y)
                            {
                                reset();

                                currentGenerationAI[i][j] = 2;
                                iterate();
                                currentGenerationAI[p1.y][p1.x] = 0;
                                iterate();
                                currentGenerationAI[p2.y][p2.x] = 0;
                                iterate();
                                cellChange.add(new Object[]{calculateDifference(), new Point(j, i), p1, p2});
                            }
                        }
                    }
                }
            }
        }
    }
    
    private int calculateDifference()
    {
        int totalPlayerCells = 0;
        int totalComputerCells = 0;
        
        for(int[] row : nextGenerationAI)
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
    
    private int calculateShowDifference()
    {
        int totalPlayerCells = 0;
        int totalComputerCells = 0;
        
        System.out.println(nextGenerationAI.length * nextGenerationAI[0].length);
        for(int[] row : nextGenerationAI)
        {
            for(int i : row)
            {
                System.out.print(i + " ");
                if(i == 1)
                    totalPlayerCells++;
                else if(i == 2)
                    totalComputerCells++;
            }
            System.out.println();
        }
        System.out.println();
        
        System.out.println(totalComputerCells + "-" + totalPlayerCells + "=" + (totalComputerCells - totalPlayerCells));
        return (totalComputerCells - totalPlayerCells);
    }
    
    /**
     * An exact copy of the game's iterate method. This way the AI knows the
     * rules of Conway's Game of Life to make an educated guess on what to do.
     */
    private void iterate() 
    {
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
                    if(currentGenerationAI[i][j + 1] == 1)
                        ones++;
                    else if(currentGenerationAI[i][j + 1] == 2)
                        twos++;

                    //If the cell is not at the bottom of the grid.
                    if(i > 0)
                    {
                        //upper right
                        if(currentGenerationAI[i - 1][j + 1] == 1)
                            ones++;
                        else if(currentGenerationAI[i - 1][j + 1] == 2)
                            twos++;
                    }

                    //If the cell is not at the top of the grid.
                    if (i < length - 1)
                    {
                        //Lower right
                        if(currentGenerationAI[i + 1][j + 1] == 1)
                            ones++;
                        else if(currentGenerationAI[i + 1][j + 1] == 2)
                            twos++;
                    }
                }

                //If the cell is not at the left most side of the board.
                if (j > 0) 
                {
                    //left
                    if(currentGenerationAI[i][j - 1] == 1)
                        ones++;
                    else if(currentGenerationAI[i][j - 1] == 2)
                        twos++;

                    //If the cell is not at the bottom of the currentGenerationAI.
                    if (i > 0)
                    {
                        //upper left
                        if(currentGenerationAI[i - 1][j - 1] == 1)
                            ones++;
                        else if(currentGenerationAI[i - 1][j - 1] == 2)
                            twos++;
                    }

                    //If the cell is not at the top of the grid.
                    if (i < length - 1)
                    {
                        //lower left
                        if(currentGenerationAI[i + 1][j - 1] == 1)
                            ones++;
                        else if(currentGenerationAI[i + 1][j - 1] == 2)
                            twos++;
                    }
                }

                //If the cell is not at the bottom of the grid.
                if (i > 0)
                {
                    //up
                    if(currentGenerationAI[i - 1][j] == 1)
                        ones++;
                    else if(currentGenerationAI[i - 1][j] == 2)
                        twos++;
                }

                //If the cell is not at the top of the currentGenerationAI.
                if (i < length - 1)
                {
                    //down
                    if(currentGenerationAI[i + 1][j] == 1)
                        ones++;
                    else if(currentGenerationAI[i + 1][j] == 2)
                        twos++;
                }

                //Now go through and update the next generation.
                int sum = ones + twos;

                if(currentGenerationAI[i][j] != 0) //If the cell is not dead.
                {
                    //If the cell has less than 2 neighbor cells then it does from starvation.
                    //If it has more than 3 then it dies from overcrowding.
                    if (sum < 2 || sum > 3)
                        nextGenerationAI[i][j] = 0;
                    else
                        nextGenerationAI[i][j] = currentGenerationAI[i][j]; //Otherwise make both grids equal.
                }
                else //If the cell is dead.
                {
                    //The cell needs to have 3 neighbors to be reborn.
                    if(sum == 3)
                    {
                        //Whichever player has the most cells determines which
                        //side the newly born cell goes to.
                        if(ones > twos)
                            nextGenerationAI[i][j] = 1;
                        else
                            nextGenerationAI[i][j] = 2;
                    }
                    else //The cell died.
                        nextGenerationAI[i][j] = 0;
                }
            }
        }
    }
    
    public void makeMove()
    {
        //To allow the board to print while the waits happen, we can make this
        //another thread that can run on its own so we can see the board 
        //between moves.
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(5000);
                }
                catch(Exception ex){}
                
                cellChange.clear();
                
                calculateOptions();
                
                //To see all possible moves the AI can make.
                System.out.println("# of possible moves to play: " + cellChange.size());

                Object[] move = cellChange.poll();
                if(move != null)
                {
                    reset();
                    
                    if(move.length == 4)
                    {
                        System.out.println("Winning score: " + move[0]);
                        currentGenerationAI[((Point) move[1]).y][((Point) move[1]).x] = 2;
                        iterate();
                        currentGenerationAI[((Point) move[2]).y][((Point) move[2]).x] = 0;
                        iterate();
                        currentGenerationAI[((Point) move[3]).y][((Point) move[3]).x] = 0;
                        iterate();
                        calculateShowDifference();
                        
                        game.makeMove(((Point) move[1]).y, ((Point) move[1]).x, 2);
                        
                        try
                        {
                            Thread.sleep(2000);
                        }
                        catch(Exception ex){}
                        
                        game.makeMove(((Point) move[2]).y, ((Point) move[2]).x, 2);
                        
                        try
                        {
                            Thread.sleep(2000);
                        }
                        catch(Exception ex){}
                        
                        game.makeMove(((Point) move[3]).y, ((Point) move[3]).x, 2);
                    }
                    else
                    {
                        System.out.println("Winning score: " + move[0]);
                        currentGenerationAI[((Point) move[1]).y][((Point) move[1]).x] = 0;
                        iterate();
                        calculateShowDifference();
                        
                        game.makeMove(((Point) move[1]).y, ((Point) move[1]).x, 2);
                    }
                    
                    try
                    {
                        Thread.sleep(3000);
                    }
                    catch(Exception ex){}

                    game.applyMove(2);
                }
            }
        };
        
        thread.start();
    }
}
