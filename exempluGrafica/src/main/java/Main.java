import listener.MovementListener;
import window.GameWindow;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class Main {
    public static void main(String[]args){

        Main.startApp();

        //gameWindow.startGame();
    }

    public static void startApp(){

        GameWindow gameWindow=new GameWindow(40,20)
                .buildWindow()
                .buildComponents()
                .buildLayout()
                .initialize()
                .buildListeners()
                .setFps(60);

        gameWindow.startGame();
    }
}
