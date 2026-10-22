// Vendored (Java port) of com.divyanshu.colorseekbar:colorseekbar:1.0.2 — the artifact was only on JCenter.
package com.divyanshu.colorseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ArrayRes;

import com.bumbumapps.vendored.R;

public class ColorSeekBar extends View {

    private static final float MIN_THUMB_RADIUS = 16f;
    private int[] colorSeeds = {Color.parseColor("#000000"), Color.parseColor("#FF5252"),
            Color.parseColor("#FFEB3B"), Color.parseColor("#00C853"), Color.parseColor("#00B0FF"),
            Color.parseColor("#D500F9"), Color.parseColor("#8D6E63")};
    private int canvasHeight;
    private final int barHeight;
    private final RectF rectf = new RectF();
    private final Paint rectPaint = new Paint();
    private final Paint thumbBorderPaint = new Paint();
    private final Paint thumbPaint = new Paint();
    private float thumbX = 24f;
    private final float thumbY;
    private float thumbRadius;
    private float thumbBorderRadius;
    private final float paddingStartPx = 30f;
    private final float paddingEndPx = 30f;
    private final float barCornerRadius;
    private final float oldThumbRadius;
    private final float oldThumbBorderRadius;
    private OnColorChangeListener colorChangeListener;

    public ColorSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ColorSeekBar);
        int colorsId = typedArray.getResourceId(R.styleable.ColorSeekBar_colorSeeds, 0);
        if (colorsId != 0) {
            colorSeeds = getColorsById(colorsId);
        }
        barCornerRadius = typedArray.getDimension(R.styleable.ColorSeekBar_cornerRadius, 8f);
        barHeight = (int) typedArray.getDimension(R.styleable.ColorSeekBar_barHeight, 20f);
        float thumbBorder = typedArray.getDimension(R.styleable.ColorSeekBar_thumbBorder, 4f);
        int thumbBorderColor = typedArray.getColor(R.styleable.ColorSeekBar_thumbBorderColor, Color.BLACK);
        typedArray.recycle();

        rectPaint.setAntiAlias(true);
        thumbBorderPaint.setAntiAlias(true);
        thumbBorderPaint.setColor(thumbBorderColor);
        thumbPaint.setAntiAlias(true);

        thumbRadius = Math.max(barHeight / 2f, MIN_THUMB_RADIUS);
        thumbBorderRadius = thumbRadius + thumbBorder;
        canvasHeight = (int) (thumbBorderRadius * 3);
        thumbY = canvasHeight / 2f;
        oldThumbRadius = thumbRadius;
        oldThumbBorderRadius = thumbBorderRadius;
    }

    private int[] getColorsById(@ArrayRes int id) {
        if (isInEditMode()) {
            String[] s = getContext().getResources().getStringArray(id);
            int[] colors = new int[s.length];
            for (int j = 0; j < s.length; j++) {
                colors[j] = Color.parseColor(s[j]);
            }
            return colors;
        }
        TypedArray typedArray = getContext().getResources().obtainTypedArray(id);
        int[] colors = new int[typedArray.length()];
        for (int j = 0; j < typedArray.length(); j++) {
            colors[j] = typedArray.getColor(j, Color.BLACK);
        }
        typedArray.recycle();
        return colors;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float barLeft = paddingStartPx;
        float barRight = getWidth() - paddingEndPx;
        float barTop = (canvasHeight / 2) - (barHeight / 2);
        float barBottom = (canvasHeight / 2) + (barHeight / 2);
        rectf.set(barLeft, barTop, barRight, barBottom);
        canvas.drawRoundRect(rectf, barCornerRadius, barCornerRadius, rectPaint);
        if (thumbX < barLeft) {
            thumbX = barLeft;
        } else if (thumbX > barRight) {
            thumbX = barRight;
        }
        thumbPaint.setColor(pickColor(thumbX, getWidth()));
        canvas.drawCircle(thumbX, thumbY, thumbBorderRadius, thumbBorderPaint);
        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint);
    }

    private int pickColor(float position, int canvasWidth) {
        float value = (position - paddingStartPx) / (canvasWidth - (paddingStartPx + paddingEndPx));
        if (value <= 0f) {
            return colorSeeds[0];
        } else if (value >= 1f) {
            return colorSeeds[colorSeeds.length - 1];
        }
        float colorPosition = value * (colorSeeds.length - 1);
        int i = (int) colorPosition;
        colorPosition -= i;
        int c0 = colorSeeds[i];
        int c1 = colorSeeds[i + 1];
        return Color.rgb(mix(Color.red(c0), Color.red(c1), colorPosition),
                mix(Color.green(c0), Color.green(c1), colorPosition),
                mix(Color.blue(c0), Color.blue(c1), colorPosition));
    }

    private static int mix(int start, int end, float position) {
        return start + Math.round(position * (end - start));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectPaint.setShader(new LinearGradient(0f, 0f, w, 0f, colorSeeds, null, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, canvasHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                thumbBorderRadius = oldThumbBorderRadius * 1.5f;
                thumbRadius = oldThumbRadius * 1.5f;
                break;
            case MotionEvent.ACTION_MOVE:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                thumbX = event.getX();
                invalidate();
                if (colorChangeListener != null) {
                    colorChangeListener.onColorChangeListener(getColor());
                }
                break;
            case MotionEvent.ACTION_UP:
                thumbBorderRadius = oldThumbBorderRadius;
                thumbRadius = oldThumbRadius;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    public int getColor() {
        return thumbPaint.getColor();
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.colorChangeListener = onColorChangeListener;
    }

    public interface OnColorChangeListener {
        void onColorChangeListener(int color);
    }
}
