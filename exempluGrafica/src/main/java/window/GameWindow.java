package window;

import assets.AssetList;
import assets.SpriteSheet;
import assets.tile.Tile;
import character.Enemy;
import character.EnemyAI;
import listener.MovementListener;
import map.Map;
import player.Player;
import projectile.ProjectileDirection;
import projectile.RockProjectile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class GameWindow extends JFrame {
    private int horizontalSpriteCount,verticalSpriteCount;
    private Map map;
    private Canvas drawingCanvas;//pe asta vom desena
    private Player player;
    private List<Enemy> enemies;
    private SpriteSheet sheet;
    private EnemyAI enemyAI;

    private List<RockProjectile> rockProjectiles;

    public static final int DEFAULT_FPS = 60;

    private int fps = DEFAULT_FPS;

    private MovementListener movementListener;

    public GameWindow(int horizontalSpriteCount,int verticalSpriteCount){
        this.horizontalSpriteCount=horizontalSpriteCount;
        this.verticalSpriteCount=verticalSpriteCount;
    }

    public GameWindow buildWindow(){
        this.setSize(horizontalSpriteCount* SpriteSheet.SPRITE_WIDTH,
                verticalSpriteCount*SpriteSheet.SPRITE_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);//nu ii poti modifica marimea
        this.setLocationRelativeTo(null);//e relativa la nimic
        this.setVisible(true);
        return this;

    }

    public GameWindow setFps(int fps){
        this.fps = fps;
        return this;
    }

    public GameWindow buildComponents(){
        this.drawingCanvas=new Canvas();
        this.drawingCanvas.setPreferredSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setMaximumSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setMinimumSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setFocusable(false);
        this.movementListener = new MovementListener();

        this.sheet = new SpriteSheet("/textures/PaooGameSpriteSheet4.png");

        this.map = new Map(this.horizontalSpriteCount, this.verticalSpriteCount, this.sheet);

        map.loadMap(Map.GAME_MAP_1);

        this.player = map.getPlayer();
        this.player.setMovementListener(this.movementListener);
        this.enemies = map.getEnemyList();

        for (Enemy e : this.enemies) {
            e.setPlayer(this.player);
        }

        this.player.setEnemies(this.enemies);

        this.enemyAI = EnemyAI.getInstance();
        this.enemyAI.setPlayer(this.player);
        this.enemyAI.setMap(this.map);
        this.enemyAI.setEnemies(this.enemies);

        this.rockProjectiles = new ArrayList<>();

        return this;
    }


    public GameWindow buildLayout(){
        this.add(this.drawingCanvas);//am adaugat pe ce vom desena imaginile
        this.pack();
        return this;
    }

    public GameWindow initialize(){
        BufferStrategy bufferStrategy=this.drawingCanvas.getBufferStrategy();

        if(bufferStrategy==null){
            this.drawingCanvas.createBufferStrategy(3);
            bufferStrategy=this.drawingCanvas.getBufferStrategy();
        }
        Graphics graphics=bufferStrategy.getDrawGraphics();
        graphics.clearRect(0,0,this.getWidth(),this.getHeight());//stergem tot

        map.drawMap(graphics);

        this.player.draw(graphics);

        bufferStrategy.show();
        graphics.dispose();//scapam de graficele astea

        return this;
    }

    public void redraw(){
        BufferStrategy bufferStrategy=this.drawingCanvas.getBufferStrategy();

        if(bufferStrategy==null){
            this.drawingCanvas.createBufferStrategy(3);
            bufferStrategy=this.drawingCanvas.getBufferStrategy();
        }
        Graphics graphics=bufferStrategy.getDrawGraphics();
        graphics.clearRect(0,0,this.getWidth(),this.getHeight());//stergem tot

        //Map map = new Map(this.horizontalSpriteCount, this.verticalSpriteCount, this.sheet);
        this.map.drawMap(graphics);

        for(RockProjectile r : this.rockProjectiles)
            r.draw(graphics);
        this.player.draw(graphics);

        for(Enemy e : this.enemies){
            e.draw(graphics);
        }

        bufferStrategy.show();
        graphics.dispose();//scapam de graficele astea
    }

    public void playerThrowsRock(){
        this.rockProjectiles.add(new RockProjectile((int)this.player.getX(), (int)this.player.getY(), this.player.getNextProjectileDirection(), this.sheet, this.map, this.player.getLocationStatus()));
    }

    public GameWindow buildListeners(){
        this.addKeyListener( this.movementListener );

        this.addKeyListener(
            new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyChar() == 'k')
                        playerThrowsRock();
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            }
        );
        //this.setFocusable(true);
        return this;
    }

    public synchronized void startGame(){

        int sleepTimer = 1000/this.fps;

        long startFrameTime = 0;
        long endFrameTime = 0;
        long frameTime = 0;
        int frameTimeMS;

        while(true) {

            startFrameTime = System.nanoTime();

            this.update();

            endFrameTime = System.nanoTime();

            frameTime = endFrameTime - startFrameTime;

            frameTimeMS = (int)(frameTime / 1000000);

            try {
                //System.out.println(frameTimeMS);
                if(sleepTimer >= frameTimeMS)
                    Thread.sleep(sleepTimer - frameTimeMS);
            } catch (InterruptedException exception) {
                //System.out.println(exception);
            }
        }

    }

    public void update(){
        this.player.update(this.map);
        this.enemyAI.update();
        this.rockProjectiles.removeIf(r-> !r.update());
        this.redraw();
    }

}
