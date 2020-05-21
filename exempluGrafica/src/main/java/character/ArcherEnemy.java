package character;

import assets.AssetList;
import assets.SpriteSheet;

public class ArcherEnemy extends Enemy {

    public ArcherEnemy(int x, int y, SpriteSheet sheet){
        super(x,y);

        super.enemySpriteLeft = sheet.getAsset(AssetList.ENEMY_ARCHER_LEFT);
        super.enemySpriteRight = sheet.getAsset(AssetList.ENEMY_ARCHER_RIGHT);
    }

    @Override
    public void update() {
        this.swapDirection();
    }
}
