package com.howells.ball.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016.5.16.
 */
public class Life {
    private Bitmap[] mBitmaps;

    private RectF rectQ;
    private RectF rectX;
    private RectF rectL;

    public Life(Bitmap[] mBitmaps) {
        this.mBitmaps = mBitmaps;
        rectQ=new RectF(650,10,670,30);
        rectX=new RectF(670,10,690,30);
        rectL=new RectF(690,10,710,30);
    }

    public void draw(Canvas mCanvas,int score){

        mCanvas.drawBitmap(mBitmaps[10], null, rectQ, null);
        mCanvas.drawBitmap(mBitmaps[11], null, rectX, null);
        mCanvas.drawBitmap(mBitmaps[score], null, rectL, null);
    }
}
