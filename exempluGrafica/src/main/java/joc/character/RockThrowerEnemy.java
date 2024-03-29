package joc.character;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.player.Directions;
import joc.player.PlayerStatus;
import joc.window.GameWindow;

import java.util.List;

/**
 * refer to archer enemy.
 *
 * dist. de aruncare e mai mica
 * nu arunca cu sageti, arunca cu pietre
 */
public class RockThrowerEnemy extends HumanoidEnemy {

    private static final int DEFAULT_ROCK_THROW_DISTANCE = 250;

    private int patrolHorizStartingPoint = 0;
    private int patrolHorizOffsetLimit = 48;

    private int patrolSpeed = 1;

    private int hitCapacity = 100;

    private int takesDamageOnRock = 50;

    private static final double RECOIL_ANGLE = 30;

    private int timeout = 0;

    private static final int THROW_ROCK_TIMEOUT = 60;
    private static final int GET_HIT_TIMEOUT = 30; // in numar de cadre, 30 = 0.5s
    private static final int HIT_PLAYER_TIME = 90;

    private int timeNearPlayer = 0;

    private float moveSpeed = 1.5f;
    public RockThrowerEnemy(int x, int y, SpriteSheet sheet){
        super(x,y);
        super.sheet = sheet;

        this.patrolHorizStartingPoint = (int)this.x;

        this.isFollowingPlayer = false;

        super.enemySpriteLeft = sheet.getAsset(AssetList.ENEMY_ROCK_THROWER_LEFT);
        super.enemySpriteRight = sheet.getAsset(AssetList.ENEMY_ROCK_THROWER_RIGHT);
    }
    private Directions movementDirection;

    public void getHit(){

//        System.out.println("Am fost lovit!");

        this.hitCapacity -= this.takesDamageOnRock;

        if(this.hitCapacity < 0) {
            if(!this.dead){
                this.dropHeart();
            }

            this.dead = true;
        }
        else
            this.timeout = GET_HIT_TIMEOUT; /// PAIN STATE

    }

    @Override
    public void update() {
        float oldX = this.x;
        float oldY = this.y;

        if(this.timeout > 0){
            this.timeout--;
            return;
        }

        if(this.collidesWithPlayer()) {

            if(this.timeNearPlayer < HIT_PLAYER_TIME){
                this.timeNearPlayer++;
            }
            else if(this.timeNearPlayer == HIT_PLAYER_TIME){
                this.player.getHit();
                this.timeNearPlayer = 0;
            }

            return;
        } else {
            this.timeNearPlayer = 0;
        }

        if(this.player.isDead())
            this.isFollowingPlayer = false;

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

        float distance = (float) Math.sqrt(Math.pow(Math.abs(height),2) + Math.pow(Math.abs(width),2));

//        System.out.println(distance);

        if(distance > DEFAULT_ROCK_THROW_DISTANCE) {
            boolean firstHalf = false;

            int quadrant = 0;

            if (height >= 0 && width >= 0)
                quadrant = 1;
            if (height >= 0 && width < 0)
                quadrant = 2;
            if (height < 0 && width < 0)
                quadrant = 3;
            if (height < 0 && width >= 0)
                quadrant = 4;

            if (Math.abs(height) > Math.abs(width)) {
                if (quadrant == 1 || quadrant == 3)
                    firstHalf = false;
                else
                    firstHalf = true;
            } else {
                if (quadrant == 1 || quadrant == 3)
                    firstHalf = true;
                else
                    firstHalf = false;
            }

//        System.out.println(quadrant + ", " + firstHalf);

            if (quadrant == 1 && firstHalf || quadrant == 4 && !firstHalf)
                this.movementDirection = Directions.RIGHT;
            else if (quadrant == 1 && !firstHalf || quadrant == 2 && firstHalf)
                this.movementDirection = Directions.UP;
            else if (quadrant == 2 && !firstHalf || quadrant == 3 && firstHalf)
                this.movementDirection = Directions.LEFT;
            else if (quadrant == 3 && !firstHalf || quadrant == 4 && firstHalf)
                this.movementDirection = Directions.DOWN;

//        System.out.println(this.movementDirection);

            switch (this.movementDirection) {
                case RIGHT:
                    this.x = this.x + this.moveSpeed;
                    break;
                case LEFT:
                    this.x = this.x - this.moveSpeed;
                    break;
                case DOWN:
                    this.y = this.y + this.moveSpeed;
                    break;
                case UP:
                    this.y = this.y - this.moveSpeed;
                    break;
            }
        }
        else {
//            System.out.println("ARUNC CHIATRA IN PLAYER");
            double slope = height/width;
//            System.out.println(height + ", " + width);
            double angle = Math.atan(slope);

            int quadrant = 0;

            if (height >= 0 && width >= 0)
                quadrant = 1;
            if (height >= 0 && width < 0)
                quadrant = 2;
            if (height < 0 && width < 0)
                quadrant = 3;
            if (height < 0 && width >= 0)
                quadrant = 4;

            double degreeAngle = Math.toDegrees(angle);

            switch (quadrant){
                case 1 : degreeAngle = degreeAngle; break;
                case 2 : degreeAngle = 90 + (90 + degreeAngle); break;
                case 3 : degreeAngle = degreeAngle + 180; break;
                case 4 : degreeAngle = 270 + (90 + degreeAngle); break;
            }

            System.out.println(degreeAngle);

            degreeAngle = this.getRecoilAngle(degreeAngle);

            GameWindow.getInstance().enemyThrowsRock((int)this.x, (int)this.y, degreeAngle);

            this.timeout = THROW_ROCK_TIMEOUT;

        }
    }

    private double getRecoilAngle(double degreeAngle){
        double res = degreeAngle + (Math.random() * RECOIL_ANGLE - RECOIL_ANGLE/2);

        if(res < 0){
            res = 360 + res;
        } else if(res >= 360){
            res = res - 360;
        }

        return res;
    }

//    private

    private void patrol(){

        this.x = this.x + patrolSpeed * (super.facingRight ? 1 : -1);
        if(super.facingRight)
            this.movementDirection = Directions.RIGHT;
        else
            this.movementDirection = Directions.LEFT;
        if(Math.abs(this.x - this.patrolHorizStartingPoint) > this.patrolHorizOffsetLimit)
            this.facingRight = !this.facingRight;
    }
}
