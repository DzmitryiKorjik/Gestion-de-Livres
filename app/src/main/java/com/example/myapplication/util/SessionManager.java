package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS = "session_prefs";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences sp;

    public SessionManager(Context ctx) {
        sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void setLoggedInEmail(String email) {
        sp.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getLoggedInEmail() {
        return sp.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return getLoggedInEmail() != null;
    }

    public void clear() {
        sp.edit().clear().apply();
    }
}
