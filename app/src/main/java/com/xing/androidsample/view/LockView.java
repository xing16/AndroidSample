package com.xing.androidsample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.xing.androidsample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/5/30.
 */

public class LockView extends View {

    private static final int DEFAULT_NORMAL_COLOR = 0xee776666;
    private static final int DEFAULT_MOVE_COLOR = 0xee0000ff;
    private static final int DEFAULT_ERROR_COLOR = 0xeeff0000;
    private static final int DEFAULT_ROW_COUNT = 3;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_MOVE = 1;
    private static final int STATE_ERROR = 2;


    private int normalColor; // 无滑动默认颜色
    private int moveColor;   // 滑动选中颜色
    private int errorColor;  // 错误颜色

    private int mWidth;    // 控件宽度

    private int mHeight;   // 控件高度

    private float radius;    // 外圆半径

    private int rowCount;

    private PointF[] points;   // 一维数组记录所有圆点的坐标点

    private Paint innerCirclePaint; // 内圆画笔

    private Paint outerCirclePaint; // 外圆画笔

    private SparseIntArray stateSparseArray;

    private List<PointF> selectedList = new ArrayList<>();

    private List<Integer> standardPointsIndexList = new ArrayList<>();

    private Path linePath = new Path();    // 手指移动的路径

    private Paint linePaint;

    private Timer timer;

    public LockView(Context context) {
        this(context, null);
    }

