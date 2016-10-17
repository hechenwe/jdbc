package com.sooncode.jdbc.sql.condition.sign;

/**
 * 时间格式
 * 
 * @author pc
 *
 */
public class DateFormatSign extends Sign {
	private String dateFormatString;
	private String date ;
	
	
	/**
	 * 年月日, 时间格式：yyyy-MM-dd ;如 2016-09-01.
	 */
	public static final DateFormatSign yyyy_MM_dd = new DateFormatSign("%Y-%m-%d");
	/**
	 * 年月, 时间格式：yyyy-MM ;如 2016-09.
	 */
	public static final DateFormatSign yyyy_MM = new DateFormatSign("%Y-%m");
	/**
	 * 月日, 时间格式：yyyy-MM , 如 01-01 ; 12-30 .
	 */
	public static final DateFormatSign MM_dd = new DateFormatSign("%m-%d");
	/**
	 * 年, 时间格式：yyyy ;如 2016.
	 */
	public static final DateFormatSign yyyy = new DateFormatSign("%Y");
	/**
	 * 月, 时间格式：MM ;如 01 12.
	 */
	public static final DateFormatSign MM = new DateFormatSign("%m");

	/**
	 * 日, 时间格式：dd , 如 01 ; 29; 30.
	 */
	public static final DateFormatSign dd = new DateFormatSign("%d");

	/**
	 * 自定义日期格式：</br> 
	 * %M 月名字(January……December) </br>
	 * %W 星期名字(Sunday……Saturday)</br> 
	 * %D 有英语前缀的月份的日期(1st, 2nd, 3rd, 等等。） </br>
	 * %Y 年, 数字, 4 位 </br>
	 * %y 年, 数字, 2 位 </br>
	 * %a 缩写的星期名字(Sun……Sat) </br>
	 * %d 月份中的天数, 数字(00……31) </br> 
	 * %e 月份中的天数, 数字(0……31) </br>
	 * %m 月,数字(01……12)</br>
	 * %c 月, 数字(1……12)</br>
	 * %b 缩写的月份名字(Jan……Dec)</br> 
	 * %j 一年中的天数(001……366)</br> 
	 * %H 小时(00……23)</br>
	 * %k 小时(0……23) </br>
	 * %h 小时(01……12) </br>
	 * %I 小时(01……12) </br>
	 * %l 小时(1……12) </br>
	 * %i 分钟,数字(00……59)</br> 
	 * %r 时间,12 小时(hh:mm:ss [AP]M) </br>
	 * %T 时间,24 小时(hh:mm:ss) </br>
	 * %S 秒(00……59)</br>
	 * %s 秒(00……59)</br> 
	 * %p AM或PM </br>
	 * %w 一个星期中的天数(0=Sunday ……6=Saturday ）</br>
	 * %U 星期(0……52),注意：“星期天” 是一个星期的第一天 </br>
	 * %u 星期(0……52),注意：“星期一” 是一个星期的第一天</br>
	 * @param signStr 日期格式字符串，如："%Y-%m-%d" ,其格式化为 "2016-01-01"
	 */
	public DateFormatSign(String signStr) {
		super(signStr);
	}
	
	public static final DateFormatSign YYYY_MM_DD(String date){
		//this.dateFormatString = "%Y-%m-%d";
		return null;
	}

}
