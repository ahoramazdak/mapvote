package net.bpj.updater;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import net.bpj.updater.activities.NotificationResult;
import net.behpardaz.voting.R;

public class UIManager {

	private static int background_resource = -1;
	private static int icon_resource = -1;
	private static int layout_resource = -1;
	private static int title_color = -1;
	private static int desc_color = -1;

	public static void displayNotification(Context context, String title,
			String message) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(
				context.getResources().getInteger(R.integer.NOTIFICATION_ID),
				createNotification(context, title, message));
	}

	public static void displayNotification(Context context, String title,
			String message, int icon_resource) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notif = createNotification(context, title, message);
		notif.icon = icon_resource;
		mNotificationManager.notify(
				context.getResources().getInteger(R.integer.NOTIFICATION_ID),
				notif);
	}

	public static void setIconResourse(int res) {
		UIManager.icon_resource = res;
	}

	public static void setBackgroundResource(int res) {
		UIManager.background_resource = res;
	}

	public static void setTitleColor(int color) {
		UIManager.title_color = color;
	}

	public static void setDescColor(int color) {
		UIManager.desc_color = color;
	}

	public static void setRTL(boolean rtl) {
		if (rtl)
			UIManager.layout_resource = R.layout.notification_layout_rtl;
		else
			UIManager.layout_resource = R.layout.notification_layout_ltr;
	}

	private static Notification createNotification(Context context,
			String title, String message) {
		Notification notif = new Notification();
		// This is who should be launched if the user selects our
		// notification.
		notif.contentIntent = PendingIntent.getActivity(context, 0, new Intent(
				context, NotificationResult.class)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
				PendingIntent.FLAG_UPDATE_CURRENT);
		notif.tickerText = message;
		// adding sound
		notif.defaults |= Notification.DEFAULT_SOUND;

		// our custom view
		if (UIManager.layout_resource == -1)
			UIManager.layout_resource = R.layout.notification_layout_ltr;
		RemoteViews contentView = new RemoteViews(context.getPackageName(),
				UIManager.layout_resource);

		if (UIManager.background_resource == -1)
			UIManager.background_resource = R.color.notification_default_background;

		contentView.setTextViewText(R.id.notif_title, title);
		contentView.setTextViewText(R.id.notif_text, message);
		if (UIManager.title_color != -1)
			contentView.setTextColor(R.id.notif_title, UIManager.title_color);
		if (UIManager.desc_color != -1)
			contentView.setTextColor(R.id.notif_text, UIManager.desc_color);

		contentView.setInt(R.id.notification_container,
				"setBackgroundResource", UIManager.background_resource);
		contentView.setInt(R.id.notif_icon, "setImageResource",
				UIManager.icon_resource);

		notif.contentView = contentView;
		notif.when = System.currentTimeMillis();
		// the icon for the status bar
		if (UIManager.icon_resource == -1) {
			UIManager.icon_resource = R.drawable.ic_launcher;
		}
		notif.when = System.currentTimeMillis();
		notif.icon = UIManager.icon_resource;

		return notif;
	}

}
