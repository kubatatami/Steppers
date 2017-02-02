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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kubatatami.steppers.R;

import java.util.LinkedList;
import java.util.List;

class InternalSteppersAdapter extends RecyclerView.Adapter<SteppersViewHolder> {

    private SteppersView steppersView;

    private FragmentManager fragmentManager;

    private int currentStep = 0;

    private StepperAdapter adapter;

    LinkedList<Integer> visibleSteps = new LinkedList<>();

    public List<SteppersView.OnStepClickListener> getOnStepClickListeners() {
        return onStepClickListeners;
    }

    private List<SteppersView.OnStepClickListener> onStepClickListeners = new LinkedList<>();

    InternalSteppersAdapter(SteppersView steppersView, FragmentManager fragmentManager) {
        this.steppersView = steppersView;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public SteppersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SteppersViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_steppers, parent, false));
    }

    @Override
    public void onBindViewHolder(final SteppersViewHolder holder, final int position) {
        int externalAdapterPosition = getExternalAdapterPosition(position);
        holder.setChecked(position < currentStep);
        if (holder.isChecked()) {
            holder.roundedView.setChecked(true);
        } else {
            holder.roundedView.setChecked(false);
            holder.roundedView.setText(Integer.toString(position + 1));
        }
        if (position == currentStep) {
            holder.roundedView.setCircleColor(steppersView.getCircleActiveColor());
            holder.textViewLabel.setTextColor(steppersView.getLabelActiveTextColor());
            holder.textViewSubLabel.setTextColor(steppersView.getSubLabelActiveTextColor());
        } else if (holder.isChecked()) {
            holder.roundedView.setCircleColor(steppersView.getCircleDoneColor());
            holder.textViewLabel.setTextColor(steppersView.getLabelDoneTextColor());
            holder.textViewSubLabel.setTextColor(steppersView.getSubLabelDoneTextColor());
        } else {
            holder.roundedView.setCircleColor(steppersView.getCircleInactiveColor());
            holder.textViewLabel.setTextColor(steppersView.getLabelInactiveTextColor());
            holder.textViewSubLabel.setTextColor(steppersView.getSubLabelInactiveTextColor());
        }
        if (position < currentStep && steppersView.isBackByTap()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStep(position);
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }

        holder.textViewLabel.setText(adapter.getLabel(externalAdapterPosition));
        holder.textViewSubLabel.setText(adapter.getSubLabel(externalAdapterPosition));
        holder.textViewLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, steppersView.getLabelTextSize());
        holder.textViewSubLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, steppersView.getSubLabelTextSize());

        holder.frameLayout.setVisibility(position == currentStep ? View.VISIBLE : View.GONE);
    }

    private int getExternalAdapterPosition(int internalPosition) {
        return visibleSteps.get(internalPosition);
    }

    @Override
    public void onViewAttachedToWindow(SteppersViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        initFragment(holder, holder.getAdapterPosition());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return visibleSteps.size();
    }

    private void initFragment(SteppersViewHolder holder, int position) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        String name = makeFragmentName(steppersView.getId(), getExternalAdapterPosition(position));
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if (position != currentStep && fragment != null) {
            ft.remove(fragment);
        } else if (position == currentStep) {
            ft.replace(holder.frameLayout.getId(), adapter.getFragment(getExternalAdapterPosition(position)), name);
        }
        ft.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    Fragment getStepFragment(int step) {
        String name = makeFragmentName(steppersView.getId(), getExternalAdapterPosition(step));
        return fragmentManager.findFragmentByTag(name);
    }

    int getCurrentStep() {
        return currentStep;
    }

    private void setCurrentStep(int step) {
        currentStep = step;
    }

    void setStep(int step) {
        if (isValidStep(getCurrentStep()) && step != getCurrentStep()) {
            int len = Math.abs(step - getCurrentStep()) + 1;
            int start = Math.min(step, getCurrentStep());
            setCurrentStep(step);
            notifyItemRangeChanged(start, len);
            for (SteppersView.OnStepClickListener onItemClickListener : onStepClickListeners) {
                onItemClickListener.onStepClick(step);
            }
        }
    }

    void nextStep() {
        if (isValidStep(getCurrentStep() + 1)) {
            setCurrentStep(currentStep + 1);
            notifyItemRangeChanged(currentStep - 1, 2);
        }
    }

    void prevStep() {
        if (isValidStep(getCurrentStep() - 1)) {
            setCurrentStep(getCurrentStep() - 1);
            notifyItemRangeChanged(getCurrentStep(), 2);
        }
    }

    private boolean isValidStep(int step) {
        return step >= 0 && step < visibleSteps.size();
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:steppers:" + viewId + ":" + id;
    }

    void setAdapter(StepperAdapter adapter) {
        this.adapter = adapter;
        setCurrentStep(0);
        visibleSteps.clear();
        int adapterStepCount = adapter.getStepCount();
        for (int i = 0; i < adapterStepCount; i++) {
            visibleSteps.add(i);
        }
        notifyDataSetChanged();
    }

    void hideStep(Integer step) {
        if (visibleSteps.contains(step)) {
            int removedItemPosition = visibleSteps.indexOf(step);
            notifyItemRemoved(removedItemPosition);
            notifyItemRangeChanged(removedItemPosition, visibleSteps.size() - removedItemPosition);
            visibleSteps.remove(step);
        }
    }

    void showStep(Integer step) {
        if (visibleSteps.contains(step)) {
            return;
        }
        for (int i = visibleSteps.size() - 1; i >= 0; i--) {
            if (visibleSteps.get(i) < step) {
                visibleSteps.add(i + 1, step);
                notifyItemInserted(i + 1);
                notifyItemRangeChanged(i + 1, visibleSteps.size() - i);
                return;
            }
        }
    }


}
