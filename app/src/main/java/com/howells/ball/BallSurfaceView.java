package com.howells.ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.howells.ball.model.GameBack;
import com.howells.ball.model.GameMap;
import com.howells.ball.model.Help;
import com.howells.ball.model.Life;
import com.howells.ball.model.Meteo;
import com.howells.ball.thread.EatThread;
import com.howells.ball.thread.TrapThread;
import com.howells.ball.utils.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016.5.12.
 */
public class BallSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable ,SensorEventListener {


    /**
     * 画图的线程
     */
    private Thread t;

    /**
     * 陷阱线程
     */
    private EatThread mEat;
    private TrapThread mTrap;

    /**
     * 每50帧刷新一次屏幕
     **/
    public static final int TIME_IN_FRAME = 50;

    /**
     * 每张图片大小  像素
     */
    private final int IMAGE_SIZE=80;

    /**
     * 游戏画笔
     **/
    private Paint mPaint;
    private Paint mTextPaint = null;
    private SurfaceHolder mSurfaceHolder = null;

    /**
     * 控制游戏更新循环
     **/
    private boolean isRunning=false;

    /**
     * 游戏画布
     **/
    private Canvas mCanvas;

    /**
     * SensorManager管理器
     **/
    private SensorManager mSensorMgr = null;
    private Sensor mSensor = null;

    /**
     * 手机屏幕宽高
     **/
    private int mScreenWidth ;
    private int mScreenHeight ;

    /**
     * 小球资源文件越界区域
     **/
    private int mScreenBallWidth ;
    private int mScreenBallHeight ;

    /**
     * 游戏背景文件
     **/
    private Bitmap[] mbitmapBg;
    private GameBack back;


    /**
     * 开始时间
     */
    private long startTime;
    private long endTime;

    /**
     * 小球资源文件
     **/
    private Bitmap mbitmapBall;
    private Bitmap mBtimapHelp;
    private Help mHelp;
    private boolean isHelp=false;
    /**
     * 地图资源文件
     */
    private Bitmap[] maps;
    private Bitmap[] GameStatusBitmap;

    private GameMap map;
    private Integer[][] xinxi;
    private int mRow;
    private int mCol;
    /**
     * 小球的坐标位置
     **/
    private float mPosX ;
    private float mPosY ;
    private float mOffset=0;
    /**
     * 重力感应X轴 Y轴 Z轴的重力值
     **/
    private float mGX = 0;
    private float mGY = 0;

    /**
     * 游戏状态    暂停  正在玩   结束
     */
    private GameStatus mStatus = GameStatus.WAITTING;
    private int statu=-1;
    public enum GameStatus {
        WAITTING,
        RUNNING,
        OVER
    }

    /**
     *
     * 生命
     */
    private int lifecount;
    private Life mLife;
    private Bitmap[] LifeBitmap;

    /**石头**/
    private List<Meteo> ListMeteo;
    private Bitmap[] MeteoBitmap;
    private Random random;

    /**关卡**/
    private int mGate =0;
    private int mGateMax;

    private Vibrator vibrator;

    public BallSurfaceView(Context context) {
        this(context, null);
    }

    public BallSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setZOrderOnTop(true);

        this.setKeepScreenOn(true);

        /** 设置当前View拥有控制焦点 **/
        this.setFocusable(true);
        /** 设置当前View拥有触摸事件 **/
        this.setFocusableInTouchMode(true);
        /** 拿到SurfaceHolder对象 **/
        mSurfaceHolder = this.getHolder();
        /** 将mSurfaceHolder添加到Callback回调函数中 **/
        mSurfaceHolder.addCallback(this);
        /** 创建画布 **/
        mCanvas = new Canvas();
        /** 创建曲线画笔 **/
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        /**加载小球资源**/
        mbitmapBall = loadImageById(R.mipmap.ball);
        /**加载游戏背景**/
        loadBack();

        /**加载地图资源**/
        loadMap();

