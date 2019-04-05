/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflifeanddeath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

/**
 *
 * @author Cory Edwards
 */
class ButtonPanel extends JPanel {

    public final Color player1 = Color.RED; //Player 1 color.
    public final Color player1Pre; //Player 1 inserting cell color.
    public final Color player2Pre; //Player 2 inserting cell color.
    public final Color player2 = Color.BLUE; //Player 2 color.
    public final Color original = UIManager.getColor("Panel.background"); //The default background color for the panel.
    public Color innerSquare = Color.BLACK; //The color that the inner square will be drawn from.
    public final int row; //The row the button is in.
    public final int column; //The column the button is in.

    /**
     * @param row Row that the button is in.
     * @param column Column that button is in.
     */
    public ButtonPanel(int row, int column) 
    {
        this.row = row;
        this.column = column;
        this.setPreferredSize(new Dimension(60, 60)); //Setting a default size.
        this.setBorder(new LineBorder(Color.BLACK)); //Setting the default border color.
        innerSquare = original; //Setting the original color to be the default to the inner square.

        //Calculating the 2 inserting cell colors by cutting the alpha in half.
        player1Pre = new Color(player1.getRed(), player1.getGreen(), player1.getBlue(), player1.getAlpha() / 2);
        player2Pre = new Color(player2.getRed(), player2.getGreen(), player2.getBlue(), player2.getAlpha() / 2);
    }
    
    /**
     * Set the background color of the cell and the color of the inner square.
     * @param background The background color of the cell.
     * @param foreground The inner square color of the cell.
     */
    public void setColor(Color background, Color foreground)
    {
        this.setBackground(background);
        innerSquare = foreground;
        repaint(); //Calls the overriding constructor paontComponent.
    }

    @Override
    //Every iteration gets repainted.
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g); //First do what the normal painComponent does.
        
        //If the innerSquare is not null then there is a color for it, so draw it.
        if(innerSquare != null)
        {
            //Get the old color saved so we can go back to it after the method is done.
            Color gColor = g.getColor();

            g.setColor(innerSquare);

            //Find where to put the inner square.
            int middleOfButtonHeight = this.getHeight()  / 2;
            int middleOfButtonWidth = this.getWidth()  / 2;
            
            int sizeOfInnerSquareHeight = this.getHeight() / 4;
            int sizeOfInnerSquareWidth = this.getWidth() / 4;

            //Draw a rectangle (x position, y position, width, height).
            g.fillRect(middleOfButtonWidth - (sizeOfInnerSquareWidth / 2),
                    middleOfButtonHeight - (sizeOfInnerSquareHeight / 2), sizeOfInnerSquareWidth, sizeOfInnerSquareHeight);
            g.setColor(gColor);
        }
    }
}
