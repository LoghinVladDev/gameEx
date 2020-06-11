package joc.player;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.character.*;
import joc.listener.MovementListener;
import joc.map.Map;
import joc.projectile.ProjectileDirection;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Obiectul jucator
 */
public class Player {
    boolean combinedMovement = false;

    private int keyCount = 0;

    /**
     * Campuri detectie inamici
     */
    private static final int REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS = 300;
    private static final int ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS = 600;
    private static final int ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS = 1000;

    private BufferedImage playerSpriteLeft;
    private BufferedImage playerSpriteRight;
    private final float FRAME_PLAYER_MOVE_SPEED = 5;
    private final float FRAME_PLAYER_COLLIDE_SPEED = 5;

    private int heartsCount = 3;

    /**
     * Returneaza nr "vieti" ale jucatorului
     * @return heartsCount
     */
    public int getHeartsCount() {
        return heartsCount;
    }

    private MovementListener movementListener;

    private List<Enemy> enemies;

    private float actualPlayerSpeed = FRAME_PLAYER_MOVE_SPEED;

    private PlayerStatus locationStatus = PlayerStatus.PLAYER_NO_COLLIDE;

    /**
     * Ne spune unde se afla player-ul, relativ la ce? => e in apa, e langa un perete etc.
     * @return status locatie
     */
    public PlayerStatus getLocationStatus() {
        return locationStatus;
    }

    private ProjectileDirection projectileDirection = ProjectileDirection.RIGHT;

    private List<HumanoidEnemy> surroundingEnemies;

    /**
     * Returneaza o lista cu inamicii in range de 1 patratel
     * @return Lista de inamici
     */
    public List<HumanoidEnemy> getSurroundingEnemies() {
        return surroundingEnemies;
    }

    /**
     * Ne spune incotro tinteste player-ul
     * @return directia urm. proiectil
     */
    public ProjectileDirection getNextProjectileDirection() {
        return projectileDirection;
    }

    private boolean dead = false;

    private float x;
    private float y;

    /**
     * Getter locatie horiz.
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * Getter locatie vertical
     * @return y
     */
    public float getY() {
        return y;
    }

    /**
     * Isi ia o lovitura. Daca nu mai are vieti, die
     */
    public void getHit(){
        this.heartsCount--;
        if(this.heartsCount <= 0)
            this.dead = true;
    }

    /**
     * este player-ul mort
     * @return true daca da, false daca nu
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Ctor player
     * @param x pozitie horiz start
     * @param y pozitie vert start
     * @param spriteSheet spritesheet-ul de pe care sa-si ia grafica
     */
    public Player(float x, float y, SpriteSheet spriteSheet){
        this.x = x;
        this.y = y;
        this.playerSpriteLeft = spriteSheet.getAsset(AssetList.PLAYER_LEFT);
        this.playerSpriteRight = spriteSheet.getAsset(AssetList.PLAYER_RIGHT);
        this.surroundingEnemies = new ArrayList<>();
    }

    /**
     * Setter pt lista de inamici
     * @param enemies lista inamici
     */
    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    /**
     * true = spirte right
     * false  = sprite left
     */
    private boolean facingRight = true;

    /**
     * Setter pt listener-ul de miscare (creat in GameWindow)
     * @param movementListener listener
     */
    public void setMovementListener(MovementListener movementListener) {
        this.movementListener = movementListener;
    }

    /**
     * Redesena jucatorul
     * @param g unde il desenam
     */
    public void draw(Graphics g){
        g.drawImage(
                facingRight ? this.playerSpriteRight : this.playerSpriteLeft,
                (int)this.x,
                (int)this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT,
                null
        );

        Ellipse2D regularEnemyDetectionCircle = new Ellipse2D.Float(
                this.x-(float) REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                this.y- (float)REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS,
                REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS
        );

        Ellipse2D rockThrowerEnemyDetectionCircle = new Ellipse2D.Float(
                this.x - (float) ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                this.y- (float)ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS,
                ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS
        );
        Ellipse2D archerDetectionCircle = new Ellipse2D.Float(
                this.x - (float) ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                this.y- (float)ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS,
                ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS
        );

        // decomenteaza pentru a vedea cercurile de detectie ale inamicilor
//        g.setColor(Color.GREEN);
//        g.drawOval(
//                (int)(this.x-(float) REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS / 2),
//                (int)(this.y- (float)REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS / 2),
//                REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS,
//                REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS);
//
//        g.setColor(Color.BLUE);
//        g.drawOval(
//                (int)(this.x - (float) ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS / 2),
//                (int)(this.y- (float)ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS / 2),
//                ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS,
//                ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS);
//        g.setColor(Color.RED);
//        g.drawOval(
//                (int)(this.x-(float) ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS / 2),
//                (int)(this.y- (float)ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS / 2),
//                ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS,
//                ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS);
    }

