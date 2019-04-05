/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.HashMap;

/**
 *
 * @author Cory Edwards
 */
public class GetData {
    
    public void giveData(String playerType, String score, int finalScore, String userName, boolean training, int result)
    {
        String player2;
        switch (playerType) 
        {
            case "0":
                player2 = "HUMAN";
                break;
            case "1":
                player2 = "DUMB_AI";
                break;
            case "2":
                player2 = "AVERAGE_AI";
                break;
            default:
                player2 = "SMART_AI";
                break;
        }
        
        if(training)
        {
            try
            {
                GameOfLifeAndDeathGame.ACCESS.execute("INSERT INTO [NSFEdwards2017].[dbo].SMART_AI_TRAINING (SCORE) VALUES (" + score + ")");
            }
            catch(Exception ex){ex.printStackTrace();}
        }
        else
        {
            try
            {
                GameOfLifeAndDeathGame.ACCESS.execute("INSERT INTO [NSFEdwards2017].[dbo]." + player2 + " (USER_NAME, SCORE, RESULT) VALUES ('" + userName + "'," + score + "," + result + ")");
                GameOfLifeAndDeathGame.ACCESS.execute("INSERT INTO [NSFEdwards2017].[dbo].SMART_AI_TRAINING (SCORE) VALUES (" + score + ")");

                HashMap<String, Integer> scores = new HashMap();
                Table table = GameOfLifeAndDeathGame.ACCESS.getTable("SELECT * FROM [NSFEdwards2017].[dbo].HIGH_SCORES");

                while(table.next())
                {
                    scores.put(table.getString("TYPE"), table.getInt("SCORE"));
                }

                if(finalScore > scores.get(player2))
                {
                    GameOfLifeAndDeathGame.ACCESS.execute("UPDATE [NSFEdwards2017].[dbo].HIGH_SCORES SET USER_NAME = '" + userName + "', SCORE = " + finalScore + " WHERE TYPE = '" + player2 + "'");
                }
            }
            catch(Exception ex){ex.printStackTrace();}
        }
    }
}
