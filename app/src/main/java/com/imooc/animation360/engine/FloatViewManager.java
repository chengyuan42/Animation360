package com.imooc.animation360.engine;

import android.content.Context;
import android.graphics.PixelFormat;
import android.renderscript.Script;
import android.text.style.ParagraphStyle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.imooc.animation360.view.FloatCircleView;
import com.imooc.animation360.view.FloatMenuView;

import java.lang.reflect.Field;

/**
 * 浮窗管理者的创建(单例)
 *
 * 1 私有化构造行数
 * 2 创建静态的返回浮窗管理类的方法
 *
 * Created by chengyuan on 16/8/12.
 */
public class FloatViewManager {
    private Context context;
    private WindowManager wm; // 通过这个windowManager来操控浮窗体的显示和隐藏以及位置的改变

    private FloatCircleView circleView;
    private FloatMenuView menuView;
    private WindowManager.LayoutParams params;

    private View.OnTouchListener circleViewTouchListener = new View.OnTouchListener() {
        private float startX;
        private float startY;
        private float x0;
        private float y0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    x0 = event.getRawX();
                    y0 = event.getRawY();

                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getRawX();
                    float y = event.getRawY();
                    float dx = x - startX;
                    float dy = y - startY;
                    params.x += dx;
                    params.y += dy;
                    circleView.setDragState(true);
                    wm.updateViewLayout(circleView, params);
                    startX = x;
                    startY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    float endX = event.getRawX();
                    if(endX > getScreenWidth()/2){
                        params.x = getScreenWidth() - circleView.width;
                    }else {
                        params.x = 0;
                    }
                    circleView.setDragState(false);
                    wm.updateViewLayout(circleView, params);

                    if(Math.abs(endX - x0) > 6){
                        return true;
                    }else {
                        return false;
                    }
                default:
                    break;
            }
            return false;
        }
    };

    public int getScreenWidth(){
        return wm.getDefaultDisplay().getWidth();
    }
    public int getScreenHeight(){
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 状态栏的高度
     * @return
     */
    public int getStatusHeight (){
        try {
            Class<?>  c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(0);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private FloatViewManager(final Context context){
        this.context = context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        circleView = new FloatCircleView(context);

        circleView.setOnTouchListener(circleViewTouchListener);
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "onclick", Toast.LENGTH_SHORT).show();
                // 隐藏circleview 显示菜单栏
                wm.removeView(circleView);
                showFloatMenuView();
                menuView.startAnimation();
            }
        });

        menuView = new FloatMenuView(context);

    }

    private static FloatViewManager instance;

    public static FloatViewManager getInstance(Context context){
        if(instance == null){
            synchronized (FloatViewManager.class) {
                if (instance == null) {
                    instance = new FloatViewManager(context);
                }
            }
        }

        return instance;
    }

    /**
     * 展示浮窗小球到窗口上
     */
    public void showFloatCircleView(){
        if(params == null){
            params = new WindowManager.LayoutParams();
            params.width = circleView.width;
            params.height = circleView.height;
            params.gravity = Gravity.TOP|Gravity.LEFT;
            params.x = 0;
            params.y = 0;
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            params.format = PixelFormat.RGBA_8888;
        }

        wm.addView(circleView, params);
    }


    protected void showFloatMenuView() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = getScreenWidth();
        params.height = getScreenHeight() - getStatusHeight();
        params.gravity = Gravity.BOTTOM|Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format = PixelFormat.RGBA_8888;

        wm.addView(menuView, params);
    }

    public void hideFloatMenuView() {
        wm.removeView(menuView);
    }
}
