package net.behpardaz.voting.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.MainActivity;
import net.behpardaz.voting.mgmt.SessionManager;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by amin on 21/09/16.
 */

public class SplashActivity extends AppCompatActivity {
    SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
         new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        net.bpj.updater.Util.init(getString(R.string.update_url),getString(R.string.update_file));
                        net.bpj.updater.Util.checkForUpdates(net.bpj.updater.Util.getAppVersion(getApplicationContext()),SplashActivity.this);

                        manager = new SessionManager(getApplicationContext());
                        final String status=manager.getPreferences(SplashActivity.this,"status");
                        Log.d("status",status);

                        if (status.equals("1")){
                            Intent i=new Intent(SplashActivity.this,MainActivity.class);
                            startActivity(i);
                        }else{
                            Intent i=new Intent(SplashActivity.this,LoginActivity.class);
                            startActivity(i);
                        }
                        overridePendingTransition(R.anim.action_fade_in, R.anim.action_fade_out);
                        // close this activity
                        finish();
                    }
                }).start();

            }
        },3000); // wait for 3 seconds
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }
}