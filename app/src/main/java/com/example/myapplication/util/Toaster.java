package com.example.myapplication.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public final class Toaster {
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private Toaster() {}

    public static void show(Context context, String message) {
        Context appCtx = context.getApplicationContext();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(appCtx, message, Toast.LENGTH_SHORT).show();
        } else {
            MAIN.post(() -> Toast.makeText(appCtx, message, Toast.LENGTH_SHORT).show());
        }
    }

    public static void showLong(Context context, String message) {
        Context appCtx = context.getApplicationContext();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(appCtx, message, Toast.LENGTH_LONG).show();
        } else {
            MAIN.post(() -> Toast.makeText(appCtx, message, Toast.LENGTH_LONG).show());
        }
    }

    public static void show(Context context, int stringResId) {
        show(context, appCtx(context).getString(stringResId));
    }

    private static Context appCtx(Context c) { return c.getApplicationContext(); }
}
