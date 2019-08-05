package com.repkap11.repcastaudio.model;

/**
 * Created by paul on 8/2/17.
 */

public class AudioGroup {
    public String displayName;
    public Boolean hasWeirdBeer;

    public AudioGroup() {
    }

    public AudioGroup(String displayName) {
        this();
        this.displayName = displayName;
    }
}
