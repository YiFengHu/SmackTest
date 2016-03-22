package smack.sample.com.smacktest;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ListView;


public class ScrollbarVerticalThumbView extends FrameLayout implements AbsListView.OnScrollListener,
        ViewGroup.OnHierarchyChangeListener {
    // how long before the fast scroll thumb disappears
    private static final long FADE_DURATION = 200;
    protected Drawable scrollbarVerticalThumb;
    protected int varticalThumbW;
    protected int varticalThumbH;
    protected int varticalThumbY;
    protected boolean varticallThumbVisible = true;
    protected ListView mList;
    protected HorizontalScrollView childHorizontalScrollView;
    protected int mVisibleItem;

    // how much transparency to use for the fast scroll thumb
    protected static final int ALPHA_MAX = 255;
    protected static final int THUMb_SIZE_MIN = 60;

    protected int verticalScrollbarPosition;
    protected boolean fastScrollBarEnabled = false;
    protected int firstVisibleItem, visibleItemCount, totalItemCount;

    protected boolean isDisplayScrollBar = true;
    protected Handler mHandler = new Handler();
    protected int alpha = 0;
    protected boolean isNeedToDraw = false;

    public ScrollbarVerticalThumbView(Context context) {
        super(context);
        init(context, null);
    }

    public void setFastScrollBarEnabled(boolean fastScrollBarEnabled) {
        this.fastScrollBarEnabled = fastScrollBarEnabled;
    }

    public ScrollbarVerticalThumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScrollbarVerticalThumbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void enableDisplayScrollBar(boolean isDisplayScrollBar) {
        this.isDisplayScrollBar = isDisplayScrollBar;
        if (childHorizontalScrollView != null) {
            childHorizontalScrollView.setHorizontalScrollBarEnabled(isDisplayScrollBar);
        }
        scrollbarVerticalThumb.setAlpha(!isDisplayScrollBar ? 0 : ALPHA_MAX);
    }

    protected void init(Context context, AttributeSet attrs) {
        Drawable varticalThumbDrawable = null;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.ScrollbarVerticalThumbView);
            varticalThumbDrawable = typedArray.getDrawable(R.styleable.ScrollbarVerticalThumbView_scrollbarThumbVertical);

            typedArray.recycle();
        }
        final Resources res = context.getResources();

        if (varticalThumbDrawable == null) {
            varticalThumbDrawable = res.getDrawable(R.drawable.scrollbar_vertical_thumb);
        }

        useVarticalThumbDrawable(varticalThumbDrawable);

        setWillNotDraw(false);

        setOnHierarchyChangeListener(this);
    }

    private void useVarticalThumbDrawable(Drawable drawable) {
        scrollbarVerticalThumb = drawable;
        varticalThumbW = scrollbarVerticalThumb.getIntrinsicWidth();
        int height = scrollbarVerticalThumb.getIntrinsicHeight();
        varticalThumbH = height > THUMb_SIZE_MIN ? height : THUMb_SIZE_MIN;
    }

    public void setAppointView(ListView appointView) {
        if (mList == null) {
            mList = initListView(appointView);
            mList.setVerticalScrollBarEnabled(false);
        }
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        if (child instanceof ListView) {
            mList = initListView((ListView) child);
        } else if (child instanceof HorizontalScrollView) {
            this.childHorizontalScrollView = (HorizontalScrollView) child;
            child.setHorizontalScrollBarEnabled(isDisplayScrollBar);
        }

    }

    private ListView initListView(ListView view) {
        verticalScrollbarPosition = view.getVerticalScrollbarPosition();
        view.setOnScrollListener(this);
        return view;
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        mList = null;
    }

    protected void onDrawVerticalScrollBar(int left, int top, int right, int bottom) {
        switch (verticalScrollbarPosition) {
            case SCROLLBAR_POSITION_LEFT:
                left = 0;
                right = varticalThumbW;
                break;
        }

        invalidate(left, top, right, bottom);
    }

    protected void changedVerticalScrollBarBounds(Drawable drawable, int left, int top, int right, int bottom) {
        switch (verticalScrollbarPosition) {
            case SCROLLBAR_POSITION_LEFT:
                left = 0;
                right = varticalThumbW;
                break;
        }
        drawable.setBounds(left, top, right, bottom);
    }

    protected int getScrollBarThumbHeigh() {
        final int viewHeight = getHeight();
        int scrollheight = viewHeight / 2;
        if (totalItemCount != 0) {
            int offset = totalItemCount - visibleItemCount;
            if (offset < 6) {
                return viewHeight - offset * 100;
            } else if (totalItemCount < scrollheight) {
                return scrollheight;
            } else {
                return THUMb_SIZE_MIN;
            }
        }
        return 0;


    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (varticallThumbVisible && !fastScrollBarEnabled) {
            final int y = varticalThumbY;
            final int viewWidth = getWidth();

            if (!isDisplayScrollBar) {
                if (mVarticalThumbScrollFade.mStarted) {
                    alpha = mVarticalThumbScrollFade.getAlpha();
                    if (alpha < ALPHA_MAX / 2) {
                        scrollbarVerticalThumb.setAlpha(alpha * 2);
                    }
                    isNeedToDraw = true;
                }
            }

            canvas.translate(0, y);
            scrollbarVerticalThumb.draw(canvas);
            canvas.translate(0, -y);

            enabledHorizontalScrollBar(alpha);

            if (!isNeedToDraw) {
                return;
            }

            if (alpha == 0) {
                mVarticalThumbScrollFade.mStarted = false;
                isNeedToDraw = false;
            }

            onDrawVerticalScrollBar(viewWidth - varticalThumbW, y, viewWidth,
                    y + varticalThumbH);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;

        final int viewWidth = getWidth();
        final int viewHeight = getHeight();

        varticallThumbVisible = true;

        if (scrollBarEnable()) {
            varticalThumbH = getScrollBarThumbHeigh();
            varticalThumbY = ((viewHeight - varticalThumbH) * firstVisibleItem)
                    / (totalItemCount - visibleItemCount);
            changedVerticalScrollBarBounds(scrollbarVerticalThumb, viewWidth - varticalThumbW, 5, viewWidth, varticalThumbH - 5);
        } else if (canScrollVertically()) {
            varticalThumbH = getScrollBarThumbHeigh() - 100;
            varticalThumbY = getListScrollY();
            changedVerticalScrollBarBounds(scrollbarVerticalThumb, viewWidth - varticalThumbW, 5, viewWidth, varticalThumbH - 5);
            onDrawVerticalScrollBar(viewWidth - varticalThumbW, varticalThumbY, viewWidth, varticalThumbY + varticalThumbH);
        } else {
            varticallThumbVisible = false;
        }

        if (firstVisibleItem == mVisibleItem) {
            return;
        }
        isNeedToDraw = true;

        mVisibleItem = firstVisibleItem;

        final int y = varticalThumbY;

        scrollbarVerticalThumb.setAlpha(varticallThumbVisible ? (alpha = ALPHA_MAX) : 0);

        onDrawVerticalScrollBar(viewWidth - varticalThumbW, y, viewWidth, y + varticalThumbH);

        mHandler.removeCallbacks(mVarticalThumbScrollFade);
        if (!isDisplayScrollBar) {
            mVarticalThumbScrollFade.mStarted = false;
        }
        mHandler.postDelayed(mVarticalThumbScrollFade, 1000);
    }


    private ScrollFade mVarticalThumbScrollFade = new ScrollFade() {
        @Override
        public void run() {
            if (!mStarted) {
                startFade();
                invalidate();
            }

            if (getAlpha() > 0) {
                final int viewWidth = getWidth();
                onDrawVerticalScrollBar(viewWidth - varticalThumbW, varticalThumbY, viewWidth, varticalThumbY + varticalThumbH);
            } else {
                mStarted = false;
//                removeThumb();
            }
            isNeedToDraw = false;
        }
    };

    public boolean scrollBarEnable() {
        return (totalItemCount != 0 && totalItemCount - visibleItemCount > 0);
    }

    protected boolean canScrollVertically() {
        if (mList != null && totalItemCount != 0) {
            int range = 0;
            for (int i = 0; i < mList.getChildCount(); i++) {
                range += mList.getChildAt(i).getHeight();
            }
            if (range > getHeight()) {
                return true;
            }
        }
        return false;
    }

    protected int getListScrollY() {
        View c = mList.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = mList.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    protected void enabledHorizontalScrollBar(int alpha) {
        boolean isEnable = (alpha > 0);
        if (isEnabledHorizontalScrollBar() == isEnable) {
            return;
        }
        if (!isDisplayScrollBar) {
            childHorizontalScrollView.setHorizontalScrollBarEnabled(isEnable);
            childHorizontalScrollView.invalidate();
        }
    }

    private boolean isEnabledHorizontalScrollBar() {
        if (childHorizontalScrollView == null) {
            return false;
        }
        return childHorizontalScrollView.isHorizontalScrollBarEnabled();
    }


    public abstract class ScrollFade implements Runnable {
        long mStartTime;
        long mFadeDuration;
        boolean mStarted;

        void startFade() {
            mFadeDuration = FADE_DURATION;
            mStartTime = SystemClock.uptimeMillis();
            mStarted = true;
        }

        int getAlpha() {
            if (!mStarted) {
                return ALPHA_MAX;
            }
            int alpha;
            long now = SystemClock.uptimeMillis();
            if (now > mStartTime + mFadeDuration) {
                alpha = 0;
            } else {
                alpha = (int) (ALPHA_MAX - ((now - mStartTime) * ALPHA_MAX)
                        / mFadeDuration);
            }
            return alpha;
        }

    }
}
