package net.leanix.metrics.dashboard.dataquality;

/**
 * Dataset for save as measurement
 */
public class DataQuality {

	private String businessCapabilityID = null;
	private String displayName = null;
	private int incompleteApps;
	private int completeApps;

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

	public int getIncompleteApps() {
		return incompleteApps;
	}

	public void setIncompleteApps(int incompleteApps) {
		this.incompleteApps = incompleteApps;
	}
	
	public void incrementIncompleteApps() {
		incompleteApps++;
	}

	public int getCompleteApps() {
		return completeApps;
	}

	public void setCompleteApps(int completeApps) {
		this.completeApps = completeApps;
	}
	
	public void incrementCompleteApps() {
		completeApps++;
	}

	@Override
	public String toString() {
		return "[businessCapabilityID=" + businessCapabilityID + ",displayName=" + displayName + ",incompleteApps="
				+ incompleteApps + ",completeApps=" + completeApps + "]";
	}
}
