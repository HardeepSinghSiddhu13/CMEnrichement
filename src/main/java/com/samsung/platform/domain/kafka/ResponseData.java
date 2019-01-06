package com.samsung.platform.domain.kafka;

/**
 * @author inirawat
 *
 */
public class ResponseData {
	private String response;
	private String data;
	private String message;
	//batchSize

	public ResponseData() {
		
	}

	public ResponseData(String response, String data,  String message) {
		super();
		this.response = response;
		this.data = data;
		this.message = message;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResponse() {
		return this.response;
	}

	public void setResponse(final String oResponse) {
		this.response = oResponse;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}


}
