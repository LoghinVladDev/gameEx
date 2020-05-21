package assets.tile;

import assets.AssetList;
import assets.SpriteSheet;

public class Tower extends Tile{
    private int health = 5;

    public Tower(SpriteSheet sheet){
        super(
                AssetList.TOWER,
                sheet
        );

        System.out.println("s-a creat turn");
    }

    public void destroy() {
        System.out.println("BOLOVAN PESTE TURN LA x = " + this.x + ", y = " + this.y);
        if(this.type.equals(AssetList.DAMAGED_CASTLE_GRASS_TILE))
            return;

        if(this.health > 0)
            this.health--;
        else {
            this.tileSprite = this.sheet.getAsset(AssetList.DAMAGED_CASTLE_GRASS_TILE);
            this.type = AssetList.DAMAGED_CASTLE_GRASS_TILE;
        }
    }
}