        /**加载分数**/
        loadScore();

        /**石头图片**/
        loadMeteo();

        mBtimapHelp=loadImageById(R.mipmap.help);

        GameStatusBitmap=new Bitmap[3];
        GameStatusBitmap[0]=loadImageById(R.mipmap.game_over);
        GameStatusBitmap[1]=loadImageById(R.mipmap.game_pass);
        GameStatusBitmap[2]=loadImageById(R.mipmap.game_win);

        /**得到SensorManager对象**/
        mSensorMgr = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 注册listener，第三个参数是检测的精确度
        //SENSOR_DELAY_FASTEST 最灵敏 因为太快了没必要使用
        //SENSOR_DELAY_GAME    游戏开发中使用
        //SENSOR_DELAY_NORMAL  正常速度
        //SENSOR_DELAY_UI 	       最慢的速度
        mSensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        //震动
        vibrator=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void loadBack(){
        mbitmapBg=new Bitmap[9];
        mbitmapBg[0] = loadImageById( R.mipmap.back_1);
        mbitmapBg[1] = loadImageById( R.mipmap.back_2);
        mbitmapBg[2] = loadImageById( R.mipmap.back_3);
        mbitmapBg[3] = loadImageById( R.mipmap.back_4);
        mbitmapBg[4] = loadImageById(  R.mipmap.back_5);
        mbitmapBg[5] = loadImageById(  R.mipmap.back_6);
        mbitmapBg[6] = loadImageById(  R.mipmap.back_7);
        mbitmapBg[7] = loadImageById(  R.mipmap.back_8);
        mbitmapBg[8] = loadImageById(  R.mipmap.back_9);
    }

    private void loadMap() {
        maps=new Bitmap[17];
        maps[1]=loadImageById( R.mipmap.tile);
        maps[2]=loadImageById( R.mipmap.home);
        //345通过 6死亡
        maps[3]=loadImageById( R.mipmap.eat_1);
        maps[4]=loadImageById( R.mipmap.eat_2);
        maps[5]=loadImageById( R.mipmap.eat_3);
        maps[6]=loadImageById( R.mipmap.eat_4);
        //789 10 11通过  12 13 14 15死亡
        maps[7]=loadImageById( R.mipmap.trap1);
        maps[8]=loadImageById( R.mipmap.trap2);
        maps[9]=loadImageById( R.mipmap.trap3);
        maps[10]=loadImageById( R.mipmap.trap4);
        maps[11]=loadImageById( R.mipmap.trap5);
        maps[12]=loadImageById( R.mipmap.trap6);
        maps[13]=loadImageById( R.mipmap.trap7);
        maps[14]=loadImageById( R.mipmap.trap8);
        maps[15]=loadImageById( R.mipmap.trap9);

        maps[16]=loadImageById( R.mipmap.plus_life);
    }

    private void loadScore() {
        LifeBitmap=new Bitmap[12];
        LifeBitmap[0]=loadImageById(R.mipmap.number0);
        LifeBitmap[1]=loadImageById(R.mipmap.number1);
        LifeBitmap[2]=loadImageById(R.mipmap.number2);
        LifeBitmap[3]=loadImageById(R.mipmap.number3);
        LifeBitmap[4]=loadImageById(R.mipmap.number4);
        LifeBitmap[5]=loadImageById(R.mipmap.number5);
        LifeBitmap[6]=loadImageById(R.mipmap.number6);
        LifeBitmap[7]=loadImageById(R.mipmap.number7);
        LifeBitmap[8]=loadImageById(R.mipmap.number8);
        LifeBitmap[9]=loadImageById(R.mipmap.number9);
        LifeBitmap[10]=loadImageById(R.mipmap.ball);
        LifeBitmap[11]=loadImageById(R.mipmap.multiply);
    }

