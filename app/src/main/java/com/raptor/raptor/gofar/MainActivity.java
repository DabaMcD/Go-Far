package com.raptor.raptor.gofar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    GameView gameView;
    Thread mainThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        createAndStartMainThread();
    }
    private void createAndStartMainThread() {
        mainThread = new Thread() {
            public void run() {
                while (mainThread.isAlive()) {
                    long prevMillis = System.currentTimeMillis();
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameView.draw();
                            }
                        });
                        long waitTime = prevMillis - System.currentTimeMillis() + 30;
                        if(waitTime > 0) {
                            Thread.sleep(waitTime);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mainThread.start();
    }
}
