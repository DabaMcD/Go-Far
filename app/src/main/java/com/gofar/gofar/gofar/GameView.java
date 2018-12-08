package com.gofar.gofar.gofar;

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
    private float buttonW, buttonH, buttonTxtSize;
    private float canyonX, canyonDistance;
    private double canyonWidth;
    private boolean canyon;
    private float distance, difficulty;
    private String state;
    private int startDifficulty;
    private float x;
    private ArrayList<String> difficulties;
    static boolean mouseIsReleased = false;

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
        buttonW = 160 * Screen.height / 400;
        buttonH = 40 * Screen.height / 400;
        buttonTxtSize = 15 * Screen.height / 400;
        canyonWidth = 5;
        canyonX = 0;
        canyonDistance = 0;
        canyon = false;
        distance = 0;
        difficulty = 0;
        startDifficulty = 0;
        difficulties = new ArrayList<>();
        difficulties.add("Easy");
        difficulties.add("Medium");
        difficulties.add("Hard");
        difficulties.add("Insane");
        x = 0;
        state = "menu";
        paint = new Paint();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.rgb(128, 112, 96));
        canvas.drawRect(-10, -10, Screen.width + 10, Screen.height + 10, paint);
        paint.setStyle(Paint.Style.FILL);

        switch(state) {
            case "menu":
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(20);
                text("Go Far", Screen.width / 2, Screen.height / 8, canvas);
                paint.setTextSize(10);
                text("a ◄BLITZ► game", Screen.width / 2, Screen.height / 5, canvas);

                if(button("Play", Screen.width / 2, Screen.height / 2, canvas)) {
                    state = "newGame";
                }
                if(button("Help", Screen.width / 2, Screen.height * 5 / 8, canvas)) {
                    state = "help";
                }
                break;
            case "help":
                paint.setTextSize(12);
                paint.setTextAlign(Paint.Align.CENTER);
                text("Move the mouse left and right to move left and right.\nDon't hit anything!", 200, 200, canvas);
                if(button("Back", Screen.width / 2, Screen.height * 5 / 8, canvas)) {
                    state = "menu";
                }
                break;
            case "newGame":
                paint.setTextSize(12);
                paint.setTextAlign(Paint.Align.CENTER);
                if(button("Easy", Screen.width / 2, Screen.height * 3 / 8, canvas)) {
                    initGame(0);
                }
                if(button("Medium", Screen.width / 2, Screen.height / 2, canvas)) {
                    initGame(1);
                }
                if(button("Hard", Screen.width / 2, Screen.height * 5 / 8, canvas)) {
                    initGame(2);
                }
                if(button("Insane", Screen.width / 2, Screen.height * 3 / 4, canvas)) {
                    initGame(3);
                }
                break;
            case "game":
                canvas.save();
                canvas.scale(Screen.width / 400, Screen.height / 400);
                if(Math.random() < 0.01 && canyonDistance > 100) {
                    canyonDistance = 0;
                    if(!canyon) {
                        canyonWidth = Math.floor(Math.random() * 4 + 4);
                        canyonDistance = -20;
                    } else {
                        canyon = false;
                    }
                }
                if(canyonDistance == -1) {
                    canyonX = 0;
                    canyon = true;
                }
                canyonX += Math.round(Math.random() * 2 - 1);
                canyonWidth += Math.round(Math.random() * 2 - 1);
                canyonWidth = constrain((float) canyonWidth, constrain((float) (4 - (difficulty * 0.9)), (float) 1.5, 4), 8);
                if((frameCount % 3) == 0) {
                    canyonDistance++;
                    distance++;
                    grid.shift();
                    ArrayList<Double> newline = new ArrayList<>();
                    for(int i = 0; i < 40; i++) {
                        newline.add(generate(i, 0));
                    }
                    grid.add(newline);
                }
                while(x < -0.5) {
                    canyonX++;
                    x += 1;
                    for(int i = 0; i < grid.size(); i++) {
                        grid.get(i).pop();
                        grid.get(i).unshift(generate(0, i));
                    }
                }
                while(x > 0.5) {
                    canyonX--;
                    x -= 1;
                    for(int i = 0; i < grid.size(); i ++) {
                        grid.get(i).shift();
                        grid.get(i).add(generate(0, i));
                    }
                }
                canvas.save();
                canvas.translate(200, 200);
                canvas.rotate((float) (((Touch.x * 0.005) - 1) * -10));
                drawGrid(x + 20, (frameCount / 3) % 1, -3, (float) ((Touch.x - 200) * 0.005), canvas);
                canvas.restore();
                x += (Touch.x * 0.005) - 1;
                if(canyonDistance < -1) {
                    paint.setTextSize(20);
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(Color.argb((int) (Math.sin(frameCount * 20) * 50 + 100), 255, 0, 0));
                    text("Canyon approaching!", 200, 50, canvas);
                }
                paint.setColor(Color.rgb(255, 255, 255));
                paint.setTextSize(12);
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(distance + " score", 3, 3 - paint.getTextSize() * 2 / 3, canvas);
                if(grid.get(1).get((int) (Math.floor(20 + x + 0.5))) < -3) {
                    state = "gameover";
                    frameCount = 200;
                    drawDistance = 30;
                }
                if(drawDistance < 30) {
                    drawDistance += 0.8;
                }
                difficulty += 0.001;
                canvas.restore();
                break;
            case "gameover":
                paint.setTextSize(buttonTxtSize * 4 / 5);
                paint.setTextAlign(Paint.Align.CENTER);
                int framesSinceDeath = (int) (frameCount - 200);
                double fly = constrain((framesSinceDeath / 50), 0, 1);
                fly = 1 - Math.pow(1 - fly, 5);
                canvas.save();
                canvas.scale(Screen.width / 400, Screen.height / 400);
                canvas.translate(200, 200);
                canvas.rotate(lerp((float) (((Touch.x * 0.005) - 1) * -30), 0, (float) fly));
                drawGrid(x + 20, (float) (-fly * 0.2), (float) (-3 + fly * 2), 0, canvas);
                canvas.restore();
                paint.setColor(Color.argb(150, 0, 0, 0));
                roundRect((Screen.width - buttonW) / 2, (Screen.height - buttonH) / 2, buttonW, buttonH, 5, canvas);
                paint.setColor(Color.rgb(255, 255, 255));
                text("Score: " + distance + "\nDifficulty: " + difficulties.get(startDifficulty), Screen.width / 2, Screen.height / 2, canvas);
                if(button("Menu", Screen.width - (buttonW / 2) - (10 * (Screen.height / 400)), Screen.height - buttonH / 2 - (10 * (Screen.height / 400)), canvas)) {
                    state = "menu";
                }
                if(button("Retry", buttonW / 2 + (10 * (Screen.height / 400)), Screen.height - buttonH / 2 - (10 * (Screen.height / 400)), canvas)) {
                    initGame(startDifficulty);
                }
                break;
        }

        mouseIsReleased = false;
        frameCount ++;
        super.onDraw(canvas);
    }
    void draw() {
        invalidate();
        requestLayout();
    }
    void initGame(int d) {
        startDifficulty = d;
        difficulty = d;
        grid = new ArrayList<>();
        state = "game";

        for(int i = 0; i < 19; i++) {
            grid.add(new ArrayList<Double>());
            for(int j = 0; j < 40; j++) {
                grid.get(i).add(Math.random());
            }
        }
        distance = 0;
        canyon = false;
        drawDistance = 0;
        canyonDistance = 0;
    }
    boolean button(String txt, float x, float y, Canvas canvas) {
        boolean returnValue = false;
        paint.setColor(Color.argb(80, 0, 0, 0));
        if(Touch.x > x - (buttonW / 2) && Touch.y > y - (buttonH / 2) && Touch.x < x + (buttonW / 2) && Touch.y < y + (buttonH / 2)) {
            if(Touch.isTouching) {
                paint.setColor(Color.argb(120, 0, 0, 0));
            }
            if(mouseIsReleased) {
                returnValue = true;
            }
        }
        roundRect(x - (buttonW / 2), y - (buttonH / 2), buttonW, buttonH, 5, canvas);
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setTextSize(buttonTxtSize);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(txt, x, y + paint.getTextSize() / 3, paint);
        return returnValue;
    }
    double generate(float x, float y) {
        if((x < ((20 - canyonWidth) + canyonX) || x > ((20 + canyonWidth) + canyonX)) && canyon) {
            float d = constrain(Math.abs(x - (20 + canyonX)), 0, 30);
            return -(d - canyonWidth) * 5 + Math.random() * 6;
        }
        if(canyon && Math.abs(x - (20 + canyonX)) < 3) {
            return 512 + Math.random();
        } else if(canyon) {
            return Math.random();
        }
        double h = Math.random();
        if(Math.random() < 0.01 + difficulty * 0.02 && !canyon) {
            h = (Math.random() * -5) - 5;
        }
        if(Noise.noise(x * 0.03, (y - distance) * 0.03) < 0.4 && h > -3) {
            h = Math.random() + 1;
            h += 512;
        }
        h += constrain((float) (Noise.noise(x * 0.05, (y - distance) * 0.05) - 0.5), 0, 1) * 300;
        return h;

        // TODO: likely some Perlin Noise issues in the code above
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
    private void roundRect(float left, float top, float width, float height, float rad, Canvas canvas) {
        canvas.drawRoundRect(left, top, left + width, top + height, rad, rad, paint);
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
    void text(String text, float x, float y, Canvas canvas) {
        canvas.drawText(text, x, y - paint.getTextSize() / 3, paint);
    }
    float lerp(float e, float t, float n){
        return (t - e) * n + e;
    }
}
