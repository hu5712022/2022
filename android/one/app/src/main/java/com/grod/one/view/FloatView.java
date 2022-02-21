package com.grod.one.view;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;

//之前用于 nxrobot 音乐列表播放 效果
public class FloatView extends View {
    /**
     * 0 停止 1 开发  用于优化性能
     */
    int statusAnim = 0;

    int maxWidth;
    int maxHeight;
    float centerX;
    Paint paint;
    Path path;
    float strokeWidth = 2;
    long maxTime = 1000;

    float swingHeight;// 用于改变 峰值  一般不大于 maxHeight
    float swingWidth = 100;//  用于改变胖瘦  越小越瘦 不能小于5 不然画不出来

    ValueAnimator anim;

    public FloatView(Context context) {
        this(context, null, 0);
    }

    public FloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public boolean isAnim() {
        return statusAnim == 1;
    }

    private void init() {
        strokeWidth = 1;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        path = new Path();


        anim = ValueAnimator.ofFloat(0, 1, 0);
        anim.setDuration(maxTime);//一圈1.2S
        anim.setInterpolator(new DecelerateInterpolator());//减速差值器
        anim.setRepeatCount(-1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (statusAnim == 1) {

                    float value = (float) animation.getAnimatedValue();
                    swingHeight = (maxHeight - strokeWidth) * value;
                    swingWidth = 150 * (1 - value) + 20;
                    path.reset();
                    postInvalidate();//通知UI绘制
                }
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (statusAnim == 1) {
                    float i = new Random().nextFloat();
                    ((ValueAnimator) animation).setFloatValues(0, i, 0);
                    ((ValueAnimator) animation).setDuration((long) (maxTime * i));
                }
            }
        });
    }

    public void setLineColor(int res) {
        paint.setColor(getResources().getColor(res));
    }

    public void startAnim() {
        if (anim.isStarted()) {
            return;
        }
        anim.start();
        statusAnim = 1;
    }

    public void stopAnim() {
        statusAnim = 0;
        if (anim.isStarted()) {
            anim.end();
            swingHeight = 0;
            path.reset();
            postInvalidate();//通知UI绘制
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxWidth = getMeasuredWidth();
        maxHeight = getMeasuredHeight();
        centerX = maxWidth / 4 * 3;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //path.reset();

        for (float x = 0; x < maxWidth; x += 5) {
            float y = maxHeight - strokeWidth - camel((x - centerX) / swingWidth) * swingHeight;
            if (x == 0) {
                //设置path的起点
                path.moveTo(0, y);
            } else {
                //连线
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, paint);

    }

    /**
     * 峰驼 函数
     *
     * @param x x值 传0返回最大值
     * @return y 值 0 到 1 之间
     */
    public float camel(float x) {
        // a*b^(-x^2)    a 控制高度   b 控制胖瘦  默认  a=1  b = 10
        float a = 1;
        float b = 10;
        return (float) (a * Math.pow(b, -x * x));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
