/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.servlets;

import java.io.ByteArrayOutputStream;
import main.utilities.ai.SmartAI;
import main.utilities.ai.AverageAI;
import main.utilities.ai.DumbAI;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.AsyncContext;
import main.utilities.ArgumentOutOfBoundsException;
import main.utilities.GetData;
import org.apache.commons.net.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Cory Edwards
 * 
 * The java rendition of Game of Life and Death. 
 * Original game design came from carykh on youtube: https://www.youtube.com/watch?v=JkGZ2Hl1l8c&t=18s
 * Java implementation done by Cory Edwards.
 */
//@WebServlet(asyncSupported = true, value = "/GOLADGame")
public class GameOfLifeAndDeathGame implements Serializable{
    
    
    //The various grids to hold past, current, and future generation.
    public int[][] oldGeneration; //past
    public int[][] currentGeneration; //current
    public int[][] nextGeneration; //future
    
    //Player settings
    public int player = 1; //1 for player 1's turn and 2 for player 2's turn.
    public int player2Type; //Whether player 2 is a human or 1 of the AI.
    
    //Variables delt with sending data to the server.
    int cellDifference = 0;
    int numOfMoves = 0;
    
    //To let a player add tiles rather than taking them away.
    public boolean taking = false; //Turns true if a tile is placed on the board.
    public int taken = 0; //Needs to be 2 to submit a move. # of players tiles taken to satisfy putting one down.
    
    public int length; //The n in the constructor. The board will be length X length big.
    
    public double probability; //The probability used to determine if a cell will be born or not while board is set up.
    
    public String user; //Used to keep track of the user at a given time.
    
    public int sent = 0; //To fix a bug where data gets sent to the server twice.
    
    public boolean training; //If the smart AI is training then this will be true.
    
    public boolean smart = false;
    
    /**
     * This is so the server can instantiate the class.
     */
    public GameOfLifeAndDeathGame(){}
    
    //Constructor
    public GameOfLifeAndDeathGame(String n, String prob, String player2Type) throws ArgumentOutOfBoundsException, NumberFormatException
    {
        //Take the incoming string if it is not empty and convert it to a number.
        int m;
        if(n == null ? true : n.equals(""))
            m = 1;
        else
            m = Integer.parseInt(n);
        
        //Take the string for probability as an int and convert it to a % later.
        int tempProbability;
        if(prob == null ? true : prob.equals(""))
            tempProbability = 60;
        else
            tempProbability = Integer.parseInt(prob);
        
        //Take the player 2 type and set it.
        if(player2Type == null ? true : player2Type.equals(""))
            this.player2Type = 0;
        else
            this.player2Type = Integer.parseInt(player2Type);
        
        probability = (float) tempProbability / 100;
        
        if(probability < 0 || probability > 1)
        {
            throw new ArgumentOutOfBoundsException(probability + " is out of range of [0, 100]");
        }
        
        if(m < 1 || m > 10)
        {
            throw new ArgumentOutOfBoundsException(m + " is out of range of [1, 10]");
        }
        
        //We wont to convert the 1 digit size to a 2 digit. EX(1 x 1 --> 10 x 10)
        m = m * 10;
        
        //We want the grids to be n X n.
        oldGeneration = new int[m][m];
        nextGeneration = new int[m][m];
        currentGeneration = new int[m][m];
        
        //Set the size for the rest of the class.
        length = m;
    }
    
    
    
