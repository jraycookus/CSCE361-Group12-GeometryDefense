package geometrydefense.geometrydefense;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;

public class Tower{
    private Bitmap towerImage;
    private Bitmap projImage;
    private Point position;
    private int damage;
    private double distance;
    private int cooldown;
    private Level level;
    private int attackSpeed;
    private int range;


    public Tower(Point position, int damage, int attackSpeed, int range, Bitmap towerImage, Bitmap projImage, Level level) {
        this.level = level;
        this.towerImage = towerImage;
        this.position = position;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.projImage = projImage;
        this.range = range;
        cooldown = 0;
    }

    public double calculateDistance(Enemy target){
        Point targetPosition = target.getPosition();

        distance = (position.x - targetPosition.x) * (position.x - targetPosition.x) + (position.y - targetPosition.y) * (position.y - targetPosition.y);
        distance = Math.sqrt(distance);

        return distance;
    }

    public void attack(Enemy target){
        Projectile projectile = new Projectile(this.projImage, new Point(this.position),target,20,this.damage,this.level);
        this.level.addProjectile(projectile);
    }

    public void update(){


        if(cooldown > 0){
            cooldown--;
            return;
        }
        ArrayList<Enemy> enemyList = level.getEnemiesOnScreen();

        if (cooldown ==0){
            for(Enemy enemy:enemyList){
                if (cooldown > 0) return;

                distance = calculateDistance(enemy);
                if(distance <= range && cooldown == 0){
                    attack(enemy);
                    cooldown = attackSpeed;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.towerImage,this.position.x-this.towerImage.getWidth()/2,this.position.y-this.towerImage.getHeight()/2,null);
    }

    public int getValue(){
        //get actual value when upgrades implemented
        return 70;
    }

    public Bitmap getTowerImage() {
        return towerImage;
    }

    public Bitmap getProjImage() {
        return projImage;
    }

    public Point getPosition() {
        return position;
    }

    public int getDamage() {
        return damage;
    }

    public double getDistance() {
        return distance;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Level getLevel() {
        return level;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getRange() {
        return range;
    }
}
