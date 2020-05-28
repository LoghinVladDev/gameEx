package map;

import assets.AssetList;
import assets.SpriteSheet;
import assets.tile.Castle;
import assets.tile.Tile;
import assets.tile.Tower;
import character.ArcherEnemy;
import character.Enemy;
import character.RegularEnemy;
import character.RockThrowerEnemy;
import player.Directions;
import player.Player;
import player.PlayerStatus;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Map {
    private Tile[][] mapLayout;
    private SpriteSheet spriteSheet;
    private int width;
    private int height;

    private Player player;

    private List<Enemy> enemyList;

    public List<Enemy> getEnemyList() {
        return enemyList;
    }

    public Player getPlayer() {
        return player;
    }

    public static final String GAME_MAP_1 = "/maps/Map1.txt";

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map(int width, int height, SpriteSheet spriteSheet){
        this.width = width;
        this.height = height;
        this.mapLayout = new Tile[height][width];
        this.spriteSheet = spriteSheet;
        this.enemyList = new ArrayList<>();
    }

    public void loadMap(String path) {
        AssetList assetType;

        /*
         * INCARCARE OBIECTE STATICE
         */
        try {
            Scanner input = new Scanner(new File(Map.class.getResource(path).getFile()));
            for(int i = 0 ;i < height; i++){
                for(int j = 0; j < width; j++){
                    assetType = this.spriteSheet.getAssetTypeByNumber(input.nextInt());

                    if(assetType.equals(AssetList.TOWER))
                        this.mapLayout[i][j] = new Tower(this.spriteSheet);
                    else if(assetType.equals(AssetList.CASTLE_GRASS_TILE))
                        this.mapLayout[i][j] = new Castle(this.spriteSheet, Castle.CASTLE_GRASS);
                    else if(assetType.equals(AssetList.CASTLE_SAND_TILE))
                        this.mapLayout[i][j] = new Castle(this.spriteSheet, Castle.CASTLE_SAND);
                    else
                        this.mapLayout[i][j] = new Tile(assetType, this.spriteSheet);
                    this.mapLayout[i][j].setLocation(j,i);
                }
            }
        }
        catch (FileNotFoundException e){
            System.out.println(e.toString());
        }

        /*
            INCARCARE OBIECTE DINAMICE : PLAYER, INAMICI
         */
        try{
            Scanner input = new Scanner(new File(Map.class.getResource(path.replace(".txt", "Enemies.txt")).getFile()));
            for(int i = 0 ;i < height; i++){
                for(int j = 0; j < width; j++){
                    int characterIndicator = input.nextInt();

                    if(characterIndicator == 1)
                        this.player = new Player(j * SpriteSheet.SPRITE_WIDTH, i * SpriteSheet.SPRITE_HEIGHT, this.spriteSheet);
                    if(characterIndicator == 2)
                        this.enemyList.add(new RegularEnemy(j * SpriteSheet.SPRITE_WIDTH, i * SpriteSheet.SPRITE_HEIGHT, this.spriteSheet));
                    if(characterIndicator == 3)
                        this.enemyList.add(new ArcherEnemy(j * SpriteSheet.SPRITE_WIDTH, i * SpriteSheet.SPRITE_HEIGHT, this.spriteSheet));
                    if(characterIndicator == 4)
                        this.enemyList.add(new RockThrowerEnemy(j * SpriteSheet.SPRITE_WIDTH, i * SpriteSheet.SPRITE_HEIGHT, this.spriteSheet));
                }
            }
        }
        catch (FileNotFoundException e){
            System.out.println(e.toString());
        }
    }

    public void drawMap(Graphics graphics){
        for(int i = 0; i < this.mapLayout.length; i++){
            for(int j = 0; j < this.mapLayout[i].length; j++){
                this.mapLayout[i][j].draw(graphics);
            }
        }
    }

    public boolean detectProjectileCollision(int x, int y){
        if(x >= 0 && x < this.width && y >= 0 && y < this.height) {
            this.mapLayout[y][x].destroy();

            //Castle c = (Castle)this.mapLayout[y][x];

            //if(c!=null)
                //c.destroy();

            if(this.mapLayout[y][x].getType().equals(AssetList.TOWER))
                return true;
            if (this.mapLayout[y][x].getType().equals(AssetList.CASTLE_GRASS_TILE))
                return true;
            if(this.mapLayout[y][x].getType().equals(AssetList.MOUNTAIN_TILE))
                return true;
            if(this.mapLayout[y][x].getType().equals(AssetList.TREE_TILE))
                return true;
        }
        return false;
    }



    public PlayerStatus detectCollision(int x, int y){
        if(this.mapLayout[y][x].getType().equals(AssetList.TOWER))
            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.MOUNTAIN_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.CASTLE_GRASS_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.CASTLE_SAND_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.DAMAGED_CASTLE_GRASS_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.WATER_TILE))
            return PlayerStatus.PLAYER_IN_WATER;
        if(this.mapLayout[y][x].getType().equals(AssetList.TREE_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
        return PlayerStatus.PLAYER_NO_COLLIDE;
    }

    public List<PlayerStatus> isPlayerAllowed(int x1, int y1, int x2, int y2, Directions direction){
        List<PlayerStatus> collisionList = new ArrayList<>();

        /*if(x1 < 0 || x2 >= this.width * SpriteSheet.SPRITE_WIDTH || y1 < 0 || y2 >= this.height * SpriteSheet.SPRITE_HEIGHT){

        }*/

        if(y1 < 0)
            collisionList.add(PlayerStatus.PLAYER_COLLIDE_TOP);
        if(x1 < 0)
            collisionList.add(PlayerStatus.PLAYER_COLLIDE_LEFT);
        if(y2 >= this.height * SpriteSheet.SPRITE_HEIGHT)
            collisionList.add(PlayerStatus.PLAYER_COLLIDE_BOTTOM);
        if(x2 >= this.width * SpriteSheet.SPRITE_WIDTH)
            collisionList.add(PlayerStatus.PLAYER_COLLIDE_RIGHT);

        if(!collisionList.isEmpty())
            return collisionList;

        int x1Mat = (x1+4)/SpriteSheet.SPRITE_WIDTH;
        int x2Mat = (x2-4)/SpriteSheet.SPRITE_WIDTH;

        int y1Mat= (y1+4)/SpriteSheet.SPRITE_HEIGHT;
        int y2Mat= (y2-4)/SpriteSheet.SPRITE_HEIGHT;

        /*
         *  x1, y1 - stanga sus
         *  x2, y1 - dreapta sus
         *  x1, y2 - stanga jos
         *  x2 y2 - dreapta jos
         */

        PlayerStatus leftTopCorner = this.detectCollision(x1Mat, y1Mat); /// stg sus
//        if(!status.equals(PlayerStatus.PLAYER_NO_COLLIDE))
//            return status;

        PlayerStatus rightTopCorner = this.detectCollision(x2Mat, y1Mat);
//        if(!status.equals(PlayerStatus.PLAYER_NO_COLLIDE))
//            return status;

        PlayerStatus leftBottomCorner = this.detectCollision(x1Mat, y2Mat);
//        if(!status.equals(PlayerStatus.PLAYER_NO_COLLIDE))
//            return status;

        PlayerStatus rightBottomCorner = this.detectCollision(x2Mat, y2Mat);
//        if(!status.equals(PlayerStatus.PLAYER_NO_COLLIDE))
//            return status;

       /*if(leftTopCorner.equals(PlayerStatus.PLAYER_COLLIDE)){
           if(direction.equals(Directions.LEFT))
               collisionList.add(PlayerStatus.PLAYER_COLLIDE_LEFT);
           if(direction.equals(Directions.UP))
               collisionList.add(PlayerStatus.PLAYER_COLLIDE_TOP);
           if(direction.equals(Directions.LEFT_UP)) {
               collisionList.add(PlayerStatus.PLAYER_COLLIDE_TOP);
               collisionList.add(PlayerStatus.PLAYER_COLLIDE_LEFT);
           }
       }*/

        switch (direction){
            case DOWN:
                if(leftBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE) || rightBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_BOTTOM);
                break;
            case LEFT:
                if(leftBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE) || leftTopCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_LEFT);
                break;
            case RIGHT:
                if(rightBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE) || rightTopCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_RIGHT);
                break;
            case UP:
                if(rightTopCorner.equals(PlayerStatus.PLAYER_COLLIDE) || leftTopCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_TOP);
                break;
            case LEFT_UP:
                if(leftBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_LEFT);
                if(rightTopCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_TOP);
                if(
                        leftTopCorner.equals(PlayerStatus.PLAYER_COLLIDE) &&
                        !leftBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE) &&
                        !rightTopCorner.equals(PlayerStatus.PLAYER_COLLIDE)
                ){
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_TOP);
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_LEFT);
                }
                break;
            case LEFT_DOWN:
                if(leftTopCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_LEFT);
                if(rightBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_BOTTOM);
                if(
                        leftBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE) &&
                        !leftTopCorner.equals(PlayerStatus.PLAYER_COLLIDE) &&
                        !rightBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE)
                ){
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_BOTTOM);
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_LEFT);
                }
                break;
            case RIGHT_UP:
                if(rightBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_RIGHT);
                if(leftTopCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_TOP);
                if(
                        rightTopCorner.equals(PlayerStatus.PLAYER_COLLIDE) &&
                        !rightBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE) &&
                        !leftTopCorner.equals(PlayerStatus.PLAYER_COLLIDE)
                ){
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_TOP);
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_RIGHT);
                }
                break;
            case RIGHT_DOWN:
                if(rightTopCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_RIGHT);
                if(leftBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE))
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_BOTTOM);
                if(
                        rightBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE) &&
                        !rightTopCorner.equals(PlayerStatus.PLAYER_COLLIDE) &&
                        !leftBottomCorner.equals(PlayerStatus.PLAYER_COLLIDE)
                ){
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_BOTTOM);
                    collisionList.add(PlayerStatus.PLAYER_COLLIDE_RIGHT);
                }
                break;
        }

        if(
            leftBottomCorner.equals(PlayerStatus.PLAYER_IN_WATER) &&
            leftTopCorner.equals(PlayerStatus.PLAYER_IN_WATER) &&
            rightBottomCorner.equals(PlayerStatus.PLAYER_IN_WATER) &&
            rightTopCorner.equals(PlayerStatus.PLAYER_IN_WATER)
        )
            collisionList.add(PlayerStatus.PLAYER_IN_WATER);

        return collisionList;
    }

}
