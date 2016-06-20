package com.example.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.example.listphoto.ImageHolder;
import com.example.listphoto.ListImageDirPopupWindow;
import com.example.listphoto.MyAdapter;
import com.example.listphoto.ListImageDirPopupWindow.OnImageDirSelected;
import com.example.utils.Utils;
import com.example.cms.R;
import com.example.cms.Cms;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

public class PickPhoto extends Activity implements OnImageDirSelected, OnClickListener {
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private boolean showLoading = false;
	private GridView viewImages;
	private TextView textPathname;
	private TextView textTotal;
	private RelativeLayout buttonChooseFolder;
	
	private HashSet<String> pathFolders = new HashSet<String>();
	private List<ImageHolder> imageHolders = new ArrayList<ImageHolder>();
	private File topFolder;
	private List<String> pathImages;
	private int totalAllCount = 0;
	private int totalFolderCount;

	private MyAdapter mAdapter;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler handle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			showLoading();
			initPhotos();
			initListDirPopupWindw();
		}
	};

	// TODO 完成图片选择器
	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.pick_photo);

		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
		this.buttonBack = (Button) findViewById(R.id.pick_photo_title_back_button);
		this.buttonBack.setOnClickListener(this);
		
		this.viewImages = (GridView) findViewById(R.id.pick_photo_list_view);
		this.textPathname = (TextView) findViewById(R.id.pick_photo_footer_pathname_text);
		this.textTotal = (TextView) findViewById(R.id.pick_photo_footer_total_text);
		this.buttonChooseFolder = (RelativeLayout) findViewById(R.id.pick_photo_footer_layout);
		this.buttonChooseFolder.setOnClickListener(this);

		loadPhotos();
	}

	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadPhotos() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}

		showLoading();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String pathFirstImage = null;
				Uri uriMediaStore = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = PickPhoto.this.getContentResolver();

				Cursor cursor = cr.query(uriMediaStore, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				while (cursor.moveToNext()) {
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					if (pathFirstImage == null) {
						pathFirstImage = path;
					}
					File fileParent = new File(path).getParentFile();
					if (fileParent == null) {
						continue;
					}
					String pathParent = fileParent.getAbsolutePath();
					ImageHolder imageHolder = null;
					if (pathFolders.contains(pathParent)) {
						continue;
					} else {
						pathFolders.add(pathParent);
						imageHolder = new ImageHolder();
						imageHolder.setDir(pathParent);
						imageHolder.setFirstImagePath(path);
					}

					int sizePathPhotos = fileParent.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					imageHolder.setCount(sizePathPhotos);
					imageHolders.add(imageHolder);
					totalAllCount += sizePathPhotos;

					if (sizePathPhotos > totalFolderCount) {
						totalFolderCount = sizePathPhotos;
						topFolder = fileParent;
					}
				}
				cursor.close();
				pathFolders = null;
				handle.sendEmptyMessage(0x110);
			}
		}).start();
	}

	private void initPhotos() {
		if (this.topFolder == null) {
			return;
		}

		this.pathImages = Arrays.asList(this.topFolder.list());
		mAdapter = new MyAdapter(getApplicationContext(), pathImages, R.layout.grid_item, topFolder.getAbsolutePath());
		viewImages.setAdapter(mAdapter);
		textTotal.setText(totalCount + "张");
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), imageHolders, LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	public void selected(ImageHolder floder) {

		topFolder = new File(floder.getDir());
		pathImages = Arrays.asList(topFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), pathImages, R.layout.grid_item, topFolder.getAbsolutePath());
		viewImages.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		textTotal.setText(floder.getCount() + "张");
		textPathname.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			PickPhoto.this.finish();
//			overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		}
		if (v == this.buttonBack) {
//			mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
			mListImageDirPopupWindow.showAsDropDown(buttonChooseFolder, 0, 0);

			// 设置背景颜色变暗
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha = .3f;
			getWindow().setAttributes(lp);
		}
	}
}