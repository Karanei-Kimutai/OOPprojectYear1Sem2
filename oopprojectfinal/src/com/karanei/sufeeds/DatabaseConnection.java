//Karanei Kimutai Michael,ICS,183523,04/11/24
package com.karanei.sufeeds;

//Importing libraries to:help connect the app to the database and handle any exception that may occur in the process
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/db_michael_karanei_183523";
    //Default database username and password
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

/*Random copyright comment-"And then it's lights out at UTOPIA, that's how you know it's us"*/
