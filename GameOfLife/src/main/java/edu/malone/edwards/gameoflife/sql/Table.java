/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.malone.edwards.gameoflife.sql;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 *
 * @author cjedwards1
 * 
 * Created when a part of the program queries SQl for data. It is used to
 * hold any data that may come from those queries such as column names, 
 * column-row data, number of rows, ext. Row numbers start at 0 and column 
 * numbers start at 1 just like when working with ResultSets.
 */
public class Table {
    
    //Start the row number off at -1 because there but first be a call to
    //next() to see if there is at least one row avaialbe like in ResultSet.
    private int row = -1;
    
    private final Column[] columns;
    
    private final HashMap<String, Integer> columnMatches = new HashMap();

    //Create the columns for the table on initialization.
    public Table(int size) 
    {
        columns = new Column[size];
        
//        Arrays.fill(columns, 0, size, new Column());
        
        for(int i = 0; i < size; i++)
        {
            columns[i] = new Column();
        }
    }
    
    /**
     * Close everything and delete everything associated with the table.
     */
    public void close()
    {
        for(Column column : columns)
        {
            column.elements = null;
            column.name = null;
            column = null;
        }
    }
    
    /**
     * Made so Table can be multi threaded.
     * @param start The row to start getting data. exclusive
     * @param finish The row to stop getting data. exclusive
     * @return A clone of this table with data from rows start to finish.
     * @throws SQLException 
     */
    public Table cloneTable(int start, int finish) throws SQLException
    {
        Table newTable = new Table(getColumnCount());
        
        int currentRow = getRow();
        
        setRow(0);
        
        while(next())
        {
            if(getRow() >= start && getRow() <= finish)
            {
                for(int i = 1; i <= getColumnCount(); i++)
                {
                    newTable.nameColumns(getColumnName(i), i - 1);
                    newTable.giveData(getObject(i), i - 1);
                }
            }
        }
        
        setRow(currentRow);
        
        return newTable;
    }
    
    /**
     * Give each column its name.
     * 
     * @param temp The list of column names.
     */
    public void nameColumns(String temp, int i)
    {
            columns[i].name = temp;
            columnMatches.put(temp, i);
    }
    
    /**
     * Give the columns all the data by row.
     * 
     * @param item The row item to add to the column.
     * @param columnName The column's name.
     */
    public void giveData(Object item, int i)
    {
        columns[i].elements.add(item);
    }
    
    /**
     * Checking if another row is available and if so setting it.
     * 
     * @return True if there is another line available and false if there is not.
     */
    public boolean next()
    {
        row++;
        return columns[0].newRow(row);
    }
    
    /**
     * If the user or program needs to set the row cursor to another row.
     * 
     * @param i Integer to set the row to.
     * @return True if the row is available and false if not.
     */
    public boolean setRow(int i)
    {
        try
        {
            columns[0].elements.get(i);
            row = i - 1;
            return true;
        }
        catch(Exception ex)
        {
            return false;
        }
    }
    
    /**
     * 
     * @return The current row.
     */
    public int getRow()
    {
        return row;
    }
    
    /**
     * 
     * @return The number of columns in the table.
     */
    public int getColumnCount()
    {
        return columns.length;
    }
    
    /**
     * 
     * @return Number of rows in the current table.
     */
    public int numberOfRows()
    {
        return columns[0].elements.size();
    }
    
