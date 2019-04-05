/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflifeanddeath;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Cory Edwards
 * 
 * The main menu to give the variables for both programs and to start up either easy.
 * It looks hideous, but works.
 */
public class StartGame {

    public static void main(String args[]) 
    {
        //Set the programs to the computer's default look.
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            Logger.getLogger(StartGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Start up the main menu.
        java.awt.EventQueue.invokeLater(() -> 
        {
            //The frame of it.
            JFrame frame = new JFrame("Main Menu");
            
            //The panel that will be in the frame.
            JPanel mainPanel = new JPanel(new GridLayout(2, 1));
            
            //The Conway's game of life panel.
            JPanel CGoLPanel = new JPanel(new GridBagLayout());
            
            //The constraints to make it easy to set up the panel.
            GridBagConstraints constraints = new GridBagConstraints();
            
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 9;
            constraints.gridheight = 7;
            
            JPanel CGoLExp = new JPanel();
            
            CGoLExp.add(new JLabel("<html><h1>Conway's Game of Life</h1>\n"
                    + "Conway's Game of Life is a simmulation of cells being born and dying.<br>"
                    + "Each cell has 8 neighbors (right, left, top, bottom, upper left, upper right, lower left, lower right).<br>"
                    + "If a cell is alive and has either 2 or 3 live neighbors then it will live on to the next generation.<br>"
                    + "If the cell has less than 2 neighbors it \"starves\" and dies.<br>"
                    + "If it has more than 3 it dies from \"overcrowding\".<br>"
                    + "If a dead cell has exactly 3 live neighbors, then it gets reborn.<br>"
                    + "This simulation has 4 settings [size of square grid], [# of generations till reset], [size of each square cell in pizels], [probability of cells being born when board is getting set up],<br>"
                    + "&emsp;&emsp;&emsp; and [time to wait between generations in milliseconds].</html>"));
            
            CGoLPanel.add(CGoLExp, constraints);
            
            constraints.gridx = 0;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            CGoLPanel.add(new JLabel("Size of grid:"), constraints);
            
            //Size of grid.
            JTextField size = new JTextField("300", 4);
            constraints.gridx = 1;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            CGoLPanel.add(size, constraints);
            
            constraints.gridx = 2;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            CGoLPanel.add(new JLabel("# of generations till reset:"), constraints);
            
            //Number of generations before reset.
            JTextField reset = new JTextField("50", 3);
            constraints.gridx = 3;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            CGoLPanel.add(reset, constraints);
            
            constraints.gridx = 4;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            CGoLPanel.add(new JLabel("Size of square pixel:"), constraints);
            
            //Size of cells in pixels.
            JTextField pixels = new JTextField("", 2);
            constraints.gridx = 5;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            CGoLPanel.add(pixels, constraints);
            
            constraints.gridx = 6;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            CGoLPanel.add(new JLabel("Probability of cell to be born with when board is set up:"), constraints);
            
            //Size of cells in pixels.
            JTextField GOLProb = new JTextField("80", 3);
            constraints.gridx = 7;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            CGoLPanel.add(GOLProb, constraints);
            
            constraints.gridx = 8;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            CGoLPanel.add(new JLabel("Time between generations: "), constraints);
            
            //Time between generations in milliseconds.
            JTextField time = new JTextField("500", 4);
            constraints.gridx = 9;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            CGoLPanel.add(time, constraints);
            
            //Button to start simulation.
            JButton CGoLEnter = new JButton("Start Conway's Game of Life simulation");
            CGoLEnter.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    java.awt.EventQueue.invokeLater(() ->
                    {
                        try
                        {
                            //[size of 1 side of the grid], [# of times till reset], [size of each square cell in pizels], [time between iterations]
                            new ConwayGameOfLife(size.getText(), reset.getText(), pixels.getText(), GOLProb.getText(), time.getText()).start();
                        }
                        catch (ArgumentOutOfBoundsException ex) 
                        {
                            Logger.getLogger(StartGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        catch( NumberFormatException ex)
                        {
                            JOptionPane.showMessageDialog(frame,
                                "Some text was not a valid number.",
                                "Number error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            });
            
            constraints.gridx = 10;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            CGoLPanel.add(CGoLEnter, constraints);
            
            //Add finished panel to main.
            mainPanel.add(CGoLPanel);
            
            //Game of life and death panel.
            JPanel GoLaDPanel = new JPanel(new GridBagLayout());
            
            JPanel GoLaDExp = new JPanel();
            
            GoLaDExp.add(new JLabel("<html><h1>Game of Life and Death</h1>"
                    + "Game of Life and Death is a 2 player game of Conway's game of life.<br>"
                    + "The consept of the game came from carykh at https://www.youtube.com/user/carykh/featured.<br>"
                    + "You and a player take turns doing 1 of 3 things.<br>"
                    + "1. Delete 1 of your cells.<br>"
                    + "2. Delete an opponent's cell.<br>"
                    + "3. Take a dead cell and make it become one of your cells. This will require you to delete 2 of your cells in the process.&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<br>"
                    + "The goal is to get your opponent's cell count down to 0.<br>"
                    + "The settings are [size of the square board to use], [the probability (as % [0, 100]) of a cell being born when the board is set up]. (Must be between 1 and 10)</html>"));
            
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 8;
            constraints.gridheight = 8;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = new Insets(0,0,0,0);
            GoLaDPanel.add(GoLaDExp, constraints);
            
            constraints.gridx = 0;
            constraints.gridy = 8;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            GoLaDPanel.add(new JLabel("Size of square grid:"), constraints);
            
            //The size of the board.
            JTextField n = new JTextField("1", 2);
            constraints.gridx = 1;
            constraints.gridy = 8;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            GoLaDPanel.add(n, constraints);
            
            constraints.gridx = 2;
            constraints.gridy = 8;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            GoLaDPanel.add(new JLabel("The probability that a cell will be born when the board is set up:"), constraints);
            
            //The size of the board.
            JTextField prob = new JTextField("60", 3);
            constraints.gridx = 3;
            constraints.gridy = 8;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            GoLaDPanel.add(prob, constraints);
            
            
            constraints.gridx = 4;
            constraints.gridy = 8;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            GoLaDPanel.add(new JLabel("Type of player 2: "), constraints);
            
            //Size of cells in pixels.
            JComboBox player2Type = new JComboBox(new String[]{"Human", "Dumb AI", "Average AI"});
            constraints.gridx = 5;
            constraints.gridy = 8;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            GoLaDPanel.add(player2Type, constraints);
            
            constraints.gridx = 6;
            constraints.gridy = 8;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,0);
            GoLaDPanel.add(new JLabel("Send game results to server? "), constraints);
            
            //Size of cells in pixels.
            JComboBox sendData = new JComboBox(new String[]{"Yes", "No"});
            constraints.gridx = 7;
            constraints.gridy = 8;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,0,0,5);
            GoLaDPanel.add(sendData, constraints);
            
            
            //Button to start the game.
            JButton GoLaDEnter = new JButton("Start Game of Life and Death");
            GoLaDEnter.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    java.awt.EventQueue.invokeLater(() ->
                    {
                        try 
                        {
                            new GameOfLifeAndDeathGame(n.getText(), prob.getText(), Integer.toString(player2Type.getSelectedIndex()), Integer.toString(sendData.getSelectedIndex())).start();
                        }
                        catch (ArgumentOutOfBoundsException ex) 
                        {
                            Logger.getLogger(StartGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        catch( NumberFormatException ex)
                        {
                            JOptionPane.showMessageDialog(frame,
                                "Some text was not a valid number.",
                                "Number error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            });
            
            constraints.gridx = 12;
            constraints.gridy = 8;
            constraints.gridwidth = 5;
            constraints.gridheight = 1;
            constraints.insets = new Insets(0,10,0,0);
            GoLaDPanel.add(GoLaDEnter, constraints);
            
            //Add finished panel to main.
            mainPanel.add(GoLaDPanel);
            
            //Finish frame and deploy.
            frame.getContentPane().add(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true); 
        });
    }
}
