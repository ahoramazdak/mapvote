package net.behpardaz.voting.activities.tabs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.AccountInfo;
import net.behpardaz.voting.mgmt.SessionManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by Ratan on 7/29/2015.
 */
public class ProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "ProfileFragment";
    SessionManager manager;
   EditText _nameText;
    EditText _familyText;
    EditText _emailText;
    EditText _mobileText;
    EditText _charge_amount;
    SwipeRefreshLayout swipeRefreshLayout;
    View inflate;
    private Bundle savedState = null;
    private AccountInfo accountData;
    private static final String STATE_LIST = "State Adapter Data";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState: outState+accountData "+outState+" "+accountData );
        if(accountData!=null)
        outState.putParcelable(STATE_LIST, accountData);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: accountData "+accountData );
        onSaveInstanceState(new Bundle());
     }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.primary_layout, container,false);
//        ButterKnife.bind(inflate);
        _nameText=inflate.findViewById(R.id.input_name1);
        _familyText=inflate.findViewById(R.id.input_address1);
        _emailText=inflate.findViewById(R.id.input_email1);
        _mobileText=inflate.findViewById(R.id.input_mobile1);
        _charge_amount=inflate.findViewById(R.id.charge_amount);

        manager=new SessionManager(getContext());
        Bundle mySavedInstanceState = getArguments();
        Log.e(TAG, "onCreateView: savedInstanceState "+savedInstanceState+" "+mySavedInstanceState );
        if(savedInstanceState!=null) {
            accountData = savedInstanceState.getParcelable(STATE_LIST);
            Log.e(TAG, "onCreateView: "+accountData );
            if(accountData!=null)
            setData(accountData);
        }else
            get_account_info();

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout_info);
        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.post(new Runnable() {
//                                    @Override
//                                    public void run() {
////                                        swipeRefreshLayout.setRefreshing(true);
//                                        swipeRefreshLayout.setRefreshing(false);
//
//                                        get_account_info();
//
//                                    }
//                                }
//        );
        return inflate;
    }

    public void get_account_info(){


                    if(!manager.getUserDetails().get(manager.USR_UID).isEmpty()) {
                        HttpAccRequestTask httpAccRequestTask = new HttpAccRequestTask(manager.getUserDetails().get(manager.USR_UID), manager.getUserDetails().get(manager.USR_TOKEN));
                        httpAccRequestTask.execute();
                    }

}

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        get_account_info();
    }


    private class HttpAccRequestTask extends AsyncTask<String, Void, AccountInfo> {
        final  ProgressDialog progressDialog ;
        String msg = "";
        private final String uid;
        private final String token;
        HttpAccRequestTask(String uid, String token) {
            this.uid = uid;
            this.token = token;
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // actually could set running = false; right here, but I'll
                    // stick to contract.
                    cancel(true);
                }
            });
        }

        @Override
        protected void onPreExecute() {

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("دریافت اطلاعات کاربر . . .");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected AccountInfo doInBackground(String... paramss) {
            return manager.get_account(uid,token);
        }

        @Override
        protected void onPostExecute(AccountInfo data) {
            if (data != null) {
                accountData=data;
                setData(accountData);

            }
            progressDialog.dismiss();
        }
        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            super.onCancelled();
        }
    }

    private void setData(AccountInfo data) {
        _nameText=(EditText) inflate.findViewById(R.id.input_name1);
        _familyText=(EditText) inflate.findViewById(R.id.input_address1);
        _emailText=(EditText) inflate.findViewById(R.id.input_email1);
        _mobileText=(EditText) inflate.findViewById(R.id.input_mobile1);
        _charge_amount=(EditText) inflate.findViewById(R.id.charge_amount);
        _nameText.setText(data.getFname());
        _familyText.setText(data.getLname());
        _emailText.setText(data.getEmail());
        _mobileText.setText(data.getMobile());
        _charge_amount.setText(data.getBalance());
    }

}
