package util;
/**
 * 内存使用情况
 * @author pc
 *
 */
public class Memory {
	public static long used() {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		return (total - free);
	}
}