package com.nuhkoca.trippo.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ScreenSizer {

    private Activity owner;

    public ScreenSizer(Activity owner) {
        this.owner = owner;
    }

    public void hideNavigationBar() {
        View decorView = owner.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void hideStatusBar() {
        Window decorWindow = owner.getWindow();

        decorWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        decorWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        decorWindow.setStatusBarColor(Color.TRANSPARENT);
    }
}