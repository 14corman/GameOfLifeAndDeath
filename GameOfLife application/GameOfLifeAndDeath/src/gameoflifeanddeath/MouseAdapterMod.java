/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflifeanddeath;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Cory Edwards
 */
class MouseAdapterMod extends MouseAdapter {
    
    //The game reference to call makeMove one the player clicks on the board.
    private final GameOfLifeAndDeathGame game;
    
    //Constructor.
    public MouseAdapterMod(GameOfLifeAndDeathGame game)
    {
        this.game = game;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int player = game.player;
        
        if(game.player2Type != 0 && game.player == 2)
            player = 1;
        
        //Get the button that was clicked, then send over its row and 
        //column to see what to do.
        ButtonPanel btn = (ButtonPanel)e.getSource();
        game.makeMove(btn.row, btn.column, player);
    }
}
