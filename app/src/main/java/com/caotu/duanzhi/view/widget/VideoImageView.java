package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.caotu.duanzhi.R;

/**
 * 针对评论里的视频类型显示,只是个图片,不用视频播放器减少资源损耗,另外还可以减少布局嵌套
 */
public class VideoImageView extends AppCompatImageView {

    private Paint paint;
    private int bmpWidth;
    private int bmpHeight;
    private Bitmap bmp;

    public VideoImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public VideoImageView(Context context) {
        super(context);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.preview_play);
        bmpWidth = bmp.getWidth();
        bmpHeight = bmp.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = width / 2 - bmpWidth / 2;
        int top = height / 2 - bmpHeight / 2;
        canvas.drawBitmap(bmp, left, top, paint);
    }
}
