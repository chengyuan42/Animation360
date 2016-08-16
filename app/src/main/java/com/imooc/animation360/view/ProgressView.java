package com.imooc.animation360.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * 加速球的绘制
 * Created by chengyuan on 16/8/13.
 */
public class ProgressView extends View {
    private int width = 200;
    private int height = 200;

    private Paint circlePaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private  Path path = new Path();
    private int progress = 50;
    private int max = 100;
    private int currentProgress = 0;
    private int count = 50;
    private boolean isSingleTap = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private GestureDetector detector;


    public ProgressView(Context context) {
        super(context);
        init();
    }

    private void init() {

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.argb(0xff,0x3a,0x8c,0x6c));

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.argb(0xff, 0x4e, 0xc9, 0x63));
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25);

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        bitmapCanvas = new Canvas(bitmap);

        detector = new GestureDetector(new MyGestureDetectorListener());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return detector.onTouchEvent(motionEvent);
            }
        });

        setClickable(true);
    }

    class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
//            Toast.makeText(getContext(), "双击", Toast.LENGTH_SHORT).show();
            startDoubleTapAnimation();
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Toast.makeText(getContext(), "单击", Toast.LENGTH_SHORT).show();
            isSingleTap = true;
            currentProgress = progress;
            startSingleTapAnimation();
            return super.onSingleTapConfirmed(e);
        }

    }

    private void startSingleTapAnimation() {
        handler.postDelayed(singleTapRunnable, 200); //每200ms刷新
    }
    private SingleTapRunnable singleTapRunnable = new SingleTapRunnable();

    class SingleTapRunnable implements Runnable {
        @Override
        public void run() {
            count--;
            if (count >= 0){
                invalidate();
                handler.postDelayed(singleTapRunnable, 200);
            }else {
                handler.removeCallbacks(singleTapRunnable); //取消进程
                count = 50;
            }
        }
    }

    /**
     *  当前进入从0递加到指定进度,每次调用invalidate方法重新绘制控件,并且将贝塞尔曲线的弯曲度逐渐减小
     */
    private void startDoubleTapAnimation() {
        handler.postDelayed(dbTapRunnable, 50);
    }

    private DoubleTapRunnable dbTapRunnable = new DoubleTapRunnable();

    class DoubleTapRunnable implements Runnable{
        @Override
        public void run() {
            currentProgress++;
            if(currentProgress <= progress){
                invalidate(); // 重新绘制控件
                handler.postDelayed(dbTapRunnable, 50);
            }else {
                handler.removeCallbacks(dbTapRunnable);
                currentProgress = 0;
            }
        }
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bitmapCanvas.drawCircle(width / 2, height / 2, width / 2, circlePaint);
        path.reset();
        float y = (1- (float) currentProgress / max) * height;
        path.moveTo(width, y);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, y);

        if(!isSingleTap){
            float d = (1 - (float) currentProgress / progress) *10;
            for (int i = 0; i < 5; i++){
                path.rQuadTo(10, -d, 20, 0);
                path.rQuadTo(10, d, 20, 0);
            }
        } else {
            float d = (float) count / 50 * 10;
            if (count % 2 == 0) {
                for (int i = 0; i < 5; i++) {
                    path.rQuadTo(20, -d, 40, 0);
                    path.rQuadTo(20, d, 40, 0);
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    path.rQuadTo(20, d, 40, 0);
                    path.rQuadTo(20, -d, 40, 0);
                }
            }
        }

        path.close();
        bitmapCanvas.drawPath(path, progressPaint);

        String text = (int) (((float) currentProgress / max) * 100) + "%";
        Paint.FontMetrics metrics =  textPaint.getFontMetrics();
        float textWidth = textPaint.measureText(text);
        float baseline = height / 2 - (metrics.ascent + metrics.descent) / 2;
        bitmapCanvas.drawText(text, width / 2 - textWidth / 2, baseline, textPaint);

        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
