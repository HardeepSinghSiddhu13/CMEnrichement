package com.samsung.platform.domain.kafka;

import java.util.ArrayList;
import java.util.List;

/**
 * @author inirawat
 *
 * Class to deal with the request JSON Object 
 */
public class RequestJSON {
	
	/* authToken , 
	 * content-type  
	 * topicName 
	 * from original Request Header*/
	
	private RequestHeader requestheader;
	List<Event> data = new ArrayList<Event>();
	
	public RequestHeader getRequestheader() {
		return requestheader;
	}
	public void setRequestheader(RequestHeader requestheader) {
		this.requestheader = requestheader;
	}
	
	public List<Event> getData() {
		return data;
	}
	public void setData(List<Event> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "RequestJSON [requestheader=" + requestheader + ", data=" + data + "]";
	}
}
