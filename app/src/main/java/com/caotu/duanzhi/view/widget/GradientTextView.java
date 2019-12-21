package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class GradientTextView extends AppCompatTextView {
    private LinearGradient mLinearGradient;
    private TextPaint mPaint;
    private Matrix mMatrix;

    //移动总量
    private float mTranslate;
    //每次重绘的偏移量
    private float offset = 20;
    //文字宽度
    private float textWith;
    //线性渲染线段长度
    private int gradientSize;

    public GradientTextView(Context context) {
        this(context, null);
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = getPaint();
        mMatrix = new Matrix();

        String text = getText().toString();

        //获取文字的宽度
        textWith = mPaint.measureText(text,0,text.length());

        //设置渐变的宽度，就按照3个字作为一个渐变长度
        gradientSize = (int) (textWith / text.length()*2);

        mLinearGradient = new LinearGradient(0, 0, gradientSize, 0, new int[]{ Color.parseColor("#FF8787"), Color.parseColor("#FF5A8E")}, null, Shader.TileMode.CLAMP);

        mPaint.setShader(mLinearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //将背景设置成黑色
//        canvas.drawColor(Color.BLACK);

        //这里有绘制文字的逻辑
        super.onDraw(canvas);

        mTranslate += offset;

        if (mTranslate >= textWith) {
            mTranslate -= offset;
            offset = -offset;
        }

        if(mTranslate <= 0){
            offset = -offset;
            mTranslate += offset;
        }

        //移动渐变
        mMatrix.setTranslate(mTranslate, 0);
        mLinearGradient.setLocalMatrix(mMatrix);

        //每次重绘延迟时间是100毫秒
        postInvalidateDelayed(100);
    }
}
