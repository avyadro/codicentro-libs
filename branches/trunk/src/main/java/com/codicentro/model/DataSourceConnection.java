/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on May 19, 2008, 10:27:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: DataSourceConnection.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       May 19, 2008           Alexander Villalobos Yadr�      New class.
 **/
package com.codicentro.model;

import com.codicentro.utils.CDCException;
import com.codicentro.utils.ResourceBundleHandler;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DataSourceConnection {

    private Connection connection = null;
    private ResourceBundleHandler resourceBundleHandler = null;
    private String prefix = null;

    public DataSourceConnection(String source, String prefix)
            throws CDCException {
        if ((source == null) || (prefix == null)) {
            throw new CDCException("lng.msg.error.datasourceconnection");
        }

        this.prefix = prefix;
        try {
            resourceBundleHandler = new ResourceBundleHandler(source);
        } catch (Exception e) {
            throw new CDCException(e);
        }
    }

    public void rollback() throws CDCException {
        try {
            if (isOpen()) {
                connection.rollback();
            } else {
                throw new CDCException("Connection is close.");
            }
        } catch (Exception e) {
            throw new CDCException(e);
        }
    }

    public boolean open() throws CDCException {
        try {
            Context ctx = new InitialContext();
            if (ctx == null) {
                throw new CDCException("lng.msg.error.openconnection");
            }
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + resourceBundleHandler.getValue(new StringBuilder().append(prefix).append(".DSLookUp").toString()));
            connection = ((ds != null) ? ds.getConnection() : null);
        } catch (Exception e) {
            throw new CDCException(e);
        } finally {
            return isOpen();
        }
    }

    public boolean isOpen() throws CDCException {
        try {
            return (connection != null) && (!connection.isClosed());
        } catch (SQLException ex) {
            throw new CDCException(ex);
        }
    }

    public Connection getConnection() throws CDCException {
        if (isOpen()) {
            return connection;
        } else {
            throw new CDCException("Connection is close.");
        }
    }

    public void close() throws CDCException {
        try {
            if (isOpen()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new CDCException(e);
        } finally {
            connection = null;
            resourceBundleHandler = null;
            prefix = null;
        }
    }

    public CallableStatement prepareCall(String sql) throws CDCException {
        CallableStatement result = null;
        try {
            if (isOpen()) {
                result = connection.prepareCall(sql);
            } else {
                throw new CDCException("Connection is close.");
            }
        } catch (SQLException e) {
            throw new CDCException(e);
        }
        return result;
    }

    public PreparedStatement prepareStatement(String sql) throws CDCException {
        if (isOpen()) {
            try {
                return connection.prepareStatement(sql);
            } catch (SQLException ex) {
                throw new CDCException(ex);
            }
        } else {
            throw new CDCException("Connection is close.");
        }
    }

    public DatabaseMetaData getMetaData() throws CDCException {
        DatabaseMetaData result = null;
        try {
            if (isOpen()) {
                result = connection.getMetaData();
            } else {
                throw new CDCException("Connection is close.");
            }
        } catch (SQLException e) {
            throw new CDCException(e);
        }
        return result;
    }

    public void commit() throws CDCException {
        try {
            if (isOpen()) {
                connection.commit();
            } else {
                throw new CDCException("Connection is close.");
            }
        } catch (SQLException ex) {
            throw new CDCException(ex);
        }
    }

    public void setAutoCommit(boolean b) throws CDCException {
        try {
            if (isOpen()) {
                connection.setAutoCommit(b);
            } else {
                throw new CDCException("Connection is close.");
            }
        } catch (SQLException e) {
            throw new CDCException(e);
        }
    }
}