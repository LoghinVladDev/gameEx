package joc.sql;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {

    private java.sql.Connection connection;
    private static Connection instance;

    /**
     * Singleton getter
     * @return
     */
    public static Connection getInstance(){
        if(instance == null)
            instance = new Connection();
        return instance;
    }

    private Connection(){
        this.connection = null;
    }

    /**
     * conecteaza la db
     * @return
     */
    public Connection connect(){
        try{
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "/src/main/resources/db/gamedb.db" );
        } catch (SQLException e){
            e.printStackTrace();
        }

        return this;
    }

    /**
     * returneaza conexiunea propriu-zisa
     * @return
     */
    public java.sql.Connection getConnection(){
        return this.connection;
    }

    /**
     * disconnect
     */
    public void disconnect(){
        try {
            this.connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}
