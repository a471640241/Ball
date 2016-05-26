package com.howells.ball.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016.5.23.
 */
public class Help {
    private Bitmap mHelpBitmap;
    private RectF rectF;
    private int width;
    private int hight;
    private int bitmapWidth;
    private int bitmapHight;
    public Help(Bitmap mHelpBitmap,int width,int hight) {
        this.mHelpBitmap = mHelpBitmap;
        this.width=width;
        this.hight=hight;
        bitmapWidth=mHelpBitmap.getWidth();
        bitmapHight=mHelpBitmap.getHeight();
        rectF =new RectF();
    }
    public void draw(Canvas canvas){
        rectF.set(width/2-bitmapWidth/2,hight/2-bitmapHight/2,width/2+bitmapWidth/2,hight/2+bitmapHight/2);
        canvas.drawBitmap(mHelpBitmap,null,rectF,null);
    }
}
