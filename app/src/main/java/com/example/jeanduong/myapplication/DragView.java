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

public class DragView extends View {
    private Paint pt = new Paint();
    private Path ph = new Path();
    private Paint paintFill = new Paint();

    private int w;
    private int h;
    protected int x_1, x_2, x_3, x_4;
    protected int y_1, y_2, y_3, y_4;
    private boolean first_time_draw = true;
    int argmin = 1;

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);

        pt.setAntiAlias(true);
        pt.setStrokeWidth(3);
        pt.setStyle(Paint.Style.STROKE);
        pt.setStrokeJoin(Paint.Join.ROUND);

        paintFill.setColor(Color.argb(150, 0, 0, 0));
        paintFill.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas cv) {

        w = getWidth();
        h = getHeight();

        if (first_time_draw) {
            x_1 = w / 4;
            x_2 = 3 * w / 4;
            x_3 = x_2;
            x_4 = x_1;

            y_1 = h / 4;
            y_2 = y_1;
            y_3 = 3 * h / 4;
            y_4 = y_3;
            first_time_draw = false;
        }

        drawVertices(cv);
        drawBorders(cv);
        cv.drawPath(ph, pt);
    }

    protected void onSizeChanged(){
        w = getWidth();
        h = getHeight();
    }

    protected void drawBorders(Canvas cv) {
        //Draw cut area
        int Ax = min(x_1, x_4);
        int Bx = max(x_2, x_3);
        int Ay = min(y_1, y_2);
        int By = max(y_3, y_4);

        cv.drawRect(Ax,0, Bx, Ay, paintFill);//Top
        cv.drawRect(0, 0, Ax, getBottom(), paintFill);//Left
        cv.drawRect(Bx, 0, getRight(), getBottom(), paintFill);//Right
        cv.drawRect(Ax, By, Bx, getBottom(), paintFill);//Bottom
    }

    protected void drawVertices(Canvas cv){
        pt.setColor(Color.RED);
        cv.drawCircle(x_1, y_1, 10, pt);
        cv.drawCircle(x_2, y_2, 10, pt);
        cv.drawCircle(x_3, y_3, 10, pt);
        cv.drawCircle(x_4, y_4, 10, pt);

        cv.drawLine(x_1, y_1, x_2, y_2, pt);
        cv.drawLine(x_2, y_2, x_3, y_3, pt);
        cv.drawLine(x_3, y_3, x_4, y_4, pt);
        cv.drawLine(x_4, y_4, x_1, y_1, pt);

        int x_min = min(x_1, x_4);
        int x_max = max(x_2, x_3);
        int y_min = min(y_1, y_2);
        int y_max = max(y_3, y_4);

        pt.setColor(Color.MAGENTA);
        cv.drawRect(x_min, y_min, x_max, y_max, pt);
        pt.setColor(Color.RED);
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
                ph.addCircle(x_touch, y_touch, 10, Path.Direction.CCW);
                break;

            case MotionEvent.ACTION_UP:
                ph.reset();
                if (argmin == 1){
                    int new_x_1 = (int) x_touch;
                    int new_y_1 = (int) y_touch;

                    if ((new_x_1 < x_3) && (new_y_1 < y_3) && check_convexity(new_x_1, new_y_1, x_2, y_2, x_3, y_3, x_4, y_4))
                    {
                        x_1 = new_x_1;
                        y_1 = new_y_1;
                    }
                }
                else if (argmin == 2){
                    int new_x_2 = (int) x_touch;
                    int new_y_2 = (int) y_touch;

                    if ((new_x_2 > x_4) && (new_y_2 < y_4) && check_convexity(x_1, y_1, new_x_2, new_y_2, x_3, y_3, x_4, y_4))
                    {
                        x_2 = new_x_2;
                        y_2 = new_y_2;
                    }
                }
                else if (argmin == 3){
                    int new_x_3 = (int) x_touch;
                    int new_y_3 = (int) y_touch;

                    if ((new_x_3 > x_1) && (new_y_3 > y_1) && check_convexity(x_1, y_1, x_2, y_2, new_x_3, new_y_3, x_4, y_4))
                    {
                        x_3 = new_x_3;
                        y_3 = new_y_3;
                    }
                }
                else{
                    int new_x_4 = (int) x_touch;
                    int new_y_4 = (int) y_touch;

                    if ((new_x_4 < x_2) && (new_y_4 > y_2) && check_convexity(x_1, y_1, x_2, y_2, x_3, y_3, new_x_4, new_y_4))
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

    protected double det(double x, double y, double x_prime, double y_prime){
        return (x * y_prime - x_prime * y);
    }

    protected boolean check_convexity(int x_1, int y_1, int x_2, int y_2, int x_3, int y_3, int x_4, int y_4){
        double x_bk = x_4 - x_1;
        double x_fd = x_2 - x_1;
        double y_bk = y_4 - y_1;
        double y_fd = y_2 - y_1;

        if (det(x_bk, y_bk, x_fd, y_fd) > 0) return false;

        x_bk = -x_fd;
        y_bk = -y_fd;
        x_fd = x_3 - x_2;
        y_fd = y_3 - y_2;

        if (det(x_bk, y_bk, x_fd, y_fd) > 0) return false;

        x_bk = -x_fd;
        y_bk = -y_fd;
        x_fd = x_4 - x_3;
        y_fd = y_4 - y_3;

        if (det(x_bk, y_bk, x_fd, y_fd) > 0) return false;

        x_bk = -x_fd;
        y_bk = -y_fd;
        x_fd = x_1 - x_4;
        y_fd = y_1 - y_4;

        if (det(x_bk, y_bk, x_fd, y_fd) > 0) return false;

        return true;
    }
}