    /**
     * Pe baza tastelor apasate, calculeaza directia in care se misca player-ul
     * @return directie
     */
    public Directions getInputDirection(){
        if(this.movementListener.isUp() && !this.movementListener.isDown() && !this.movementListener.isLeft() && !this.movementListener.isRight())
            return Directions.UP;
        if(this.movementListener.isUp() && !this.movementListener.isDown() && !this.movementListener.isLeft() && this.movementListener.isRight())
            return Directions.RIGHT_UP;
        if(!this.movementListener.isUp() && !this.movementListener.isDown() && !this.movementListener.isLeft() && this.movementListener.isRight())
            return Directions.RIGHT;
        if(!this.movementListener.isUp() && this.movementListener.isDown() && !this.movementListener.isLeft() && this.movementListener.isRight())
            return Directions.RIGHT_DOWN;
        if(!this.movementListener.isUp() && this.movementListener.isDown() && !this.movementListener.isLeft() && !this.movementListener.isRight())
            return Directions.DOWN;
        if(!this.movementListener.isUp() && this.movementListener.isDown() && this.movementListener.isLeft() && !this.movementListener.isRight())
            return Directions.LEFT_DOWN;
        if(!this.movementListener.isUp() && !this.movementListener.isDown() && this.movementListener.isLeft() && !this.movementListener.isRight())
            return Directions.LEFT;
        if(this.movementListener.isUp() && !this.movementListener.isDown() && this.movementListener.isLeft() && !this.movementListener.isRight())
            return Directions.LEFT_UP;
        return Directions.NOTHING;
    }

    /**
     * Update-ul de coordonate / actiuni / coliziune a jucatorului
     * @param map harta, folosita la coliziuni
     */
    public void update(Map map){
        Directions playerOrientation = getInputDirection(); // incotro se misca

        this.getDetectedByEnemies(); // vezi ce inamici sunt in campurile de detectie

        switch (playerOrientation){
            case DOWN: this.projectileDirection = ProjectileDirection.DOWN;                                             break;
            case UP: this.projectileDirection = ProjectileDirection.UP;                                                 break;
            case LEFT: this.projectileDirection = ProjectileDirection.LEFT;                 this.facingRight = false;   break;
            case RIGHT: this.projectileDirection = ProjectileDirection.RIGHT;               this.facingRight = true;    break;
            case LEFT_DOWN: this.projectileDirection = ProjectileDirection.BOTTOMLEFT;      this.facingRight = false;   break;
            case LEFT_UP: this.projectileDirection = ProjectileDirection.TOPLEFT;           this.facingRight = false;   break;
            case RIGHT_DOWN: this.projectileDirection = ProjectileDirection.BOTTOMRIGHT;    this.facingRight = true;    break;
            case RIGHT_UP: this.projectileDirection = ProjectileDirection.TOPRIGHT;         this.facingRight = true;    break;
        }

        if(locationStatus.equals(PlayerStatus.PLAYER_IN_WATER))
            this.actualPlayerSpeed = (float) (0.6 * FRAME_PLAYER_MOVE_SPEED);
        else
        if(locationStatus.equals(PlayerStatus.PLAYER_NO_COLLIDE))
            this.actualPlayerSpeed = FRAME_PLAYER_MOVE_SPEED;
        else
        if(locationStatus.equals(PlayerStatus.PLAYER_COLLIDE))
            this.actualPlayerSpeed = FRAME_PLAYER_COLLIDE_SPEED;

        //System.out.println(playerOrientation);

        float oldX = this.x;
        float oldY = this.y;

        switch (playerOrientation){
            case DOWN: this.y = this.y + actualPlayerSpeed; break;
            case UP:   this.y = this.y - actualPlayerSpeed; break;
            case LEFT: this.x = this.x - actualPlayerSpeed; break;
            case RIGHT:this.x = this.x + actualPlayerSpeed; break;

            case LEFT_UP: this.x = this.x - actualPlayerSpeed; this.y = this.y - actualPlayerSpeed; break;
            case LEFT_DOWN: this.x = this.x - actualPlayerSpeed; this.y = this.y + actualPlayerSpeed; break;
            case RIGHT_DOWN: this.x = this.x + actualPlayerSpeed; this.y = this.y + actualPlayerSpeed; break;
            case RIGHT_UP: this.x = this.x + actualPlayerSpeed; this.y = this.y - actualPlayerSpeed; break;
        }

        /// primim setul de coliziuni  ( in urma miscarii, cu ce se intersecteaza jucatorul? )
        List<PlayerStatus> status = map.isPlayerAllowed((int)this.x, (int)this.y, (int)this.x + SpriteSheet.SPRITE_WIDTH - 1, (int)this.y + SpriteSheet.SPRITE_HEIGHT - 1, playerOrientation);

//        System.out.println(status);

//        System.out.println(this.surroundingEnemies);

        if(status.contains(PlayerStatus.PLAYER_COLLIDE_LEFT) || status.contains(PlayerStatus.PLAYER_COLLIDE_RIGHT))
            this.x = oldX;

        if(status.contains(PlayerStatus.PLAYER_COLLIDE_TOP) || status.contains(PlayerStatus.PLAYER_COLLIDE_BOTTOM))
            this.y = oldY;

        for(HumanoidEnemy e : this.surroundingEnemies){
            if(!e.isDead())
                if(!e.getPlayerCollisionDirection().equals(Directions.NOTHING)){
                    Directions relativePlayerPosition = e.getPlayerCollisionDirection();

                    this.stopIfEnemyIsAround(oldX, oldY, playerOrientation, relativePlayerPosition);
                }
        }

        if(status.contains(PlayerStatus.PLAYER_COLLIDE))
            this.locationStatus = PlayerStatus.PLAYER_COLLIDE;
        else if(status.contains(PlayerStatus.PLAYER_IN_WATER))
            this.locationStatus = PlayerStatus.PLAYER_IN_WATER;
        else if(status.isEmpty())
            this.locationStatus = PlayerStatus.PLAYER_NO_COLLIDE;
    }

