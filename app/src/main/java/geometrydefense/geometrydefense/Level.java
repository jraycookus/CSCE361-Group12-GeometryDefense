package geometrydefense.geometrydefense;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.ArrayList;


public class Level extends SurfaceView implements SurfaceHolder.Callback{
    private LevelThread thread;
    private BitmapFactory.Options options = new BitmapFactory.Options();
    private BitmapFactory.Options options4sprites = new BitmapFactory.Options();
    private Bitmap levelMap;
    private Bitmap towerImage;
    private Bitmap projImage;
    private ArrayList<Tower> towersOnScreen = new ArrayList<Tower>();
    private ArrayList<Enemy> enemiesOnScreen = new ArrayList<Enemy>();
    private ArrayList<Projectile> projOnScreen = new ArrayList<Projectile>();
    private ArrayList<Tower> destroyTower = new ArrayList<Tower>(); // keep track of list of objects to remove from screen since they cant be removed while looping though list
    private ArrayList<Enemy> deadEnemy=new ArrayList<Enemy>();
    private ArrayList<Projectile> destroyProj = new ArrayList<Projectile>();
    private int gold=1000;
    private int lives=10;
    private int towerCost = 100;
    private double timetoNextWave=-1;     //number of ticks before wave is sent at 30 fps
    private double timetoNextEnemy=0;     //number of ticks before next enemy of a wave is sent
    private int enemiesLeft=0;      //number of enemies remaining in wave
    private int enemyWave=0;     //current enemy wave number-used to choose which enemy to spawn from list
    private ArrayList<Enemy> enemyWaves = new ArrayList<Enemy>();
    private ArrayList<Point> enemyPath = new ArrayList<Point>();
    private LevelActivity activity;     //keep the creating activity to keep track of ui elements
    private boolean buyMode=false;  //boolean to keep track of if a tower is being placed -resets after placing tower
    private boolean sellMode = false; //same as buyMode except for selling an existing tower instead
    private Point buyTowerPoint;    //Point to keep track of where the user is hovering to place their tower-used to draw tower
    private float density = getResources().getDisplayMetrics().density;
    private int scaleMultipliyer = (int) (density +0.5f);
    private int topUIHeight = (int) (180 * density +0.5f);
    private int dstWidth = getResources().getDisplayMetrics().widthPixels;
    private int dstHeight = (getResources().getDisplayMetrics().heightPixels) - 180;
    private int bgWidth = 540;
    private int bgHieght = 777;




