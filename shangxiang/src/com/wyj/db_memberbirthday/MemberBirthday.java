package com.wyj.db_memberbirthday;

public class MemberBirthday {

	private String mid;// ID  //用户ID
	private String title;  //生日标题 或者姓名
	private String birthday_time;  // 生日时间
	private String type;   // 1 阴历  2 阳历
	private String rtime; //提醒时间
	
	public  MemberBirthday(String mid,String title, String birthday_time , String type ,String rtime){
		this.mid = mid;
		this.title = title;
		this.birthday_time =birthday_time;
		this.type = type;
		this.rtime = rtime;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBirthday_time() {
		return birthday_time;
	}
	public void setBirthday_time(String birthday_time) {
		this.birthday_time = birthday_time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRtime() {
		return rtime;
	}
	public void setRtime(String rtime) {
		this.rtime = rtime;
	}

	
}
