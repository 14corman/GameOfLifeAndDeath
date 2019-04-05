/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.malone.edwards.gameoflife.utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import edu.malone.edwards.admea.nodeUtils.Nodes;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.MalformedURLException;
import static edu.malone.edwards.gameoflife.utilities.IndependentGame.independentExecutor;
import edu.malone.edwards.gameoflife.utilities.ai.SmartAI;
import org.openide.util.Exceptions;

/**
 *
 * @author Cory Edwards
 */
@WebListener
public class ContextListener implements ServletContextListener 
{
    @Override
    public void contextInitialized(ServletContextEvent sce) 
    {
        try 
        {
            SmartAI.getInstance(10).init();
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) 
    {
        SmartAI.getInstance(10).close();
        
        independentExecutor.shutdownNow();
    }
}
