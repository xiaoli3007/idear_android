package com.example.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToListDemo {



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


}