    private void loadMeteo() {
        MeteoBitmap=new Bitmap[8];
        MeteoBitmap[0]=loadImageById(R.mipmap.meteo1);
        MeteoBitmap[1]=loadImageById(R.mipmap.meteo2);
        MeteoBitmap[2]=loadImageById(R.mipmap.meteo3);
        MeteoBitmap[3]=loadImageById(R.mipmap.meteo4);
        MeteoBitmap[4]=loadImageById(R.mipmap.meteo5);
        MeteoBitmap[5]=loadImageById(R.mipmap.meteo6);
        MeteoBitmap[6]=loadImageById(R.mipmap.meteo7);
        MeteoBitmap[7]=loadImageById(R.mipmap.meteo8);
        random=new Random();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRunning = true;
        t = new Thread(this);
        t.start();

        mEat=new EatThread(xinxi,mCol,mRow);
        mEat.start();

        mTrap=new TrapThread(xinxi,mCol,mRow);
        mTrap.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();

            logic();

            if(mStatus==GameStatus.RUNNING) {
                /** 在这里加上线程安全锁 **/
                synchronized (mSurfaceHolder) {
                    /** 拿到当前画布 然后锁定 **/
                    mCanvas = mSurfaceHolder.lockCanvas();

                    draw();
                    /** 绘制结束后解锁显示在屏幕上 **/
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
            }

            long end = System.currentTimeMillis();

            try {
                if (end - start < TIME_IN_FRAME) {
                    Thread.sleep(TIME_IN_FRAME - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void logic() {
        switch (mStatus) {
            case RUNNING:
                changeXY();
                checkball();
                break;
            case OVER:

                break;
            default:
                break;
        }
    }

    private int x1,y1;  //左上角初始位置
    private int x2,y2;  //偏移后左上角位置
    private int x3,y3;  //右下角初始位置
    private int x4,y4;  //偏移后右下角位置
    private void changeXY() {

        float offsetX=mGX*2;
        float offsetY=mGY*2;

        //初始位置
        x1= (int) ((mPosX+mOffset)/IMAGE_SIZE);
        y1= (int) ((mPosY+mOffset)/IMAGE_SIZE);

        x3= (int) ((mPosX-mOffset+IMAGE_SIZE)/IMAGE_SIZE);
        y3= (int) ((mPosY-mOffset+IMAGE_SIZE)/IMAGE_SIZE);

        //检测小球是否可以到达
        x2= (int) ((mPosX-offsetX+mOffset)/IMAGE_SIZE);
        y2= (int) ((mPosY+offsetY+mOffset)/IMAGE_SIZE);

        x4= (int) ((mPosX-offsetX-mOffset+IMAGE_SIZE)/IMAGE_SIZE);
        y4= (int) ((mPosY+offsetY-mOffset+IMAGE_SIZE)/IMAGE_SIZE);

        //向左
        if(mGX>0){

            y1= (int) ((mPosY+mOffset+2)/IMAGE_SIZE);
            y3= (int) ((mPosY-mOffset-2+IMAGE_SIZE)/IMAGE_SIZE);

            if(x4>0) {
                if (xinxi[y1][x2] != 0 && xinxi[y3][x4 - 1] != 0) {
                    mPosX -= offsetX;
                }
            }
        }else {//向右

            y1= (int) ((mPosY+mOffset+2)/IMAGE_SIZE);
            y3= (int) ((mPosY-mOffset-2+IMAGE_SIZE)/IMAGE_SIZE);

            if(x2<mRow-1) {
                if (xinxi[y3][x4] != 0&&xinxi[y1][x2 + 1] != 0) {
                    mPosX -= offsetX;
                }
            }
        }

        //向下
        if(mGY>0){

            x1= (int) ((mPosX+mOffset+2)/IMAGE_SIZE);
            x3= (int) ((mPosX-mOffset-2+IMAGE_SIZE)/IMAGE_SIZE);

            if(y2<mCol-1){
                if(xinxi[y4][x3]!=0&&xinxi[y2+1][x1]!=0){
                    mPosY+=offsetY;
                }
            }
        }else {//向上

            x1= (int) ((mPosX+mOffset+2)/IMAGE_SIZE);
            x3= (int) ((mPosX-mOffset-2+IMAGE_SIZE)/IMAGE_SIZE);

            if(y4>0){
                if(xinxi[y2][x1]!=0 &&xinxi[y4-1][x3]!=0){
                    mPosY+=offsetY;
                }
            }
        }

    }

    private void checkball() {
        //2 win
        //6 die //12 13 14 15 die
        //16 life

        int mid=xinxi[(y1+y3)/2][(x1+x3)/2];
        if(mid==2){
            if(mGate==mGateMax-1){
                statu=1;
            }else {
                mPosX = 0;
                mPosY = 0;
                mGate++;
                againgame();
                statu=2;
            }
        }
        if(mid==6){
            long[] pattern={100,400,100,400};
            vibrator.vibrate(pattern,-1);
            if(lifecount==0){
                mPosX = 0;
                mPosY = 0;
                mGate=0;
                againgame();
                statu=0;
            }else {
                mPosX = 0;
                mPosY = 0;
                lifecount--;
            }
        }
        if(mid>=12&&mid<=15){
            long[] pattern={100,400,100,400};
            vibrator.vibrate(pattern,-1);
            if(lifecount==0){

                mPosX = 0;
                mPosY = 0;
                mGate=0;
                againgame();
                statu=0;
            }else {
                mPosX = 0;
                mPosY = 0;
                lifecount--;
            }
        }
        if(mid==16){
            xinxi[y1][x1]=1;
            lifecount++;
        }


 /*       int left=xinxi[y1][x1];
        int right=xinxi[y3][x3];

        if(left==2&&right==2){
            if(mGate==mGateMax-1){
                statu=1;
            }else {
                mPosX = 0;
                mPosY = 0;
                mGate++;
                againgame();
                statu=2;
            }
        }
        if(left==6&&right==6){

            if(lifecount==0){

                mPosX = 0;
                mPosY = 0;
                mGate=0;
                againgame();
                statu=0;
            }else {
                mPosX = 0;
                mPosY = 0;
                lifecount--;
            }
        }
        if(left>=12&&left<=15&&right>=12&&right<=15){

            if(lifecount==0){

                mPosX = 0;
                mPosY = 0;
                mGate=0;
                againgame();
                statu=0;
            }else {
                mPosX = 0;
                mPosY = 0;
                lifecount--;
            }
        }
        if(left==16&&right==16){
            xinxi[y1][x1]=1;
            lifecount++;
        }
*/
    }

    /**绘图**/
    private void draw() {

        /**绘制游戏背景**/
        back.draw(mCanvas);

        /**绘制地图**/
        drawmap();

        /**绘制小球**/
        mCanvas.drawBitmap(mbitmapBall, null, new RectF(mPosX,mPosY,mPosX+IMAGE_SIZE,mPosY+IMAGE_SIZE), null);

        /**生命数**/
        mLife.draw(mCanvas,lifecount);

        /**滚石**/
        drawmeteo();

        if(isHelp) {
            mHelp.draw(mCanvas);
            mStatus=GameStatus.WAITTING;
        }

        if(statu!=-1){
            mCanvas.drawBitmap(GameStatusBitmap[statu],null,new RectF(mScreenWidth/2-90,mScreenHeight/2-100,mScreenWidth/2+180,mScreenHeight/2+110),null);
            mStatus=GameStatus.WAITTING;
            if(statu==1||statu==2){
                endTime=System.currentTimeMillis();
                SharedPrefsUtil.putValue(getContext(),"rand",String.valueOf(mGate),endTime-startTime);
            }
        }

        /**X轴 Y轴 Z轴的重力值**/
        mCanvas.drawText("X轴重力值 ：" + mGX, 0, 20, mPaint);
        mCanvas.drawText("Y轴重力值 ：" + mGY, 0, 40, mPaint);
    }

    private void drawmap() {
        map.draw(mCanvas,xinxi);
    }

    private void drawmeteo() {

        long t=System.currentTimeMillis();
        if(t%31==29){
            if (ListMeteo.size()==0){
                Meteo meteo = new Meteo(MeteoBitmap, 240, 240);
                meteo.setX(random.nextInt(600));
                ListMeteo.add(meteo);
                ListMeteo.add(null);
                ListMeteo.add(null);
            }else {
                for (int i = 0; i < ListMeteo.size(); i++) {
                    Meteo m = ListMeteo.get(i);
                    if (m == null) {
                        int r=random.nextInt(2)+1;
                        m = new Meteo(MeteoBitmap,120*r, 120*r);
                        m.setX(random.nextInt(600));
                        ListMeteo.set(i,m);
                        break;
                    }
                }
            }
        }

        for(int i=0;i<ListMeteo.size();i++) {
            Meteo m=ListMeteo.get(i);
            if (m != null) {
                if (m.getY() > 1280) {
                    ListMeteo.set(i,null);
                    continue;
                }
                m.draw(mCanvas);
            }
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mScreenWidth = w;
        mScreenHeight = h;

        mRow=w/IMAGE_SIZE;
        mCol=h/IMAGE_SIZE;

        /**得到小球越界区域**/
        mScreenBallWidth = mScreenWidth - IMAGE_SIZE;
        mScreenBallHeight = mScreenHeight - IMAGE_SIZE;

        map=new GameMap(mRow,mCol,maps,IMAGE_SIZE,IMAGE_SIZE);
        xinxi=map.getmMaps(mGate);
        mGateMax=map.getMpaNumber();
        Log.d("BallSurfaceView", "mGateMax:" + mGateMax);
        back=new GameBack(mScreenWidth,mScreenHeight,mbitmapBg);
        mLife=new Life(LifeBitmap);

        mHelp=new Help(mBtimapHelp,w,h);

        ListMeteo=new ArrayList<>();
        lifecount=0;
        mPosX=0;
        mPosY=0;

    }

    //传感器回调
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mGX = sensorEvent.values[0];
        mGY = sensorEvent.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }

    //触摸回调
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            switch (mStatus) {
                case WAITTING:
                    mStatus = GameStatus.RUNNING;
                    statu=-1;
                    isHelp=false;
                    startTime=System.currentTimeMillis();
                    break;
                case RUNNING:

                    break;
            }
        }
        return false;
    }


    private Bitmap loadImageById(int i) {
        return BitmapFactory.decodeResource(getResources(), i);
    }

    private int dp2px(Context context, float dp) {
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                        .getDisplayMetrics()));
        return px;
    }

    public void NextGate() {
        if (mGate != mGateMax-1){
            mGate ++;
        }else {
            Toast.makeText(getContext(), "已经是最后一关了", Toast.LENGTH_SHORT).show();
        }
        againgame();
    }
    public void PrevGete(){
        if(mGate!=0){
            mGate--;
        }else {
            Toast.makeText(getContext(), "已经是第一关了", Toast.LENGTH_SHORT).show();
        }
        againgame();
    }

    public void showhelp(){
        Log.d("BallSurfaceView", "111111");

        isHelp=true;
        mStatus=GameStatus.RUNNING;
    }

    public void pausegame(){
        mStatus=GameStatus.WAITTING;
    }

    public void continuegame(){
        mStatus=GameStatus.RUNNING;
    }

    public void stopgame(){
        isRunning=false;
    }

    public void againgame(){
        xinxi=map.getmMaps(mGate);
        ListMeteo.clear();
        lifecount=2;

        mEat.setIsrunning(false);
        mEat=new EatThread(xinxi,mCol,mRow);
        mEat.start();

        mTrap.setIsrunning(false);
        mTrap=new TrapThread(xinxi,mCol,mRow);
        mTrap.start();
        statu=-1;
    }

}
