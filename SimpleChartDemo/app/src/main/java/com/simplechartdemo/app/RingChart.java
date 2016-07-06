package com.simplechartdemo.app;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RingChart extends View {
    private static final String TAG = RingChart.class.getSimpleName();

    private RectF mPieBounds = new RectF();
    private Paint arcPaint;
    private Paint borderPaint;
    private Paint directLinePaint;

    private RectF ringBound;
    private RectF outBorderBound;
    private RectF innerBouderBound;

    private Paint titlePaint;
    private float titleWidth;
    private String title;

    private Paint subTitlePaint;
    private float subTitleWidth;
    private String subTitle;

    float radius;
    float labelTextSize;
    private Paint labelPaint;
    private List<DataItem> dataItems;

    private float totalValue;
    private ItemDrawerHelper itemDrawerHelper;

    private static final float CHART_MARGIN = 80;
    private static final float MARGIN_BETWEEN_TITLES = 90;
    private static final float RING_WIDTH = 90;
    private static final float EXTENT_LINE_LENGTH = RING_WIDTH;

    private static final float BORDER_WIDTH = 4;
    private static final float DIRECTION_LINE_WIDTH = 5;
    private static final float DIRECTION_LINE_OUTTER_LENGTH = 90;

    private static final int LABEL_ALIGNMENT_LEFT = 0;
    private static final int LABEL_ALIGNMENT_RIGHT = 1;

    private static final int LABEL_LINE_MARGIN = 15;

    public RingChart(Context context) {
        super(context);
        init();
    }

    public RingChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerToSW(this);
        dataItems = new ArrayList<DataItem>();
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(0xff000000);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_WIDTH);

        directLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        directLinePaint.setColor(0xff000000);
        directLinePaint.setStyle(Paint.Style.STROKE);
        directLinePaint.setStrokeWidth(DIRECTION_LINE_WIDTH);

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setColor(0xffff0000);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(RING_WIDTH);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(0xff000000);
        labelTextSize = getResources().getDimension(R.dimen.ring_chart_label_text_size);
        labelPaint.setTextSize(labelTextSize);

        float titleHeight = getResources().getDimension(R.dimen.ring_chart_title_size);
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(0xff000000);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextSize(titleHeight);
        setTitle("OS");

        float subTitleHeight = getResources().getDimension(R.dimen.ring_chart_subtitle_size);
        subTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        subTitlePaint.setColor(0xff000000);
        subTitlePaint.setTextSize(subTitleHeight);

        setSubTitle("SubTitle");

        itemDrawerHelper = new ItemDrawerHelper();
    }

    private void setTitle(String title) {
        this.title = title;
        this.titleWidth = titlePaint.measureText(this.title);

        onDataChanged();
    }

    private void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        this.subTitleWidth = subTitlePaint.measureText(this.subTitle);

        onDataChanged();
    }

    public void addItem(String label, float value, int color) {
        DataItem item = new DataItem(label, value, color);
        this.dataItems.add(item);
        totalValue += value;

        reArrangeDataPercentage();

        onDataChanged();
    }

    private void reArrangeDataPercentage() {
        if (dataItems.size() == 1) {
            DataItem item = dataItems.get(0);
            item.startDegree = 0;
            item.sweepDegree = 360;

            return;
        }

        int i;
        int startDegree = 210;
        int totalSweepDegree = 0;

        for (i = 0; i < dataItems.size(); i++) {
            DataItem item = dataItems.get(i);
            item.startDegree = startDegree;
            item.sweepDegree = (int) (360 * (item.value / totalValue));

            if (i == dataItems.size() - 1) {
                item.sweepDegree = 360 - totalSweepDegree;
            } else {
                totalSweepDegree += item.sweepDegree;
            }
            startDegree += item.sweepDegree;
        }

    }

    public void clearData() {
        totalValue = 0f;
        dataItems.clear();

        onDataChanged();
    }

    private void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.e(TAG, "onMeasure");
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

