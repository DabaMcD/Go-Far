package com.raptor.raptor.gofar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View {
    private ArrayList<ArrayList<Double>> grid;
    private float drawDistance;
    private long frameCount;
    private Paint paint;

    public GameView(Context context) {
        super(context);
        constructor();
    }
    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        constructor();
    }
    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor();
    }
    private void constructor() {
        grid = new ArrayList<>();
        drawDistance = 0;
        frameCount = 0;
        paint = new Paint();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        frameCount ++;

        super.onDraw(canvas);
    }
    void draw() {
        invalidate();
        requestLayout();
    }
    void drawGrid(float xOffset, float yOffset, float zOffset, float turn, Canvas canvas) {
        for(int i = grid.size() - 1; i > 0; i--) {
            for(int j = 0; j < grid.get(i).size(); j++) {
                if(i > drawDistance) {
                    continue;
                }
                float x = j - xOffset;
                float ri = i - yOffset;
                x -= turn * ri;
                float y = i - yOffset;
                double z = grid.get(i).get(j) - zOffset;
                if(y < 0.01) {
                    continue;
                }
                double d = y * 0.01;
                double s = 0.5 / d;
                boolean water = false;
                if(z > 256) {
                    z -= 512;
                    water = true;
                }
                double sx = (x / d);
                double sy = (z / d);
                float c = map(dist(0, 0, x, y), 0, 17, 1, 0);
                if(c < 0) {
                    c = 0;
                }
                int c2 = Color.rgb(255, 255, 192);
                if(sx < -(210 + s) || sx > (210 + s)) {
                    continue;
                }
                if(water) {
                    float animation = (frameCount % 15) / 15;
                    c2 = Color.rgb(0, 96, 255);
                    int c3 = Color.rgb(64, 128, 255);
                    paint.setColor(Color.rgb(lerpColor(Color.rgb(128, 112, 96), c2, c), lerpColor(Color.rgb(128, 112, 96), c2, c), lerpColor(Color.rgb(128, 112, 96), c2, c)));
                    rect((float) (sx - s), (float) (sy - s), (float) (s * 2), (float) (s * 30), canvas);
                    paint.setColor(Color.rgb(lerpColor(Color.rgb(128, 112, 96), lerpColor(c2, c3, animation), c), lerpColor(Color.rgb(128, 112, 96), lerpColor(c2, c3, animation), c), lerpColor(Color.rgb(128, 112, 96), lerpColor(c2, c3, animation), c)));
                    rect((float) (sx - s), (float) (sy + (s * (animation - 1))), (float) (s * 2), (float) (s * 0.5), canvas);
                    paint.setColor(Color.rgb(lerpColor(Color.rgb(128, 112, 96), lerpColor(c3, c2, animation), c), lerpColor(Color.rgb(128, 112, 96), lerpColor(c3, c2, animation), c), lerpColor(Color.rgb(128, 112, 96), lerpColor(c3, c2, animation), c)));
                    rect((float) (sx - s), (float) (sy + (s * animation)), (float) (s * 2), (float) (s * 0.5), canvas);
                } else {
                    if(i > (drawDistance - 5)) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(Color.rgb(0, 255, 0));
                        rect((float) (sx - s), (float) (sy - s), (float) (s * 2), (float) (s * 300), canvas);
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(Color.rgb(128, 112, 96));
                        // todo: make sure the code above isn't showing whackily
                    } else {
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(Color.rgb(lerpColor(Color.rgb(128, 112, 96), c2, c), lerpColor(Color.rgb(128, 112, 96), c2, c), lerpColor(Color.rgb(128, 112, 96), c2, c)));
                    }
                    rect((float) (sx - s), (float) (sy - s), (float) (s * 2), (float) (s * 300), canvas);
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                }
            }
        }
    }
    private void rect(float left, float top, float width, float height, Canvas canvas) {
        canvas.drawRect(left, top, left + width, top + height, paint);
    }
    float constrain(float e, float t, float n) {
        return (((e > n) ? n : e) < t) ? t : e;
    }
    float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
    }
    float map(float e, float t, float n, float r, float i) {
        return r+(i-r)*((e-t)/(n-t));
    }
    static int lerpColor(int a, int b, double phase) {
        int red = (int) (Color.red(a) * phase + Color.red(b) * (1 - phase));
        int green = (int) (Color.green(a) * phase + Color.green(b) * (1 - phase));
        int blue = (int) (Color.blue(a) * phase + Color.blue(b) * (1 - phase));
        return Color.rgb(red, green, blue);
    }
}
