package joc.item;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.player.Player;

import java.awt.*;

public class Heart extends Item {

    public Heart(Player player, SpriteSheet sheet, float x, float y){
        super(player, sheet, x, y);

        super.sprite = sheet.getAsset(AssetList.HEART_ICON);
    }

    /**
     * verif daca player-ul a luat item-ul
     */
    private void checkPlayerCollision(){
        Rectangle playerMesh = new Rectangle(
                (int)this.player.getX(),
                (int)this.player.getY(),
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT
        );

        Rectangle heartMesh = new Rectangle(
                (int)this.x,
                (int)this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT
        );

        if(playerMesh.intersects(heartMesh)){
            this.toBeDeSpawned = true;
//            Item.getGameWorldItems().remove(this);
            this.player.heal();
        }
    }

    @Override
    public void update() {
        this.animate();
        this.checkPlayerCollision();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(
                this.sprite,
                (int)this.x,
                (int)this.y,
                SpriteSheet.SPRITE_WIDTH,
                SpriteSheet.SPRITE_HEIGHT,
                null
        );
    }

}
