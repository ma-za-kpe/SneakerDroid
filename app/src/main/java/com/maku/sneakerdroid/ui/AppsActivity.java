package com.maku.sneakerdroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.maku.sneakerdroid.R;
import com.maku.sneakerdroid.adapters.AppInfoAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppsActivity extends AppCompatActivity {

    private static final String TAG = "AppsActivity";

//    vars
    ArrayList<ApplicationInfo > mStringsSystemApps = new ArrayList<>();
    ArrayList<ApplicationInfo > appsList = new ArrayList<>();

    private LinearLayoutManager mLinearLayoutManager;
    private AppInfoAdapter mAppInfoAdapter;


    //widgets
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        mRecyclerView = findViewById(R.id.appInfoRecyclerview);

        checkAppsInstalled();
        displayAllApps();
        displaySystemApps();
        initRecyclerview();
    }

    private void displayAllApps() {
        Log.d(TAG, "displayAllApps: " + mStringsSystemApps.size());
    }

    private void initRecyclerview() {
        Log.d(TAG, "initRecyclerview: ");
        mLinearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAppInfoAdapter = new AppInfoAdapter(appsList, this);
        mRecyclerView.setAdapter(mAppInfoAdapter);
    }

    private void displaySystemApps() {
        Log.d(TAG, "displaySystemApps: " + mStringsSystemApps.size());
    }

    private void checkAppsInstalled() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo app : apps) {
            if(pm.getLaunchIntentForPackage(app.packageName) != null) {
                // apps with launcher intent
                if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    // updated system apps
                    Log.d(TAG, "checkAppsInstalled: updated system apps " + app);
                    mStringsSystemApps.add(app);

                } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    // system apps
                    Log.d(TAG, "checkAppsInstalled: system apps " + app);

                } else {
                    // user installed apps
                    Log.d(TAG, "checkAppsInstalled: user installed apps " + app);

                }
                appsList.add(app);
            }

        }
    }


}
