package com.liuchuanzheng.chart.aa;

/**
 * @author 刘传政
 * @date 2019-11-15 10:13
 * QQ:1052374416
 * 电话:18501231486
 * 作用:
 * 注意事项:
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;

/**
 * 刻度尺
 * Created by Administrator on 2016/9/14.
 */
public class ScaleView extends View {

    private Paint mLinePaint;
    private Paint mTextPaint;
    private Paint mRulerPaint;
    private float progrees = 10;
    private int max = 101;
    private int min = 0;
    private boolean isCanMove;

    public ScaleView(Context context) {
        super(context);
        init();
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.CYAN);
        mLinePaint.setAntiAlias(true);//抗锯齿
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(4);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.CYAN);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setTextSize(48);
        //
        mRulerPaint = new Paint();
        mRulerPaint.setAntiAlias(true);
        mRulerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mRulerPaint.setColor(Color.RED);
        mRulerPaint.setStrokeWidth(4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(setMeasureWidth(widthMeasureSpec), setMeasureHeight(heightMeasureSpec));
    }

    private int setMeasureHeight(int spec) {
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);
        int result = Integer.MAX_VALUE;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                size = Math.min(result, size);
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
                size = result;
                break;
        }
        return size;
    }

    private int setMeasureWidth(int spec) {
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);
        int result = Integer.MAX_VALUE;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                size = Math.min(result, size);
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
                size = result;
                break;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        for (int i = min; i < max; i++) {
            if (i % 10 == 0) {
                canvas.drawLine(10, 0, 10, 72, mLinePaint);
                String text = i / 10 + "";
                Rect rect = new Rect();
                float txtWidth = mTextPaint.measureText(text);
                mTextPaint.getTextBounds(text, 0, text.length(), rect);
                canvas.drawText(text, 10 - txtWidth / 2, 72 + rect.height() + 10, mTextPaint);
            } else if (i % 5 == 0) {
                canvas.drawLine(10, 0, 10, 64, mLinePaint);
            } else {
                canvas.drawLine(10, 0, 10, 48, mLinePaint);
            }
            canvas.translate(18, 0);
        }
        canvas.restore();
        canvas.drawLine(progrees, 0, progrees, 160, mRulerPaint);
        canvas.drawCircle(progrees, 170, 10, mRulerPaint);
        BigDecimal bd = new BigDecimal((progrees - 18) / 180);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        mTextPaint.setTextSize(48);
        canvas.drawText(bd.floatValue() + "cm", 500, 400, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCanMove = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isCanMove) {
                    return false;
                }
                float x = event.getX() - 10;
                progrees = x;
                invalidate();
                break;
        }
        return true;
    }
}

