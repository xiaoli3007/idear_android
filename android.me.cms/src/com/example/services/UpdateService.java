package com.example.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.activity.Navigation;
import com.example.cms.Consts;
import com.example.cms.R;
import com.example.cms.Cms;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class UpdateService extends Service {
	private String headerUserAgent = "ShangXiang-Android Updater";
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	private String filepath = null;
	private String filename = null;
	private File filesave = null;
	private NotificationManager nm = null;
	private Notification notification = null;
	private Intent intent = null;
	private PendingIntent pi = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			this.filepath = intent.getStringExtra("path");
			this.filename = intent.getStringExtra("file_name");
			this.nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			this.notification = new Notification();
			this.intent = new Intent(this, Navigation.class);
			this.pi = PendingIntent.getActivity(this, 0, this.intent, 0);
			this.notification.icon = R.drawable.app_icon;
			this.notification.tickerText = Cms.APP.getString(R.string.update_ticker_title);
			this.notification.setLatestEventInfo(this, Cms.APP.getString(R.string.app_name), Cms.APP.getString(R.string.update_notification_ready), this.pi);
			this.nm.notify(0, this.notification);

			String strFilename = this.filename == null ? this.filepath.substring(this.filepath.lastIndexOf("/") + 1) : this.filename;
			this.filesave = new File(Consts.UPDATE_LOCAL, strFilename);
			new Thread(new UpdateRunnable()).start();
		} catch (Exception e) {
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private Handler updateHandler = new Handler() {
		@SuppressWarnings("static-access")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				UpdateService.this.notification.flags |= UpdateService.this.notification.FLAG_AUTO_CANCEL;
				Uri uri = Uri.fromFile(UpdateService.this.filesave);
				Intent intentInstall = new Intent(Intent.ACTION_VIEW);
				intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
				UpdateService.this.pi = PendingIntent.getActivity(UpdateService.this, 0, intentInstall, 0);
				UpdateService.this.notification.defaults = Notification.DEFAULT_SOUND;
				UpdateService.this.notification.setLatestEventInfo(UpdateService.this, Cms.APP.getString(R.string.app_name), Cms.APP.getString(R.string.update_notification_download_success), UpdateService.this.pi);
				UpdateService.this.nm.notify(0, UpdateService.this.notification);
				stopService(UpdateService.this.intent);
				break;
			case DOWNLOAD_FAIL:
				UpdateService.this.notification.setLatestEventInfo(UpdateService.this, Cms.APP.getString(R.string.app_name), Cms.APP.getString(R.string.update_notification_download_error), UpdateService.this.pi);
				UpdateService.this.nm.notify(0, UpdateService.this.notification);
				break;
			default:
				stopService(UpdateService.this.intent);
				break;
			}
		}
	};

	class UpdateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			try {
				if (!UpdateService.this.filesave.exists()) {
					UpdateService.this.filesave.createNewFile();
				}
				long lngFilesize = DownloadUpdateFile(UpdateService.this.filepath, UpdateService.this.filesave);
				if (lngFilesize > 0) {
					message.what = DOWNLOAD_COMPLETE;
					UpdateService.this.updateHandler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				UpdateService.this.updateHandler.sendMessage(message);
			}
		}
	}

	public long DownloadUpdateFile(String fileurl, File fileto) throws Exception {
		int intDisplaySize = 0;
		long lngCurrentSize = 0;
		int intTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(fileurl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("User-Agent", this.headerUserAgent);
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			intTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(fileto, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				lngCurrentSize += readsize;
				if (intDisplaySize == 0 || (int) (lngCurrentSize * 100 / intTotalSize) - 1 > intDisplaySize) {
					intDisplaySize += 1;
					this.notification.setLatestEventInfo(UpdateService.this, Cms.APP.getString(R.string.app_name), Cms.APP.getString(R.string.update_notification_downloading) + " " + (int) (lngCurrentSize * 100 / intTotalSize) + "%", pi);
					this.nm.notify(0, notification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return lngCurrentSize;
	}

}