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
	
	/*
	public static void main(String[] args) {
		Set<Message> set = new TreeSet<Message>();
		Message m1 = Message.create("", "", "", "", "20170716144257");
		Message m2 = Message.create("", "", "", "", "20200714144257");
		Message m3 = Message.create("", "", "", "", "20170718144257");
		Message m4 = Message.create("", "", "", "", "20170707144257");
		Message m5 = Message.create("", "", "", "", "20140716144257");
		Message m6 = Message.create("", "", "", "", "20160716144257");
		
		set.add(m1);
		set.add(m2);
		set.add(m3);
		set.add(m4);
		set.add(m5);
		set.add(m6);
		
		Iterator<Message> it = set.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().time);
		}
		
		Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(set));
	}
	*/
	
	private Message(String guid, String interfaceId, String code, String status, String time) {
		this.guid = guid.toUpperCase();
		this.interfaceId = interfaceId;
		this.code = code;
		this.status = status;
		this.time = time;
	}
	
	public static Message create(String guid, String interfaceId, String code, String status, String time) {
		return new Message(guid, interfaceId, code, status, time);
	}
	
	public int compareTo(Message o) {
		return this.time.compareTo(o.time);
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
}
