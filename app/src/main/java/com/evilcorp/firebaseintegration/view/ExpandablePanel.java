package com.evilcorp.firebaseintegration.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.evilcorp.firebaseintegration.R;

public class ExpandablePanel extends LinearLayout {
    private static final String TAG = ExpandablePanel.class.getSimpleName();
    private static final int DEFAULT_ANIM_DURATION = 500;

    private final int mHandleId;
    private final int mContentId;

    private View mHandle;
    private View mContent;

    private boolean mExpanded = true;
    private boolean mFirstOpen = true;

    private float mCollapsedHeight;
    private float mCollapsedWidth;

    private int mOriginalHeight;
    private int mOriginalWidth;

    private int mContentHeight;
    private int mContentWidth;
    private int mAnimationDuration = 0;

    private OnExpandListener mListener;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private float oldX;
    private float oldY;

    public ExpandablePanel(Context context) {
        this(context, null);

    }

    public ExpandablePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandablePanel, 0, 0);

        mAnimationDuration = a.getInteger(R.styleable.ExpandablePanel_animationDuration, DEFAULT_ANIM_DURATION);

        int handleId = a.getResourceId(R.styleable.ExpandablePanel_handle, 0);
        if (handleId == 0) {
            throw new IllegalArgumentException(
                    "The handle attribute is required and must refer to a valid child.");
        }

        int contentId = a.getResourceId(R.styleable.ExpandablePanel_content, 0);
        if (contentId == 0) {
            throw new IllegalArgumentException(
                    "The content attribute is required and must refer to a valid child.");
        }

        mCollapsedWidth = a.getDimension(R.styleable.ExpandablePanel_minWidth,.0f);
        mCollapsedHeight = a.getDimension(R.styleable.ExpandablePanel_minHeight,.0f);

        mHandleId = handleId;
        mContentId = contentId;

        a.recycle();

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if(mContentHeight <= mOriginalHeight && mContentHeight >= mCollapsedHeight) {
            int eventAction = event.getAction();
            float eventX = event.getX();
            float eventY = event.getY();
            Log.d(TAG, "X: " + eventX + " , Y: " + eventY);

            switch (eventAction) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "Action Down");
                    oldX = eventX;
                    oldY = eventY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dX = eventX - oldX;
                    float dY = eventY - oldY;
                    Log.d(TAG, "Action Move "+dX+" , "+dY);
                    if (mContentHeight <= mOriginalHeight && dY > 0f){
                        applyTransformation(0,dY,0.1f);
                    }

                    if(mContentHeight >= mCollapsedHeight && dY < 0f){
                        applyTransformation(0,dY,0.1f);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "Action Up");

                    break;
                default:
                    break;
            }
        //}
        return true;
    }


    public void setOnExpandListener(OnExpandListener listener) {
        mListener = listener;
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHandle = findViewById(mHandleId);
        if (mHandle == null) {
            throw new IllegalArgumentException("The handle attribute is must refer to an existing child.");
        }

        mContent = findViewById(mContentId);
        if (mContent == null) {
            throw new IllegalArgumentException("The content attribute is must refer to an existing child.");
        }

        mOriginalWidth = mContent.getWidth();
        mOriginalHeight = mContent.getHeight();

        mHandle.setOnClickListener(new PanelToggler());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        mContentContainer.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//        mHandle.measure(MeasureSpec.UNSPECIFIED, heightMeasureSpec);
//        mCollapsedHeight = mHandle.getMeasuredHeight();

        mContentWidth = mContent.getMeasuredWidth();
        mContentHeight = mContent.getMeasuredHeight();

        if(mOriginalHeight == 0){
            mOriginalHeight = mContentHeight;
        }
        if(mOriginalWidth == 0){
            mOriginalWidth = mContentWidth;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        if (mFirstOpen) {
//            mContentContainer.getLayoutParams().width = 0;
//            mContentContainer.getLayoutParams().height = mCollapsedHeight;
//            mFirstOpen = false;
//        }
//
//        int width = mHandle.getMeasuredWidth()
//                + mContentContainer.getMeasuredWidth()
//                + mContentContainer.getPaddingRight();
//        int height = mContentContainer.getMeasuredHeight() + mContentContainer.getPaddingBottom();
//
//        setMeasuredDimension(width, height);
    }

    private class PanelToggler implements OnClickListener {
        @Override
        public void onClick(View v) {
            startAnimation();
        }
    }

    private void startAnimation(){
        Animation animation;

        if (mExpanded) {
            animation = new ExpandAnimation(mContentWidth, mContentWidth, mContentHeight, mCollapsedHeight);
            if (mListener != null) {
                mListener.onCollapse(mHandle,mContent);
            }
        } else {
            ExpandablePanel.this.invalidate();
            animation = new ExpandAnimation(mContentWidth, mContentWidth, mCollapsedHeight, mOriginalHeight);
            if (mListener != null) {
                mListener.onExpand(mHandle,mContent);
            }
        }

        animation.setDuration(mAnimationDuration);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mExpanded = !mExpanded;
            }
        });

        mContent.startAnimation(animation);
    }

    private class ExpandAnimation extends Animation {

        private final float mStartWidth;
        private final float mDeltaWidth;
        private final float mStartHeight;
        private final float mDeltaHeight;

        ExpandAnimation(float startWidth, float endWidth, float startHeight, float endHeight) {
            mStartWidth = startWidth;
            mDeltaWidth = endWidth - startWidth;
            mStartHeight = startHeight;
            mDeltaHeight = endHeight - startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
            lp.width = (int) (mStartWidth + mDeltaWidth * interpolatedTime);
            lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
            mContent.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    private void applyTransformation(float deltaWidth, float deltaHeight, float time){
        android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
        lp.width = (int) (getWidth() + deltaWidth * time);
        lp.height = (int) (getHeight() + deltaHeight * time);
        mContent.setLayoutParams(lp);
    }

    public interface OnExpandListener {

        public void onExpand(View handle, View content);
        public void onCollapse(View handle, View content);

    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG,"OnScale");
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }
}