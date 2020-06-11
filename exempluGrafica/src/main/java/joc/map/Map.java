package joc.map;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.assets.tile.Castle;
import joc.assets.tile.Tile;
import joc.assets.tile.Tower;
import joc.character.*;
import joc.player.Directions;
import joc.player.Player;
import joc.player.PlayerStatus;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class Map {
    private Tile[][] mapLayout;
    private SpriteSheet spriteSheet;
    private int width;
    private int height;

    public static final int MAP_DEFAULT_KEY_COUNT = 5;

    private Player player;

    private int keyCount = 0;

    private List<Enemy> enemyList;

    /**
     * Returneaza inamicii incarcati
     * @return lista inamici
     */
    public List<Enemy> getEnemyList() {
        return enemyList;
    }

    /**
     * Returneaza player-ul
     * @return
     */
    public Player getPlayer() {
        return player;
    }

    public static final String GAME_MAP_1 = "/maps/Map1.txt";
    public static final String GAME_MAP_2 = "/maps/Map2.txt";
    public static final String GAME_MAP_3 = "/maps/Map3.txt";
    public static final String GAME_TEST_MAP = "/maps/MapTest.txt";

    private List<String> mapsList = new ArrayList<>();

    /**
     * getter latime
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * getter inaltime
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * seteaza cate chei vor aparea pe harta
     * @param keyCount
     */
    public void setKeyCount(int keyCount) {
        this.keyCount = keyCount;
    }

    /**
     * Ctor harta
     * @param width cate patratele latime
     * @param height cate inaltime
     * @param spriteSheet de unde isi va lua grafica
     */
    public Map(int width, int height, SpriteSheet spriteSheet){
        this.width = width;
        this.height = height;
        this.mapLayout = new Tile[height][width];
        this.spriteSheet = spriteSheet;
        this.enemyList = new ArrayList<>();

        this.addMapsToList();
    }

    /**
     * lista harti
     * @return
     */
    public List<String> getMapsList() {
        return mapsList;
    }

    /**
     * init harti
     */
    private void addMapsToList(){
//        this.mapsList.add(GAME_MAP_1); // TODO : REMOVE THIS LINE AFTER RELEASE
        this.mapsList.add(GAME_MAP_3);
        this.mapsList.add(GAME_MAP_2);
        this.mapsList.add(GAME_MAP_1);
    }

    /**
     * cate chei am pe harta
     * @return
     */
    public int getKeyCount() {
        return keyCount;
    }

    /**
     * generare chei ( un hash : castel - > boolean , castel -> true = castelul are cheie, false = castelul nu are cheie )
     * @param castle // lista tuturor castelelor
     * @return // hash-u
     */
    private java.util.Map<Tile, Boolean> generateKeys(List<Tile> castle){
        java.util.Map<Tile, Boolean> hasKey = new HashMap<>();

        if(this.keyCount > castle.size())
            this.keyCount = castle.size();

        for(Tile t : castle){
            hasKey.put(t, false);
        }
        int keyCount = this.keyCount;
        while(keyCount-- > 0){
            int castleNo = (int)(Math.random() * castle.size());

            while(hasKey.get(castle.get(castleNo))){
                castleNo = (int)(Math.random() * castle.size());
            }

            hasKey.put(castle.get(castleNo), true);
        }

        return hasKey;
    }

    /**
     * incarcare harta de la path
     * @param path path-ul hartii
     */
    public void loadMap(String path) {
        AssetList assetType;

        List<Tile> castles = new ArrayList<>();

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
                    else if(assetType.equals(AssetList.CASTLE_GRASS_TILE)) {
                        this.mapLayout[i][j] = new Castle(this.spriteSheet, Castle.CASTLE_GRASS);
                        castles.add(this.mapLayout[i][j]);
                    }
                    else if(assetType.equals(AssetList.CASTLE_SAND_TILE)) {
                        this.mapLayout[i][j] = new Castle(this.spriteSheet, Castle.CASTLE_SAND);
                        castles.add(this.mapLayout[i][j]);
                    }
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
                    if(characterIndicator == 5)
                        this.enemyList.add(new Spike(j * SpriteSheet.SPRITE_WIDTH, i * SpriteSheet.SPRITE_HEIGHT, this.spriteSheet));
                }
            }
        }
        catch (FileNotFoundException e){
            System.out.println(e.toString());
        }

        java.util.Map<Tile, Boolean> hasKey = this.generateKeys(castles);
        hasKey.forEach((castle,willSpawnKey) -> {
            if(willSpawnKey){
                castle.addOnDropKey(this.player);
            }
        });

    }

    /**
     * desenarea hartii ( fiecare tile se redeseanaza)
     * @param graphics unde va desena
     */
    public void drawMap(Graphics graphics){
        for (Tile[] tileRow : this.mapLayout) {
            for (Tile tile : tileRow) {
                tile.draw(graphics);
            }
        }
    }

    /**
     * detecteaza coliziunile proiectilelor, apeleaza destroy() per obiect pentru a "distruge" tile-ul, se distruge daca e configurat sa o faca
     * @param x horiz piatra
     * @param y vert piatra
     * @return true daca a intrat in ceva, false otherwise
     */
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

    /**
     * pietre inamici coliziune, nu apeleaza destroy(), dar verifica daca a intrat in ceva
     * @param x horiz proj
     * @param y vert proj
     * @return true daca a intrat in cv, false otherwise
     */
    public boolean detectEnemyProjectileCollision(int x, int y){
        if(x >= 0 && x < this.width && y >= 0 && y < this.height) {
//            this.mapLayout[y][x].destroy();

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

    /**
     * detectare coliziune player: verifica cu fiecare Tile care are coliziune
     * @param x horiz player
     * @param y vert player
     * @return status : collide, in water, no_collide
     */
    public PlayerStatus detectCollision(int x, int y){
        if(this.mapLayout[y][x].getType().equals(AssetList.TOWER))
            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.MOUNTAIN_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.CASTLE_GRASS_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.CASTLE_SAND_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
//        if(this.mapLayout[y][x].getType().equals(AssetList.DAMAGED_CASTLE_GRASS_TILE))
//            return PlayerStatus.PLAYER_COLLIDE;
        if(this.mapLayout[y][x].getType().equals(AssetList.WATER_TILE))
            return PlayerStatus.PLAYER_IN_WATER;
        if(this.mapLayout[y][x].getType().equals(AssetList.TREE_TILE))
            return PlayerStatus.PLAYER_COLLIDE;
        return PlayerStatus.PLAYER_NO_COLLIDE;
    }

    /**
     * fct apleata din player, returneaza toate coliziunile la pozitia data, cu directia de miscare alease
     * @param x1 "stanga" jucator
     * @param y1 "sus" jucator          -> formeaza o "cutie" de coliziune
     * @param x2 "dreapta" jucator
     * @param y2 "jos" jucator
     * @param direction directia in care se deplaseaza
     * @return lista coliziuni
     */
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
        PlayerStatus rightTopCorner = this.detectCollision(x2Mat, y1Mat);
        PlayerStatus leftBottomCorner = this.detectCollision(x1Mat, y2Mat);
        PlayerStatus rightBottomCorner = this.detectCollision(x2Mat, y2Mat);

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
