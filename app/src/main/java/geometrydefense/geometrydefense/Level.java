package geometrydefense.geometrydefense;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.ArrayList;

/**
 * Created by Colby on 3/12/2016.
 */

public class Level extends SurfaceView implements SurfaceHolder.Callback{
    private LevelThread thread;
    private BitmapFactory.Options options = new BitmapFactory.Options();
    private Bitmap levelMap;
    private ArrayList<Tower> towersOnScreen = new ArrayList<Tower>();
    private ArrayList<Enemy> enemiesOnScreen = new ArrayList<Enemy>();
    private ArrayList<Enemy> deadEnemy=new ArrayList<Enemy>();
    private ArrayList<Projectile> projOnScreen = new ArrayList<Projectile>();
    private int gold=1000;
    private int lives=10;
    //number of ticks before wave is sent at 30 fps
    private double timetoNextWave=-1;
    //number of ticks before next enemy of a wave is sent
    private double timetoNextEnemy=0;
    //number of enemies remaining in wave
    private int enemiesLeft=0;
    //current enemy wave number-used to choose which enemy to spawn
    private int enemyWave=0;
    private ArrayList<Enemy> enemyWaves = new ArrayList<Enemy>();
    private ArrayList<Point> enemyPath = new ArrayList<Point>();
    //keep the creating activity to keep track of ui elements
    private LevelActivity activity;




    public Level(LevelActivity activity){
        super(activity);
        this.activity=activity;
        // adding the callback (this) to the surface holder to intercept event
        getHolder().addCallback(this);
        setFocusable(true);
        //create new thread to start running the level
        thread = new LevelThread(getHolder(), this);
        //load level background
        options.inScaled=false;
        levelMap = BitmapFactory.decodeResource(getResources(), R.drawable.level1map,options);

        //create path list-enemies will start at first point and then go throug all points until reaching the end
        enemyPath.add(new Point(90,0));
        enemyPath.add(new Point(90,690));
        enemyPath.add(new Point(210,690));
        enemyPath.add(new Point(210,90));
        enemyPath.add(new Point(330,90));
        enemyPath.add(new Point(330,690));
        enemyPath.add(new Point(450,690));
        enemyPath.add(new Point(450,90));
        enemyPath.add(new Point(540,90));

        //create enemy list
        for(int i=0;i<10;i++){
            enemyWaves.add(new Enemy(100+i*10,0,5,100+i*5,BitmapFactory.decodeResource(getResources(),R.drawable.normal_enemy_sprite,options),enemyPath,this));
        }


        //set send now btn text
        activity.updateText(R.id.send_now_btn,"in: 0 sec\nsend now");


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // screen pressed

        } if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // drag event

        } if (event.getAction() == MotionEvent.ACTION_UP) {
            // touch was released

        }
        return true;

    }


    public void render(Canvas canvas){
        //draw own level map first
        canvas.drawBitmap(levelMap, 0, 0, null);

        //draw all enemies, towers, and projectiles
        for(Enemy enemy:enemiesOnScreen){
            enemy.draw(canvas);
        }
        for(Tower tower: towersOnScreen){
            tower.draw(canvas);
        }
        for(Projectile projectile:projOnScreen){
            projectile.draw(canvas);
        }
    }


    public void update(){
        //check if next wave should be sent
        if(timetoNextWave==0){
            sendWave();
        }else{
            timetoNextWave-=1;
            //time starts negative at start so dont update unless its positive
            if(timetoNextWave>0){
                activity.updateText(R.id.send_now_btn,"in: "+(int)timetoNextWave/30 +" sec\nsend now");
            }
        }
        //if there are still enemies to send, check the timer
        if(enemiesLeft>0){
            if(timetoNextEnemy>0){
                timetoNextEnemy-=1;
            }else{
                spawnEnemy();
                timetoNextEnemy=30;
            }
        }

        //remove any enemies that should be destroyed
        enemiesOnScreen.removeAll(deadEnemy);

        //call update for all enemies, towers, and projectiles
        for(Enemy enemy:enemiesOnScreen){
            enemy.update();
        }
        for(Tower tower:towersOnScreen){
            tower.update();
        }
        for(Projectile projectile:projOnScreen){
            projectile.update();
        }

    }


    public void sendWave(){
        timetoNextWave=900;
        enemiesLeft+=10;
        spawnEnemy();
    }

    public void spawnEnemy(){
        //check if any enemies are left to send
        if(enemiesLeft<=0){
            return;
        }
        Enemy enemy = enemyWaves.get(enemyWave);
        //create new copy of enemy from waves and add it to list of enemies on screen to spawn it
        enemiesOnScreen.add(new Enemy(enemy.getHitpoints(),enemy.getArmor(),enemy.getSpeed(),enemy.getValue(),enemy.getSprite(),enemy.getPath(),this));
        enemiesLeft-=1;
    }

    public void updateGold(int gold){
        this.gold+=gold;
        activity.updateText(R.id.gold_text,"Gold: "+this.gold);
    }

    public void loselife(Enemy enemy){
        this.lives-=1;
        activity.updateText(R.id.lives_text,"Lives: "+this.lives);
        deadEnemy.add(enemy);
    }

}


