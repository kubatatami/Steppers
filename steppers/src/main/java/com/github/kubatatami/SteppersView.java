/*
 * Copyright (C) 2016 Jakub Bogacki, Krystian Drożdżyński
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kubatatami;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.kubatatami.steppers.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SteppersView extends FrameLayout {

    private InternalSteppersAdapter internalSteppersAdapter;

    private FragmentManager fragmentManager;

    private List<OnStepChangedListener> onStepChangedListeners = new ArrayList<>();

    private int circleActiveColor;

    private int circleInactiveColor;

    private int circleDoneColor;

    private int labelActiveTextColor;

    private int labelInactiveTextColor;

    private int labelDoneTextColor;

    private int subLabelActiveTextColor;

    private int subLabelInactiveTextColor;

    private int subLabelDoneTextColor;

    private int labelTextSize;

    private int subLabelTextSize;

    private boolean backByTap;

    public SteppersView(Context context) {
        super(context);
    }

    public SteppersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttrs(attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SteppersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttrs(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SteppersView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadAttrs(attrs, defStyleAttr);
    }

    private void loadAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SteppersView, defStyle, 0);
        circleDoneColor = a.getColor(R.styleable.SteppersView_circleDoneColor, ContextCompat.getColor(getContext(), R.color.circle_color_light_blue));
        circleActiveColor = a.getColor(R.styleable.SteppersView_circleActiveColor, ContextCompat.getColor(getContext(), R.color.circle_color_light_blue));
        circleInactiveColor = a.getColor(R.styleable.SteppersView_circleInactiveColor, ContextCompat.getColor(getContext(), R.color.circle_color_dark_blue));
        labelActiveTextColor = a.getColor(R.styleable.SteppersView_labelActiveTextColor, ContextCompat.getColor(getContext(), android.R.color.black));
        labelInactiveTextColor = a.getColor(R.styleable.SteppersView_labelInactiveTextColor, ContextCompat.getColor(getContext(), R.color.circle_color_dark_blue));
        labelDoneTextColor = a.getColor(R.styleable.SteppersView_labelDoneTextColor, ContextCompat.getColor(getContext(), R.color.circle_color_dark_blue));
        subLabelActiveTextColor = a.getColor(R.styleable.SteppersView_subLabelActiveTextColor, ContextCompat.getColor(getContext(), R.color.label_color));
        subLabelInactiveTextColor = a.getColor(R.styleable.SteppersView_subLabelInactiveTextColor, ContextCompat.getColor(getContext(), R.color.label_color));
        subLabelDoneTextColor = a.getColor(R.styleable.SteppersView_subLabelDoneTextColor, ContextCompat.getColor(getContext(), R.color.label_color));
        labelTextSize = a.getDimensionPixelSize(R.styleable.SteppersView_labelTextSize, getResources().getDimensionPixelSize(R.dimen.label_text_size));
        subLabelTextSize = a.getDimensionPixelSize(R.styleable.SteppersView_subLabelTextSize, getResources().getDimensionPixelSize(R.dimen.sub_label_text_size));
        backByTap = a.getBoolean(R.styleable.SteppersView_backByTap, true);
        a.recycle();
    }

    public void notifyDataSetChanged() {
        if (internalSteppersAdapter != null) {
            internalSteppersAdapter.notifyDataSetChanged();
        }
    }

    public void setAdapter(StepperAdapter adapter) {
        if (fragmentManager != null && internalSteppersAdapter == null) {
            build();
        }
        internalSteppersAdapter.setAdapter(adapter);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        if (internalSteppersAdapter == null) {
            build();
        }
    }

    public void addOnStepChangedListener(OnStepChangedListener onStepChangedListener) {
        onStepChangedListeners.add(onStepChangedListener);
    }

    public void removeOnStepChangedListener(OnStepChangedListener onStepChangedListener) {
        onStepChangedListeners.remove(onStepChangedListener);
    }

    public Fragment getStepFragment(int step) {
        return internalSteppersAdapter.getStepFragment(step);
    }

    public Fragment getCurrentStepFragment() {
        return getStepFragment(getCurrentStep());
    }

    public int getCurrentStep() {
        return internalSteppersAdapter.getCurrentStep();
    }

    public void setStep(int step) {
        internalSteppersAdapter.setStep(step);
    }

    public void nextStep() {
        internalSteppersAdapter.nextStep();
    }

    public void prevStep() {
        internalSteppersAdapter.prevStep();
    }

    public int getStepCount() {
        return internalSteppersAdapter.getItemCount();
    }

    @ColorInt
    public int getCircleActiveColor() {
        return circleActiveColor;
    }

    public void setCircleActiveColor(@ColorInt int circleActiveColor) {
        this.circleActiveColor = circleActiveColor;
        notifyDataSetChanged();
    }

    @ColorInt
    public int getCircleInactiveColor() {
        return circleInactiveColor;
    }

    public void setCircleInactiveColor(@ColorInt int circleInactiveColor) {
        this.circleInactiveColor = circleInactiveColor;
        notifyDataSetChanged();
    }

    @ColorInt
    public int getCircleDoneColor() {
        return circleDoneColor;
    }

    public void setCircleDoneColor(@ColorInt int circleDoneColor) {
        this.circleDoneColor = circleDoneColor;
        notifyDataSetChanged();
    }

    @ColorInt
    public int getLabelActiveTextColor() {
        return labelActiveTextColor;
    }

    public void setLabelActiveTextColor(@ColorInt int labelActiveTextColor) {
        this.labelActiveTextColor = labelActiveTextColor;
        notifyDataSetChanged();
    }

    @ColorInt
    public int getLabelInactiveTextColor() {
        return labelInactiveTextColor;
    }

    public void setLabelInactiveTextColor(@ColorInt int labelInactiveTextColor) {
        this.labelInactiveTextColor = labelInactiveTextColor;
        notifyDataSetChanged();
    }

    @ColorInt
    public int getLabelDoneTextColor() {
        return labelDoneTextColor;
    }

    public void setLabelDoneTextColor(@ColorInt int labelDoneTextColor) {
        this.labelDoneTextColor = labelDoneTextColor;
        notifyDataSetChanged();
    }

    @ColorInt
    public int getSubLabelActiveTextColor() {
        return subLabelActiveTextColor;
    }

    public void setSubLabelActiveTextColor(@ColorInt int subLabelActiveTextColor) {
        this.subLabelActiveTextColor = subLabelActiveTextColor;
        notifyDataSetChanged();
    }

    @ColorInt
    public int getSubLabelInactiveTextColor() {
        return subLabelInactiveTextColor;
    }

    public void setSubLabelInactiveTextColor(@ColorInt int subLabelInactiveTextColor) {
        this.subLabelInactiveTextColor = subLabelInactiveTextColor;
        notifyDataSetChanged();
    }

    @ColorInt
    public int getSubLabelDoneTextColor() {
        return subLabelDoneTextColor;
    }

    public void setSubLabelDoneTextColor(@ColorInt int subLabelDoneTextColor) {
        this.subLabelDoneTextColor = subLabelDoneTextColor;
        notifyDataSetChanged();
    }

    public int getLabelTextSize() {
        return labelTextSize;
    }

    public void setLabelTextSize(int labelTextSize) {
        this.labelTextSize = labelTextSize;
    }

    public int getSubLabelTextSize() {
        return subLabelTextSize;
    }

    public void setSubLabelTextSize(int subLabelTextSize) {
        this.subLabelTextSize = subLabelTextSize;
    }

    public boolean isBackByTap() {
        return backByTap;
    }

    public void setBackByTap(boolean backByTap) {
        this.backByTap = backByTap;
        notifyDataSetChanged();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getCurrentStep(), internalSteppersAdapter.visibleSteps);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setStep(savedState.getCurrentStep());
        internalSteppersAdapter.visibleSteps.clear();
        internalSteppersAdapter.visibleSteps.addAll(savedState.getVisibleSteps());
    }

    private void build() {
        initAdapter();
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.item_text_margin_top));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(internalSteppersAdapter);
        addView(recyclerView);
    }

    private void initAdapter() {
        internalSteppersAdapter = new InternalSteppersAdapter(this, fragmentManager);
        internalSteppersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                invokeStepChangedListeners(getCurrentStep());
            }
        });
    }

    private void invokeStepChangedListeners(int step) {
        for (OnStepChangedListener onStepChangedListener : onStepChangedListeners) {
            onStepChangedListener.onStepChanged(step);
        }
    }

    public void addOnStepClickListener(OnStepClickListener onStepClickListener) {
        internalSteppersAdapter.getOnStepClickListeners().add(onStepClickListener);
    }

    public void removeOnStepClickListener(OnStepClickListener onStepClickListener) {
        internalSteppersAdapter.getOnStepClickListeners().remove(onStepClickListener);
    }

    public void hideStep(int step) {
        internalSteppersAdapter.hideStep(step);
    }

    public void showStep(int step) {
        internalSteppersAdapter.showStep(step);
    }

    public interface OnStepChangedListener {

        void onStepChanged(int step);
    }

    public interface OnStepClickListener {

        void onStepClick(int step);
    }

    protected static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        int currentStep;

        LinkedList<Integer> visibleSteps = new LinkedList<>();

        public SavedState(Parcel source) {
            super(source);
            currentStep = source.readInt();
            source.readList(visibleSteps, LinkedList.class.getClassLoader());
        }

        public SavedState(Parcelable superState, int currentStep, LinkedList<Integer> visibleSteps) {
            super(superState);
            this.currentStep = currentStep;
            this.visibleSteps = visibleSteps;
        }

        public LinkedList<Integer> getVisibleSteps() {
            return visibleSteps;
        }

        public int getCurrentStep() {
            return currentStep;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentStep);
            out.writeList(visibleSteps);
        }
    }

}
