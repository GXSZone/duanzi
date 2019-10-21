package com.caotu.duanzhi.advertisement;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.caotu.duanzhi.utils.DevicesUtils;


public class RoundFrameLayout extends FrameLayout {
    public RoundFrameLayout(@NonNull Context context) {
        super(context);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float dimension = DevicesUtils.dp2px(5);
        Path path = new Path();
        RectF rectF = new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        path.addRoundRect(rectF, dimension, dimension, Path.Direction.CW);
        // 先对canvas进行裁剪
        canvas.clipPath(path, Region.Op.INTERSECT);

        super.dispatchDraw(canvas);
    }
}
