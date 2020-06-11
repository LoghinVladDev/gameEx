package joc.character;

import joc.map.Map;
import joc.player.Player;

import java.awt.*;

/**
 * Interfata - > nu are propr., doar functii (ce face un inamic ? )
 */
public interface Enemy {
    /**
     * Redraw
     * @param g unde
     */
    void draw(Graphics g);

    /**
     * Update
     */
    void update();

    /**
     * I se da jucatorul
     * @param player
     */
    void setPlayer(Player player);

    /**
     * Setter mapa
     * @param map
     */
    void setMap(Map map);
    /**
     * true daca e mort, false otherwise
     */
    boolean isDead();

    /**
     * isi ia o lovitura
     */
    void getHit();

    /**
     * getter horiz
     * @return
     */
    float getX();

    /**
     * getter vert
     * @return
     */
    float getY();

    /**
     * true daca colideaza, false otherwise
     * daca noi nu facem override, standard e true.
     * by default, all enemies collide with player
     * humanoidEnemy collides by default, nu suprascriem
     * Spike doesn't collide => suprascirem.
     * @return
     */
    default boolean collides(){
        return true;
    }
}
