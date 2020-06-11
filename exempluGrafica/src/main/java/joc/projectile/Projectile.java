package joc.projectile;

import joc.assets.SpriteSheet;
import joc.map.Map;
import joc.window.GameWindow;

import java.awt.*;

public interface Projectile {
    boolean update();
    void draw(Graphics g);

    //Projectile p = new Projectile.ProjectileFactory().withAngle(angle).withMap(this.map).withWindow(this).withSheet(this.sheet).withStartingPosition(x,y).buildArrow();

    /**
     * Factory !!!!
     *
     * Se creaza o fabrica, specifica proiectilelor, i se dau parametrii , la final construieste de tipul x
     *
     */
    class ProjectileFactory{
        private double angle = 0;
        private Map map = null;
        private GameWindow window = null;
        private SpriteSheet sheet = null;
        private int x, y;

        /**
         * i se da un unghi viitorului proj
         * @param angle unghi
         * @return fabrica
         */
        public ProjectileFactory withAngle(double angle){
            this.angle = angle;
            return this;
        }

        /**
         * i se da o harta
         * @param map harta
         * @return fabrica
         */
        public ProjectileFactory withMap(Map map){
            this.map = map;
            return this;
        }

        /**
         * i se da o fereastra
         * @param window fereastra
         * @return fabrica
         */
        public ProjectileFactory withWindow(GameWindow window){
            this.window = window;
            return this;
        }

        public ProjectileFactory withSheet(SpriteSheet sheet){
            this.sheet = sheet;
            return this;
        }

        public ProjectileFactory withStartingPosition(int x, int y){
            this.x = x;
            this.y = y;
            return this;
        }

        public Projectile buildArrow(){
            return new ArrowProjectile(this.x, this.y, this.angle, this.sheet, this.map, this.window);
        }

        public Projectile buildRock(){
            return new RockProjectile(this.x, this.y, this.angle, this.sheet, this.map, this.window);
        }
    }
}
