package character;

import assets.AssetList;
import assets.SpriteSheet;
import map.Map;
import player.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;

public abstract class Enemy {
    protected BufferedImage enemySpriteRight;
    protected BufferedImage enemySpriteLeft;

    protected int frameTimeout;

    protected float x, y;

    private Player player;

    protected Map map;

    protected boolean facingRight = true;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean collidesWithPlayer(){
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
        )
            return true;
        if(
                enemyTop >= playerTop && enemyTop < playerBottom &&
                enemyRight >= playerLeft && enemyRight < playerRight
        )
            return true;
        if(
                enemyBottom >= playerTop && enemyBottom < playerBottom &&
                enemyLeft >= playerLeft && enemyLeft < playerRight
        )
            return true;
        if(
                enemyBottom >= playerTop && enemyBottom < playerBottom
                && enemyRight >= playerLeft && enemyRight < playerRight
        )
            return true;

        return false;
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
