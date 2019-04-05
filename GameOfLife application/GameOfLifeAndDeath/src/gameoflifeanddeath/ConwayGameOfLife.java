/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflifeanddeath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.beans.Transient;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;


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
 *      Made it into a standalone class.
 * 
 * 
 * This is the original Conway's Game of Life.
 */
public class ConwayGameOfLife extends JPanel {

    private final int[][] grid;
    private final int[][] nextGenGrid;
    private final Random rnd = new Random();
    private int generationCounter;
    private final int reset;
    private final int size;
    private final int timer;
    private final JFrame frame;
    private final double probability;

    /**
     * 
     * @param n size of the square grid (n x n)
     * @param size Size of cells in pixels. Defaults to screen size / n.
     * @param reset Iteration number to reset simulation. Defaults to 50
     * @param prob The probability of having a cell be born when board is set up.
     * @param timer The time in milliseconds between iterations. Defaults to 500.
     */
    public ConwayGameOfLife(String n, String reset, String size, String prob, String timer) throws ArgumentOutOfBoundsException, NumberFormatException
    {
        frame = new JFrame("Conway's game of life");
        
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
            JOptionPane.showMessageDialog(frame,
                "The percentage should be between 0% and 100%.",
                "Percentage error",
                JOptionPane.ERROR_MESSAGE);
            throw new ArgumentOutOfBoundsException(probability + " is out of range of [0, 100]");
        }
                    
        //Size is the size in pixels of each cell
        if(size == null ? true : size.equals(""))
        {
            //Get the height and width of the default screen for the computer.
            GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            double screenWidth = screen.getDisplayMode().getWidth();
            double screenHeight = screen.getDisplayMode().getHeight();

            this.size = screenWidth < screenHeight ? (int) screenWidth / m : (int) screenHeight / m;
        }
        else
            this.size = Integer.parseInt(size);
        
        //Set the number of columns and rows the grid will have to be n x n.
        this.grid = new int[m][m];
        
        //This will hold the next generation until each iteration is done
        //to then update the grid.
        this.nextGenGrid = new int[m][m];
        
        if(reset == null ? false : !reset.equals(""))
            this.reset = Integer.parseInt(reset);
        else
            this.reset = 50;
        
        if(timer == null ? false : !timer.equals(""))
            this.timer = Integer.parseInt(timer);
        else
            this.timer = 500;
        
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

    public void updateGrid() 
    {
        if(generationCounter >= reset + 1)
            setupGrid();
        
        for (int i = 0; i < grid.length; i++) 
        {
            for (int j = 0; j < grid[i].length; j++) 
            {
                applyRule(i, j);
            }
        }
        
        //The next generation has been calculated, so now move to it.
        changeGeneration();
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

    @Override
    @Transient
    //Make the starting size of the frame the size of the grid.
    public Dimension getPreferredSize() 
    {
        return new Dimension(grid.length * size, grid[0].length * size);
    }

    @Override
    //Every iteration gets repainted.
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Color gColor = g.getColor();

        g.drawString("Generation: " + generationCounter++, 0, 10);
        for (int i = 0; i < grid.length; i++) 
        {
            for (int j = 0; j < grid[i].length; j++) 
            {
                if (grid[i][j] == 1) 
                {
                    //Set the color of the live cells.
                    g.setColor(Color.red);
                    
                    //Find where to put the center of the grid.
                    int middleOfSquare = (size * grid.length) / 2;
                    int midWidth = (int) (frame.getContentPane().getWidth() / 2) - middleOfSquare;
                    int midHeight = (int) (frame.getContentPane().getHeight() / 2) - middleOfSquare;
                    
                    //Draw a rectangle (x position, y position, width, height).
                    //x = [column # of cell] * [width of cells]
                    //y = [row # of cell] * [height of cells]
                    g.fillRect((j * size) + midWidth, (i * size) + midHeight, size, size);
                }
            }
        }

        g.setColor(gColor);
    }

    public void start() 
    {
        frame.getContentPane().add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setVisible(true);
        
        //recalculate and repaint the grid every 500 milliseconds.
        new Timer(timer, (ActionEvent e) -> 
        {
            updateGrid();
            repaint();
        }).start();
    }
}
