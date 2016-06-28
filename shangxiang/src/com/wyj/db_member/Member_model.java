package com.wyj.db_member;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Member_model {

	private Member_DBHelper member_helper;

	public Member_model(Context context) {

		member_helper = new Member_DBHelper(context);

	}

	public void Member_insert(String username, String passwd, String regtime) {

		SQLiteDatabase db = null;
		db = member_helper.getReadableDatabase();
		String insert_sql = "insert into members(username,passwd,regtime) values(?,?,?)";
		db.execSQL(insert_sql, new Object[] { username, passwd, regtime });
		db.close();
	}

	/**
	 * @param db
	 * @return 验证用户密码
	 */
	public boolean checkMember(String reg_username, String reg_passwd) {
		MemberDefine memberinfo = null;
		memberinfo = findByUsername(reg_username);
		// Log.i("aaaa","------错误是什么-"+memberinfo.getUsername());
		if (memberinfo.getUsername() != null) {
			if (!memberinfo.getPasswd().equals(reg_passwd)) {

				return false;
			} else {

				return true;
			}
		} else {
			return false;
		}

	}

	public MemberDefine findByUsername(String reg_username) {
		MemberDefine memberinfo = new MemberDefine();
		SQLiteDatabase db = null;
		db = member_helper.getReadableDatabase();

		String sql = "SELECT * FROM members where username=?";
		// 查询
		Cursor cursor = db.rawQuery(sql, new String[] { "" + reg_username });
		// 判断是否含有下一个
		if (cursor.moveToNext()) {
			// 为对象的属性赋值
			memberinfo.setMemberID(cursor.getInt(cursor
					.getColumnIndex("memberID")));
			memberinfo.setUsername(cursor.getString(cursor
					.getColumnIndex("username")));
			memberinfo.setPasswd(cursor.getString(cursor
					.getColumnIndex("passwd")));

		}

		return memberinfo;
	}

}
