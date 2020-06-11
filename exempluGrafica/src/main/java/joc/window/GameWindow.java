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

    /**
     * Singleton. O singura fereastra de joc
     * @return
     */
    public static GameWindow getInstance(){
        if(GameWindow.instance == null)
            GameWindow.instance = new GameWindow(40,20);
        return instance;
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

    /**
     * ctor private. singleton nu poate fi construit direct
     * @param horizontalSpriteCount
     * @param verticalSpriteCount
     */
    private GameWindow(int horizontalSpriteCount, int verticalSpriteCount){
        this.horizontalSpriteCount=horizontalSpriteCount;
        this.verticalSpriteCount=verticalSpriteCount;
    }

    /**
     * constr. fereastra
     * @return
     */
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

    /**
     * constr componentele din fereastra
     * @return
     */
    public GameWindow buildComponents(){
//        this.add(interiorPanel);

        this.drawingCanvas=new Canvas();
        this.drawingCanvas.setPreferredSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setMaximumSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setMinimumSize(new Dimension(this.getWidth(),this.getHeight()));
        this.drawingCanvas.setFocusable(false);

        this.movementListener = new MovementListener();
        this.sheet = new SpriteSheet(SPRITE_SHEET_DEFAULT_LOCATION);
        this.map = new Map(this.horizontalSpriteCount, this.verticalSpriteCount, this.sheet);

        this.loadMap();

        return this;
    }

    /**
     * incarca urmatoarea harta
     */
    private void loadMap(){
        this.map.setKeyCount(Map.MAP_DEFAULT_KEY_COUNT);
        this.mapToLoad = this.map.getMapsList().get(0);
        map.loadMap(this.mapToLoad);
        map.getMapsList().remove(this.mapToLoad);

        this.player = map.getPlayer(); // ia player-ul din harta
        this.player.setMovementListener(this.movementListener);
        this.enemies = map.getEnemyList(); //ia inamicii din harta

        this.ui = new UI(this, this.sheet).setPlayer(this.player); // fa un UI pe baza player-ului (0 chei, 3 inimi)

        for (Enemy e : this.enemies) {
            e.setPlayer(this.player); // pt fiecare inamic, da-i player-ul
        }

        this.player.setEnemies(this.enemies); // da-i player-ului lista de inamici

        this.enemyAI = EnemyAI.getInstance(); // controller inamici  (singleton)
        this.enemyAI.setPlayer(this.player); // da-i lista inamici
        this.enemyAI.setMap(this.map); //da-i harta
        this.enemyAI.setEnemies(this.enemies); // da-i inamicii

        this.projectiles = new ArrayList<>(); // creaza  o noua lista cu proj

    }

    public GameWindow buildLayout(){
        this.add(this.drawingCanvas);//am adaugat pe ce vom desena imaginile
        this.pack();
        return this;
    }

    /**
     * init fereastra
     * @return
     */
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

    /**
     * redesenare toate obiecte
     */
    public void redraw(){
        BufferStrategy bufferStrategy=this.drawingCanvas.getBufferStrategy(); // buffering = stocarea de imagini in viitor pentru a afisa in o modalitate usoara. Desenare pe "pagini" (buffere)

        if(bufferStrategy==null){
            this.drawingCanvas.createBufferStrategy(3); // 3 buffere = 3 imagini in avans fata de cea de pe ecran
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

    /**
     * apelata pt a crea o piatra aruncata de jucator
     */
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

    /**
     * apelata pt piatra inamic
     * @param x
     * @param y
     * @param angle
     */
    public void enemyThrowsRock(int x, int y, double angle){
        /// factory + builder pattern (creational pattern)
        this.projectiles.add(
                new Projectile.ProjectileFactory()
                        .withWindow(this)
                        .withStartingPosition(x , y)
                        .withAngle(angle)
                        .withMap(this.map)
                        .withSheet(this.sheet)
                        .buildRock()
        );
    }

    /**
     * apelata pt sageata inamic
     * @param x
     * @param y
     * @param angle
     */
    public void enemyThrowsArrow(int x, int y, double angle){
        /// factory + builder pattern (creational pattern)
        this.projectiles.add(
                new Projectile.ProjectileFactory()
                    .withWindow(this)
                    .withStartingPosition(x , y)
                    .withAngle(angle)
                    .withMap(this.map)
                    .withSheet(this.sheet)
                    .buildArrow()
        );
    }

    /**
     * listenerele de taste
     * @return
     */
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
        return this;
    }

    /**
     * porneste jocul
     */
    public void startGame(){

        this.revalidate();
        this.repaint();

        int sleepTimer = 1000/this.fps; // durata unui cadru = 1000ms / cate cadre

        // thread-ul de logica a jocului ( update-uri logice, merge in paralel cu afisarea pe ecran)
        Thread gameThread = new Thread(
            () -> {
                long startFrameTime = 0;
                long endFrameTime = 0;
                long frameTime = 0;
                int frameTimeMS;
                while (true) { /// while (true) = infinite loop
                    startFrameTime = System.nanoTime();

                    update(); // toate instructiunile jocului

                    endFrameTime = System.nanoTime();

                    frameTime = endFrameTime - startFrameTime;

                    frameTimeMS = (int) (frameTime / 1000000); // cat a durat cadrul in MS

                    try {
                        //System.out.println(frameTimeMS);
                        if (sleepTimer >= frameTimeMS) // daca durata cadr. < cat ar trebui sa dureze
                            Thread.sleep(sleepTimer - frameTimeMS); // asteapta restul timpului
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        );

        gameThread.start();

    }

    /**
     * cat scor avem la final
     * @return
     */
    private int countHighScore(){
        int score = 0;

        for(Enemy e : this.enemyAI.getDeadEnemies()){
            try{
                RegularEnemy e1 = (RegularEnemy) e;

                if(e1.isDead())
                    score += 100;

//                System.out.println(score);

            } catch ( ClassCastException ignored ){

                try{

                    RockThrowerEnemy e2 = (RockThrowerEnemy) e;

                    if(e2.isDead())
                        score += 50;
//                    System.out.println(score);

                } catch ( ClassCastException ignored1 ){

                    try{

                        ArcherEnemy e3 = (ArcherEnemy) e;

                        if(e3.isDead())
                            score += 150;
//                        System.out.println(score);

                    } catch ( ClassCastException ignored2 ){

                    }

                }

            }
        }

        return score;
    }

    /**
     * inserarea scor in BD
     * @param playerName
     */
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

    /**
     * asteapta cateva cadre pana iesi / pana incarci harta
     */
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

    /**
     * update-ul obiectelor
     */
    public void update(){

        if(this.player.getKeyCount() >= this.map.getKeyCount()) // avem cate chei avem nevoie ca sa finalizam nivelul ?
            this.waitBeforeExit();

        if(this.player.getHeartsCount() > 0) { // este player alive?
            this.player.update(this.map); // update player
        } else if(this.player.getHeartsCount() <= 0) { // a murit?
            this.waitBeforeExit();
        }
        this.enemyAI.update(); // update each enemy
        this.projectiles.removeIf(r -> !r.update()); // update each projectile and remove if destroyed

        Item toBeRemoved = null; // itemele din joc, update pe fiecare si scoate-l pe cel luat de jucator

        for(Item i : Item.getGameWorldItems()){
            if(i.isToBeDeSpawned())
                toBeRemoved = i;
            else {
                i.update();
            }
        }

        this.countKeys(); //pune keys pe UI

        if(toBeRemoved != null){
            Item.getGameWorldItems().remove(toBeRemoved);
        }

        this.ui.update(); // update UI
        this.redraw(); // redeseneaza tot
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        System.out.println(e);
    }
}
