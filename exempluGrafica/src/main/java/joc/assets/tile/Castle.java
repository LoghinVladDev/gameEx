package joc.assets.tile;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.item.Item;
import joc.item.Key;
import joc.player.Player;
import joc.window.GameWindow;

/**
 * Class Castle extended from Tile
 */
public class Castle extends Tile {
    private int health = 3;

    private Player player;

    private static final int CASTLE_SCORE_POINTS = 400;

    public static final int CASTLE_SAND = 1;
    public static final int CASTLE_GRASS = 0;

    private boolean willDropKey = false;

    /**
     * Ctor
     * @param sheet Sprite sheet from which to load asset
     * @param type asset type, which castle type is it?
     */
    public Castle(SpriteSheet sheet, int type){
        super(
            (type == CASTLE_SAND ? AssetList.CASTLE_SAND_TILE :
                (type == CASTLE_GRASS ? AssetList.CASTLE_GRASS_TILE : AssetList.UNKNOWN_ASSET)
            ),
            sheet
        );
    }

    /**
     * adaugare de cheie la distrugere. Aici face mai multe.
     * @param player
     */
    @Override
    public void addOnDropKey(Player player){
        this.willDropKey = true;
        this.player = player;

        System.out.println(this.x + ", " + this.y);
    }


    /**
     * Called when rock hits castle. Aici face mai multe
     * Calculeaza daca piatra care a intrat in tile trebuie sa distruga tile-ul sau nu
     */
    @Override
    public void destroy() {
        if(this.type.equals(AssetList.DAMAGED_CASTLE_GRASS_TILE))
            return;

        if(this.health > 0)
            this.health--;
        else {
            this.tileSprite = this.sheet.getAsset(AssetList.DAMAGED_CASTLE_GRASS_TILE);
            this.type = AssetList.DAMAGED_CASTLE_GRASS_TILE;

            GameWindow.getInstance().setScore(GameWindow.getInstance().getScore() + CASTLE_SCORE_POINTS);

            if(this.willDropKey)
                Item.getGameWorldItems().add(new Key(this.player, this.sheet, this.x*SpriteSheet.SPRITE_WIDTH, this.y*SpriteSheet.SPRITE_HEIGHT));
        }
    }
}
