package com.repkap11.repcastaudio.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.repkap11.repcastaudio.R;
import com.repkap11.repcastaudio.fractivity.Fractivity;
import com.repkap11.repcastaudio.model.AudioGroup;

/**
 * Created by paul on 8/8/17.
 */
public class AddAudioGroupFractivityFragment extends Fractivity.FractivityFragment {
    private EditText mEditTextName;
    private Button mSaveLocationButtion;

    @Override
    protected void create(Bundle savedInstanceState) {

    }

    @Override
    protected void saveState(Bundle outState) {

    }

    @Override
    protected void restoreState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fractivity_add_audio_group, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fractivity_bar_menu_app_bar_layout);
        toolbar.setTitle(R.string.fractivity_add_audio_group_title);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_black, null);
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mEditTextName = (EditText) rootView.findViewById(R.id.fractivity_add_audio_group_edit_text_name);
        mSaveLocationButtion = (Button) rootView.findViewById(R.id.fractivity_add_audio_group_button_save);
        mSaveLocationButtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mEditTextName.getText().toString();
                if (text != null && !text.isEmpty()) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference locationsRef = database.getReference(getResources().getString(R.string.root_key_audio_groups));
                    DatabaseReference newLocation = locationsRef.push();
                    newLocation.setValue(new AudioGroup(mEditTextName.getText().toString()));
                }
                getActivity().finish();
            }
        });
        return rootView;
    }

    @Override
    protected void destroyView() {
        mEditTextName = null;
        mSaveLocationButtion = null;
    }
}
