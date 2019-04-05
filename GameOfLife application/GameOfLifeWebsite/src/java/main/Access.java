/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

/**
 *
 * @author cjedwards1
 * 
 * Contains the ability to use SQl queries and hides log in credentials for 
 * security purposes from the main program. When the program needs to 
 * communicate with SQL, it calls this class to execute a SQL String
 * and return the table instance for the program to use. If the program 
 * calls for the variable ID_NUM in a query the class will block the variable
 * from being entered into the table data. If ID_NUM is truly wanted, have
 * it come in under an alias such as id_num or id.
 */
public class Access extends HttpServlet {
    
    private DataSource datasource;
    
    public Access()
    {
        try 
        {
            // Look up the JNDI data source only once at init time
            Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
            System.out.println(envCtx);
            datasource = (DataSource) envCtx.lookup("jdbc/TestDB");
        }
        catch (NamingException e) 
        {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException 
    {
        return datasource.getConnection();
    }

    /**
     * Executes the query string, creates the table, then returns the table.
     * @param sqlString The string that is a functioning SQL query.
     * @return The table that would normally result in the query being executed.
     * @throws Exception 
     */
    synchronized public Table getTable(String sqlString) throws Exception
    {
        Table table;
        Connection connection = null;
        Statement stmt = null;
        try
        {
            connection = getConnection();
            stmt = connection.createStatement();
            
            //Set and execute a query.
            ResultSet temp = stmt.executeQuery(sqlString);
            ResultSetMetaData allColumns = temp.getMetaData();
            int numberOfColumns = allColumns.getColumnCount();
            ArrayList<String> tempHolder = new ArrayList();

            for(int i = 1; i <= numberOfColumns; i++)
            {
                //There could be a possibility of 2 of the same columns
                //being added (ex: id of student for both majors).
                if(!tempHolder.contains(allColumns.getColumnName(i)))
                    tempHolder.add(allColumns.getColumnName(i));
            }
            
            table = new Table(tempHolder.size());
            table.nameColumns(tempHolder);
            
            while(temp.next())
            {
                for(String item : tempHolder)
                {
                    table.giveData(temp.getObject(item), item);
                }
            }
            temp.close();
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                    
                    if(stmt != null)
                        stmt.close();
                }
                catch(SQLException e){}
            }
        }
        
        return table;
    }
    
    /**
     * Give a student's ID and a SQL string with a "?" in it. The ID will replace
     * the "?"'s inside the string, then the string will be executed and a Table
     * will be returned of the results.
     * 
     * @param sql The functioning SQL query with "?"'s in place of student IDs.
     * @param id The student's ID.
     * @return A Table of the results.
     * @throws Exception 
     */
    public Table push(String sql, int id) throws Exception
    {
        String result = "";
        for(char item : sql.toCharArray())
        {
            if(item == '?')
                result += id;
            else
                result += item;
        }
        
        return getTable(result);
    }
    
    /**
     * If for whatever reason the program does not need to return a ResultSet
     * such as when wanting to INSERT or UPDATE.
     * 
     * @param query The functioning SQL query.
     * @throws SQLException 
     */
    public void execute(String query) throws SQLException
    {
        Connection connection = null;
        Statement stmt = null;
        try
        {
            connection = getConnection();
            stmt = connection.createStatement();
            stmt.execute(query);
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                    
                    if(stmt != null)
                        stmt.close();
                }
                catch(SQLException e){}
            }
        }
    }
    
    /**
     * Used to call a stored procedure from the sql database.
     * 
     * @param proc The name of the process to be executed.
     * @param params The list of parameters that will be use in execution.
     * @throws Exception 
     */
    synchronized public Table executeProcedure(String proc, String[] params) throws Exception
    {
        Table table = null;
        Connection connection = null;
        try
        {
            connection = getConnection();
            
            //Start building the query to call the procedure.
            //End result should look like --> {call [proc](param1, param2, ..., paramn)}
            StringBuilder query = new StringBuilder("{call ");
            query.append(proc).append("(");
            
            if(params.length > 0)
            {
                /**You have to insert ?'s into the string first and not the params
                 *because since we are using NVARCHARs, it throws an error saying
                 *that it is off the wrong format. This as well as some params will
                 *hold "," in them which will also throw an error. It is also
                 *best practice to do it this way anyways.
                 */
                query.append("?");
                
                for(int i = 1; i < params.length; i++)
                    query.append(",?");
                
                query.append(")}");
                
                //Set up the now formated String as a prepared statement.
                CallableStatement cStmt = connection.prepareCall(query.toString());
                
                //Now insert the parameters into the CallableStatement where
                //the question marks were.
                for(int i = 0; i < params.length; i++)
                    cStmt.setNString(i + 1, params[i]);

                boolean results = cStmt.execute();

                //From here it is the same as getTable.
                if(results)
                {
                    //Set and execute a query.
                    ResultSet temp = cStmt.getResultSet();
                    ResultSetMetaData allColumns = temp.getMetaData();
                    int numberOfColumns = allColumns.getColumnCount();
                    ArrayList<String> tempHolder = new ArrayList();

                    //There may be columns of ID_NUM that cannot be used, 
                    //so cannot be added. To make life easy, put all columns 
                    //execpt those 2 into an ArrayList then take them out.
                    for(int i = 1; i <= numberOfColumns; i++)
                    {
                        tempHolder.add(allColumns.getColumnName(i));
                    }

                    table = new Table(tempHolder.size());
                    table.nameColumns(tempHolder);

                    while(temp.next())
                    {
                        for(String item : tempHolder)
                        {
                            table.giveData(temp.getObject(item), item);
                        }
                    }
                    temp.close();
                }
                cStmt.close();
            }
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch(SQLException e){}
            }
        }
        
        return table;
    }
}