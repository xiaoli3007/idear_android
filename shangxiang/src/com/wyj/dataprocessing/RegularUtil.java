package com.wyj.dataprocessing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

public class RegularUtil {

	static Toast toast;

	public static boolean checkName(Activity context, String name) {
		if (TextUtils.isEmpty(name) || name.length() < 3 || name.length() > 16
				|| !nameFormat(name)) {
			toast = Toast.makeText(context, "昵称不符合规范，3-16个中英文字符、数字",
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		return true;
	}

	public static boolean checkTrueName(Activity context, String Truename) {
		if (TextUtils.isEmpty(Truename)) {
			toast = Toast.makeText(context, "真实姓名不能为空！", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		return true;
	}

	public static boolean checkAddress(Activity context, String Address) {
		if (TextUtils.isEmpty(Address)) {
			toast = Toast.makeText(context, "地址不能为空！", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		return true;
	}

	public static boolean checkPhone(Activity context, String phone) {
		if (!phoneFormat(phone)) {

			alert_msg(context, "手机号输入不正确");
			return false;
		}
		return true;
	}

	public static boolean checkEmail(Activity context, String email) {
		if (!emailFormat(email) || email.length() > 31) {
			Toast.makeText(context, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public static boolean checkPassword(Activity context, String password) {
		if (!passwordFormat(password)) {

			alert_msg(context, "密码格式是8-20位英文字符、数字");

			return false;
		}
		return true;
	}

	public static void alert_msg(Activity context, String msg) {

		toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static boolean phoneFormat(String phone) {
		Pattern pattern = Pattern.compile("^(1)[0-9]{10}$");
		Matcher mc = pattern.matcher(phone);
		return mc.matches();
	}

	private static boolean emailFormat(String email) {
		Pattern pattern = Pattern
				.compile("^[A-Za-z\\d]+(\\.[A-Za-z\\d]+)*@([\\dA-Za-z](-[\\dA-Za-z])?)+(\\.{1,2}[A-Za-z]+)+$");
		Matcher mc = pattern.matcher(email);
		return mc.matches();
	}

	/**
	 * 以字母开头，长度在6~16之间，只能包含字符、数字和下划线（w）
	 * 
	 * @param password
	 * @return
	 */
	private static boolean passwordFormat(String password) {
		Pattern pattern = Pattern
				.compile("^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{6,20}$");
		Matcher mc = pattern.matcher(password);
		return mc.matches();
	}

	public static boolean nameFormat(String name) {
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5A-Za-z0-9_]{3,16}$");
		Matcher mc = pattern.matcher(name);
		return mc.matches();
	}

	/**
	 * 获取含双字节字符的字符串字节长度
	 * 
	 * @param s
	 * @return
	 */
	public static int getStringLength(String s) {
		char[] chars = s.toCharArray();
		int count = 0;
		for (char c : chars) {
			count += getSpecialCharLength(c);
		}
		return count;
	}

	/**
	 * 获取字符长度：汉、日、韩文字符长度为2，ASCII码等字符长度为1
	 * 
	 * @param c
	 *            字符
	 * @return 字符长度
	 */
	private static int getSpecialCharLength(char c) {
		if (isLetter(c)) {
			return 1;
		} else {
			return 2;
		}
	}

	/**
	 * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
	 * 
	 * @param char c, 需要判断的字符
	 * @return boolean, 返回true,Ascill字符
	 */
	private static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}
}
