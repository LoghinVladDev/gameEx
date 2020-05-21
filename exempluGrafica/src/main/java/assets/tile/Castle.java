package assets.tile;

import assets.AssetList;
import assets.SpriteSheet;

public class Castle extends Tile {
    private int health = 3;

    public static final int CASTLE_SAND = 1;
    public static final int CASTLE_GRASS = 0;

    public Castle(SpriteSheet sheet, int type){
        super(
            (type == CASTLE_SAND ? AssetList.CASTLE_SAND_TILE :
                (type == CASTLE_GRASS ? AssetList.CASTLE_GRASS_TILE : AssetList.UNKNOWN_ASSET)
            ),
            sheet
        );
    }

    public void destroy() {
        System.out.println("BOLOVAN PESTE CASTEL LA x = " + this.x + ", y = " + this.y);
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
