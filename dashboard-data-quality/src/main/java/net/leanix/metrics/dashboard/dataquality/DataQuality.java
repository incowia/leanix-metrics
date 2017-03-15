package net.leanix.metrics.dashboard.dataquality;

/**
 * Dataset for save as measurement
 */
public class DataQuality {

	private String businessCapabilityID = null;
	private String displayName = null;
	private int totalApps;
	private int incompleteApps;
	private int completeApps;
	private int incompleteAppsPercent = 0;;
	private int completeAppsPercent = 0;

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

	public int getTotalApps() {
		return totalApps;
	}

	public void setTotalApps(int totalApps) {
		this.totalApps = totalApps;
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

	public int getCompleteAppsPercent() {
		return completeAppsPercent;
	}

	public void setCompleteAppsPercent(int completeAppsPercent) {
		this.completeAppsPercent = completeAppsPercent;
	}

	public int getIncompleteAppsPercent() {
		return incompleteAppsPercent;
	}

	public void setIncompleteAppsPercent(int incompleteAppsPercent) {
		this.incompleteAppsPercent = incompleteAppsPercent;
	}

	@Override
	public String toString() {
		return "[businessCapabilityID=" + businessCapabilityID + ",displayName=" + displayName + ",incompleteApps="
				+ incompleteApps + ",completeApps=" + completeApps + ",incompleteApps%=" + incompleteAppsPercent + ",completeApps%=" + completeAppsPercent +"]";
	}
}
