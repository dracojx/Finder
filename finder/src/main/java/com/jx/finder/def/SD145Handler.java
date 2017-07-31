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
public class SD145Handler extends DefaultHandler {
	private String keyword;
	private String current;

	private boolean right;
	
	private SD145Handler(String keyword) {
		super();
		this.keyword = keyword;
		this.right = false;
	}
	
	public static SD145Handler create(String keyword) {
		return new SD145Handler(keyword);
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
		if(Constants.TAG_SD145_ORDER_NUMBER.equals(current)) {
			String value = new String(ch, start, length);
			if(keyword.equalsIgnoreCase(value)) {
				right = true;
			}
		}
		current = null;
	}
	
}
