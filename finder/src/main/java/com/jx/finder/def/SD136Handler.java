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
public class SD136Handler extends DefaultHandler {
	private String keyword;
	private String current;
	private String salesType;

	private boolean right;
	
	private SD136Handler(String keyword) {
		super();
		this.keyword = keyword;
		this.right = false;
	}
	
	public static SD136Handler create(String keyword) {
		return new SD136Handler(keyword);
	}

	public String getSalesType() {
		return salesType;
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
		if(Constants.TAG_SD136_ORDER_NUMBER.equals(current)) {
			String value = new String(ch, start, length);
			if(keyword.equalsIgnoreCase(value)) {
				right = true;
			}
		} else if(Constants.TAG_SD136_SALES_TYPE.equals(current)) {
			salesType = new String(ch, start, length);
		}
		current = null;
	}
	
}
