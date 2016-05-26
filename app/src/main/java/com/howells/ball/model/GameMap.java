package com.howells.ball.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016.5.13.
 */
public class GameMap {

    private Bitmap[] mBitmaps;
    private int picwidth;
    private int picheight;
    private int row;
    private int col;
    private RectF rectF;


    private Integer [][][] mMaps=new Integer [][][]{
            {	//第一张图
                    { 1,0,1,0,0,1,1,1,1,},
                    { 1,0,1,0,0,0,0,1,0,},
                    { 1,0,1,1,1,1,1,1,0,},
                    { 1,1,1,1,0,0,0,0,0,},
                    { 1,0,0,0,0,0,0,0,0,},
                    { 1,0,0,1,1,1,0,1,0,},
                    { 1,0,0,1,0,1,1,1,0,},
                    { 1,1,0,1,0,1,0,1,0,},
                    { 1,0,0,1,0,0,0,0,0,},
                    { 1,0,1,3,0,1,0,0,16,},
                    { 1,0,0,1,0,1,1,0,1,},
                    { 1,1,1,1,1,1,16,1,1,},
                    { 1,0,0,3,0,1,1,0,0,},
                    { 1,1,1,1,1,7,1,1,1,},
                    { 1,0,0,1,0,1,16,0,0,},
                    { 1,1,7,1,1,1,1,1,2,},
            },
            {
                    { 1,0,1,0,0,1,1,1,1,},
                    { 1,0,1,0,0,0,0,1,0,},
                    { 1,0,1,1,1,1,1,1,0,},
                    { 1,1,1,1,2,3,4,0,0,},
                    { 1,0,0,0,0,0,0,0,0,},
                    { 1,0,0,1,1,1,7,1,0,},
                    { 1,0,0,1,0,1,9,1,0,},
                    { 1,1,0,1,0,1,0,1,0,},
                    { 1,0,0,1,0,11,10,0,0,},
                    { 1,0,1,3,0,13,0,0,16,},
                    { 1,0,0,16,0,1,1,0,1,},
                    { 1,1,1,1,1,1,16,1,1,},
                    { 1,0,0,3,0,1,1,0,0,},
                    { 1,1,1,1,1,7,1,1,1,},
                    { 1,0,0,1,0,1,16,0,0,},
                    { 1,1,7,1,1,1,1,1,2,},
            }
    };

    public GameMap(int row, int col, Bitmap[] maps,int picwidth,int picheight){
        this.row=row;//9
        this.col=col;//16
        mBitmaps=maps;
        this.picheight=picheight;
        this.picwidth=picwidth;
        rectF=new RectF();
    }

    public void draw(Canvas mCanvas,Integer[][] mapxinxi){

        for(int i=0;i<col;i++){
            for(int j=0;j<row;j++){
                int k=mapxinxi[i][j];
                if(k==0){
                    continue;
                }
                rectF.set(j*picwidth,i*picheight,(j+1)*picwidth,(i+1)*picheight);
                mCanvas.drawBitmap(mBitmaps[k], null, rectF, null);
            }
        }
    }


    public Integer[][] getmMaps(int level) {
        return mMaps[level];
    }
    public int getMpaNumber(){
        return mMaps.length;
    }
}
