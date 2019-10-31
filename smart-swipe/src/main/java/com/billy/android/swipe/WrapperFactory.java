package com.billy.android.swipe;

import android.content.Context;

/**
 * @author billy.qi
 */
public class WrapperFactory implements SmartSwipe.IWrapperFactory {
    @Override
    public SmartSwipeWrapper createWrapper(Context context) {
        return new SmartSwipeWrapperX(context);
    }
}
