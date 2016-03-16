package geometrydefense.geometrydefense;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;


public class Enemy {
    private double hitpoints;
    private double armor;
    private double speed;
    private int value;
    private double timeAlive;
    private Bitmap sprite;
    private final ArrayList<Point> path;
    private int targetpoint;
    private Point position;
    private Level level;

    public Enemy(double hitpoints, double armor, double speed,int value,Bitmap sprite, ArrayList<Point> path,Level level) {
        this.hitpoints = hitpoints;
        this.armor = armor;
        this.speed = speed;
        this.value = value;
        this.sprite = sprite;
        this.path = path;
        this.level=level;
        this.targetpoint=1;
        this.position=new Point(path.get(0).x,path.get(0).y);
    }

    public double getSpeed() {
        return speed;
    }

    public double getHitpoints() {
        return hitpoints;
    }

    public double getArmor() {
        return armor;
    }

    public int getValue() {
        return value;
    }

    public Bitmap getSprite() {
        return sprite;
    }

    public ArrayList<Point> getPath() {
        return path;
    }

    public Point getPosition(){
        return this.position;
    }

    public void update(){
        //move at speed in x direction and y direction towards next point
        //take the sign of the difference of position and target to get direction
        this.position.x+=Math.signum(this.path.get(targetpoint).x - this.position.x)*speed;
        this.position.y+=Math.signum(this.path.get(targetpoint).y-this.position.y)*speed;
        if(distance(this.position,this.path.get(targetpoint))<=speed){
            this.position.x=this.path.get(targetpoint).x;
            this.position.y=this.path.get(targetpoint).y;
            //if the target point is the last, the enemy has reach the end
            if(targetpoint==this.path.size()-1){
                endOfPath();
            }else {
                this.targetpoint += 1;
            }
        }
    }

    public double distance(Point p1, Point p2){
        return Math.sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y));
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(this.sprite,this.position.x-this.sprite.getWidth()/2,this.position.y-this.sprite.getHeight()/2,null);

    }

    public void killed(){
        this.level.updateGold(this.value);
        this.level.remove(this);
    }

    public void projectileHit(double damage){
        //armor will be a percentage between 0(does not reduce damage at all) and 1(negates all damage)
        this.hitpoints-=damage*(1-armor);
        if(this.hitpoints<=0){
            this.killed();
        }
    }

    public void endOfPath(){
        this.level.loselife(this);
        this.level.remove(this);
    }

}
