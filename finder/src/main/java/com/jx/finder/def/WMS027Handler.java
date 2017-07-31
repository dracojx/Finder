package com.jx.finder.def;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** 
 * @author Draco
 * @version 1.0
 * @date 2017年7月25日
 *  
 */
public class WMS027Handler extends DefaultHandler {
	private String keyword;
	private String current;
	private boolean right;
	
	private WMS027Handler(String keyword) {
		super();
		this.keyword = keyword;
		this.right = false;
	}
	
	public static WMS027Handler create(String keyword) {
		return new WMS027Handler(keyword);
	}
	
	public boolean isRight() {
		return right;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		current = qName;
	}
	
	@Override
	public void characters (char[] ch, int start, int length) throws SAXException {
		if(Constants.TAG_WMS027_DELIVERY.equals(current)) {
			String value = new String(ch, start, length);
			if(keyword.equalsIgnoreCase(value)) {
				right = true;
			}
		}
		current = null;
	}
	
}
