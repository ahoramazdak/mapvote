package net.bpj.updater;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import net.behpardaz.voting.R;

public class Util {

	private static final String CLASS_TAG = "AppUpdater";

	public static String base_url;
	private static String config_file_url;
	private static int title_resource = -1;
	private static int text_resource = -1;
	private static int downloader_title_resource = -1;
	private static int downloader_description_resource = -1;
	private static int downloading_update_message = -1;

	public static void init(String base_url) {
		Util.base_url = base_url;
	}

	public static void init(String base_url, String config_file) {
		Util.init(base_url);
		Util.config_file_url = config_file;
	}

	public static void setConfigFileAddress(String config_file) {
		Util.config_file_url = config_file;
	}

	public static boolean isNetworkAvailable(Context context) {
		boolean status = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null)
			status |= wifiNetwork.isConnectedOrConnecting();
		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null)
			status |= mobileNetwork.isConnectedOrConnecting();
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null)
			status |= activeNetwork.isConnectedOrConnecting();
		return status;
	}

	public static String getAppVersion(Context context) {
		PackageInfo pInfo;
		try {
			pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "0.0";
	}

	public static void setUpdateTitle(int title_res) {
		Util.title_resource = title_res;
	}

	public static void setUpdateText(int text_res) {
		Util.text_resource = text_res;
	}

	public static void setDownloaderTitle(int text_res) {
		Util.downloader_title_resource = text_res;
	}

	public static void setDownloaderDescription(int text_res) {
		Util.downloader_description_resource = text_res;
	}

	public static void setDownloadingUpdateMessage(int text_res) {
		Util.downloading_update_message = text_res;
	}

	public static int getDownloadingUpdateMessage() {
		return Util.downloading_update_message;
	}

	public static void displayUpdateNotification(Context context) {
		if (Util.title_resource == -1)
			Util.title_resource = R.string.update_notif_default_title;
		if (Util.text_resource == -1)
			Util.text_resource = R.string.update_notif_default_text;
		if (Util.downloader_title_resource == -1)
			Util.downloader_title_resource = R.string.app_name;
		if (Util.downloader_description_resource == -1)
			Util.downloader_description_resource = R.string.update_downloader_default_title;
		if (Util.downloading_update_message == -1)
			Util.downloading_update_message = R.string.message_downloading_update;
		UIManager.displayNotification(context,
				context.getString(Util.title_resource),
				context.getString(Util.text_resource));
	}

	public static void checkForUpdates(String current_version, Context context) {
		if (isNetworkAvailable(context)) {
			ReadSettingsFileTask task = new ReadSettingsFileTask(
					current_version, context);
			task.execute(new String[] { Util.base_url + Util.config_file_url });
		} else {
			Log.d(CLASS_TAG, "No Internet Access");
			Toast.makeText(context, "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}
	}

	private static class ReadSettingsFileTask extends
			AsyncTask<String, Void, Void> {

		String oldVersion;
		HashMap<String, String> settings;
		Context context;

		public ReadSettingsFileTask(String oldVersion, Context context) {
			this.context = context;
			this.oldVersion = oldVersion;
			settings = new HashMap<String, String>();
		}

		@Override
		protected Void doInBackground(String... urls) {
			String url = urls[0];
			Log.d(CLASS_TAG, "Connecting " + url);

			try {
				URL urlObj = new URL(url);
				HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
				InputStream is = urlConnection.getInputStream();
				int statusCode = urlConnection.getResponseCode();
				Log.d(CLASS_TAG, "Status Code: " + statusCode);
				if (statusCode == 200) {
					for (String line : IO.readLinesFromStream(urlConnection.getInputStream())) {
						Log.d(CLASS_TAG, "Line: " + line);
						int del = line.indexOf(context
								.getString(R.string.SETTING_VALUE_DELIMITER));
						settings.put(line.substring(0, del),
								line.substring(del + 1));
					}
				}

			} catch (Exception e) {
				Log.d(CLASS_TAG, "error:" + e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			for (String setting : settings.keySet()) {
				if (setting.equals(context.getString(R.string.APP_VERSION_KEY))) {
					Log.w("Util", "Older version:" + oldVersion
							+ " and current version:" + settings.get(setting));

					if (!oldVersion.equals(settings.get(setting))) { // New
						// version
						// is
						// available
						// String newVersion = settings.get(setting);
						displayUpdateNotification(context);

						SharedPreferences appSettings = PreferenceManager
								.getDefaultSharedPreferences(context);
						Editor edit = appSettings.edit();
						edit.putString("apk_url", settings.get(context
								.getApplicationContext().getString(
										R.string.APP_URL_KEY)));
						edit.commit();
						Log.d(CLASS_TAG,
								"Saved apk url as "
										+ appSettings.getString("apk_url", ""));

					} else { // No update required
						Log.d(CLASS_TAG, "Already up-to-date");
						// UIManager.displayNotification(context,
						// "Aldeary Updated", "Your app is up to date");
					}
				}
			}
		}
	}

	public static void invokeAppUpdateRequest(Context context,
			String absolute_file_url) {
		DownloadManager dm = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);

		Uri uri = Uri.parse(absolute_file_url);
		String apk_file_name = absolute_file_url.substring(absolute_file_url
				.lastIndexOf("/") + 1);
		SharedPreferences appSettings = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = appSettings.edit();
		edit.putString("apk_file_name", apk_file_name);
		edit.putString("apk_url", "");
		edit.commit();

		IO.init(context.getString(R.string.app_name));
		File dest = new File(IO.appRoot, apk_file_name);
		Log.d(CLASS_TAG, "Downloading " + absolute_file_url);
		Log.d(CLASS_TAG, "Downloading to " + dest.getAbsolutePath());

		context.registerReceiver(onUpdateComplete, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		context.registerReceiver(onUpdateNotificationClick, new IntentFilter(
				DownloadManager.ACTION_NOTIFICATION_CLICKED));

		dm.enqueue(new DownloadManager.Request(uri)
				.setAllowedNetworkTypes(
						DownloadManager.Request.NETWORK_WIFI
								| DownloadManager.Request.NETWORK_MOBILE)
				.setAllowedOverRoaming(false)
				.setTitle(context.getString(Util.downloader_title_resource))
				.setDescription(
						context.getString(Util.downloader_description_resource))
				.setDestinationUri(Uri.fromFile(dest)));
	}

	public static BroadcastReceiver onUpdateComplete = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {
			ctxt.unregisterReceiver(this);
			ctxt.unregisterReceiver(onUpdateNotificationClick);

			SharedPreferences appSettings = PreferenceManager
					.getDefaultSharedPreferences(ctxt);
			String apk_file_name = appSettings.getString("apk_file_name", "");

			File apk_file = new File(IO.appRoot, apk_file_name);
			Log.d(CLASS_TAG, "Installing from " + apk_file.getAbsolutePath());

			Intent promptInstall = new Intent(Intent.ACTION_VIEW);
			String mimetype = android.webkit.MimeTypeMap.getSingleton()
					.getMimeTypeFromExtension("apk");
			promptInstall.setDataAndType(Uri.fromFile(apk_file), mimetype)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctxt.startActivity(promptInstall);
		}
	};

	public static BroadcastReceiver onUpdateNotificationClick = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {
			Intent showDownloads = new Intent(
					DownloadManager.ACTION_VIEW_DOWNLOADS)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctxt.startActivity(showDownloads);
		}
	};

}
