package net.leanix.metrics.excelimport;

import java.util.Date;
import java.util.HashMap;
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
	
	private Map<String,String>listOfTags = new HashMap<>();

	public Map<String, String> getListOfTags() {
		return listOfTags;
	}

	public void setListOfTags(Map<String, String> listOfTags) {
		this.listOfTags = listOfTags;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append("host=").append(host).append(',');
		sb.append("token=").append(token).append(',');
		sb.append("workspaceID=").append(workspaceID).append(',');
		sb.append("name=").append(name).append(',');
		sb.append("fields=").append(listOfFields.size()).append(',');
		sb.append("tags=").append(listOfTags.size());
		return sb.append(']').toString();
	}
}