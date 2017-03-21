package net.leanix.metrics.excelimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Measurement {
	
	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	private String workspaceID;

	public String getWorkspaceID() {
		return workspaceID;
	}

	public void setWorkspaceID(String workspaceID) {
		this.workspaceID = workspaceID;
	}
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	private Map<String,String>listOfFields = new HashMap<>();

	public Map<String, String> getListOfFields() {
		return listOfFields;
	}

	public void setListOfFields(Map<String, String> listOfFields) {
		this.listOfFields = listOfFields;
	}
}