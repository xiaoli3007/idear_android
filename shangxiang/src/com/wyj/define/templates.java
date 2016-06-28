package com.wyj.define;

public class templates {
	private String province;
	private String templename;
	private int templeid;

	public templates(String province, String templename, int templeid) {
		super();
		this.province = province;
		this.templename = templename;
		this.templeid = templeid;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getTemplename() {
		return templename;
	}

	public void setTemplename(String templename) {
		this.templename = templename;
	}

	public int getTempleid() {
		return templeid;
	}

	public void setTempleid(int templeid) {
		this.templeid = templeid;
	}

}
