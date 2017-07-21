package com.jx.finder.def;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Draco
 * @version 1.0
 * @date 2017年7月20日
 * 
 */
public class ConvertUtil {
	private static final SAXReader READER = new SAXReader();

	public static String blobToString(Blob blob) throws Exception {
		return new String(blob.getBytes(1, (int) blob.length()), "UTF-8");
	}

	public static Element blobToElement(Blob blob, String begin, String end)
			throws Exception {
		String msg = new String(blob.getBytes(1, (int) blob.length()), "UTF-8");
		int beginIndex = msg.indexOf(begin);
		int endIndex = msg.lastIndexOf(end) + end.length();

		if (beginIndex >= 0) {
			String xml = msg.substring(beginIndex, endIndex);
			Document document = READER.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			return document.getRootElement();
		}
		return null;
	}

	public static Element stringToElement(String xml) {
		xml = xml.replace("<![CDATA[", "");
		xml = xml.substring(0, xml.length() - 3);
		Document document;
		try {
			document = READER.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
		} catch (Exception e) {
			return null;
		}

		return document.getRootElement();
	}

	public static String parseTime(String time) throws Exception {
		DateFormat dfGMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		DateFormat dfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		dfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = dfGMT.parse(time);

		return dfLocal.format(date);
	}
}
