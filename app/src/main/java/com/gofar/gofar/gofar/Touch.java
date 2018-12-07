package com.gofar.gofar.gofar;

import android.view.MotionEvent;
import android.view.View;

class Touch {
    static float x;
    static float y;
    static boolean isTouching = false;

    static void setTouchListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        isTouching = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x = event.getX();
                        y = event.getY();
                        isTouching = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        x = event.getX();
                        y = event.getY();
                        isTouching = false;
                        GameView.mouseIsReleased = true;
                        break;
                }
                return true;
            }
        });
    }
}
