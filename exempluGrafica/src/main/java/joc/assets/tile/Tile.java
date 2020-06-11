package joc.assets.tile;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.player.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * O patratica in harta , fara caracteristici speciale
 */
public class Tile {
    protected BufferedImage tileSprite;
    protected AssetList type;
    protected SpriteSheet sheet;
    protected int x, y;

    /**
     * ctor
     */
    public Tile(){
        this.tileSprite = null;
    }

    /**
     * ce tip de patratica este
     * @return tip
     */
    public AssetList getType() {
        return type;
    }

    /**
     * ctor cu parametrii
     * @param spriteType tipul de patrat
     * @param sheet de unde incarca
     */
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

    // fct default de destroy, nu face prea multe.
    public void destroy(){

    }

    /**
     * fct default pt adaugarea unei chei la distrugere. nu face prea multe. Va fi suprascrisa de Tile-uri specifice
     * @param player
     */
    public void addOnDropKey(Player player){

    }

    /**
     * deseneaza tile-ul la ...
     * @param x hor
     * @param y vert
     * @param graphics unde
     */
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

    /**
     * pune poz. tile-ului
     * @param x hor
     * @param y vert
     */
    public void setLocation(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * redeseneaza
     * @param graphics unde
     */
    public void draw(Graphics graphics){
        this.drawAt(this.x, this.y, graphics);
    }
}
