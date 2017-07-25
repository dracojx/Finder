package com.jx.finder.def;




/** 
 * @author Draco
 * @version 1.0
 * @date 2017年7月16日
 *  
 */
public class Message implements Comparable<Message> {
	private String guid;
	private String interfaceId;
	private String code;
	private String status;
	private String time;
	private String keyword;
	private String sortKey;
	
	
	private Message(String guid, String interfaceId, String code, String status, String time) throws Exception {
		this.guid = guid.toUpperCase();
		this.interfaceId = interfaceId;
		this.code = code;
		this.status = status;
		this.time = ConvertUtil.parseTime(time);
		this.sortKey = time;
	}

	private Message(String guid, String interfaceId, String code, String status, String time, String keyword) throws Exception {
		this(guid, interfaceId, code, status, time);
		this.keyword = keyword;
	}
	
	public static Message create(String guid, String interfaceId, String code, String status, String time) throws Exception {
		return new Message(guid, interfaceId, code, status, time);
	}
	
	public static Message create(String guid, String interfaceId, String code, String status, String time, String keyword) throws Exception {
		return new Message(guid, interfaceId, code, status, time, keyword);
	}
	
	public int compareTo(Message o) {
		return this.sortKey.compareTo(o.sortKey);
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String toString() {
		return time + " : " + code + " " + interfaceId;
	}
}
