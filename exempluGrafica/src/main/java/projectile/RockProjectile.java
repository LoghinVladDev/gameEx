package projectile;

import assets.AssetList;
import assets.SpriteSheet;
import character.Enemy;
import map.Map;
import player.PlayerStatus;
import window.GameWindow;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RockProjectile {
    public static final int DEFAULT_PROJECTILE_SPEED = 20;

    private int framesPassed = 0;

    private Map map;

    private double x;
    private double y;
    private BufferedImage spriteImage;
    private ProjectileDirection projectileDirection;
    private GameWindow gameWindow;

    private double speed = DEFAULT_PROJECTILE_SPEED;

    private int stayTime = 0;

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

    public RockProjectile(int x, int y, ProjectileDirection projectileDirection, SpriteSheet sheet, Map map, PlayerStatus status, GameWindow gameWindow){
        this.gameWindow = gameWindow;
        this.spriteImage = this.loadProjectileForDirection(projectileDirection, sheet);

        if(status.equals(PlayerStatus.PLAYER_IN_WATER))
            this.speed = DEFAULT_PROJECTILE_SPEED * 0.6;

        this.map = map;

        this.x = x;
        this.y = y;
        this.projectileDirection = projectileDirection;
    }

    private void detectEnemyCollision(int x1, int y1, int x2, int y2){
        Rectangle r = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        for(Enemy e : this.gameWindow.getEnemies()){
            Rectangle enemyRectangle = new Rectangle(
                    (int)e.getX(),
                    (int)e.getY(),
                    SpriteSheet.SPRITE_WIDTH,
                    SpriteSheet.SPRITE_HEIGHT
            );

//            System.out.println(r.toString() + ", " + enemyRectangle.toString());

            if(r.intersects(enemyRectangle)){
                e.getHit();
                this.speed = 0;
            }
        }
    }

    public boolean update(){

        if(this.speed <= 0)
            return false;

        this.framesPassed ++;
        if(this.framesPassed % (2 + this.stayTime) == 0){
            this.speed = this.speed - 0.8;
        }

        if(this.speed == 2.5)
            this.stayTime += 2;
        if(this.speed == 1.5)
            this.stayTime += 5;

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

        int x1 = (int) this.x;
        int x2 = (int) (this.x + SpriteSheet.SPRITE_WIDTH - 1);
        int y1 = (int) this.y;
        int y2 = (int) (this.y + SpriteSheet.SPRITE_HEIGHT - 1);

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
