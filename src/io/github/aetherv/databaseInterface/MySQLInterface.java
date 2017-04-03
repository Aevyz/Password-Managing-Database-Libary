package io.github.aetherv.databaseInterface;

import java.sql.*;
import java.util.Properties;

/**
 * Implementation of the ConnectionInterface for MYSQL databases (or derivatives thereof).
 * Tested on MariaDB servers.
 */
public class MySQLInterface implements ConnectionInterface {

    /**
     * A Connection that shall be used to generate Prepared Statements for querying.
     * Final since there should be no changes to which database we are sending the data to.
     */
    private final Connection connection;

    /**
     * Constructor that generates the Connection connection.
     * @param usr Username for logging into the database server.
     * @param pwd Password for logging into the database server.
     * @param url URL of the database server. If you want, you can already specify which specific database you wish to access, however this can be done during the querying as well.
     * @param port The port of the database
     * @throws SQLException Exceptions involving the login procedure. Includes invalid login data, invalid permissions and other errors.
     */
    public MySQLInterface(String usr, String pwd, String url, int port) throws SQLException {
        this.connection = createConnection(usr, pwd, url, port);
    }

    /**
     * Execute a given prepared statement, that returns no Result Set.
     * @param pStmt A prepared statement which shall be executed by this method.
     * @throws SQLException Exceptions involving the execution of the SQL query.
     */
    @Override
    public void executePreparedStatement(PreparedStatement pStmt) throws SQLException{
        pStmt.execute();
    }

    /**
     * Execute a given prepared statement, that returns a Result Set.
     * @param pStmt A prepared statement which shall be executed by this method.
     * @return A Result Set containing the data from the SQL query.
     * @throws SQLException Exceptions involving the execution of the SQL query.
     */
    @Override
    public ResultSet queryPreparedStatement(PreparedStatement pStmt) throws SQLException{
        return pStmt.executeQuery();
    }

    /**
     * Creates a connection to a database server, based on the specifications obtained via the parameters.
     * @param usr Username for logging into the database server.
     * @param pwd Password for logging into the database server.
     * @param url URL of the database server. If you want, you can already specify which specific database you wish to access, however this can be done during the querying as well.
     * @param port The port of the database
     * @return A Connection Object that contains the properties specified.
     * @throws SQLException Exceptions involving the login procedure. Includes invalid login data, invalid permissions and other errors.
     */
    @Override
    public Connection createConnection(String usr, String pwd, String url, int port) throws SQLException {
        Connection con;
        Properties conProp = new Properties();
        conProp.put("user", usr);
        conProp.put("password", pwd);

        con = DriverManager.getConnection(
                "jdbc:mysql://"+
                    url + ":"+
                    port + "/",
                    conProp
        );
        System.out.println("Connection Success");

        return con;
    }

    /**
     * Returns the current connection for use. Particularly helpful for generating prepared Statements.
     * @return Current Connection.
     * If no Connection is there, return null.
     * (Note: I am pretty doubtful that it is possible to generate a null in this example)
     */
    @Override
    public Connection getConnection() {
        return connection;
    }
}
