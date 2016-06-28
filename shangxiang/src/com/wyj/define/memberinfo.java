package com.wyj.define;

public class memberinfo {
	private  String username = "";	//相当于mobile
	private  int memberid;
	private  String headface = "";
	private  String tmb_headface = "";
	private  String truename = "";
	private  int sex;
	
	public memberinfo(String username, int memberid, String headface, String tmb_headface,int sex, String area) {
		super();
		this.username = username;
		this.memberid = memberid;
		this.headface = headface;
		this.tmb_headface = tmb_headface;
		this.sex = sex;
		this.area = area;
	}
	
	public memberinfo( String headface, int sex, String area, String truename) {
		super();
	
		this.headface = headface;
		this.sex = sex;
		this.area = area;
		this.truename = truename;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getMemberid() {
		return memberid;
	}

	public void setMemberid(int memberid) {
		this.memberid = memberid;
	}

	public String getHeadface() {
		return headface;
	}

	public void setHeadface(String headface) {
		this.headface = headface;
	}

	public String getTmb_headface() {
		return tmb_headface;
	}

	public void setTmb_headface(String tmb_headface) {
		this.tmb_headface = tmb_headface;
	}

	public String getTruename() {
		return truename;
	}

	public void setTruename(String truename) {
		this.truename = truename;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	private  String area = "";
	

	
	

}
