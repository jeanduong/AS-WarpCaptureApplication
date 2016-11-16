package com.example.jeanduong.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

public class RectDragView extends View {

    private Path ph = new Path();
    private Paint pt = new Paint();
    private Paint paintFill = new Paint();

    private int w;
    private int h;
    protected int x_1, x_2, x_3, x_4;
    protected int y_1, y_2, y_3, y_4;
    private boolean first_time_draw = true;
    int argmin = 1;

    public RectDragView(Context context, AttributeSet attrs) {
        super(context, attrs);

        pt.setColor(Color.RED);
        pt.setAntiAlias(true);
        pt.setStrokeWidth(3);
        pt.setStyle(Paint.Style.STROKE);
        pt.setStrokeJoin(Paint.Join.ROUND);

        paintFill.setColor(Color.argb(150, 0, 0, 0));
        paintFill.setStyle(Paint.Style.FILL);

        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas cv) {

        w = getWidth();
        h = getHeight();

        if (first_time_draw) {
            x_1 = w / 2;
            x_2 = 3 * w / 4;
            x_3 = x_1;
            x_4 = w / 4;

            y_1 = h / 4;
            y_2 = h / 2;
            y_3 = 3 * h / 4;
            y_4 = y_2;
            first_time_draw = false;
        }

        drawDragPoints(cv);
        fillBorders(cv);
        cv.drawPath(ph, pt);
    }

    protected void onSizeChanged(){
        w = getWidth();
        h = getHeight();
    }

    protected void fillBorders(Canvas cv) {
        //Draw cut area
        int Ax = min(x_2, x_4);
        int Bx = max(x_2, x_4);
        int Ay = min(y_1, y_3);
        int By = max(y_1, y_3);

        cv.drawRect(Ax,0, Bx, Ay, paintFill);//Top
        cv.drawRect(0, 0, Ax, getBottom(), paintFill);//Left
        cv.drawRect(Bx, 0, getRight(), getBottom(), paintFill);//Right
        cv.drawRect(Ax, By, Bx, getBottom(), paintFill);//Bottom
    }

    protected void drawDragPoints(Canvas cv){

        x_1 = (x_2 + x_4) / 2;
        x_3 = x_1;
        y_2 = (y_1 + y_3) / 2;
        y_4 = y_2;

        cv.drawCircle(x_1, y_1, 30, pt);
        cv.drawCircle(x_2, y_2, 30, pt);
        cv.drawCircle(x_3, y_3, 30, pt);
        cv.drawCircle(x_4, y_4, 30, pt);

        cv.drawRect(x_4, y_1, x_2, y_3, pt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x_touch = event.getX();
        float y_touch = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ph.moveTo(x_touch, y_touch);

                double min_distance = pow(x_touch - x_1, 2) + pow(y_touch - y_1, 2);

                argmin = 1;

                double d_2 = pow(x_touch - x_2, 2) + pow(y_touch - y_2, 2);

                if (d_2 < min_distance){
                    min_distance = d_2;
                    argmin = 2;
                }

                double d_3 = pow(x_touch - x_3, 2) + pow(y_touch - y_3, 2);

                if (d_3 < min_distance){
                    min_distance = d_3;
                    argmin = 3;
                }

                if (pow(x_touch - x_4, 2) + pow(y_touch - y_4, 2) < min_distance){
                    argmin = 4;
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                ph.reset();
                ph.addCircle(x_touch, y_touch, 30, Path.Direction.CCW);
                break;

            case MotionEvent.ACTION_UP:
                ph.reset();
                if (argmin == 1){
                    int new_x_1 = (int) x_touch;
                    int new_y_1 = (int) y_touch;

                    if (new_y_1 < y_3)
                    {
                        x_1 = new_x_1;
                        y_1 = new_y_1;
                    }
                }
                else if (argmin == 2){
                    int new_x_2 = (int) x_touch;
                    int new_y_2 = (int) y_touch;

                    if (new_x_2 > x_4)
                    {
                        x_2 = new_x_2;
                        y_2 = new_y_2;
                    }
                }
                else if (argmin == 3){
                    int new_x_3 = (int) x_touch;
                    int new_y_3 = (int) y_touch;

                    if (new_y_3 > y_1)
                    {
                        x_3 = new_x_3;
                        y_3 = new_y_3;
                    }
                }
                else{
                    int new_x_4 = (int) x_touch;
                    int new_y_4 = (int) y_touch;

                    if (new_x_4 < x_2)
                    {
                        x_4 = new_x_4;
                        y_4 = new_y_4;
                    }
                }

                break;

            default:
                return false;
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }



}
