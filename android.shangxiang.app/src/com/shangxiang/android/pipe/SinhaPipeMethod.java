package com.shangxiang.android.pipe;

import org.apache.http.HttpResponse;

import android.os.Handler;
import android.os.Looper;

public class SinhaPipeMethod extends Thread {
	private Handler handler;
	private SinhaPipeClient sinhaPipeClient;
	private int type;
	private MethodCallback callback;

	public SinhaPipeMethod(SinhaPipeClient sinhaPipeClient, int type, MethodCallback callback) {
		this.sinhaPipeClient = sinhaPipeClient;
		this.type = type;
		this.callback = callback;
		this.handler = new Handler();
	}

	public SinhaPipeMethod(SinhaPipeClient sinhaPipeClient, MethodCallback callback) {
		this(sinhaPipeClient, 0, callback);
	}

	@Override
	public void run() {
		Looper.prepare();
		try {
			Object result = null;
			switch (this.type) {
			case 1:
				result = this.sinhaPipeClient.ToBitmap((HttpResponse) this.sinhaPipeClient.Call());
				break;
			default:
				result = this.sinhaPipeClient.ToString((HttpResponse) this.sinhaPipeClient.Call());
				break;
			}
			final Object resultConv = result;
			this.handler.post(new Runnable() {
				public void run() {
					callback.CallFinished(null, resultConv);
				}
			});
		} catch (final SinhaPipeException e) {
			this.handler.post(new Runnable() {
				public void run() {
					callback.CallFinished("" + e, null);
				}
			});
		}
	}

	public interface MethodCallback {
		void CallFinished(String err, Object result);
	}
}