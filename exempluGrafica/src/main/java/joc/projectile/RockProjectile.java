package joc.projectile;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.character.Enemy;
import joc.map.Map;
import joc.player.PlayerStatus;
import joc.window.GameWindow;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RockProjectile implements Projectile {
    public static final int DEFAULT_PROJECTILE_SPEED = 20;
    public static final int DEFAULT_ENEMY_PROJECTILE_SPEED = 10;

    private int framesPassed = 0;

    private Map map;

    private boolean isPlayerThrown;

    private double x;
    private double y;
    private BufferedImage spriteImage;
    private ProjectileDirection projectileDirection;
    private GameWindow gameWindow;

    private double speed = DEFAULT_PROJECTILE_SPEED;

    private double vertSpeed, horizSpeed;
    private double angle;

    private int stayTime = 0;

    /**
     * ne da imaginea pentru orientarea aleasa
     * @param projectileDirection
     * @param sheet
     * @return
     */
    private BufferedImage loadProjectileForDirection(ProjectileDirection projectileDirection, SpriteSheet sheet){
        switch (projectileDirection){
            case UP: return sheet.getAsset(AssetList.ROCK_UP);
            case DOWN: return sheet.getAsset(AssetList.ROCK_DOWN);
            case RIGHT: return sheet.getAsset(AssetList.ROCK_RIGHT);
            case LEFT: return sheet.getAsset(AssetList.ROCK_LEFT);
            case TOPRIGHT: return sheet.getAsset(AssetList.ROCK_TOPRIGHT);
            case TOPLEFT: return sheet.getAsset(AssetList.ROCK_TOPLEFT);
            case BOTTOMLEFT: return sheet.getAsset(AssetList.ROCK_BOTTOMLEFT);
            case BOTTOMRIGHT: return sheet.getAsset(AssetList.ROCK_BOTTOMRIGHT);
            default: return sheet.getAsset(AssetList.UNKNOWN_ASSET);
        }
    }

    /**
     * ne da imaginea pe un unghi setat
     * @param ang
     * @param sheet
     * @return
     */
    private BufferedImage loadProjectileForAngle(double ang, SpriteSheet sheet){
        if(ang <= 67.5 && ang > 22.5)
            return sheet.getAsset(AssetList.ROCK_TOPRIGHT);
        else if(ang <= 112.5 && ang > 67.5)
            return sheet.getAsset(AssetList.ROCK_UP);
        else if(ang <= 157.5 && ang > 112.5)
            return sheet.getAsset(AssetList.ROCK_TOPLEFT);
        else if(ang <= 202.5 && ang > 157.5)
            return sheet.getAsset(AssetList.ROCK_LEFT);
        else if(ang <= 247.5 && ang > 202.5)
            return sheet.getAsset(AssetList.ROCK_BOTTOMLEFT);
        else if(ang <= 302.5 && ang > 247.5)
            return sheet.getAsset(AssetList.ROCK_DOWN);
        else if(ang <= 347.5 && ang > 302.5)
            return sheet.getAsset(AssetList.ROCK_BOTTOMRIGHT);
        else
            return sheet.getAsset(AssetList.ROCK_RIGHT);
    }

    /**
     * ctor proj inamic
     * @param x
     * @param y
     * @param angle
     * @param sheet
     * @param map
     * @param gameWindow
     */
    public RockProjectile(int x, int y, double angle, SpriteSheet sheet, Map map, GameWindow gameWindow){
        this.gameWindow = gameWindow;
        this.spriteImage = this.loadProjectileForAngle(angle, sheet);
        this.angle = angle;

        this.map = map;
        this.x = x;
        this.y = y;
        this.speed = DEFAULT_ENEMY_PROJECTILE_SPEED;

        this.isPlayerThrown = false;
    }

    /**
     * ctor proj player
     * @param x
     * @param y
     * @param projectileDirection
     * @param sheet
     * @param map
     * @param status
     * @param gameWindow
     */
    public RockProjectile(int x, int y, ProjectileDirection projectileDirection, SpriteSheet sheet, Map map, PlayerStatus status, GameWindow gameWindow){
        this.gameWindow = gameWindow;
        this.spriteImage = this.loadProjectileForDirection(projectileDirection, sheet);

        if(status.equals(PlayerStatus.PLAYER_IN_WATER))
            this.speed = DEFAULT_PROJECTILE_SPEED * 0.6;

        this.map = map;

        this.x = x;
        this.y = y;
        this.projectileDirection = projectileDirection;

        this.isPlayerThrown = true;
    }

    /**
     * ne spune daca a intrat in inamic
     * @param x1 stg inamic
     * @param y1 sus inamic
     * @param x2 drp inamic
     * @param y2 jos inamic
     */
    private void detectEnemyCollision(int x1, int y1, int x2, int y2){
        Rectangle r = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        for(Enemy e : this.gameWindow.getEnemies()){
            if(e.collides()) {

                Rectangle enemyRectangle = new Rectangle(
                        (int) e.getX(),
                        (int) e.getY(),
                        SpriteSheet.SPRITE_WIDTH,
                        SpriteSheet.SPRITE_HEIGHT
                );

//            System.out.println(r.toString() + ", " + enemyRectangle.toString());

                if (r.intersects(enemyRectangle)) {
                    e.getHit();
                    this.speed = 0;
                }
            }
        }
    }

    /**
     * verif cine a aruncat proj pt ca au behaviour diferit
     * @return true daca a intrat in ceva, false otherwise
     */
    public boolean update(){
        if(this.isPlayerThrown)
            return this.updatePlayer();
        else{
            return this.updateEnemyProjectile();
        }
    }

    /**
     * update pt proj inamice
     * calcula viteza cu fortele compuse
     *
     * @return
     */
    public boolean updateEnemyProjectile() {
        if (this.speed <= 0)
            return false;

        this.slowDown();

        this.vertSpeed = Math.sin(Math.toRadians(angle)) * this.speed;
        this.horizSpeed = Math.cos(Math.toRadians(angle)) * this.speed;

        this.x += this.horizSpeed;
        this.y += this.vertSpeed * -1;

        this.detectPlayerCollision();

        int x1 = (int) this.x + 15;
        int x2 = (int) (this.x + SpriteSheet.SPRITE_WIDTH - 16);
        int y1 = (int) this.y + 15;
        int y2 = (int) (this.y + SpriteSheet.SPRITE_HEIGHT - 16);

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

    /**
     * daca intra in jucator
     */
    private void detectPlayerCollision(){
        Rectangle rockMesh = new Rectangle(
                (int) this.x + 15,
                (int) this.y + 15,
                SpriteSheet.SPRITE_WIDTH - 16,
                SpriteSheet.SPRITE_HEIGHT - 16
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

    /**
     * incetinea piatra cu timpul
     */
    private void slowDown(){
        if(isPlayerThrown) {
            this.framesPassed++;
            if (this.framesPassed % (2 + this.stayTime) == 0) {
                this.speed = this.speed - 0.8;
            }

            if (this.speed == 2.5)
                this.stayTime += 2;
            if (this.speed == 1.5)
                this.stayTime += 5;
        } else {
            this.framesPassed++;
            if(this.framesPassed %(3 + this.stayTime) == 0){
                this.speed = this.speed - 0.6;

            }

            if (this.speed == 2.5)
                this.stayTime += 2;
            if (this.speed == 1.5)
                this.stayTime += 2;
        }

    }

    /**
     * update proj jucator
     * @return
     */
    public boolean updatePlayer(){
        if(this.speed <= 0)
            return false;

        this.slowDown();

        //System.out.println(this.framesPassed);
        switch (this.projectileDirection){
            case UP:
                this.y = this.y - speed;
                break;
            case DOWN:
                this.y = this.y + speed;
                break;
            case LEFT:
                this.x = this.x - speed;
                break;
            case RIGHT:
                this.x = this.x + speed;
                break;
            case TOPRIGHT:
                this.x = this.x + speed * (Math.sqrt(0.5));
                this.y = this.y - speed * (Math.sqrt(0.5));
                break;
            case TOPLEFT:
                this.x = this.x - speed * (Math.sqrt(0.5));
                this.y = this.y - speed * (Math.sqrt(0.5));
                break;
            case BOTTOMRIGHT:
                this.x = this.x + speed * (Math.sqrt(0.5));
                this.y = this.y + speed * (Math.sqrt(0.5));
                break;
            case BOTTOMLEFT:
                this.x = this.x - speed * (Math.sqrt(0.5));
                this.y = this.y + speed * (Math.sqrt(0.5));
                break;
        }

        int x1 = (int) this.x + 15;
        int x2 = (int) (this.x + SpriteSheet.SPRITE_WIDTH - 16);
        int y1 = (int) this.y + 15;
        int y2 = (int) (this.y + SpriteSheet.SPRITE_HEIGHT - 16);

        this.detectEnemyCollision(x1, y1, x2, y2);

        int x1Mat = x1/SpriteSheet.SPRITE_WIDTH;
        int x2Mat = x2/SpriteSheet.SPRITE_WIDTH;

        int y1Mat= y1/SpriteSheet.SPRITE_HEIGHT;
        int y2Mat= y2/SpriteSheet.SPRITE_HEIGHT;

        if(
                this.map.detectProjectileCollision(x1Mat,y1Mat) ||
                this.map.detectProjectileCollision(x1Mat,y2Mat) ||
                this.map.detectProjectileCollision(x2Mat,y1Mat) ||
                this.map.detectProjectileCollision(x2Mat,y2Mat)
        ){
            this.speed = 0;
        }

        return true;
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
