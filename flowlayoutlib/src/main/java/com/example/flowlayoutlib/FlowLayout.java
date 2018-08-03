package com.example.flowlayoutlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/3.
 */

public class FlowLayout extends ViewGroup{
    public final static int WRAP_VERTICAL = 1;
    public final static int WRAP_HORIZONTAL = 2;

    private Context mContext;

    private int mCurrentRow;

    private List<Row> mRowList;

    private int mWrapMode;

    public FlowLayout(Context context) {
        this(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs,R.styleable.FlowLayout);
        mWrapMode = typedArray.getInt(R.styleable.FlowLayout_wrapMode,WRAP_VERTICAL);
        typedArray.recycle();

        mRowList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.AT_MOST){
            mWrapMode = WRAP_VERTICAL;
            onMeasureVertical(widthMeasureSpec,heightMeasureSpec);
        }else if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            mWrapMode = WRAP_HORIZONTAL;
            onMeasureHorizontal(widthMeasureSpec,heightMeasureSpec);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    private void onMeasureVertical(int widthMeasureSpec, int heightMeasureSpec){
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;

        mRowList.clear();
        mCurrentRow = 0;
        mRowList.add(new Row());

        for(int i = 0;i < getChildCount();i++){
            Row currentRow = mRowList.get(mCurrentRow);
            final View child = getChildAt(i);
            measureChild(child,widthMeasureSpec,heightMeasureSpec);

            if(child.getVisibility() == GONE || child.getMeasuredWidth() == 0 || child.getMeasuredHeight() == 0){
                continue;
            }

            final MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

            int widthInNeed = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;

            if(currentRow.width + paddingLeft + paddingRight + widthInNeed > width){
                mCurrentRow++;
                currentRow = new Row();
                mRowList.add(currentRow);
            }
            currentRow.width += widthInNeed;
            currentRow.mViews.add(child);
        }

        for(Row row : mRowList){
            measureRowHeight(row);
            height += row.height;
        }
        height += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(width,height);
    }

    private void measureRowHeight(Row row){
        int height = 0;
        for(View view : row.mViews){
            final MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
            height = Math.max(view.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin,height);
        }
        row.height = height;
    }

    private void onMeasureHorizontal(int widthMeasureSpec, int heightMeasureSpec){
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch(mWrapMode){
            case WRAP_VERTICAL:
                onLayoutVertical(changed,l,t,r,b);
                break;

            case WRAP_HORIZONTAL:
                onLayoutHorizontal(changed,l,t,r,b);
                break;
        }
    }

    private void onLayoutVertical(boolean changed, int l, int t, int r, int b){
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        int left;
        int right;
        int top;
        int bottom;
        int currentWidth = paddingLeft;
        int currentHeight = paddingTop;

        for(int i = 0;i < mRowList.size();i++){
            Row row = mRowList.get(i);
            for(View view : row.mViews){
                final MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
                left = currentWidth + layoutParams.leftMargin;
                right = left + view.getMeasuredWidth();
                top = currentHeight + layoutParams.topMargin;
                bottom = top + view.getMeasuredHeight();
                view.layout(left,top,right,bottom);
                currentWidth = right + layoutParams.rightMargin;
            }
            currentWidth = paddingLeft;
            currentHeight += row.height;
        }
    }

    private void onLayoutHorizontal(boolean changed, int l, int t, int r, int b){
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(mContext,attrs);
    }

    private class Row{
        List<View> mViews;
        int width;
        int height;

        public Row(){
            mViews = new ArrayList<>();
            width = 0;
            height = 0;
        }
    }
}
