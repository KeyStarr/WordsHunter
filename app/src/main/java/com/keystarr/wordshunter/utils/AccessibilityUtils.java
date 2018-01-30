package com.keystarr.wordshunter.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

/**
 * Created by Bizarre on 01.07.2017.
 */

public final class AccessibilityUtils {
    private static final String SERVICE_ID = "com.keystarr.wordshunter/.services.KeyGetService";

    private AccessibilityUtils() {
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (SERVICE_ID.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }
}