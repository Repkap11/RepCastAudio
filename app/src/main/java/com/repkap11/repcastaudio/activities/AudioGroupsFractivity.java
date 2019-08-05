package com.repkap11.repcastaudio.activities;

import com.repkap11.repcastaudio.firebase.FirebaseAdapterFractivity;
import com.repkap11.repcastaudio.fragments.AudioGroupsFractivityFragment;
import com.repkap11.repcastaudio.model.AudioGroup;


public class AudioGroupsFractivity extends FirebaseAdapterFractivity<AudioGroupsFractivityFragment.Holder, AudioGroup> {
    @Override
    protected FirebaseAdapterFractivity.FirebaseAdapterFragment createFirebaseFragment() {
        return new AudioGroupsFractivityFragment();
    }


}
