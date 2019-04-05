/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflifeanddeath;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Cory Edwards
 */
public class DumbAI {
    
    ArrayList<Point> currentCells = new ArrayList();
    ArrayList<Point> playerCells = new ArrayList();
    ArrayList<Point> openCells = new ArrayList();
    
    final GameOfLifeAndDeathGame game;
    
    private final Random rnd = new Random(); //Used to randomly set up the board.

    DumbAI(GameOfLifeAndDeathGame game)
    {
        this.game = game;
    }
    
    public void setGeneration(int[][] current)
    {
        currentCells.clear();
        playerCells.clear();
        openCells.clear();
        
        for(int i = 0; i < current.length; i++)
        {
            for(int j = 0; j < current[0].length; j++)
            {
                //i is row # or y.
                //j is column # or x.
                switch (current[i][j]) 
                {
                    case 0:
                        openCells.add(new Point(j, i));
                        break;
                    case 2:
                        currentCells.add(new Point(j, i));
                        break;
                    default:
                        playerCells.add(new Point(j, i));
                        break;
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
                
                double value = rnd.nextDouble();

                //Make each section equally likely.
                if(value >= 0.67) //Take a random player cell.
                {
                    //Go until there is only 1 left.
                    while(playerCells.size() > 1)
                    {
                        //50 50 shot of getting rid of a random cell.
                        if(rnd.nextBoolean())
                        {
                            //The bounds parameter for rnd is exclusive, so it will never
                            //actually get to playerCell's size.
                            playerCells.remove(rnd.nextInt(playerCells.size()));
                        }
                    }

                    game.makeMove(playerCells.get(0).y, playerCells.get(0).x, 2);
                }
                else if(value >= 0.34) //Take a random computer cell.
                {
                    //Go until there is only 1 left.
                    while(currentCells.size() > 1)
                    {
                        //50 50 shot of getting rid of a random cell.
                        if(rnd.nextBoolean())
                        {
                            //The bounds parameter for rnd is exclusive, so it will never
                            //actually get to playerCell's size.
                            currentCells.remove(rnd.nextInt(currentCells.size()));
                        }
                    }

                    game.makeMove(currentCells.get(0).y, currentCells.get(0).x, 2);
                }
                else //Add a random cell, and take 2 random ones away.
                {
                    if(currentCells.size() != 1)
                    {
                        //Go until there is only 1 left.
                        while(openCells.size() > 1)
                        {
                            //50 50 shot of getting rid of a random cell.
                            if(rnd.nextBoolean())
                            {
                                //The bounds parameter for rnd is exclusive, so it will never
                                //actually get to playerCell's size.
                                openCells.remove(rnd.nextInt(openCells.size()));
                            }
                        }

                        game.makeMove(openCells.get(0).y, openCells.get(0).x, 2);

                        try
                        {
                            Thread.sleep(2000);
                        }
                        catch(Exception ex){}

                        //Go until there is only 2 left.
                        while(currentCells.size() > 2)
                        {
                            //50 50 shot of getting rid of a random cell.
                            if(rnd.nextBoolean())
                            {
                                //The bounds parameter for rnd is exclusive, so it will never
                                //actually get to playerCell's size.
                                currentCells.remove(rnd.nextInt(currentCells.size()));
                            }
                        }

                        game.makeMove(currentCells.get(0).y, currentCells.get(0).x, 2);

                        try
                        {
                            Thread.sleep(2000);
                        }
                        catch(Exception ex){}

                        game.makeMove(currentCells.get(1).y, currentCells.get(1).x, 2);
                    }
                    else
                    {
                        value = rnd.nextDouble();
                        
                        //Make each section equally likely.
                        if(value >= 0.50) //Take a random player cell.
                        {
                            //Go until there is only 1 left.
                            while(playerCells.size() > 1)
                            {
                                //50 50 shot of getting rid of a random cell.
                                if(rnd.nextBoolean())
                                {
                                    //The bounds parameter for rnd is exclusive, so it will never
                                    //actually get to playerCell's size.
                                    playerCells.remove(rnd.nextInt(playerCells.size()));
                                }
                            }

                            game.makeMove(playerCells.get(0).y, playerCells.get(0).x, 2);
                        }
                        else //Take a random computer cell.
                        {
                            //Go until there is only 1 left.
                            while(currentCells.size() > 1)
                            {
                                //50 50 shot of getting rid of a random cell.
                                if(rnd.nextBoolean())
                                {
                                    //The bounds parameter for rnd is exclusive, so it will never
                                    //actually get to playerCell's size.
                                    currentCells.remove(rnd.nextInt(currentCells.size()));
                                }
                            }

                            game.makeMove(currentCells.get(0).y, currentCells.get(0).x, 2);
                        }
                    }
                }

                try
                {
                    Thread.sleep(3000);
                }
                catch(Exception ex){}

                game.applyMove(2);
            }
        };
                
        thread.start();
    }
}
