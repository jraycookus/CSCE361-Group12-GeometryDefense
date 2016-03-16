package geometrydefense.geometrydefense;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.Image;


public class Projectile {
    private Bitmap sprite;
    private Point position;
    private Enemy target;
    private double speed;
    private double damage;
    private Level level;


    public Projectile(Bitmap sprite, Point position, Enemy target, double speed, double damage,Level level) {
        this.sprite = sprite;
        this.position = position;
        this.target = target;
        this.speed = speed;
        this.damage = damage;
        this.level=level;
    }

    public void update(){

        //update the projectiles position towards its enemy
        double xdiff = this.target.getPosition().x-this.position.x;
        double ydiff = this.target.getPosition().y-this.position.y;
        //get angle from projectile towards its target
        double direction = Math.atan2(ydiff,xdiff);
        this.position.x = this.position.x+(int)(speed*Math.cos(direction));
        this.position.y = this.position.y+(int)(speed*Math.sin(direction));
        //check if the projectile is within its speed range of hitting the target, instead of sqrt the sum, just square speed
        if(xdiff*xdiff+ydiff*ydiff<=this.speed*this.speed){
            this.hitTarget();
        }
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(this.sprite,this.position.x-this.sprite.getWidth()/2,this.position.y-this.sprite.getHeight()/2,null);

    }

    public void hitTarget(){
        //if the projectile hits the enemy, call the enemy hit function and destroy the projectile
        this.target.projectileHit(this.damage);
        this.destroy();

    }

    public void destroy(){
        this.level.remove(this);
    }


    public Bitmap getSprite() {
        return sprite;
    }

    public Point getPosition() {
        return position;
    }

    public Enemy getTarget() {
        return target;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDamage() {
        return damage;
    }

    public Level getLevel() {
        return level;
    }
}
