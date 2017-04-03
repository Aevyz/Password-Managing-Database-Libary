package io.github.aetherv.databaseInterface;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An Interface that specifies how a particular Database shall interact with the API.
 * As an example, MySQLInterface, implements this Interface for a mysql based server
 */
public interface ConnectionInterface {

    /**
     * Execute a prepared Statement that does not return a Result Set.
     * An example of which would be the INSERT based SQL query.
     * @param pStmt A prepared statement which shall be executed by this method
     * @throws SQLException An exception pertaining to flaws in the given Prepared Statement's SQL syntax.
     */
    void executePreparedStatement(PreparedStatement pStmt) throws SQLException;

    /**
     * Execute a prepared Statement that should return a result set.
     * An example would be a SELECT based SQL query.
     * @param pStmt A prepared statement which shall be executed by this method.
     * @return A Result Set containing the data from the SQL query.
     * @throws SQLException An exception pertaining to flaws in the given Prepared Statement's SQL syntax.
     */
    ResultSet queryPreparedStatement(PreparedStatement pStmt) throws SQLException;

    /**
     * Creates a connection to a database server, based on the specifications obtained via the parameters.
     * @param usr Username for logging into the database server.
     * @param pwd Password for logging into the database server.
     * @param url URL of the database server. If you want, you can already specify which specific database you wish to access, however this can be done during the querying as well.
     * @param port The port of the database
     * @return A Connection Object that contains the properties specified.
     * @throws SQLException Exceptions involving the login procedure. Includes invalid login data, invalid permissions and other errors.
     */
    Connection createConnection(String usr, String pwd, String url, int port) throws SQLException;

    /**
     * Returns the current connection for use. Particularly helpful for generating prepared Statements.
     * @return Current Connection.
     * If no Connection is present, return null.
     */
    Connection getConnection();

}
