package com.nuhkoca.trippo.adapter;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.nuhkoca.trippo.R;

public class DurationBindingAdapter {

    @BindingAdapter(value = {"duration"})
    public static void bindDuration(TextView view, Object[] durations) {
        if (durations != null) {
            double duration = (double) durations[0];
            String durationUnit = (String) durations[1];

            view.setText(String.format(view.getContext().getString(R.string.duration_format), duration, durationUnit));

        } else {
            view.setText("N/A");
        }
    }
}