package window;

import assets.AssetList;
import assets.SpriteSheet;
import assets.tile.Tile;
import character.*;
import listener.MovementListener;
import map.Map;
import player.Player;
import projectile.ProjectileDirection;
import projectile.RockProjectile;
import sql.Connection;
import window.ui.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameWindow extends JFrame {

    private static final String SPRITE_SHEET_DEFAULT_LOCATION = "/textures/SpriteSheet.png";

    private int horizontalSpriteCount,verticalSpriteCount;

    private int score = 0;

    private UI ui;

    private Map map;
    private Canvas drawingCanvas;//pe asta vom desena
    private Player player;
    private List<Enemy> enemies;
    private SpriteSheet sheet;
    private EnemyAI enemyAI;

    private static final int FRAMES_BEFORE_SHUTDOWN_DEFAULT = 300;

    private int framesBeforeShutdown = FRAMES_BEFORE_SHUTDOWN_DEFAULT;

    public List<Enemy> getEnemies() {
        return enemies;
    }

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

        this.sheet = new SpriteSheet(SPRITE_SHEET_DEFAULT_LOCATION);

        this.map = new Map(this.horizontalSpriteCount, this.verticalSpriteCount, this.sheet);

        map.loadMap(Map.GAME_MAP_1);

        this.player = map.getPlayer();
        this.player.setMovementListener(this.movementListener);
        this.enemies = map.getEnemyList();

        this.ui = new UI(this, this.sheet).setPlayer(this.player);

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

        this.ui.draw(graphics);

        bufferStrategy.show();
        graphics.dispose();//scapam de graficele astea
    }

    public void playerThrowsRock(){
        this.rockProjectiles.add(
                new RockProjectile(
                        (int)this.player.getX(),
                        (int)this.player.getY(),
                        this.player.getNextProjectileDirection(),
                        this.sheet,
                        this.map,
                        this.player.getLocationStatus(),
                        this
                )
        );
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

    private int countHighScore(){
        int score = 0;

        for(Enemy e : this.enemyAI.getDeadEnemies()){
            try{
                RegularEnemy e1 = (RegularEnemy) e;

                if(e1.isDead())
                    score += 100;

                System.out.println(score);

            } catch ( ClassCastException ignored ){

                try{

                    RockThrowerEnemy e2 = (RockThrowerEnemy) e;

                    if(e2.isDead())
                        score += 50;
                    System.out.println(score);

                } catch ( ClassCastException ignored1 ){

                    try{

                        ArcherEnemy e3 = (ArcherEnemy) e;

                        if(e3.isDead())
                            score += 150;
                        System.out.println(score);

                    } catch ( ClassCastException ignored2 ){

                    }

                }

            }
        }

        return score;
    }

    private void updateScoreIntoDatabase(String playerName){
        try{

            PreparedStatement sqlStatement = Connection
                    .getInstance()
                    .connect()
                    .getConnection()
                    .prepareStatement("INSERT INTO high_scores (score_holder, score) values (?, ?)");

            sqlStatement.setString(1, playerName);
            sqlStatement.setInt(2, this.score);

            sqlStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void waitBeforeExit(){
        if(this.framesBeforeShutdown > 0)
            this.framesBeforeShutdown--;
        else {

            this.score = this.countHighScore();

            this.updateScoreIntoDatabase("Larisa");

            System.exit(0);
        }
    }

    public void update(){
        if(this.player.getHeartsCount() > 0) {
            this.player.update(this.map);
        } else {
            this.waitBeforeExit();
        }
        this.enemyAI.update();
        this.rockProjectiles.removeIf(r -> !r.update());
        this.ui.update();
        this.redraw();
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
