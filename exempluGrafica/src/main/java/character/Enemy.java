package character;

import map.Map;
import player.Player;

import java.awt.*;

public interface Enemy {
    void draw(Graphics g);
    void update();
    void setPlayer(Player player);
    void setMap(Map map);
    boolean isDead();
    void getHit();
    float getX();
    float getY();
    default boolean collides(){
        return true;
    }
}
