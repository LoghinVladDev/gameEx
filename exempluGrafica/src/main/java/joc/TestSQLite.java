package joc;

import joc.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TestSQLite {
    public static boolean thread1Out = true;
    public static int i = 5;
    public static void main(String[] args) {
        Thread t1 = new Thread(){
            private int output = 5;
            public void run(){
                while(output > 0){

                    while(!thread1Out);

                    output--;
                    System.out.println(i);
                    thread1Out = false;
                }
            }
        };
        Thread t2 = new Thread() {
            private int output = 5;

            public void run() {
                while (output > 0) {

                    while(thread1Out);

                    output--;
                    i++;

                    thread1Out = true;
                }
            }
        };

        t1.start();
        t2.start();
//        try{
//            Thread.sleep(1000);
//        } catch (InterruptedException ignored){
//
//        }
        System.out.println("main");
    }

}
