package com.repkap11.repcastaudio.model;

/**
 * Created by paul on 8/2/17.
 */

public class User {
    public String displayName;
    public Long appVersion;

    public User() {
    }

    public User(String displayName, Long carSize) {
        this();
        this.displayName = displayName;
    }

    public static String getDisplayNameLink() {
        return "displayName";
    }

    public static String getAppVersionLink() {
        return "androidVersion";
    }
}
