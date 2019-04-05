/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.utilities;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
//import org.apache.commons.configuration2.Configuration;

/**
 *
 * @author Cory Edwards
 */
public class ServletFunctions 
{    
//    public static Configuration properties;
    
    public static void retrieveUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher rd = request.getServletContext().getContext("/Core").getRequestDispatcher("/servlet_functions");
        rd.include(request, response);
    }
}
