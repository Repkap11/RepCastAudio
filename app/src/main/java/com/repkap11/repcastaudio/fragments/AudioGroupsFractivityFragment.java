package com.repkap11.repcastaudio.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.repkap11.repcastaudio.R;
import com.repkap11.repcastaudio.app.RCAApplication;
import com.repkap11.repcastaudio.firebase.FirebaseAdapterFractivity;
import com.repkap11.repcastaudio.model.AudioGroup;
import com.repkap11.repcastaudio.model.User;

/**
 * Created by paul on 8/8/17.
 */
public class AudioGroupsFractivityFragment extends FirebaseAdapterFractivity.FirebaseAdapterFragment {

    private static final String TAG = AudioGroupsFractivityFragment.class.getSimpleName();

    @Override
    protected void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
    }

    @Override
    protected void onBackIconClick() {

    }

    @Override
    protected boolean getShowBackIcon() {
        return false;
    }

    @Override
    protected void onFabClick() {
        //startActivity(new Intent(getActivity(), AddLunchGroupFractivity.class));
    }

    @Override
    protected boolean getShowFab() {
        return true;
    }

    @Override
    public String getBarTitleString(Context context) {
        return context.getResources().getString(R.string.fractivity_audio_groups_title);
    }

    private ListView mListView;
    private FloatingActionButton mFab;

    //Using this activity view
    @Override
    protected View createAdapterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, boolean attachToRoot) {
        View rootView = inflater.inflate(R.layout.fractivity_audio_groups, container, attachToRoot);
        mListView = (ListView) rootView.findViewById(R.id.fractivity_lunch_groups_list);
        return rootView;
    }

    @Override
    protected void destroyView() {
        mListView = null;
        mFab = null;
        super.destroyView();
    }

    //Put this data
    @Override
    protected String adapterReference() {
        return getString(R.string.root_key_audio_groups);
    }

    //With this filter
    @Override
    protected Query getQuery(DatabaseReference databaseRef) {
        return databaseRef.orderByValue();
    }

    //Into list listview
    @Override
    protected AbsListView getListView(View rootView) {
        return mListView;
    }

    //Where each element uses this view
    @Override
    public int getListResource() {
        return R.layout.fractivity_audio_groups_list_element;
    }

    //And that view has a holder caching position and subviews
    @Override
    public Holder populateHolder(View convertView) {
        Holder holder = new Holder();
        holder.mName = (TextView) convertView.findViewById(R.id.fractivity_lunch_groups_list_element_text);
        return holder;
    }

    //And each subview is populated with data
    @Override
    public void populateView(View convertView, Object o, int position, String key, Object value) {
        Holder holder = (Holder) o;
        AudioGroup lunchGroup = (AudioGroup) value;
        holder.mName.setText(lunchGroup.displayName);
        holder.mIndex = position;
    }

    @Override
    public Class getAdapterDataClass() {
        return AudioGroup.class;
    }

    @Override
    protected void onItemClicked(View view, Object holderObject, int position, String key, String link, Object value) {
        //Intent intent = new Intent(getContext(), TabFractivity.class);
        //Log.e(TAG, "Starting with group:" + key);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Unexpected null user");
            getActivity().finish();
            return;
        }
        RCAApplication.setUserAudioGroup(AudioGroupsFractivityFragment.this.getActivity(), key);
        String userKey = RCAApplication.getUserKey(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference uderRef = database.getReference(RCAApplication.getUserKey(getActivity()));
        uderRef.child(User.getDisplayNameLink()).setValue(user.getDisplayName());
        RCAApplication.updateDeviceToken(getActivity(), true);

        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e(TAG, "onItemClicked: TODO send an intent when the groups is clicked.");

        //startActivity(intent);
    }

    public static class Holder {
        public TextView mName;
        public int mIndex;
    }
}
