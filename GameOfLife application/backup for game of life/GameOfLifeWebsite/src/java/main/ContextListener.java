/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) 
    {
        try 
        {
            System.out.println("Stopping executor service");
            SmartAI.executorService.shutdown();
            if(!SmartAI.executorService.awaitTermination(60, TimeUnit.SECONDS))
                SmartAI.executorService.shutdownNow();
        } catch (InterruptedException ex) {}
    }
}
