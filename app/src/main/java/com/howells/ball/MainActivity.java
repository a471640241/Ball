package com.howells.ball;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Administrator on 2016.5.12.
 */
public class MainActivity extends AppCompatActivity {

    BallSurfaceView mGame;
    MediaPlayer mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mGame= (BallSurfaceView) findViewById(R.id.game);
        mPlayer=MediaPlayer.create(this,R.raw.backmuics);
        mPlayer.setLooping(true);
        mPlayer.start();
  //      mPlayer.stop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        mGame.pausegame();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setItems(getResources().getStringArray(R.array.ItemArray), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 自动生成的方法存根
                boolean flag=true;
                switch (arg1){
                    case 0:
                        mGame.continuegame();
                        flag=false;
                        break;
                    case 1:
                        mGame.againgame();
                        break;
                    case 2:
                        mGame.PrevGete();
                        break;
                    case 3:
                        mGame.NextGate();
                        break;
                    case 4:
                        if(mPlayer.isPlaying()){
                            mPlayer.stop();
                        }else {
                            try {
                                mPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mPlayer.start();
                        }
                        flag=false;
                        break;
                    case 5:
                        mGame.stopgame();
                        finish();
                        break;
                    case 6:
                        mGame.showhelp();
                        break;
                }
                if(flag){
                    Toast.makeText(MainActivity.this, "点击任意位置继续游戏", Toast.LENGTH_SHORT).show();
                }
                arg0.dismiss();
            }
        });
        builder.show();
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.start();
        mGame.continuegame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
        mGame.pausegame();
    }
}
