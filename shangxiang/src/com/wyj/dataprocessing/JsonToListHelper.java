package com.wyj.dataprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonToListHelper {
	
	
	public static List<Map<String, Object>> jsonToList(String jsonString,String key) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray_datas = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray_datas.length(); i++) {
				JSONObject jsonObject_data = jsonArray_datas.getJSONObject(i);
				map = new HashMap<String, Object>();
				map.put("id", jsonObject_data.getString("id"));
				map.put("title", jsonObject_data.getString("title"));
				map.put("source", jsonObject_data.getString("source"));
				map.put("wap_thumb", jsonObject_data.getString("wap_thumb"));
				map.put("create_time", jsonObject_data.getString("create_time"));
				map.put("nickname", jsonObject_data.getString("nickname"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Map<String, Object>> gettemplelist_json(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray_datas = jsonObject.getJSONArray("templelist");

			for (int i = 0; i < jsonArray_datas.length(); i++) {
				JSONObject jsonObject_data = jsonArray_datas.getJSONObject(i);
				map = new HashMap<String, Object>();
				map.put("templeid", jsonObject_data.getInt("templeid"));
				map.put("templename", jsonObject_data.getString("templename"));
				map.put("province", jsonObject_data.getString("province"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Map<String, Object>> orderlist_json(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray_datas = jsonObject.getJSONArray("orderlist");

			for (int i = 0; i < jsonArray_datas.length(); i++) {
				JSONObject jsonObject_data = jsonArray_datas.getJSONObject(i);
				map = new HashMap<String, Object>();
				map.put("orderid", jsonObject_data.getInt("orderid"));
				map.put("wishname", jsonObject_data.getString("wishname"));
				map.put("wishtext", jsonObject_data.getString("wishtext"));
				map.put("truename", jsonObject_data.getString("truename"));
				map.put("templename", jsonObject_data.getString("templename"));
				map.put("alsowish", jsonObject_data.getString("alsowish"));
				map.put("wishtype", jsonObject_data.getString("wishtype"));
				map.put("name_blessings", jsonObject_data.getString("name_blessings"));
				map.put("co_blessings", jsonObject_data.getInt("co_blessings"));
				map.put("bleuser", jsonObject_data.getString("bleuser"));
				// "http://v1.qzone.cc/avatar/201402/03/12/16/52ef1800d3656616.jpg%21200x200.jpg");
				map.put("headface", jsonObject_data.getString("headface"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Map<String, Object> orderdetail_orderinfo(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject jsonObject_data = jsonObject.getJSONObject("orderinfo");

			map = new HashMap<String, Object>();
			map.put("wishname", jsonObject_data.getString("wishname"));
			map.put("wishtype", jsonObject_data.getString("wishtype"));
			map.put("wishtext", jsonObject_data.getString("wishtext"));
			map.put("templename", jsonObject_data.getString("templename"));
			map.put("blessingser", jsonObject_data.getString("blessingser"));
			map.put("headface", jsonObject_data.getString("headface"));
			// list.add(map);

		} catch (Exception e) {
			e.printStackTrace();
			Log.i("json", "==err");
		}
		return map;
	}

	public static List<Map<String, Object>> orderdetail_memberlist_json(
			String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray_datas = jsonObject
					.getJSONArray("blessings_members");

			for (int i = 0; i < jsonArray_datas.length(); i++) {
				JSONObject jsonObject_data = jsonArray_datas.getJSONObject(i);
				map = new HashMap<String, Object>();
				map.put("time_diff", jsonObject_data.getString("time_diff"));
				map.put("cn_retime", jsonObject_data.getString("cn_retime"));
				map.put("nickname", jsonObject_data.getString("nickname"));
				map.put("truename", jsonObject_data.getString("truename"));
				// map.put("headface",
				// "http://b.hiphotos.baidu.com/image/pic/item/b3fb43166d224f4a611cb3150af790529822d12b.jpg");
				map.put("headface", jsonObject_data.getString("headface"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Map<String, Object> jsontoCode(String jsonString) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			map.put("code", jsonObject.getString("code"));
			map.put("msg", jsonObject.getString("msg"));
			// Log.i("JsonToListHelper", "=="+code);
			// if (code.equals("succeed")) {
			// return true;
			// }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public static Map<String, Object> jsontoUsername(String jsonString) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			map.put("username", jsonObject.getString("username"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public static Map<String, Object> jsontosingle(String jsonString, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			JSONObject infoObject = new JSONObject(jsonObject.getString(key));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
