package com.caotu.duanzhi.view.viewpagertranformer;

import android.view.View;

public class ScalePageTransformer extends BasePageTransformer {
    private static final float MIN_SCALE = 0.9F;

    @Override
    public void touch2Left(View view, float position) {
        float scale = Math.max(MIN_SCALE, 1 - Math.abs(position));
        view.setScaleY(scale);
    }

    @Override
    public void touch2Right(View view, float position) {
        float scale = Math.max(MIN_SCALE, 1 - Math.abs(position));
        view.setScaleY(scale);
    }

    @Override
    public void other(View view, float position) {

    }
}
