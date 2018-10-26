package com.luck.picture.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.luck.picture.lib.R;


public class RoundCornerImageView extends android.support.v7.widget.AppCompatImageView {

    private boolean all_corners;
    private boolean left_bottom_corners;
    private boolean left_top_corners;
    private boolean right_bottom_corners;
    private boolean right_top_corners;
    private float round_corner_size;

    public RoundCornerImageView(Context context) {
        this(context, null);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    Context context;

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView);
        all_corners = typedArray.getBoolean(R.styleable.RoundCornerImageView_all_corners, false);
        left_bottom_corners = typedArray.getBoolean(R.styleable.RoundCornerImageView_left_bottom_corners, false);
        left_top_corners = typedArray.getBoolean(R.styleable.RoundCornerImageView_left_top_corners, false);
        right_bottom_corners = typedArray.getBoolean(R.styleable.RoundCornerImageView_right_bottom_corners, false);
        right_top_corners = typedArray.getBoolean(R.styleable.RoundCornerImageView_right_top_corners, false);
        round_corner_size = typedArray.getDimension(R.styleable.RoundCornerImageView_round_corner_size, 0);

        typedArray.recycle();

    }

    public void setAllCorners(boolean all_corners) {
        this.all_corners = all_corners;
    }

    public void setRoundCornerSize(float round_corner_size) {
        this.round_corner_size = round_corner_size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        int i = (int) round_corner_size;
        int leftTopX = 0;
        int leftTopY = 0;
        int leftBottomX = 0;
        int leftBottomY = 0;
        int rightBottomX = 0;
        int rightBottomY = 0;
        int rightTopX = 0;
        int rightTopY = 0;
        if (all_corners) {
            left_bottom_corners = true;
            left_top_corners = true;
            right_bottom_corners = true;
            right_top_corners = true;
        }

        if (left_bottom_corners) {
            leftBottomX = leftBottomY = i;
        }
        if (left_top_corners) {
            leftTopX = leftTopY = i;
        }
        if (right_bottom_corners) {
            rightBottomX = rightBottomY = i;
        }
        if (right_top_corners) {
            rightTopX = rightTopY = i;
        }

        float radii[] = {leftTopX, leftTopY, rightTopX, rightTopY, rightBottomX, rightBottomY, leftBottomX, leftBottomY};
        clipPath.addRoundRect(new RectF(0, 0, w, h), radii, Path.Direction.CCW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