    public LockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttrs(context, attrs);
        init();
    }


    private void readAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LockView);
        normalColor = typedArray.getColor(R.styleable.LockView_normalColor, DEFAULT_NORMAL_COLOR);
        moveColor = typedArray.getColor(R.styleable.LockView_moveColor, DEFAULT_MOVE_COLOR);
        errorColor = typedArray.getColor(R.styleable.LockView_errorColor, DEFAULT_ERROR_COLOR);
        rowCount = typedArray.getInteger(R.styleable.LockView_rowCount, DEFAULT_ROW_COUNT);
        typedArray.recycle();
    }

    private void init() {
        stateSparseArray = new SparseIntArray(rowCount * rowCount);
        points = new PointF[rowCount * rowCount];

        innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setStyle(Paint.Style.FILL);

        outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerCirclePaint.setStyle(Paint.Style.FILL);


        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeWidth(30);
        linePaint.setColor(moveColor);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        // 外圆半径 = 相邻外圆之间间距 = 2倍内圆半径
        radius = Math.min(w, h) / (2 * rowCount + rowCount - 1) * 1.0f;
        // 各个圆设置坐标点
        for (int i = 0; i < rowCount * rowCount; i++) {
            points[i] = new PointF(0, 0);
            points[i].set((i % rowCount * 3 + 1) * radius, (i / rowCount * 3 + 1) * radius);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(widthMeasureSpec);
        int height = getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int getSize(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return size;
        } else if (mode == MeasureSpec.AT_MOST) {
            return Math.min(size, dp2Px(600));
        }
        return dp2Px(600);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawLinePath(canvas);
    }

    private void drawCircle(Canvas canvas) {
        // 依次从索引 0 到索引 8，根据不同状态绘制圆点
        for (int index = 0; index < rowCount * rowCount; index++) {
            int state = stateSparseArray.get(index);
            switch (state) {
                case STATE_NORMAL:
                    innerCirclePaint.setColor(normalColor);
                    outerCirclePaint.setColor(normalColor & 0x66ffffff);
                    break;
                case STATE_MOVE:
                    innerCirclePaint.setColor(moveColor);
                    outerCirclePaint.setColor(moveColor & 0x66ffffff);
                    break;
                case STATE_ERROR:
                    innerCirclePaint.setColor(errorColor);
                    outerCirclePaint.setColor(errorColor & 0x66ffffff);
                    break;
            }
            canvas.drawCircle(points[index].x, points[index].y, radius, outerCirclePaint);
            canvas.drawCircle(points[index].x, points[index].y, radius / 2f, innerCirclePaint);
        }
    }

    /**
     * 绘制选中点之间相连的路径
     *
     * @param canvas
     */
    private void drawLinePath(Canvas canvas) {
        // 重置linePath
        linePath.reset();
        // 选中点个数大于 0 时，才绘制连接线段
        if (selectedList.size() > 0) {
            // 起点移动到按下点位置
            linePath.moveTo(selectedList.get(0).x, selectedList.get(0).y);
            for (int i = 1; i < selectedList.size(); i++) {
                linePath.lineTo(selectedList.get(i).x, selectedList.get(i).y);
            }
            // 手指抬起时，touchPoint设置为null,使得已经绘制游离的路径，消失掉，
            if (touchPoint != null) {
                linePath.lineTo(touchPoint.x, touchPoint.y);
            }
            canvas.drawPath(linePath, linePaint);
        }
    }

    private PointF touchPoint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reset();
            case MotionEvent.ACTION_MOVE:
                if (touchPoint == null) {
                    touchPoint = new PointF(event.getX(), event.getY());
                } else {
                    touchPoint.set(event.getX(), event.getY());
                }
                for (int i = 0; i < rowCount * rowCount; i++) {
                    if (getDistance(touchPoint, points[i]) < radius) {
                        stateSparseArray.put(i, STATE_MOVE);
                        if (!selectedList.contains(points[i])) {
                            selectedList.add(points[i]);
                        }
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (check()) {   // 正确图案
                    if (listener != null) {
                        listener.onComplete(true);
                    }
                    for (int i = 0; i < stateSparseArray.size(); i++) {
                        int index = stateSparseArray.keyAt(i);
                        stateSparseArray.put(index, STATE_MOVE);
                    }
                } else {     // 错误图案
                    for (int i = 0; i < stateSparseArray.size(); i++) {
                        int index = stateSparseArray.keyAt(i);
                        stateSparseArray.put(index, STATE_ERROR);
                    }
                    linePaint.setColor(0xeeff0000);
                    if (listener != null) {
                        listener.onComplete(false);
                    }
                }
                touchPoint = null;
                if (timer == null) {
                    timer = new Timer();
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        linePath.reset();
                        linePaint.setColor(0xee0000ff);
                        selectedList.clear();
                        stateSparseArray.clear();
                        postInvalidate();
                    }
                }, 1000);
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 清除绘制图案的条件，当触发 invalidate() 时将清空图案
     */
    private void reset() {
        touchPoint = null;
        linePath.reset();
        linePaint.setColor(0xee0000ff);
        selectedList.clear();
        stateSparseArray.clear();
    }


    public void onStop() {
        timer.cancel();
    }

    private boolean check() {
        if (selectedList.size() != standardPointsIndexList.size()) {
            return false;
        }
        for (int i = 0; i < standardPointsIndexList.size(); i++) {
            Integer index = standardPointsIndexList.get(i);
            if (points[index] != selectedList.get(i)) {
                return false;
            }
        }
        return true;
    }

    public void setStandard(List<Integer> pointsList) {
        if (pointsList == null) {
            throw new IllegalArgumentException("standard points index can't null");
        }
        if (pointsList.size() > rowCount * rowCount) {
            throw new IllegalArgumentException("standard points index list can't large to rowcount * columncount");
        }
        standardPointsIndexList = pointsList;
    }

    private OnDrawCompleteListener listener;

    public void setOnDrawCompleteListener(OnDrawCompleteListener listener) {
        this.listener = listener;
    }


    public interface OnDrawCompleteListener {
        void onComplete(boolean isSuccess);
    }


    private float getDistance(PointF centerPoint, PointF downPoint) {
        return (float) Math.sqrt(Math.pow(centerPoint.x - downPoint.x, 2) + Math.pow(centerPoint.y - downPoint.y, 2));

    }

    private int dp2Px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

}
