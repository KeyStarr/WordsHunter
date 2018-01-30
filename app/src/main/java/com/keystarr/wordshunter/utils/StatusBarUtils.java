package com.keystarr.wordshunter.utils;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Cyril on 20.11.2017.
 */

public final class StatusBarUtils {

    private StatusBarUtils() {
    }

    public static void setStatusBarColor(Window window, int colorReferenceR) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(window.getContext(), colorReferenceR));
        }
    }
}
