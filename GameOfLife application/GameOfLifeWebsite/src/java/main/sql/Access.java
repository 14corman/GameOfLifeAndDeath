/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.sql;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

/**
 *
 * @author cjedwards1
 * 
 * Contains the ability to use SQl queries and hides log in credentials for 
 * security purposes from the main program. When the program needs to 
 * communicate with SQL, it calls this class to execute a SQL String
 * and return the table instance for the program to use.
 */
public class Access extends HttpServlet {
    
    private final HashMap<String, DataSource> dataSources;
    
//    public Access(String tusername, String tpassword) throws SQLException
//    {
//        try
//        {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            //Open up both connection and statement to be used later.
//            conn = DriverManager.getConnection("jdbc:sqlserver://srv-david:8133",
//                    tusername, tpassword);
//
//            stmt = conn.createStatement();
//        }
//        catch(ClassNotFoundException ex)
//        {
//            ex.printStackTrace();
//        }
//    }
    
    public static Access getInstance() 
    {
        return AccessHolder.INSTANCE;
    }
    
    private static class AccessHolder
    {
        private static final Access INSTANCE = new Access();
    }
    
    private Access()
    {
        dataSources = new HashMap();
        createDatasource();
    }

    private Connection getConnection(String database) throws SQLException 
    {
        return dataSources.get(database).getConnection();
    }
    
