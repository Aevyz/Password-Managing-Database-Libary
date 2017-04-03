package io.github.aetherv.demo;

import io.github.aetherv.databaseInterface.ConnectionInterface;
import io.github.aetherv.databaseInterface.MySQLInterface;
import io.github.aetherv.encryption.Encryption;
import io.github.aetherv.exceptions.AlreadyRegisteredException;
import io.github.aetherv.exceptions.FieldNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Connection to a specific database to login and register. This is a demo and should be treated as such. It is likely that there are mistakes in here, that may cause vulnerabilities. It should serve as a general idea of how you could possibly implement the following methods.
 <br>
 <br>
 <br>  Created using the following commands. The user we shall be using only has access to the Insert and Select functions (for security)
 <br>       USER: "demouser"
 <br>       PASS: "demopassword"
 <br>
 <br>  1)
 <br>    create database demowebsite;
 <br>  2)
 <br>     CREATE TABLE demowebsite.userdb (
 <br>         ID INT NOT NULL AUTO_INCREMENT,
 <br>         Username VARCHAR(30) NOT NULL,
 <br>         Email VARCHAR(30) NOT NULL,
 <br>         Firstname VARCHAR(30) NOT NULL,
 <br>         Surname VARCHAR(30) NOT NULL,
 <br>         Birthday DATE NOT NULL,
 <br>         PasswordHash VARCHAR(400) NOT NULL,
 <br>         Salt VARCHAR(64) NOT NULL,
 <br>         PRIMARY KEY (ID)
 <br>     );
 */

public class DemoLoginRegister {

