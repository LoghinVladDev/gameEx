package character;

import assets.AssetList;
import assets.SpriteSheet;
import map.Map;
import player.Directions;
import player.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;

public abstract class Enemy {
    protected BufferedImage enemySpriteRight;
    protected BufferedImage enemySpriteLeft;

    protected int frameTimeout;

    protected float x, y;

    protected boolean dead;

    protected Player player;
    private Directions playerCollisionDirection;

    protected Map map;

    public boolean isDead() {
        return dead;
    }

    protected boolean facingRight = true;

    protected boolean isFollowingPlayer;

    public void getHit(){
        this.dead = true;
    }

    public boolean isFollowingPlayer() {
        return isFollowingPlayer;
    }

    public void setFollowingPlayer(boolean followingPlayer) {
        isFollowingPlayer = followingPlayer;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Directions getPlayerCollisionDirection() {
        return playerCollisionDirection;
    }



    public boolean collidesWithPlayer(){
        boolean cBotLeft = false;
        boolean cBotRight= false;
        boolean cTopLeft = false;
        boolean cTopRight= false;

        float playerTop = this.player.getY();
        float playerBottom = this.player.getY() + SpriteSheet.SPRITE_HEIGHT;
        float playerLeft = this.player.getX();
        float playerRight = this.player.getX() + SpriteSheet.SPRITE_WIDTH;

        float enemyTop = this.y;
        float enemyBottom = this.y + SpriteSheet.SPRITE_HEIGHT;
        float enemyLeft = this.x;
        float enemyRight = this.x + SpriteSheet.SPRITE_WIDTH;

        if(
                enemyTop >= playerTop && enemyTop < playerBottom &&
                enemyLeft >= playerLeft && enemyLeft < playerRight
        ) {
            cTopLeft = true;
        }
        if(
                enemyTop >= playerTop && enemyTop < playerBottom &&
                enemyRight >= playerLeft && enemyRight < playerRight
        ) {
            cTopRight = true;
        }
        if(
                enemyBottom >= playerTop && enemyBottom < playerBottom &&
                enemyLeft >= playerLeft && enemyLeft < playerRight
        ) {
            cBotLeft = true;
        }
        if(
                enemyBottom >= playerTop && enemyBottom < playerBottom
                && enemyRight >= playerLeft && enemyRight < playerRight
        ) {
            cBotRight = true;
        }

        if(cTopLeft && cBotLeft)
            this.playerCollisionDirection = Directions.LEFT;
        else if(cTopLeft && cTopRight)
            this.playerCollisionDirection = Directions.UP;
        else if(cBotRight && cBotLeft)
            this.playerCollisionDirection = Directions.DOWN;
        else if(cTopRight && cBotRight)
            this.playerCollisionDirection = Directions.RIGHT;
        else if(cTopLeft)
            this.playerCollisionDirection = Directions.LEFT_UP;
        else if(cBotLeft)
            this.playerCollisionDirection = Directions.LEFT_DOWN;
        else if(cTopRight)
            this.playerCollisionDirection = Directions.RIGHT_UP;
        else if(cBotRight)
            this.playerCollisionDirection = Directions.RIGHT_DOWN;
        else
            this.playerCollisionDirection = Directions.NOTHING;

        if(!this.playerCollisionDirection.equals(Directions.NOTHING)){
            if(!this.player.getSurroundingEnemies().contains(this))
                this.player.getSurroundingEnemies().add(this);
            return true;
        } else {
            this.player.getSurroundingEnemies().remove(this);
            return false;
        }

//        return collisionStatus;
    }

    public void swapDirection(){
        if(this.frameTimeout > 0) {
            this.frameTimeout--;
            return;
        }
        if(this.frameTimeout == 0)
            this.frameTimeout = 60;
        this.facingRight = !this.facingRight;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    protected Enemy(float x, float y){
        this.x = x;
        this.y = y;
        //this.enemySpriteLeft = sheet.getAsset(AssetList.ENEMY_REGULAR_LEFT);
        //this.enemySpriteRight = sheet.getAsset(AssetList.ENEMY_REGULAR_RIGHT);
    }

    public abstract void update();

    public void draw(Graphics g){
        g.drawImage(
                facingRight ? this.enemySpriteRight : this.enemySpriteLeft,
                (int)this.x,
                (int)this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT,
                null
        );

        //System.out.println(this.player.getX() + " " +this.player.getY());
    }
}
