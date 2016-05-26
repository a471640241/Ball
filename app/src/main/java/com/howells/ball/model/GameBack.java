package com.howells.ball.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016.5.16.
 */
public class GameBack {
    private Bitmap[] mBitmaps;
    private int mGameWidth;
    private int mGameHeight;
    private RectF rectF;
    private int flag;

    public GameBack(int mGameWidth, int mGameHeight,Bitmap[] mBitmaps) {
        this.mBitmaps = mBitmaps;
        this.mGameWidth = mGameWidth;
        this.mGameHeight = mGameHeight;
        rectF=new RectF(0,0,mGameWidth,mGameHeight);
        flag=0;
    }

    public void draw(Canvas mCanvas){

        if(flag==9)
            flag=0;
        mCanvas.drawBitmap(mBitmaps[flag], null, rectF, null);
        flag++;
    }
}
