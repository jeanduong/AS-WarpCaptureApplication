package com.example.jeanduong.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;


/**
 * TODO: document your custom view class.
 */
public class SelectAreaView extends View {
    enum Status {
        WAITING,
        FIRSTDOWN,
        DRAWING,
        SELECT,
        DRAG
    }

    static int DOUBLE_CLICK_SPEED = 333;

    public class Area {
        PointF _upLeft;
        PointF  _downRight;
        boolean isSelected;

        public Area() {
            _upLeft =  new PointF();
            _downRight =  new PointF();
            isSelected = false;
        }

        public Area(float up, float left, float bottom, float right) {
            _upLeft =  new PointF(up, left);
            _downRight =  new PointF(right, bottom);
            isSelected = false;
        }

        public Area(int up, int left, int bottom, int right) {
            _upLeft =  new PointF(clamp01(up / width), clamp01(left / height));
            _downRight =  new PointF(clamp01(bottom / width), clamp01(right / height));
            isSelected = false;
        }

        public void Select() {
            isSelected = true;
        }

        public void Unselect() {
            isSelected = false;
        }

        private float clamp01(float value) {
            if (value < 0f)
                return 0f;
            if (value > 1f)
                return 1f;
            return value;
        }

        public void Display(Canvas canvas) {
            canvas.drawRect(_upLeft.x * width ,
                    _upLeft.y * height ,
                    _downRight.x * width,
                    _downRight.y * height,
                    (isSelected ? paintFillSelect : paintFillNotSelect));
        }

        public void SetArea(int x, int y) {
            float xConvert = clamp01(x / width);
            float yConvert = clamp01(y / height);

            if (xConvert < _upLeft.x)
                _upLeft.x = xConvert;
            if (xConvert > _downRight.x)
                _downRight.x = xConvert;

            if (yConvert < _upLeft.y)
                _upLeft.y = yConvert;
            if (yConvert > _downRight.y)
                _downRight.y = yConvert;
        }

        public boolean IsInside(float x, float y) {
            return (_upLeft.x < x && _downRight.x > x
                    && _upLeft.y < y && _downRight.y > y);
        }

        public void Move(float x, float y) {
            Log.d("Move " + x + " " + y, "v");
            _upLeft.x += x;
            _upLeft.y += y;
            _downRight.x += x;
            _downRight.y += y;
        }
    }

    Status _st = Status.WAITING;
    Area _currentArea;

    ArrayList<Area> _areas;

    Paint paintStroke = new Paint();
    Paint paintFillSelect = new Paint();
    Paint paintFillNotSelect = new Paint();

    float height;
    float width;

    boolean isDefaultPositionsInit = false;

    public SelectAreaView(Context context) {
        super(context);
        init(context);
    }

    public SelectAreaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectAreaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        _areas = new ArrayList<>();

        paintStroke.setColor(Color.RED);
        paintStroke.setAntiAlias(true);
        paintStroke.setStrokeWidth(1);
        paintStroke.setStyle(Paint.Style.STROKE);

        paintFillNotSelect.setColor(Color.argb(200, 255, 255, 255));
        paintFillNotSelect.setStyle(Paint.Style.FILL);
//        paintFillNotSelect.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

        paintFillSelect.setColor(Color.argb(200, 255, 0, 255));
        paintFillSelect.setStyle(Paint.Style.FILL);
//        paintFillSelect.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

    }

    //Delay because width and height are not define when init was called
    private void InitPositions() {
        lastMotionPosition = new PointF();

        height = getHeight();
        width = getWidth();

        isDefaultPositionsInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isDefaultPositionsInit)
            InitPositions();

        canvas.drawColor(Color.TRANSPARENT);

        for (Area ar:
                _areas) {
            ar.Display(canvas);
        }
    }

    float lastDownTime;
    PointF lastMotionPosition;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x_cord = (int) event.getX();
        int y_cord = (int) event.getY();
        event.getEventTime();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                boolean cancelAction = false;

                if (_st == Status.SELECT)
                {
                    if (event.getEventTime() - lastDownTime < DOUBLE_CLICK_SPEED
                            && _currentArea.IsInside(event.getX() / width, event.getY() / height)) {
                        Log.d("Remove Area !", "v");
                        _areas.remove(_currentArea);
                        cancelAction = true;
                    } else
                        Log.d("unselect Area !", "v");

                    UnSelect();
                    _st = Status.WAITING;
                }
                else if (_st == Status.WAITING && !cancelAction) {
                    for (Area ar:
                            _areas) {
                        if (ar.IsInside(event.getX() / width, event.getY() / height))
                        {
                            Log.d("Select Area !", "v");
                            _currentArea = ar;
                            _currentArea.Select();
                            _st = Status.SELECT;
                            invalidate();
                            break;
                        }
                    }
                }

                if (_st == Status.WAITING && !cancelAction)
                    _st = Status.FIRSTDOWN;

                lastDownTime = event.getEventTime();
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (_st == Status.FIRSTDOWN)
                    _st = Status.WAITING;
                else if (_st == Status.DRAWING || _st == Status.DRAG) {
                    UnSelect();
                    _st = Status.WAITING;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (_st == Status.DRAWING)
                {
                    _currentArea.SetArea(x_cord, y_cord);
                } else if (_st == Status.SELECT) {
                    _st = Status.DRAG;
                }
                else if (_st == Status.DRAG) {
                    _currentArea.Move((event.getX() - lastMotionPosition.x) / width,
                            (event.getY() - lastMotionPosition.y) / height);
                }
                else if (_st == Status.FIRSTDOWN) {
                    _st = Status.DRAWING;
                    Log.d("New Area !", "v");
                    _currentArea = new Area(x_cord - 25, y_cord - 25, x_cord + 25, y_cord + 25);
                    _areas.add(0, _currentArea); // push on the top, to easily delete the last area created
                }
                invalidate();
                break;

            default: break;
        }
        lastMotionPosition.x = event.getX();
        lastMotionPosition.y = event.getY();
        return true;
    }

    public void UnSelect()
    {
        _currentArea.Unselect();
        _currentArea = null;
    }

    public Area[] GetAllAreas() {
        Object[] tmp = _areas.toArray();
        int nb_areas = tmp.length;
        Area[] ar = new Area[nb_areas];

        for (int k = 0; k < nb_areas; ++k)
        {
            ar[k] = (Area)tmp[k];
        }

        return ar;
    }
}
