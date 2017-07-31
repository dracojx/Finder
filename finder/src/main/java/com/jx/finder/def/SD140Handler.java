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
public class SD140Handler extends DefaultHandler {
	private String key;
	private String val;
	private String current;
	private String status;
	private String text;
	
	private boolean checked;
	private boolean right;
	
	private SD140Handler(String key, String val) {
		super();
		this.key = key;
		this.val = val;
		this.right = false;
		this.checked = false;
	}
	
	public static SD140Handler create(String key, String val) {
		return new SD140Handler(key, val);
	}
	
	public String getStatus() {
		return status;
	}

	public String getText() {
		return text;
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
		if(key.equals(current)) {
			if(!checked) {
				String value = new String(ch, start, length);
				if(val.equalsIgnoreCase(value)) {
					right = true;
				}
				checked = true;
			}
		} else if(Constants.TAG_SD140_STATUS_CODE.equals(current)) {
			status = new String(ch, start, length);
		} else if(Constants.TAG_SD140_STATUS_TEXT.equals(current)) {
			text = new String(ch, start, length);
		}
		current = null;
	}
	
}
