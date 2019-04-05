/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.malone.edwards.gameoflife.utilities;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.malone.edwards.gameoflife.servlets.GameOfLifeAndDeathGame;
import edu.malone.edwards.gameoflife.utilities.ai.AverageAI;
import edu.malone.edwards.gameoflife.utilities.ai.DumbAI;
import edu.malone.edwards.gameoflife.utilities.ai.SmartAI;
import org.apache.commons.lang3.ArrayUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author cjedwards1
 */
@WebServlet("/smart_v_ais")
public class IndependentGame  extends HttpServlet
{
    public static ScheduledExecutorService independentExecutor = Executors.newScheduledThreadPool(1); 
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException
    {
        
        for(int i = 0; true; i++)
        {
            try
            {
                final int x = i;
                final Future handler = independentExecutor.submit(() ->
                {
                    try
                    {
                        System.out.println("Game " + (x + 1) + " started");
                        GameOfLifeAndDeathGame game = new GameOfLifeAndDeathGame("1", "60", "1");
                        game.setUpGrid();
                        game.smart = true;
                        while(game.sent == 0)
                        {
                            smartMakeMove(game, false);
        //                    System.out.println("Smart AI done");
                            smartMakeMove(game, true);
        //                    System.out.println("Smart AI done thinking");
                            dumbMakeMove(game);
        //                    System.out.println("Other AI done");
                        }
                        System.out.println("Game " + (x + 1) + " complete");
                    }
                    catch(Exception ex)
                    {
                        Exceptions.printStackTrace(ex);
                    }
                });

                independentExecutor.schedule(() ->
                {
                    handler.cancel(true);      
                }, 5, TimeUnit.MINUTES);
                System.out.println(handler.get());
            }
            catch(InterruptedException | ExecutionException ex)
            {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private void smartMakeMove(GameOfLifeAndDeathGame game, boolean think)
    {
        int[][] temp = new int[10][10];

        //Make every grid equal to the future grid so the next move can commense.
        for (int i = 0; i < 10; i++) 
        {
            System.arraycopy(game.currentGeneration[i], 0, temp[i], 0, temp[i].length);
        }

        if(think)
            SmartAI.getInstance(10).thinkMoveIndependent(temp);
        else
            SmartAI.getInstance(10).makeMoveIndependent(temp, game);
    }
    
    private void dumbMakeMove(GameOfLifeAndDeathGame game)
    {
        DumbAI dumb = new DumbAI();
        dumb.setGame(game);
        dumb.setGeneration(game.currentGeneration);
        dumb.makeMoveIndependent();
    }
    
    private void averageMakeMove(GameOfLifeAndDeathGame game)
    {
        AverageAI average = new AverageAI(10);
        average.setGame(game);
        average.setGeneration(game.currentGeneration.clone());
        average.makeMoveIndependent();
    }
}
