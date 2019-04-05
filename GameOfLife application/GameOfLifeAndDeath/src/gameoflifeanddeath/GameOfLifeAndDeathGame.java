/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflifeanddeath;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author Cory Edwards
 * 
 * The java rendition of Game of Life and Death. 
 * Original game design came from carykh on youtube: https://www.youtube.com/watch?v=JkGZ2Hl1l8c&t=18s
 * Java implementation done by Cory Edwards.
 */
public class GameOfLifeAndDeathGame extends JPanel{
    
    private JFrame frame = new JFrame("Game of Life and Death"); //The frame holding the game. Will be called to close if the game closes and a player clicks no.
    
    //The various grids to hold past, current, and future generation.
    public final int[][] oldGeneration; //past
    public final int[][] currentGeneration; //current
    public final int[][] nextGeneration; //future
    
    private final Random rnd = new Random(); //Used to randomly set up the board.
    
    private final ArrayList<ButtonPanel> list = new ArrayList<>(); //List of all button panels in order.
    
    //Player settings
    public int player = 1; //1 for player 1's turn and 2 for player 2's turn.
    public JLabel player2CellsLabel = new JLabel(); //Used to show player 1 how many cells he has.
    public JLabel player1CellsLabel = new JLabel(); //Used to show player 2 how many cells he has.
    public JLabel player1Label = new JLabel("Player 1 ...."); //To show it's player 1's square
    public JLabel player2Label = new JLabel("Player 2 ...."); //To show it's player 2's square.
    public int player2Type; //Whether player 2 is a human or 1 of the AI.
    
    //Buttons made global so to make sure their text is always the correct size.
    JButton undo;
    JButton submit;
    
    //Variables delt with sending data to the server.
    boolean sending = true;
    int cellDifference = 0;
    int numOfMoves = 0;
    
    //AIs
    DumbAI dumb;
    AverageAI average;
    
    //Used to show who's turn it is.
    public int wait = 0; //Cycles from 0 - 4. Will be the # of "." that show up.
    public Timer waiting  = new Timer(200, (ActionEvent e) -> //The timer that shows which player's turn it is.
    {
        //The string that will show up for the player.
        String waitString = "";

        //How many "."'s to have in it?
        switch(wait)
        {
            case 1:
                waitString = ".";
                break;
            case 2:
                waitString = "..";
                break;
            case 3:
                waitString = "...";
                break;
            case 4:
                waitString = "....";
                break;
            default:
        }

        if(player == 1)
        {
            player1Label.setText("Player 1 " + waitString);
            player2Label.setText("Player 2");
        }
        else
        {
            player1Label.setText("Player 1");
            player2Label.setText("Player 2 " + waitString);
        }

        //If [wait] is 3 then reset, otherwise add 1.
        wait = wait == 4 ? 0 : wait + 1;
    });
    
    //To let a player add tiles rather than taking them away.
    public boolean taking = false; //Turns true if a tile is placed on the board.
    public int taken = 0; //Needs to be 2 to submit a move. # of players tiles taken to satisfy putting one down.
    
    private final int length; //The n in the constructor. The board will be length X length big.
    
    private final double probability; //The probability used to determine if a cell will be born or not while board is set up.
    
    //Constructor
    public GameOfLifeAndDeathGame(String n, String prob, String player2Type, String sendData) throws ArgumentOutOfBoundsException, NumberFormatException
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
        
