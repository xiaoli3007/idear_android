package com.shangxiang.android.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.shangxiang.android.R;
import com.shangxiang.android.Consts;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.activity.Navigation;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

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
	public final static int START_DOWNLOAD = 0;
	public final static int DOWNLOAD_COMPLETE = 1;
	public final static int DOWNLOAD_FAIL = 2;
	public final static int CANCEL_UPDATE = 99;
	private Intent intent = null;
	private String filepath = null;
	private File filesave = null;
	private NotificationManager nm = null;
	private Notification notification = null;
	private PendingIntent pi = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.intent = new Intent(ShangXiang.APP, Navigation.class);
		ShangXiang.updateServiceHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case START_DOWNLOAD:
					startNotification();
					break;
				case DOWNLOAD_COMPLETE:
					completeNotification();
					break;
				case DOWNLOAD_FAIL:
					failNotification();
					break;
				default:
					stopService(intent);
					break;
				}
			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		checkVersion();
		return super.onStartCommand(intent, flags, startId);
	}

	private void checkVersion() {
		if (Utils.CheckNetwork()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("apptype", "android"));

			SinhaPipeClient httpClient = new SinhaPipeClient();
			httpClient.Config("get", Consts.URI_CHECK_VERSION, params, true);
			SinhaPipeMethod httpMethod = new SinhaPipeMethod(httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					if (null == error) {
						checkVersion((String) result);
					}
				}
			});
			httpMethod.start();
		}
	}

	private void checkVersion(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					String version = result.optString("version", "");
					if (!ShangXiang.VERSION.trim().equalsIgnoreCase(version.trim())) {
						this.filepath = result.optString("downloadurl", "");
						this.filesave = new File(Consts.UPDATE_LOCAL, "ShangXiang.apk");
						Message message = ShangXiang.updateHandler.obtainMessage();
						ShangXiang.updateHandler.sendMessage(message);
					} else {
						stopService(this.intent);
					}
				} else {
					stopService(this.intent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				stopService(this.intent);
			}
		} else {
			stopService(this.intent);
		}
	}

	private void startNotification() {
		try {
			this.nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			this.notification = new Notification();
			this.pi = PendingIntent.getActivity(ShangXiang.APP, 0, this.intent, 0);
			this.notification.icon = R.drawable.app_icon;
			this.notification.tickerText = getString(R.string.update_ticker_title);
			this.notification.setLatestEventInfo(ShangXiang.APP, getString(R.string.app_name), getString(R.string.update_notification_ready), this.pi);
			this.nm.notify(0, this.notification);
			new Thread(new UpdateRunnable()).start();
		} catch (Exception e) {
		}
	}

	private void completeNotification() {
		try {
			this.notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Uri uri = Uri.fromFile(this.filesave);
			Intent intentInstall = new Intent(Intent.ACTION_VIEW);
			intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
			this.pi = PendingIntent.getActivity(ShangXiang.APP, 0, intentInstall, 0);
			this.notification.defaults = Notification.DEFAULT_SOUND;
			this.notification.setLatestEventInfo(ShangXiang.APP, getString(R.string.app_name), getString(R.string.update_notification_download_success), this.pi);
			this.nm.notify(0, this.notification);
			stopService(this.intent);
		} catch (Exception e) {
		}
	}

	private void failNotification() {
		try {
			this.notification.setLatestEventInfo(ShangXiang.APP, getString(R.string.app_name), getString(R.string.update_notification_download_error), this.pi);
			this.nm.notify(0, this.notification);
			stopService(this.intent);
		} catch (Exception e) {
		}
	}

	private void updateNotification(String percent) {
		try {
			this.notification.setLatestEventInfo(ShangXiang.APP, getString(R.string.app_name), getString(R.string.update_notification_downloading) + " " + percent, this.pi);
			this.nm.notify(0, this.notification);
		} catch (Exception e) {
		}
	}

	class UpdateRunnable implements Runnable {
		Message message = ShangXiang.updateServiceHandler.obtainMessage();

		public void run() {
			try {
				if (!filesave.exists()) {
					filesave.createNewFile();
				}
				long lngFilesize = DownloadUpdateFile(filepath, filesave);
				if (lngFilesize > 0) {
					message.what = DOWNLOAD_COMPLETE;
					ShangXiang.updateServiceHandler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = CANCEL_UPDATE;
				ShangXiang.updateServiceHandler.sendMessage(message);
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
					int size = (int) (lngCurrentSize * 100 / intTotalSize);
					if (0 == size % 5) {
						updateNotification(size + "%");
					}
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