package com.nuhkoca.trippo.adapter;

import android.databinding.BindingAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.nuhkoca.trippo.R;

public class PriceBindingAdapter {

    @BindingAdapter(value = {"priceText", "suffix"})
    public static void bindPrice(TextView view, String[] prices, TextView suffix) {
        if (prices != null) {
            String amount = prices[0];
            String currency = prices[1];

            if (currency.equals("EUR")) {
                view.setText(String.format(view.getContext().getString(R.string.money_suffix), amount, "€"));
            } else {
                view.setText(String.format(view.getContext().getString(R.string.money_suffix), amount, "$"));
            }
        } else {
            hideViews(view, suffix);
        }
    }

    private static void hideViews(final TextView view, final TextView suffix) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                suffix.setVisibility(View.GONE);

                ViewGroup.MarginLayoutParams marginLayoutParams =
                        (ViewGroup.MarginLayoutParams) view.getLayoutParams();

                marginLayoutParams.setMarginEnd(0);

                view.setLayoutParams(marginLayoutParams);

                suffix.setText("N/A");
            }
        });
    }
}