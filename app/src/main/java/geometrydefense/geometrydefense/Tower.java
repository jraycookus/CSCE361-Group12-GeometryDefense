package geometrydefense.geometrydefense;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;

public class Tower{
    private Bitmap towerImage;
    private Point position;
    private int damage;
    private double distance;
    private int cooldown;
    private Point targetPosition;
    private Level level;
    private int attackSpeed;
    private int range;


    public Tower(Point position, int damage, int attackSpeed, int range, Bitmap towerImage, Level level) {
        this.level = level;
        this.towerImage = towerImage;
        this.position = position;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.range = range;
        cooldown = 0;
    }

    public double calculateDistance(Enemy target){
        targetPosition = target.getPosition();

        distance = (position.x - targetPosition.x) * (position.x - targetPosition.x) + (position.y - targetPosition.y) * (position.y - targetPosition.y);
        distance = Math.sqrt(distance);

        return distance;
    }

    public void attack(Enemy target){

        Projectile projectile = new Projectile(target);
    }

    public void update(){

        ArrayList<Enemy> enemyList = level.getEnemiesOnScreen();

        if(cooldown > 0){
            cooldown--;
            return;
        }
        if (cooldown ==0){
            for(Enemy enemy:enemyList){
                if (cooldown > 0) return;

                distance = calculateDistance(enemy);
                if(distance <= range && cooldown == 0){
                    attack(enemy);
                    cooldown = 200;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.towerImage,this.position.x-this.towerImage.getWidth()/2,this.position.y-this.towerImage.getHeight()/2,null);
    }
}
