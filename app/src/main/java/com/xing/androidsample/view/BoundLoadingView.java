package com.xing.androidsample.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.xing.androidsample.R;

/**
 * Created by Administrator on 2018/6/3.
 */

public class BoundLoadingView extends View {

    private static final int DEFAULT_CIRCLE_COLOR = 0xffff0000;

    private static final int DEFAULT_RECT_COLOR = 0xff00ff00;

    private static final int DEFAULT_TRIANGLE_COLOR = 0xff0000ff;

    private Paint paint;

    private int circleColor;

    private int rectColor;

    private int triangleColor;

    private float centerX;

    private float centerY;

    private float radius;

    private int mWidth;

    private int mHeight;
    private float pointY;
    private float fraction;
    private float degree;
    private Path path;
    private float shadowValue;
    private RectF shadowRect;


    public BoundLoadingView(Context context) {
        this(context, null);
    }

    public BoundLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttrs(context, attrs);
        init();
    }


    private void readAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BoundLoadingView);
        circleColor = typedArray.getColor(R.styleable.BoundLoadingView_circleColor, DEFAULT_CIRCLE_COLOR);
        rectColor = typedArray.getColor(R.styleable.BoundLoadingView_rectColor, DEFAULT_RECT_COLOR);
        triangleColor = typedArray.getColor(R.styleable.BoundLoadingView_triangleColor, DEFAULT_TRIANGLE_COLOR);
        typedArray.recycle();
    }


    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        path = new Path();

        shadowRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredSize(widthMeasureSpec, dp2Px(20));
        int height = getMeasuredSize(heightMeasureSpec, dp2Px(100));
        setMeasuredDimension(width, height);
    }


    private int getMeasuredSize(int measureSpec, int defaultSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return size;
        } else if (mode == MeasureSpec.AT_MOST) {
            return Math.min(size, defaultSize);
        } else {
            return defaultSize;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        centerX = w / 2f;
        centerY = 0;


        radius = w > dp2Px(20) ? dp2Px(10) : w;
        startAnimator();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        shadowRect.set(centerX - 2 * radius * shadowValue, mHeight - 12, centerX + 2 * radius * shadowValue, mHeight);
        paint.setColor(Color.BLACK);
        paint.setMaskFilter(new BlurMaskFilter(4, BlurMaskFilter.Blur.NORMAL));
        canvas.drawOval(shadowRect, paint);


        canvas.translate(centerX, pointY);


        canvas.rotate(degree, 0, 0);


        if (number % 3 == 0) {
            paint.setColor(rectColor);
            canvas.drawRect(-radius, -radius, radius, radius, paint);
        } else if (number % 3 == 1) {
            paint.setColor(circleColor);
            canvas.drawCircle(0, 0, radius, paint);
        } else if (number % 3 == 2) {
            path.reset();
            path.moveTo((float) (-Math.sqrt(3) / 2f * radius), radius / 2f);
            path.lineTo(0, -radius);
            path.lineTo((float) (Math.sqrt(3) / 2f * radius), radius / 2f);
            path.close();
            paint.setColor(triangleColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, paint);
        }
        canvas.restore();
    }

    private boolean isUp = true;

    private int number = 0;

    private AnimatorSet initAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(isUp ? (float) (mHeight - radius * Math.sqrt(2)) : (float) (radius * Math.sqrt(2)), isUp ? (float) (radius * Math.sqrt(2)) : (float) (mHeight - radius * Math.sqrt(2)));
//        valueAnimator = ValueAnimator.ofFloat(isUp ? 1f : 0, isUp ? 0 : 1f);
        valueAnimator.setDuration(600);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pointY = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator rotateAnimator = ValueAnimator.ofFloat(isUp ? 0 : 210, isUp ? 210 : 270);
        rotateAnimator.setDuration(600);
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) animation.getAnimatedValue();
            }
        });

        ValueAnimator shadowAnimator = ValueAnimator.ofFloat(isUp ? 0 : 1.0f, isUp ? 1.0f : 0);
        shadowAnimator.setDuration(600);
        shadowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        shadowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shadowValue = (float) animation.getAnimatedValue();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator, rotateAnimator, shadowAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isUp = !isUp;
                startAnimator();
                if (isUp) {
                    number++;
                }

            }
        });
        return animatorSet;
    }

    public void startAnimator() {
        AnimatorSet animatorSet = initAnimator();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        animatorSet.start();
    }


    private int dp2Px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }


}
