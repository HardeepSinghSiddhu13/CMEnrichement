package com.samsung.platform.domain.kafka;

import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
	
	private String emsID;
	private String version;	
	private String domain;
	private String subDomain;
	private Map<String, String> otherParams = new HashMap<String, String>();
	
	public String getEmsID() {
		return emsID;
	}
	public void setEmsID(String emsID) {
		this.emsID = emsID;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public Map<String, String> getOtherParams() {
		return otherParams;
	}
	public void setOtherParams(Map<String, String> otherParams) {
		this.otherParams = otherParams;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getSubDomain() {
		return subDomain;
	}
	public void setSubDomain(String subDomain) {
		this.subDomain = subDomain;
	}
	@Override
	public String toString() {
		return "RequestHeader [emsID=" + emsID + ", version=" + version + ", domain=" + domain + ", subDomain="
				+ subDomain + ", otherParams=" + otherParams + "]";
	}
	
	
	
}
