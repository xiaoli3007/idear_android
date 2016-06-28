package com.wyj.db_member;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Member_DBHelper extends SQLiteOpenHelper {

	private static Context context;
	private static String db_name = "member_db.db";
	private static int version = 1;

	public Member_DBHelper(Context context) {
		super(context, db_name, null, version);
		// TODO Auto-generated constructor stub
	}

	public Member_DBHelper() {
		super(context, db_name, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String membersql = "create table members(memberID integer primary key autoincrement not null,username varchar,passwd varchar,regtime varchar)";
		db.execSQL(membersql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onCreate(db);
	}

}
