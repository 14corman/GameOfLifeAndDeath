/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import static main.AIA.NODE_LIST;

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
        try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("C:\\Server Files\\GameOfLifeWebsite\\Nodes.txt"))) 
        {     
            NODE_LIST = (Nodes) inputStream.readObject();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            NODE_LIST = new Nodes();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) 
    {
        try 
        {
            System.out.println("Stopping executor service");
            
            try (ObjectOutputStream bw = new ObjectOutputStream(new FileOutputStream("C:\\Server Files\\GameOfLifeWebsite\\Nodes.txt"))) 
            {
                bw.writeObject(NODE_LIST);
                bw.flush();

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            
            SmartAI.executorService.shutdown();
            if(!SmartAI.executorService.awaitTermination(60, TimeUnit.SECONDS))
                SmartAI.executorService.shutdownNow();
        } catch (InterruptedException ex) {}
    }
}
