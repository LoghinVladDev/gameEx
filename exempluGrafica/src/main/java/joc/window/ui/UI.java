package joc.window.ui;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.player.Player;
import joc.window.GameWindow;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UI {

    private Player player;

    private BufferedImage heartSprite;
    private BufferedImage keySprite;
    private GameWindow window;

    private static final int HEART_HORIZ_OFFSET = 20;
    private static final int HEART_VERT_OFFSET = 20;
    private static final int KEY_HORIZ_OFFSET = 20;
    private static final int KEY_VERT_OFFSET = 100;

    private int heartCount;
    private int keyCount = 0;

    public UI(GameWindow gameWindow, SpriteSheet sheet){
        this.heartSprite = sheet.getAsset(AssetList.HEART_ICON);
        this.keySprite = sheet.getAsset(AssetList.KEY_ICON);
        this.window = gameWindow;
    }

    public UI setPlayer(Player p){
        this.player = p;
        return this;
    }

    public void setKeyCount(int keyCount){
        this.keyCount = keyCount;
    }

    public void update(){
        this.heartCount = player.getHeartsCount();
    }

    /**
     * pe baza a cate chei / inimi are, deseneaza mai la dreapta cate una
     * @param g
     */
    public void draw(Graphics g){
        for(int i = 0; i < heartCount; i++){
            g.drawImage(
                    this.heartSprite,
                    HEART_HORIZ_OFFSET + i * SpriteSheet.SPRITE_WIDTH,
                    HEART_VERT_OFFSET ,
                    SpriteSheet.SPRITE_WIDTH,
                    SpriteSheet.SPRITE_HEIGHT,
                    null
            );
        }
        for(int i = 0; i < keyCount; i++){
            g.drawImage(
                    this.keySprite,
                    KEY_HORIZ_OFFSET + i * SpriteSheet.SPRITE_WIDTH,
                    KEY_VERT_OFFSET ,
                    SpriteSheet.SPRITE_WIDTH,
                    SpriteSheet.SPRITE_HEIGHT,
                    null
            );
        }

    }

}
