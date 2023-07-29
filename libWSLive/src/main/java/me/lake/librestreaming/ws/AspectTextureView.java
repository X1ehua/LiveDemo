package me.lake.librestreaming.ws;

import android.content.Context;
import android.util.AttributeSet;

public class AspectTextureView extends android.view.TextureView {
    public static final int MODE_FITXY = 0;
    public static final int MODE_INSIDE = 1;
    public static final int MODE_OUTSIDE = 2;
    private double mTargetAspect = -1;
    private int    mAspectMode   = MODE_OUTSIDE;

    public AspectTextureView(Context context) {
        super(context);
    }

    public AspectTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param mode        {@link #MODE_FITXY},{@link #MODE_INSIDE},{@link #MODE_OUTSIDE}
     * @param aspectRatio width/height
     */
    public void setAspectRatio(int mode, double aspectRatio) {
        if (mode != MODE_INSIDE && mode != MODE_OUTSIDE && mode != MODE_FITXY) {
            throw new IllegalArgumentException("illegal mode");
        }
        if (aspectRatio < 0) {
            throw new IllegalArgumentException("illegal aspect ratio");
        }
        if (mTargetAspect != aspectRatio || mAspectMode != mode) {
            mTargetAspect = aspectRatio;
            mAspectMode = mode;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mTargetAspect > 0) {
            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);

            double viewAspectRatio = (double) initialWidth / initialHeight;
            double aspectDiff = mTargetAspect / viewAspectRatio - 1;

            if (Math.abs(aspectDiff) > 0.01 && mAspectMode != MODE_FITXY) {
                if (mAspectMode == MODE_INSIDE) {
                    if (aspectDiff > 0) {
                        initialHeight = (int) (initialWidth / mTargetAspect);
                    } else {
                        initialWidth = (int) (initialHeight * mTargetAspect);
                    }
                } else if (mAspectMode == MODE_OUTSIDE) {
                    if (aspectDiff > 0) {
                        initialWidth = (int) (initialHeight * mTargetAspect);
                    } else {
                        initialHeight = (int) (initialWidth / mTargetAspect);
                    }
                }
                widthMeasureSpec  = MeasureSpec.makeMeasureSpec(initialWidth,  MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        android.view.View v = (android.view.View)getParent();
        if (v != null) {
            int mw = v.getMeasuredWidth();
            int mh = v.getMeasuredHeight();
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();
            t = (mh - h) / 2;
            l = (mw - w) / 2;
            r += l;
            b += t;
        }
        super.layout(l, t, r, b);
    }
}