    private void createDatasource()
    {
        try 
        {
            // Look up the JNDI data source only once at init time
            Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
            dataSources.put("project", (DataSource) envCtx.lookup("jdbc/GOLAD"));
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Executes the query string, creates the table, then returns the table.
     * @param sqlString The string that is a functioning SQL query.
     * @param variables The array of variables that will go into the query.
     * @param database The data source name to get (Options are project and sp)
     * @return The table that would normally result in the query being executed.
     * @throws Exception 
     */
    public Table getTable(String sqlString, Object[] variables, String database) throws Exception
    {
        System.out.println(sqlString);
        Table table = null;
        Connection connection = null;
        PreparedStatement stmt = null;
        try
        {
            connection = getConnection(database);
            stmt = connection.prepareStatement(sqlString);
            
            for(int i = 0; i < variables.length; i++)
            {
                if(variables[i] instanceof Boolean)
                    stmt.setBoolean(i + 1, (Boolean) variables[i]);
                else if(variables[i] instanceof Integer)
                    stmt.setInt(i + 1, (Integer) variables[i]);         
                else if(variables[i] instanceof Byte)
                    stmt.setByte(i + 1, (Byte) variables[i]);
                else if(variables[i] instanceof byte[])
                    stmt.setBytes(i + 1, (byte[]) variables[i]);
                else if(variables[i] instanceof Double)
                    stmt.setDouble(i + 1, (Double) variables[i]);           
                else if(variables[i] instanceof String)
                    stmt.setString(i + 1, variables[i].toString());
                else if(variables[i] instanceof Object)
                    stmt.setObject(i + 1, variables[i]);
                else
                    throw new SQLException(variables[i] + " is not an instance of Boolean, Integer, Byte, bytes[], Double, String, or Object.");
            }
            
            //Set and execute a query.
            try(ResultSet temp = stmt.executeQuery())
            {
                ResultSetMetaData allColumns = temp.getMetaData();
                int numberOfColumns = allColumns.getColumnCount();
                
                table = new Table(allColumns.getColumnCount());
                
                while(temp.next())
                {
                    for(int i = 1; i <= numberOfColumns; i++)
                    {
                        table.nameColumns(allColumns.getColumnName(i), i - 1);
                        table.giveData(temp.getObject(i), i - 1);
                    }
                }
            }
        }
        catch(SQLServerException ex)
        {
            createDatasource();
            return errorGetTable(sqlString, variables, database);
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
    
    private Table errorGetTable(String sqlString, Object[] variables, String database) throws Exception
    {
        Table table = null;
        Connection connection = null;
        PreparedStatement stmt = null;
        try
        {
            connection = getConnection(database);
            stmt = connection.prepareStatement(sqlString);
            
            for(int i = 0; i < variables.length; i++)
            {
                if(variables[i] instanceof Boolean)
                    stmt.setBoolean(i + 1, (Boolean) variables[i]);
                else if(variables[i] instanceof Integer)
                    stmt.setInt(i + 1, (int) variables[i]);         
                else if(variables[i] instanceof Byte)
                    stmt.setByte(i + 1, (byte) variables[i]);
                else if(variables[i] instanceof byte[])
                    stmt.setBytes(i + 1, (byte[]) variables[i]);
                else if(variables[i] instanceof Double || variables[i] instanceof Float)
                    stmt.setFloat(i + 1, (float) variables[i]);           
                else if(variables[i] instanceof String)
                    stmt.setString(i + 1, variables[i].toString());
                else if(variables[i] instanceof Object)
                    stmt.setObject(i + 1, variables[i]);
                else
                    throw new SQLException(variables[i] + " is not an instance of Boolean, Integer, Byte, bytes[], Double, String, or Object.");
            }
            
            //Set and execute a query.
            try(ResultSet temp = stmt.executeQuery())
            {
                ResultSetMetaData allColumns = temp.getMetaData();
                int numberOfColumns = allColumns.getColumnCount();
                
                table = new Table(allColumns.getColumnCount());
                
                while(temp.next())
                {
                    for(int i = 1; i <= numberOfColumns; i++)
                    {
                        table.nameColumns(allColumns.getColumnName(i), i - 1);
                        table.giveData(temp.getObject(i), i - 1);
                    }
                }
            }
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
     * If for whatever reason the program does not need to return a ResultSet
     * such as when wanting to INSERT or UPDATE.
     * 
     * @param query The functioning SQL query.
     * @param variables The array of variables that will go into the query.
     * @param database The datasource name to get (Options are project and sp)
     * @return The key of the row that was changed.
     * @throws SQLException 
     */
    public int execute(String query, Object[] variables, String database) throws SQLException
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        int result;
        try
        {
            connection = getConnection(database);
            stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            for(int i = 0; i < variables.length; i++)
            {
                if(variables[i] instanceof Boolean)
                    stmt.setBoolean(i + 1, (Boolean) variables[i]);
                else if(variables[i] instanceof Integer)
                    stmt.setInt(i + 1, (Integer) variables[i]);         
                else if(variables[i] instanceof Byte)
                    stmt.setByte(i + 1, (Byte) variables[i]);       
                else if(variables[i] instanceof byte[])
                    stmt.setBytes(i + 1, (byte[]) variables[i]);
                else if(variables[i] instanceof Double)
                    stmt.setDouble(i + 1, (Double) variables[i]);           
                else if(variables[i] instanceof String)
                    stmt.setString(i + 1, variables[i].toString());
                else if(variables[i] instanceof Object)
                    stmt.setObject(i + 1, variables[i]);
                else
                    throw new SQLException(variables[i] + " is not an instance of Boolean, Integer, Byte, bytes[], Double, String, or Object.");
            }
            stmt.executeUpdate();
            connection.commit();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
            {
                result = rs.getInt(1);
            }
            else
                result = 0;
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
        
        return result;
    }
    
    /**
     * Used to call a stored procedure from the sql database.
     * 
     * @param proc The name of the process to be executed.
     * @param params The list of parameters that will be use in execution.
     * @param database The datasource name to get (Options are project and sp)
     * @throws Exception 
     */
    synchronized public Table executeProcedure(String proc, String[] params, String database) throws Exception
    {
        Table table = null;
        Connection connection = null;
        try
        {
            connection = getConnection(database);
            
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
                    try(ResultSet temp = cStmt.getResultSet())
                    {
                        ResultSetMetaData allColumns = temp.getMetaData();
                        int numberOfColumns = allColumns.getColumnCount();

                        table = new Table(allColumns.getColumnCount());

                        while(temp.next())
                        {
                            for(int i = 1; i <= numberOfColumns; i++)
                            {
                                table.nameColumns(allColumns.getColumnName(i), i - 1);
                                table.giveData(temp.getObject(i), i - 1);
                            }
                        }
                    }
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