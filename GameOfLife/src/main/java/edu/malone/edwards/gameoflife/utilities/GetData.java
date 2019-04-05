/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.malone.edwards.gameoflife.utilities;

import java.sql.SQLException;
import edu.malone.edwards.gameoflife.sql.Access;
import java.util.HashMap;
import edu.malone.edwards.gameoflife.sql.Table;
import org.openide.util.Exceptions;

/**
 *
 * @author Cory Edwards
 */
public class GetData {
    
    public void giveData(String playerType, String score, int finalScore, String userName, boolean training, int result, boolean smart)
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
//                try
//                {
//                    Access.getInstance().execute("INSERT INTO [NSFEdwards2017].[dbo].SMART_AI_TRAINING (SCORE) VALUES (?)", new Object[]{score}, "project");
//                }
//                catch(Exception ex){ex.printStackTrace();}
                break;
        }
        
        if(!training && !smart)
        {
            try
            {
                Access.getInstance().execute("INSERT INTO [NSFEdwards2017].[dbo]." + player2 + " (USER_NAME, SCORE, RESULT) VALUES (?, ?, ?)", new Object[]{userName, score, result}, "project");
               
                HashMap<String, Integer> scores = new HashMap();
                Table table = Access.getInstance().getTable("SELECT * FROM [NSFEdwards2017].[dbo].HIGH_SCORES", new Object[]{}, "project");

                while(table.next())
                {
                    scores.put(table.getString("TYPE"), table.getInt("SCORE"));
                }

                if(finalScore > scores.get(player2))
                {
                    Access.getInstance().execute("UPDATE [NSFEdwards2017].[dbo].HIGH_SCORES SET USER_NAME = ?, SCORE = ? WHERE TYPE = ?", new Object[]{userName, finalScore, player2}, "project");
                }
            }
            catch(Exception ex){ex.printStackTrace();}
        }
        else if(smart)
        {
            try
            {
                Access.getInstance().execute("INSERT INTO [NSFEdwards2017].[dbo].SMART_" + player2 + " (SCORE, RESULT) VALUES (?, ?)", new Object[]{score, result}, "project");
            }
            catch(SQLException ex)
            {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
