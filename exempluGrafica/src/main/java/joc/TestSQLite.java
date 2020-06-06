package joc;

import joc.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestSQLite {
    public static void main(String[] args) {
        Connection.getInstance().connect();

        try{
            PreparedStatement statementSelect = Connection
                    .getInstance()
                    .getConnection()
                    .prepareStatement("SELECT * FROM high_scores");

            ResultSet rs = statementSelect.executeQuery();

            while(rs.next()){
                System.out.println(rs.getInt(1) + ", " + rs.getString(2) + ", " + rs.getInt(3));
            }

        } catch ( SQLException e){
            e.printStackTrace();
        }

        Connection.getInstance().disconnect();
    }

}
