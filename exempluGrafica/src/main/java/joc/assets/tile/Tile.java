package joc.assets.tile;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.player.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    protected BufferedImage tileSprite;
    protected AssetList type;
    protected SpriteSheet sheet;
    protected int x, y;

    public Tile(){
        this.tileSprite = null;
    }

    public AssetList getType() {
        return type;
    }

    public Tile(AssetList spriteType, SpriteSheet sheet) {
        this.type = spriteType;
        if (spriteType.ordinal() >= 8 && spriteType.ordinal() != 20)
            throw new RuntimeException();

        this.sheet = sheet;
        this.tileSprite = sheet.getAsset(spriteType);
    }

    /*public void destory(){
        if(this.type.equals(AssetList.CASTLE_GRASS_TILE)){
            this.tileSprite = this.sheet.getAsset(AssetList.DAMAGED_CASTLE_GRASS_TILE);
            this.type = AssetList.DAMAGED_CASTLE_GRASS_TILE;
        }
    }*/

    public void destroy(){
//        System.out.println("BOLOVAN PESTE TILE LA x = " + this.x + ", y = " + this.y);
    }

    public void addOnDropKey(Player player){

    }

    public void drawAt(int x, int y, Graphics graphics){
        graphics.drawImage(
                this.tileSprite,
                x * SpriteSheet.SPRITE_WIDTH,
                y * SpriteSheet.SPRITE_HEIGHT,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT,
                null
        );
    }

    public void setLocation(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics graphics){
        this.drawAt(this.x, this.y, graphics);
    }
}