    /**
     * Is used when the game starts to setup the board.
     */
    public void setUpGrid()
    {   
        //Only put values in for the first half. We will then calc the rest
        //of the board to make it symmetrical and fair.
        int[][] tempArray = new int[length / 2][length];
        
        //Randomly assign the cells.
        for (int i = 0; i < length / 2; i++) 
        {
            for (int j = 0; j < length; j++)
            {
                Random rnd = new Random();
                //Probability of having a square born.
                if (rnd.nextDouble() >= (1 - probability))
                    tempArray[i][j] = rnd.nextInt(2) + 1; //random int from 0 - 1.
            }
        }
        
        //Add the first half of the rows.
        for(int i = 0; i < tempArray.length; i++)
        {
            System.arraycopy(tempArray[i], 0, oldGeneration[i], 0, oldGeneration[i].length);
            System.arraycopy(tempArray[i], 0, currentGeneration[i], 0, currentGeneration[i].length);
            System.arraycopy(tempArray[i], 0, nextGeneration[i], 0, nextGeneration[i].length);
        }
        
        //Transpose the array then add the new array for the rest of the rows.
        int[][] flippedArray = transposeArray(tempArray);
        
        //Add the second half of rows.
        for(int i = 0; i < flippedArray.length; i++)
        {
            System.arraycopy(flippedArray[i], 0, oldGeneration[i + flippedArray.length], 0, oldGeneration[i].length);
            System.arraycopy(flippedArray[i], 0, currentGeneration[i + flippedArray.length], 0, currentGeneration[i].length);
            System.arraycopy(flippedArray[i], 0, nextGeneration[i + flippedArray.length], 0, nextGeneration[i].length);
        }
        
        //A new game has started, so reset to send new data to server.
        sent = 0;
        
        //Reseting other variables.
        cellDifference = 0;
        numOfMoves = 0;
        player = 1;
        
        //Show what the next iteration will be.
        checkBoard();
        
    }
    
