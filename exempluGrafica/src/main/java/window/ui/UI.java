package window.ui;

import assets.AssetList;
import assets.SpriteSheet;
import player.Player;
import window.GameWindow;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class UI {

    private Player player;

    private BufferedImage heartSprite;
    private GameWindow window;

    private static final int HORIZ_OFFSET = 20;
    private static final int VERT_OFFSET = 20;

    private int heartCount;

    public UI(GameWindow gameWindow, SpriteSheet sheet){
        this.heartSprite = sheet.getAsset(AssetList.HEART_ICON);
        this.window = gameWindow;
    }

    public UI setPlayer(Player p){
        this.player = p;
        return this;
    }

    public void update(){
        this.heartCount = player.getHeartsCount();
    }

    public void draw(Graphics g){
        for(int i = 0; i < heartCount; i++){
            g.drawImage(
                    this.heartSprite,
                    HORIZ_OFFSET + i * SpriteSheet.SPRITE_WIDTH,
                    VERT_OFFSET ,
                    SpriteSheet.SPRITE_WIDTH,
                    SpriteSheet.SPRITE_HEIGHT,
                    null
            );
        }
    }

}