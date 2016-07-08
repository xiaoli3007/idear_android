package com.example.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import android.widget.*;

import com.example.cms.BaseFragment;
import com.example.cms.Consts;
import com.example.cms.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.popupwindow.MyPopupWindowsBirthDayDate;
import com.example.popupwindow.MyPopupWindowsCity;
import com.example.popupwindow.MyPopupWindowsDate;
import com.example.popupwindow.MyPopupWindowsHomeSelectSingle;
import com.example.services.HttpClientHelper;
import com.example.utils.JsonString;
import com.example.utils.Utils;
import com.example.widget.MyViewPager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


public class Home extends BaseFragment {

	private TextView home_single_select,home_double_select,home_three_select,home_select_birthday;

	private ViewPager home_slide;

	private LinearLayout home_slide_group,home_slide_group_url;
	/** 存放viewPager的list */
	private List<ImageView> viewList,viewList_url;
	/** viewpager的适配器 */
	private MyAdvertisementAdapter_url MyAdvertisementAdapter_url;
	private MyViewPager home_side_url;
	/** 全局变量之图片缓存map */
	protected Map<String, Bitmap> cacheImageMap;
	/**
	 * 点点数组
	 */
	private ImageView[] tips,tips_url;

	private int[] imgIds = new int[] { R.drawable.temp1, R.drawable.temp2,
			R.drawable.temp3,R.drawable.temp4,R.drawable.temp3 };

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.home, null);

			this.home_single_select =(TextView) view.findViewById(R.id.home_single_select);
			this.home_double_select =(TextView) view.findViewById(R.id.home_double_select);
			this.home_three_select =(TextView) view.findViewById(R.id.home_three_select);
			this.home_select_birthday =(TextView) view.findViewById(R.id.home_select_birthday);

			this.home_single_select.setOnClickListener(this);
			this.home_double_select.setOnClickListener(this);
			this.home_three_select.setOnClickListener(this);
			this.home_select_birthday.setOnClickListener(this);

			this.home_slide=(ViewPager) view.findViewById(R.id.home_slide);

			this.home_slide_group=(LinearLayout) view.findViewById(R.id.home_slide_group);

			this.home_side_url=(MyViewPager) view.findViewById(R.id.home_slide_url);
			this.home_slide_group_url=(LinearLayout) view.findViewById(R.id.home_slide_group_url);

			this.cacheImageMap = new HashMap<String, Bitmap>();
			return view;
		}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);

		//图片形式一  直接是本地的图片======================================================================


		viewList = new ArrayList<ImageView>();

		tips = new ImageView[5];
		for (int i = 0; i < 5; i++) {
			ImageView mImageView = new ImageView(getActivity());
			tips[i] = mImageView;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT));
			layoutParams.rightMargin = 3;
			layoutParams.leftMargin = 3;

			mImageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
			home_slide_group.addView(mImageView, layoutParams);

			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(imgIds[i]);
			imageView.setOnClickListener(this);
			viewList.add(imageView);
		}

		this.home_slide.setAdapter(new MyAdvertisementAdapter_loacal(viewList));
		this.home_slide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {

			}

			@Override
			public void onPageSelected(int i) {
				setImageBackground(i);
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
      //=======================================================================================================


		viewList_url = new ArrayList<ImageView>();



		this.MyAdvertisementAdapter_url =new MyAdvertisementAdapter_url(viewList_url);
		this.home_side_url.setAdapter(new MyAdvertisementAdapter_url(viewList_url));
		this.home_side_url.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {

			}

			@Override
			public void onPageSelected(int i) {
				setImageBackground_url(i);
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});

		//=================================================================请求开始

		new MyImageTask(getActivity(),"GET",1).execute(Consts.URI_LIST_INFO);	//获取幻灯片的数据

	}


	/**
	 * 设置选中的tip的背景
	 *
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}

	private void setImageBackground_url(int selectItems) {
		for (int i = 0; i < tips_url.length; i++) {
			if (i == selectItems) {
				tips_url[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips_url[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}
	/**
	 * 实现左右滑动广告
	 *
	 * @author
	 *
	 */
	class MyAdvertisementAdapter_loacal extends PagerAdapter {
		private List<ImageView> list = null;

		public MyAdvertisementAdapter_loacal(List<ImageView> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position));// 每一个item实例化对象
			return list.get(position);
		}
	}


	class MyAdvertisementAdapter_url extends PagerAdapter {
		private List<ImageView> list = null;

		public MyAdvertisementAdapter_url(List<ImageView> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position));// 每一个item实例化对象
			return list.get(position);
		}
	}

	// 需要先继承AsyncTask接口，第一个参数是执行路径，第二个是进度，第三个是返回值
	private class MyImageTask extends AsyncTask<String, Void, Object> {
		private  ProgressDialog pDialog;
		private String methodString;	//看请求是GET 还是POST
		private int flag;
		private  ImageView image;
		private  String ImageUrl;
		private  int position;

		public MyImageTask(Context context ,String methodString ,int flag){
			this.methodString=methodString;
			this.flag=flag; //请求标识  看是请求的哪个东西
//			pDialog = new ProgressDialog(context);
//			pDialog.setMessage("加载中。。。");
		}

		public MyImageTask(Context context ,String methodString ,int flag,ImageView image,String ImageUrl,int position){
			this.methodString=methodString;
			this.flag=flag; //请求标识  看是请求的哪个东西
			this.image=image;
			this.ImageUrl=ImageUrl;
			this.position=position;
//			pDialog = new ProgressDialog(context);
//			pDialog.setMessage("加载中。。。");
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		//	pDialog.show();
		}
		@Override
		protected Object doInBackground(String... strings) {
			Object obj = null;
			if(flag==1){
				String data = null;
				if (methodString.equals("GET")) {

					data = HttpClientHelper.httpUrl(strings[0]);

				} else {
					// data = HttpClientHelper.doPostSubmit(params[0], map);
				}
				if (data != null) {
					obj=data;
				} else {
					System.out.println("没有数据！！");
					return null;
				}
			}else if(flag==2){

				byte[] data=HttpClientHelper.loadByteFromURL(strings[0]);

				obj =data;
			}else{

			}

		 return obj;

		}

		@Override
		protected void onPostExecute(Object obj) {
			super.onPostExecute(obj);
			if (obj == null) {
				return;
			}
			if(flag ==1){

				String result2 = (String) obj;
				try{
					JSONObject result = new JSONObject(result2);
					if (result.optString("code", "").equals("succeed")) {
						JSONArray d=result.getJSONArray("data");

						make_home_side_images_json(d);	//处理json数据  把每一张图片异步加载出来
						Log.i("aaa", "======请求的数据"+d.length());
					} else {
						Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
					}
				}catch (JSONException e){
					e.printStackTrace();
				}
				Log.i("aaa", "======请求的数据"+result2);

			}else if(flag ==2){

				getBitmap(ImageUrl, (byte[]) obj, position);

			}else{

			}

//			pDialog.dismiss();

		}
	}


	/**
	 * 获取字节数组，转成bitmap图片显示
	 *
	 * @param urlStr
	 * @param result
	 * @param i
	 * @return
	 */
	public Bitmap getBitmap(String urlStr, byte[] result, int i) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
		ImageView imageView = (ImageView) viewList_url.get(i);



		imageView.setImageBitmap(bitmap);



		cacheImageMap.put(urlStr, bitmap);
		MyAdvertisementAdapter_url.notifyDataSetChanged();

		return bitmap;
	}

	public  void  make_home_side_images_json(JSONArray d){

		tips_url = new ImageView[d.length()];
		for (int i = 0; i < d.length(); i++) {
			ImageView mImageView = new ImageView(getActivity());
			tips_url[i] = mImageView;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT));
			layoutParams.rightMargin = 3;
			layoutParams.leftMargin = 3;

			mImageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
			home_slide_group_url.addView(mImageView, layoutParams);

			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setImageResource(imgIds[i]);	//默认的五张图片
			imageView.setOnClickListener(this);

			viewList_url.add(imageView);
		}

		for (int i=0;i<d.length();i++){
			JSONObject  item=d.optJSONObject(i);

			int id=item.optInt("id");
			String urlStr=item.optString("thumb");
			ImageView imageView = (ImageView) viewList_url.get(i);
			imageView.setTag(id);

			if(item.optString("thumb") !=""){

				if (cacheImageMap.get(urlStr) == null) {
					new MyImageTask(getActivity(),"GET",2,imageView,urlStr,i).execute(urlStr);// 加载每一张图片
				} else {
					imageView.setImageBitmap(cacheImageMap.get(urlStr));
					MyAdvertisementAdapter_url.notifyDataSetChanged();	//更新适配器
				}

			}

		}
	}

	@Override
	public void onClick(View v) {

		if (v == this.home_single_select) {

		//	Utils.hideKeyboard(getActivity()); 隐藏键盘
			try{
				JSONObject result = new JSONObject(JsonString.get_home_single_select());

				new MyPopupWindowsHomeSelectSingle(getActivity(),
						home_single_select, getActivity(), result.getJSONArray("wishgradeinfo"));
			} catch (JSONException e){
				e.printStackTrace();
			}

		}

		if (v == this.home_double_select) {

			try{
				JSONArray result = new JSONArray(Utils.getRegions());
				new MyPopupWindowsCity(getActivity(),
						home_double_select, getActivity(), result);
			} catch (JSONException e){
				e.printStackTrace();
			}

		}

		if (v == this.home_three_select) {

				new MyPopupWindowsDate(getActivity(),
						home_three_select, getActivity());
		}

		if (v == this.home_select_birthday) {

			new MyPopupWindowsBirthDayDate(getActivity(),
					home_select_birthday, getActivity(),new MyPopupWindowsBirthDayDate.OnSelectListener() {

				@Override
				public void OnSelect(String result, int type) {
					// TODO Auto-generated method stub
					Log.i("aaaa", "回调日期--------type看是阴历还是阳历---------" + result
							+ "----" + type);

					home_select_birthday.setText(result);

				}
			});
		}

	}







}