    /**
     * Take an array and transpose it as if it was a matrix. EX(element (0, 2) will become (2, 0)).
     * At the same time it will reverse the 1's and 2's and leave the 0's alone. 
     * This will give symmetry to the board and make it even for each player.
     * 
     * @param tempArray The array to transpose.
     * @return The transposed array.
     */
    public int[][] transposeArray(int[][] tempArray)
    {
        int numRows = tempArray.length;
        int numColumns = tempArray[0].length;
        
        int[][] transposedArray = new int[numRows][numColumns];
        
        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < numColumns / 2; j++)
            {
                int temp = tempArray[i][j];
                
                if(temp == 1)
                    temp = 2;
                else if(temp == 2)
                    temp = 1;
                
                int temp2 = tempArray[i][numColumns - j - 1];
                
                if(temp2 == 1)
                    temp2 = 2;
                else if(temp2 == 2)
                    temp2 = 1;
                
                transposedArray[i][j] = temp2;
                transposedArray[i][numColumns - j - 1] = temp;
            }
        }
        
        for(int i = 0; i < numRows / 2; i++)
        {
            int[] temp = transposedArray[i];
            transposedArray[i] = transposedArray[numRows - i - 1];
            transposedArray[numRows - i - 1] = temp;
        }
        
        return transposedArray;
    }
    
    /**
     * Called every time the game needs to update the board.
     */
    public void paintButtons(PrintWriter out) throws JSONException
    {
        JSONObject currentGrid = new JSONObject();
        JSONArray rows = new JSONArray();
            
        //Go over each button.
        for (int i = 0; i < length; i++) 
        {
            JSONArray col = new JSONArray();
            for (int j = 0; j < length; j++) 
            {
                JSONObject colors = new JSONObject();
                
                //If these 2 are not the same, then that is the button the user changed as their move.
                if(oldGeneration[i][j] == currentGeneration[i][j])
                {
                    //Paint the buttons accordingly
                    if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 2)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Blue");
                    }
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 0)
                    {
                        colors.put("out", "Blue");
                        colors.put("in", "Original");
                    }
                    else if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 0)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Original");
                    }
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 2)
                    {
                        colors.put("out", "Blue");
                        colors.put("in", "Blue");
                    }
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 2)
                    {
                        colors.put("out", "Red");
                        colors.put("in", "Blue");
                    }
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 1)
                    {
                        colors.put("out", "Blue");
                        colors.put("in", "Red");
                    }
                    else if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 1)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Red");
                    }
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 0)
                    {
                        colors.put("out", "Red");
                        colors.put("in", "Original");
                    }
                    else if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 0)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Original");
                    }
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 1)
                    {
                        colors.put("out", "Red");
                        colors.put("in", "Red");
                    }
                }
                else
                {
                    if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 2)
                    {
                        colors.put("out", "LightBlue");
                        colors.put("in", "Blue");
                    }
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 0)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Original");
                    }
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 1)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Red");
                    }
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 2)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Blue");
                    }
                    else if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 1)
                    {
                        colors.put("out", "LightRed");
                        colors.put("in", "Red");
                    }
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 0)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Original");
                    }
                    else if(oldGeneration[i][j] == 0 && currentGeneration[i][j] == 1 && nextGeneration[i][j] == 0)
                    {
                        colors.put("out", "LightRed");
                        colors.put("in", "Original");
                    }
                    else if(oldGeneration[i][j] == 0 && currentGeneration[i][j] == 2 && nextGeneration[i][j] == 0)
                    {
                        colors.put("out", "LightBlue");
                        colors.put("in", "Original");
                    }
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 1)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Red");
                    }
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 2)
                    {
                        colors.put("out", "Original");
                        colors.put("in", "Blue");
                    }
                }
                col.put(colors);
            }
            rows.put(col);
        }
        currentGrid.put("grid", rows);
        currentGrid.put("player", player);
        out.write(currentGrid.toString());
        out.flush();
    }
    
    public void saveGame(HttpServletResponse response)
    {
        try(ByteArrayOutputStream bo = new ByteArrayOutputStream())
        {
            try(ObjectOutputStream os = new ObjectOutputStream(bo))
            {
                try(PrintWriter outStream = response.getWriter())
                {
                    os.writeObject(this);

                    os.flush();
                    outStream.write(Base64.encodeBase64URLSafeString(bo.toByteArray()));
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        catch(IOException ex)
        {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void aiTurn(int moveByPlayer, AsyncContext context)
    {
        try
        {
            context.setTimeout(300000);
            
            int newPlayer1Num = 0;
            int newPlayer2Num = 0;
            for(int[] row : oldGeneration)
            {
                for(int num : row)
                {
                    if(num == 1)
                        newPlayer1Num++;
                    else if(num == 2)
                        newPlayer2Num++;
                }
            }
            
            if(newPlayer1Num != 0 && newPlayer2Num != 0)
            {
                switch (player2Type) 
                {
                    case 1:
                        DumbAI dumb = new DumbAI();
                        dumb.setGame(this);
                        dumb.setGeneration(currentGeneration);
                        dumb.makeMove(context);
                        break;
                    case 2:
                        AverageAI average = new AverageAI(length);
                        average.setGame(this);
                        average.setGeneration(currentGeneration.clone());
                        average.makeMove(context);
                        break;
                    case 3:
                        int[][] temp = new int[length][length];

                        //Make every grid equal to the future grid so the next move can commense.
                        for (int i = 0; i < length; i++) 
                        {
                            System.arraycopy(currentGeneration[i], 0, temp[i], 0, temp[i].length);
                        }

                        SmartAI.getInstance(length).makeMove(temp, this, context);
                        break;
                    default:
                        break;
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * When the user clicks anywhere on the board.
     * @param row The row they clicked.
     * @param column The column they clicked.
     */
    public void makeMove(int row, int column, int moveByPlayer) 
    {
        //Check whether the board has changed at all to determine if the 
        //player was allowed to move.
        boolean same = true;
        for(int i = 0; i < length; i++)
            same = same ? Arrays.equals(oldGeneration[i], currentGeneration[i]) : false;
        
        if(moveByPlayer == player)
        {
            //Board is the same so the player was allowed to make the move.
            if(same)
            {
                //If the old location is 0 then the user wants to add a cell.
                if(oldGeneration[row][column] == 0)
                {
                    //They are adding a cell so set [taking] true.
                    taking = true;
                    if(player == 1)
                        currentGeneration[row][column] = 1;
                    else
                        currentGeneration[row][column] = 2;
                }
                else //Else the player wanted to get rid of a cell.
                    currentGeneration[row][column] = 0;
            }
            else //Else the player may not be able to make the move.
            {
                //If taking is set true then see if the player got rid of the correct cell.
                if(taking)
                {
                    if(player == 1 && currentGeneration[row][column] == 1)
                    {
                        //Make sure the player cannot accidentally get rid of more than 2 cells.
                        if(taken < 2)
                        {
                            taken++;
                            currentGeneration[row][column] = 0;
                        }
                    }
                    else if(player == 2 && currentGeneration[row][column] == 2)
                    {
                        if(taken < 2)
                        {
                            taken++;
                            currentGeneration[row][column] = 0;
                        }
                    }
                }
            }

            //Set up for the next iteration.
            checkBoard();
        }
    }
    
    /**
     * If the "submit move" button was pressed.
     */
    public void applyMove(int moveByPlayer)
    {
        try
        {
            if(moveByPlayer == player)
            {
                //Again check to see if the old and current grid are the same or not.
                boolean same = true;
                for(int i = 0; i < length; i++)
                    same = same ? Arrays.equals(oldGeneration[i], currentGeneration[i]) : false;

                //If they are the same then a valid move was not played so skip, or
                //if the player placed a cell and did not get rid of 2 others already.
                if(!same && ((taking && taken == 2) || (!taking && taken == 0)))
                {
                    numOfMoves++;

                    //Count the number of cells each player has.
                    int player1Num = 0;
                    int player2Num = 0;
                    for(int[] row : oldGeneration)
                    {
                        for(int num : row)
                        {
                            if(num == 1)
                                player1Num++;
                            else if(num == 2)
                                player2Num++;
                        }
                    }

                    cellDifference += (player2Num - player1Num);

                    //The move is legit so reset taking and taken for the next player.
                    taking = false;
                    taken = 0;

                    //Make every grid equal to the future grid so the next move can commense.
                    for (int i = 0; i < length; i++) 
                    {
                        System.arraycopy(nextGeneration[i], 0, oldGeneration[i], 0, oldGeneration[i].length);
                        System.arraycopy(nextGeneration[i], 0, currentGeneration[i], 0, currentGeneration[i].length);
                    }

                    checkBoard();
                    
                    int newPlayer1Num = 0;
                    int newPlayer2Num = 0;
                    for(int[] row : oldGeneration)
                    {
                        for(int num : row)
                        {
                            if(num == 1)
                                newPlayer1Num++;
                             else if(num == 2)
                                newPlayer2Num++;
                        }
                    }

                    //Give the other player the turn.
                    if(player == 1)
                        player = 2;
                    else
                    {
                        player = 1;
                        if(newPlayer2Num != 0 && newPlayer1Num != 0)
                        {
                            if(player2Type == 3)
                            {
                                int[][] temp = new int[length][length];

                                //Make every grid equal to the future grid so the next move can commense.
                                for (int i = 0; i < length; i++) 
                                {
                                    System.arraycopy(currentGeneration[i], 0, temp[i], 0, temp[i].length);
                                }

                                SmartAI.getInstance(length).thinkMove(temp, this);
                            }
                        }
                    }
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void sendDataToServer(int finalTotal, String userName, int result)
    {
        new GetData().giveData(Integer.toString(player2Type), Float.toString((float)((float) cellDifference / (float) numOfMoves)), finalTotal, userName, training, result, smart);
    }
    
    /**
     * Called when the board needs to be changed.
     * Will iterate to the next generation and repaint the buttons.
     * It will then go through and see how many cells player 1 & 2 has to see if the game is over or not,
     *      and determines who won, lost, or tied.
     */
    public void checkBoard()
    {
        //Go to the next generation.
        iterate();
        
        //Count the number of cells each player has.
        int player1Num = 0;
        int player2Num = 0;
        for(int[] row : oldGeneration)
        {
            for(int num : row)
            {
                if(num == 1)
                    player1Num++;
                else if(num == 2)
                    player2Num++;
            }
        }
        
        if(sent == 0)
        {
            //Check if the game is over or not.
            if(player1Num == 0 && player2Num == 0) //Tie
            {
                sendDataToServer(player2Type == 0 ? (player1Num > player2Num ? player1Num : player2Num) : player1Num, user, 3);
                sent++;
            }
            else if(player1Num == 0) // player 2 won
            {
                sendDataToServer(player2Type == 0 ? (player1Num > player2Num ? player1Num : player2Num) : player1Num, user, 2);
                sent++;
            }
            else if(player2Num == 0) // player 1 won
            {
                sendDataToServer(player2Type == 0 ? (player1Num > player2Num ? player1Num : player2Num) : player1Num, user, 1);
                sent++;
            }
        }
            
    }
    
    /**
     * If the "undo move" was clicked.
     */
    public void undoMove(int moveByPlayer)
    {
        if(moveByPlayer == player)
        {
            //Reset all the variables if a new player was going to play.
            taken = 0;
            taking = false;

            //Set current and future grids to the past grid.
            for (int i = 0; i < length; i++) 
            {
                System.arraycopy(oldGeneration[i], 0, nextGeneration[i], 0, nextGeneration[i].length);
                System.arraycopy(oldGeneration[i], 0, currentGeneration[i], 0, currentGeneration[i].length);
            }

            checkBoard();
        }
    }

    /**
     * Called when the game needs to go to the next generation.
     * Implements Conway's Game of Life rules.
     */
    public void iterate() 
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
                    if(currentGeneration[i][j + 1] == 1)
                        ones++;
                    else if(currentGeneration[i][j + 1] == 2)
                        twos++;

                    //If the cell is not at the bottom of the grid.
                    if(i > 0)
                    {
                        //upper right
                        if(currentGeneration[i - 1][j + 1] == 1)
                            ones++;
                        else if(currentGeneration[i - 1][j + 1] == 2)
                            twos++;
                    }

                    //If the cell is not at the top of the grid.
                    if (i < length - 1)
                    {
                        //Lower right
                        if(currentGeneration[i + 1][j + 1] == 1)
                            ones++;
                        else if(currentGeneration[i + 1][j + 1] == 2)
                            twos++;
                    }
                }

                //If the cell is not at the left most side of the board.
                if (j > 0) 
                {
                    //left
                    if(currentGeneration[i][j - 1] == 1)
                        ones++;
                    else if(currentGeneration[i][j - 1] == 2)
                        twos++;

                    //If the cell is not at the bottom of the currentGeneration.
                    if (i > 0)
                    {
                        //upper left
                        if(currentGeneration[i - 1][j - 1] == 1)
                            ones++;
                        else if(currentGeneration[i - 1][j - 1] == 2)
                            twos++;
                    }

                    //If the cell is not at the top of the grid.
                    if (i < length - 1)
                    {
                        //lower left
                        if(currentGeneration[i + 1][j - 1] == 1)
                            ones++;
                        else if(currentGeneration[i + 1][j - 1] == 2)
                            twos++;
                    }
                }

                //If the cell is not at the bottom of the grid.
                if (i > 0)
                {
                    //up
                    if(currentGeneration[i - 1][j] == 1)
                        ones++;
                    else if(currentGeneration[i - 1][j] == 2)
                        twos++;
                }

                //If the cell is not at the top of the currentGeneration.
                if (i < length - 1)
                {
                    //down
                    if(currentGeneration[i + 1][j] == 1)
                        ones++;
                    else if(currentGeneration[i + 1][j] == 2)
                        twos++;
                }

                //Now go through and update the next generation.
                int sum = ones + twos;

                if(currentGeneration[i][j] != 0) //If the cell is not dead.
                {
                    //If the cell has less than 2 neighbor cells then it does from starvation.
                    //If it has more than 3 then it dies from overcrowding.
                    if (sum < 2 || sum > 3)
                        nextGeneration[i][j] = 0;
                    else
                        nextGeneration[i][j] = currentGeneration[i][j]; //Otherwise make both grids equal.
                }
                else //If the cell is dead.
                {
                    //The cell needs to have 3 neighbors to be reborn.
                    if(sum == 3)
                    {
                        //Whichever player has the most cells determines which
                        //side the newly born cell goes to.
                        if(ones > twos)
                            nextGeneration[i][j] = 1;
                        else
                            nextGeneration[i][j] = 2;
                    }
                    else //The cell died.
                        nextGeneration[i][j] = 0;
                }
                
                //To debug what the past, current, and future value of the cell is.
                //System.out.println("(" + i + ", " + j + "): oldGeneration: " + oldGeneration[i][j] + " pre: " + currentGeneration[i][j] + " next: " + nextGeneration[i][j]);
            }
        }
    }
}
