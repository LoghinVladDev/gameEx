package player;

import java.util.List;
import assets.AssetList;
import assets.SpriteSheet;
import character.Enemy;
import listener.MovementListener;
import map.Map;
import projectile.ProjectileDirection;

import javax.print.attribute.standard.PDLOverrideSupported;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class Player {
    boolean combinedMovement = false;
    private BufferedImage playerSpriteLeft;
    private BufferedImage playerSpriteRight;
    private final float FRAME_PLAYER_MOVE_SPEED = 5;
    private final float FRAME_PLAYER_COLLIDE_SPEED = 5;

    private MovementListener movementListener;

    private List<Enemy> enemies;

    private float actualPlayerSpeed = FRAME_PLAYER_MOVE_SPEED;

    private PlayerStatus locationStatus = PlayerStatus.PLAYER_NO_COLLIDE;

    public PlayerStatus getLocationStatus() {
        return locationStatus;
    }

    private ProjectileDirection projectileDirection = ProjectileDirection.RIGHT;

    public ProjectileDirection getNextProjectileDirection() {
        return projectileDirection;
    }

    private float x;
    private float y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Player(float x, float y, SpriteSheet spriteSheet){
        this.x = x;
        this.y = y;
        this.playerSpriteLeft = spriteSheet.getAsset(AssetList.PLAYER_LEFT);
        this.playerSpriteRight = spriteSheet.getAsset(AssetList.PLAYER_RIGHT);
    }


    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    /**
     * true = spirte right
     * false  = sprite left
     */
    private boolean facingRight = true;

    public void setMovementListener(MovementListener movementListener) {
        this.movementListener = movementListener;
    }

    public void draw(Graphics g){
        g.drawImage(
                facingRight ? this.playerSpriteRight : this.playerSpriteLeft,
                (int)this.x,
                (int)this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT,
                null
        );
    }
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

    public void update(Map map){
        Directions playerOrientation = getInputDirection();

        switch (playerOrientation){
            case DOWN: this.projectileDirection = ProjectileDirection.DOWN;                 break;
            case UP: this.projectileDirection = ProjectileDirection.UP;                     break;
            case LEFT: this.projectileDirection = ProjectileDirection.LEFT;                 break;
            case RIGHT: this.projectileDirection = ProjectileDirection.RIGHT;               break;
            case LEFT_DOWN: this.projectileDirection = ProjectileDirection.BOTTOMLEFT;      break;
            case LEFT_UP: this.projectileDirection = ProjectileDirection.TOPLEFT;           break;
            case RIGHT_DOWN: this.projectileDirection = ProjectileDirection.BOTTOMRIGHT;    break;
            case RIGHT_UP: this.projectileDirection = ProjectileDirection.TOPRIGHT;         break;
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

        List<PlayerStatus> status = map.isPlayerAllowed((int)this.x, (int)this.y, (int)this.x + SpriteSheet.SPRITE_WIDTH - 1, (int)this.y + SpriteSheet.SPRITE_HEIGHT - 1, playerOrientation);

        System.out.println(status);

        /*
        if(status.contains(PlayerStatus.PLAYER_COLLIDE)){
            this.x = oldX;
            this.y = oldY;
        }*/

        if(status.contains(PlayerStatus.PLAYER_COLLIDE_LEFT) || status.contains(PlayerStatus.PLAYER_COLLIDE_RIGHT))
            this.x = oldX;

        if(status.contains(PlayerStatus.PLAYER_COLLIDE_TOP) || status.contains(PlayerStatus.PLAYER_COLLIDE_BOTTOM))
            this.y = oldY;

        if(status.contains(PlayerStatus.PLAYER_COLLIDE))
            this.locationStatus = PlayerStatus.PLAYER_COLLIDE;
        else if(status.contains(PlayerStatus.PLAYER_IN_WATER))
            this.locationStatus = PlayerStatus.PLAYER_IN_WATER;
        else if(status.isEmpty())
            this.locationStatus = PlayerStatus.PLAYER_NO_COLLIDE;
    }

    /*
    public void moveLeft(){
        this.combinedMovement = true;
        this.projectileDirection = ProjectileDirection.LEFT;
        this.x = x - actualPlayerSpeed; this.facingRight = false;
        //System.out.println("Player moved left, new coords : x = " +  this.x + ", y = " + this.y);
    }

    public void moveRight(){
        this.projectileDirection = ProjectileDirection.RIGHT;
        //System.out.println("Player moved right, new coords : x = " +  this.x + ", y = " + this.y);
        this.x = x + actualPlayerSpeed; this.facingRight = true;
    }

    public void moveUp(){
        this.combinedMovement = true;
        this.projectileDirection = ProjectileDirection.UP;
        //System.out.println("Player moved up, new coords : x = " +  this.x + ", y = " + this.y);
        this.y = y - actualPlayerSpeed;
    }

    public void moveDown(){
        this.combinedMovement = true;
        this.projectileDirection = ProjectileDirection.DOWN;
        //System.out.println("Player moved down, new coords : x = " +  this.x + ", y = " + this.y);
        this.y = y + actualPlayerSpeed;
    }

     */

    /*
    public void update(MovementListener movementListener, Map map){
        this.combinedMovement = false;

        if(locationStatus.equals(PlayerStatus.PLAYER_IN_WATER))
            this.actualPlayerSpeed = (float) (0.6 * FRAME_PLAYER_MOVE_SPEED);
        else
            if(locationStatus.equals(PlayerStatus.PLAYER_NO_COLLIDE))
                this.actualPlayerSpeed = FRAME_PLAYER_MOVE_SPEED;
            else
                if(locationStatus.equals(PlayerStatus.PLAYER_COLLIDE))
                    this.actualPlayerSpeed = FRAME_PLAYER_COLLIDE_SPEED;

        float oldX = this.x;
        float oldY = this.y;

        if(movementListener.isUp()){
            this.moveUp();
        }

        PlayerStatus statusUp = map.isPlayerAllowed((int)this.x, (int)this.y, (int)this.x + SpriteSheet.SPRITE_WIDTH - 1, (int)this.y + SpriteSheet.SPRITE_HEIGHT - 1);

        if(statusUp.equals(PlayerStatus.PLAYER_COLLIDE)){
            this.x = oldX;
            this.y = oldY;
        }

        oldX = this.x;
        oldY = this.y;

        if(movementListener.isDown()){
            this.moveDown();
        }

        PlayerStatus statusDown = map.isPlayerAllowed((int)this.x, (int)this.y, (int)this.x + SpriteSheet.SPRITE_WIDTH - 1, (int)this.y + SpriteSheet.SPRITE_HEIGHT - 1);

        if(statusDown.equals(PlayerStatus.PLAYER_COLLIDE)){
            this.x = oldX;
            this.y = oldY;
        }

        oldX = this.x;
        oldY = this.y;

        if(movementListener.isLeft()){
            this.moveLeft();
        }

        PlayerStatus statusLeft = map.isPlayerAllowed((int)this.x, (int)this.y, (int)this.x + SpriteSheet.SPRITE_WIDTH - 1, (int)this.y + SpriteSheet.SPRITE_HEIGHT - 1);

        if(statusLeft.equals(PlayerStatus.PLAYER_COLLIDE)){
            this.x = oldX;
            this.y = oldY;
        }

        oldX = this.x;
        oldY = this.y;

        if(movementListener.isRight()){
            this.moveRight();
        }

        PlayerStatus statusRight = map.isPlayerAllowed((int)this.x, (int)this.y, (int)this.x + SpriteSheet.SPRITE_WIDTH - 1, (int)this.y + SpriteSheet.SPRITE_HEIGHT - 1);

        if(statusRight.equals(PlayerStatus.PLAYER_COLLIDE)){
            this.x = oldX;
            this.y = oldY;
        }

        if(
                statusDown.equals(PlayerStatus.PLAYER_COLLIDE) ||
                statusLeft.equals(PlayerStatus.PLAYER_COLLIDE) ||
                statusUp.equals(PlayerStatus.PLAYER_COLLIDE) ||
                statusRight.equals(PlayerStatus.PLAYER_COLLIDE)
        )
            this.locationStatus = PlayerStatus.PLAYER_COLLIDE;
        else
            if(
                statusDown.equals(PlayerStatus.PLAYER_IN_WATER) ||
                        statusLeft.equals(PlayerStatus.PLAYER_IN_WATER) ||
                        statusUp.equals(PlayerStatus.PLAYER_IN_WATER) ||
                        statusRight.equals(PlayerStatus.PLAYER_IN_WATER)
            )
                this.locationStatus = PlayerStatus.PLAYER_IN_WATER;
            else
                this.locationStatus = PlayerStatus.PLAYER_NO_COLLIDE;

        if(movementListener.isUp() && movementListener.isRight())
            this.projectileDirection = ProjectileDirection.TOPRIGHT;
        else
            if(movementListener.isUp() && movementListener.isLeft())
                this.projectileDirection = ProjectileDirection.TOPLEFT;
            else
                if(movementListener.isRight() && movementListener.isDown())
                    this.projectileDirection = ProjectileDirection.BOTTOMRIGHT;
                else
                    if(movementListener.isLeft() && movementListener.isDown())
                        this.projectileDirection = ProjectileDirection.BOTTOMLEFT;

    }

     */
}
