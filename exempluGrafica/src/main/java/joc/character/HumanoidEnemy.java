package joc.character;

import joc.assets.SpriteSheet;
import joc.item.Heart;
import joc.item.Item;
import joc.map.Map;
import joc.player.Directions;
import joc.player.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class HumanoidEnemy implements Enemy {
    protected BufferedImage enemySpriteRight;
    protected BufferedImage enemySpriteLeft;

    protected SpriteSheet sheet;

    public static final int LOW_DROP_CHANCE = 5;
    public static final int MEDIUM_DROP_CHANCE = 15;
    public static final int HIGH_DROP_CHANCE = 40;

    private int dropChance;

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

    protected void dropHeart(){
        switch (this.player.getHeartsCount()){
            case 3 : this.dropChance = LOW_DROP_CHANCE; break;
            case 2 : this.dropChance = MEDIUM_DROP_CHANCE; break;
            case 1 : this.dropChance = HIGH_DROP_CHANCE; break;
            default: this.dropChance = 0;
        }

        boolean drops = ((int)(Math.random() * 100) < this.dropChance);

        if(drops){
            Item.getGameWorldItems().add(new Heart(this.player, this.sheet, this.x, this.y));
        }
    }

    protected boolean isFollowingPlayer;
//
//    public void getHit(){
//        this.dead = true;
//    }

    public abstract void getHit();

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
        if(this.player.isDead()) {
            this.playerCollisionDirection = Directions.NOTHING;
            return false;
        }

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

    protected HumanoidEnemy(float x, float y){
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

        //System.out.println(this.joc.player.getX() + " " +this.joc.player.getY());
    }
}
