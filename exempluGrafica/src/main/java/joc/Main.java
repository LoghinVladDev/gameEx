package joc;

import joc.map.Map;
import joc.window.GameWindow;
import joc.window.MenuWindow;

import java.awt.*;

public class Main {

    public static String username;

    public static void main(String[]args){

        Main.startMenu();

//        Main.startApp();

        //gameWindow.startGame();
    }

    public static void startMenu(){
        MenuWindow window = new MenuWindow().buildComponents().buildLayout().buildListeners();

        window.setVisible(true);
    }

    public static void startApp(){

        GameWindow gameWindow=GameWindow.getInstance()
                .buildWindow()
                .buildComponents()
                .buildLayout()
                .initialize()
                .buildListeners()
                .setFps(60);

//        gameWindow.startMenu();

        gameWindow.toFront();
        gameWindow.setVisible(true);
        gameWindow.setFocusable(true);
        gameWindow.requestFocus();

        gameWindow.startGame();
    }
}
