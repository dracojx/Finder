package com.jx.finder.def;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/** 
 * @author Draco
 * @version 1.0
 * @date 2017年7月16日
 *  
 */
public class DataUtil {
	private static final Map<String, String> STATUS = status();
	
	public static String parseTime(String time) throws Exception {
		DateFormat dfGMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		DateFormat dfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		dfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = dfGMT.parse(time);
		
		return dfLocal.format(date);
	}
	
	public static String parseStatus(String status) {
		String trans = STATUS.get(status);
		if(trans == null) {
			return status;
		}
		return trans;
	}
	
	private static Map<String, String> status() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("DLNG", "处理中");
		map.put("DLVD", "完成");
		map.put("WAIT", "等待");
		map.put("FAIL", "失败");
		
		return map;
	}
}