//        Log.e(TAG, "onSizeChanged");
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        // Account for the label
//        if (mShowText) xpad += mTextWidth;

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the pie.
        float diameter = Math.min(ww, hh);

        mPieBounds = new RectF(
                0,
                0,
                ww,
                hh);
        mPieBounds.offsetTo(getPaddingLeft(), getPaddingTop());

        calcSizeForDraw();
        onDataChanged();
    }

    private void calcSizeForDraw() {
        float diameter = Math.min(mPieBounds.width(), mPieBounds.height());
        diameter -= CHART_MARGIN * 2;
        radius = diameter / 2;

        ringBound = new RectF(mPieBounds.centerX() - radius, mPieBounds.centerY() - radius
                , mPieBounds.centerX() + radius, mPieBounds.centerY() + radius);

        float widthDelta = RING_WIDTH / 2;

        outBorderBound = new RectF(ringBound.left - widthDelta,
                ringBound.top - widthDelta,
                ringBound.right + widthDelta,
                ringBound.bottom + widthDelta);

        innerBouderBound = new RectF(ringBound.left + widthDelta,
                ringBound.top + widthDelta,
                ringBound.right - widthDelta,
                ringBound.bottom - widthDelta);
    }

    private void onDataChanged() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.e(TAG, "onDraw");

        float tX = ringBound.centerX() - titleWidth / 2;
        float tY = ringBound.centerY();
        canvas.drawText(title, tX, tY, titlePaint);

        tX = ringBound.centerX() - subTitleWidth / 2;
        tY += MARGIN_BETWEEN_TITLES;

        canvas.drawText(subTitle, tX, tY, subTitlePaint);

        for(DataItem item :dataItems){
            itemDrawerHelper.setMisc(canvas, item);
            itemDrawerHelper.drawArc();
        }

        drawBorder(canvas);

        for(DataItem item :dataItems){
            itemDrawerHelper.setMisc(canvas, item);
            itemDrawerHelper.doDraw();
        }

    }

    private void drawBorder(Canvas canvas) {
        canvas.drawOval(outBorderBound, borderPaint);
        canvas.drawOval(innerBouderBound, borderPaint);
    }

    private class ItemDrawerHelper {
        Canvas canvas;
        DataItem item;

        public void setMisc(Canvas canvas, DataItem item) {
            this.canvas = canvas;
            this.item = item;
        }

        public void drawArc(){
            arcPaint.setColor(item.color);
//            arcPaint.setStrokeWidth(RING_WIDTH-2);
            canvas.drawArc(ringBound,item.startDegree, item.sweepDegree, false,arcPaint);

        }

        public void doDraw() {
            int degree = item.startDegree + item.sweepDegree / 2;

            double cornerRadius = Math.toRadians(degree);
            float sin = (float) Math.sin(cornerRadius);
            float cos = (float) Math.cos(cornerRadius);
            float startY = sin * radius + ringBound.centerY();
            float startX = cos * radius + ringBound.centerX();

            PointF startP = new PointF(startX, startY);
            PointF endP = findConnectLineTurnPoint(sin, cos, startP);
            canvas.drawLine(startP.x, startP.y, endP.x, endP.y, directLinePaint);

            PointF outerLineEndP = findHorizonDirectLineEndP(endP);
            int txtAlignment = outerLineEndP.x < ringBound.centerX() ? LABEL_ALIGNMENT_LEFT : LABEL_ALIGNMENT_RIGHT;
            float outerStartx = (txtAlignment == LABEL_ALIGNMENT_LEFT) ? endP.x + 1 : endP.x - 1;
            canvas.drawLine(outerStartx, endP.y, outerLineEndP.x, outerLineEndP.y, directLinePaint);

            PointF txtStartP = findLabelTextStartP(outerLineEndP, txtAlignment);
            canvas.drawText(item.label, txtStartP.x, txtStartP.y, labelPaint);
        }

        private PointF findLabelTextStartP(PointF lineEndP, int labelAlignment) {
            float tWidth = item.labelWidth;
            float txtOff = (labelAlignment == LABEL_ALIGNMENT_LEFT) ? -(LABEL_LINE_MARGIN + tWidth) : LABEL_LINE_MARGIN;
            PointF startP = new PointF();
            startP.x = lineEndP.x + txtOff;
            startP.y = lineEndP.y + labelTextSize / 2;

            return startP;
        }

        private PointF findHorizonDirectLineEndP(PointF startP) {
            PointF endP = new PointF();
            endP.y = startP.y;

            if (startP.x < ringBound.centerX()) {
                // TEXT LEFT
                endP.x = ringBound.left - DIRECTION_LINE_OUTTER_LENGTH;
            } else {
                // TEXT RIGHT
                endP.x = ringBound.right + DIRECTION_LINE_OUTTER_LENGTH;
            }

            return endP;
        }

        private PointF findConnectLineTurnPoint(float sin, float cos, PointF startP) {
            PointF p = new PointF();
            p.set(startP.x + cos * EXTENT_LINE_LENGTH, startP.y + sin * EXTENT_LINE_LENGTH);

            return p;
        }
    }

    private class DataItem {
        String label;
        float value;
        int color;
        float labelWidth;
        int startDegree;
        int sweepDegree;

        public DataItem(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
            this.labelWidth = labelPaint.measureText(this.label);
        }

    }

}