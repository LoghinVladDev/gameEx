package character;

import assets.AssetList;
import assets.SpriteSheet;
import player.PlayerStatus;

import java.util.List;

public class RegularEnemy extends Enemy {

    private int patrolHorizStartingPoint = 0;
    private int patrolHorizOffsetLimit = 48;

    private int patrolSpeed = 3;

    public RegularEnemy(int x, int y, SpriteSheet sheet){
        super(x,y);

        this.patrolHorizStartingPoint = (int)this.x;

        super.enemySpriteLeft = sheet.getAsset(AssetList.ENEMY_REGULAR_LEFT);
        super.enemySpriteRight = sheet.getAsset(AssetList.ENEMY_REGULAR_RIGHT);
    }

    @Override
    public void update() {
        float oldX = this.x;
        float oldY = this.y;

        if(this.collidesWithPlayer())
            return;

        this.x = this.x + patrolSpeed * (super.facingRight ? 1 : -1);

//        List<PlayerStatus> status = map.isPlayerAllowed(
//                (int)this.x,
//                (int)this.y,
//                (int)this.x + SpriteSheet.SPRITE_WIDTH-1,
//                (int)this.y + SpriteSheet.SPRITE_HEIGHT-1
//        );
//
//        if(status.equals(PlayerStatus.PLAYER_COLLIDE)){
//            this.x = oldX;
//            this.y = oldY;
//            this.facingRight = !this.facingRight;
//
//            this.patrolHorizStartingPoint = (int) (oldX + (this.facingRight?1:-1) * patrolHorizOffsetLimit);
//        }

        if(Math.abs(this.x - this.patrolHorizStartingPoint) > this.patrolHorizOffsetLimit)
            this.facingRight = !this.facingRight;
    }
}
