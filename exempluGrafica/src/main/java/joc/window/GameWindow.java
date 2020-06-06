package joc.window;

import joc.Main;
import joc.assets.SpriteSheet;
import joc.character.*;
import joc.item.Item;
import joc.listener.MovementListener;
import joc.map.Map;
import joc.panel.MenuPanel;
import joc.player.Player;
import joc.projectile.ArrowProjectile;
import joc.projectile.Projectile;
import joc.projectile.RockProjectile;
import joc.sql.Connection;
import joc.window.ui.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameWindow extends JFrame implements ActionListener {

    private static final String SPRITE_SHEET_DEFAULT_LOCATION = "/textures/SpriteSheet.png";

    private MenuPanel menuPanel;

    private int horizontalSpriteCount,verticalSpriteCount;

    private String mapToLoad;

//    private JPanel gamePanel;

    private int score = 0;

    private UI ui;

    private static GameWindow instance;
    private JPanel interiorPanel;

    public static GameWindow getInstance(){
        if(GameWindow.instance == null)
            GameWindow.instance = new GameWindow(40,20);
        return instance;
    }

    public void startMenu(){
        System.out.println(this.getFocusOwner());
        System.out.println(Arrays.toString(this.getKeyListeners()));
//        this.drawingCanvas.setVisible(false);

        this.menuPanel.setSize(100,100);


//        this.add(this.menuPanel);
//        this.menuPanel.setVisible(true);

//        this.startGame();
    }

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

    private List<Projectile> projectiles;

    public static final int DEFAULT_FPS = 60;

    private int fps = DEFAULT_FPS;

    private MovementListener movementListener;

    public GameWindow setMapToLoad(String mapToLoad) {
        this.mapToLoad = mapToLoad;
        return this;
    }

    private GameWindow(int horizontalSpriteCount, int verticalSpriteCount){
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

    public Player getPlayer() {
        return player;
    }

    public GameWindow setFps(int fps){
        this.fps = fps;
        return this;
    }

    public GameWindow buildComponents(){
//        this.add(interiorPanel);

        this.drawingCanvas=new Canvas();
        this.drawingCanvas.setPreferredSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setMaximumSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setMinimumSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setFocusable(false);

//        this.add(this.drawingCanvas);

        this.movementListener = new MovementListener();

        this.sheet = new SpriteSheet(SPRITE_SHEET_DEFAULT_LOCATION);

//        this.menuPanel = new MenuPanel();
        this.map = new Map(this.horizontalSpriteCount, this.verticalSpriteCount, this.sheet);

        this.loadMap();

        return this;
    }

    private void loadMap(){
//        System.out.println("incarcare");
        this.map.setKeyCount(Map.MAP_DEFAULT_KEY_COUNT);

//        joc.map.loadMap(Map.GAME_MAP_1);
        this.mapToLoad = this.map.getMapsList().get(0);
        map.loadMap(this.mapToLoad);
        map.getMapsList().remove(this.mapToLoad);

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

        this.projectiles = new ArrayList<>();

    }

    public GameWindow buildLayout(){
//        this.add(this.menuPanel);
        this.add(this.drawingCanvas);//am adaugat pe ce vom desena imaginile

//        this.menuPanel.setVisible(false);

//        this.interiorPanel.setVisible(false);
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

        //Map joc.map = new Map(this.horizontalSpriteCount, this.verticalSpriteCount, this.sheet);
        this.map.drawMap(graphics);

        for(Projectile r : this.projectiles)
            r.draw(graphics);

        for(Enemy e : this.enemies){
            e.draw(graphics);
        }
        this.player.draw(graphics);


        Item.getGameWorldItems().forEach(e->e.draw(graphics));

        this.ui.draw(graphics);

        bufferStrategy.show();
        graphics.dispose();//scapam de graficele astea
    }

    public void playerThrowsRock(){
        this.projectiles.add(
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

    public void enemyThrowsRock(int x, int y, double angle){
        this.projectiles.add(
                new RockProjectile(
                    x,
                    y,
                    angle,
                    this.sheet,
                    this.map,
                    this
                )
        );
    }

    public void enemyThrowsArrow(int x, int y, double angle){
        this.projectiles.add(
                new ArrowProjectile(
                        x,
                        y,
                        angle,
                        this.sheet,
                        this.map,
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

    public void startGame(){

//        this.setFocusable(true);
//        this.buildListeners();
//        System.out.println(Arrays.toString(this.getKeyListeners()));
        System.out.println(this.getFocusOwner());
//        System.out.println(this.paramString());
//        this.remove(this.menuPanel);
//        this.buildListeners();
//        this.requestFocus();
//        this.drawingCanvas.setVisible(true);
        this.revalidate();
        this.repaint();
//        this.pack();
        int sleepTimer = 1000/this.fps;

        Thread gameThread = new Thread(() -> {
            long startFrameTime = 0;
            long endFrameTime = 0;
            long frameTime = 0;
            int frameTimeMS;
            while (true) {
                startFrameTime = System.nanoTime();

                update();

                endFrameTime = System.nanoTime();

                frameTime = endFrameTime - startFrameTime;

                frameTimeMS = (int) (frameTime / 1000000);

                try {
                    //System.out.println(frameTimeMS);
                    if (sleepTimer >= frameTimeMS)
                        Thread.sleep(sleepTimer - frameTimeMS);
                } catch (InterruptedException exception) {
                    //System.out.println(exception);
                }
            }
        });

        gameThread.start();

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
        boolean exit = false;

        if(this.map.getMapsList().isEmpty())
            exit = true;

        if(this.player.getHeartsCount() <= 0)
            exit = true;

        if(exit)
            if(this.framesBeforeShutdown > 0)
                this.framesBeforeShutdown--;
            else {

                this.score += this.countHighScore();

                this.updateScoreIntoDatabase(Main.username);

                System.exit(0);  // TODO : du-te in alta fct
            }
        else {
            if(this.framesBeforeShutdown > 0)
                this.framesBeforeShutdown--;
            else {
                this.framesBeforeShutdown = FRAMES_BEFORE_SHUTDOWN_DEFAULT;

                this.score += this.countHighScore();
                this.loadMap();
            }
        }
    }

    private void countKeys(){
        this.ui.setKeyCount(this.player.getKeyCount());
    }

    public void update(){
//        System.out.println(this.joc.player.getKeyCount() + ", " + this.joc.map.getKeyCount());

        if(this.player.getKeyCount() >= this.map.getKeyCount())
            this.waitBeforeExit();

        if(this.player.getHeartsCount() > 0) {
            this.player.update(this.map);
        } else if(this.player.getHeartsCount() <= 0) {
            this.waitBeforeExit();
        }
        this.enemyAI.update();
        this.projectiles.removeIf(r -> !r.update());

        Item toBeRemoved = null;

//        Item.getGameWorldItems().forEach(e->{
//            if(e.isToBeDeSpawned()){
//                toBeRemoved = e;
//            } else
//                e.update();
//        });

        for(Item i : Item.getGameWorldItems()){
            if(i.isToBeDeSpawned())
                toBeRemoved = i;
            else {
                i.update();
            }
        }

        this.countKeys();

        if(toBeRemoved != null){
            Item.getGameWorldItems().remove(toBeRemoved);
        }

        this.ui.update();
        this.redraw();
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
    }
}