    /**
     * Apelat pentru a fi descoperit de inamici
     */
    private void getDetectedByEnemies(){
        Ellipse2D regularEnemyDetectionCircle = new Ellipse2D.Float(
                this.x-(float) REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                this.y- (float)REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS,
                REGULAR_ENEMY_DETECTION_CIRCLE_RADIUS
        );

        Ellipse2D rockThrowerEnemyDetectionCircle = new Ellipse2D.Float(
                this.x - (float) ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                this.y- (float)ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS,
                ROCK_THROWER_ENEMY_DETECTION_CIRCLE_RADIUS
        );
        Ellipse2D archerDetectionCircle = new Ellipse2D.Float(
                this.x - (float) ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                this.y- (float)ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS / 2,
                ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS,
                ARCHER_ENEMY_DETECTION_CIRCLE_RADIUS
        );

        for(Enemy e : this.enemies){

            try{
                RegularEnemy castedRegularEnemy = (RegularEnemy)e;

                if(regularEnemyDetectionCircle.contains(castedRegularEnemy.getX(), castedRegularEnemy.getY()))
                    if(!castedRegularEnemy.isFollowingPlayer())
                        castedRegularEnemy.setFollowingPlayer(true);

            } catch ( ClassCastException ignored ){
                try{
                    RockThrowerEnemy castedRockThrowerEnemy = (RockThrowerEnemy) e;

                    if(rockThrowerEnemyDetectionCircle.contains(castedRockThrowerEnemy.getX(), castedRockThrowerEnemy.getY()))
                        if(!castedRockThrowerEnemy.isFollowingPlayer())
                            castedRockThrowerEnemy.setFollowingPlayer(true);
                } catch ( ClassCastException ignored1 ){
                    try{
                        ArcherEnemy castedArcher = (ArcherEnemy) e;

                        if(archerDetectionCircle.contains(castedArcher.getX(), castedArcher.getY()))
                            if(!castedArcher.isFollowingPlayer())
                                castedArcher.setFollowingPlayer(true);
                    } catch ( ClassCastException ignored2 ){

                    }
                }
            }

//            if(regularEnemyDetectionCircle.contains(e.getX(), e.getY())){
////                if(!e.isFollowingPlayer()) {
////                    e.setFollowingPlayer(true);
////                }
//            }
        }
    }

