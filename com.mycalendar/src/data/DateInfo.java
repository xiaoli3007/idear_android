package data;

public class DateInfo {
	private int date;
	private boolean isThisMonth;
	private boolean isWeekend;
	private boolean isHoliday;
	private String NongliDate;
	private String NongliInfo;
	private String Nonglinumber;	//农历的数字
	private String Holiday;
	private int Year;
	private int Month;
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public String getNongliDate() {
		return NongliDate;
	}
	public void setNongliDate(String nongliDate) {
		NongliDate = nongliDate;
	}
	public String getNongliInfo() {
		return NongliInfo;
	}
	public void setNongliInfo(String NongliInfos) {
		NongliInfo = NongliInfos;
	}
	public String getNonglinumber() {
		return Nonglinumber;
	}
	public void setNonglinumber(String Nonglinumbers) {
		Nonglinumber = Nonglinumbers;
	}
	public String getHoliday() {
		return Holiday;
	}
	public void setHoliday(String Holidays) {
		Holiday = Holidays;
	}
	public int getYear() {
		return Year;
	}
	public void setYear(int Year) {
		this.Year = Year;
	}
	public int getMonth() {
		return Month;
	}
	public void setMonth(int Month) {
		this.Month = Month;
	}
	public boolean isThisMonth() {
		return isThisMonth;
	}
	public void setThisMonth(boolean isThisMonth) {
		this.isThisMonth = isThisMonth;
	}
	public boolean isHoliday() {
		return isHoliday;
	}
	public void setHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	public boolean isWeekend() {
		return isWeekend;
	}
	public void setWeekend(boolean isWeekend) {
		this.isWeekend = isWeekend;
	}
}
