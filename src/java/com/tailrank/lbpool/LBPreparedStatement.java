
package com.tailrank.lbpool;

import java.sql.*;
import java.util.Calendar;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Iterator;

public class LBPreparedStatement implements PreparedStatement {

    /** My delegate. */
    protected PreparedStatement _stmt = null;

    /** The connection that created me. **/
    protected LBConnection _conn = null;

    protected String _sql = null;

    protected LBPendingStatementMeta pendingStatementMeta = null;

    public LBPreparedStatement( LBConnection c,
                                String sql,
                                PreparedStatement s ) {
        _stmt = s;
        _conn = c;
        _sql = sql;

        pendingStatementMeta = c.pendingStatementMeta;

        //this statement is open now... 
        ++pendingStatementMeta.openStatements;

    }

    protected void handleException( SQLException e ) throws SQLException {

        _conn.handleException( e );

        //NOTE: we found this bug later in the development of the LBPool driver.
        //We need to create a new statement representing the new connection.
        //
        // FIXME: Right now we don't support the various overloaded methods such as:
        //
        // prepareStatement(String sql, int autoGeneratedKeys) 
        //
        // Moving forward we'll have to add these.

        //FIXME: how do we re-apply the values from teh previous prepared
        //statement?  this is a BIG issue.

        PreparedStatement new_stmt = _conn.getDelegate().prepareStatement( _sql );

        _stmt = new_stmt;

    }

    protected void reapplyParameters( PreparedStatement source, PreparedStatement target ) {

        //I can use getParameterMetaData here to get the types of the parameters
        //I think.

//         ParameterMetaData pmd = source.getParameterMetaData();

//         int count = pmd.getParameterCount();

//         for( int i = 1; i <= count; ++i ) {

//             int type = pmd.getParameterType( i );

//         }
        
    }
    
    /** Sets my delegate. */
    public void setDelegate(PreparedStatement s) {
        _stmt = s;
    }

    /**
     * Close this DelegatingPreparedStatement, and close
     * any ResultSets that were not explicitly closed.
     */
    public void close() throws SQLException {
        _stmt.close();

        --pendingStatementMeta.openStatements;

    }

    public Connection getConnection() throws SQLException {
        return _conn; // return the delegating connection that created this
    }

    public ResultSet executeQuery(String sql) throws SQLException {

        while( true ) {
            try {
                _conn.isExecutingStatement = true;
                return _stmt.executeQuery( sql );
            } catch ( SQLException e ) {
                handleException( e );
            } finally {
                _conn.isExecutingStatement = false;
            }

        }

    }

    public ResultSet getResultSet() throws SQLException {

        return _stmt.getResultSet();
    }

    public ResultSet executeQuery() throws SQLException {

        while( true ) {

            try {
                _conn.isExecutingStatement = true;
                return _stmt.executeQuery();
            } catch ( SQLException e ) {
                handleException( e );
            } finally {
                _conn.isExecutingStatement = false;
            }

        }

    }

    public int executeUpdate(String sql) throws SQLException {  return _stmt.executeUpdate(sql);}
    public int getMaxFieldSize() throws SQLException {  return _stmt.getMaxFieldSize();}
    public void setMaxFieldSize(int max) throws SQLException { _stmt.setMaxFieldSize(max);}
    public int getMaxRows() throws SQLException { return _stmt.getMaxRows();}
    public void setMaxRows(int max) throws SQLException {  _stmt.setMaxRows(max);}
    public void setEscapeProcessing(boolean enable) throws SQLException { _stmt.setEscapeProcessing(enable);}
    public int getQueryTimeout() throws SQLException {  return _stmt.getQueryTimeout();}
    public void setQueryTimeout(int seconds) throws SQLException {  _stmt.setQueryTimeout(seconds);}
    public void cancel() throws SQLException {  _stmt.cancel();}
    public SQLWarning getWarnings() throws SQLException {  return _stmt.getWarnings();}
    public void clearWarnings() throws SQLException {  _stmt.clearWarnings();}
    public void setCursorName(String name) throws SQLException {  _stmt.setCursorName(name);}
    public boolean execute(String sql) throws SQLException {  return _stmt.execute(sql);}
    public int getUpdateCount() throws SQLException {  return _stmt.getUpdateCount();}
    public boolean getMoreResults() throws SQLException {  return _stmt.getMoreResults();}
    public void setFetchDirection(int direction) throws SQLException {  _stmt.setFetchDirection(direction);}
    public int getFetchDirection() throws SQLException {  return _stmt.getFetchDirection();}
    public void setFetchSize(int rows) throws SQLException {  _stmt.setFetchSize(rows);}
    public int getFetchSize() throws SQLException {  return _stmt.getFetchSize();}
    public int getResultSetConcurrency() throws SQLException {  return _stmt.getResultSetConcurrency();}
    public int getResultSetType() throws SQLException {  return _stmt.getResultSetType();}
    public void addBatch(String sql) throws SQLException {  _stmt.addBatch(sql);}
    public void clearBatch() throws SQLException {  _stmt.clearBatch();}
    public int[] executeBatch() throws SQLException {  return _stmt.executeBatch();}

