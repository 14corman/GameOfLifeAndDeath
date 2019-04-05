/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import main.sql.Access;
import main.sql.Table;
import main.utilities.ArgumentOutOfBoundsException;
import static main.utilities.ServletFunctions.retrieveUser;
import org.apache.commons.net.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.openide.util.Exceptions;

/**
 *
 * @author cjedwards1
 */
@WebServlet(asyncSupported = true, value = "/GOLADGame")
public class GOLAD extends HttpServlet
{
    
    public static HashMap<Integer, GameOfLifeAndDeathGame> games = new HashMap();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException
    {
        try(PrintWriter out = response.getWriter())
        {
            JSONObject scores = new JSONObject();
            
//            Table table = Access.getInstance().getTable("SELECT * FROM [NSFEdwards2017].[dbo].HIGH_SCORES", new Object[]{}, "project");
//            
//            while(table.next())
//            {
//                if(!table.getString("USER_NAME").equals("NA"))
//                    scores.put(table.getString("TYPE"), table.getString("USER_NAME") + " with " + Integer.toString(table.getInt("SCORE")));
//            }
            
            out.write(scores.toString());
            out.flush();
            
        }
        catch(Exception ex){ex.printStackTrace();}
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException
    {
        try
        {
            retrieveUser(request, response);

            System.out.println("New User: " + request.getAttribute("user"));
            System.out.println("Is guest: " + request.getAttribute("isNotGuest"));

            if((boolean) request.getAttribute("isNotGuest"))
            {
                if(request.getParameter("paint") != null)
                {
                    response.setContentType("application/json");
                    GameOfLifeAndDeathGame game;
                    try(ByteArrayInputStream bi = new ByteArrayInputStream(Base64.decodeBase64(request.getParameter("game").getBytes("UTF-8"))))
                    {
                        if(bi.available() > 0)
                        {
                            try(ObjectInputStream is = new ObjectInputStream(bi))
                            {
                                game = (GameOfLifeAndDeathGame) is.readObject();
                            }
                        }
                        else
                            return;
                    }
                    
                    if(game != null)
                    {
                        try(PrintWriter out = response.getWriter())
                        {
                            game.paintButtons(out);
                        }
                        catch(JSONException ex)
                        {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                else
                {
                    response.setContentType("text/html");

                    if(request.getParameter("setup") != null)
                    {
                        try
                        {
                            GameOfLifeAndDeathGame game = new GameOfLifeAndDeathGame("1", "60", request.getParameter("player2"));

                            game.setUpGrid();
                            game.saveGame(response);
                        }
                        catch(ArgumentOutOfBoundsException ex){
                            Exceptions.printStackTrace(ex);
                        }
                        catch(NumberFormatException ex){
                            Exceptions.printStackTrace(ex);
                        }
                    }
                    else
                    {
                        GameOfLifeAndDeathGame game;
                        try(ByteArrayInputStream bi = new ByteArrayInputStream(Base64.decodeBase64(request.getParameter("game").getBytes("UTF-8"))))
                        {
                            if(bi.available() > 0)
                            {
                                try(ObjectInputStream is = new ObjectInputStream(bi))
                                {
                                        game = (GameOfLifeAndDeathGame) is.readObject();
                                }
                            }
                            else
                                return;
                        }
                        
                        if(game != null)
                        {
                            game.user = request.getAttribute("user").toString();

                            int currentPlayer = Integer.parseInt(request.getParameter("player"));
                            String typeMove = request.getParameter("move");

                            if(game.player2Type != 0 && game.player == 2)
                                currentPlayer = 1;

                            game.training = request.getParameter("training") != null ? Boolean.valueOf(request.getParameter("training")) : false;

                            switch(typeMove)
                            {
                                case "make":
                                    game.makeMove(Integer.parseInt(request.getParameter("row")), Integer.parseInt(request.getParameter("column")), currentPlayer);
                                    game.saveGame(response);
                                    break;
                                case "apply":
                                    game.applyMove(currentPlayer);
                                    game.saveGame(response);
                                    break;
                                case "undo":
                                    game.undoMove(currentPlayer);
                                    game.saveGame(response);
                                    break;
                                case "made":
                                    game.aiTurn(currentPlayer, request.startAsync(request, response));
                                default:
                                    break;
                            }
                        }
                        else
                            System.out.println("Null game!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                }
            }
            else
                response.sendRedirect("/GameOfLife/access_denied.jsp");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
