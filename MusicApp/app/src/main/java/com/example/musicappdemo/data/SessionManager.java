package com.example.musicappdemo.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicappdemo.model.auth.AuthResponse;

public class SessionManager {
    private static final String PREFS = "musicapp_session";
    private static final String KEY_ACCESS = "access_token";
    private static final String KEY_REFRESH = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AVATAR = "avatar_uri";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context ctx) {
        prefs = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager get(Context ctx) {
        if (instance == null) instance = new SessionManager(ctx);
        return instance;
    }

    public void save(AuthResponse auth) {
        SharedPreferences.Editor e = prefs.edit();
        e.putString(KEY_ACCESS, auth.accessToken);
        e.putString(KEY_REFRESH, auth.refreshToken);
        if (auth.user != null) {
            e.putString(KEY_USER_ID, auth.user.getId());
            e.putString(KEY_EMAIL, auth.user.getEmail());
            e.putString(KEY_GENDER, auth.user.getGender());
        }
        e.apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getGender() {
        return prefs.getString(KEY_GENDER, null);
    }

    public void saveAvatarUri(String uri) {
        prefs.edit().putString(KEY_AVATAR, uri).apply();
    }

    public String getAvatarUri() {
        return prefs.getString(KEY_AVATAR, null);
    }

    public boolean isLoggedIn() {
        String t = getAccessToken();
        return t != null && !t.isEmpty();
    }

    public void logout() {
        prefs.edit().clear().commit();
    }
}