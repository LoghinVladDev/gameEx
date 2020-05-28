package sql;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Connection {
    public static final String DB_PATH = "jdbc:sqlite:C:/Git/java/proj/exempluGrafica/src/main/resources/db/gamedb.db";

    private java.sql.Connection connection;
    private static Connection instance;

    public static Connection getInstance(){
        if(instance == null)
            instance = new Connection();
        return instance;
    }

    private Connection(){
        this.connection = null;
    }

    public Connection connect(){
        try{
            this.connection = DriverManager.getConnection(DB_PATH);
        } catch (SQLException e){
            e.printStackTrace();
        }

        return this;
    }

    public java.sql.Connection getConnection(){
        return this.connection;
    }

    public void disconnect(){
        try {
            this.connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}
