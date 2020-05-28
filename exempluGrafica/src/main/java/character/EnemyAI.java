package character;

import map.Map;
import player.Player;

import java.util.List;

public class EnemyAI {
    private static EnemyAI instance;

    public static EnemyAI getInstance(){
        if(EnemyAI.instance == null)
            EnemyAI.instance = new EnemyAI();
        return EnemyAI.instance;
    }

    private EnemyAI(){

    }

    private List<Enemy> enemies;
    private Player player;

    private Map map;

    public void setMap(Map map) {
        this.map = map;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
        for (Enemy e : this.enemies) {
            e.setMap(this.map);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isInitialised(){
        return !(this.enemies == null || this.player == null);
    }

    public void update(){
        if(!this.isInitialised())
            return;

        Enemy deadEnemy = null;

        for(Enemy e : this.enemies){
            if(e.isDead()){
                deadEnemy = e;
                continue;
            }
            e.update();
        }

        if(deadEnemy != null)
            this.enemies.remove(deadEnemy);
    }

}