    /**
     * Opreste jucatorul daca se intersecteaza cu inamici
     * @param oldX poz oriz veche
     * @param oldY poz vert veche
     * @param playerDir directia in care se deplaseaza juc.
     * @param relativePlayerPosition unde e jucatorul pt. inamic
     */
    private void stopIfEnemyIsAround(float oldX, float oldY, Directions playerDir, Directions relativePlayerPosition){
        switch (playerDir){
            case RIGHT:
                if (
                        relativePlayerPosition.equals(Directions.LEFT) ||
                        relativePlayerPosition.equals(Directions.LEFT_UP) ||
                        relativePlayerPosition.equals(Directions.LEFT_DOWN)
                )
                    this.x = oldX;
                break;
            case LEFT:
                if (
                        relativePlayerPosition.equals(Directions.RIGHT_DOWN) ||
                        relativePlayerPosition.equals(Directions.RIGHT_UP) ||
                        relativePlayerPosition.equals(Directions.RIGHT)
                )
                    this.x = oldX;
                break;
            case DOWN:
                if (
                        relativePlayerPosition.equals(Directions.UP) ||
                        relativePlayerPosition.equals(Directions.RIGHT_UP) ||
                        relativePlayerPosition.equals(Directions.LEFT_UP)
                )
                    this.y = oldY;
                break;
            case UP:
                if (
                        relativePlayerPosition.equals(Directions.DOWN) ||
                        relativePlayerPosition.equals(Directions.RIGHT_DOWN) ||
                        relativePlayerPosition.equals(Directions.LEFT_DOWN)
                )
                    this.y = oldY;
                break;

            case RIGHT_DOWN:
                if (
                        relativePlayerPosition.equals(Directions.LEFT) ||
                                relativePlayerPosition.equals(Directions.LEFT_UP) ||
                                relativePlayerPosition.equals(Directions.LEFT_DOWN)
                )
                    this.x = oldX;
                if (
                        relativePlayerPosition.equals(Directions.UP) ||
                                relativePlayerPosition.equals(Directions.RIGHT_UP) ||
                                relativePlayerPosition.equals(Directions.LEFT_UP)
                )
                    this.y = oldY;
                break;
            case LEFT_UP:
                if (
                        relativePlayerPosition.equals(Directions.RIGHT_DOWN) ||
                                relativePlayerPosition.equals(Directions.RIGHT_UP) ||
                                relativePlayerPosition.equals(Directions.RIGHT)
                )
                    this.x = oldX;
                if (
                        relativePlayerPosition.equals(Directions.DOWN) ||
                                relativePlayerPosition.equals(Directions.RIGHT_DOWN) ||
                                relativePlayerPosition.equals(Directions.LEFT_DOWN)
                )
                    this.y = oldY;
                break;
            case RIGHT_UP:
                if (
                        relativePlayerPosition.equals(Directions.LEFT) ||
                                relativePlayerPosition.equals(Directions.LEFT_UP) ||
                                relativePlayerPosition.equals(Directions.LEFT_DOWN)
                )
                    this.x = oldX;
                if (
                        relativePlayerPosition.equals(Directions.DOWN) ||
                                relativePlayerPosition.equals(Directions.RIGHT_DOWN) ||
                                relativePlayerPosition.equals(Directions.LEFT_DOWN)
                )
                    this.y = oldY;
            case LEFT_DOWN:
                if (
                        relativePlayerPosition.equals(Directions.RIGHT_DOWN) ||
                                relativePlayerPosition.equals(Directions.RIGHT_UP) ||
                                relativePlayerPosition.equals(Directions.RIGHT)
                )
                    this.x = oldX;
                if (
                        relativePlayerPosition.equals(Directions.UP) ||
                                relativePlayerPosition.equals(Directions.RIGHT_UP) ||
                                relativePlayerPosition.equals(Directions.LEFT_UP)
                )
                    this.y = oldY;
                break;
        }
    }

    /**
     * "vindeca" o viata, colectezi un heart item
     */
    public void heal(){
        this.heartsCount++;
    }

    /**
     * ia o cheie de pe jos
     */
    public void addKey(){
        this.keyCount++;
    }

    /**
     * cate chei are juc.
     * @return keyCount
     */
    public int getKeyCount() {
        return keyCount;
    }

}
