package com.nuhkoca.trippo.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class SearchView extends android.support.v7.widget.SearchView {
    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if (getQuery().length() == 0) {
                    onActionViewCollapsed();
                }
            }
        }

        return super.dispatchKeyEventPreIme(event);
    }
}