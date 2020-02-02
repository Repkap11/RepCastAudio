package com.repkap11.repcastaudio.firebase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repkap11.repcastaudio.R;
import com.repkap11.repcastaudio.activities.SettingsActivity;
import com.repkap11.repcastaudio.activities.SignInFractivity;
import com.repkap11.repcastaudio.app.RCAApplication;
import com.repkap11.repcastaudio.app.UpdateAppTask;
import com.repkap11.repcastaudio.fractivity.Fractivity;
import com.repkap11.repcastaudio.model.User;

/**
 * Created by paul on 8/5/17.
 */

public abstract class BarMenuFractivity extends Fractivity<Fractivity.FractivityFragment> {

    private static final String TAG = BarMenuFractivity.class.getSimpleName();

    public abstract static class BarMenuFractivityFragment extends Fractivity.FractivityFragment implements GoogleApiClient.OnConnectionFailedListener {
        private static final String TAG = BarMenuFractivity.class.getSimpleName();
        private static final int REQUEST_CODE_ASK_FOR_WRITE_EXPERNAL_PERMISSION = 44;
        private GoogleApiClient mGoogleAPIClient;
        private boolean mShowBar = true;
        private long mCurrentAppVersionNumber;
        private MenuItem mUpdateMenuItem;
        private int mShowingAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER;

        @Override
        public void saveState(@NonNull Bundle outState) {
            outState.putBoolean("mShowBar", mShowBar);
            Log.w(TAG, "onSaveInstanceState: Saving mShowBar:" + mShowBar);
            outState.putLong("mCurrentAppVersionNumber", mCurrentAppVersionNumber);
            outState.putInt("mShowingAsActionFlag", mShowingAsActionFlag);
        }

        @Override
        public void restoreState(@NonNull Bundle savedInstanceState) {
            mShowBar = savedInstanceState.getBoolean("mShowBar");
            mCurrentAppVersionNumber = savedInstanceState.getLong("mCurrentAppVersionNumber");
            mShowingAsActionFlag = savedInstanceState.getInt("mShowingAsActionFlag");
            Log.w(TAG, "onActivityCreated: Restoring mShowBar:" + mShowBar);
        }


        @Override
        final public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            try {
                PackageInfo oldPackageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_SIGNATURES);
                mCurrentAppVersionNumber = oldPackageInfo.versionCode;

            } catch (PackageManager.NameNotFoundException e) {
                mCurrentAppVersionNumber = 0;
                e.printStackTrace();
            }
            Log.w(TAG, "onCreateOptionsMenu: mShowBar being used as:" + mShowBar);
            if (mShowBar) {
                inflater.inflate(R.menu.menu_main, menu);
                boolean needsNewTrigger = mUpdateMenuItem == null;
                mUpdateMenuItem = menu.findItem(R.id.action_update);
                mUpdateMenuItem.setShowAsAction(mShowingAsActionFlag);
                String groupKey = RCAApplication.getUserPerferedAudioGroup(getActivity());
                if (groupKey != null && needsNewTrigger) {
                    String userKey = RCAApplication.getUserKey(getActivity());
                    if (userKey != null) {
                        //Log.e(TAG, "Starting signed in user:" + userKey);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference appVersionRef = database.getReference(userKey).child(User.getAppVersionLink());
                        appVersionRef.setValue(mCurrentAppVersionNumber);
                    }
                    DatabaseReference groupsAppVersionRef = FirebaseDatabase.getInstance().getReference("androidVersion");
                    groupsAppVersionRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (mUpdateMenuItem != null) {
                                Long appVersion;
                                try {
                                    appVersion = dataSnapshot.getValue(Long.class);
                                } catch (DatabaseException e) {
                                    appVersion = 0l;
                                }
                                if (appVersion == null) {
                                    appVersion = 0L;
                                }
                                if (appVersion > mCurrentAppVersionNumber) {
                                    mShowingAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM;
                                    mUpdateMenuItem.setShowAsAction(mShowingAsActionFlag);
                                } else {
                                    mShowingAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER;
                                    mUpdateMenuItem.setShowAsAction(mShowingAsActionFlag);
                                }
                                //Log.e(TAG, "CurrentAppVersion:" + mCurrentAppVersionNumber + " server recomends:" + appVersion);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

        @Override
        final public void onDestroyOptionsMenu() {
            mUpdateMenuItem = null;
            super.onDestroyOptionsMenu();
        }

        @Override
        final public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    //Toast.makeText(getActivity(), "Settings selected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    return true;

                case R.id.action_update:
                    startUpdateAppProcedure();
                    break;
                case R.id.action_sign_out:
                    triggerLogOut();
                    break;
                default:
                    Toast.makeText(getActivity(), "Other selected", Toast.LENGTH_SHORT).show();
                    break;

            }
            return false;
        }

        private void triggerLogOut() {
            RCAApplication.updateDeviceToken(getActivity(), false);
            RCAApplication.setUserAudioGroup(getActivity(), null);
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(mGoogleAPIClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Intent intent = new Intent(getActivity(), SignInFractivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        private void startUpdateAppProcedure() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_FOR_WRITE_EXPERNAL_PERMISSION);
                    return;
                }
            }
            continueUpdateAppWithPermissions();
        }

        @Override
        public void requestPermissionResult(int requestCode, String[] permissions,
                                            int[] grantResults) {
            continueUpdateAppWithPermissions();

        }

        private void continueUpdateAppWithPermissions() {
            new UpdateAppTask(getActivity().getApplicationContext(), true).execute();
        }

        @Override
        @CallSuper
        protected void create(Bundle savedInstanceState) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleAPIClient = new GoogleApiClient.Builder(
                    getActivity())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mGoogleAPIClient.connect();
        }


        final protected View createView(LayoutInflater inflater, ViewGroup container, Bundle
                savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fractivity_bar_menu, container, false);
            createBarView(inflater, rootView, savedInstanceState, true);
            setHasOptionsMenu(mShowBar);
            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fractivity_bar_menu_toolbar);
            Log.w(TAG, "createView: mShowBar being used as:" + mShowBar);
            if (mShowBar) {
                ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
                //getActivity().setActionBar(toolbar);
                toolbar.setTitle(getBarTitleString(getActivity()));
                boolean showBackIcon = getShowBackIcon();
                if (showBackIcon) {
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_black, null);
                    toolbar.setNavigationIcon(drawable);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackIconClick();
                        }
                    });
                }
            } else {
                toolbar.setVisibility(View.GONE);
            }
            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            boolean showFab = getShowFab();
            if (showFab) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFabClick();
                    }
                });
            } else {
                fab.hide();
            }

            return rootView;
        }

        protected abstract void onBackIconClick();

        protected abstract boolean getShowBackIcon();

        protected abstract void onFabClick();

        protected abstract boolean getShowFab();

        public abstract String getBarTitleString(Context context);

        @Override
        protected void destroyView() {

        }

        protected abstract View createBarView(LayoutInflater inflater, ViewGroup
                container, Bundle savedInstanceState, boolean intoPatent);


        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        public void setShowBar(boolean showBar) {
            mShowBar = showBar;
        }

        public boolean getShowBar() {
            return mShowBar;
        }
    }
}
