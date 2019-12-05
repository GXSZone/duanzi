package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.caotu.duanzhi.utils.DevicesUtils;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;

public class MyPagerIndicator extends View implements IPagerIndicator {


    // 控制动画
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Paint mPaint;
    private List<PositionData> mPositionDataList;
    private RectF rectF;
    private int tenInt;
    private int sevenInt;


    public MyPagerIndicator(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FE6785"));
        rectF = new RectF();
        tenInt = DevicesUtils.dp2px(10);
        sevenInt = DevicesUtils.dp2px(7);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(rectF, 5, 5, mPaint);
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
        //默认有间距,需要稍微调整下
        leftX = current.mContentRight - 10;
        nextLeftX = next.mContentRight - 10;

        rectF.left = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
        rectF.top = current.mContentBottom - tenInt;
        rectF.right = rectF.left + sevenInt;
        rectF.bottom = rectF.top + tenInt;
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