    /**
     * Get the column name from the list of columns.
     * @param i Number of column to find. Remember column list starts at 1.
     * @return The column name.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public String getColumnName(int i) throws ArrayIndexOutOfBoundsException
    {
        return columns[i - 1].name;
    }
    
    /**
     * Get the Integer from the given column based based off of the current row.
     * @param columnName Column to find the Integer from.
     * @return an Integer value
     * @throws SQLException If the column name does not exist.
     * @throws NumberFormatException If the item is not formated to be an Integer.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Integer getInt(String columnName) throws SQLException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        if(columnMatches.containsKey(columnName))
            return columns[columnMatches.get(columnName)].retreiveInt(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Integer from the given column based based off of the current row.
     * @param i Column number to find the Integer from.
     * @return an Integer value
     * @throws SQLException If the column does not exist.
     * @throws NumberFormatException If the item is not formated to be an Integer.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Integer getInt(int i) throws SQLException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        if(columns.length >= i)
            return columns[i - 1].retreiveInt(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the String from the given column based based off of the current row.
     * @param columnName Column to find the String from.
     * @return A string.
     * @throws SQLException If the column name does not exist.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public String getString(String columnName) throws SQLException, ArrayIndexOutOfBoundsException
    {
        if(columnMatches.containsKey(columnName))
            return columns[columnMatches.get(columnName)].retreiveString(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the String from the given column based based off of the current row.
     * @param i Column number to find the String from.
     * @return A string.
     * @throws SQLException If the column does not exist.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public String getString(int i) throws SQLException, ArrayIndexOutOfBoundsException
    {
        if(columns.length >= i)
            return columns[i - 1].retreiveString(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Object from the given column based based off of the current row.
     * @param columnName Column to find the Object from.
     * @return An Object.
     * @throws SQLException If the column name does not exist.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Object getObject(String columnName) throws SQLException, ArrayIndexOutOfBoundsException
    {
        if(columnMatches.containsKey(columnName))
            return columns[columnMatches.get(columnName)].retreiveObject(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Object from the given column based based off of the current row.
     * @param i Column number to find the Object from.
     * @return An Object.
     * @throws SQLException If the column does not exist.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Object getObject(int i) throws SQLException, ArrayIndexOutOfBoundsException
    {
        if(columns.length >= i)
            return columns[i - 1].retreiveObject(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Double from the given column based based off of the current row.
     * @param columnName Column to find the Double from.
     * @return A Double.
     * @throws SQLException If the column name does not exist.
     * @throws NumberFormatException If the object is not formated to be a Double.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Double getDouble(String columnName) throws SQLException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        if(columnMatches.containsKey(columnName))
            return columns[columnMatches.get(columnName)].retreiveDouble(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Double from the given column based based off of the current row.
     * @param i Column number to find the Double from.
     * @return A Double.
     * @throws SQLException If the column does not exist.
     * @throws NumberFormatException If the object is not formated to be a Double.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Double getDouble(int i) throws SQLException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        if(columns.length >= i)
            return columns[i - 1].retreiveDouble(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Boolean from the given column based based off of the current row.
     * @param columnName Column to find the Boolean from.
     * @return A Boolean.
     * @throws SQLException If the column name does not exist.
     * @throws NumberFormatException If the object is not formated to be a Double.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Boolean getBoolean(String columnName) throws SQLException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        if(columnMatches.containsKey(columnName))
            return columns[columnMatches.get(columnName)].retreiveBoolean(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Boolean from the given column based based off of the current row.
     * @param i Column number to find the Boolean from.
     * @return A Boolean.
     * @throws SQLException If the column does not exist.
     * @throws NumberFormatException If the object is not formated to be a Double.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Boolean getBoolean(int i) throws SQLException, NumberFormatException, ArrayIndexOutOfBoundsException
    {
        if(columns.length >= i)
            return columns[i - 1].retreiveBoolean(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Date from the given column based based off of the current row.
     * @param columnName Column to find the Date from.
     * @return A Date.
     * @throws SQLException If the column name does not exist.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Date getDate(String columnName) throws SQLException, ArrayIndexOutOfBoundsException
    {
        if(columnMatches.containsKey(columnName))
            return columns[columnMatches.get(columnName)].retreiveDate(row);
        else
            throw new SQLException();
    }
    
    /**
     * Get the Date from the given column based based off of the current row.
     * @param i Column number to find the Date from.
     * @return A Date.
     * @throws SQLException If the column does not exist.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    public Date getDate(int i) throws SQLException, ArrayIndexOutOfBoundsException
    {
        if(columns.length >= i)
            return columns[i - 1].retreiveDate(row);
        else
            throw new SQLException();
    }
}

//Each column inside the table.
class Column {
    
    //Name of the column.
    protected String name;
    
    //Will be to hold any rows.
    protected ArrayList elements = new ArrayList();
    
    protected boolean newRow(int i)
    {
        return i < elements.size();
    }
    
    /**
     * 
     * @param row Row to look for in the column.
     * @return An Integer
     * @throws NumberFormatException  If the Object cannot be formated to an Integer.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    protected Integer retreiveInt(int row) throws NumberFormatException, ArrayIndexOutOfBoundsException
    {
        if(elements.get(row) instanceof BigDecimal)
           return ((BigDecimal) elements.get(row)).intValue();
        
        if(elements.get(row) instanceof String)
            return Integer.valueOf((String) elements.get(row));
        
        return (Integer) elements.get(row);
    }
    
    /**
     * 
     * @param row Row to look for in the column.
     * @return A String.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    protected String retreiveString(int row) throws ArrayIndexOutOfBoundsException
    {
        String result = elements.get(row) != null ? elements.get(row).toString() : null;
        
        if(result != null)
        {
            result = result.replaceAll("'", "''");
            return result;
        }
        else
            return null;
    }
    
    /**
     * 
     * @param row Row to look for in the column.
     * @return An Object.
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     */
    protected Object retreiveObject(int row) throws ArrayIndexOutOfBoundsException
    {
        return elements.get(row);
    }
    
    /**
     * 
     * @param row Row to look for in the column.
     * @return A Double
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     * @throws NumberFormatException If the Object cannot be formated to a Double.
     */
    protected Double retreiveDouble(int row) throws NumberFormatException, ArrayIndexOutOfBoundsException
    {
        if(elements.get(row) instanceof BigDecimal)
           return ((BigDecimal) elements.get(row)).doubleValue();
        
        if(elements.get(row) instanceof String)
            return Double.valueOf((String) elements.get(row));
        
        return (Double) elements.get(row);
    }
    
    /**
     * 
     * @param row Row to look for in the column.
     * @return A Boolean
     * @throws ArrayIndexOutOfBoundsException If the row is invalid.
     * @throws NumberFormatException If the Object cannot be formated to a Double.
     */
    protected Boolean retreiveBoolean(int row) throws NumberFormatException, ArrayIndexOutOfBoundsException
    {
        return (Boolean) elements.get(row);
    }
    
    /**
     * 
     * @param row Row to look for in the column.
     * @return A sql Date
     * @throws ArrayIndexOutOfBoundsException If the row is invalid. 
     */
    protected Date retreiveDate(int row) throws ArrayIndexOutOfBoundsException
    {
        if(elements.get(row) instanceof Timestamp)
            return new java.sql.Date(((Timestamp) elements.get(row)).getTime());
        
        return (Date) elements.get(row);
    }
}