    public int executeUpdate() throws SQLException {  return _stmt.executeUpdate();}
    public void setNull(int parameterIndex, int sqlType) throws SQLException {  _stmt.setNull(parameterIndex,sqlType);}
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {  _stmt.setBoolean(parameterIndex,x);}
    public void setByte(int parameterIndex, byte x) throws SQLException {  _stmt.setByte(parameterIndex,x);}
    public void setShort(int parameterIndex, short x) throws SQLException {  _stmt.setShort(parameterIndex,x);}
    public void setInt(int parameterIndex, int x) throws SQLException {  _stmt.setInt(parameterIndex,x);}
    public void setLong(int parameterIndex, long x) throws SQLException {  _stmt.setLong(parameterIndex,x);}
    public void setFloat(int parameterIndex, float x) throws SQLException {  _stmt.setFloat(parameterIndex,x);}
    public void setDouble(int parameterIndex, double x) throws SQLException {  _stmt.setDouble(parameterIndex,x);}
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {  _stmt.setBigDecimal(parameterIndex,x);}
    public void setString(int parameterIndex, String x) throws SQLException {  _stmt.setString(parameterIndex,x);}
    public void setBytes(int parameterIndex, byte x[]) throws SQLException {  _stmt.setBytes(parameterIndex,x);}
    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {  _stmt.setDate(parameterIndex,x);}
    public void setTime(int parameterIndex, java.sql.Time x) throws SQLException {  _stmt.setTime(parameterIndex,x);}
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException {  _stmt.setTimestamp(parameterIndex,x);}
    public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) 
        throws SQLException {  _stmt.setAsciiStream(parameterIndex,x,length);}

    /** @deprecated */
    public void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) 
        throws SQLException {  _stmt.setUnicodeStream(parameterIndex,x,length);}
    public void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) 
        throws SQLException{  _stmt.setBinaryStream(parameterIndex,x,length);}
    public void clearParameters() throws SQLException {  _stmt.clearParameters();}
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) 
        throws SQLException {  _stmt.setObject(parameterIndex, x, targetSqlType, scale);}
    public void setObject(int parameterIndex, Object x, int targetSqlType) 
        throws SQLException {  _stmt.setObject(parameterIndex, x, targetSqlType);}
    public void setObject(int parameterIndex, Object x) throws SQLException {  _stmt.setObject(parameterIndex, x);}
    public boolean execute() throws SQLException {  return _stmt.execute();}
    public void addBatch() throws SQLException {  _stmt.addBatch();}
    public void setCharacterStream(int parameterIndex, java.io.Reader reader, int length) 
        throws SQLException {  _stmt.setCharacterStream(parameterIndex,reader,length);}
    public void setRef (int i, Ref x) throws SQLException {  _stmt.setRef(i,x);}
    public void setBlob (int i, Blob x) throws SQLException {  _stmt.setBlob(i,x);}
    public void setClob (int i, Clob x) throws SQLException {  _stmt.setClob(i,x);}
    public void setArray (int i, Array x) throws SQLException {  _stmt.setArray(i,x);}
    public ResultSetMetaData getMetaData() throws SQLException {  return _stmt.getMetaData();}
    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) 
        throws SQLException {  _stmt.setDate(parameterIndex,x,cal);}
    public void setTime(int parameterIndex, java.sql.Time x, Calendar cal) 
        throws SQLException {  _stmt.setTime(parameterIndex,x,cal);}
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal) 
        throws SQLException {  _stmt.setTimestamp(parameterIndex,x,cal);}
    public void setNull (int paramIndex, int sqlType, String typeName) 
        throws SQLException {  _stmt.setNull(paramIndex,sqlType,typeName);}

    public void setNClob(int v,java.io.Reader r) throws SQLException {
        throw new SQLException( "not implemented" );
    }


    public void setBlob(int v,java.io.InputStream is) throws SQLException {
        throw new SQLException( "not implemented" );
        //_stmt.setBlob( v, is );
    }

    public boolean isPoolable() throws SQLException {
        return true;
    }
    
    public boolean getMoreResults(int current) throws SQLException {
        
        return _stmt.getMoreResults(current);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        
        return _stmt.getGeneratedKeys();
    }

    public int executeUpdate(String sql, int autoGeneratedKeys)
        throws SQLException {
        
        return _stmt.executeUpdate(sql, autoGeneratedKeys);
    }

    public int executeUpdate(String sql, int columnIndexes[])
        throws SQLException {
        
        return _stmt.executeUpdate(sql, columnIndexes);
    }

    public int executeUpdate(String sql, String columnNames[])
        throws SQLException {
        
        return _stmt.executeUpdate(sql, columnNames);
    }

    public boolean execute(String sql, int autoGeneratedKeys)
        throws SQLException {
        
        return _stmt.execute(sql, autoGeneratedKeys);
    }

    public boolean execute(String sql, int columnIndexes[])
        throws SQLException {
        
        return _stmt.execute(sql, columnIndexes);
    }

    public boolean execute(String sql, String columnNames[])
        throws SQLException {
        
        return _stmt.execute(sql, columnNames);
    }

    public int getResultSetHoldability() throws SQLException {
        
        return _stmt.getResultSetHoldability();
    }

    public void setURL(int parameterIndex, java.net.URL x)
        throws SQLException {
        
        _stmt.setURL(parameterIndex, x);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        
        return _stmt.getParameterMetaData();
    }

}
