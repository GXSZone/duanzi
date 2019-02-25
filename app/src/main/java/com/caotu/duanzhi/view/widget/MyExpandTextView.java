package com.caotu.duanzhi.view.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.CustomMovementMethod;


public class MyExpandTextView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = MyExpandTextView.class.getSimpleName();

    /* The default number of lines */
    private static final int MAX_COLLAPSED_LINES = 8;

    /* The default animation duration */
    private static final int DEFAULT_ANIM_DURATION = 300;

    /* The default alpha value when the animation starts */
    private static final float DEFAULT_ANIM_ALPHA_START = 0.7f;

    protected TextView mTv;

    protected TextView mButton; // Button to expand/collapse

    private boolean mRelayout;

    private boolean mCollapsed = true; // Show short version as default.

    private int mCollapsedHeight;

    private int mTextHeightWithMaxLines;

    private int mMaxCollapsedLines;

    private int mMarginBetweenTxtAndBottom;

    private boolean mAnimating;


//    private SparseBooleanArray mCollapsedStatus;
//    private int mPosition;

    public MyExpandTextView(Context context) {
        this(context, null);
    }

    public MyExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MyExpandTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public TextView getTextView() {
        return mTv;
    }

    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.HORIZONTAL == orientation) {
            throw new IllegalArgumentException("ExpandableTextView only supports Vertical Orientation.");
        }
        super.setOrientation(orientation);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.expand_collapse) {
            if (mButton.getVisibility() != View.VISIBLE) {
                return;
            }

            mCollapsed = !mCollapsed;
            if (!mCollapsed) {
                HelperForStartActivity.dealRequestContent(mContentId);
            }
            mButton.setText(mCollapsed ? "全文" : "收起");

            mAnimating = true;
            Animation animation;
            if (mCollapsed) {
                animation = new ExpandCollapseAnimation(this, getHeight(), mCollapsedHeight);
            } else {
                animation = new ExpandCollapseAnimation(this, getHeight(), getHeight() +
                        mTextHeightWithMaxLines - mTv.getHeight());
            }

            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
//                applyAlphaAnimation(mTv, mAnimAlphaStart);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // clear animation here to avoid repeated applyTransformation() calls
                    clearAnimation();
                    // clear the animation flag
                    mAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            clearAnimation();
            startAnimation(animation);
        } else if (view.getId() == R.id.expandable_text) {
            if (textListener != null) {
                textListener.clickText(view);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // while an animation is in progress, intercept all the touch events to children to
        // prevent extra clicks during the animation
        return mAnimating;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // If no change, measure and return
        if (!mRelayout || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;

        // Setup with optimistic case
        // i.e. Everything fits. No button needed
        mButton.setVisibility(View.GONE);
        mTv.setMaxLines(Integer.MAX_VALUE);

        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // If the text fits in collapsed mode, we are done.
        if (mTv.getLineCount() <= mMaxCollapsedLines) {
            return;
        }

        // Saves the text height w/ max lines
        mTextHeightWithMaxLines = getRealTextViewHeight(mTv);

        // Doesn't fit in collapsed mode. Collapse text view as needed. Show
        // button.
        if (mCollapsed) {
            mTv.setMaxLines(mMaxCollapsedLines);
        }
        mButton.setVisibility(View.VISIBLE);

        // Re-measure with new setup
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mCollapsed) {
            // Gets the margin between the TextView's bottom and the ViewGroup's bottom
            mTv.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTxtAndBottom = getHeight() - mTv.getHeight();
                }
            });
            // Saves the collapsed height of this ViewGroup
            mCollapsedHeight = getMeasuredHeight();
        }
    }

    public void setText(@Nullable CharSequence text) {
        clearAnimation();
        mRelayout = true;
        mTv.setText(text);
        mTv.setMovementMethod(CustomMovementMethod.getInstance());
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    public String mContentId;

    public void clickCount(String contentId) {
        mContentId = contentId;
    }

    public void setText(@Nullable CharSequence text, @NonNull SparseBooleanArray collapsedStatus, int position) {
//        mCollapsedStatus = collapsedStatus;
//        mPosition = position;
        boolean isCollapsed = collapsedStatus.get(position, true);
        clearAnimation();
        mCollapsed = isCollapsed;
        mButton.setText(mCollapsed ? "全文" : "收起");
        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
    }


    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextView_maxCollapsedLines, MAX_COLLAPSED_LINES);
        typedArray.recycle();
        // enforces vertical orientation
        setOrientation(LinearLayout.VERTICAL);
        // default visibility is gone
        setVisibility(GONE);
    }

    private void findViews() {
        mTv = (TextView) findViewById(R.id.expandable_text);
        mTv.setOnClickListener(this);
        mButton = (TextView) findViewById(R.id.expand_collapse);
        mButton.setText(mCollapsed ? "全文" : "收起");
        mButton.setOnClickListener(this);
    }


    private static int getRealTextViewHeight(@NonNull TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }

    class ExpandCollapseAnimation extends Animation {
        private final View mTargetView;
        private final int mStartHeight;
        private final int mEndHeight;

        public ExpandCollapseAnimation(View view, int startHeight, int endHeight) {
            mTargetView = view;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(DEFAULT_ANIM_DURATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final int newHeight = (int) ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mTv.setMaxHeight(newHeight - mMarginBetweenTxtAndBottom);
//            if (Float.compare(mAnimAlphaStart, 1.0f) != 0) {
//                applyAlphaAnimation(mTv, mAnimAlphaStart + interpolatedTime * (1.0f - mAnimAlphaStart));
//            }
            mTargetView.getLayoutParams().height = newHeight;
            mTargetView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public ClickTextListener textListener;

    public void setTextListener(@Nullable ClickTextListener listener) {
        textListener = listener;
    }

    public interface ClickTextListener {
        void clickText(View textView);
    }
//    public interface OnExpandStateChangeListener {
//        /**
//         * Called when the expand/collapse animation has been finished
//         *
//         * @param textView - TextView being expanded/collapsed
//         * @param isExpanded - true if the TextView has been expanded
//         */
//        void onExpandStateChanged(TextView textView, boolean isExpanded);
//    }


}
