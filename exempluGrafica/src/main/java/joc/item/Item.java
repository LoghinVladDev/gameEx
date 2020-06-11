package joc.item;

import joc.assets.SpriteSheet;
import joc.player.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Item {

    protected float yVelocity = 0;

    protected static final float Y_VELOCITY_THRESHOLD = 4;
    protected static final float Y_VELOCITY_FRAME_INCREASE = 2;

    protected static final int FRAME_STOP_LIMIT = 4;
    protected int timeout = 0;

    protected boolean up = true;

    protected boolean toBeDeSpawned = false;

    /**
     * daca player-ul a luat item-ul ,trebuie scos din cadru
     * @return
     */
    public boolean isToBeDeSpawned() {
        return toBeDeSpawned;
    }

    private static List<Item> gameWorldItems = new ArrayList<>();

    protected float x;
    protected float y;

    protected Player player;
    protected SpriteSheet sheet;

    protected BufferedImage sprite;

    /**
     * bounce sus-jos
     */
    protected void animate(){
        if(this.timeout == 0) {
            if (up) {
                if (yVelocity < Y_VELOCITY_THRESHOLD) {
                    yVelocity = yVelocity + Y_VELOCITY_FRAME_INCREASE;
                } else {
                    up = false;
                }
            } else {
                if (yVelocity > -1 * Y_VELOCITY_THRESHOLD) {
                    yVelocity = yVelocity - Y_VELOCITY_FRAME_INCREASE;
                } else {
                    up = true;
                }
            }

            this.y += this.yVelocity;
            this.timeout = FRAME_STOP_LIMIT;
        } else {
            this.timeout--;
        }
    }

    /**
     * ctor
     * @param player
     * @param sheet
     * @param xPos
     * @param yPos
     */
    protected Item(Player player, SpriteSheet sheet, float xPos, float yPos){
        this.player = player;
        this.sheet = sheet;
        this.x = xPos;
        this.y = yPos;
    }

    public abstract void update();
    public abstract void draw(Graphics g);

    public static List<Item> getGameWorldItems() {
        return gameWorldItems;
    }
}
