package com.wyj.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import com.wyj.pipe.Cms;
import com.wyj.pipe.Utils;
import com.wyj.utils.FilePath;
import com.wyj.utils.MD5;

import android.app.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Imageviewpager extends Activity implements OnClickListener{
	/**
	 * ViewPager
	 */
	private ViewPager viewPager;
	
	/**
	 * 装点点的ImageView数组
	 */
	private ImageView[] tips;
	
	/**
	 * 装ImageView数组
	 */
	private ImageView[] mImageViews;
	
	/**
	 * 图片资源id
	 */
	private String[] imgIdArray ;
	
	private ProgressBar loading;
	private TextView OrderviewBack,Orderthumbsave;
	private int defaut_position=0;
	private String default_image_url="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageviewpager);
		ViewGroup group = (ViewGroup)findViewById(R.id.OrderviewGroup);
		viewPager = (ViewPager) findViewById(R.id.OrderviewPager);
		
		loading =(ProgressBar) findViewById(R.id.OrderImageloading);
		OrderviewBack =(TextView) findViewById(R.id.OrderviewBack);
		
		OrderviewBack.setOnClickListener(this);
		Orderthumbsave =(TextView) findViewById(R.id.Orderthumbsave);
		
		Orderthumbsave.setOnClickListener(this);
		
		
		Bundle bundle = this.getIntent().getExtras();
		imgIdArray=bundle.getStringArray("thumbs");
		defaut_position= bundle.getInt("position");
		
		if(null != bundle && imgIdArray!=null ){
			
			Log.i("aaaa", "-------viewpager传过来的位置"+defaut_position);
			//将点点加入到ViewGroup中
			tips = new ImageView[imgIdArray.length];
			for(int i=0; i<tips.length; i++){
				ImageView imageView = new ImageView(this);
		    	imageView.setLayoutParams(new LayoutParams(20,20));
		    	imageView.setPadding(0, 0, 10, 0);
		    	tips[i] = imageView;
		    	if(i == 0){
		    		tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
		    	}else{
		    		tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
		    	}
		    	
		    	 group.addView(imageView);
			}
			
			mImageViews = new ImageView[imgIdArray.length];
			for(int i=0; i<mImageViews.length; i++){
				ImageView imageView = new ImageView(this);
				
				//String path = imgIdArray[i];
				imageView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imageView.setOnClickListener(Imageviewpager.this);
				imageView.setTag("");
				mImageViews[i] = imageView;
			}
			
			default_image_url=imgIdArray[defaut_position];
			Cms.imageLoader.displayImage(imgIdArray[defaut_position], mImageViews[defaut_position], Cms.imageLoaderOptions, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					loading.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					loading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					loading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					loading.setVisibility(View.GONE);
				}
			});
			
			mImageViews[defaut_position].setTag(imgIdArray[defaut_position]);
			
			//设置Adapter
			viewPager.setAdapter(new MyAdapter());
			//设置监听，主要是设置点点的背景
			viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
			//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
			
			viewPager.setOffscreenPageLimit(imgIdArray.length);
			
			viewPager.setCurrentItem(defaut_position);
		}		
	}
	
	/**
	 * 
	 * @author xiaanming
	 *
	 */
	public class MyAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return imgIdArray.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);
			//((ViewPager) container).removeView((ImageView) object);
			
		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(View container, int position) {
			
			Log.i("aaaa", "------适配器-viewpager位置----"+position);
		
			((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0);
			return mImageViews[position % mImageViews.length];
		}		
	}

	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			setImageBackground(position % imgIdArray.length);
			default_image_url=imgIdArray[position];
			if(mImageViews[position].getTag().equals("")){
				Cms.imageLoader.displayImage(imgIdArray[position], mImageViews[position], Cms.imageLoaderOptions, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						loading.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						loading.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						loading.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						loading.setVisibility(View.GONE);
					}
				});
				mImageViews[position].setTag(imgIdArray[position]);
			}
			
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	/**
	 * 设置选中的tip的背景
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems){
		for(int i=0; i<tips.length; i++){
			if(i == selectItems){
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			}else{
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int sender = v.getId();
		
		Log.i("aaaa", "-------viewpager点击---"+sender);
		if (R.id.OrderviewBack == sender) {
			this.finish();
		}
		if (R.id.Orderthumbsave == sender) {
			
		String path=Cms.imageLoader.getDiscCache().get(default_image_url).getPath();
			//default_image_url
		
		copyFile(path,FilePath.ROOT_DIRECTORY+MD5.getMD5(path)+"3333.jpg");
		Utils.ShowToast(Imageviewpager.this, "保存成功");
		Log.i("aaaa", "当前的图片是----"+path);
		}
		
		
	}
	
	/**  
     * 复制单个文件  
     * @param oldPath String 原文件路径 如：c:/fqf.txt  
     * @param newPath String 复制后路径 如：f:/fqf.txt  
     * @return boolean  
     */   
   public void copyFile(String oldPath, String newPath) {   
       try {   
           int bytesum = 0;   
           int byteread = 0;   
           File oldfile = new File(oldPath);   
           if (oldfile.exists()) { //文件存在时   
               InputStream inStream = new FileInputStream(oldPath); //读入原文件   
               FileOutputStream fs = new FileOutputStream(newPath);   
               byte[] buffer = new byte[1444];   
               int length;   
               while ( (byteread = inStream.read(buffer)) != -1) {   
                   bytesum += byteread; //字节数 文件大小   
                   System.out.println(bytesum);   
                   fs.write(buffer, 0, byteread);   
               }   
               inStream.close();   
           }   
       }   
       catch (Exception e) {   
           System.out.println("复制单个文件操作出错");   
           e.printStackTrace();   
  
       }   
  
   }  

}
