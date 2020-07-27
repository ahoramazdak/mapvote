package net.bpj.updater.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.bpj.updater.Util;
import net.behpardaz.voting.R;


public class NotificationResult extends Activity {

	private static final String CLASS_TAG = "AppUpdater";
	private TextView notificationMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Have the system blur any windows behind this one.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.activity_notification_result);

		notificationMessage = (TextView) findViewById(R.id.txt_notif_message);
		notificationMessage.setText(getString(Util
				.getDownloadingUpdateMessage()));
		notificationMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		handleNotification(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleNotification(intent);
		super.onNewIntent(intent);
	}

	private void handleNotification(Intent intent) {

		SharedPreferences appSettings = PreferenceManager
				.getDefaultSharedPreferences(this);

		String apk_url = appSettings.getString("apk_url", "");
		Log.d(CLASS_TAG, "url is = " + apk_url);
		if (!apk_url.equals("")) {
			String url = Util.base_url + apk_url;
			if (apk_url.startsWith("http")) {
				url = apk_url;
			}
			Util.invokeAppUpdateRequest(getApplicationContext(), url);
		} else {
			Log.d(CLASS_TAG, "url was empty > up to date");
			TextView txt = (TextView) findViewById(R.id.txt_notif_message);
			txt.setText("Already up-to-date");
		}

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		HoldTask pause = new HoldTask();
		pause.execute((Void) null);
	}

	public class HoldTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// Hold for a while
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			finish();
		}
	}

}
