package net.leanix.metrics.dashboard.dataquality;

/**
 * Dataset for save as measurement
 */
public class DataQuality {
	
	private String businessCapabilityID = null;
	private String displayName = null;
	private String incompleteApps = null;
	private String complete = null;

	public String getBusinessCapabilityID() {
		return businessCapabilityID;
	}
	
	public void setBusinessCapabilityID(String businessCapabilityID) {
		this.businessCapabilityID = businessCapabilityID;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getIncompleteApps() {
		return incompleteApps;
	}
	
	public void setIncompleteApps(String incompleteApps) {
		this.incompleteApps = incompleteApps;
	}
	
	public String getComplete() {
		return complete;
	}
	
	public void setComplete(String complete) {
		this.complete = complete;
	}
}