    public Level(LevelActivity activity){
        super(activity);
        this.activity=activity;
        // adding the callback (this) to the surface holder to intercept event
        getHolder().addCallback(this);
        setFocusable(true);
        //create new thread to start running the level
        thread = new LevelThread(getHolder(), this);
        //load level background
        options.inScaled = false;
        options4sprites.inScaled = true;

        levelMap = BitmapFactory.decodeResource(getResources(), R.drawable.level1map,options);
        levelMap = Bitmap.createScaledBitmap(levelMap, dstWidth, dstHeight,true);
        towerImage = BitmapFactory.decodeResource(getResources(),R.drawable.tower_sprite,options4sprites);
        projImage = BitmapFactory.decodeResource(getResources(),R.drawable.projectile_sprite,options4sprites);

        //create path list-enemies will start at first point and then go through all points until reaching the end



        enemyPath.add(new Point(scalePointW(90.0),scalePointH(0.0)));
        enemyPath.add(new Point(scalePointW(90.0),scalePointH(690.0)));
        enemyPath.add(new Point(scalePointW(210.0),scalePointH(690.0)));
        enemyPath.add(new Point(scalePointW(210.0),scalePointH(90.0)));
        enemyPath.add(new Point(scalePointW(330.0),scalePointH(90.0)));
        enemyPath.add(new Point(scalePointW(330.0),scalePointH(690.0)));
        enemyPath.add(new Point(scalePointW(450.0),scalePointH(690.0)));
        enemyPath.add(new Point(scalePointW(450.0),scalePointH(90.0)));
        enemyPath.add(new Point(scalePointW(540.0),scalePointH(90.0)));

        //create enemy list
        for(int i=0;i<10;i++){
            enemyWaves.add(new Enemy(100+i*10,0,5,100+i*5,BitmapFactory.decodeResource(getResources(),R.drawable.normal_enemy_sprite,options4sprites),enemyPath,this));
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
    //mouse events on canvas
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // screen pressed
            if(this.buyMode){
                this.buyTowerPoint=roundToNearest(new Point((int)event.getX(),(int)event.getY()));
            }

        } if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // drag event
            if(this.buyMode){
                this.buyTowerPoint=roundToNearest(new Point((int)event.getX(),(int)event.getY()));
            }

        } if (event.getAction() == MotionEvent.ACTION_UP) {
            // touch was released
            if(this.buyMode){
                this.buyTower(roundToNearest(new Point((int)event.getX(),(int)event.getY())));
            }
            if(this.sellMode){
                this.sellTower(roundToNearest((new Point((int)event.getX(),(int)event.getY()))));
            }

        }
        return true;

    }

    //used to scale width of path point with background stretch
    public int scalePointW(double point) {
        double p = (point / bgWidth) * dstWidth;
        int pInt = (int) (p + 0.5d);
        return pInt;
    }
    public int scalePointH(double point) {
        double p = (point / bgHieght) * (dstHeight);
        int pInt = (int) (p + 0.5d);
        return pInt;
    }
    //take a point and round its coordinates to the nearest 30 for the grid size of the game to place towers-then add 15 to place tower in middle of grid square
    public Point roundToNearest(Point p){
        int multiple =30;
        p.x=Math.round(p.x/multiple)*multiple+multiple/2;
        p.y=Math.round(p.y/multiple)*multiple+multiple/2;
        return p;
    }

    public void buyTower(Point position){
        //check if the user has enough gold to build the tower
        if(gold>=towerCost){
            //check if the tower is in a valid position
            if(validPosition(position)) {
                Tower t = new Tower(position, 50, 20, 200, this.towerImage, this.projImage, this);
                this.updateGold(-towerCost);
                this.towersOnScreen.add(t);
            }
        }
        //whether buy was succesful or not, remove buymode and reset towerpoint so the tower isnt displayed anymore
        this.buyTowerPoint=null;
        this.buyMode=false;
    }

    public boolean validPosition(Point position){
        //check if the tower is not on the path or other obstacle
        for(Tower t:this.towersOnScreen){
            if(t.getPosition().equals(position)){
                return false;
            }
        }
        return true;
    }

    public void sellTower(Point position){
        //check to see if a tower was on top of sell point
        for(Tower t:this.towersOnScreen){
            if(t.getPosition().equals(position)){
                //tower at position-remove it and refund some gold
                this.updateGold(t.getValue());
                this.destroyTower.add(t);
                break; //towers cannot be on top of each other so break loop
            }
        }
        //whether tower was sold or not, remove sellmode
        this.sellMode=false;

    }

    public void buyBtn(){
        //if sellmode is on, take it off
        if(this.sellMode){
            this.sellMode = !this.sellMode;
        }
        this.buyMode = !this.buyMode;
    }

    public void sellBtn(){
        //if buymode is on, take it off
        if(this.buyMode){
            this.buyMode = !this.buyMode;
        }
        this.sellMode=!this.sellMode;
    }

    public void render(Canvas canvas){
        //draw own level map first
        canvas.drawBitmap(levelMap, 0, 0, null);
        //draw tower to be placed if in buymode and the user has their finger on sccreen for the coordinates
        if(this.buyMode&&this.buyTowerPoint!=null){
            canvas.drawBitmap(this.towerImage,this.buyTowerPoint.x-this.towerImage.getWidth()/2,this.buyTowerPoint.y-this.towerImage.getHeight()/2,null);
        }
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

        //remove any objects that should be destroyed
        enemiesOnScreen.removeAll(deadEnemy);
        towersOnScreen.removeAll(destroyTower);
        projOnScreen.removeAll(destroyProj);

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
        activity.updateText(R.id.lives_text, "Lives: " + this.lives);
    }

    //used to remove Enemies, towers, and projectiles from screen.  Will be removed at start of update loop since they cant be removed while looping through
    public void remove(Object object){
        if(object.getClass() == Enemy.class) {
            deadEnemy.add((Enemy) object);
            //if an enemy is removed, make sure to remove all projectiles that are still currently targeting it
            for(Projectile p:this.projOnScreen){
                if(p.getTarget().equals((Enemy)object)){
                    this.destroyProj.add(p);
                }
            }
        }else if (object.getClass()==Tower.class){
            destroyTower.add((Tower)object);
        }else if(object.getClass()==Projectile.class){
            destroyProj.add((Projectile) object);
        }
        //do nothing if not of type enemy, tower, or projectile
    }

    public void addProjectile(Projectile p){
        this.projOnScreen.add(p);
    }

    public ArrayList<Enemy> getEnemiesOnScreen(){
        return this.enemiesOnScreen;
    }

}


