/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.malone.edwards.gameoflife.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import main.utilities.ArgumentOutOfBoundsException;

import org.json.JSONArray;
import org.json.JSONObject;


@SuppressWarnings("serial")
/**
 * Original code from arynaq : http://stackoverflow.com/a/19972585
 * 
 * Changes by Cory Edwards:
 *      Added comments.
 *      Changed a little formating of the code.
 *      Added the nextGenGrid so that every iteration does not update until the very end.
 *      Added the size variable so it is easy to change the pixel size of the cells.
 *      Changed the way the grid is sized to make it an n x n.
 *      Added that the frame be full screen.
 *      Made the grid of cells be in the middle of the screen.
 *      Made the simulation restart after set number of iterations.
 *      Made it into a standalone servlet.
 * 
 * 
 * This is the original Conway's Game of Life.
 */
@WebServlet("/simulation")
public class ConwayGameOfLife extends HttpServlet {

    private int[][] grid;
    private int[][] nextGenGrid;
    private Random rnd = new Random();
    private int generationCounter;
    private int reset;
    private double probability;
    
    /**
     * This is so the server can instantiate the class.
     */
    public ConwayGameOfLife(){}

    /**
     * 
     * @param n size of the square grid (n x n)
     * @param reset Iteration number to reset simulation. Defaults to 50
     * @param prob The probability of having a cell be born when board is set up.
     */
    public ConwayGameOfLife(String n, String reset, String prob) throws ArgumentOutOfBoundsException, NumberFormatException
    {
        int m;
        if(n == null ? true : n.equals(""))
            m = 300;
        else
            m = Integer.parseInt(n);
        
        //Take the string for probability as an int and convert it to a % later.
        int tempProbability;
        if(prob == null ? true : prob.equals(""))
            tempProbability = 60;
        else
            tempProbability = Integer.parseInt(prob);
        
        probability = (float) tempProbability / 100;
        
        if(probability < 0 || probability > 1)
        {
            throw new ArgumentOutOfBoundsException(probability + " is out of range of [0, 100]");
        }
                   
        //Set the number of columns and rows the grid will have to be n x n.
        this.grid = new int[m][m];
        
        //This will hold the next generation until each iteration is done
        //to then update the grid.
        this.nextGenGrid = new int[m][m];
        
        if(reset == null ? false : !reset.equals(""))
            this.reset = Integer.parseInt(reset);
        else
            this.reset = 50;
        
        setupGrid();
    }
    
    private void setupGrid()
    {
        //Reset generation counter when a new simulation starts.
        generationCounter = 0;
        
        //Reset the grid to all 0 to have a random reset.
        for(int[] row : grid)
            Arrays.fill(row, 0);
        
        //Must do this for both arrays.
        for(int[] row : nextGenGrid)
            Arrays.fill(row, 0);
        
        for (int[] row : grid) 
        {
            for (int j = 0; j < row.length; j++)
            {
                //The probability of a cell having a chance to be born.
                if (rnd.nextDouble() >= (1- probability))
                    row[j] = rnd.nextInt(2); //random int from 0 - 1.
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException
    {
        try(PrintWriter out = response.getWriter())
        {
            ConwayGameOfLife game;
            if(request.getParameter("setup") != null)
            {
                HttpSession session = request.getSession();
                game = new ConwayGameOfLife("100", "30", "60");
                
                grid = game.grid;
                nextGenGrid = game.nextGenGrid;
                rnd = game.rnd;
                generationCounter = game.generationCounter;
                reset = game.reset;
                probability = game.probability;
                
                session.setAttribute("game", game);
                setupGrid();
            }
            else
            {
                game = (ConwayGameOfLife) request.getSession().getAttribute("game");
                
                grid = game.grid;
                nextGenGrid = game.nextGenGrid;
                rnd = game.rnd;
                generationCounter = game.generationCounter;
                reset = game.reset;
                probability = game.probability;
            }
            
            if(generationCounter >= reset)
                setupGrid();
            else
                generationCounter++;

            for (int i = 0; i < grid.length; i++) 
            {
                for (int j = 0; j < grid[i].length; j++) 
                {
                    applyRule(i, j);
                }
            }

            //The next generation has been calculated, so now move to it.
            changeGeneration();
            
            JSONObject currentGrid = new JSONObject();
            JSONArray rows = new JSONArray();
            
            for(int[] row : grid)
            {
                JSONArray col = new JSONArray();
                
                for(int i : row)
                {
                    col.put(i);
                }
                rows.put(row);
            }
            
            currentGrid.put("grid", rows);
            
            out.write(currentGrid.toString());
            out.flush();
            
            game.grid = grid;
            game.nextGenGrid = nextGenGrid;
            game.rnd = rnd;
            game.generationCounter = generationCounter;
            game.reset = reset;
            game.probability = probability;
            request.getSession().setAttribute("game", game);
        }
        catch(Exception ex){ex.printStackTrace();}
    }
    
    private void changeGeneration()
    {
        for (int i = 0; i < grid.length; i++) 
        {
            System.arraycopy(nextGenGrid[i], 0, grid[i], 0, grid[i].length);
        }
    }


    private void applyRule(int i, int j) 
    {
        //Get all of the current cells around the current one.
        int left = 0, right = 0, up = 0, down = 0;
        int dUpperLeft = 0, dUpperRight = 0, dLowerLeft = 0, dLowerRight = 0;

        //If the cell is not on the right most side of the board.
        if (j < grid.length - 1) 
        {
            right = grid[i][j + 1];
            
            //If the cell is not at the bottom of the grid.
            if(i > 0)
                dUpperRight = grid[i - 1][j + 1];
            
            //If the cell is not at the top of the grid.
            if (i < grid.length - 1)
                dLowerRight = grid[i + 1][j + 1];
        }

        //If the cell is not at the left most side of the board.
        if (j > 0) 
        {
            left = grid[i][j - 1];
            
            //If the cell is not at the bottom of the grid.
            if (i > 0)
                dUpperLeft = grid[i - 1][j - 1];
            
            //If the cell is not at the top of the grid.
            if (i < grid.length - 1)
                dLowerLeft = grid[i + 1][j - 1];
        }

        //If the cell is not at the bottom of the grid.
        if (i > 0)
            up = grid[i - 1][j];
        
        //If the cell is not at the top of the grid.
        if (i < grid.length - 1)
            down = grid[i + 1][j];

        //Now go through and update the next generation.
        int sum = left + right + up + down + dUpperLeft + dUpperRight
                + dLowerLeft
                + dLowerRight;

        if (grid[i][j] == 1) 
        {
            if (sum < 2 || sum > 3)
                nextGenGrid[i][j] = 0;
        }
        else 
        {
            if (sum == 3)
                nextGenGrid[i][j] = 1;
        }
    }
}
