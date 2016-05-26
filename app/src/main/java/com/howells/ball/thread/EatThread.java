package com.howells.ball.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016.5.16.
 */
public class EatThread extends Thread{
    private Integer xinxi[][];
    private List<Integer> PosX;
    private List<Integer> PosY;
    private List<Integer> eat;
    private boolean isrunning=true;
    private final int sTime=500;

    public EatThread(Integer[][] xinxi,int col,int row){
        this.xinxi=xinxi;
        PosX=new ArrayList<>();
        PosY=new ArrayList<>();
        eat=new ArrayList<>();
        for(int i=0;i<col;i++){
            for (int j=0;j<row;j++){
                if(xinxi[i][j]>=3&&xinxi[i][j]<=6){
                    PosX.add(i);
                    PosY.add(j);
                    eat.add(xinxi[i][j]);
                }
            }
        }
    }

    @Override
    public void run() {
        while (isrunning){
            long start = System.currentTimeMillis();

            for(int i=0;i<eat.size();i++){

                if(eat.get(i)==7){
                    eat.set(i,3);
                }
                xinxi[PosX.get(i)][PosY.get(i)]=eat.get(i);
                eat.set(i,eat.get(i)+1);
            }


            long end = System.currentTimeMillis();

            try {
                if (end - start < sTime) {
                    Thread.sleep(sTime - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setIsrunning(boolean isrunning) {
        this.isrunning = isrunning;
    }
}
