package com.github.jimtrung.theater.util;

import java.util.prefs.Preferences;

public class AuthTokenUtil {
  private String accessToken;

  private static final String KEY_REFRESH_TOKEN = "refreshToken";
  private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

  public AuthTokenUtil() {}

  public void saveAccessToken(String token) {
    this.accessToken = token;
  }

  public void saveRefreshToken(String token) {
    prefs.put(KEY_REFRESH_TOKEN, token);
  }

  public void clearRefreshToken() {
    prefs.remove(KEY_REFRESH_TOKEN);
  }

  public void clearAccessToken() { this.accessToken = null; }

  public String loadAccessToken() {
    return accessToken;
  }

  public String loadRefreshToken() {
    return prefs.get(KEY_REFRESH_TOKEN, null);
  }
}
