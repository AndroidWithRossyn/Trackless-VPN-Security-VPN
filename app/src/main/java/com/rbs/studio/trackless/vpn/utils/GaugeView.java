package com.rbs.studio.trackless.vpn.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class GaugeView extends View {
    private float strokeWidth;
    private int backgroundColor;
    private int fillColor;
    private int startAngle;
    private int angles;
    private int maxValue;
    private int value = 0;
    private Paint paint = null;
    private RectF rect = null;


//    public GaugeView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        final int[] GaugeView = {0x7f010000, 0x7f010001, 0x7f010002, 0x7f010003,0x7f010004 ,0x7f010005 };
//        setStrokeWidth(obtainStyledAttributes.getDimension(5, 10.0f));
//        setBackgroundColor(obtainStyledAttributes.getColor(1, -3355444));
//        setFillColor(obtainStyledAttributes.getColor(2, -1));
//        setStartAngle(obtainStyledAttributes.getInt(4, 135));
//        setAngles(obtainStyledAttributes.getInt(0, 270));
//        setMaxValue(obtainStyledAttributes.getInt(3, 1000));
//


    public GaugeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        final int[] GaugeView = {0x7f010000, 0x7f010001, 0x7f010002, 0x7f010003, 0x7f010004, 0x7f010005};

        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, GaugeView, 0, 0);
        setStrokeWidth(obtainStyledAttributes.getDimension(5, 10.0f));
        setBackgroundColor(obtainStyledAttributes.getColor(1, -3355444));
        setFillColor(obtainStyledAttributes.getColor(2, -1));
        setStartAngle(obtainStyledAttributes.getInt(4, 135));
        setAngles(obtainStyledAttributes.getInt(0, 270));
        setMaxValue(obtainStyledAttributes.getInt(3, 1000));
    }


    public GaugeView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = getWidth() < getHeight() ? getWidth() : getHeight();
        float w = size - (2 * strokeWidth);
        float h = size - (2 * strokeWidth);
        float radius = (w < h ? w / 2 : h / 2);
        if (rect == null) rect = new RectF();
        rect.set((getWidth() - (2 * strokeWidth)) / 2 - radius + strokeWidth, (getHeight() - (2 * strokeWidth)) / 2 - radius + strokeWidth, (getWidth() - (2 * strokeWidth)) / 2 - radius + strokeWidth + w, (getHeight() - (2 * strokeWidth)) / 2 - radius + strokeWidth + h);
        if (paint == null) paint = new Paint();
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(backgroundColor);
        canvas.drawArc(rect, startAngle, angles, false, paint);
        paint.setColor(fillColor);
        canvas.drawArc(rect, startAngle, (float) ((startAngle + value * ((double) Math.abs(angles) / maxValue)) - startAngle), false, paint);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
        invalidate();
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
        invalidate();
    }

    public int getAngles() {
        return angles;
    }

    public void setAngles(int angles) {
        this.angles = angles;
        invalidate();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
    }

    public static final class styleable {
        public static final int GaugeView_gauge_angles = 0x00000000;
        public static final int GaugeView_gauge_backgroundColor = 0x00000001;
        public static final int GaugeView_gauge_fillColor = 0x00000002;
        public static final int GaugeView_gauge_maxValue = 0x00000003;
        public static final int GaugeView_gauge_startAngle = 0x00000004;
        public static final int GaugeView_gauge_strokeWidth = 0x00000005;

        private styleable() {
        }
    }

    /* loaded from: classes.dex */
    public static final class attr {
        public static final int gauge_angles = 0x7f010000;
        public static final int gauge_backgroundColor = 0x7f010001;
        public static final int gauge_fillColor = 0x7f010002;
        public static final int gauge_maxValue = 0x7f010003;
        public static final int gauge_startAngle = 0x7f010004;
        public static final int gauge_strokeWidth = 0x7f010005;

        private attr() {
        }
    }

}