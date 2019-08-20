package com.maku.sneakerdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.maku.sneakerdroid.R;
import com.maku.sneakerdroid.ui.DataUploadActivity;
import com.maku.sneakerdroid.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.pm.ApplicationInfo.FLAG_SYSTEM;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {

    private static final String TAG = "AppInfoAdapter";

    //    vars
    private ArrayList<ApplicationInfo> mArraylist = new ArrayList<>();
    private Context mContext;
    boolean is_system = false;

    HashMap<Boolean,Boolean> statuses = new HashMap<Boolean,Boolean>();
    HashMap<String, HashMap<Boolean,Boolean>> apps = new HashMap<String,HashMap<Boolean,Boolean>>();

    public AppInfoAdapter(ArrayList<ApplicationInfo> arraylist, Context context) {
        mArraylist = arraylist;
        mContext = context;
    }

    @NonNull
    @Override
    public AppInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appinfodata, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppInfoAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");

        holder.name.setText(mArraylist.get(position).packageName);

//        check if systemApp, or installed app, or uninstalled app
        PackageManager pm = mContext.getPackageManager();
        if (pm.getLaunchIntentForPackage(mArraylist.get(position).packageName) != null) {

            if ((mArraylist.get(position).flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // check system apps
                is_system = true;
                Log.d(TAG, "checkAppsInstalled: system apps: " + mArraylist.get(position).packageName);
                Log.d(TAG, "onBindViewHolder: is installed: " + Helper.isPackageInstalled(mContext, mArraylist.get(position).packageName));

                // add to hashmap
                statuses.put( is_system, Helper.isPackageInstalled(mContext, mArraylist.get(position).packageName));
                apps.put(mArraylist.get(position).packageName, statuses);

                if (Helper.isPackageInstalled(mContext, mArraylist.get(position).packageName)) {
                    holder.system.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_done));
                }
            } else {

                // check if not a system app
                is_system = false;
                Log.d(TAG, "checkAppsInstalled: not a system apps: " + mArraylist.get(position).packageName);
                Log.d(TAG, "onBindViewHolder: is installed:" + Helper.isPackageInstalled(mContext, mArraylist.get(position).packageName));

                // add to hashmap
                statuses.put( is_system, Helper.isPackageInstalled(mContext, mArraylist.get(position).packageName));
                apps.put(mArraylist.get(position).packageName, statuses);

                if (Helper.isPackageInstalled(mContext, mArraylist.get(position).packageName)) {
                    holder.system.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_not));
                }

            }
        }

//        onclick
        holder.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, DataUploadActivity.class);
                intent.putExtra("name",mArraylist.get(position).packageName);
                mContext.startActivity(intent);
            }
        });


    }

    private void loopthrough() {

        // Returns Set view
        Set< Map.Entry<String, HashMap<Boolean,Boolean>> > st = apps.entrySet();

        for (Map.Entry<String, HashMap<Boolean,Boolean>> me:st)
        {

            Log.d(TAG, "loopthrough: keys " + me.getKey());
            Log.d(TAG, "loopthrough: values " + me.getValue());
        }
    }


    @Override
    public int getItemCount() {
        return mArraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

//        widgets
        TextView name;
        TextView installed;
        TextView upload;
        TextView system;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            installed = itemView.findViewById(R.id.installed);
            upload = itemView.findViewById(R.id.upload);
            system = itemView.findViewById(R.id.systemApp);
        }

    }
}
