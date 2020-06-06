package joc.character;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.map.Map;
import joc.player.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Spike implements Enemy {
    private float x, y;
    private Map map;
    private Player player;
    private SpriteSheet sheet;
    private BufferedImage spriteHidden;
    private BufferedImage spriteShown;
    private BufferedImage sprite;

    private static final int FRAME_COUNT_SHOW = 90;
    private static final int FRAME_COUNT_ON_SURFACE = 120;
    private static final int FRAME_COUNT_HIT_PLAYER = 60;

    private int showTimer = 0;
    private int onSurfaceTimer = 0;
    private int hitPlayerTimer = 0;
    private boolean shown = false;
    private boolean active = false;

    public boolean collides(){
        return false;
    }

    public Spike(int x, int y, SpriteSheet sheet){
        this.sheet = sheet;
        this.x = x;
        this.y = y;

        this.spriteHidden = sheet.getAsset(AssetList.SPIKE_HIDDEN);
        this.spriteShown = sheet.getAsset(AssetList.SPIKE_SHOWN);

        this.sprite = this.spriteHidden;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(
                this.sprite,
                (int)this.x,
                (int)this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT,
                null
        );
    }

    private boolean isPlayerOver(){
        Rectangle spikeMesh = new Rectangle(
                (int) this.x,
                (int) this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT
        );

        Rectangle playerMesh = new Rectangle(
                (int) this.player.getX(),
                (int) this.player.getY(),
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT
        );

        return spikeMesh.intersects(playerMesh);
    }

    @Override
    public void update() {
        if(this.isPlayerOver()){
//            System.out.println("ESTE PESTE TEPI");
            if(!this.shown && !this.active) {
                this.showTimer = FRAME_COUNT_SHOW;
                this.active = true;
//                System.out.println("A inceput timer-ul pt afisare");
            }
            else if(this.shown && this.active) {
                if(this.hitPlayerTimer == 0) {
                    this.player.getHit();
                    this.hitPlayerTimer = FRAME_COUNT_HIT_PLAYER;
                } else {
                    this.hitPlayerTimer--;
                }
            }
        }
        if(!this.shown && this.active) {
            if(this.showTimer > 0) {
//                System.out.println("Asteptam...");
                this.showTimer--;
            }
            else {
//                System.out.println("S-a afisat");
                this.shown = true;
                this.sprite = this.spriteShown;
                this.onSurfaceTimer = FRAME_COUNT_ON_SURFACE;
            }
        } else if(this.active) {
            if(this.onSurfaceTimer > 0){
                this.onSurfaceTimer--;
            } else {
                this.shown = false;
                this.sprite = this.spriteHidden;
                this.active = false;
            }
        }
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public void getHit() {

    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }
}
