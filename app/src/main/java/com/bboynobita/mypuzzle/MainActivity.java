package com.bboynobita.mypuzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bboynobita.mypuzzle.view.GamePuzzleLayout;

public class MainActivity extends AppCompatActivity {
private GamePuzzleLayout mGamePuzzleLayout;
    private TextView mLevel;
    private TextView mTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        mLevel= (TextView) findViewById(R.id.id_level);
        mTime= (TextView) findViewById(R.id.id_time);
        mGamePuzzleLayout= (GamePuzzleLayout) findViewById(R.id.game_puzzle);
        mGamePuzzleLayout.setTimeEnabled(true);
        mGamePuzzleLayout.setOnGamePuzzleListener(new GamePuzzleLayout.GamePuzzleListener() {
            @Override
            public void nextLevel(final int nextLevel) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Game Info");
                builder.setMessage("LEVEL UP!!!");
                builder.setPositiveButton("NEXT LEVEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                   mGamePuzzleLayout.nextLevel();
                        mLevel.setText(""+nextLevel);
                    }
                });
                builder.create().show();
            }

            @Override
            public void timechanged(int currentTime) {
                mTime.setText(""+currentTime);
            }

            @Override
            public void gameover() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Game Info");
                builder.setMessage("GAME OVER!!!");
                builder.setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGamePuzzleLayout.restart();
                    }
                });
                builder.setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.create().show();
            }
        });

    }



    @Override
    protected void onPause() {
        super.onPause();
       // Log.i("aa","调用了");
        mGamePuzzleLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGamePuzzleLayout.resume();
    }
}
