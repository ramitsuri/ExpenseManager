package com.ramitsuri.expensemanager.ui.decoration;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/***
 * RecyclerView Item decoration that implements sticky header.
 * A list or grid of items can be grouped together under a header item and
 * the header stays on top of all items that it contains
 */
public class StickyHeaderItemDecoration extends RecyclerView.ItemDecoration {

    private StickyHeaderInterface mListener;
    private int mStickyHeaderHeight;

    public StickyHeaderItemDecoration(@NonNull StickyHeaderInterface listener) {
        mListener = listener;
    }

    @Override
    public void onDrawOver(@NotNull Canvas c, @NotNull RecyclerView parent,
            @NotNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        View topChild = parent.getChildAt(0);
        if (topChild == null) {
            return;
        }

        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return;
        }

        int headerPosition = mListener.getHeaderPositionForItem(topChildPosition);
        View currentHeader = getHeaderViewForItem(parent, headerPosition);
        fixLayoutSize(parent, currentHeader);

        int contactPoint = currentHeader.getBottom();
        View childInContact = getChildInContact(parent, contactPoint, headerPosition);

        if (childInContact != null &&
                mListener.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, currentHeader, childInContact);
            return;
        }

        drawHeader(c, currentHeader);
    }

    private View getHeaderViewForItem(RecyclerView parent, int itemPosition) {
        int headerPosition = mListener.getHeaderPositionForItem(itemPosition);
        int layoutResId = mListener.getHeaderLayout();
        View header = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        mListener.bindHeaderData(header, headerPosition);
        return header;
    }

    private void drawHeader(Canvas c, View header) {
        c.save();
        c.translate(0, 0);
        header.draw(c);
        c.restore();
    }

    private void moveHeader(Canvas c, View currentHeader, View nextHeader) {
        c.save();
        c.translate(0, nextHeader.getTop() - currentHeader.getHeight());
        currentHeader.draw(c);
        c.restore();
    }

    private View getChildInContact(RecyclerView parent, int contactPoint,
            int currentHeaderPosition) {
        View childInContact = null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            int heightTolerance = 0;
            View child = parent.getChildAt(i);

            // measure height tolerance with child, if child is another header
            if (currentHeaderPosition != i) {
                boolean isChildHeader = mListener.isHeader(parent.getChildAdapterPosition(child));
                if (isChildHeader) {
                    heightTolerance = mStickyHeaderHeight - child.getHeight();
                }
            }

            // add heightTolerance if child is on top in display area
            int childBottomPosition;
            if (child.getTop() > 0) {
                childBottomPosition = child.getBottom() + heightTolerance;
            } else {
                childBottomPosition = child.getBottom();
            }

            if (childBottomPosition > contactPoint) {
                if (child.getTop() <= contactPoint) {
                    // this child overlaps the contactPoint
                    childInContact = child;
                    break;
                }
            }
        }
        return childInContact;
    }

    // Properly measures and layouts the top sticky header
    private void fixLayoutSize(ViewGroup parent, View view) {
        // Specs for parent (RecyclerView)
        int widthSpec =
                View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec =
                View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup
                .getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(),
                        view.getLayoutParams().width);
        int childHeightSpec = ViewGroup
                .getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(),
                        view.getLayoutParams().height);

        view.measure(childWidthSpec, childHeightSpec);

        mStickyHeaderHeight = view.getMeasuredHeight();
        view.layout(0, 0, view.getMeasuredWidth(), mStickyHeaderHeight);
    }

    public interface StickyHeaderInterface {

        /**
         * @return Position of header that represents the item at position itemPosition
         */
        int getHeaderPositionForItem(int itemPosition);

        /**
         * @return Layout resource id for header items
         */
        @LayoutRes
        int getHeaderLayout();

        /**
         * Used to bind data to header view at position headerPosition
         */
        void bindHeaderData(View header, int headerPosition);

        /**
         * @return True if item at position itemPosition is a header item
         */
        boolean isHeader(int itemPosition);
    }
}
