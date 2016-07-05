package com.example.image;

import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.example.utils.CtLog;
import com.example.utils.FilePath;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitmapManager {
	private static HashMap<String, SoftReference<Bitmap>> cache; // 图片缓存是当有加载过相同的图片的时候，可以快速重复使用，比如同一个人的头像。
	private static ExecutorService pool; // 固定线程池里的并发线程数，可以防止用户在快速滑动列表的时候，不执行已经滑过去的加载线程。
	private static Map<ImageView, String> imageViews;
	private Bitmap defaultBmp;
	private static BitmapManager instance = null;

	static {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(5); // 固定线程池
		imageViews = Collections
				.synchronizedMap(new WeakHashMap<ImageView, String>());
	}

	private BitmapManager() {
	}

	private BitmapManager(Bitmap def) {
		this.defaultBmp = def;
	}

	public static BitmapManager getInstance() {
		if (instance == null) {
			instance = new BitmapManager();
		}
		return instance;
	}

	/**
	 * 设置默认图片
	 *
	 * @param bmp
	 */
	public void setDefaultBmp(Bitmap bmp) {
		defaultBmp = bmp;
	}

	/**
	 * 加载图片
	 *
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, ImageView imageView) {
		loadBitmap(url, imageView, this.defaultBmp, 0, 0);
	}

	/**
	 * 加载图片-可设置加载失败后显示的默认图片
	 *
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
		loadBitmap(url, imageView, defaultBmp, 0, 0);
	}

	// 圆角处理
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 加载圆角图片
	 *
	 */

	public void loadRoundBitmap(String url, ImageView imageView,
			Bitmap defaultBmp, int width, int height) {
		CtLog.d("image", "url = " + url);
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {
			// 显示缓存图片
			bitmap = getRoundedCornerBitmap(bitmap, 1.5f);
			imageView.setImageBitmap(bitmap);
		} else {
			// 加载SD卡中的图片缓
			String filename = FilePath.getInstance().getFileName(url);
			FilePath.getInstance().isExists(FilePath.PORTRAIT);
			String filepath = FilePath.PORTRAIT + "/" + filename;
			File file = new File(filepath);
			if (file.exists()) {
				// 显示SD卡中的图片缓
				try {
					FileInputStream fis = new FileInputStream(file);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bmp = BitmapFactory.decodeStream(fis, null, options);
					if (bmp == null) {
						if (defaultBmp != null) {
							defaultBmp = getRoundedCornerBitmap(defaultBmp,
									1.5f);
							imageView.setImageBitmap(defaultBmp);
						}
					} else {
						bmp = getRoundedCornerBitmap(bmp, 1.5f);
						imageView.setImageBitmap(bmp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// 线程加载网络图片
				defaultBmp = getRoundedCornerBitmap(defaultBmp, 1.5f);
				imageView.setImageBitmap(defaultBmp);
				queueJob(url, file, imageView);
			}
		}
	}

	/**
	 * 加载图片-可指定显示图片的高宽
	 * 根据图片的url地址，先从图片缓存里面查找是否已缓存过，如果没有，再从SD卡的图片缓存文件中查找，如果再没有，最后才是加载网络图片
	 *
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp,
			int width, int height) {
		CtLog.d("image", "url = " + url);
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {
			// 显示缓存图片
			imageView.setImageBitmap(bitmap);
		} else {
			// 加载SD卡中的图片缓
			String filename = FilePath.getInstance().getFileName(url);
			FilePath.getInstance().isExists(FilePath.PORTRAIT);
			String filepath = FilePath.PORTRAIT + "/" + filename;

			File file = new File(filepath);

			if (file.exists()) {
				// 显示SD卡中的图片缓

				try {
					FileInputStream fis = new FileInputStream(file);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bmp = BitmapFactory.decodeStream(fis, null, options);
					if (bmp == null) {
						if (defaultBmp != null) {
							imageView.setImageBitmap(defaultBmp);
						}
					} else {
						imageView.setImageBitmap(bmp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				// 线程加载网络图片
				imageView.setImageBitmap(defaultBmp);
				queueJob(url, file, imageView);
			}
		}
	}

	/**
	 * 根据url读取图片
	 *
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapByUrl(String url) {
		Bitmap bitmap = null;
		bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			// 加载SD卡中的图片缓
			String filename = FilePath.getInstance().getFileName(url);
			FilePath.getInstance().isExists(FilePath.PICTURE);
			String filepath = FilePath.PICTURE + "/" + filename;
			File file = new File(filepath);
			if (file.exists()) {
				// 显示SD卡中的图片缓
				try {
					FileInputStream fis = new FileInputStream(file);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					bitmap = BitmapFactory.decodeStream(fis, null, options);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// 线程加载网络图片
				bitmap = downloadBitmap(url, file);
			}
		}
		return bitmap;
	}


	/**
	 * 根据url读取缓存图片
	 *
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapCacheByUrl(String url) {
		Bitmap bitmap = null;
		bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			// 加载SD卡中的图片缓
			String filename = FilePath.getInstance().getFileName(url);
			FilePath.getInstance().isExists(FilePath.PICTURE);
			String filepath = FilePath.PICTURE + "/" + filename;
			File file = new File(filepath);
			if (file.exists()) {
				// 显示SD卡中的图片缓
				try {
					FileInputStream fis = new FileInputStream(file);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					bitmap = BitmapFactory.decodeStream(fis, null, options);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// 缓存不存在返回空
				bitmap = null;
			}
		}
		return bitmap;
	}

	/**
	 * 从缓存中获取图片
	 *
	 * @param url
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		if (cache.containsKey(url)) {
			bitmap = cache.get(url).get();
		}
		return bitmap;
	}

	/**
	 * 从网络中加载图片
	 *
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void queueJob(final String url, final File file,
			final ImageView imageView) {

		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
					}
				}
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = Message.obtain();
				message.obj = downloadBitmap(url, file);
				handler.sendMessage(message);
			}
		}).start();

	}

	public File downloadBitmapFile(String path, File file) {
		Bitmap bitmap = null;
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);

				byte[] buffer = new byte[1024];

				int len = 0;

				while ((len = is.read(buffer)) != -1) {

					fos.write(buffer, 0, len);

				}

				is.close();

				fos.close();

				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 下载图片-可指定显示图片的高宽
	 *
	 * @param url
	 * @param width
	 * @param height
	 */
	private Bitmap downloadBitmap(String path, File file) {
		Bitmap bitmap = null;
		try {
			// http加载图片
			HttpGet httpGet = new HttpGet(path);
			httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
			HttpConnectionParams.setSoTimeout(params, 30 * 1000);
			HttpResponse httpresponse = httpclient.execute(httpGet);
			CtLog.d("client", "getStatusCode = "
					+ httpresponse.getStatusLine().getStatusCode());

			if (httpresponse.getStatusLine().getStatusCode() == 200) {
				Log.i("cccc", "------图片地址状态0000");
				HttpEntity entity = httpresponse.getEntity();
				InputStream is = entity.getContent();
				Log.i("cccc",
						"------图片地址状态33333"
								+ Environment.getExternalStorageDirectory());
				// Environment.getExternalStorageDirectory();
				FileOutputStream fos = new FileOutputStream(file);
				Log.i("cccc", "------图片地址状态111111");
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}

				is.close();
				fos.close();
				Log.i("cccc", "------图片地址状态2222");
				// 返回一个URI对象
				FileInputStream fa = new FileInputStream(file.toString());
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				bitmap = BitmapFactory.decodeStream(fa, null, options);

				// 放入缓存
				cache.put(path, new SoftReference<Bitmap>(bitmap));
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
