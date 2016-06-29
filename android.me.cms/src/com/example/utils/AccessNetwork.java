package com.example.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.example.services.GetPostUtil;
import com.example.services.UploadPostUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AccessNetwork implements Runnable {
	private String op;
	private String url;
	private String params;
	private Handler h;
	private int requestcode;
	private String filepath;
	private String username;

	private Map<String, String> mapparams;
	private Map<String, File> files;

	public AccessNetwork(String op, String url, String params, Handler h) {
		super();
		this.op = op;
		this.url = url;
		this.params = params;
		this.h = h;
	}

	public AccessNetwork(String op, String url, String params, Handler h,
			int code) {
		super();
		this.op = op;
		this.url = url;
		this.params = params;
		this.requestcode = code;
		this.h = h;
	}

	public AccessNetwork(String op, String url, String filepath,
			String username, Handler h) {
		super();
		this.op = op;
		this.url = url;
		this.filepath = filepath;
		this.username = username;
		this.h = h;
	}

	public AccessNetwork(String op, String url, Map<String, String> mapparams,
			Map<String, File> files, Handler h) {
		super();
		this.op = op;
		this.url = url;
		this.mapparams = mapparams;
		this.files = files;
		this.h = h;
	}

	@Override
	public void run() {
		Message m = new Message();
		m.what = 0x123;
		if (op.equals("GET")) {

			Log.i("send", "发送GET请求");
			m.obj = GetPostUtil.sendGet(url, params);
			Log.i("send", ">>>>>>>>>>>>" + m.obj);
		}

		if (op.equals("POST")) {

			Log.i("send", "发送POST请求");
			m.obj = GetPostUtil.sendPost(url, params);
			Log.i("send", ">>>>>>>>>>>>" + m.obj);
		}
		if (op.equals("UPLOAD")) {

			Log.i("send", "发送UPLOAD请求");
			// m.obj = UploadPostUtil.uploadFile(url, filepath,username);
			try {
				m.obj = UploadPostUtil.post(url, mapparams, files);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("send", ">>>>>>>>>>>>" + m.obj);
		}
		h.sendMessage(m);
	}
}
