package com.nuhkoca.trippo.adapter;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.nuhkoca.trippo.R;

public class ScoreBindingAdapter {

    @BindingAdapter(value = {"score"})
    public static void bindScore(TextView view, double score) {
        if (score != 0) {
            view.setText(String.format(view.getContext().getString(R.string.score_prefix), score));
        } else {
            view.setText("N/A");
        }
    }
}