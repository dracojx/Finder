package com.jx.finder.def;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
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

	public static String blobToString(Blob blob) throws Exception {
		return new String(blob.getBytes(1, (int) blob.length()), "UTF-8");
	}
	
	public static InputStream blobToInputStream(Blob blob) throws Exception {
		String content = new String(blob.getBytes(1, (int) blob.length()), "UTF-8");
		int beginIndex = content.indexOf("<?xml");
		int endIndex = content.lastIndexOf(">") + 1;
		return new ByteArrayInputStream(content.substring(beginIndex, endIndex).getBytes(Charset.forName("UTF-8")));
	}
	
	public static InputStream blobToInputStream(Blob blob, String start, String end) throws Exception {
		String content = new String(blob.getBytes(1, (int) blob.length()), "UTF-8");
		int beginIndex = content.indexOf(start);
		int endIndex = content.lastIndexOf(end) + end.length();
		content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + content.substring(beginIndex, endIndex);
		return new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8")));
	}
	
	public static InputStream blobToInputStreamSub(Blob blob) throws Exception {
		String content = new String(blob.getBytes(1, (int) blob.length()), "UTF-8").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
		
		int beginIndex = content.lastIndexOf("<![CDATA[") + 9;
		int endIndex = content.lastIndexOf("]]>");
		return new ByteArrayInputStream(content.substring(beginIndex, endIndex).getBytes(Charset.forName("UTF-8")));
	}

	public static Element blobToElement(SAXReader reader, Blob blob, String begin, String end)
			throws Exception {
		String msg = new String(blob.getBytes(1, (int) blob.length()), "UTF-8");
		int beginIndex = msg.indexOf(begin);
		int endIndex = msg.lastIndexOf(end) + end.length();

		if (beginIndex >= 0) {	
			String xml = msg.substring(beginIndex, endIndex);
			Document document = reader.read(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
			return document.getRootElement();
		}
		return null;
	}

	public static Element stringToElement(SAXReader reader, String xml) {
		xml = xml.replace("<![CDATA[", "").replace("]]>", "");
		Document document;
		try {
			document = reader.read(new ByteArrayInputStream(xml
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
