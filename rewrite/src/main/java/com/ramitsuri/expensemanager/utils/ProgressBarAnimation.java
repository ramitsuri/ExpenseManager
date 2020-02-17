package com.ramitsuri.expensemanager.utils;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

public class ProgressBarAnimation extends Animation {
    private ProgressBar mProgressBar;
    private float mFrom;
    private float mTo;

    public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
        super();
        mProgressBar = progressBar;
        mFrom = from;
        mTo = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = mFrom + (mTo - mFrom) * interpolatedTime;
        mProgressBar.setProgress((int)value);
    }
}
