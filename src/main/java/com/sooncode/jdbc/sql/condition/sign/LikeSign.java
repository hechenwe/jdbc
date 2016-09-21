package com.sooncode.jdbc.sql.condition.sign;
/**
 * 模糊匹配
 * @author pc
 *
 */
public class LikeSign extends Sign {

	/**
	 * 模糊匹配    "%XXX%"
	 */
	public static final Sign LIKE = new Sign("LIKE");
	/**
	 * 右模糊匹配    "%XXX"
	 */
	public static final Sign R_LIKE = new Sign("R_LIKE");
	/**
	 * 左模糊匹配    "XXX%"
	 */
	public static final Sign L_LIKE = new Sign("L_LIKE");

}
