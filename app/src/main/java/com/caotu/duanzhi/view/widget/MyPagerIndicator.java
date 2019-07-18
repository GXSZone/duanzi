package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.caotu.duanzhi.R;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;

public class MyPagerIndicator extends View implements IPagerIndicator {


    // 控制动画
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Paint mPaint;
    private List<PositionData> mPositionDataList;
    private Bitmap bitmap;


    public MyPagerIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nav_bg);
    }

    float left;
    float top;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, left, top, mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);

        float leftX;
        float nextLeftX;

        leftX = current.mLeft + (current.width()) / 2.0f - bitmap.getWidth() / 2.0f;
        nextLeftX = next.mLeft + (next.width()) / 2.0f- bitmap.getWidth() / 2.0f;

        left = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
        top = getHeight() / 2.0f - bitmap.getHeight() / 2.0f;

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPositionDataProvide(List<PositionData> dataList) {
        mPositionDataList = dataList;
    }

}