    /**
     <br>      Main method. Contains a database login sequence, followed by choice between login and register
     <br>      Login:
     <br>          Checks if the data would allow for a login and returns the authentication status via console
     <br>      Register:
     <br>           Registers yourself in the DB presuming user and emails are unique.
     * @param args Does nothing so just leave blank
     */
    public static void main (String [] args){
        try {
            System.out.println("Login into DB");
            System.out.println("Default: demouser; demopass; localhost; 3306");
            DemoLoginRegister DLR = new DemoLoginRegister(
                new MySQLInterface(getString("Username"), getString("Password"), getString("URL"), Integer.parseInt(getString("Port"))),
                new DemoEncryption(),
                getString("Table That Should Be Used (Default: demowebsite.userdb)")
            );
            while(true){

                try (Scanner sc = new Scanner(System.in)) {
                    System.out.println("Login:\t\t True\nRegister:\t False");
                    if (sc.nextBoolean()) new login(DLR, sc);
                    else new register(DLR, sc);
                    System.out.println("Continue:\tTrue\nEnd:\t\tFalse");
                    if (!sc.nextBoolean()) break;
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Help Method For Main and subclasses
     * @param question String which contains the question that shall be asked before awaiting a response (Scanner.next())
     * @return User Input via Scanner.next()
     */
    private static String getString(String question){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter "+question);return sc.next();
    }
    /**
     * Helping class for login procedures
     */
    private static class login{
        final Scanner sc;
        final DemoLoginRegister dlr;

        login(DemoLoginRegister dlr, Scanner sc) throws FieldNotFoundException, SQLException{
            this.dlr = dlr;
            this.sc=sc;
            if(dlr.login(getString("Username"), getString("Password"))) System.out.println("Login Success");
            else System.out.println("Authentication Fail");
        }
    }

    /**
     * Helping class for registration
     */
    static class register{
        Scanner sc;
        final DemoLoginRegister dlr;

        register(DemoLoginRegister dlr, Scanner sc) throws FieldNotFoundException, SQLException, AlreadyRegisteredException {
            this.dlr = dlr;
            this.sc = sc;
            System.out.println("Due to me not bothering to code the order, please enter your birthday first\nEnter birthday \nFirst Year, Then Month, Then Day");


            //Probably the simplest way of doing this, although it is not good practice
            @SuppressWarnings("deprecation") Date birthday = new Date(sc.nextInt()-1900, sc.nextInt()-1, sc.nextInt());
            dlr.register(getString("Username"), getString("Email"), getString("First Name"), getString("Last Name"), birthday, getString("Password"));
        }
    }


    private final Encryption encrypt;
    private final Connection con;
    private final ArrayList<String> fields;

    private final String table;
    private final String[] peppers = new String[]{"P1", "e2", "p3", "p4", "e5", "r6", "s7"};

    /**
     * Init. variables
     * @param server What kind of server shall we be using. Shall be an Object implementing ConnectionInterface
     * @param encrypt What Encryption algorithm shall we be using. Shall be an Object implementing Encryption
     * @param table Which table shall we use in the database. Either the db shall be specified in the URL or you specify it in the table.
     * @throws SQLException Most likely SQL Database Login Exception.
     */
    public DemoLoginRegister(ConnectionInterface server, Encryption encrypt, String table) throws SQLException {
        this.encrypt = encrypt;
        this.table = table;
        this.con = server.getConnection();
        this.fields=fields();
    }

    /**
     * Checks and verifies if the username and password is found in the specified table
     * @param usr Username of person to be logged in
     * @param pwd Password of person to be logged in
     * @return If the data matches that of the usr in the database, return true, else false
     * @throws SQLException Probably an error amongst the lines of, user not found in the table
     * @throws FieldNotFoundException Field specified,in this case "Username" does not exist in the table, aka you messed up the setup of the server
     */
    public boolean login (String usr, String pwd) throws SQLException, FieldNotFoundException {
        String salt = find(usr, "Username", "Salt");
        String hash = find(usr, "Username", "PasswordHash");
        return encrypt.compareHashToPassword(hash, pwd, salt, peppers);
    }

    /**
     * Registers a user into the table using the Insert SQL command.
     * @param username The Username by which the user wished to be known by.
     * @param email The users email address.
     * @param firstname The users first name.
     * @param lastname The users surname.
     * @param birthday The users birthday.
     * @param password The users password.
     * @throws SQLException SQL error regarding either the Insertion of data into the table.
     * @throws FieldNotFoundException Table misconfiguration. The fields "Username" or "Email" do not exist in the table
     * @throws AlreadyRegisteredException The given username or email are already found within the table.
     */
    public void register(String username, String email, String firstname, String lastname, Date birthday, String password) throws SQLException, FieldNotFoundException, AlreadyRegisteredException {
        if(!hasDuplicates(username, "Username")) throw new AlreadyRegisteredException(username, "Username");
        if(!hasDuplicates(email, "Email")) throw new AlreadyRegisteredException(email, "Email");

        String salt = encrypt.genSalt();
        String prepString = encrypt.prepareString(password, salt, peppers);
        String hash = encrypt.encrypt(prepString);

        String sql = String.format("INSERT INTO %s VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?);", table);
        PreparedStatement pStmt = con.prepareStatement(sql);

        pStmt.setString(1, username);
        pStmt.setString(2, email);
        pStmt.setString(3, firstname);
        pStmt.setString(4, lastname);
        pStmt.setDate(5, birthday);
        pStmt.setString(6, hash);
        pStmt.setString(7, salt);

        pStmt.execute();
    }

    /**
     * Help method that lists all fields of the table. This is to help prevent SQL Injects, hopefully :D
     * @return An ArrayList containing all the fields of the table, in the correct order.
     * @throws SQLException Should not be triggered, but if it does, it is most likely because the table is empty or doesn't exist.
     */
    private ArrayList<String> fields() throws SQLException {
        PreparedStatement pStmt = con.prepareStatement("SHOW COLUMNS FROM "+table);
        ResultSet rs = pStmt.executeQuery();
        ArrayList<String> r = new ArrayList<>();

        while(rs.next()){
            r.add(rs.getString(1));
        }
        return r;
    }

    /**
     * Help method that checks whether or not a given String is found within a field.
     * @param toSearch The String that should be verified, whether or not it is unique to a field.
     * @param Field The Field in which should be checked.
     * @return True if the String toSearch is unique within its field
     * @throws SQLException Some sort of SQL error, probably involving data types. (If this is triggered at all)
     * @throws FieldNotFoundException Field is not found within the table
     */
    private boolean hasDuplicates(String toSearch, String Field) throws SQLException, FieldNotFoundException{
        String sqlQuery = String.format("SELECT COUNT(*) FROM %s WHERE `%s`=?;", table, Field);
        if (!this.fields.contains(Field)) throw new FieldNotFoundException(Field, this.fields);
        PreparedStatement pStmt= con.prepareStatement(sqlQuery);
        pStmt.setString(1, toSearch);

        ResultSet rs = pStmt.executeQuery();
        rs.next();

        return rs.getInt("COUNT(*)")==0;
    }

    //TODO craft a better explanation
    /**
     * Help method that finds a given field based off of a unique identifier. IMPORTANT: THIS METHOD DOES NOT CHECK IF THE FIELD IS A UNIQUE IDENTIFIER, IT WILL SIMPLY RETURN THE TOPMOST VALUE. As I suck at explaining: Say you wish to find the username based off of an email address, find("dory@finding.com", "Email", "Username") would do that.
     * @param identifier A unique identifier of the data, ie. an Email, User or Primary Key
     * @param idField A unique value that is present within the identifier.
     * @param contentField The content that you wish to find.
     * @return Given data of content field.
     * @throws FieldNotFoundException Field that you entered does not exist in the table.
     * @throws SQLException An Error based off of the SQL method being invalid.
     */
    private String find(String identifier, String idField, String contentField) throws FieldNotFoundException, SQLException {
        String sqlQuery = String.format("SELECT %s FROM %s WHERE `%s`=?;", contentField, table, idField);
        if (!this.fields.contains(idField)) throw new FieldNotFoundException(idField, this.fields);
        if (!this.fields.contains(contentField)) throw new FieldNotFoundException(contentField, this.fields);
        PreparedStatement pStmt= con.prepareStatement(sqlQuery);
        pStmt.setString(1, identifier);

        ResultSet rs = pStmt.executeQuery();
        rs.next();
        return rs.getString(contentField);

    }
}
