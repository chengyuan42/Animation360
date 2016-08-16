package com.imooc.animation360.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.imooc.animation360.R;

/**
 *
 * Created by chengyuan on 16/8/12.
 */
public class FloatCircleView extends View {
    public int width = 150;
    public int height = 150;

    private Paint circlePaint;
    private Paint textPaint;

    private String text = "50%";
    private boolean isDrag = false;
    private Bitmap bitmap;

    public FloatCircleView(Context context) {
        super(context);
    }

    public FloatCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     *  确定该控件或子控件的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
        initPaints();
    }

    /**
     *  初始化画笔
     */
    private void initPaints() {
        // 画笔
        circlePaint = new Paint();
        circlePaint.setColor(Color.GRAY); // 画笔颜色
        circlePaint.setAntiAlias(true);

        // 文字
        textPaint = new Paint();
        textPaint.setTextSize(25);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.drag_img2);
        bitmap = Bitmap.createScaledBitmap(src, width, height, true);

    }

    /**
     *  绘制控件的内容
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if(isDrag){
            canvas.drawBitmap(bitmap, 0, 0, null);
        }else {
            canvas.drawCircle(width / 2, height / 2, width / 2, circlePaint);
            float textWidth = textPaint.measureText(text);
            float x = width / 2 - textWidth / 2;
            Paint.FontMetrics metrics = textPaint.getFontMetrics();
            float dy = -(metrics.descent + metrics.ascent) / 2;
            float y = dy + height / 2;
            canvas.drawText(text, x ,y, textPaint);
        }
    }


    public void setDragState(boolean b) {
        isDrag = b;
        invalidate();
    }
}
