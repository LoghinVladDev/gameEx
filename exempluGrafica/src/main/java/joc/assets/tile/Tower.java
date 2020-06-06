package joc.assets.tile;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.window.GameWindow;

public class Tower extends Tile{
    private int health = 5;

    private static final int TOWER_SCORE_POINTS = 600;

    public Tower(SpriteSheet sheet){
        super(
                AssetList.TOWER,
                sheet
        );

        System.out.println("s-a creat turn");
    }

    public void destroy() {
//        System.out.println("BOLOVAN PESTE TURN LA x = " + this.x + ", y = " + this.y);
        if(this.type.equals(AssetList.DAMAGED_CASTLE_GRASS_TILE))
            return;

        if(this.health > 0)
            this.health--;
        else {

            GameWindow.getInstance().setScore(GameWindow.getInstance().getScore() + TOWER_SCORE_POINTS);
            this.tileSprite = this.sheet.getAsset(AssetList.DAMAGED_CASTLE_GRASS_TILE);
            this.type = AssetList.DAMAGED_CASTLE_GRASS_TILE;
        }
    }
}
