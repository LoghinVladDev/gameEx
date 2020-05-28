package character;

import assets.AssetList;
import assets.SpriteSheet;
import player.Directions;
import player.PlayerStatus;

import java.util.List;

public class RegularEnemy extends Enemy {

    private int patrolHorizStartingPoint = 0;
    private int patrolHorizOffsetLimit = 48;

    private int patrolSpeed = 3;

    private float moveSpeed = 2;

    public RegularEnemy(int x, int y, SpriteSheet sheet){
        super(x,y);

        this.patrolHorizStartingPoint = (int)this.x;

        this.isFollowingPlayer = false;

        super.enemySpriteLeft = sheet.getAsset(AssetList.ENEMY_REGULAR_LEFT);
        super.enemySpriteRight = sheet.getAsset(AssetList.ENEMY_REGULAR_RIGHT);
    }

    private Directions movementDirection;



    @Override
    public void update() {
        float oldX = this.x;
        float oldY = this.y;

        if(this.collidesWithPlayer())
            return;

        if(!this.isFollowingPlayer)
            this.patrol();
        else{
            this.followPlayer();
        }

        List<PlayerStatus> status = map.isPlayerAllowed(
                (int)this.x,
                (int)this.y,
                (int)this.x + SpriteSheet.SPRITE_WIDTH-1,
                (int)this.y + SpriteSheet.SPRITE_HEIGHT-1,
                this.movementDirection
        );

        if(!status.isEmpty()){
            this.x = oldX;
            this.y = oldY;
            if(!this.isFollowingPlayer)
            this.facingRight = !this.facingRight;
            this.patrolHorizStartingPoint = (int) (oldX + (this.facingRight?1:-1) * patrolHorizOffsetLimit);
        }
    }

    private void followPlayer(){
        float height = this.y - this.player.getY();
        float width = this.player.getX() - this.x;

        boolean firstHalf = false;

        int quadrant = 0;

        if(height >=0 && width >= 0)
            quadrant = 1;
        if(height >=0 && width < 0)
            quadrant = 2;
        if(height < 0 && width < 0)
            quadrant = 3;
        if(height < 0 && width >= 0)
            quadrant = 4;

        if(Math.abs(height) > Math.abs(width)) {
            if(quadrant == 1 || quadrant == 3)
                firstHalf = false;
            else
                firstHalf = true;
        }
        else{
            if(quadrant == 1 || quadrant == 3)
                firstHalf = true;
            else
                firstHalf = false;
        }

//        System.out.println(quadrant + ", " + firstHalf);

        if(quadrant == 1 && firstHalf || quadrant == 4 && !firstHalf)
            this.movementDirection = Directions.RIGHT;
        else if(quadrant == 1 && !firstHalf || quadrant == 2 && firstHalf)
            this.movementDirection = Directions.UP;
        else if(quadrant == 2 && !firstHalf || quadrant == 3 && firstHalf)
            this.movementDirection = Directions.LEFT;
        else if(quadrant == 3 && !firstHalf || quadrant == 4 && firstHalf)
            this.movementDirection = Directions.DOWN;

//        System.out.println(this.movementDirection);

        switch (this.movementDirection){
            case RIGHT: this.x = this.x + this.moveSpeed;   break;
            case LEFT:  this.x = this.x - this.moveSpeed;   break;
            case DOWN: this.y = this.y + this.moveSpeed;    break;
            case UP: this.y = this.y - this.moveSpeed;      break;
        }
    }

//    private

    private void patrol(){

//        this.checkForPlayer();

        this.x = this.x + patrolSpeed * (super.facingRight ? 1 : -1);
        if(super.facingRight)
            this.movementDirection = Directions.RIGHT;
        else
            this.movementDirection = Directions.LEFT;
        if(Math.abs(this.x - this.patrolHorizStartingPoint) > this.patrolHorizOffsetLimit)
            this.facingRight = !this.facingRight;
    }
}
