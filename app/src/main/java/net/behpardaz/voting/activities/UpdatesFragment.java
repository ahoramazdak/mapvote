package net.behpardaz.voting.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.tabs.SwipeListAdapter;
import net.behpardaz.voting.mgmt.SessionManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by Ratan on 7/29/2015.
 */
public class UpdatesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "UpdatesFragment";
    private String URL_TOP_250 = "http://api.androidhive.info/json/imdb_top_250.php?offset=";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeListAdapter adapter;
    private List<VoterReq> votersList;
    SessionManager manager;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private static final String STATE_LIST = "State Adapter Data";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState: "+outState );
        if(adapter!=null)
        outState.putParcelableArrayList(STATE_LIST, adapter.getList());
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.updates_layout,null);
        listView = (ListView) view.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        manager=new SessionManager(getContext());
        //If restoring from state, load the list from the bundle
        if (savedInstanceState != null) {
            ArrayList<VoterReq> list = savedInstanceState.getParcelableArrayList(STATE_LIST);
            adapter = new SwipeListAdapter(this.getActivity(), list);
        } else {

            if(votersList ==null|| votersList.isEmpty()) {
                votersList = new ArrayList<>();
                votersList = manager.getList();
            }
        adapter = new SwipeListAdapter(this.getActivity(), votersList);
            }
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
//        swipeRefreshLayout.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        swipeRefreshLayout.setRefreshing(true);
//
//                                        fetchMovies();
//                                    }
//                                }
//        );

        verifyStoragePermissions(getActivity());
        return view;
    }

   /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        fetchVoters();
    }

    /**
     * Fetching movies json by making http call
     */
    private void fetchVoters() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

//         appending offset to url
//        String url = URL_TOP_250 + offSet;
        List<VoterReq> list = manager.getList();
        if(!list.isEmpty()) {
            System.out.println(list.size());
            if(votersList ==null)
                votersList =new ArrayList<VoterReq>();
            if(!votersList.equals(list)){
            votersList =list;
                adapter = new SwipeListAdapter(this.getActivity(), votersList);
                listView.setAdapter(adapter);            }
        }
        swipeRefreshLayout.setRefreshing(false);
            }

}
