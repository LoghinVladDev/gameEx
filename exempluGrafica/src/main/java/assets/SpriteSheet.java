package assets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {
    private BufferedImage spriteImage;
    private static final int DECAL_SPRITE_WIDTH = 48 , DECAL_SPRITE_HEIGHT = 48;
    public static final int SPRITE_WIDTH=36,SPRITE_HEIGHT=36;//constante si nu pot fi  modificate
    public SpriteSheet(String bufferedImageLocation)  {
        try {


            this.spriteImage = ImageIO.read(SpriteSheet.class.getResource(bufferedImageLocation));
        }
        catch (IOException exception){
            System.out.println(exception.toString());
            exception.printStackTrace();//se afiseaza stiva de apeluri in cazul in care a crapat
        }
    }

    public BufferedImage getSpriteImage() {
        return spriteImage;
    }

    public static BufferedImage rotate(BufferedImage bimg, double angle) {

        int w = bimg.getWidth();
        int h = bimg.getHeight();

        BufferedImage rotated = new BufferedImage(w, h, bimg.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.rotate(Math.toRadians(360 - angle), w/2, h/2);
        graphic.drawImage(bimg, null, 0, 0);
        graphic.dispose();
        return rotated;
    }


    /**
     * Returns BufferedImage for selected asset type
     * @param assetType Selected asset type
     * @return BufferedImage of the selected sprite
     */
    public BufferedImage getAsset(AssetList assetType){
        switch (assetType){
            case GRASS_TILE:                return this.crop(0,0);
            case SAND_TILE:                 return this.crop(1,0);
            case WATER_TILE:                return this.crop(2,0);
            case MOUNTAIN_TILE:             return this.crop(3,0);
            case CASTLE_GRASS_TILE:         return this.crop(0,1);
            case DAMAGED_CASTLE_GRASS_TILE: return this.crop(1,1);
            case CASTLE_SAND_TILE:          return this.crop(2,1);
            case TREE_TILE:                 return this.crop(3,1);
            case PLAYER_LEFT:               return this.crop(0,2);
            case PLAYER_RIGHT:              return this.crop(1,2);
            case ROCK_UP:                   return this.crop(2,2);
            case ROCK_DOWN:                 return this.crop(3,2);
            case ROCK_LEFT:                 return this.crop(0,3);
            case ROCK_RIGHT:                return this.crop(1,3);
            case ROCK_TOPRIGHT:             return this.crop(2, 3);
            case ROCK_TOPLEFT:              return this.crop(3, 3);
            case ROCK_BOTTOMLEFT:           return this.crop(4, 2);
            case ROCK_BOTTOMRIGHT:          return this.crop(4, 3);
            case ENEMY_REGULAR_LEFT:        return this.crop(0, 4);
            case ENEMY_REGULAR_RIGHT:       return this.crop(1, 4);
            case TOWER:                     return this.crop(4, 1);
            case HEART_ICON:                return this.crop(4,0);
            case KEY_ICON:                  return this.crop(5,0);
            case ENEMY_ARCHER_LEFT:         return this.crop(5, 4);
            case ENEMY_ARCHER_RIGHT:        return this.crop(6,4);
            case ENEMY_ROCK_THROWER_LEFT:   return this.crop(3, 4);
            case ENEMY_ROCK_THROWER_RIGHT:  return this.crop(4,4);
            case ARROW_BOTTOM:              return this.crop(7 ,1);
            case ARROW_BOTTOM_LEFT:         return this.crop(7, 0);
            case ARROW_LEFT:                return this.crop(5, 2);
            case ARROW_TOP_LEFT:            return this.crop(7, 4);
            case ARROW_TOP:                 return this.crop(5, 1);
            case ARROW_TOP_RIGHT:           return this.crop(6, 1);
            case ARROW_RIGHT:               return this.crop(7, 2);
            case ARROW_BOTTOM_RIGHT:        return this.crop(7, 3);
            case SPIKE_HIDDEN:              return this.crop(7, 0);
            case SPIKE_SHOWN:               return this.crop(6, 0);
            default:                        return this.crop(6, 3);
        }
    }

    public AssetList getAssetTypeByNumber(int ordinal){
        switch (ordinal){
            case 0:  return AssetList.GRASS_TILE;
            case 1:  return AssetList.SAND_TILE;
            case 2:  return AssetList.WATER_TILE;
            case 3:  return AssetList.MOUNTAIN_TILE;
            case 4:  return AssetList.CASTLE_GRASS_TILE;
            case 5:  return AssetList.DAMAGED_CASTLE_GRASS_TILE;
            case 6:  return AssetList.CASTLE_SAND_TILE;
            case 7:  return AssetList.TREE_TILE;
            case 8:  return AssetList.PLAYER_LEFT;
            case 9:  return AssetList.PLAYER_RIGHT;
            case 10: return AssetList.ROCK_UP;
            case 11: return AssetList.ROCK_DOWN;
            case 12: return AssetList.ROCK_LEFT;
            case 13: return AssetList.ROCK_RIGHT;
            case 14: return AssetList.ROCK_TOPRIGHT;
            case 15: return AssetList.ROCK_TOPLEFT;
            case 16: return AssetList.ROCK_BOTTOMLEFT;
            case 17: return AssetList.ROCK_BOTTOMRIGHT;
            case 18: return AssetList.ENEMY_REGULAR_LEFT;
            case 19: return AssetList.ENEMY_REGULAR_RIGHT;
            case 20: return AssetList.TOWER;
            case 21: return AssetList.HEART_ICON;
            case 22: return AssetList.KEY_ICON;
            case 23: return AssetList.ENEMY_ARCHER_LEFT;
            case 24: return AssetList.ENEMY_ARCHER_RIGHT;
            case 25: return AssetList.ENEMY_ROCK_THROWER_LEFT;
            case 26: return AssetList.ENEMY_ROCK_THROWER_RIGHT;
            case 27: return AssetList.ARROW_TOP;
            case 28: return AssetList.ARROW_BOTTOM;
            case 29: return AssetList.ARROW_LEFT;
            case 30: return AssetList.ARROW_RIGHT;
            case 31: return AssetList.ARROW_TOP_LEFT;
            case 32: return AssetList.ARROW_TOP_RIGHT;
            case 33: return AssetList.ARROW_BOTTOM_LEFT;
            case 34: return AssetList.ARROW_BOTTOM_RIGHT;
            case 35: return AssetList.SPIKE_HIDDEN;
            case 36: return AssetList.SPIKE_SHOWN;
            default: return AssetList.UNKNOWN_ASSET;
        }
    }

    public BufferedImage crop(int x, int y){
        return this.spriteImage.getSubimage(x * DECAL_SPRITE_WIDTH, y * DECAL_SPRITE_HEIGHT, DECAL_SPRITE_WIDTH, DECAL_SPRITE_HEIGHT);
    }
}
