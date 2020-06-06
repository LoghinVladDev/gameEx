package joc.item;

import joc.assets.AssetList;
import joc.assets.SpriteSheet;
import joc.player.Player;

import java.awt.*;

public class Key extends Item {
    public Key(Player player, SpriteSheet sheet, float xPos, float yPos) {
        super(player, sheet, xPos, yPos);

        this.sprite = sheet.getAsset(AssetList.KEY_ICON);
//        System.out.println("se spawneaza o cheie dar nu prea , x = " + xPos + ", y = " + yPos );
    }

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
            this.player.addKey();
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