        if(sendData.equals("1"))
        {
            JOptionPane.showMessageDialog(frame,
                "You have selected to not send data to the server when the game finishes.",
                "Sending data",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        if(m != 1 || tempProbability != 60)
        {
            sending = false;
            
            JOptionPane.showMessageDialog(frame,
                "Since the size of the grid is not 1 and probability of a cell being born\n"
                    + "when the board is made is not 60, no data will be sent of this game.",
                "Sending data",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        //Take the player 2 type and set it.
        if(player2Type == null ? true : player2Type.equals(""))
            this.player2Type = 0;
        else
            this.player2Type = Integer.parseInt(player2Type);
        
        probability = (float) tempProbability / 100;
        
        if(probability < 0 || probability > 1)
        {
            JOptionPane.showMessageDialog(frame,
                "The percentage should be between 0% and 100%.",
                "Percentage error",
                JOptionPane.ERROR_MESSAGE);
            throw new ArgumentOutOfBoundsException(probability + " is out of range of [0, 100]");
        }
        
        if(m < 1 || m > 10)
        {
            JOptionPane.showMessageDialog(frame,
                "The board size should be between 1 and 10.",
                "Board size error",
                JOptionPane.ERROR_MESSAGE);
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
    private void setUpGrid()
    {   
        //Only put values in for the first half. We will then calc the rest
        //of the board to make it symmetrical and fair.
        int[][] tempArray = new int[length / 2][length];
        
        //Randomly assign the cells.
        for (int i = 0; i < length / 2; i++) 
        {
            for (int j = 0; j < length; j++)
            {
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
    private int[][] transposeArray(int[][] tempArray)
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
     * Get the button at the specified row and column.
     * @param r The row to get the button (starts at 0).
     * @param c The column to get the button (starts at 0).
     * @return The ButtonPanel at that location.
     */
    private ButtonPanel getGridButton(int r, int c)
    {
        int index = r * length + c;
        return list.get(index);
    }
    
    /**
     * Called every time the game needs to update the board.
     */
    private void paintButtons()
    {
        //Go over each button.
        for (int i = 0; i < length; i++) 
        {
            for (int j = 0; j < length; j++) 
            {
                //Get the current button.
                ButtonPanel btnPanel = getGridButton(i, j);
                
                //If these 2 are not the same, then that is the button the user changed as their move.
                if(oldGeneration[i][j] == currentGeneration[i][j])
                {
                    //Paint the buttons accordingly
                    if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 2)
                        btnPanel.setColor(btnPanel.original, btnPanel.player2);
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 0)
                        btnPanel.setColor(btnPanel.player2, btnPanel.original);
                    else if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 0)
                        btnPanel.setColor(btnPanel.original, btnPanel.original);
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 2)
                        btnPanel.setColor(btnPanel.player2, btnPanel.player2);
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 2)
                        btnPanel.setColor(btnPanel.player1, btnPanel.player2);
                    else if(oldGeneration[i][j] == 2 && nextGeneration[i][j] == 1)
                        btnPanel.setColor(btnPanel.player2, btnPanel.player1);
                    else if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 1)
                        btnPanel.setColor(btnPanel.original, btnPanel.player1);
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 0)
                        btnPanel.setColor(btnPanel.player1, btnPanel.original);
                    else if(oldGeneration[i][j] == 0 && nextGeneration[i][j] == 0)
                        btnPanel.setColor(btnPanel.original, btnPanel.original);
                    else if(oldGeneration[i][j] == 1 && nextGeneration[i][j] == 1)
                        btnPanel.setColor(btnPanel.player1, btnPanel.player1);
                }
                else
                {
                    if(currentGeneration[i][j] == 0 && nextGeneration[i][j] == 2)
                        btnPanel.setColor(btnPanel.original, btnPanel.player2);
                    else if(currentGeneration[i][j] == 2 && nextGeneration[i][j] == 0)
                        btnPanel.setColor(btnPanel.player2Pre, btnPanel.original);
                    else if(currentGeneration[i][j] == 0 && nextGeneration[i][j] == 1)
                        btnPanel.setColor(btnPanel.original, btnPanel.player1);
                    else if(currentGeneration[i][j] == 1 && nextGeneration[i][j] == 0)
                        btnPanel.setColor(btnPanel.player1Pre, btnPanel.original);
                    else if(currentGeneration[i][j] == 0 && nextGeneration[i][j] == 0)
                        btnPanel.setColor(btnPanel.original, new Color(btnPanel.original.getRed(), 
                                btnPanel.original.getGreen(), btnPanel.original.getBlue(), 255));
                    else if(currentGeneration[i][j] == 1 && nextGeneration[i][j] == 1)
                        btnPanel.setColor(btnPanel.player1Pre, null);
                    else if(currentGeneration[i][j] == 2 && nextGeneration[i][j] == 2)
                        btnPanel.setColor(btnPanel.player2Pre, null);
                }
            }
        }
        //Repaint the board just to make sure everything is set.
        repaint();
    }
    
    @Override
    //Every time the frame changes in any way, it gets repainted.
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        
        //Sinse the frame is changing in some way, make sure all text is the 
        //correct size.
        setFontSize(player1Label);
        setFontSize(player1CellsLabel);
        setFontSize(player2CellsLabel);
        setFontSize(player2Label);
        setFontSize(submit);
        setFontSize(undo);
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
        
        ButtonPanel button = getGridButton(row, column);
        
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
                    {
                        currentGeneration[row][column] = 1;
                        button.setBorder(new LineBorder(button.player1));
                    }
                    else
                    {
                        currentGeneration[row][column] = 2;
                        button.setBorder(new LineBorder(button.player2));
                    }
                }
                else //Else the player wanted to get rid of a cell.
                {
                    currentGeneration[row][column] = 0;
                    button.setBorder(new LineBorder(Color.BLACK));
                    button.setColor(button.original, button.original);
                }
            }
            else //Else the player may not be able to make the move.
            {
                //If taking is set true then see if the player got rid of the correct cell.
                if(taking)
                {
                    if(player == 1 && button.getBackground() == button.player1)
                    {
                        //Make sure the player cannot accidentally get rid of more than 2 cells.
                        if(taken < 2)
                        {
                            taken++;
                            currentGeneration[row][column] = 0;
                            button.setBorder(new LineBorder(Color.BLACK));
                            button.setColor(button.original, button.original);
                        }
                    }
                    else if(player == 2 && button.getBackground() == button.player2)
                    {
                        if(taken < 2)
                        {
                            taken++;
                            currentGeneration[row][column] = 0;
                            button.setBorder(new LineBorder(Color.BLACK));
                            button.setColor(button.original, button.original);
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
        if(moveByPlayer == player)
        {
            //Reset the wait to 0 just in case it was not at this point in time.
            wait = 0;

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

                    //Just in case there was a cell added, turn every boarder to black.
                    for(int j = 0; j < length; j++)
                        getGridButton(i, j).setBorder(new LineBorder(Color.BLACK));
                }

                checkBoard();

                //Give the other player the turn.
                if(player == 1)
                {
                    player = 2;

                    if(frame != null ? frame.isShowing() : false)
                    {
                        if(player2Type == 1)
                        {
                            dumb.setGeneration(currentGeneration);
                            dumb.makeMove();
                        }
                        else if(player2Type == 2)
                        {
                            average.setGeneration(currentGeneration.clone());
                            average.makeMove();
                        }
                    }
                }
                else
                    player = 1;
            }
        }
    }
    
    //Main part of code for method from: http://stackoverflow.com/a/1359700
    private void sendDataToServer(int finalTotal)
    {
        try 
        {
            float results;
            
            results = (float) cellDifference / (float) numOfMoves;
            
            String urlParameters = "?gameType=" + Integer.toString(player2Type) + "&result=" + Float.toString(results) + "&finalScore=" + Integer.toString(finalTotal);
            
            System.out.println(urlParameters);
            
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("http://research.malone.int/GameOfLifeWebsite/SendResults" + urlParameters);

            HttpResponse response = client.execute(request);
        } 
        catch(Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Called when the board needs to be changed.
     * Will iterate to the next generation and repaint the buttons.
     * It will then go through and see how many cells player 1 & 2 has to see if the game is over or not,
     *      and determines who won, lost, or tied.
     */
    private void checkBoard()
    {
        //Go to the next generation.
        iterate();
        
        //Repaint the buttons to show what the next generation will be.
        paintButtons();
        
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
        
        //Show the players' their cells.
        player1CellsLabel.setText("Cells = " + player1Num);
        player2CellsLabel.setText("Cells = " + player2Num);
        
        //Check if the game is over or not.
        if(player1Num == 0 && player2Num == 0) //Tie
        {
            sendDataToServer(player2Type == 0 ? (player1Num > player2Num ? player1Num : player2Num) : player1Num);
            
            //Stop the timer from firing.
            if(waiting.isRunning())
                waiting.stop();
            
            //No more turns can be played so set these both to nothing.
            player1Label.setText("Player 1");
            player2Label.setText("Player 2");
            
            //Show the players the result of the game and ask if they want to play again.
            int picked = JOptionPane.showOptionDialog(null,
                    "IT'S A DRAW!!\nRestart?",
                    "Game Over",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"yes", "no"},
                    "no");
            
            //If yes then reset the board and resart the timer.
            if(picked == 0)
            {
                setUpGrid();
                waiting.start();
            }
            else
            {
                if(frame.isShowing())
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                else
                    frame = null;
            }
        }
        else if(player1Num == 0) // player 2 won
        {
            sendDataToServer(player2Type == 0 ? (player1Num > player2Num ? player1Num : player2Num) : player1Num);
            
            if(waiting.isRunning())
                waiting.stop();
            
            player1Label.setText("Player 1");
            player2Label.setText("Player 2");
            
            int picked = JOptionPane.showOptionDialog(null,
                    "Player 2 wins!!\nRestart?",
                    "Game Over",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"yes", "no"},
                    "no");
            
            if(picked == 0)
            {
                setUpGrid();
                waiting.start();
            }
            else
            {
                if(frame.isShowing())
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                else
                    frame = null;
            }
        }
        else if(player2Num == 0) // player 1 won
        {
            sendDataToServer(player2Type == 0 ? (player1Num > player2Num ? player1Num : player2Num) : player1Num);
                
            if(waiting.isRunning())
                waiting.stop();
            
            player1Label.setText("Player 1");
            player2Label.setText("Player 2");
            
            int picked = JOptionPane.showOptionDialog(null,
                    "Player 1 Wins!!\nRestart?",
                    "Game Over",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"yes", "no"},
                    "no");
            
            if(picked == 0)
            {
                setUpGrid();
                waiting.start();
            }
            else
            {
                if(frame.isShowing())
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                else
                    frame = null;
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

                for(int j = 0; j < length; j++)
                    getGridButton(i, j).setBorder(new LineBorder(Color.BLACK));
            }

            checkBoard();
        }
    }

    /**
     * Called when the game needs to go to the next generation.
     * Implements Conway's Game of Life rules.
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
        
//        System.out.println();
    }
    
    /**
     * Code from coobird : http://stackoverflow.com/a/2715279
     * 
     * I added the the (size / 2) so that way the fonts do not overcrowd the space.
     * @param label The label to adjust the size.
     */
    public void setFontSize(Object element)
    {
        if(element instanceof JLabel)
        {
            JLabel label = (JLabel) element;
            Font labelFont = label.getFont();
            String labelText = label.getText().replaceAll("\\.", "");

            int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
            int componentWidth = label.getWidth();

            // Find out how much the font can grow in width.
            double widthRatio = (double)componentWidth / (double)stringWidth;

            int newFontSize = (int)(labelFont.getSize() * widthRatio);
            int componentHeight = label.getHeight();

            // Pick a new font size so it will not be larger than the height of label.
            int fontSizeToUse = Math.min(newFontSize, componentHeight);

            // Set the label's font size to the newly determined size.
            ((JLabel) element).setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse / 2));
        }
        else if(element instanceof JButton)
        {
            JButton button = (JButton) element;
            
            Font labelFont = button.getFont();
            String labelText = button.getText();

            int stringWidth = button.getFontMetrics(labelFont).stringWidth(labelText);
            int componentWidth = button.getWidth();

            // Find out how much the font can grow in width.
            double widthRatio = (double)componentWidth / (double)stringWidth;

            int newFontSize = (int)(labelFont.getSize() * widthRatio);
            int componentHeight = button.getHeight();

            // Pick a new font size so it will not be larger than the height of button.
            int fontSizeToUse = Math.min(newFontSize, componentHeight);

            // Set the button's font size to the newly determined size.
            ((JButton) element).setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse / 2));
        }
    }

    /**
     * Set up and push out the frame.
     */
    public void createAndShowUI() 
    {
        //The panel that will go into the frame.
        JPanel finalPanel = new JPanel(new GridLayout(1, 2));
        finalPanel.add(this); //Add the game.
        
        //The extra things to the right like the buttons.
        JPanel extra = new JPanel(new GridLayout(3, 1));
        
        //The submit button.
        submit = new JButton("Submit move");
        submit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                int moveByPlayer = player;
        
                if(player2Type != 0 && player == 2)
                    moveByPlayer = 1;

                applyMove(moveByPlayer);
            }
        });
        extra.add(submit);
        
        //This will hold everything for the players.
        JPanel players = new JPanel(new GridLayout(1,2));
        
        //Player 1's panel.
        JPanel player1Panel = new JPanel(new GridLayout(2, 1));
        
        //Setup the labels.
        player1Label.setForeground(Color.WHITE);
        player1CellsLabel.setForeground(Color.WHITE);
        
        //Setup the panel and add it.
        player1Panel.add(player1Label);
        player1Panel.add(player1CellsLabel);
        player1Panel.setBackground(getGridButton(0, 0).player1);
        players.add(player1Panel);
        
        //Player 2's panel.
        JPanel player2Panel = new JPanel(new GridLayout(2, 1));
        
        player2Label.setForeground(Color.WHITE);
        player2CellsLabel.setForeground(Color.WHITE);
        
        player2Panel.add(player2Label);
        player2Panel.add(player2CellsLabel);
        player2Panel.setBackground(getGridButton(0, 0).player2);
        players.add(player2Panel);
        
        //Add the players panel.
        extra.add(players);
        
        //The undo button.
        undo = new JButton("Undo move");
        undo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                int moveByPlayer = player;
        
                if(player2Type != 0 && player == 2)
                    moveByPlayer = 1;

                undoMove(moveByPlayer);
            }
        });
        extra.add(undo);
        
        //Add the extras panel and finsih setup of the frame.
        finalPanel.add(extra);
        
        if(frame != null)
        {
            frame.getContentPane().add(finalPanel);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();

            //Now that the frame is packed and the labels are painted, we can
            //now resize all of the labels.
            setFontSize(player1Label);
            setFontSize(player1CellsLabel);
            setFontSize(player2CellsLabel);
            setFontSize(player2Label);
            setFontSize(submit);
            setFontSize(undo);

            //Put the frame in the middle of the screen and show it to the players.
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
            frame.setVisible(true);
        }
    }
   
    /**
     * Called from main. Starts up the game.
     */
    public void start()
    {
        //Set the grid to be length X length for the cells.
        setLayout(new GridLayout(length, length));
        
        dumb = new DumbAI(this);
        average = new AverageAI(this, length);
        
        //Make the mouse adapter that will be hooked to each button.
        MouseAdapter myMA = new MouseAdapterMod(this);

        //Go through and make and put all the buttons in.
        for (int i = 0; i < length; i++) 
        {
            for (int j = 0; j < length; j++) 
            {
                ButtonPanel btnPanel = new ButtonPanel(i, j);
                btnPanel.addMouseListener(myMA);
                list.add(btnPanel);
                add(btnPanel);
            }
        }
        
        //The contructor is done by this point, so call setup then create the frame.
        setUpGrid();
        createAndShowUI();
       
        waiting.start();
    }
    
}
