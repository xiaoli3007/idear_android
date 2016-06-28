package com.wyj.db_memberbirthday;


import java.util.List;
import java.util.Map;

import com.wyj.db.SQLiteDataBaseHelper;

import android.content.Context;
import android.util.Log;


public class MemberBirthday_model {

	private final String TAG = "MemberBirthday_model";
	/** 数据库操作 */
	SQLiteDataBaseHelper db;
	public MemberBirthday_model(Context context) {
		
		db = new SQLiteDataBaseHelper(context, "cms");
		
	}

	public boolean insert(MemberBirthday info) {

		String sql = "INSERT INTO member_birthday(mid,title,birthday_time,type,rtime) values (?,?,?,?,?)";
		boolean flag = db.updataData(sql, new String[] { info.getMid(), info.getTitle(),
				info.getBirthday_time(), info.getType(), info.getRtime() });
		
		
//		String sql = "INSERT INTO tb_words(english,chinese) values (?,?)";
//		boolean flag = db.updataData(sql, new String[] { "1111111","22222222"});
		
		Log.i(TAG, "生日插入返回flag==" + flag);
		return flag;
	}

	
	public boolean update(MemberBirthday info, String id) {

		
		String sql = "UPDATE member_birthday SET mid = ?,title = ?,birthday_time = ?,type = ?,rtime = ?  WHERE _id = ?";
		

		boolean flag = db.updataData(sql, new String[] { info.getMid(), info.getTitle(),
				info.getBirthday_time(), info.getType(), info.getRtime(), id });
		Log.i(TAG, "生日更新返回flag==" + flag);
		
		return flag;
	}
	
	public List<Map<String, String>> select( String id) {
		List<Map<String, String>> list=null;
		
		if(id.equals("")){
			String sql = "SELECT * FROM member_birthday ";
			list = db.SelectData(sql, null);
		}else{
			String sql = "SELECT * FROM member_birthday WHERE _id = ?";
			list = db.SelectData(sql, new String[]{id});
		}
		Log.i(TAG,"生日查询的值"+list.toString());
		
		return list;
	}
	

}
