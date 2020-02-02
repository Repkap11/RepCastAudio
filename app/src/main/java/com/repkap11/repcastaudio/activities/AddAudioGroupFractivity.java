package com.repkap11.repcastaudio.activities;

import android.os.Bundle;

import com.repkap11.repcastaudio.fractivity.Fractivity;
import com.repkap11.repcastaudio.fragments.AddAudioGroupFractivityFragment;

public class AddAudioGroupFractivity extends Fractivity<Fractivity.FractivityFragment> {
    @Override
    protected AddAudioGroupFractivityFragment createFragment(Bundle savedInstanceState) {
        return new AddAudioGroupFractivityFragment();
    }

}
