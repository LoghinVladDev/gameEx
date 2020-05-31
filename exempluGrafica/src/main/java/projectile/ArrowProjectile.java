package projectile;

import assets.AssetList;
import assets.SpriteSheet;
import map.Map;
import window.GameWindow;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ArrowProjectile implements Projectile {
    public static final int DEFAULT_ENEMY_PROJECTILE_SPEED = 23;

    private int framesPassed = 0;

    private Map map;

    private boolean isPlayerThrown;

    private double x;
    private double y;
    private BufferedImage spriteImage;
    private GameWindow gameWindow;

    private double speed;

    private double vertSpeed, horizSpeed;
    private double angle;

    private int stayTime = 0;

    public ArrowProjectile(int x, int y, double angle, SpriteSheet sheet, Map map, GameWindow gameWindow){
        this.gameWindow = gameWindow;
        //this.spriteImage = this.loadProjectileForAngle(angle, sheet);
        this.angle = angle;
        this.spriteImage = SpriteSheet.rotate(sheet.getAsset(AssetList.ARROW_RIGHT), angle);

        this.map = map;
        this.x = x;
        this.y = y;
        this.speed = DEFAULT_ENEMY_PROJECTILE_SPEED;

        this.isPlayerThrown = false;
    }

    public boolean update(){
        return this.updateEnemyProjectile();
    }

    public boolean updateEnemyProjectile() {
        if (this.speed <= 0)
            return false;

        this.slowDown();

        this.vertSpeed = Math.sin(Math.toRadians(angle)) * this.speed;
        this.horizSpeed = Math.cos(Math.toRadians(angle)) * this.speed;

        this.x += this.horizSpeed;
        this.y += this.vertSpeed * -1;

        this.detectPlayerCollision();

        int x1 = (int) this.x;
        int x2 = (int) (this.x + SpriteSheet.SPRITE_WIDTH - 1);
        int y1 = (int) this.y;
        int y2 = (int) (this.y + SpriteSheet.SPRITE_HEIGHT - 1);

        int x1Mat = x1 / SpriteSheet.SPRITE_WIDTH;
        int x2Mat = x2 / SpriteSheet.SPRITE_WIDTH;

        int y1Mat = y1 / SpriteSheet.SPRITE_HEIGHT;
        int y2Mat = y2 / SpriteSheet.SPRITE_HEIGHT;

        if (
                this.map.detectEnemyProjectileCollision(x1Mat, y1Mat) ||
                        this.map.detectEnemyProjectileCollision(x1Mat, y2Mat) ||
                        this.map.detectEnemyProjectileCollision(x2Mat, y1Mat) ||
                        this.map.detectEnemyProjectileCollision(x2Mat, y2Mat)
        ) {
            this.speed = 0;
        }

        return true;
    }

    private void detectPlayerCollision(){
        Rectangle rockMesh = new Rectangle(
                (int) this.x,
                (int) this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT
        );

        Rectangle playerMesh = new Rectangle(
                (int) GameWindow.getInstance().getPlayer().getX(),
                (int) GameWindow.getInstance().getPlayer().getY(),
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT
        );

        if(rockMesh.intersects(playerMesh)){
            GameWindow.getInstance().getPlayer().getHit();
            this.speed = 0;
        }
    }

    private void slowDown(){
        this.framesPassed++;
        if(this.framesPassed %(1 + this.stayTime) == 0){
            this.speed = this.speed - 0.6;

        }

        if (this.speed == 2.5)
            this.stayTime += 2;
        if (this.speed == 1.5)
            this.stayTime += 2;

    }

    public void draw(Graphics g){
        if(this.speed <= 0){
            return;
        }
        g.drawImage(
                this.spriteImage,
                (int)this.x,
                (int)this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT,
                null
        );
    }
}
