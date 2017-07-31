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
public class SD173Handler extends DefaultHandler {
	private String keyword;
	private String current;
	private String orderType;
	private String messagePurpose;

	private boolean right;
	
	private SD173Handler(String keyword) {
		super();
		this.keyword = keyword;
		this.right = false;
	}
	
	public static SD173Handler create(String keyword) {
		return new SD173Handler(keyword);
	}

	public String getOrderType() {
		return orderType;
	}

	public String getMessagePurpose() {
		return messagePurpose;
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
		if(Constants.TAG_SD173_ORDER_NUMBER.equals(current)) {
			String value = new String(ch, start, length);
			if(keyword.equalsIgnoreCase(value)) {
				right = true;
			}
		} else if(Constants.TAG_SD173_ORDER_TYPE.equals(current)) {
			orderType = new String(ch, start, length);
		} else if(Constants.TAG_SD173_MESSAGE_PURPOSE.equals(current)) {
			messagePurpose = new String(ch, start, length);
		}
		current = null;
	}
	
}
