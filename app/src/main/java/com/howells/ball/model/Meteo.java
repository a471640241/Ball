package com.howells.ball.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016.5.16.
 */
public class Meteo {
    private Bitmap[] mBitmaps;

    private RectF rect;
    private int x;
    private int y;
    private int picwidth;
    private int picheight;
    private int flag;

    public Meteo(Bitmap[] mBitmaps,int width,int height) {
        this.mBitmaps = mBitmaps;
        rect=new RectF();
        picheight=height;
        picwidth=width;
        flag=0;
        y=0;
    }

    public void draw(Canvas mCanvas){
        y+=10;

        rect.set(x,y,x+picwidth,y+picheight);

        if (flag==8){
            flag=0;
        }
        mCanvas.drawBitmap(mBitmaps[flag], null, rect, null);
        flag++;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
