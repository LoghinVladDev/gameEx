package character;

import assets.AssetList;
import assets.SpriteSheet;

public class RockThrowerEnemy extends Enemy {

    public RockThrowerEnemy(int x, int y, SpriteSheet sheet){
        super(x,y);

        super.enemySpriteLeft = sheet.getAsset(AssetList.ENEMY_ROCK_THROWER_LEFT);
        super.enemySpriteRight = sheet.getAsset(AssetList.ENEMY_ROCK_THROWER_RIGHT);
    }

    @Override
    public void update() {
        this.swapDirection();
    }
}
